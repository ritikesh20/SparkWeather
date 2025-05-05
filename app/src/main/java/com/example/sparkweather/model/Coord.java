package com.example.sparkweather.model;

import java.io.Serializable;

public class Coord implements Serializable {
    public double lon;
    public double lat;

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
}


