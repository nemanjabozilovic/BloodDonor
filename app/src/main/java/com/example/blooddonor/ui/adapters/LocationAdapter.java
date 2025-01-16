package com.example.blooddonor.ui.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonor.R;
import com.example.blooddonor.domain.models.LocationDTO;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    private List<LocationDTO> locations;
    private final OnLocationClickListener listener;
    private boolean isSelectionMode = false;

    public LocationAdapter(List<LocationDTO> locations, OnLocationClickListener listener) {
        this.locations = locations;
        this.listener = listener;
    }

    public LocationAdapter(List<LocationDTO> locations, OnLocationClickListener listener, boolean isSelectionMode) {
        this(locations, listener);
        this.isSelectionMode = isSelectionMode;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setLocations(List<LocationDTO> locations) {
        this.locations = locations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        LocationDTO location = locations.get(position);
        holder.nameTextView.setText(location.getName());
        holder.typeTextView.setText(location.getLocationTypeName());

        holder.itemView.setOnClickListener(v -> listener.onLocationClick(location));

        if (isSelectionMode) {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        } else {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);

            holder.btnEdit.setOnClickListener(v -> listener.onEditClick(location));
            holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(location.getId()));
        }
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public interface OnLocationClickListener {
        void onLocationClick(LocationDTO location);
        void onEditClick(LocationDTO location);
        void onDeleteClick(int locationId);
    }

    public static class LocationViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, typeTextView;
        ImageView btnEdit, btnDelete;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.location_name);
            typeTextView = itemView.findViewById(R.id.location_type);
            btnEdit = itemView.findViewById(R.id.btn_edit_location);
            btnDelete = itemView.findViewById(R.id.btn_delete_location);
        }
    }
}