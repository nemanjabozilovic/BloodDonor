package com.example.blooddonor.domain.repositories;

import com.example.blooddonor.data.models.BloodRequest;

import java.util.List;

public interface BloodRequestRepository {
    BloodRequest getBloodRequestById(int requestId);

    List<BloodRequest> getBloodRequestsByUserId(int userId);

    List<BloodRequest> getAllBloodRequests();

    boolean insertBloodRequest(BloodRequest request);

    boolean updateBloodRequest(BloodRequest request);

    boolean deleteBloodRequest(int requestId);
}