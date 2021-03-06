package com.example.surface.near_field_communication;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import static android.nfc.NdefRecord.createTextRecord;


public class WriteActivity extends AppCompatActivity {

    public static final String TAG = "NfcDemo";
    public static final String MIME_TEXT_PLAIN = "text/plain";

    private EditText editText;
    private TextView myTextView;
    private NfcAdapter myNfcAdapter;
    private ToggleButton readEnable;
    private Button helpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        // Sets up the NFC adapter and Textview editor

        myTextView = (TextView) findViewById(R.id.textView_explanation);
        myNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        readEnable = (ToggleButton)findViewById(R.id.readEnable);
        editText = (EditText) findViewById(R.id.editText);
        editText.setHint("Type message here"); // sets up the initial comment in the edit text box
        helpButton = (Button)findViewById(R.id.helpButton);// Help Button bottom right of screen

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WriteActivity.this, HelpActivity.class));
            }
        });

        if (myNfcAdapter == null) {
            // Stops here if the devices doesn't support NFC...
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (!myNfcAdapter.isEnabled()) {
            myTextView.setText("NFC is disabled.");
        }
        else {
            myTextView.setText(R.string.scanned_text);
        }

        handleIntent(getIntent());
    }
    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                if(readEnable.isChecked()) {
                    // reading tag stuff below
                    new NdefReaderTask().execute(tag);
                }
                else {
                    String s = editText.getText().toString();
                    NdefMessage ndefMessage= createNdefMessage(s);
                    WriteNdefMessage(tag,ndefMessage);
                }
            } else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    new NdefReaderTask().execute(tag);
                    break;
                }
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        /**
         * It's important, that the activity is in the foreground (resumed). Otherwise
         * an IllegalStateException is thrown.
         */
        setupForegroundDispatch(this, myNfcAdapter);
    }

    @Override
    protected void onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        stopForegroundDispatch(this, myNfcAdapter);

        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        handleIntent(intent);
    }
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    /**
     * @param activity The corresponding {@link BaseActivity} requesting to stop the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);


    }
    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF is not supported by this Tag.
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();
            // If message is currently null on the tag then delegate the work into a Handler and print 'Tag is Empty'
            if(ndefMessage == null) {
                Handler h1 = new Handler(Looper.getMainLooper());
                h1.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Tag is empty", Toast.LENGTH_SHORT).show();
                    }
                });

                return "";
            }
            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "Unsupported Encoding", e);
                    }
                }
            }

            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */

            byte[] payload = record.getPayload();

            //Catch for if Nothing is currently on tag, Delegates work into the Handler

            if(payload.length == 0) {
                Handler h1 = new Handler(Looper.getMainLooper());
                h1.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Tag is empty", Toast.LENGTH_SHORT).show();
                    }
                });
                return "";
            }
            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"

            // Get the Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                myTextView.setText("Read content: " + result);
            }
        }
    }

    //Code to Write a message to the tag.

    private void updateTag(Tag tag, NdefMessage ndefMessage) {

        try {
            NdefFormatable ndefFormatable = NdefFormatable.get(tag);
            if (ndefFormatable == null) {
                Toast.makeText(this, "Tag is not ndef formatable", Toast.LENGTH_SHORT).show();
            }
            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();

            Toast.makeText(this, "Tag has been written" ,Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e("format Tag stage", e.getMessage());
        }
    }

    private void WriteNdefMessage(Tag tag, NdefMessage ndefMessage) {
        try {
            if(tag == null){
                Toast.makeText(this, "Tag object cant be null", Toast.LENGTH_SHORT).show();
            }
            Ndef ndef = Ndef.get(tag);

            if(ndef == null){
                updateTag(tag,ndefMessage);

            }
            else{
                ndef.connect();
                if(!ndef.isWritable()){
                    // make sures tag is Writeable and not locked

                    Toast.makeText(this, "Tag is not Writeable",Toast.LENGTH_SHORT).show();
                    ndef.close();
                    return;
                }
                ndef.writeNdefMessage(ndefMessage);
                ndef.close();
                Toast.makeText(this, "Tag Written!!",Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

            Log.e("Write NdefMessage",e.getMessage());
        }

    }
    private NdefMessage createNdefMessage(String content){

        // create text record not done yet
        NdefRecord ndefRecord = createTextRecord("", content);

        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{ndefRecord});

        return ndefMessage;
    }
    public void tg1ReadWriteOnClick(View view){
        myTextView.setText("");
    }
}
