package com.tripPlanner.project.repository.maps;

import com.tripPlanner.project.entity.maps.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<LocationEntity, Long> {
}
