package com.jkapps.sqlitelistview;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;

/** 2020-01-07
 * ListView from SQLite Database (SQLite CRUD)
 * Crop and upload images from Device Gallery
 * Search Bar
*/

public class MainActivity extends AppCompatActivity {

    EditText et_title, et_content;
    ImageView iv_photo;
    Button btnSave, btnList;

    final int REQUEST_CODE_GALLERY = 999;

    public static SQLiteHelper mSQLiteHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_title = findViewById(R.id.et_title);
        et_content = findViewById(R.id.et_content);
        iv_photo = findViewById(R.id.iv_photo);
        btnSave = findViewById(R.id.btnSave);
        btnList = findViewById(R.id.btnList);

        iv_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY);
            }
        });

        mSQLiteHelper = new SQLiteHelper(this, "DB_NAME.sqlite", null, 1);
        mSQLiteHelper.queryData("CREATE TABLE IF NOT EXISTS TABLE_NAME " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, title VARCHAR, content VARCHAR, image BLOB)");

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mSQLiteHelper.insertData(
                            et_title.getText().toString().trim(),
                            et_content.getText().toString().trim(),
                            imageViewToByte(iv_photo)
                    );
                    toastMessage("Added successfully.");
                    et_title.setText("");
                    et_content.setText("");
                    iv_photo.setImageResource(R.drawable.insert_photo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toastMessage("Going back to the list ...");
                startActivity(new Intent(MainActivity.this, ListActivity.class));
            }
        });
    }

    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery();
            } else {
                toastMessage("You don't have permission to access file location");
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void pickImageFromGallery() {
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery, "Select Picture ..."), REQUEST_CODE_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                iv_photo.setImageURI(resultUri);
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                toastMessage("Error: " + error);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
