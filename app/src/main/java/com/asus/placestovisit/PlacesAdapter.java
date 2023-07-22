package com.asus.placestovisit;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.asus.placestovisit.databinding.RecyclerRowBinding;

import java.util.ArrayList;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlacesHolder> {

    ArrayList<Places> placesArrayList;

    public PlacesAdapter(ArrayList<Places> placesArrayList) {
        this.placesArrayList = placesArrayList;
    }

    @NonNull
    @Override
    public PlacesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerRowBinding binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new PlacesHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesHolder holder, int position) {

        holder.binding.recyclerViewTextView.setText(placesArrayList.get(position).name);


    }

    @Override
    public int getItemCount() {
        return placesArrayList.size();
    }

    
    public class PlacesHolder extends RecyclerView.ViewHolder {

        private RecyclerRowBinding binding;

        public PlacesHolder(RecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }


}
