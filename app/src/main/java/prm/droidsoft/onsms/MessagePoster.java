package prm.droidsoft.onsms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.util.Log;

public class MessagePoster {

	private static final String TAG = "MessagePoster";

	public static boolean PostSMS(Context context, String smsFrom, String smsText,
			String lat, String lng, String email, String deviceName) {
		HttpClient httpclient = new DefaultHttpClient();
		
		HttpGet httpget = new HttpGet("http://otmopo3smssave.appspot.com/");
		try {

			// Execute HTTP Get Request
			HttpResponse response = httpclient.execute(httpget);
			Log.v(TAG, "Send get got response " + response.toString());

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}

		HttpPost httppost = new HttpPost("http://otmopo3smssave.appspot.com/");
		
		try {
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("from_number", smsFrom));
			nameValuePairs.add(new BasicNameValuePair("sms_text", smsText));
			nameValuePairs.add(new BasicNameValuePair("email", email));
			nameValuePairs.add(new BasicNameValuePair("loc_lat", lat));
			nameValuePairs.add(new BasicNameValuePair("loc_long", lng));
            nameValuePairs.add(new BasicNameValuePair("dev_name", deviceName));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			Log.v(TAG, "Send post " + nameValuePairs + " got response "
					+ response.getStatusLine() + response.getAllHeaders());
			return true;

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		return false;
	}
}
