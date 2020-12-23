package name.avioli.unilinks;

import androidx.annotation.NonNull;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;

public class MethodCallHandlerImpl implements MethodCallHandler  {
    private String initialLink;
    private String latestLink;
    private boolean initialIntent = true;

    public void receivedLink(String dataString) {
        if (initialIntent) {
            initialLink = dataString;
            initialIntent = false;
        }
        latestLink = dataString;
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        if (call.method.equals("getInitialLink")) {
            result.success(initialLink);
        } else if (call.method.equals("getLatestLink")) {
            result.success(latestLink);
        } else {
            result.notImplemented();
        }
    }
}
