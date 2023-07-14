package com.asus.placestovisit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.asus.placestovisit.databinding.ActivityMainBinding;
import com.asus.placestovisit.databinding.ActivityPlacesBinding;

public class PlacesActivity extends AppCompatActivity {

    private ActivityPlacesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlacesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }

    public void save(View view){

    }

    public void selectImage(View view){

    }

}