package com.example.yaswanththaluri.iplocator;

public class Event {
    public final String city;
    public final String regionname;
    public final String country;
    public final String lalitude;
    public final String longitude;
    public final String organisation;
    public final String status;

    public Event(String cityname, String region, String cntry, String lat, String lon, String org, String st) {
        city = cityname;
        regionname = region;
        country = cntry;
        lalitude = lat;
        longitude = lon;
        organisation = org;
        status = st;
    }
}
