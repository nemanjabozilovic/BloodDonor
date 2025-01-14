package com.example.blooddonor.domain.mappers;

import com.example.blooddonor.data.models.LocationType;
import com.example.blooddonor.domain.models.LocationTypeDTO;

import java.util.List;
import java.util.stream.Collectors;

public class LocationTypeMapper {
    public static LocationTypeDTO toDTO(LocationType locationType) {
        return new LocationTypeDTO(locationType.getId(), locationType.getName());
    }

    public static LocationType toModel(LocationTypeDTO locationTypeDTO) {
        return new LocationType(locationTypeDTO.getId(), locationTypeDTO.getName());
    }

    public static List<LocationTypeDTO> toDTO(List<LocationType> locationTypes) {
        return locationTypes.stream()
                .map(LocationTypeMapper::toDTO)
                .collect(Collectors.toList());
    }

    public static List<LocationType> toModel(List<LocationTypeDTO> locationTypeDTOs) {
        return locationTypeDTOs.stream()
                .map(LocationTypeMapper::toModel)
                .collect(Collectors.toList());
    }
}