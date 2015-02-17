// Copyright (c) 2012 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.media;

import android.content.Context;
/// M: for keep background light on
import android.content.pm.PackageManager;
import android.Manifest.permission;
import android.media.MediaPlayer;
import android.net.Uri;
/// M: for keep background light on
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;

import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;

/// M: Add mediatek xlog import
import com.mediatek.xlog.Xlog;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

// A wrapper around android.media.MediaPlayer that allows the native code to use it.
// See media/base/android/media_player_bridge.cc for the corresponding native code.
@JNINamespace("media")
public class MediaPlayerBridge {

    private static final String TAG = "MediaPlayerBridge";

    // Local player to forward this to. We don't initialize it here since the subclass might not
    // want it.
    private MediaPlayer mPlayer;

    @CalledByNative
    private static MediaPlayerBridge create() {
        Xlog.d(TAG, "create()");
        return new MediaPlayerBridge();
    }

    protected MediaPlayer getLocalPlayer() {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        }
        Xlog.d(TAG, "getLocalPlayer() = " + mPlayer);
        return mPlayer;
    }

    @CalledByNative
    protected void setSurface(Surface surface) {
        Xlog.d(TAG, "setSurface( " + surface + " )");
        getLocalPlayer().setSurface(surface);
    }

    @CalledByNative
    protected void prepareAsync() throws IllegalStateException {
        Xlog.d(TAG, "prepareAsync()");
        getLocalPlayer().prepareAsync();
    }

    @CalledByNative
    protected boolean isPlaying() {
        boolean ret = getLocalPlayer().isPlaying();
        Xlog.d(TAG, "isPlaying() = " + ret);
        return ret;
    }

    @CalledByNative
    protected int getVideoWidth() {
        int ret = getLocalPlayer().getVideoWidth();
        Xlog.d(TAG, "getVideoWidth() = " + ret);
        return ret;
    }

    @CalledByNative
    protected int getVideoHeight() {
        int ret = getLocalPlayer().getVideoHeight();
        Xlog.d(TAG, "getVideoHeight() = " + ret);
        return ret;
    }

    @CalledByNative
    protected int getCurrentPosition() {
        int ret = getLocalPlayer().getCurrentPosition();
        Xlog.d(TAG, "getCurrentPosition() = " + ret);
        return ret;
    }

    @CalledByNative
    protected int getDuration() {
        int ret = getLocalPlayer().getDuration();
        Xlog.d(TAG, "getDuration() = " + ret);
        return ret;
    }

    @CalledByNative
    protected void release() {
        Xlog.d(TAG, "release()");
        getLocalPlayer().release();
    }

    @CalledByNative
    protected void setVolume(double volume) {
        Xlog.d(TAG, "getLocalPlayer( " + volume + " )");
        getLocalPlayer().setVolume((float) volume, (float) volume);
    }

    @CalledByNative
    protected void start() {
        Xlog.d(TAG, "start()");
        getLocalPlayer().start();
        /// M: for Media button intent handler
        MediaButtonIntentHandler handler = MediaButtonIntentHandler.getInstance();
        if (handler != null) {
            handler.registeHandler(null);
        }
    }

    @CalledByNative
    protected void pause() {
        Xlog.d(TAG, "pause()");
        getLocalPlayer().pause();
        /// M: for Media button intent handler
        MediaButtonIntentHandler handler = MediaButtonIntentHandler.getInstance();
        if (handler != null) {
            handler.unRegisteHandler(null);
        }
    }

    @CalledByNative
    protected void seekTo(int msec) throws IllegalStateException {
        Xlog.d(TAG, "seekTo( " + msec + " )");
        getLocalPlayer().seekTo(msec);
    }

    @CalledByNative
    protected boolean setDataSource(
            Context context, String url, String cookies, boolean hideUrlLog) {
        Uri uri = Uri.parse(url);
        Xlog.d(TAG, "setDataSource( " + url + " )");
        Xlog.d(TAG, "setDataSource( " + cookies + " )");
        Xlog.d(TAG, "setDataSource( " + hideUrlLog + " )");
        HashMap<String, String> headersMap = new HashMap<String, String>();
        if (hideUrlLog)
            headersMap.put("x-hide-urls-from-log", "true");
        if (!TextUtils.isEmpty(cookies))
            headersMap.put("Cookie", cookies);
        try {
            getLocalPlayer().setDataSource(context, uri, headersMap);
            /// M: to keep the background light on while playing and getting data
            if (context.checkCallingOrSelfPermission(permission.WAKE_LOCK)
                    == PackageManager.PERMISSION_GRANTED) {
                Xlog.d(TAG, "setWakeMode()");
                getLocalPlayer().setWakeMode(context, PowerManager.SCREEN_BRIGHT_WAKE_LOCK);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected void setOnBufferingUpdateListener(MediaPlayer.OnBufferingUpdateListener listener) {
        Xlog.d(TAG, "setOnBufferingUpdateListener( " + listener + " )");
        getLocalPlayer().setOnBufferingUpdateListener(listener);
    }

    protected void setOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
        Xlog.d(TAG, "setOnCompletionListener( " + listener + " )");
        getLocalPlayer().setOnCompletionListener(listener);
    }

    protected void setOnErrorListener(MediaPlayer.OnErrorListener listener) {
        Xlog.d(TAG, "setOnErrorListener( " + listener + " )");
        getLocalPlayer().setOnErrorListener(listener);
    }

    protected void setOnPreparedListener(MediaPlayer.OnPreparedListener listener) {
        Xlog.d(TAG, "setOnPreparedListener( " + listener + " )");
        getLocalPlayer().setOnPreparedListener(listener);
    }

    protected void setOnSeekCompleteListener(MediaPlayer.OnSeekCompleteListener listener) {
        Xlog.d(TAG, "setOnSeekCompleteListener( " + listener + " )");
        getLocalPlayer().setOnSeekCompleteListener(listener);
    }

    protected void setOnVideoSizeChangedListener(MediaPlayer.OnVideoSizeChangedListener listener) {
        Xlog.d(TAG, "setOnVideoSizeChangedListener( " + listener + " )");
        getLocalPlayer().setOnVideoSizeChangedListener(listener);
    }

    /// M: for toast when audio/video is not supported.
    protected void setOnInfoListener(MediaPlayer.OnInfoListener listener) {
        Xlog.d(TAG, "setOnInfoListener( " + listener + " )");
        getLocalPlayer().setOnInfoListener(listener);
    }

    private static class AllowedOperations {
        private final boolean mCanPause;
        private final boolean mCanSeekForward;
        private final boolean mCanSeekBackward;

        private AllowedOperations(boolean canPause, boolean canSeekForward,
                boolean canSeekBackward) {
            Xlog.d(TAG, "AllowedOperations( " + canPause + ", " + canSeekForward + ", " + canSeekBackward + " )");
            mCanPause = canPause;
            mCanSeekForward = canSeekForward;
            mCanSeekBackward = canSeekBackward;
        }

        @CalledByNative("AllowedOperations")
        private boolean canPause() { return mCanPause; }

        @CalledByNative("AllowedOperations")
        private boolean canSeekForward() { return mCanSeekForward; }

        @CalledByNative("AllowedOperations")
        private boolean canSeekBackward() { return mCanSeekBackward; }
    }

    /**
     * Returns an AllowedOperations object to show all the operations that are
     * allowed on the media player.
     */
    @CalledByNative
    private static AllowedOperations getAllowedOperations(MediaPlayerBridge bridge) {
        MediaPlayer player = bridge.getLocalPlayer();
        boolean canPause = true;
        boolean canSeekForward = true;
        boolean canSeekBackward = true;
        Xlog.d(TAG, "getAllowedOperations( " + bridge + ", " + player + " )");
        try {
            Method getMetadata = player.getClass().getDeclaredMethod(
                    "getMetadata", boolean.class, boolean.class);
            getMetadata.setAccessible(true);
            Object data = getMetadata.invoke(player, false, false);
            if (data != null) {
                Class<?> metadataClass = data.getClass();
                Method hasMethod = metadataClass.getDeclaredMethod("has", int.class);
                Method getBooleanMethod = metadataClass.getDeclaredMethod("getBoolean", int.class);

                int pause = (Integer) metadataClass.getField("PAUSE_AVAILABLE").get(null);
                int seekForward =
                    (Integer) metadataClass.getField("SEEK_FORWARD_AVAILABLE").get(null);
                int seekBackward =
                        (Integer) metadataClass.getField("SEEK_BACKWARD_AVAILABLE").get(null);
                hasMethod.setAccessible(true);
                getBooleanMethod.setAccessible(true);
                canPause = !((Boolean) hasMethod.invoke(data, pause))
                        || ((Boolean) getBooleanMethod.invoke(data, pause));
                canSeekForward = !((Boolean) hasMethod.invoke(data, seekForward))
                        || ((Boolean) getBooleanMethod.invoke(data, seekForward));
                canSeekBackward = !((Boolean) hasMethod.invoke(data, seekBackward))
                        || ((Boolean) getBooleanMethod.invoke(data, seekBackward));
            }
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "Cannot find getMetadata() method: " + e);
        } catch (InvocationTargetException e) {
            Log.e(TAG, "Cannot invoke MediaPlayer.getMetadata() method: " + e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "Cannot access metadata: " + e);
        } catch (NoSuchFieldException e) {
            Log.e(TAG, "Cannot find matching fields in Metadata class: " + e);
        }
        return new AllowedOperations(canPause, canSeekForward, canSeekBackward);
    }
}
