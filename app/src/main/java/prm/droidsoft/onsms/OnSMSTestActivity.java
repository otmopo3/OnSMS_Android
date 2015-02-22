package prm.droidsoft.onsms;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import com.appspot.otmopo3smssave.smssync.*;

import com.appspot.otmopo3smssave.smssync.model.SmsApiDatamodelSmsCollection;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import java.io.IOException;


public class OnSMSTestActivity extends ListActivity implements SwipeRefreshLayout.OnRefreshListener {
    protected static final String TAG = "OnSMSTestActivity";

    static final int REQUEST_ACCOUNT_PICKER = 2;
    static final String WEB_CLIENT_ID = "173742313316-ue7cvcvjti6v40kk9205kc068v9jqh5h.apps.googleusercontent.com";
    String PREF_ACCOUNT_NAME = "PREF_ACCOUNT_NAME";

    Context _context;

    SwipeRefreshLayout _swipeLayout;

    SharedPreferences _settings;
    GoogleAccountCredential _credential;

    SmsListAdapter _adapter;

    GcmManager _gcmManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        _swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        _swipeLayout.setOnRefreshListener(this);
        _swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        _context = this;

        _settings = getSharedPreferences(
                "OnSms", 0);

        _credential = GoogleAccountCredential.usingAudience(this,
                "server:client_id:" +
                        WEB_CLIENT_ID
        );

        setSelectedAccountName(_settings.getString(PREF_ACCOUNT_NAME, null));

        _adapter = new SmsListAdapter(this);

        setListAdapter(_adapter);

        _gcmManager = new GcmManager(this, _settings);

        _gcmManager.TryRegister();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.open_test_activity:
                openTestActivity();
                return true;
            case R.id.update_smsList:
                updateSmsList();
                return true;
            case R.id.send_test_message:
                _gcmManager.sendTestMessageAsync();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openTestActivity() {
        Intent startTestActivityIntent = new Intent(this, TestActivity.class);

        startActivity(startTestActivityIntent);
    }

    private void updateSmsList() {
        Log.v(TAG, "onClick get sms");

        Smssync.Builder builder = new Smssync.Builder(
                AndroidHttp.newCompatibleTransport(), new GsonFactory(), _credential);

        Smssync service = builder.build();

        if (_credential.getSelectedAccountName() != null) {
            new GetSmsListAsyncTask(_context, service).execute();
        } else {
            chooseAccount();
        }
    }


    private void setSelectedAccountName(String accountName) {
        SharedPreferences.Editor editor = _settings.edit();
        editor.putString(PREF_ACCOUNT_NAME, accountName);

        editor.commit();
        _credential.setSelectedAccountName(accountName);
    }

    void chooseAccount() {
        startActivityForResult(_credential.newChooseAccountIntent(),
                REQUEST_ACCOUNT_PICKER);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ACCOUNT_PICKER:
                if (data != null && data.getExtras() != null) {
                    String accountName =
                            data.getExtras().getString(
                                    AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        setSelectedAccountName(accountName);
                        SharedPreferences.Editor editor = _settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.commit();
                        // User is authorized.
                    }
                }
                break;
        }
    }

    @Override
    public void onRefresh() {
        updateSmsList();
    }

    private class GetSmsListAsyncTask extends AsyncTask<Void, Void, SmsApiDatamodelSmsCollection> {
        Context context;
        Smssync _service;

        public GetSmsListAsyncTask(Context context, Smssync service) {
            this.context = context;
            _service = service;
        }

        protected SmsApiDatamodelSmsCollection doInBackground(Void... unused) {
            SmsApiDatamodelSmsCollection smsList = null;
            try {
                smsList = _service.smssync().getSmsList().execute();
            } catch (IOException e) {
                Log.d(TAG, e.getMessage(), e);
            }
            return smsList;
        }

        protected void onPostExecute(SmsApiDatamodelSmsCollection smsList) {

            if (smsList != null) {
                _adapter.AddNewCollection(smsList);
            }

            _swipeLayout.setRefreshing(false);
        }
    }


}