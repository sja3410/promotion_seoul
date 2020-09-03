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

import java.util.ArrayList;
import java.util.List;


public class search extends AppCompatActivity {

    private EditText search_edittext;
    private List<String> list;  // 데이터를 넣을 리스트변수
    private ListView search_listView;  // 검색을 보여줄 리스트변수
    private SearchAdapter adapter;  // 리스트뷰에 연결할 어댑터
    private ArrayList<String> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        search_edittext = (EditText) findViewById(R.id.search_edittext);
        search_listView = (ListView) findViewById(R.id.search_list_view);

        // 리스트 생성
        list = new ArrayList<String>();

        // 데이터베이스에서 사용자의 정보 가져와서 저장
        settingList();

        // 리스트의 모든 데이터를 arrayList에 복사
        arrayList = new ArrayList<String>();
        arrayList.addAll(list);

        // 리스트에 연동할 어댑터 생성
        adapter = new SearchAdapter(list, this);

        // 리스트뷰에 어댑터 연결
        search_listView.setAdapter(adapter);

        // 검색어 입력 이벤트 리스너
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
                search_person(searchText);
            }
        });
    }

    public void search_person(String Text){
        // 입력 초기화
        list.clear();

        if(Text.length() == 0){
            list.addAll(arrayList);
        } else {
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i).toLowerCase().contains(Text)) {
                    list.add(arrayList.get(i));
                }
            }
        }
        // 어댑터 갱신
        adapter.notifyDataSetChanged();
    }

    private void settingList() {
        list.add("안선정");
        list.add("이아현");
        list.add("임혜지");
    }
}

