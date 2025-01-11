package com.example.blooddonor.domain.repositories;

import com.example.blooddonor.data.models.LocationType;

import java.util.List;

public interface LocationTypeRepository {
    LocationType getLocationTypeById(int id);
    List<LocationType> getAllLocationTypes();
    boolean insertLocationType(LocationType locationType);
    boolean updateLocationType(LocationType locationType);
    boolean deleteLocationType(int id);
}