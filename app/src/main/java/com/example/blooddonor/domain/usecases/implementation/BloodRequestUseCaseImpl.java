package com.example.blooddonor.domain.usecases.implementation;

import com.example.blooddonor.data.models.BloodRequest;
import com.example.blooddonor.domain.mappers.BloodRequestMapper;
import com.example.blooddonor.domain.models.BloodRequestDTO;
import com.example.blooddonor.domain.repositories.BloodRequestRepository;
import com.example.blooddonor.domain.repositories.LocationRepository;
import com.example.blooddonor.domain.usecases.interfaces.BloodRequestUseCase;

import java.util.ArrayList;
import java.util.List;

public class BloodRequestUseCaseImpl implements BloodRequestUseCase {
    private final BloodRequestRepository bloodRequestRepository;
    private final LocationRepository locationRepository;

    public BloodRequestUseCaseImpl(BloodRequestRepository bloodRequestRepository, LocationRepository locationRepository) {
        this.bloodRequestRepository = bloodRequestRepository;
        this.locationRepository = locationRepository;
    }

    @Override
    public BloodRequestDTO getBloodRequestById(int requestId) {
        BloodRequest bloodRequest = bloodRequestRepository.getBloodRequestById(requestId);
        return bloodRequest != null ? BloodRequestMapper.toDTO(bloodRequest) : null;
    }

    @Override
    public List<BloodRequestDTO> getAllBloodRequests() {
        List<BloodRequestDTO> bloodRequestDTOs = new ArrayList<>();
        List<BloodRequest> bloodRequests = bloodRequestRepository.getAllBloodRequests();
        for (BloodRequest bloodRequest : bloodRequests) {
            bloodRequestDTOs.add(BloodRequestMapper.toDTO(bloodRequest));
        }
        return bloodRequestDTOs;
    }

    @Override
    public BloodRequestDTO insertBloodRequest(BloodRequestDTO bloodRequestDTO) {
        BloodRequest bloodRequest = BloodRequestMapper.toModel(bloodRequestDTO);
        boolean success = bloodRequestRepository.insertBloodRequest(bloodRequest);
        return success ? BloodRequestMapper.toDTO(bloodRequest) : null;
    }

    @Override
    public boolean updateBloodRequest(BloodRequestDTO bloodRequestDTO) {
        BloodRequest bloodRequest = BloodRequestMapper.toModel(bloodRequestDTO);
        return bloodRequestRepository.updateBloodRequest(bloodRequest);
    }

    @Override
    public boolean deleteBloodRequest(int requestId) {
        return bloodRequestRepository.deleteBloodRequest(requestId);
    }
}