package WeatherAPI;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * DataPoint class
 *
 * @author Patrick Shinn
 * @version 4/20/17
 */

public class DataPoint {
    private JSONObject data;

    public DataPoint(JSONObject data) {
        this.data = data;
    }

    public String getSummary() throws JSONException{
        return data.getString("summary");
    }

    public String getTime() throws JSONException{
        return data.getString("time");
    }

    public String getIcon() throws JSONException{
        return data.getString("icon");
    }

    public int getNearestStormDistance() throws JSONException{
        return data.getInt("nearestStormDistance");
    }

    public double getPrecipIntensity() throws JSONException{
        return data.getDouble("precipIntensity");
    }

    public double getPrecipIntensityError() throws  JSONException{
        return data.getDouble("precipIntensityError");
    }

    public int getPrecipProbabilty() throws  JSONException{
        return data.getInt("precipProbability");
    }

    public String getPrecipType() throws JSONException{
        String tempData;
        try{
            tempData = data.getString("precipType");
        }catch (Exception e){
            tempData = "";
        }
        return tempData;
    }

    public double getTemperature() throws JSONException{
        return data.getDouble("temperature");
    }

    public double getApparentTemperature() throws JSONException{
        return data.getDouble("apparentTemperature");
    }

    public double getDewPoint() throws JSONException{
        return data.getDouble("dewPoint");
    }

    public double getHumidity() throws JSONException{
        return data.getDouble("humidity");
    }

    public double getWindSpeed() throws JSONException{
        return data.getDouble("windSpeed");
    }

    public int getWindBearing() throws JSONException{
        return data.getInt("windBearing");
    }

    public double getVisibility() throws JSONException{
        return data.getDouble("visibility");
    }

    public double getCloudCover() throws JSONException{
        return data.getDouble("cloudCover");
    }

    public double getPressure() throws JSONException{
        return data.getDouble("pressure");
    }

    public double getOZONE() throws JSONException{
        return data.getDouble("ozone");
    }
}
