package weather.common.data.weatherstation;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import weather.common.data.resource.Resource;

/**
 * This class is to store daily average values for weather underground variables.
 * @author Alinson Antony
 * @version Spring 2012
 */
public class WeatherStationDailyAverage implements Serializable {
   
    private static final long serialVersionUID = 1L;
    private int resourceNumber;
    private Calendar date;
    
    //temperature
    private double temperatureMin;
    private double temperatureMax;
    private double temperatureMedian;
    
    //dewpoint
    private double dewpointMin;
    private double dewpointMax;
    private double dewpointMedian;
    
    //relative humidity
    private double relativeHumidityMin;
    private double relativeHumidityMax;
    private double relativeHumidityMedian;
    
    //pressure
    private double pressureMin;
    private double pressureMax;
    private double pressureMedian;
    
   //windspeed    
    private double windSpeedMin;
    private double windSpeedMax;
    private double windSpeedMedian;
    
    //solar radiation
    private double solarRadiationMin;
    private double solarRadiationMax;
    private double solarRadiationMedian;
   
    //hourly precipitation
    private double hourlyPrecipitationMin;
    private double hourlyPrecipitationMax;
    private double hourlyPrecipitationMedian;
    //daily precipitation
    private double dailyPrecipitation;
    
    //wind gust
    private double windGustMin;
    private double windGustMax;
    private double windGustMedian;
   /**
    * Constructor for creating WeatherStationDailyAverage object.
    * @param resource Object of the resource.
    * @param temperatureMin double that is minimum temperature.
    * @param temperatureMax double that is maximum temperature.
    * @param temperatureMedian double that is median temperature.
    * @param dewpointMin double that is minimum dew point
    * @param dewpointMax double that is maximum dew point
    * @param dewpointMedian double that is median dew point.
    * @param relativeHumidityMin double that is minimum relative humidity.
    * @param relativeHumidityMax double that is maximum relative humidity.
    * @param relativeHumidityMedian double that is median relative humidity.
    * @param pressureMin double that is minimum pressure.
    * @param pressureMax double that is maximum pressure.
    * @param pressureMedian double that is median pressure.
    * @param windSpeedMin double that is minimum wind speed.
    * @param windSpeedMax double that is maximum wind speed.
    * @param windSpeedMedian double that is median wind speed.
    * @param solarRadiationMin double that is minimum solar radiation.
    * @param solarRadiationMax double that is maximum solar radiation.
    * @param solarRadiationMedian double that is median solar radiation.
    * @param hourlyPrecipitationMin double that is minimum hourly precipitation.
    * @param hourlyPrecipitationMax double that is maximum hourly precipitation.
    * @param hourlyPrecipitationMedian double that is median hourly precipitation.
    * @param dailyPrecipitation double that is daily precipitation
    * @param windGustMin double that is minimum windGust.
    * @param windGustMax double that is maximum windGust.
    * @param windGustMedian double that is median windGust.

   */
    
