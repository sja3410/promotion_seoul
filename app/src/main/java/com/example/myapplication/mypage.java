package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class mypage extends AppCompatActivity {
    TextView user_name;
    TextView user_follower;
    TextView user_posting;
    TextView user_following;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef = db.collection("Profile").document(user.getUid());

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage);
        findViewById(R.id.profile_change).setOnClickListener(onClickListener);
        getUsername();
        get_post();
        get_follower();
        get_following();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.profile_change:
                    Intent intent_picture = new Intent(v.getContext(),profile_picture.class);
                    startActivity(intent_picture);
                    break;
            }
        }
    };
    public void getUsername(){
        user_name = findViewById(R.id.user);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                user_name.setText(document.getString("username"));
            }
        });
    }

    public void get_post() {
        user_posting = findViewById(R.id.gesi);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                Integer post = document.getLong("posting").intValue();

                user_posting.setText("게시물\n"+post.toString());
            }
        });
    }

    public void get_follower(){
        user_follower = findViewById(R.id.follower);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                Integer follower = document.getLong("follower").intValue();

                user_follower.setText("팔로워\n"+follower.toString());
            }
        });
    }
    public void get_following(){
        user_following = findViewById(R.id.following);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                Integer following = document.getLong("following").intValue();

                user_following.setText("팔로잉\n"+following.toString());
            }
        });
    }


}
