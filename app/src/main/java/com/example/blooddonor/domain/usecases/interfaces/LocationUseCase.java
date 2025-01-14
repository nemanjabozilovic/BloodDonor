package com.example.blooddonor.domain.usecases.interfaces;

import com.example.blooddonor.domain.models.LocationDTO;

import java.util.List;

public interface LocationUseCase {
    LocationDTO getLocationById(int locationId);
    List<LocationDTO> getAllLocations();
    LocationDTO insertLocation(LocationDTO locationDTO);
    boolean updateLocation(LocationDTO locationDTO);
    boolean deleteLocation(int locationId);
}