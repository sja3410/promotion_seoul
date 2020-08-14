package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class Post extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hyeji_post);
        findViewById(R.id.postadd).setOnClickListener(onClickListener);
        findViewById(R.id.mypagebtn).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            switch(v.getId()){
                case R.id.postadd:
                    Intent intent_post = new Intent(v.getContext(), Add_gallery.class); //바꾸기 (사진 추가하는 창으로)
                    startActivity(intent_post);
                    break;
                case R.id.mypagebtn:
                    Intent intent_mypage = new Intent(v.getContext(), mypage.class); //바꾸기 (mypage 창으로)
                    startActivity(intent_mypage);
                    break;
            }
        }

    };
}