    public WeatherStationDailyAverage(
     Resource resource,
     double temperatureMin,double temperatureMax,double temperatureMedian,
     double dewpointMin,double dewpointMax, double dewpointMedian,
     double relativeHumidityMin,double relativeHumidityMax,double relativeHumidityMedian,
     double pressureMin,double pressureMax, double pressureMedian,
     double windSpeedMin, double windSpeedMax, double windSpeedMedian,
     double solarRadiationMin,double solarRadiationMax,double solarRadiationMedian,
     double hourlyPrecipitationMin, double hourlyPrecipitationMax, double hourlyPrecipitationMedian,
     double dailyPrecipitation,
     double windGustMin, double windGustMax, double windGustMedian
      )
    {
       
        this.resourceNumber=resource.getResourceNumber();
        this.date=GregorianCalendar.getInstance();
        this.temperatureMin=temperatureMin;
        this.temperatureMax=temperatureMax;
        this.temperatureMedian=temperatureMedian;
        this.pressureMin=pressureMin;
        this.pressureMax=pressureMax;
        this.pressureMedian=pressureMedian;
        this.dewpointMin=dewpointMin;
        this.dewpointMax=dewpointMax;
        this.dewpointMedian=dewpointMedian;
        this.relativeHumidityMin=relativeHumidityMin;
        this.relativeHumidityMax=relativeHumidityMax;
        this.relativeHumidityMedian=relativeHumidityMedian;
        this.windSpeedMin=windSpeedMin;
        this.windSpeedMax=windSpeedMax;
        this.windSpeedMedian=windSpeedMedian;
        this.solarRadiationMin=solarRadiationMin;
        this.solarRadiationMax=solarRadiationMax;
        this.solarRadiationMedian=solarRadiationMedian;
        this.hourlyPrecipitationMin=hourlyPrecipitationMin;
        this.hourlyPrecipitationMax=hourlyPrecipitationMax;
        this.hourlyPrecipitationMedian=hourlyPrecipitationMedian;
        this.dailyPrecipitation=dailyPrecipitation;
        this.windGustMin=windGustMin;
        this.windGustMax=windGustMax;
        this.windGustMedian=windGustMedian;
       
    }
    /**
     * 
     * Constructor for creating WeatherStationDailyAverage object.
     * @param resource Object of the resource.
     */
    public WeatherStationDailyAverage(Resource resource)
    {
        resourceNumber=resource.getResourceNumber();
        this.date=GregorianCalendar.getInstance();
    }
    /**
     * Method to set DailyPrecipitation 
     * @param precipitation new double value to be set to precipitation.
     */
   public void setDailyPrecipitation(double precipitation)
   {
       this.dailyPrecipitation=precipitation;
   }
  /**
   * Method to get daily precipitation.
   * @return The daily precipitation.
   */
    public double getDailyPrecipitation()
   {
       return this.dailyPrecipitation;
   }
    /**
     * Method to set the resource number.
     * @param number New resource number.
     */
    public void setResourceNumber(int number)
    {
        this.resourceNumber=number;
    }
     /**
     * Method to set date.
     * @param date New date.
     */
    public void setDate(Calendar date)
    {
        this.date=date;
    }
    /**
     * Method to set minimum temperature.
     * @param minimum New value for temperatureMin.
     */
    public void setTemperatureMin(double minimum)
    {
        this.temperatureMin=minimum;
    }
     /**
     * Method to set maximum temperature.
     * @param maximum New value for temperatureMax.
     */
    public void setTemperatureMax(double maximum)
    {
        this.temperatureMax=maximum;
    }
     /**
     * Method to set median temperature.
     * @param median New value for temperatureMedian.
     */
    public void setTemperatureMedian(double median)
    {
        this.temperatureMedian=median;
    }
    /**
     * Method to set minimum pressure.
     * @param minimum New value for pressureMin.
     */
    public void setPressureMin(double minimum)
    {
        this.pressureMin=minimum;
    }
     /**
     * Method to set maximum pressure.
     * @param maximum New value for pressureMax.
     */
    public void setPressureMax(double maximum)
    {
        this.pressureMax=maximum;
    }
     /**
     * Method to set median pressure.
     * @param median New value for pressureMedian.
     */
    public void setPressureMedian(double median)
    {
        this.pressureMedian=median;
    }
     /**
     * Method to set minimum dew point.
     * @param minimum New value for dewpointMin.
     */
    public void setDewPointMin(double minimum)
    {
        this.dewpointMin=minimum;
    }
     /**
     * Method to set maximum dew point.
     * @param maximum New value for dewpointMax.
     */
    public void setDewPointMax(double maximum)
    {
        this.dewpointMax=maximum;
    }
     /**
     * Method to set median dew point.
     * @param median New value for dewpointMedian.
     */
    public void setDewPointMedian(double median)
    {
        this.dewpointMedian=median;
    }
     /**
     * Method to set minimum relative humidity.
     * @param minimum New value for relativeHumidityMin.
     */
    public void setRelativeHumidityMin(double minimum)
    {
        this.relativeHumidityMin=minimum;
    }
     /**
     * Method to set maximum relative humidity.
     * @param maximum New value for relativeHumidityMax.
     */
    public void setRelativeHumidityMax(double maximum)
    {
        this.relativeHumidityMax=maximum;
    }
     /**
     * Method to set median relative humidity.
     * @param median New value for relativeHumidityMedian.
     */
    public void setRelativeHumidityMedian(double median)
    {
        this.relativeHumidityMedian=median;
    }
     /**
     * Method to set minimum wind speed.
     * @param minimum New value for windSpeedMin.
     */
    public void setWindSpeedMin(double minimum)
    {
        this.windSpeedMin=minimum;
    }
     /**
     * Method to set maximum wind speed.
     * @param maximum New value for windSpeedMax.
     */
    public void setWindSpeedMax(double maximum)
    {
        this.windSpeedMax=maximum;
    }
     /**
     * Method to set median wind speed.
     * @param median New value for windSpeedMedian.
     */
    public void setWindSpeedMedian(double median)
    {
        this.windSpeedMedian=median;
    }
    
