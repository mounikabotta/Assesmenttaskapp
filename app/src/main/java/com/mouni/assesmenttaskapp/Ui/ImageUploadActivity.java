package com.mouni.assesmenttaskapp.Ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.mouni.assesmenttaskapp.Data.User;
import com.mouni.assesmenttaskapp.Data.UserDao;
import com.mouni.assesmenttaskapp.Data.UserDatabase;
import com.mouni.assesmenttaskapp.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageUploadActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    private static final int TAKE_PHOTO = 101;

    private ImageView imageView;
    private Uri imageUri;
    private int userId;

    private UserDao userDao;
    private File photoFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);

        imageView = findViewById(R.id.image_view_upload);
        Button uploadButton = findViewById(R.id.button_upload);

        userId = getIntent().getIntExtra("user_id", -1);
        UserDatabase db = UserDatabase.getInstance(this);
        userDao = db.userDao();

        imageView.setOnClickListener(v -> openImagePicker());

        uploadButton.setOnClickListener(v -> showImageOptions());
    }

    private void showImageOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source")
                .setItems(new CharSequence[]{"Camera", "Gallery"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            takePhoto();
                            break;
                        case 1:
                            openImagePicker();
                            break;
                    }
                });
        builder.create().show();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                return;
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.mouni.assesmenttaskapp.fileprovider",
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, TAKE_PHOTO);
            }
        }
    }


    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE && data != null) {
                imageUri = data.getData();
                imageView.setImageURI(imageUri);
                saveImageLocally();
            } else if (requestCode == TAKE_PHOTO ) {
                if (photoFile != null) {
                    imageUri = Uri.fromFile(photoFile);
                    imageView.setImageURI(imageUri);
                    saveImageLocally();
                }
            }
        }
    }

    private void saveImageLocally() {
        if (imageUri == null) {
            return;
        }

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "user_image_" + userId + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();

            new Thread(() -> {
                User user = userDao.getUserByIdSync(userId);
                if (user != null) {
                    user.setLocalImagePath(file.getAbsolutePath());
                    userDao.updateLocalImagePath(userId, file.getAbsolutePath());
                }
            }).start();

            Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
        }
    }
}

