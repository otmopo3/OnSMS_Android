package prm.droidsoft.onsms;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.appspot.otmopo3smssave.smssync.model.SmsApiDatamodelSmsCollection;

public class SmsSaver extends android.database.sqlite.SQLiteOpenHelper {

	private static final String TAG = "SmsSaver";
	private static final int DATABASE_VERSION = 2;
	private static final String SMS_TABLE_NAME = "smsstorage";
	private static final String KEY_ID = "_id";
	private static final String SMS_LAT = "SMS_LAT";
	private static final String SMS_LNG = "SMS_LNG";
	private static final String SMS_TEXT = "SMS_TEXT";
	private static final String SMS_FROM = "SMS_FROM";
	private static final String SMS_TABLE_CREATE = "CREATE TABLE "

	+ SMS_TABLE_NAME + " (" + KEY_ID + " integer primary key autoincrement, "
			+ SMS_FROM + " TEXT, " + SMS_TEXT + " TEXT, " + SMS_LAT + " TEXT, "
			+ SMS_LNG + " TEXT);";

	Context context;

	private static Lock lock = new ReentrantLock();

	public SmsSaver(Context context) {
		super(context, SMS_TABLE_NAME, null, DATABASE_VERSION);

		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SMS_TABLE_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public void put(String smsFrom, String smsText) {
		Location loc = MyLocationManager.getLastKnownLocation(context);
		SQLiteDatabase db = null;
		lock.lock();
		try {

			db = getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(SMS_FROM, smsFrom);
			values.put(SMS_TEXT, smsText);
			values.put(SMS_LAT, Double.toString(loc.getLatitude()));
			values.put(SMS_LNG, Double.toString(loc.getLongitude()));
			db.insert(SMS_TABLE_NAME, null, values);

		} catch (Exception e) {
			Log.e(TAG, "Excetion in put:\n" + e);
		} finally {
			if (db != null)
				db.close();
			lock.unlock();
		}
	}

	private String getEMail() {
		// Add your data
		AccountManager accountManager = AccountManager.get(context);
		Account[] accounts = accountManager.getAccountsByType("com.google");

		String email = "unknown";
		if (accounts != null && accounts.length > 0)
			email = accounts[0].name;
		return email;
	}


    public void postAll() {
        new PostSmsAsyncTask(context).execute();
    }


    private class PostSmsAsyncTask extends AsyncTask<Void, Void, Void> {
        Context context;

        public PostSmsAsyncTask(Context context) {
            this.context = context;
        }

        protected Void doInBackground(Void... unused) {
            postAllAsync();

            return null;
        }

        private void postAllAsync() {
            SQLiteDatabase db = null;
            lock.lock();
            int c = 0;
            try {
                String email = getEMail();
                db = getReadableDatabase();
                Cursor cursor = db.rawQuery("select * from " + SMS_TABLE_NAME,
                        new String[] {});
                Log.v(TAG, "Got " + cursor.getCount() + " sms to post ");
                while (cursor.moveToNext()) {
                    PostNextMessage(db, email, cursor);
                    c++;
                }

            } catch (Exception e) {
                Log.e(TAG, "Excetion in postAll:\n" + e);
            } finally {
                if (db != null)
                    db.close();
                lock.unlock();
            }
            Log.v(TAG, "Totaly posted " + c + " messages ");
        }

        private void PostNextMessage(SQLiteDatabase db, String email, Cursor cursor) {
            String smsFrom = cursor.getString(cursor
                    .getColumnIndex(SMS_FROM));
            String smsText = cursor.getString(cursor
                    .getColumnIndex(SMS_TEXT));
            String lat = cursor.getString(cursor.getColumnIndex(SMS_LAT));
            String lng = cursor.getString(cursor.getColumnIndex(SMS_LNG));
            int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
            String deviceName = Build.MODEL;
            if (MessagePoster.PostSMS(context, smsFrom, smsText, lat, lng,
                    email, deviceName)) {
                Log.v(TAG, "Posted sms successfully deleting now ");
                db.delete(SMS_TABLE_NAME, KEY_ID + "=" + id, null);
            } else {
                Log.v(TAG, "SMS was not posted, leaving in queue ");
            }
        }
    }

}
