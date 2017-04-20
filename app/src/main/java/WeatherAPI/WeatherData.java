package WeatherAPI;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * WeatherData class
 *
 * @author Patrick Shinn and Brandon Duke
 * @version 4/20/17
 */

public class WeatherData {
    private JSONObject data;
    private String json;
    public WeatherData(String JSONString) throws JSONException{
        this.data = new JSONObject(JSONString);
        this.json = JSONString;
    }

    public DataPoint getCurrently()throws JSONException{
        return new DataPoint(data.getJSONObject("currently"));
    }

    public Hourly getHourly()throws JSONException{
        return new Hourly(data.getJSONObject("hourly"));
    }

    public Daily getDaily()throws JSONException{
        return new Daily(data.getJSONObject("daily"));
    }

    public String getTimeZone() throws JSONException{
        return data.getString("timezone");
    }

    public double getLatitude() throws JSONException{
        return data.getDouble("latitude");
    }

    public double getLongitude() throws JSONException{
        return data.getDouble("longitude");
    }

    public String getJson() {
        return json;
    }
}
