package com.example.blooddonor.domain.usecases.implementation;

import com.example.blooddonor.data.models.BloodRequest;
import com.example.blooddonor.data.models.Location;
import com.example.blooddonor.data.models.User;
import com.example.blooddonor.domain.mappers.BloodRequestMapper;
import com.example.blooddonor.domain.models.BloodRequestDTO;
import com.example.blooddonor.domain.repositories.BloodRequestRepository;
import com.example.blooddonor.domain.repositories.LocationRepository;
import com.example.blooddonor.domain.repositories.UserRepository;
import com.example.blooddonor.domain.usecases.interfaces.BloodRequestUseCase;

import java.util.ArrayList;
import java.util.List;

public class BloodRequestUseCaseImpl implements BloodRequestUseCase {
    private final BloodRequestRepository bloodRequestRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    public BloodRequestUseCaseImpl(BloodRequestRepository bloodRequestRepository, LocationRepository locationRepository, UserRepository userRepository) {
        this.bloodRequestRepository = bloodRequestRepository;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BloodRequestDTO getBloodRequestById(int requestId) {
        BloodRequest bloodRequest = bloodRequestRepository.getBloodRequestById(requestId);
        if (bloodRequest == null) {
            return null;
        }

        BloodRequestDTO bloodRequestDTO = BloodRequestMapper.toDTO(bloodRequest);

        Location location = locationRepository.getLocationById(bloodRequest.getLocationId());
        if (location != null) {
            bloodRequestDTO.setLocationName(location.getName());
        }

        User user = userRepository.getUserById(bloodRequest.getSubmittedBy());
        if (user != null) {
            bloodRequestDTO.setSubmittedByName(user.getFullName());
        }

        return bloodRequestDTO;
    }

    @Override
    public List<BloodRequestDTO> getBloodRequestsByUserId(int userId) {
        List<BloodRequestDTO> bloodRequestDTOs = new ArrayList<>();
        List<BloodRequest> bloodRequests = bloodRequestRepository.getBloodRequestsByUserId(userId);

        for (BloodRequest bloodRequest : bloodRequests) {
            BloodRequestDTO dto = BloodRequestMapper.toDTO(bloodRequest);

            Location location = locationRepository.getLocationById(bloodRequest.getLocationId());
            if (location != null) {
                dto.setLocationName(location.getName());
            }

            User user = userRepository.getUserById(bloodRequest.getSubmittedBy());
            if (user != null) {
                dto.setSubmittedByName(user.getFullName());
            }

            bloodRequestDTOs.add(dto);
        }

        return bloodRequestDTOs;
    }

    @Override
    public List<BloodRequestDTO> getAllBloodRequests() {
        List<BloodRequestDTO> bloodRequestDTOs = new ArrayList<>();
        List<BloodRequest> bloodRequests = bloodRequestRepository.getAllBloodRequests();

        for (BloodRequest bloodRequest : bloodRequests) {
            BloodRequestDTO dto = BloodRequestMapper.toDTO(bloodRequest);

            Location location = locationRepository.getLocationById(bloodRequest.getLocationId());
            if (location != null) {
                dto.setLocationName(location.getName());
            }

            User user = userRepository.getUserById(bloodRequest.getSubmittedBy());
            if (user != null) {
                dto.setSubmittedByName(user.getFullName());
            }

            bloodRequestDTOs.add(dto);
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