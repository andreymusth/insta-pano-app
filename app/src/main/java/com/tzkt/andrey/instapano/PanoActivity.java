package com.tzkt.andrey.instapano;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.tzkt.andrey.instapano.limiter.LimiterView;
import com.tzkt.andrey.instapano.settings.SettingsActivity;
import com.tzkt.andrey.instapano.utils.NavigationUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class PanoActivity extends SingleFragmentActivity implements EnterPageFragment.Callbacks,
        EditorFragment.Callbacks {

    public static final int PICK_IMAGE = 1;
    private static final int REQUEST_STORAGE_PERMISSION = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 3;

    private String mCurrentPhotoPath;

    @Override
    public Fragment createFragment() {
        return EnterPageFragment.newInstance();
    }

    @Override
    public void onButtonClicked(int actionId) {

        switch (actionId) {
            case R.id.btn_choose_photo:
                openImageChooser();
                break;
            case R.id.btn_take_photo:
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // If you do not have permission, request it
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_STORAGE_PERMISSION);
                } else {
                    // Launch the camera if the permission exists
                    launchCamera();
                }
                break;
            case R.id.action_preview:

                NavigationUtils.openPreview(this);

                break;
        }

    }

    private void launchCamera() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Called when you request permission to read and write to external storage
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If you get permission, launch the camera
                    launchCamera();
                } else {
                    // If you do not get permission, show a Toast
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            Uri selectedImage = data.getData();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, EditorFragment.newInstance(selectedImage.toString()))
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK && null != data) {


//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, EditorFragment.newInstance(selectedImage.toString()))
//                    .addToBackStack(null)
//                    .commitAllowingStateLoss();
        }


    }

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void openImageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        if (R.id.action_settings == itemId) {
            NavigationUtils.openSettings(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
