package io.javabrains.coronavirustracker.services;

import io.javabrains.coronavirustracker.model.LocationStats;
import io.javabrains.coronavirustracker.model.LocationStatsEntity;
import io.javabrains.coronavirustracker.repository.LocationStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationStatsPersistenceService {

    @Autowired
    private LocationStatsRepository repository;

    @Transactional
    public void saveAllStats(List<LocationStats> statsList) {
        repository.deleteAll();

        List<LocationStatsEntity> entities = statsList.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());

        repository.saveAll(entities);
    }

    public List<LocationStatsEntity> getAllFromDb() {
        return repository.findAll();
    }

    public List<LocationStatsEntity> getByCountry(String country) {
        return repository.findByCountry(country);
    }

    private LocationStatsEntity toEntity(LocationStats stats) {
        return new LocationStatsEntity(
                stats.getState(),
                stats.getCountry(),
                stats.getLatestTotal(),
                stats.getDiffFromPreviousDay()
        );
    }
}
