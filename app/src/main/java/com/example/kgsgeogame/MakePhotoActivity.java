package com.example.kgsgeogame;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MakePhotoActivity extends Activity {
    public final static String DEBUG_TAG = "MakePhotoActivity";
    private Camera camera;
    private int cameraId = 0;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int IMAGE_CAPTURE = 102;
    double longitude = 0;
    double latitude = 0;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_photo);
        if (checkPermission()) {
            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            TextView tv = (TextView) findViewById(R.id.tex_locat);
            String lonLat = "Long: " + Double.toString(longitude) + " | Lat: " + Double.toString(latitude);
            tv.setText(lonLat);
            // Launch the camera as soon as this activity loads
            //dispatchTakePictureIntent();
        }
        else{
            requestPermission();
        }
    }
    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            TextView tv = (TextView) findViewById(R.id.tex_locat);
            String lonLat = "Long: " + Double.toString(longitude) + " | Lat: " + Double.toString(latitude);
            tv.setText(lonLat);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

//lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //onClick for the camera button
    // This loads the camera
    public void camOnClick(View view) {
        try {
            // Launch into the camera
            dispatchTakePictureIntent();
            }
        catch (Exception e){
            // Display any exceptions
            Toast.makeText(this,"CAN NOT TAKE PICTURE ERROR: " + e.toString(),Toast.LENGTH_LONG).show();
        }
    }

    // This stores the picture into an album it internally keeps track of
    public void storeOnClick(View view){
        try{
            picFile = new File(mCurrentPhotoPath);
            PhotoAlbum.store(picFile);
            openMainScreen();
            Toast.makeText(this,"Saved to Album!",Toast.LENGTH_LONG).show();
        }
        catch (Exception e){
            Toast.makeText(this,"CAN NOT STORE PICTURE ERROR: " + e.toString(),Toast.LENGTH_LONG).show();
        }
    }

    public void openMainScreen(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    static final int REQUEST_TAKE_PHOTO = 1;
    // This is the code to launch into the camera
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this,ex.toString(),Toast.LENGTH_LONG).show();
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                Toast.makeText(this,photoURI.toString(),Toast.LENGTH_LONG).show();
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
    File picFile = null;
    // This handles the result of the camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        super.onActivityResult(requestCode, resultCode, intent);

        try {
            String currentPhotoPath = mCurrentPhotoPath;
            picFile = new File(currentPhotoPath);
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            if (bitmap != null) {
                // TODO GO TO THE TAGGING THE IMAGE ACTIVITY
                // This will show the image, allow the user to select tags for the image, and describe the image
                ImageView iv = (ImageView) findViewById(R.id.img_preview);
                iv.setImageBitmap(bitmap);
                Toast.makeText(this, "Preview Loaded", Toast.LENGTH_LONG).show();
                ImageButton subButton = (ImageButton) findViewById(R.id.submitButton);
                subButton.setVisibility(View.VISIBLE);
            }
        } catch (Exception error) {
            Toast.makeText(this,"It looks like you didn't take a picture.",Toast.LENGTH_LONG).show();
            error.printStackTrace();
        }
    }
// This checks all of our permissions (Camera, External Storage, and Fine Location)
    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }
// This tries to get said permissions if they are not yet open
    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();

                    // main logic
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("You need to allow access permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermission();
                                            }
                                        }
                                    });
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MakePhotoActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

}