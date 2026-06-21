package io.javabrains.coronavirustracker.model;

import javax.persistence.*;

@Entity
@Table(name = "location_stats")
public class LocationStatsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String state;

    private String country;

    private int latestTotal;

    private int diffFromPreviousDay;

    public LocationStatsEntity() {
    }

    public LocationStatsEntity(String state, String country, int latestTotal, int diffFromPreviousDay) {
        this.state = state;
        this.country = country;
        this.latestTotal = latestTotal;
        this.diffFromPreviousDay = diffFromPreviousDay;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getLatestTotal() {
        return latestTotal;
    }

    public void setLatestTotal(int latestTotal) {
        this.latestTotal = latestTotal;
    }

    public int getDiffFromPreviousDay() {
        return diffFromPreviousDay;
    }

    public void setDiffFromPreviousDay(int diffFromPreviousDay) {
        this.diffFromPreviousDay = diffFromPreviousDay;
    }
}
