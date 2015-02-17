package org.chromium.android_webview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.WebAddress;
import android.os.AsyncTask;
import android.view.ViewGroup;

import com.mediatek.xlog.Xlog;

import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;

@JNINamespace("android_webview")
public class AwPasswordHandler {

    private static final String LOGTAG = "AwPasswordHandler";

    private static AwPasswordHandler mInstance = null;
    private static final String SCHEME_HOST_DELIMITER = "://";
    private static AlertDialog mSavePasswordDialog = null;
    private static boolean mEnableSave = false;
    private static Context mContext = null;

    private ViewGroup mView = null;
    private AwBrowserContext mBrowserContext = null;

    public static AwPasswordHandler create(ViewGroup view, AwBrowserContext browserContext) {
        if (mContext != null && mContext != view.getContext()) {
            Xlog.d(LOGTAG, "mContext = " + mContext + ", context = " + view.getContext());
            mInstance = null;
            if (mSavePasswordDialog != null) {
                mSavePasswordDialog.dismiss();
                mSavePasswordDialog = null;
            }
        }
        if (mInstance == null) {
            mInstance = new AwPasswordHandler(view, browserContext);
            mContext = view.getContext();
        }
        return mInstance;
    }

    @CalledByNative
    public static AwPasswordHandler getInstance() {
        Xlog.d(LOGTAG, "getInstance, mInstance = " + mInstance);
        return mInstance;
    }

    private AwPasswordHandler(ViewGroup view, AwBrowserContext browserContext) {
        mView = view;
        mBrowserContext = browserContext;
    }
    
    public void setViewInfo(ViewGroup view) {
        Xlog.d(LOGTAG, "setViewInfo, mView = " + view);
        mView = view;
    }

    @CalledByNative
    private void reqeustPassword(
                    final int nativePointer, final String[] data, final String[] data16) {
        final PasswordDatabase db = 
            mBrowserContext.getPasswordDatabase(mContext);
        WebAddress uri = new WebAddress(data[PasswordDatabase.FIELD_SIGNON_REALM]);
        final String newScheme = uri.getScheme() + SCHEME_HOST_DELIMITER + uri.getHost();
        Xlog.d(LOGTAG, "reqeustPassword, newScheme = " + newScheme);
        Xlog.d(LOGTAG, "reqeustPassword, data16 = " 
                + data16[0] + ", " + data16[1] + ", " + data16[2] + ", " + data16[3]);
        new AsyncTask<String, Void, PasswordDatabase.RequestRetData>() {
            @Override
            protected PasswordDatabase.RequestRetData doInBackground(String... params) {
                Xlog.d(LOGTAG, "reqeustPassword doInBackground " + newScheme);
                return db.getUsernamePassword(newScheme);
            }

            @Override
            protected void onPostExecute(PasswordDatabase.RequestRetData result) {
                if (result != null) {
                    String[] ret_data16 = new String[4];
                    String[] ret_data = new String[4];

                    if (!result.signonRealm.isEmpty()) {
                        ret_data[PasswordDatabase.FIELD_SIGNON_REALM] = result.signonRealm;
                    } else {
                        ret_data[PasswordDatabase.FIELD_SIGNON_REALM] = data[PasswordDatabase.FIELD_SIGNON_REALM];
                    }
                    if (!result.originSignonRealm.isEmpty()) {
                        ret_data[PasswordDatabase.FIELD_ORIGIN_SIGNON_REALM] = result.originSignonRealm;
                    } else {
                        ret_data[PasswordDatabase.FIELD_ORIGIN_SIGNON_REALM] = data[PasswordDatabase.FIELD_ORIGIN_SIGNON_REALM];
                    }
                    if (!result.originSpec.isEmpty()) {
                        ret_data[PasswordDatabase.FIELD_ORIGIN_SPEC] = result.originSpec;
                    } else {
                        ret_data[PasswordDatabase.FIELD_ORIGIN_SPEC] = data[PasswordDatabase.FIELD_ORIGIN_SPEC];
                    }
                    if (!result.actionSpec.isEmpty()) {
                        ret_data[PasswordDatabase.FIELD_ACTION_SPEC] = result.actionSpec;
                    } else {
                        ret_data[PasswordDatabase.FIELD_ACTION_SPEC] = data[PasswordDatabase.FIELD_ACTION_SPEC];
                    }
                    Xlog.d(LOGTAG, "reqeustPassword onPostExecute, ret_data = " 
                            + ret_data[0] + ", " + ret_data[1]);
                    Xlog.d(LOGTAG, "reqeustPassword onPostExecute, ret_data = " 
                            + ret_data[2] + ", " + ret_data[3]);
                    
                    if (!result.usernameName.isEmpty()) {
                        ret_data16[PasswordDatabase.FIELD_USERNAME_NAME] = result.usernameName;
                    } else {
                        ret_data16[PasswordDatabase.FIELD_USERNAME_NAME] = data16[PasswordDatabase.FIELD_USERNAME_NAME];
                    }
                    if (!result.usernameValue.isEmpty()) {
                        ret_data16[PasswordDatabase.FIELD_USERNAME_VALUE] = result.usernameValue;
                    } else {
                        ret_data16[PasswordDatabase.FIELD_USERNAME_VALUE] = data16[PasswordDatabase.FIELD_USERNAME_VALUE];
                    }
                    if (!result.passwordName.isEmpty()) {
                        ret_data16[PasswordDatabase.FIELD_PASSWORD_NAME] = result.passwordName;
                    } else {
                        ret_data16[PasswordDatabase.FIELD_PASSWORD_NAME] = data16[PasswordDatabase.FIELD_PASSWORD_NAME];
                    }
                    ret_data16[PasswordDatabase.FIELD_PASSWORD_VALUE] = result.passwordValue;
                    Xlog.d(LOGTAG, "reqeustPassword onPostExecute, ret_data16 = " 
                            + ret_data16[0] + ", " + ret_data16[1] + ", " + ret_data16[2] + ", " + ret_data16[3]);
                    nativeOnRequestDone(nativePointer, ret_data, ret_data16);
                }
            }
        }.execute();
    }

