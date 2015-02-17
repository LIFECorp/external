package org.chromium.media;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.KeyEvent;

import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;

import com.mediatek.xlog.Xlog;

/**
 * M: for media button intents. To play/pause the media source.
 */
public class MediaButtonIntentHandler extends BroadcastReceiver {

    private static final String TAG = "MediaButtonIntentHandler";
    private ComponentName mMBIRec;
    private final Context mContext;
    private static MediaButtonIntentHandler mHandler;
    private boolean mRegisted;

    private MediaButtonIntentHandler(Context context) {
        Xlog.d(TAG, "Constructor: context: " + context);
        mContext = context;
    }

    public static MediaButtonIntentHandler getInstance() {
        Xlog.d(TAG, "getInstance: mHandler: " + mHandler);
        if (mHandler != null) {
            return mHandler;
        } else {
            Xlog.e(TAG, "Can not get instance: context is null");
            return null;
        }
    }

    public static MediaButtonIntentHandler getInstance(Context context) {
        Xlog.d(TAG, "getInstance: context: " + context + ", mHandler: " + mHandler);
        if (mHandler == null) {
            mHandler = new MediaButtonIntentHandler(context);
        }
        return mHandler;
    }

    public void registeHandler(AudioManager am) {
        Xlog.d(TAG, "registeHanler called, mRegisted: " + mRegisted);
        if (mRegisted) {
            return;
        }
        if (mMBIRec == null) {
            mMBIRec = new ComponentName(mContext.getPackageName(), MediaButtonIntentHandler.class.getName());
        }
        AudioManager manager = (am != null) ? am : 
            (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        manager.registerMediaButtonEventReceiver(mMBIRec);
        mRegisted = true;
    }

    public void unRegisteHandler(AudioManager am) {
        Xlog.d(TAG, "unRegisteHanler, mRegisted: " + mRegisted);
        if (!mRegisted) {
            return;
        }
        AudioManager manager = (am != null) ? am : 
            (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        manager.unregisterMediaButtonEventReceiver(mMBIRec);
        mRegisted = false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();
        Xlog.d(TAG, "onReceive intentAction: " + intentAction);
        if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
            KeyEvent event = (KeyEvent)
                    intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            Xlog.d(TAG, "onReceive event: " + event);
            if (event == null) {
                return;
            }

            int keycode = event.getKeyCode();
            int action = event.getAction();
            Xlog.d(TAG, "onReceive keycode: " + keycode + ", action: " + action);

            switch (keycode) {
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    if (action == KeyEvent.ACTION_UP) {
                        /**
                         * should pause the media file
                         */
                    }
                    break;

                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    if (action == KeyEvent.ACTION_UP) {
                        /**
                         * should play the media file
                         */
                    }
                    break;

                default:
                    break;
            }
        }
    }

}

