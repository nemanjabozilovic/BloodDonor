package com.example.blooddonor.ui.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.blooddonor.R;
import com.example.blooddonor.domain.models.BloodRequestDTO;
import java.util.List;

public class BloodRequestAdapter extends RecyclerView.Adapter<BloodRequestAdapter.ViewHolder> {
    private List<BloodRequestDTO> bloodRequests;

    public BloodRequestAdapter(List<BloodRequestDTO> bloodRequests) {
        this.bloodRequests = bloodRequests;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<BloodRequestDTO> newBloodRequests) {
        this.bloodRequests = newBloodRequests;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.blood_request_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BloodRequestDTO request = bloodRequests.get(position);
        holder.bind(request);
    }

    @Override
    public int getItemCount() {
        return bloodRequests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView patientName;
        private final TextView location;
        private final TextView bloodType;
        private final TextView deadline;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            patientName = itemView.findViewById(R.id.patient_name);
            location = itemView.findViewById(R.id.location);
            bloodType = itemView.findViewById(R.id.blood_type);
            deadline = itemView.findViewById(R.id.deadline);
        }

        public void bind(BloodRequestDTO request) {
            patientName.setText(request.getPatientName());

            location.setText(request.getLocationName());
            bloodType.setText(request.getBloodType());
            deadline.setText(request.getDeadline());
        }
    }
}