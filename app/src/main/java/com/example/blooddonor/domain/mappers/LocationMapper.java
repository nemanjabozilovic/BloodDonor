package com.example.blooddonor.domain.mappers;

import com.example.blooddonor.data.models.Location;
import com.example.blooddonor.domain.models.LocationDTO;

import java.util.List;
import java.util.stream.Collectors;

public class LocationMapper {
    public static LocationDTO toDTO(Location location) {
        return new LocationDTO(location.getId(), location.getName(), location.getPhoneNumbers(), location.getLocation(), location.getLocationTypeId(), location.getLongitude(), location.getLatitude());
    }

    public static Location toModel(LocationDTO locationDTO) {
        return new Location(locationDTO.getId(), locationDTO.getName(), locationDTO.getPhoneNumbers(), locationDTO.getLocation(), locationDTO.getLocationTypeId(), locationDTO.getLongitude(), locationDTO.getLatitude());
    }

    public static List<LocationDTO> toDTO(List<Location> locations) {
        return locations.stream()
                .map(LocationMapper::toDTO)
                .collect(Collectors.toList());
    }

    public static List<Location> toModel(List<LocationDTO> locationDTOs) {
        return locationDTOs.stream()
                .map(LocationMapper::toModel)
                .collect(Collectors.toList());
    }
}