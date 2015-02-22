package prm.droidsoft.onsms;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Aleksey_2 on 09.05.2014.
 */
public class TestActivity extends Activity {

    protected static final String TAG = "TestActivity";

    Button _postSms;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);

        _postSms = (Button) findViewById(R.id.button1);
        _postSms.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v(TAG, "onClick ");
                //MessagePoster.PostSMS(getBaseContext(), "900", "test","","");

                String smsText = ((EditText) findViewById(R.id.smsTextToSendEdit)).getText().toString();
                SmsSaver saver = new SmsSaver(getBaseContext());
                saver.put("900", smsText);
                saver.postAll();
            }
        });
    }
}
