package com.example.blooddonor.domain.repositories;

import com.example.blooddonor.data.models.Location;

import java.util.List;

public interface LocationRepository {
    Location getLocationById(int locationId);
    List<Location> getAllLocations();
    boolean insertLocation(Location location);
    boolean updateLocation(Location location);
    boolean deleteLocation(int locationId);
}