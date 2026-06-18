package io.javabrains.coronavirustracker.model;

public class LocationStats {

    private String state;
    private String country;
    private  int latestTotal;
    private  int diffFromPreviousDay;
    private int diffFromPreviousDay2;

    public int getDiffFromPreviousDay() {
        return diffFromPreviousDay;
    }

    public void setDiffFromPreviousDay(int diffFromPreviousDay) {
        this.diffFromPreviousDay = diffFromPreviousDay;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "LocationStats{" +
                "state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", latestTotal=" + latestTotal +
                '}';
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

    public int getDiffFromPreviousDay2() {
        return diffFromPreviousDay2;
    }

    public void setDiffFromPreviousDay2(int diffFromPreviousDay2) {
        this.diffFromPreviousDay2 = diffFromPreviousDay2;
    }

}
