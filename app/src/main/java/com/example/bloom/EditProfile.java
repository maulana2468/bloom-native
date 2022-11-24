package com.example.bloom;

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
import com.example.bloom.databinding.ActivityEditProfileBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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

        getSupportActionBar().hide();

        binding.changeProfile.setOnClickListener(v -> {

            FishBun.with(this)
                    .setImageAdapter(new GlideAdapter())
                    .exceptGif(true)
                    .setMaxCount(1)
                    .setMinCount(1)
                    .startAlbumWithOnActivityResult(7);
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
            FirebaseAuth user = FirebaseAuth.getInstance();



            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                    .build();

            finish();
        });

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
    }
}