package name.avioli.unilinks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.EventChannel.EventSink;
import io.flutter.plugin.common.EventChannel.StreamHandler;

public class UniLinksStreamHandler implements StreamHandler {
    static class ChangeReceiver extends BroadcastReceiver {
        final EventSink events;

        ChangeReceiver(EventSink events) {
            this.events = events;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // NOTE: assuming intent.getAction() is Intent.ACTION_VIEW

            // Log.v("uni_links", String.format("received action: %s", intent.getAction()));

            String dataString = intent.getDataString();

            if (dataString == null) {
                events.error("UNAVAILABLE", "Link unavailable", null);
            } else {
                events.success(dataString);
            }
        }
    }


    public BroadcastReceiver changeReceiver;

    @Override
    public void onListen(Object o, EventChannel.EventSink eventSink) {
        changeReceiver = new ChangeReceiver(eventSink);
    }

    @Override
    public void onCancel(Object o) {
        changeReceiver = null;
    }
}
