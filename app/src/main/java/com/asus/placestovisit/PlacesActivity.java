package com.asus.placestovisit;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.asus.placestovisit.databinding.ActivityMainBinding;
import com.asus.placestovisit.databinding.ActivityPlacesBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;

public class PlacesActivity extends AppCompatActivity {

    private ActivityPlacesBinding binding;

    ActivityResultLauncher<Intent> activityResultLauncher;      // it use to go to the gallery
    ActivityResultLauncher<String> permissionLauncher;          // it use to request permission

    Bitmap selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlacesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        registerLauncher();
    }

    public void save(View view){

        String name = binding.editTextName.getText().toString();
        String country = binding.editTextCountry.getText().toString();
        String year = binding.editTextYear.getText().toString();

        Bitmap smallImage = makeSmallerImage(selectedImage,300);

        // convert image to byte array
        // because it is needed so we can save the image in SQLITE

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] byteImageArray = outputStream.toByteArray();

    }

    public Bitmap makeSmallerImage(Bitmap image , int maximumSize){

        int width = image.getWidth();
        int height = image.getHeight();

        float imageRatio = (float) (width/height);

        if (imageRatio>1){
            // Image horizontal if greater than 1
            // landscape image or horizontal image
            width = maximumSize;
            height = (int) (width/imageRatio);

        }else{
            // Image vertical if less than 1
            // partrait image or vertical image
            height = maximumSize;
            width = (int) (height * imageRatio);

        }

        return Bitmap.createScaledBitmap(image,width,height,true);

    }


    public void selectImage(View view){

        if (Build.VERSION.SDK_INT>=33){

            //  PERMISSION_GRANTED == allowed ,   PERMISSION_DENIED == not allowed
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                // request permission in here,
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_MEDIA_IMAGES)){

                    Snackbar.make(view,"Permission needed for galary",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // request permission
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);

                        }
                    }).show();

                }else{
                    // request permission
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);

                }

            }else {
                // allowed and go to gallery
                Intent intentToGallary  = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                activityResultLauncher.launch(intentToGallary);
            }


        }else{

            //  PERMISSION_GRANTED == allowed ,   PERMISSION_DENIED == not allowed
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                // request permission in here,
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){

                    Snackbar.make(view,"Permission needed for galary",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // request permission
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

                        }
                    }).show();

                }else{
                    // request permission
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

                }

            }else {
                // allowed and go to gallery
                Intent intentToGallary  = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                activityResultLauncher.launch(intentToGallary);
            }
        }

    }

    public void registerLauncher(){

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                if (result.getResultCode()== Activity.RESULT_OK){   // if the user has selected something

                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null){
                        Uri imageData = intentFromResult.getData();     // gets the address of the photo


                        try{

                            if (Build.VERSION.SDK_INT >=28){

                                ImageDecoder.Source source = ImageDecoder.createSource(PlacesActivity.this.getContentResolver(),imageData);
                                selectedImage = ImageDecoder.decodeBitmap(source);
                                binding.imageView.setImageBitmap(selectedImage);

                            }else {

                                selectedImage = MediaStore.Images.Media.getBitmap(PlacesActivity.this.getContentResolver(),imageData);
                                binding.imageView.setImageBitmap(selectedImage);

                            }

                        } catch (Exception e){
                            e.printStackTrace();
                        }

                    }


                }


            }
        });




        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {

                if(result){
                    // permisssion granted
                    Intent intentToGallary = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallary);
                }else{
                    // permission denied
                    Toast.makeText(PlacesActivity.this,"Permission needed!",Toast.LENGTH_LONG);
                }

            }
        });

    }


}