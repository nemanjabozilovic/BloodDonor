package com.example.blooddonor.domain.usecases.implementation;

import com.example.blooddonor.data.models.BloodRequest;
import com.example.blooddonor.data.models.Location;
import com.example.blooddonor.data.models.LocationType;
import com.example.blooddonor.domain.mappers.LocationMapper;
import com.example.blooddonor.domain.models.LocationDTO;
import com.example.blooddonor.domain.repositories.BloodRequestRepository;
import com.example.blooddonor.domain.repositories.LocationRepository;
import com.example.blooddonor.domain.repositories.LocationTypeRepository;
import com.example.blooddonor.domain.usecases.interfaces.LocationUseCase;

import java.util.ArrayList;
import java.util.List;

public class LocationUseCaseImpl implements LocationUseCase {
    private final LocationRepository locationRepository;
    private final LocationTypeRepository locationTypeRepository;
    private final BloodRequestRepository bloodRequestRepository;

    public LocationUseCaseImpl(LocationRepository locationRepository, LocationTypeRepository locationTypeRepository, BloodRequestRepository bloodRequestRepository) {
        this.locationRepository = locationRepository;
        this.locationTypeRepository = locationTypeRepository;
        this.bloodRequestRepository = bloodRequestRepository;
    }

    @Override
    public LocationDTO getLocationById(int locationId) {
        Location location = locationRepository.getLocationById(locationId);
        if (location == null) {
            return null;
        }

        LocationDTO locationDTO = LocationMapper.toDTO(location);

        LocationType locationType = locationTypeRepository.getLocationTypeById(location.getLocationTypeId());
        locationDTO.setLocationTypeName(locationType != null ? locationType.getName() : "N/A");

        return locationDTO;
    }

    @Override
    public List<LocationDTO> getAllLocations() {
        List<LocationDTO> locationDTOs = new ArrayList<>();
        locationRepository.getAllLocations().forEach(location -> {
            LocationDTO locationDTO = LocationMapper.toDTO(location);
            LocationType locationType = locationTypeRepository.getLocationTypeById(location.getLocationTypeId());
            locationDTO.setLocationTypeName(locationType != null ? locationType.getName() : "N/A");
            locationDTOs.add(locationDTO);
        });
        return locationDTOs;
    }

    @Override
    public LocationDTO insertLocation(LocationDTO locationDTO) {
        return locationRepository.insertLocation(LocationMapper.toModel(locationDTO))
                ? locationDTO
                : null;
    }

    @Override
    public boolean updateLocation(LocationDTO locationDTO) {
        return locationRepository.updateLocation(LocationMapper.toModel(locationDTO));
    }

    @Override
    public boolean deleteLocation(int locationId) {
        List<BloodRequest> bloodRequests = bloodRequestRepository.getAllBloodRequests();

        for (BloodRequest request : bloodRequests) {
            if (request.getLocationId() == locationId) {
                bloodRequestRepository.deleteBloodRequest(request.getId());
            }
        }

        return locationRepository.deleteLocation(locationId);
    }

}