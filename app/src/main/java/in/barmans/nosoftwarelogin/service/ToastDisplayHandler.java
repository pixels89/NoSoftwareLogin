package in.barmans.nosoftwarelogin.service;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

/**
 * Created by mbarman on 10/20/17.
 */

public class ToastDisplayHandler {

    private static Handler messageHandler = null;
    private static ToastDisplayHandler instance = null; //TODO: not thread safe


    public static ToastDisplayHandler initialize(final Activity activity) {
        messageHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                CharSequence text = ((Bundle) message.obj).getCharSequence("message");
                Toast toast = Toast.makeText(activity.getApplicationContext(), text, Toast.LENGTH_SHORT);
                toast.show();
            }
        };
        instance = new ToastDisplayHandler();
        return getInstance();
    }

    public static ToastDisplayHandler getInstance() {
        if (instance == null)
            throw new RuntimeException("ToastDisplay should be initialized from MainActivity");
        return instance;
    }

    public void showMessage(String messageString) {
        Bundle bundle = new Bundle();
        bundle.putCharSequence("message", messageString);
        Message message = messageHandler.obtainMessage(1, bundle);
        message.sendToTarget();
    }
}
