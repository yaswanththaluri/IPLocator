package com.example.yaswanththaluri.iplocator;

public class Event
{
    public final String city;
    public final String regionname;
    public final String country;
    public final String lalitude;
    public final String longitude;
    public final String organisation;

    public Event(String cityname, String region, String cntry, String lat, String lon, String org)
    {
        city = cityname;
        regionname = region;
        country = cntry;
        lalitude = lat;
        longitude = lon;
        organisation = org;
    }
}
