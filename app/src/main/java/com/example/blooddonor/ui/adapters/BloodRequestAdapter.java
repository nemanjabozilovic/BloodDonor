package com.example.blooddonor.ui.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonor.R;
import com.example.blooddonor.domain.models.BloodRequestDTO;
import com.example.blooddonor.domain.models.LocationDTO;
import com.example.blooddonor.domain.usecases.interfaces.LocationUseCase;

import java.util.List;

public class BloodRequestAdapter extends RecyclerView.Adapter<BloodRequestAdapter.ViewHolder> {
    private final LocationUseCase locationUseCase;
    private List<BloodRequestDTO> bloodRequests;

    public BloodRequestAdapter(List<BloodRequestDTO> bloodRequests, LocationUseCase locationUseCase) {
        this.bloodRequests = bloodRequests;
        this.locationUseCase = locationUseCase;
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
        holder.bind(request, locationUseCase);
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
        private final View detailsButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            patientName = itemView.findViewById(R.id.patient_name);
            location = itemView.findViewById(R.id.location);
            bloodType = itemView.findViewById(R.id.blood_type);
            deadline = itemView.findViewById(R.id.deadline);
            detailsButton = itemView.findViewById(R.id.details_button);
        }

        private static void setupListeners(
                TextView getDirections,
                LocationDTO locationDTO,
                TextView share,
                BloodRequestDTO bloodRequest,
                TextView contact,
                View view) {

            getDirectionsListener(getDirections, locationDTO);
            shareListener(locationDTO, share, bloodRequest);

            contact.setOnClickListener(v -> {
                if (locationDTO.getPhoneNumbers() != null && !locationDTO.getPhoneNumbers().isEmpty()) {
                    String[] phoneNumbers = locationDTO.getPhoneNumbers().split(",");

                    if (phoneNumbers.length == 1) {
                        makePhoneCall(view, phoneNumbers[0].trim());
                    } else {
                        showPhoneNumberSelectionDialog(view, phoneNumbers);
                    }
                } else {
                    Toast.makeText(view.getContext(), R.string.error_location_not_found, Toast.LENGTH_SHORT).show();
                }
            });
        }

        private static void showPhoneNumberSelectionDialog(View view, String[] phoneNumbers) {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle(R.string.select_phone_number)
                    .setItems(phoneNumbers, (dialog, which) -> makePhoneCall(view, phoneNumbers[which].trim()))
                    .show();
        }

        private static void makePhoneCall(View view, String phoneNumber) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            view.getContext().startActivity(intent);
        }

        private static void shareListener(LocationDTO locationDTO, TextView share, BloodRequestDTO bloodRequest) {
            share.setOnClickListener(v -> {
                String shareMessage = String.format(
                        "📢 Blood Donation Request 🩸\n\n" +
                                "Dear Blood Donors,\n\n" +
                                "A new blood donation request has been created. Your help can save a life!\n\n" +
                                "🔹 Patient Name: %1$s\n" +
                                "🩸 Required Blood Type: %2$s\n\n" +
                                "🏥 Location: %3$s - %4$s\n" +
                                "📞 Contact: %5$s\n\n" +
                                "🩸 Compatible Donors: %6$s\n" +
                                "⏳ Deadline: %7$s\n\n" +
                                "If you are eligible and willing to donate, please reach out as soon as possible.\n\n" +
                                "Your generosity can make a real difference. Thank you for your life-saving contribution! ❤️\n\n" +
                                "Blood Donor Team 🩸",
                        bloodRequest.getPatientName(),
                        bloodRequest.getBloodType(),
                        bloodRequest.getLocationName(),
                        locationDTO.getLocation(),
                        locationDTO.getPhoneNumbers(),
                        bloodRequest.getPossibleDonors(),
                        bloodRequest.getDeadline() != null && !bloodRequest.getDeadline().isEmpty() ? bloodRequest.getDeadline() : "ASAP");

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

                Intent chooser = Intent.createChooser(shareIntent, v.getContext().getString(R.string.share_via));
                v.getContext().startActivity(chooser);
            });
        }

        private static void getDirectionsListener(TextView getDirections, LocationDTO locationDTO) {
            getDirections.setOnClickListener(v -> {
                if (locationDTO.getLatitude() != 0.0 && locationDTO.getLongitude() != 0.0) {
                    Uri gmmIntentUri = new Uri.Builder()
                            .scheme("google.navigation")
                            .appendQueryParameter("q", locationDTO.getLatitude() + "," + locationDTO.getLongitude())
                            .build();
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

                    if (mapIntent.resolveActivity(v.getContext().getPackageManager()) != null) {
                        v.getContext().startActivity(mapIntent);
                    } else {
                        Toast.makeText(v.getContext(), R.string.maps_app_not_found, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(v.getContext(), R.string.error_location_not_found, Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void bind(BloodRequestDTO request, LocationUseCase locationUseCase) {
            patientName.setText(request.getPatientName());
            location.setText(request.getLocationName());
            bloodType.setText(request.getBloodType());
            deadline.setText(request.getDeadline());

            detailsButton.setOnClickListener(v -> showBloodRequestDetailsDialog(v, request, locationUseCase));
        }

        private void showBloodRequestDetailsDialog(View view, BloodRequestDTO bloodRequest, LocationUseCase locationUseCase) {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            LayoutInflater inflater = LayoutInflater.from(view.getContext());
            View dialogView = inflater.inflate(R.layout.dialog_blood_request_details, null);
            builder.setView(dialogView);

            TextView submittedBy = dialogView.findViewById(R.id.submitted_by_value);
            TextView patientName = dialogView.findViewById(R.id.patient_name_value);
            TextView location = dialogView.findViewById(R.id.location_value);
            TextView locationType = dialogView.findViewById(R.id.location_type_value);
            TextView locationPhoneNumbers = dialogView.findViewById(R.id.location_phone_numbers_value);
            TextView bloodType = dialogView.findViewById(R.id.blood_type_value);
            TextView possibleDonors = dialogView.findViewById(R.id.possible_donors_value);
            TextView deadline = dialogView.findViewById(R.id.deadline_value);
            TextView getDirections = dialogView.findViewById(R.id.get_directions_text);
            TextView share = dialogView.findViewById(R.id.share_text);
            TextView contact = dialogView.findViewById(R.id.contact_text);

            submittedBy.setText(view.getContext().getString(
                    R.string.submitted_by_format,
                    bloodRequest.getSubmittedByName(),
                    bloodRequest.getCreatedAt()
            ));

            patientName.setText(bloodRequest.getPatientName());

            LocationDTO locationDTO = locationUseCase.getLocationById(bloodRequest.getLocationId());

            location.setText(view.getContext().getString(
                    R.string.location_format,
                    bloodRequest.getLocationName(),
                    locationDTO.getLocation()
            ));
            locationType.setText(locationDTO.getLocationTypeName());
            locationPhoneNumbers.setText(locationDTO.getPhoneNumbers());
            bloodType.setText(bloodRequest.getBloodType());
            possibleDonors.setText(bloodRequest.getPossibleDonors());
            deadline.setText(bloodRequest.getDeadline());

            setupListeners(getDirections, locationDTO, share, bloodRequest, contact, view);

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}