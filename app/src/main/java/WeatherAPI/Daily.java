package WeatherAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Daily class
 *
 * @author Patrick Shinn and Brandon Duke
 * @version 4/20/17
 */

public class Daily {
    private JSONObject data;

    public Daily(JSONObject data){
        this.data = data;
    }

    public DailyDataPoint getDay(int day) throws JSONException {
        JSONArray jsonArray = data.getJSONArray("data");

        return new DailyDataPoint(jsonArray.getJSONObject(day));
    }

    public String getSummary() throws JSONException{
        return data.getString("summary");
    }

    public String getIcon() throws JSONException{
        return data.getString("icon");
    }
}
