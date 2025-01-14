package com.example.blooddonor.domain.usecases.implementation;

import com.example.blooddonor.data.models.LocationType;
import com.example.blooddonor.domain.mappers.LocationMapper;
import com.example.blooddonor.domain.models.LocationDTO;
import com.example.blooddonor.domain.repositories.LocationRepository;
import com.example.blooddonor.domain.repositories.LocationTypeRepository;
import com.example.blooddonor.domain.usecases.interfaces.LocationUseCase;

import java.util.ArrayList;
import java.util.List;

public class LocationUseCaseImpl implements LocationUseCase {
    private final LocationRepository locationRepository;
    private final LocationTypeRepository locationTypeRepository;

    public LocationUseCaseImpl(LocationRepository locationRepository, LocationTypeRepository locationTypeRepository) {
        this.locationRepository = locationRepository;
        this.locationTypeRepository = locationTypeRepository;
    }

    @Override
    public LocationDTO getLocationById(int locationId) {
        return LocationMapper.toDTO(locationRepository.getLocationById(locationId));
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
        return locationRepository.deleteLocation(locationId);
    }
}