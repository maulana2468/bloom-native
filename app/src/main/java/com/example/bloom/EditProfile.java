package com.example.bloom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.bloom.databinding.ActivityEditProfileBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.MimeType;
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {

    private EditText nameTemp;
    private ActivityEditProfileBinding binding;
    private Uri destinationUri;
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            showDialogChangeProfilePicture(resultUri);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }

        if (resultCode == RESULT_OK && requestCode == 7) {
            Log.d("EditProfile", String.valueOf(data.getParcelableArrayListExtra(FishBun.INTENT_PATH)));
            ArrayList<Uri> dataUri = data.getParcelableArrayListExtra(FishBun.INTENT_PATH);
            Log.d("EditProfile", dataUri.get(0).toString());
            editPP(dataUri.get(0));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Button back = findViewById(R.id.back_edit_profile);
        EditText name = findViewById(R.id.edit_nama);

        getSupportActionBar().hide();

        ImageView photo = findViewById(R.id.photo);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (!Uri.EMPTY.equals(auth.getCurrentUser().getPhotoUrl())) {
            Glide.with(this).load(auth.getCurrentUser().getPhotoUrl()).diskCacheStrategy(DiskCacheStrategy.ALL).into(photo);
        }

        binding.changeProfile.setOnClickListener(v -> {

            FishBun.with(this)
                    .setImageAdapter(new GlideAdapter())
                    .exceptGif(true)
                    .setMaxCount(1)
                    .setMinCount(1)
                    .startAlbumWithOnActivityResult(7);
        });

        binding.saveName.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (!binding.editNama.getText().toString().isEmpty()) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(binding.editNama.getText().toString().trim())
                        .build();

                update(user, profileUpdates);
            } else {
                Snackbar.make(binding.getRoot(), "Nama tidak boleh kosong", Snackbar.LENGTH_LONG);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void editPP(Uri sourceUri) {
        Uri tempPict = Uri.fromFile(new File(getCacheDir(), String.valueOf(new Random().nextDouble())));

        Log.d("EditProfile","CALLED");
        UCrop.Options options = new UCrop.Options();
        options.setCircleDimmedLayer(true);

        UCrop.of(sourceUri, tempPict)
                .withOptions(options)
                .withAspectRatio(1, 1)
                .start(EditProfile.this);
    }

    private void showDialogChangeProfilePicture(Uri tempUri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_changed_pict, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.getWindow().getDecorView().setBackgroundResource(R.drawable.bg_popup);

        Button cancel = dialogView.findViewById(R.id.cancel_pp);
        Button save = dialogView.findViewById(R.id.save_pp);
        ImageView image  = dialogView.findViewById(R.id.pick_temp);

        Glide.with(this).load(tempUri).circleCrop().into(image);

        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        save.setOnClickListener(v -> {
            dialog.dismiss();
            binding.photo.setVisibility(View.GONE);
            binding.loadingLoadPp.setVisibility(View.VISIBLE);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseStorage storage = FirebaseStorage.getInstance();

            StorageReference storageRef = storage.getReference();
            StorageReference profilePictRef = storageRef.child("profilePicture/" + user.getUid() + ".png");
            UploadTask uploadTask = profilePictRef.putFile(tempUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        Snackbar.make(binding.getRoot(), task.getException().toString(), Snackbar.LENGTH_SHORT).show();
                    }

                    // Continue with the task to get the download URL
                    return profilePictRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(Uri.parse(String.valueOf(downloadUri)))
                                .build();

                        update(user, profileUpdates);
                    } else {
                        Snackbar.make(binding.getRoot(), task.getException().toString(), Snackbar.LENGTH_SHORT).show();
                    }
                }
            });




        });

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
    }

    private void update(FirebaseUser user, UserProfileChangeRequest profileUpdates) {
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Ganti data berhasil", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(EditProfile.this, Dashboard.class));
                        } else {
                            Snackbar.make(binding.getRoot(), task.getException().toString(), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}