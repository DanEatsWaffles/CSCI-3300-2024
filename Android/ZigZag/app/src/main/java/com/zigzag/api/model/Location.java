package com.zigzag.api.model;

public class Location {
    private double longitude;
    private double latitude;
    private double distance;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getDistanceString() {
        final double METERS_TO_MILES = 0.000621371;
        final double METERS_TO_FEET = 3.28084;

        int distanceInMiles = (int) Math.floor(distance * METERS_TO_MILES);

        if (distanceInMiles < 0.1) {
            int distanceInFeet = (int) Math.floor(distance * METERS_TO_FEET);
            return distanceInFeet + " " + (distanceInFeet == 1 ? "foot" : "feet"); // If it's one foot, return foot instead of feet.
        } else {
            return distanceInMiles + " miles";
        }
    }
}
