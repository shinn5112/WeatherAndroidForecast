package WeatherAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * DailyDataPoint class
 *
 * @author Brandon Duke
 * @version 4/20/17
 */

public class Hourly {
    private JSONObject data;

    public Hourly(JSONObject data){
        this.data = data;
    }

    public DataPoint getHour(int hour) throws JSONException{
        JSONArray jsonArray = data.getJSONArray("data");

        return new DataPoint(jsonArray.getJSONObject(hour));
    }
}
