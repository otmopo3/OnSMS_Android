package prm.droidsoft.onsms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

//import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {

	private static final String TAG = "SMSReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v(TAG, "onReceive");
		// TODO Auto-generated method stub
		Bundle bundle = intent.getExtras();
		SmsMessage[] msgs = null;
		String str = "";
		if (bundle != null) {
			Object[] pdus = (Object[]) bundle.get("pdus");

			msgs = new SmsMessage[pdus.length];
			String smsText = "";
			String smsFrom = "";
			for (int i = 0; i < msgs.length; i++) {
				msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				str += "\nSMS from " + msgs[i].getOriginatingAddress();
				str += "\nText:\n";
				str += msgs[i].getMessageBody().toString();
				// msgs[i].
				str += " \n";
				Log.v(TAG, "got sms " + str);
				if (smsFrom.equalsIgnoreCase(msgs[i].getOriginatingAddress())) {
					smsText += msgs[i].getMessageBody().toString();

				} else {

					smsText = msgs[i].getMessageBody().toString();
				}
				smsFrom = msgs[i].getOriginatingAddress();

			}					
			Log.v(TAG, "Saving sms to local storage");
			SmsSaver saver=new SmsSaver(context);
			saver.put(smsFrom,  smsText);
			Log.v(TAG, "Posting all sms to GAE");
			saver.postAll();
		}

	}

}