     /**
     * Method to set minimum solar radiation.
     * @param minimum New value for solarRadiationMin.
     */
    public void setSolarRadiationMin(double minimum)
    {
        this.solarRadiationMin=minimum;
    }
     /**
     * Method to set maximum solar radiation.
     * @param maximum New value for solarRadiationMax.
     */
    public void setSolarRadiationMax(double maximum)
    {
        this.solarRadiationMax=maximum;
    }
     /**
     * Method to set median solar radiation.
     * @param median New value for solarRadiationMedian.
     */
    public void setSolarRadiationMedian(double median)
    {
        this.solarRadiationMedian=median;
    }
   
    /**
     * Method to set minimum daily precipitation.
     * @param minimum New value for hourlyPrecipitationMin.
     */
    public void setHourlyPrecipitationMin(double minimum)
    {
        this.hourlyPrecipitationMin=minimum;
    }
     /**
     * Method to set maximum daily precipitation.
     * @param maximum New value for hourlyPrecipitationMax.
     */
    public void setHourlyPrecipitationMax(double maximum)
    {
        this.hourlyPrecipitationMax=maximum;
    }
     /**
     * Method to set median daily precipitation.
     * @param median New value for hourlyPrecipitationMedian.
     */
    public void setHourlyPrecipitationMedian(double median)
    {
        this.hourlyPrecipitationMedian=median;
    }
     /**
     * Method to set minimum wind gust.
     * @param minimum New value for wind gust.
     */
    public void setWindgustMin(double minimum)
    {
        this.windGustMin=minimum;
    }
     /**
     * Method to set maximum wind gust.
     * @param maximum New value for wind gust.
     */
    public void setWindgustMax(double maximum)
    {
        this.windGustMax=maximum;
    }
    /**
     * Method to set median wind gust.
     * @param median New value for wind gust.
     */
    public void setWindgustMedian(double median)
    {
        this.windGustMedian=median;
    }
   
  
    /**
     * Method to get the resource Number .
     * @return Number of the resource.
     */
    public int getResourceNumber()
    {
        return this.resourceNumber;
    }
       /**
     * Method to get the resource date.
     * @return Date of the resource.
     */
    public Calendar getDate()
    {
        return this.date;
    }
    /**
     * Method to get minimum temperature.
     * @return Value of temperatureMin.
     */
    public double getTemperatureMin()
    {
        return temperatureMin;
    }
    /**
     * Method to get maximum temperature.
     * @return Value of temperatureMax.
     */
    public double getTemperatureMax()
    {
        return temperatureMax;
    }
    /**
     * Method to get median temperature.
     * @return Value of temperatureMedian.
     */
    public double getTemperatureMedian()
    {
        return temperatureMedian;
    }
    /**
     * Method to get minimum pressure.
     * @return Value of pressureMin.
     */
    public double getPressureMin()
    {
        return pressureMin;
    }
    /**
     * Method to get maximum pressure.
     * @return Value of pressureMax.
     */
    public double getPressureMax()
    {
        return pressureMax;
    }
    /**
     * Method to get median pressure.
     * @return Value of pressureMedian.
     */
    public double getPressureMedian()
    {
        return pressureMedian;
    }
    /**
     * Method to get dew point.
     * @return Value of dewpointMin.
     */
    public double getDewPointMin()
    {
        return dewpointMin;
    }
    /**
     * Method to get dew point.
     * @return Value of dewpointMax.
     */
    public double getDewPointMax()
    {
        return dewpointMax;
    }
    /**
     * Method to get dew point.
     * @return Value of dewpointMedian.
     */
    public double getDewPointMedian()
    {
        return dewpointMedian;
    }
     /**
     * Method to get relative humidity.
     * @return Value of relativeHumidityMin.
     */
    public double getRelativeHumidityMin()
    {
        return relativeHumidityMin;
    }
    /**
     * Method to get relative humidity.
     * @return Value of relativeHumidityMax.
     */
    public double getRelativeHumidityMax()
    {
        return relativeHumidityMax;
    }
    /**
     * Method to get relative humidity.
     * @return Value of relativeHumidityMedian.
     */
    public double getRelativeHumidityMedian()
    {
        return relativeHumidityMedian;
    }
    /**
     * Method to get wind speed.
     * @return Value of windSpeedMin.
     */
    public double getWindSpeedMin()
    {
        return windSpeedMin;
    }
    /**
     * Method to get wind speed.
     * @return Value of windSpeedMax.
     */
    public double getWindSpeedMax()
    {
        return windSpeedMax;
    }
    /**
     * Method to get wind speed.
     * @return Value of windSpeedMedian.
     */
    public double getWindSpeedMedian()
    {
        return windSpeedMedian;
    }
   
