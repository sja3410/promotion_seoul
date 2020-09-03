package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.LauncherActivity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;


public class search extends AppCompatActivity {

    private EditText search_edittext;
    private ListView s_listview;
    private ScrollView s_scrollview;

    // 터치이벤트를 통해 리스트뷰가 스크롤될 수 있게 함
    private void init(){
        s_scrollview = (ScrollView)findViewById(R.id.search_scroll_view);
        s_listview = (ListView)findViewById(R.id.search_list_view);

        s_listview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                s_scrollview.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        search_edittext = findViewById(R.id.search_edittext);
        search_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = search_edittext.getText().toString();
                // adapter.filter(searchText);  <- 이제 다음 시작은 어댑터 추가부터(커스텀뷰 제작 참고)
            }
        });
    }
}

