package com.example.blooddonor.domain.usecases.interfaces;

import com.example.blooddonor.data.models.BloodRequest;
import com.example.blooddonor.domain.models.BloodRequestDTO;

import java.util.List;

public interface BloodRequestUseCase {
    BloodRequestDTO getBloodRequestById(int requestId);
    List<BloodRequestDTO> getBloodRequestsByUserId(int userId);
    List<BloodRequestDTO> getAllBloodRequests();
    BloodRequestDTO insertBloodRequest(BloodRequestDTO bloodRequestDTO);
    boolean updateBloodRequest(BloodRequestDTO bloodRequestDTO);
    boolean deleteBloodRequest(int requestId);
}