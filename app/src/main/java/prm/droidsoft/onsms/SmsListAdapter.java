package prm.droidsoft.onsms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.appspot.otmopo3smssave.smssync.model.SmsApiDatamodelSms;
import com.appspot.otmopo3smssave.smssync.model.SmsApiDatamodelSmsCollection;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksey_2 on 09.05.2014.
 */
public class SmsListAdapter extends ArrayAdapter<SmsSerializableAdapter> {

    public static final String filename = "foor.ser";
    private final Context _context;

    // private List<SmsSerializableAdapter>

    public SmsListAdapter(Context context) {
        super(context, R.layout.sms_row_layout);

        _context = context;

        try {
            FileInputStream fileInputStream = _context.openFileInput(filename);
            ObjectInputStream oInputStream = new ObjectInputStream(fileInputStream);
            Object one = oInputStream.readObject();
            List<SmsSerializableAdapter> smss = (List<SmsSerializableAdapter>) one;
            oInputStream.close();
            addItems(smss);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (OptionalDataException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void AddNewCollection(SmsApiDatamodelSmsCollection smsApiSmsCollection) {
        clear();
        List<SmsSerializableAdapter> newItems = new ArrayList<SmsSerializableAdapter>();
        for (SmsApiDatamodelSms sms : smsApiSmsCollection.getItems()) {
            newItems.add(new SmsSerializableAdapter(sms));
        }

        addItems(newItems);

        try {
            FileOutputStream fileStream = _context.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream os;
            os = new ObjectOutputStream(fileStream);
            os.writeObject(newItems);
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addItems(List<SmsSerializableAdapter> newItems) {
        for (SmsSerializableAdapter smsad : newItems)
        {
            add(smsad);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) _context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.sms_row_layout, parent, false);

        TextView fromNumberTv = (TextView) rowView.findViewById(R.id.smsFromNumber);
        TextView smsTextTv = (TextView) rowView.findViewById(R.id.smsText);

        SmsSerializableAdapter sms = getItem(position);

        fromNumberTv.setText(sms.getSmsFromNumber());

        smsTextTv.setText(sms.getSmsText());

        return rowView;
    }


}
