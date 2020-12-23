package name.avioli.unilinks;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;

public class UniLinksPlugin
        implements FlutterPlugin, ActivityAware,
                PluginRegistry.NewIntentListener {

    private static final String MESSAGES_CHANNEL = "uni_links/messages";
    private static final String EVENTS_CHANNEL = "uni_links/events";

    private Context context;
    private MethodChannel methodChannel;
    private EventChannel eventChannel;
    private UniLinksStreamHandler streamHandler;
    private MethodCallHandlerImpl methodCallHandler;

    @Override
    public void onAttachedToEngine(FlutterPluginBinding binding) {
        startListening(binding.getApplicationContext(), binding.getBinaryMessenger());
    }

    private void startListening(Context applicationContext, BinaryMessenger messenger) {
        context = applicationContext;

        methodChannel = new MethodChannel(messenger, MESSAGES_CHANNEL);
        methodCallHandler = new MethodCallHandlerImpl();
        methodChannel.setMethodCallHandler(methodCallHandler);

        eventChannel = new EventChannel(messenger, EVENTS_CHANNEL);
        streamHandler = new UniLinksStreamHandler();
        eventChannel.setStreamHandler(streamHandler);
    }

    /** Plugin registration. */
    public static void registerWith(PluginRegistry.Registrar registrar) {
        // Detect if we've been launched in background
        if (registrar.activity() == null) {
            return;
        }

        final UniLinksPlugin instance = new UniLinksPlugin();
        instance.startListening(registrar.context(), registrar.messenger());

        instance.handleIntent(registrar.context(), registrar.activity().getIntent());
        registrar.addNewIntentListener(instance);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        methodChannel.setMethodCallHandler(null);
        methodCallHandler = null;
        methodChannel = null;

        eventChannel.setStreamHandler(null);
        streamHandler.onCancel(null);
        eventChannel = null;
        streamHandler = null;

        context = null;
    }


    @Override
    public boolean onNewIntent(Intent intent) {
        handleIntent(context, intent);

        return false;
    }

    @Override
    public void onAttachedToActivity(ActivityPluginBinding binding) {
        binding.addOnNewIntentListener(this);

        handleIntent(context, binding.getActivity().getIntent());
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {}

    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
        binding.addOnNewIntentListener(this);

        handleIntent(context, binding.getActivity().getIntent());
    }

    @Override
    public void onDetachedFromActivity() {}

    private void handleIntent(Context context, Intent intent) {
        final String action = intent.getAction();
        final String dataString = intent.getDataString();

        methodCallHandler.receivedLink(dataString);

        if (Intent.ACTION_VIEW.equals(action)) {
            if (streamHandler.changeReceiver != null) {
                streamHandler.changeReceiver.onReceive(context, intent);
            }
        }
    }

}
