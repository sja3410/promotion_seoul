package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class ahhyun_login extends AppCompatActivity
        implements View.OnClickListener
{
    TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ahhyun_login);
        text=(TextView)findViewById(R.id.signup_text);
        text.setOnClickListener(this);
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.signup_text:
                Intent intent=new Intent(getApplicationContext(), ahhyun_signup.class);
                startActivity(intent);
        }
    }
}
