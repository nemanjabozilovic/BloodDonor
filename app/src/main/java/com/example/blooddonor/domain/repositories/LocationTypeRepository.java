package com.example.blooddonor.domain.repositories;

import com.example.blooddonor.data.models.LocationType;

import java.util.List;

public interface LocationTypeRepository {
    LocationType getLocationTypeById(int id);
    LocationType getLocationTypeByName(String name);
    List<LocationType> getAllLocationTypes();
}