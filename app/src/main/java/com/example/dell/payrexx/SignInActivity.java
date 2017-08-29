package com.example.dell.payrexx;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.payrexx.LocalStorage.Utility;


public class SignInActivity extends AppCompatActivity implements View.OnClickListener{
 private    TextView support_txt,no_pyrexx_accont,register_now_for_free;
 private    TextView save_btn;
    private EditText yor_name_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        try{
            boolean chk_login =Utility.getBooleanPreferences(SignInActivity.this, "isCurrentUser");
            if (chk_login){
                Intent i = new Intent(SignInActivity.this, LoginWebview.class);
                startActivity(i);

               /* Intent i = new Intent(SignInActivity.this, NfcCarderReader.class);
                startActivity(i);*/
                finish();
            }
        }catch (Exception e){
            Log.d("login_chk",""+e.getMessage());
        }

        init();

    }
    private void init(){
        support_txt= (TextView) findViewById(R.id.need_support);
        save_btn= (TextView) findViewById(R.id.save_btn);
        no_pyrexx_accont= (TextView) findViewById(R.id.no_pyrexx_accont_txt);
        register_now_for_free= (TextView) findViewById(R.id.register_now_for_free_txt);
        yor_name_edit= (EditText) findViewById(R.id.your_name_edit);

        support_txt.setOnClickListener(this);
        save_btn.setOnClickListener(this);
        no_pyrexx_accont.setOnClickListener(this);
        register_now_for_free.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.need_support:
                Intent intent=new Intent(SignInActivity.this,SupportWebview.class);
                startActivity(intent);
                finish();
                break;

            case R.id.save_btn:
                String your_name_str=yor_name_edit.getText().toString();
                Utility.setStringPreferences(SignInActivity.this,"user_name",your_name_str);

                if(TextUtils.isEmpty(your_name_str)){
                    Toast.makeText(this, "Please enter your name ", Toast.LENGTH_SHORT).show();
                }
                else {
                    Utility.setBooleanPreferences(SignInActivity.this, "isCurrentUser", true);

                    Intent intent1=new Intent(SignInActivity.this,LoginWebview.class);
                    //intent1.putExtra("your_name",your_name_str);
                    startActivity(intent1);
                    finish();
                  //  yor_name_edit.setText("");
                }

                break;

            case R.id.no_pyrexx_accont_txt:
                break;

            case R.id.register_now_for_free_txt:
                Intent intent3=new Intent(SignInActivity.this,RegisterWebview.class);
                startActivity(intent3);
                finish();
                break;
        }
    }

}
