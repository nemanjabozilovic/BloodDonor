package com.example.blooddonor.domain.usecases.interfaces;

import com.example.blooddonor.data.models.LocationType;
import com.example.blooddonor.domain.models.LocationTypeDTO;

import java.util.List;

public interface LocationTypeUseCase {
    LocationTypeDTO getLocationTypeById(int typeId);
    LocationTypeDTO getLocationTypeByName(String name);
    List<LocationTypeDTO> getAllLocationTypes();
}