     /**
     * Method to get solar radiation.
     * @return Value of solarRadiationMin.
     */
    public double getSolarRadiationMin()
    {
        return solarRadiationMin;
    }
    /**
     * Method to get solar radiation.
     * @return Value of solarRadiationMax.
     */
    public double getSolarRadiationMax()
    {
        return solarRadiationMax;
    }
    /**
     * Method to get solar radiation.
     * @return Value of solarRadiationMedian.
     */
    public double getSolarRadiationMedian()
    {
        return solarRadiationMedian;
    }
    
    /**
     * Method to get daily precipitation.
     * @return Value of hourlyPrecipitationMin.
     */
    public double getHourlyPrecipitationMin()
    {
        return hourlyPrecipitationMin;
    }
    /**
     * Method to get daily precipitation.
     * @return Value of hourlyPrecipitationMax.
     */
    public double getHourlyPrecipitationMax()
    {
        return hourlyPrecipitationMax;
    }
    /**
     * Method to get daily precipitation.
     * @return Value of hourlyPrecipitationMedian.
     */
    public double getHourlyPrecipitationMedian()
    {
        return hourlyPrecipitationMedian;
    }
   /**
     * Method to get wind gust minimum.
     * @return Value of wind gust minimum.
     */
    public double getWindgustMin()
    {
        return windGustMin;
    }
     /**
     * Method to get wind gust maximum.
     * @return Value of wind gust maximum.
     */
    public double getWindgustMax()
    {
        return this.windGustMax;
    }
   /**
     * Method to get wind gust median.
     * @return Value of wind gust median.
     */
    public double getWindgustMedian()
    {
        return this.windGustMedian;
    }
  
   
}
