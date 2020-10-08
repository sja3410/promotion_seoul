package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class search extends AppCompatActivity {

    private EditText search_edittext;
    private List list;  // 데이터를 넣을 리스트변수
    private List userlist;
    private ListView search_listView;  // 검색을 보여줄 리스트변수
    private SearchAdapter adapter;  // 리스트뷰에 연결할 어댑터
    private ArrayList<String> arrayList;

    // Cloud Firestore 인스턴스를 액티비티로 가져옴
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersCollectionRef = db.collection("Profile");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent intent = getIntent();
        final String uid = intent.getStringExtra("uid");
        search_edittext = (EditText) findViewById(R.id.search_edittext);
        search_listView = (ListView) findViewById(R.id.search_list_view);


        FirebaseFirestoreSettings.Builder settings = new FirebaseFirestoreSettings.Builder();
        //settings.setTimestampsInSnapshotsEnabled(true);
        db.setFirestoreSettings(settings.build());

        // 리스트 생성
        list = new ArrayList<Object>();
        userlist = new ArrayList<Object>();

        // 검색어 입력 이벤트 리스너
        search_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //settingList(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = search_edittext.getText().toString();
                settingList(searchText);
                list.clear();
                userlist.clear();
            }
        });

        search_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 해당 유저의 페이지로 이동
                // 유저 페이지 만들고 나서 채우기
                HashMap<String, Object> map = new HashMap<String, Object>();
                Object user = userlist.get(position);
                Intent intent_mypage = new Intent(view.getContext(), Friend_page.class); //바꾸기 (mypage 창으로)
                intent_mypage.putExtra("friend_uid",user.toString());
                intent_mypage.putExtra("my_uid", uid );
                startActivity(intent_mypage);

            }
        });
    }


    private void settingList(final String text) {
        list.clear();
        usersCollectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        String username = document.getString("username");
                        String name = document.getString("name");
                        if(username.contains(text)) {
                            map.put("userName", username);
                            map.put("name", name);
                            map.put("userId", document.getId());
                            list.add(map);
                            userlist.add(map.get("userId").toString());
                        }
                    }
                }
                show(list);
            }
        });
    }
    public void show(List list)
    {
        String[] keys = new String[]{"userName","name"};
        int[] ids = new int[]{R.id.username, R.id.name};
        SimpleAdapter customAdapter = new SimpleAdapter(this, list, R.layout.layout, keys, ids);
        search_listView.setAdapter(customAdapter);
        startToast(ids.toString());
    }
    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}

