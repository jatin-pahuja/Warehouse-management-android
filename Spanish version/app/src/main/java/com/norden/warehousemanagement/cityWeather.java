package com.norden.warehousemanagement;

public class cityWeather {
    public String CityName;
    public double Temperature;
    public double MaxTemp;
    public double MinTemp;
    public double RealFeelTemp;
    public String Description;
    public String WeatherIcon;

    public cityWeather(String cityName, double temperature, double maxTemp, double minTemp, double realFeelTemp, String description, String weatherIcon) {
        CityName = cityName;
        Temperature = temperature - 273.15;
        MaxTemp = maxTemp - 273.15;
        MinTemp = minTemp - 273.15;
        RealFeelTemp = realFeelTemp - 273.15;
        Description = description;
        WeatherIcon = weatherIcon;
    }

    public double getTemperature() {
        return Temperature;
    }
    public void setTemperature(int temperature) {
        Temperature = temperature;
    }

    public double getMaxTemp() {
        return MaxTemp;
    }
    public void setMaxTemp(int maxTemp) {
        MaxTemp = maxTemp;
    }

    public double getMinTemp() {
        return MinTemp;
    }
    public void setMinTemp(int minTemp) {
        MinTemp = minTemp;
    }

    public double getRealFeelTemp() {
        return RealFeelTemp;
    }
    public void setRealFeelTemp(int realFeelTemp) {
        RealFeelTemp = realFeelTemp;
    }

    public String getDescription() {
        return Description;
    }
    public void setDescription(String description) {
        Description = description;
    }

    public String getWeatherIcon() {
        return WeatherIcon;
    }
    public void setWeatherIcon(String weatherIcon) {
        WeatherIcon = weatherIcon;
    }

    public String getCityName() {
        return CityName;
    }
    public void setCityName(String cityName) {
        CityName = cityName;
    }

    @Override
    public String toString() {
        return "cityWeather{" +
                "Temperature=" + Temperature +
                ", MaxTemp=" + MaxTemp +
                ", MinTemp=" + MinTemp +
                ", RealFeelTemp=" + RealFeelTemp +
                ", Description='" + Description + '\'' +
                '}';
    }
}
