package com.example.dell.payrexx;

/**
 * Created by kapil on 24-Aug-17.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;

import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;


import com.example.dell.payrexx.LocalStorage.Utility;
import com.example.dell.payrexx.cardreader.LoyaltyCardReader;
import com.example.dell.payrexx.common.logger.Log;

public class Test extends AppCompatActivity implements LoyaltyCardReader.AccountCallback {
    private WebView webView;
    private ProgressBar progressBar;
    private String url ="https://{input}.payrexx.com/vpos";
    private Dialog dialog;
    private TextView logoutNo,logoutYes;
    private Toolbar toolbar;
    String aco_no;

    public static final String TAG = "CardReaderFragment";
    // Recommend NfcAdapter flags for reading from other Android devices. Indicates that this
    // activity is interested in NFC-A devices (including other Android devices), and that the
    // system should not check for the presence of NDEF-formatted data (e.g. Android Beam).
    public static int READER_FLAGS =
            NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;
    public LoyaltyCardReader mLoyaltyCardReader;
    private TextView mAccountField;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_webview);


        //   url = getIntent().getDataString();
        progressBar = (ProgressBar) findViewById(R.id.webview_progressbar);
        webView = (WebView) findViewById(R.id.webview);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar.setVisibility(View.VISIBLE);

        String your_name= Utility.getStringPreferences(Test.this,"user_name");

        // url ="https://"+your_name+".payrexx.com/vpos";
        url ="https://demo.payrexx.com/en/vpos?section=checkout";



        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress)
            {

                progressBar.setVisibility(View.GONE);
                //Make the bar disappear after URL is loaded, and changes string to Loading...
                setTitle("Loading...");
                setProgress(progress * 100); //Make the bar disappear after URL is loaded

                // Return the app name after finish loading
                if(progress == 100)
                    setTitle(R.string.app_name);
            }
        });
        webView.setWebViewClient(new MyBrowser());

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        String TEST = "tikam";
        webView.loadUrl(url);
        // webView.loadUrl("http://www.google.com");
       /* webView.loadUrl("javascript:" +
                *//*"document.getElementById('contact-details').value ='" + TEST + "';" +
                "document.getElementById('custom-amount').value = '" + TEST + "';" +
                "document.getElementById('fixed-currency').value = '" + TEST + "';" +*//*
                "document.getElementById('card-number-PPS').value = '" + TEST + "';" +
                "document.getElementById('card-holdername-PPS').value='" + TEST + "';" +
                "document.getElementById('card-cvc-PPS').value='" + TEST + "';" +
                "document.getElementById('card-expiry-month')[0].value='" + 01 + "';" +
                "document.getElementById('card-expiry-year')[0].value='" + 2017 + "';");
*/
        mLoyaltyCardReader = new LoyaltyCardReader(this);
        enableReaderMode();
    }




    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            String TEST = "tikam";
            String aco_no = "123456789";
            String holdername = "Prajapati";
            Integer amount = 1;
            Integer month = 1;
            Integer year = 2017;

            webView.loadUrl("javascript:" +

                    "document.getElementById('custom-amount').value = '" + amount + "';" +
                    "document.getElementById('fixed-currency').value = '" + TEST + "';" +
                    "document.getElementById('card-number-PPS').value = '" + aco_no + "';" +
                    "document.getElementById('card-holdername-PPS').value='" + holdername + "';" +
                    "document.getElementById('card-cvc-PPS').value='" + 158 + "';" +
//
                    "document.getElementById('cc-exp-month').value='" + month + "';" +
                    "document.getElementById('cc-exp-year').option[].value='" + year + "';");
//                "document.getElementByClassName('card-expiry-month')[0].value='" + 1 + "';" +
//                "document.getElementById('cc-exp-year')[0].value='" + 2017 + "';");

        }
//t@gmail.com

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.shakti_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.log_out:
                logOutMethod();
                break;
        }
        return true;
    }
    private  void logOutMethod() {

        dialog = new Dialog(this);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.logout_dialoge);
        logoutNo = (TextView) dialog.findViewById(R.id.logoutYesId);
        logoutYes = (TextView) dialog.findViewById(R.id.logoutNoId);

        logoutNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        logoutYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(Test.this, SignInActivity.class);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Utility.getBooleanPreferences(Test.this, "isCurrentUser");

                Utility.clearAllSharedPreferences(Test.this);
                startActivity(intent);
                finish();
            }
        });

        dialog.show();
    }
    @Override
    public void onPause() {
        super.onPause();
        disableReaderMode();
    }

    @Override
    public void onResume() {
        super.onResume();
        enableReaderMode();
    }

    private void enableReaderMode() {
        Log.i(TAG, "Enabling reader mode");
//        Activity activity = getActivity();
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(Test.this);
        if (nfc != null) {
            nfc.enableReaderMode(Test.this, mLoyaltyCardReader, READER_FLAGS, null);
        }
    }

    private void disableReaderMode() {
        Log.i(TAG, "Disabling reader mode");
        // Activity activity = getActivity();
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(Test.this);
        if (nfc != null) {
            nfc.disableReaderMode(Test.this);
        }
    }
    @Override
    public void onAccountReceived(final String account) {
        // This callback is run on a background thread, but updates to UI elements must be performed
        // on the UI thread.
        Test.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // aco_no=account;
                mAccountField.setText(account);
            }
        });
    }

}
