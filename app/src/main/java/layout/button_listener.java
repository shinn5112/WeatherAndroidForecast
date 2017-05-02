package layout;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by David on 5/1/2017.
 */

public class button_listener extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent thisIntent = weatherService.createIntentStartNotificationService(context);
        context.startService(thisIntent);

    }
}
