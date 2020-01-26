package com.jkapps.sqlitelistview;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static com.jkapps.sqlitelistview.MainActivity.mSQLiteHelper;

public class ListActivity extends AppCompatActivity {

    private static final String TAG = "ListDataActivity";

    SearchView searchView;
    ListView mListView;
    ArrayList<Model> mList;
    ListAdapter mAdapter = null;

    ImageView iv_photo;

    final int REQUEST_CODE_GALLERY = 888;

    Button btnAdd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        searchView = findViewById(R.id.searchView);
        mListView = findViewById(R.id.listView);
        mList = new ArrayList<>();
        mAdapter = new ListAdapter(this, R.layout.row_layout, mList);
        mListView.setAdapter(mAdapter);

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });

        Cursor cursor = mSQLiteHelper.getData("SELECT * FROM TABLE_NAME");
        mList.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String content = cursor.getString(2);
            byte[] image = cursor.getBlob(3);

            mList.add(new Model(id, title, content, image));
        }
        mAdapter.notifyDataSetChanged();
        if (mList.size() == 0) {
            toastMessage("No data found.");
        }

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final CharSequence[] items = {"Read", "Update", "Delete"};

                AlertDialog.Builder dialog = new AlertDialog.Builder(ListActivity.this);
                dialog.setTitle("Choose an action.");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0) {
                            Cursor c = mSQLiteHelper.getData("SELECT id FROM TABLE_NAME");
                            ArrayList<Integer> arrID = new ArrayList<>();
                            while (c.moveToNext()) {
                                arrID.add(c.getInt(0));
                            }
                            showDialogRead(ListActivity.this, arrID.get(position));
                        }

                        if (which == 1) {
                            Cursor c = mSQLiteHelper.getData("SELECT id FROM TABLE_NAME");
                            ArrayList<Integer> arrID = new ArrayList<>();
                            while (c.moveToNext()) {
                                arrID.add(c.getInt(0));
                            }
                            showDialogUpdate(ListActivity.this, arrID.get(position));
                        }

                        if (which == 2) {
                            Cursor c = mSQLiteHelper.getData("SELECT id FROM TABLE_NAME");
                            ArrayList<Integer> arrID = new ArrayList<>();
                            while (c.moveToNext()) {
                                arrID.add(c.getInt(0));
                            }
                            showDialogDelete(arrID.get(position));
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });

        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createScreenIntent = new Intent(ListActivity.this, MainActivity.class);
                startActivity(createScreenIntent);
            }
        });
    }


    private void showDialogRead(Activity activity, final int position) {
        final Dialog dialogRead = new Dialog(activity);
        dialogRead.setContentView(R.layout.read_dialog);
        dialogRead.setTitle("Read ...");

        final TextView tv_title = dialogRead.findViewById(R.id.tv_title);
        final TextView tv_content = dialogRead.findViewById(R.id.tv_content);
        iv_photo = dialogRead.findViewById(R.id.iv_photo);
        Button btnClose = dialogRead.findViewById(R.id.btnClose);

        Cursor cursor = mSQLiteHelper.getData(
                "SELECT * FROM TABLE_NAME WHERE id = " + position);
        mList.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            tv_title.setText(title);
            String content = cursor.getString(2);
            tv_content.setText(content);
            byte[] image = cursor.getBlob(3);
            iv_photo.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
        }

        int width = (int)(activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        int height = (int)(activity.getResources().getDisplayMetrics().heightPixels * 0.7);
        dialogRead.getWindow().setLayout(width, height);
        dialogRead.show();

        updateListData();

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogRead.dismiss();
            }
        });
    }

    private void showDialogUpdate(Activity activity, final int position) {
        final Dialog dialogUpdate = new Dialog(activity);
        dialogUpdate.setContentView(R.layout.update_dialog);
        dialogUpdate.setTitle("Update ...");

        final EditText et_title = dialogUpdate.findViewById(R.id.et_title);
        final EditText et_content = dialogUpdate.findViewById(R.id.et_content);
        iv_photo = dialogUpdate.findViewById(R.id.iv_photo);
        Button btnUpdate = dialogUpdate.findViewById(R.id.btnUpdate);

        Cursor cursor = mSQLiteHelper.getData(
                "SELECT * FROM TABLE_NAME WHERE id = " + position);
        mList.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            et_title.setText(title);
            String content = cursor.getString(2);
            et_content.setText(content);
            byte[] image = cursor.getBlob(3);
            iv_photo.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));

            mList.add(new Model(id, title, content, image));
        }

        int width = (int)(activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        int height = (int)(activity.getResources().getDisplayMetrics().heightPixels * 0.7);
        dialogUpdate.getWindow().setLayout(width, height);
        dialogUpdate.show();

        iv_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(ListActivity.this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mSQLiteHelper.updateData(
                            et_title.getText().toString().trim(),
                            et_content.getText().toString().trim(),
                            MainActivity.imageViewToByte(iv_photo),
                            position
                    );
                    toastMessage("Updated successfully.");
                    dialogUpdate.dismiss();
                }
                catch (Exception error) {
                    Log.e("Update error: ", error.getMessage());
                }
                updateListData();
            }
        });
    }

    private void showDialogDelete(final int position) {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(ListActivity.this);
        dialogDelete.setTitle("Delete ...");
        dialogDelete.setMessage("Are you sure to delete it?");
        dialogDelete.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    mSQLiteHelper.deleteData(position);
                    toastMessage("Deleted Successfully.");
                }
                catch (Exception e) {
                    Log.e("error: ", e.getMessage());
                }
                updateListData();
            }
        });
        dialogDelete.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogDelete.show();
    }

    private void updateListData() {
        Cursor cursor = mSQLiteHelper.getData("SELECT * FROM TABLE_NAME");
        mList.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String content = cursor.getString(2);
            byte[] image = cursor.getBlob(3);

            mList.add(new Model(id, title, content, image));
        }
        mAdapter.notifyDataSetChanged();
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
        startActivityForResult(Intent.createChooser(gallery, "Select Picture"), REQUEST_CODE_GALLERY);
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
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
