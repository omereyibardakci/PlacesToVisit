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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlacesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // open or create database
        database = this.openOrCreateDatabase("PLACES",MODE_PRIVATE,null);


        registerLauncher();


        Intent intent = getIntent();
        String info = intent.getStringExtra("info");
        if (info.equals("new")){
            // new add places
            binding.button.setVisibility(View.VISIBLE);

        }else {
            // old places
            int placesId = intent.getIntExtra("placesId",1);
            binding.button.setVisibility(View.INVISIBLE);

            // pull data for show the places
            try {

                Cursor cursor = database.rawQuery("SELECT * FROM Places WHERE id = ?",new String[] {String.valueOf(placesId)});

                int nameIndex = cursor. getColumnIndex("name");
                int countryIndex = cursor.getColumnIndex("country");
                int yearIndex = cursor.getColumnIndex("year");
                int imageIndex = cursor.getColumnIndex("image");

                while (cursor.moveToNext()){

                    binding.editTextName.setText(cursor.getString(nameIndex));
                    binding.editTextCountry.setText(cursor.getString(countryIndex));
                    binding.editTextYear.setText(cursor.getString(yearIndex));


                    // byte array is not put imageview
                    // convert bitmap to put byte array into imageview
                    byte [] bytes = cursor.getBlob(imageIndex);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    binding.imageView.setImageBitmap(bitmap);

                }
                cursor.close();

            }catch (Exception e){
                e.printStackTrace();
            }


        }

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



        // Data Access Layer
        try {

            database.execSQL("CREATE TABLE IF NOT EXISTS Places (id INTEGER PRIMARY KEY,name VARCHAR, country VARCHAR, year VARCHAR, image BLOB)");

            String sqlInsertString = "INSERT INTO Places (name,country,year,image) VALUES (?,?,?,?)";
            SQLiteStatement sqLiteStatement = database.compileStatement(sqlInsertString);
            sqLiteStatement.bindString(1,name);
            sqLiteStatement.bindString(2,country);
            sqLiteStatement.bindString(3,year);
            sqLiteStatement.bindBlob(4,byteImageArray);
            sqLiteStatement.execute();

        }catch (Exception e){
            e.printStackTrace();
        }

        Intent intent = new Intent(PlacesActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);        // closes all previous activities
        startActivity(intent);


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
                    Toast.makeText(PlacesActivity.this,"Permission needed!",Toast.LENGTH_LONG).show();
                }

            }
        });

    }


}