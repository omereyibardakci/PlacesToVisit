package com.asus.placestovisit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.asus.placestovisit.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    ArrayList<Places> placesArrayList;
    PlacesAdapter placesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        placesArrayList = new ArrayList<Places>();


        // recyclerView
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        placesAdapter = new PlacesAdapter(placesArrayList);
        binding.recyclerView.setAdapter(placesAdapter);


        getData();



    }


    @SuppressLint("NotifyDataSetChanged")
    public void getData(){

        try {

            SQLiteDatabase database = this.openOrCreateDatabase("PLACES",MODE_PRIVATE,null);

            Cursor cursor = database.rawQuery(" SELECT * FROM Places",null);
            int idIndex = cursor.getColumnIndex("id");
            int nameIndex = cursor.getColumnIndex("name");

            while (cursor.moveToNext()){

                int id = cursor.getInt(idIndex);
                String name = cursor.getString(nameIndex);

                Places places = new Places(id,name);
                placesArrayList.add(places);



            }
                                                    //  !!!  VERY IMPORTANT  !!!
            placesAdapter.notifyDataSetChanged();   //  !!!  used to notify that data has been updated or changed  !!!

            cursor.close();

        } catch (Exception e){
            e.printStackTrace();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.places_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.addPlace){
            Intent intent=new Intent(this,PlacesActivity.class);
            intent.putExtra("info","new");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


}