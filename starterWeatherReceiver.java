package layout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by David on 4/29/2017.
 */

public final class starterWeatherReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        weatherAlarmReceiver.setupAlarm(context);
    }
}
