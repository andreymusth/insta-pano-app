package com.tzkt.andrey.instapano;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.tzkt.andrey.instapano.utils.BitmapUtils;
import com.tzkt.andrey.instapano.utils.NavigationUtils;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class PanoActivity extends SingleFragmentActivity implements EnterPageFragment.Callbacks,
        EditorFragment.Callbacks {

    public static final int PICK_IMAGE = 1;
    private static final int REQUEST_STORAGE_PERMISSION_CAMERA = 21;
    private static final int REQUEST_STORAGE_PERMISSION_CHOOSER = 22;
    private static final int REQUEST_IMAGE_CAPTURE = 3;

    private static final String FILE_PROVIDER_AUTHORITY = "com.tzkt.andrey.fileprovider";

    private String mTempPhotoPath;

    @Override
    public Fragment createFragment() {
        return EnterPageFragment.newInstance();
    }

    @Override
    public void onButtonClicked(int actionId) {

        switch (actionId) {
            case R.id.btn_choose_photo:
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    requestPermission(REQUEST_STORAGE_PERMISSION_CHOOSER);
                } else {
                    openImageChooser();
                }
                break;
            case R.id.btn_take_photo:
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermission(REQUEST_STORAGE_PERMISSION_CAMERA);
                } else {
                    // Launch the camera if the permission exists
                    launchCamera();
                }
                break;
            case R.id.action_preview:
                NavigationUtils.openPreview(this);
                break;
            case R.id.action_rotate_right:

                // finding current fragment

                FragmentManager fm = getSupportFragmentManager();

                EditorFragment current = (EditorFragment) fm.findFragmentById(R.id.fragment_container);
                new RotateTask(current).execute();

                break;
        }

    }

    private void launchCamera() {

        // Create the capture image intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the temporary File where the photo should go
            File photoFile = null;
            try {
                photoFile = BitmapUtils.createTempImageFile(this);
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                // Get the path of the temporary file
                mTempPhotoPath = photoFile.getAbsolutePath();

                // Get the content URI for the image file
                Uri photoURI = FileProvider.getUriForFile(this,
                        FILE_PROVIDER_AUTHORITY,
                        photoFile);

                // Add the URI so the camera can store the image
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                // Launch the camera activity
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Called when you request permission to read and write to external storage
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If you get permission, launch the camera
                    launchCamera();
                } else {
                    // If you do not get permission, show a Toast
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_STORAGE_PERMISSION_CHOOSER:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // If you get permission, launch the camera
                        openImageChooser();
                    } else {
                        // If you do not get permission, show a Toast
                        Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                    }
                    break;
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

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, EditorFragment.newInstance(BitmapFactory.decodeFile(mTempPhotoPath)))
                    .addToBackStack(null)
                    .commitAllowingStateLoss();

            }
    }

    private void openImageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.image_chooser_title)), PICK_IMAGE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_settings:
                NavigationUtils.openSettings(this);
                return true;
            case R.id.action_instructions:
                NavigationUtils.openInstructions(this);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void requestPermission(int requestCode){
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
    }

    public class RotateTask extends AsyncTask<Void, Void, Bitmap> {

        private EditorFragment mCurrent;

        public RotateTask(EditorFragment current) {
            mCurrent = current;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            return new WeakReference<>(BitmapUtils.rotateBitmap(mCurrent.getBitmap())).get();
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            FragmentManager fm = getSupportFragmentManager();

            mCurrent.setBitmap(result);

            fm.beginTransaction()
                    .detach(mCurrent)
                    .attach(mCurrent)
                    .commit();
        }
    }
}
