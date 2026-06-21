package io.javabrains.coronavirustracker.repository;

import io.javabrains.coronavirustracker.model.LocationStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationStatsRepository extends JpaRepository<LocationStatsEntity, Long> {

    List<LocationStatsEntity> findByCountry(String country);

    List<LocationStatsEntity> findByState(String state);
}