    private void clickHandler(final String[] data, final String[] data16, final boolean remember) {
        final PasswordDatabase db = 
            mBrowserContext.getPasswordDatabase(mContext);
        WebAddress uri = new WebAddress(data[PasswordDatabase.FIELD_SIGNON_REALM]);
        final String newScheme = uri.getScheme() + SCHEME_HOST_DELIMITER + uri.getHost();
        new AsyncTask<Object, Void, Void>() {
            @Override
            protected Void doInBackground(Object... params) {
                Xlog.d(LOGTAG, "clickHandler doInBackground " + newScheme + ", " + remember);
                db.setUsernamePassword(newScheme, data, data16, remember);
                return null;
            }
        }.execute();
    }

    @CalledByNative
    private void showPasswordDialog(final String[] data, final String[] data16, boolean permitted) {
        WebAddress uri = new WebAddress(data[PasswordDatabase.FIELD_SIGNON_REALM]);
        String newScheme = uri.getScheme() + SCHEME_HOST_DELIMITER + uri.getHost();
        Xlog.d(LOGTAG, "showPasswordDialog scheme " + newScheme + ", " + permitted);
        if (!permitted) {
            clickHandler(data, data16, true);
            return;
        }
        Xlog.d(LOGTAG, "showPasswordDialog dialog " + mSavePasswordDialog);
        if (mSavePasswordDialog != null) {
            mSavePasswordDialog.dismiss();
            mSavePasswordDialog = null;
        }
        mSavePasswordDialog = new AlertDialog.Builder(mView.getContext())
                .setTitle(org.chromium.content.R.string.save_password_label)
                .setMessage(org.chromium.content.R.string.save_password_message)
                .setPositiveButton(org.chromium.content.R.string.save_password_notnow,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // not now
                        mSavePasswordDialog = null;
                    }
                })
                .setNeutralButton(org.chromium.content.R.string.save_password_remember,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // remember
                        clickHandler(data, data16, true);
                        mSavePasswordDialog = null;
                    }
                })
                .setNegativeButton(org.chromium.content.R.string.save_password_never,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // never
                        clickHandler(data, data16, false);
                        mSavePasswordDialog = null;
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        // dismiss
                        mSavePasswordDialog = null;
                    }
                }).show();
    }

    public static void onPauseDismiss() {
        Xlog.d(LOGTAG, "onPauseDismiss dialog " + mSavePasswordDialog);
        if (mSavePasswordDialog != null) {
            mSavePasswordDialog.dismiss();
            mSavePasswordDialog = null;
        }
    }

    public static void setSavePassword(boolean save) {
        Xlog.d(LOGTAG, "setSavePassword " + save);
        mEnableSave = save;
    }

    @CalledByNative
    private boolean getSavePassword() {
        Xlog.d(LOGTAG, "getSavePassword " + mEnableSave);
        return mEnableSave;
    }

    private native void nativeOnRequestDone(
        int nativeAwPasswordHandlerDelegateImpl, String[] data, String[] data16);
}
