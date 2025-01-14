package com.example.blooddonor.domain.usecases.implementation;

import com.example.blooddonor.domain.mappers.LocationTypeMapper;
import com.example.blooddonor.domain.models.LocationTypeDTO;
import com.example.blooddonor.domain.repositories.LocationTypeRepository;
import com.example.blooddonor.domain.usecases.interfaces.LocationTypeUseCase;

import java.util.ArrayList;
import java.util.List;

public class LocationTypeUseCaseImpl implements LocationTypeUseCase {
    private final LocationTypeRepository locationTypeRepository;

    public LocationTypeUseCaseImpl(LocationTypeRepository locationTypeRepository) {
        this.locationTypeRepository = locationTypeRepository;
    }

    @Override
    public LocationTypeDTO getLocationTypeById(int typeId) {
        return LocationTypeMapper.toDTO(locationTypeRepository.getLocationTypeById(typeId));
    }

    @Override
    public LocationTypeDTO getLocationTypeByName(String name) {
        return LocationTypeMapper.toDTO(locationTypeRepository.getLocationTypeByName(name));
    }

    @Override
    public List<LocationTypeDTO> getAllLocationTypes() {
        List<LocationTypeDTO> locationTypeDTOs = new ArrayList<>();
        locationTypeRepository.getAllLocationTypes().forEach(locationType ->
                locationTypeDTOs.add(LocationTypeMapper.toDTO(locationType))
        );
        return locationTypeDTOs;
    }
}