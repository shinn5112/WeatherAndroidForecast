package WeatherAPI;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * DailyDataPoint class
 *
 * @author Patrick Shinn and Brandon Duke
 * @version 4/20/17
 */

public class DailyDataPoint extends DataPoint {
    private JSONObject data;

    public DailyDataPoint(JSONObject data){
        super(data);
        this.data = data;
    }

    public String getSunriseTime() throws JSONException{
        return data.getString("sunriseTime");
    }

    public String getSunsetTime() throws JSONException{
        return data.getString("sunsetTime");
    }

    public double getMoonPhase() throws JSONException{
        return data.getDouble("moonPhase");
    }

    public double getPrecipIntensityMax() throws JSONException{
        return data.getDouble("precipIntensityMax");
    }

    public String getPrecipIntensityMaxTime() throws JSONException{
        return data.getString("precipIntensityMaxTime");
    }

    public double getTemperatureMin() throws JSONException{
        return data.getDouble("temperatureMin");
    }

    public String getTemperatureMinTime() throws JSONException{
        return data.getString("temperatureMinTime");
    }

    public double getTemperatureMax() throws JSONException{
        return data.getDouble("temperatureMax");
    }

    public String getTemperatureMaxTime() throws JSONException{
        return data.getString("temperatureMaxTime");
    }

    public double getApparentTemperatureMin() throws JSONException{
        return data.getDouble("apparentTemperatureMin");
    }

    public String getApparentTemperatureMinTime() throws JSONException{
        return data.getString("apparentTemperatureMinTime");
    }

    public double getApparentTemperatureMax() throws JSONException{
        return data.getDouble("apparentTemperatureMax");
    }

    public String getApparentTemperatureMaxTime() throws JSONException{
        return data.getString("apparentTemperatureMinTime");
    }

    /* These methods are in a data point, however each dat does not contain these
        values so they are depreciated from the super class so that they are not used
     */
    @Deprecated
    @Override
    public double getApparentTemperature() throws JSONException {
        return 0.0;
    }

    @Deprecated
    @Override
    public int getNearestStormDistance() throws JSONException{
        return 0;
    }

    @Deprecated
    @Override
    public double getPrecipIntensityError() throws  JSONException{
        return 0.0;
    }

    @Deprecated
    @Override
    public double getTemperature() throws JSONException{
        return 0.0;
    }

}
