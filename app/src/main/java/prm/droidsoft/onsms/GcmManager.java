package prm.droidsoft.onsms;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Aleksey_2 on 11.05.2014.
 */
public class GcmManager {
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "929300537311";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "GCMDemo";

    GoogleCloudMessaging _gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences _prefs;
    Context _context;
    String regid;

    public GcmManager(Context context, SharedPreferences prefs) {
        _context = context;
        _prefs = prefs;

    }

    public boolean TryRegister()
    {
        _gcm = GoogleCloudMessaging.getInstance(_context);
        regid = getRegistrationId(_context);

        if (regid.isEmpty()) {
            registerInBackground();
        }

        Log.d("RegID", regid);

        return true;
    }


    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {

        String registrationId = _prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = _prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask() {
            @Override
            protected String doInBackground(Object[] objects) {
                String msg = "";
                try {
                    if (_gcm == null) {
                        _gcm = GoogleCloudMessaging.getInstance(_context);
                    }
                    regid = _gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(_context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }


                return msg;
            }

            /**
             * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
             * or CCS to send messages to your app. Not needed for this demo since the
             * device sends upstream messages to a server that echoes back the message
             * using the 'from' address in the message.
             */
            private void sendRegistrationIdToBackend() {
                // Your implementation here.
            }

            /**
             * Stores the registration ID and app versionCode in the application's
             * {@code SharedPreferences}.
             *
             * @param context application's context.
             * @param regId registration ID
             */
            private void storeRegistrationId(Context context, String regId) {
                ///final SharedPreferences prefs = getGCMPreferences(context);
                int appVersion = getAppVersion(context);
                Log.i(TAG, "Saving regId on app version " + appVersion);
                SharedPreferences.Editor editor = _prefs.edit();
                editor.putString(PROPERTY_REG_ID, regId);
                editor.putInt(PROPERTY_APP_VERSION, appVersion);
                editor.commit();
            }


        }.execute(null, null, null);
    }

    public void sendTestMessageAsync()
    {
        new AsyncTask() {
            @Override
            protected String doInBackground(Object[] objects) {
                String msg = "";
                try {
                    Bundle data = new Bundle();
                    data.putString("my_message", "Hello World");
                    data.putString("my_action",
                            "com.google.android.gcm.demo.app.ECHO_NOW");
                    String id = Integer.toString(msgId.incrementAndGet());
                    _gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);
                    msg = "Sent message";
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }



        }.execute(null, null, null);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(_context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity)_context,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");

            }
            return false;
        }
        return true;
    }

}