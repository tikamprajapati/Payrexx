package com.example.dell.payrexx;


import android.app.Dialog;
import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.Intent;

import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.payrexx.LocalStorage.Utility;
import com.pro100svitlo.creditCardNfcReader.CardNfcAsyncTask;
import com.pro100svitlo.creditCardNfcReader.utils.CardNfcUtils;

public class LoginWebview extends AppCompatActivity implements CardNfcAsyncTask.CardNfcInterface, OnTouchListener {
    String month, year,card;
    private WebView webView;
    private ProgressBar progressBar;
    private String url = "https://{input}.payrexx.com/vpos";
    private Dialog dialog;
    private TextView logoutNo, logoutYes, scan_btn;
    private Toolbar toolbar;
    private CardNfcAsyncTask mCardNfcAsyncTask;
    private NfcAdapter mNfcAdapter;
    private AlertDialog mTurnNfcDialog;
    private ProgressDialog mProgressDialog;
    private String mDoNotMoveCardMessage;
    private String mUnknownEmvCardMessage;
    private String mCardWithLockedNfcMessage;
    private boolean mIsScanNow;
    private boolean mIntentFromCreate;
    private CardNfcUtils mCardNfcUtils;
    private RelativeLayout webviewMain;
    private static final Handler handler = new Handler();
    String cardnumber,exp_month,exp_year;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_webview);

        progressBar = (ProgressBar) findViewById(R.id.webview_progressbar);
        webView = (WebView) findViewById(R.id.webview);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        webviewMain = (RelativeLayout) findViewById(R.id.webviewMain);
        webviewMain.setOnTouchListener(this);
        setSupportActionBar(toolbar);
        progressBar.setVisibility(View.VISIBLE);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(LoginWebview.this);
        if (mNfcAdapter == null) {
            TextView noNfc = (TextView) findViewById(android.R.id.candidatesArea);
            noNfc.setVisibility(View.VISIBLE);
        } else {
            mCardNfcUtils = new CardNfcUtils(LoginWebview.this);
            createProgressDialog();
            initNfcMessages();
            mIntentFromCreate = true;
            onNewIntent(getIntent());
        }

        //url = getIntent().getDataString();


        String your_name = Utility.getStringPreferences(LoginWebview.this, "user_name");

        url ="https://"+your_name+".payrexx.com/vpos";
       // url = "https://payrexx.com/en/vpos?section=checkout";
       // url = "https://demo.payrexx.com/en/vpos?section=checkout";


        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setVisibility(View.GONE);
                //Make the bar disappear after URL is loaded, and changes string to Loading...
                setTitle("Loading...");
                setProgress(progress * 100); //Make the bar disappear after URL is loaded
                // Return the app name after finish loading
                if (progress == 100)
                    setTitle(R.string.app_name);
            }
        });


        webView.setWebViewClient(new MyBrowser());
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(url);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new MyBrowser(), "app");

    }

    @Override
    protected void onResume() {
        super.onResume();
        mIntentFromCreate = false;
        if (mNfcAdapter != null && !mNfcAdapter.isEnabled()) {
            showTurnOnNfcDialog();

        } else if (mNfcAdapter != null) {
            if (!mIsScanNow) {
            }
            mCardNfcUtils.enableDispatch();
        }
    }


    @Override
    public void startNfcReadCard() {
        mIsScanNow = true;
        mProgressDialog.show();
    }


    @Override
    public void cardIsReadyToRead() {
        card = mCardNfcAsyncTask.getCardNumber();
        String expiredDate = mCardNfcAsyncTask.getCardExpireDate();
        String[] separated = expiredDate.split("/");
        month = separated[0];
        year = separated[1];
        MyBrowser mb = new MyBrowser();

        mb.checkJS(card, month, year);


    }

    private String getPrettyCardNumber(String card) {
        //Toast.makeText(LoginWebview.this, "11100000000", Toast.LENGTH_LONG).show();
        String div = " - ";
        return card.substring(0, 4) + div + card.substring(4, 8) + div + card.substring(8, 12)
                + div + card.substring(12, 16);


    }

    @Override
    public void doNotMoveCardSoFast() {
        Toast.makeText(LoginWebview.this, "Dont't move casd so fast", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void unknownEmvCard() {
        Toast.makeText(LoginWebview.this, "Unknown EVM Card", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void cardWithLockedNfc() {
        Toast.makeText(LoginWebview.this, "Card With Locked NFC", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finishNfcReadCard() {
        mProgressDialog.dismiss();
        mCardNfcAsyncTask = null;
        mIsScanNow = false;
    }

    private void createProgressDialog() {
        String title = getString(R.string.ad_progressBar_title);
        String mess = getString(R.string.ad_progressBar_mess);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(mess);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mNfcAdapter != null && mNfcAdapter.isEnabled()) {
            mCardNfcAsyncTask = new CardNfcAsyncTask.Builder(this, intent, mIntentFromCreate)
                    .build();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mNfcAdapter != null) {
            mCardNfcUtils.disableDispatch();
        }
    }

    private void showTurnOnNfcDialog() {
        if (mTurnNfcDialog == null) {
            String title = getString(R.string.ad_nfcTurnOn_title);
            String mess = getString(R.string.ad_nfcTurnOn_message);
            String pos = getString(R.string.ad_nfcTurnOn_pos);
            String neg = getString(R.string.ad_nfcTurnOn_neg);
            mTurnNfcDialog = new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(mess)
                    .setPositiveButton(pos, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Send the user to the settings page and hope they turn it on
                            if (android.os.Build.VERSION.SDK_INT >= 19) {
                                startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
                            } else {
                                startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                            }
                        }
                    })
                    .setNegativeButton(neg, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            onBackPressed();
                        }
                    }).create();
        }
        mTurnNfcDialog.show();
    }

    private void initNfcMessages() {

        mDoNotMoveCardMessage = getString(R.string.snack_doNotMoveCard);
        mCardWithLockedNfcMessage = getString(R.string.snack_lockedNfcCard);
        mUnknownEmvCardMessage = getString(R.string.snack_unknownEmv);
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
        switch (item.getItemId()) {
            case R.id.log_out:
                logOutMethod();
                break;

        }
        return true;
    }

    private void logOutMethod() {
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
                Intent intent = new Intent(LoginWebview.this, SignInActivity.class);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Utility.getBooleanPreferences(LoginWebview.this, "isCurrentUser");
                Utility.clearAllSharedPreferences(LoginWebview.this);
                startActivity(intent);
                finish();
            }
        });

        dialog.show();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
       // Toast.makeText(getApplicationContext(), "Check", Toast.LENGTH_SHORT).show();
        if (event.getAction() == MotionEvent.ACTION_UP) {
            return true;
        }
        return false;
    }

    private class MyBrowser extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        int toastCount = 0;


        @JavascriptInterface
        public String checkCall()
        {
             cardnumber = card;
             exp_month = month;
             exp_year = year;

            if(toastCount==0) {
                Toast.makeText(LoginWebview.this, "Please keep your card to back panel of the phone", Toast.LENGTH_SHORT).show();
                toastCount=1;
            }
            return checkNull(cardnumber) + "-" + checkNull(exp_month) + "-" + checkNull(exp_year);
        }


        public String checkNull(String value) {
            if (value == null || value == "null" || value == "undefined" || value == "") {
                return "";
            }
            return value;
        }

        public String generateToast(){
            Toast.makeText(LoginWebview.this, "Please keep your card to back panel of the phone", Toast.LENGTH_SHORT).show();
            return "yes";
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            webView.loadUrl("javascript:" +
                    "function hasClass1(element, cls) {\n" +
                    "    return (' ' + element.className + ' ').indexOf(' ' + cls + ' ') > -1;\n" +
                    "}" +
                    "var myVar1 = setInterval(function(){"
                    + "var el1 = document.getElementById('payment-details');"
                    + "if(hasClass1(el1, 'active')){  "
                    +"var itemnew = app.checkCall(); clearInterval(myVar1);"
                    + "}},1000);");
        }

        public void checkJS(String card, String month, String year){

            webView.loadUrl("javascript:" +
                    "function hasClass(element, cls) {\n" +
                    "    return (' ' + element.className + ' ').indexOf(' ' + cls + ' ') > -1;\n" +
                    "}" +
                    "var myVar = setInterval(function(){"
                    + "var el = document.getElementById('payment-details');"
                    + "if(hasClass(el, 'active')){  "
                    + "var item = app.checkCall();"
                    + "var temp = item.split(\"-\");"
                    + "document.getElementById('card-number-PPS').value = temp[0];"
                    + "document.getElementsByClassName('card-expiry-month')[0].value = temp[1];"
                    + "document.getElementsByClassName('card-expiry-year')[0].value = '20'+temp[2];if(temp[0]!=''){clearInterval(myVar);}"
                    + "}},1000);");
        }



    }


}
