package com.tripPlanner.project.repository.Maps;

import com.tripPlanner.project.entity.Maps.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<LocationEntity, Long> {
}
