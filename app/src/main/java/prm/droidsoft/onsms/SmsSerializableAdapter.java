package prm.droidsoft.onsms;

import com.appspot.otmopo3smssave.smssync.model.SmsApiDatamodelSms;

import java.io.Serializable;

/**
 * Created by Aleksey_2 on 10.05.2014.
 */
public class SmsSerializableAdapter implements Serializable {

    public String getSmsFromNumber() {
        return _smsFromNumber;
    }

    public String getSmsText() {
        return _smsText;
    }

    private final String _smsFromNumber;
    private final String _smsText;

    public SmsSerializableAdapter(SmsApiDatamodelSms sms) {
        _smsFromNumber = sms.getFromNumber();
        _smsText = sms.getSmsText();
    }

}
