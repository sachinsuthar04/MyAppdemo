package com.sachin;

import android.Manifest;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sachin.databinding.ActivityAddRecordBinding;
import com.sachin.roomdb.RecordDatabase;
import com.sachin.roomdb.model.Recorduser;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by sachin suthar 23 june 2020.
 */
public class AddRecordActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final int CAMERA_REQUEST = 111;
    private static final int GALLERY_REQUEST = 222;
    private String profilePath = "";

    private RecordDatabase recordDatabase;
    private Recorduser recorduser;
    ActivityAddRecordBinding mBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_record);

        recordDatabase = RecordDatabase.getInstance(AddRecordActivity.this);

        setlistner();

    }

    private void setlistner() {
        mBinding.btncancel.setOnClickListener(v -> finish());
        mBinding.butSave.setOnClickListener(view -> {
            recorduser = new Recorduser(mBinding.etContent.getText().toString(), mBinding.etTitle.getText().toString(), profilePath);
            new InsertTask(AddRecordActivity.this, recorduser).execute();
        });
        mBinding.myAvatar.setOnClickListener(v -> openCameraAndGalleryDialog());
    }

    private void setResult(Recorduser record, int flag) {
        setResult(flag, new Intent().putExtra("record", record));
        finish();
    }

    @AfterPermissionGranted(GALLERY_REQUEST)
    private void checkGalleryPermission() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(AddRecordActivity.this, perms)) {
            galleryIntent();
        } else {
            EasyPermissions.requestPermissions
                    (this,"Please allow external storage permission.", GALLERY_REQUEST, perms);
        }
    }
    @AfterPermissionGranted(CAMERA_REQUEST)
    private void checkCameraPermission() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(AddRecordActivity.this, perms)) {
            cameraIntent();
        } else {
            EasyPermissions.requestPermissions(AddRecordActivity.this,
                    "Allow Camera permission", CAMERA_REQUEST, perms);
        }
    }

    private void galleryIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, GALLERY_REQUEST);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    private void cameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                return;
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(AddRecordActivity.this,
                        getPackageName() + ".provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "DailyPass_" + timeStamp + "_";
//        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
//     prefix

                ".jpg",
//     suffix

                storageDir
//     directory

        );
        // Save a file: path for use with ACTION_VIEW in
        profilePath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = Uri.parse(profilePath);
            profilePath = imageUri.getPath();
            MediaScannerConnection.scanFile(AddRecordActivity.this,
                    new String[]{profilePath}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {

                        }
                    });
            if (profilePath != null) {
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.customer)
                        .error(R.drawable.customer);
                Glide.with(AddRecordActivity.this).load(imageUri).
                        apply(options).into(mBinding.myAvatar);
            }
        }

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            profilePath = ImageFilePath.getPath(AddRecordActivity.this, uri);//working
            Log.e("=>", "File path  is " + profilePath);
            if (profilePath != null) {
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.customer)
                        .error(R.drawable.customer);
                Glide.with(AddRecordActivity.this).load(uri).
                        apply(options).into(mBinding.myAvatar);
            }
        }
    }

    private static class InsertTask extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<AddRecordActivity> activityReference;
        private Recorduser recorddata;

        // only retain a weak reference to the activity
        InsertTask(AddRecordActivity context, Recorduser recorddata) {
            activityReference = new WeakReference<>(context);
            this.recorddata = recorddata;
        }

        // doInBackground methods runs on a worker thread
        @Override
        protected Boolean doInBackground(Void... objs) {
            // retrieve auto incremented note id
            long j = activityReference.get().recordDatabase.getrecordDao().insertRecorduser(recorddata);
            recorddata.setRecord_id(j);
            Log.e("ID ", "doInBackground: " + j);
            return true;
        }

        // onPostExecute runs on main thread
        @Override
        protected void onPostExecute(Boolean bool) {
            if (bool) {
                activityReference.get().setResult(recorddata, 1);
                activityReference.get().finish();
            }
        }
    }

    private void openCameraAndGalleryDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddRecordActivity.this);
            builder.setTitle("");
            builder.setMessage("Choose Profile Pic");
            builder.setPositiveButton("Use Gallery", (dialog, id) -> {
                checkGalleryPermission();
                dialog.dismiss();
            });

            builder.setNegativeButton("Use Camera", (dialog, id) -> {
                checkCameraPermission();
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        } catch (Exception e) {
        }
    }
}
