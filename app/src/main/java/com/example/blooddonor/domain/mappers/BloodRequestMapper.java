package com.example.blooddonor.domain.mappers;

import com.example.blooddonor.data.models.BloodRequest;
import com.example.blooddonor.domain.models.BloodRequestDTO;

import java.util.List;
import java.util.stream.Collectors;

public class BloodRequestMapper {
    public static BloodRequestDTO toDTO(BloodRequest request) {
        return new BloodRequestDTO(request.getId(), request.getSubmittedBy(), request.getPatientName(), request.getLocationId(), request.getBloodType(), request.getPossibleDonors(), request.getCreatedAt(), request.getDeadline());
    }

    public static BloodRequest toModel(BloodRequestDTO requestDTO) {
        return new BloodRequest(requestDTO.getId(), requestDTO.getSubmittedBy(), requestDTO.getPatientName(), requestDTO.getLocationId(), requestDTO.getBloodType(), requestDTO.getPossibleDonors(), requestDTO.getCreatedAt(), requestDTO.getDeadline());
    }

    public static List<BloodRequestDTO> toDTO(List<BloodRequest> requests) {
        return requests.stream()
                .map(BloodRequestMapper::toDTO)
                .collect(Collectors.toList());
    }

    public static List<BloodRequest> toModel(List<BloodRequestDTO> requestDTOs) {
        return requestDTOs.stream()
                .map(BloodRequestMapper::toModel)
                .collect(Collectors.toList());
    }
}