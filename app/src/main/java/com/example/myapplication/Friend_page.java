package com.example.myapplication;

import android.app.AppComponentFactory;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.getInteger;
import static java.lang.Integer.parseInt;

public class Friend_page extends AppCompatActivity {
    private static final String TAG = "friend_page";
    TextView user_name;
    TextView write_user;
    Map<String, Object> user = new HashMap<>();
    Map<String, Object> follow = new HashMap<>();
    TextView user_follower;
    TextView user_posting;
    TextView user_following;
    String friend_uid;
    String my_uid;


    /* FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
     FirebaseFirestore db = FirebaseFirestore.getInstance();
     DocumentReference docRef = db.collection("Profile").document(user.getUid());*/
    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersCollectionRef = db.collection("Profile");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.friend_page);
        findViewById(R.id.follow_button).setOnClickListener(onClickListener);
        user_name = findViewById(R.id.user);
        write_user = findViewById(R.id.write_username);
        Intent intent = getIntent();
        friend_uid = intent.getStringExtra("friend_uid");
        startToast(friend_uid);
        my_uid= intent.getStringExtra("my_uid");
        startToast(my_uid);
        getUsername();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.follow_button:
                    startToast(my_uid);
                    // 내 팔로우 수 증가
                    usersCollectionRef.document(my_uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();
                            // String follower = document.get("follower").toString();
                            // int follower = parseInt(document.getString("follower"));
                            long follower = document.getLong("follower");
                            follower = follower + 1;
                            startToast(Long.toString(follower));
                            /*
                            startToast(Integer.toString(follower));
                            user.put("follower", Integer.toString(follower));
                            db.collection("Profile").document(my_uid)
                                    .set(user, SetOptions.merge());
                            user.clear();
                            follow.put("follower_uid", 1);
                            db.collection("Follower").document(my_uid).collection("Follower").document(friend_uid).set(follow, SetOptions.merge());
                            //db.collection("Follower").document(my_uid).set(follow, SetOptions.merge());
                            follow.clear();
                            */
                            //Map<String, Object> profile = new HashMap<>();
                            //profile.put("follower", Integer.toString(follower));
                            DocumentReference mypageRef = db.collection("Profile").document(my_uid);
                            mypageRef
                                    .update("follower", follower);
                                    /*
                                    .addOnCompleteListener(new OnSuccessListener<Void>(){
                                        @Override
                                        public void onSuccess(Void aVoid){
                                            Log.d(TAG, "업데이트 성공");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "업데이트 실패", e);
                                        }
                                    });
                                    */
                        }
                    });
                    // 친구 팔로잉 수 증가
                    usersCollectionRef.document(friend_uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();
                            // int following= parseInt(document.getString("following"));
                            long following = document.getLong("following");
                            following = following + 1;
                            /*
                            user.put("following", following);
                            db.collection("Profile").document(friend_uid)
                                    .set(user, SetOptions.merge());
                            user.clear();
                            follow.put("follower_uid", 1);
                            db.collection("Follower").document(friend_uid).collection("Following").document(my_uid).set(follow,SetOptions.merge());
                            //follow.put("following_uid", my_uid);
                            //db.collection("Following").document(friend_uid).set(follow, SetOptions.merge());
                            follow.clear();
                            */
                            DocumentReference friendpageRef = db.collection("Profile").document(friend_uid);
                            friendpageRef
                                    .update("following", following);
                                    /*
                                    .addOnCompleteListener(new OnSuccessListener<Void>(){
                                        @Override
                                        public void onSuccess(Void aVoid){
                                            Log.d(TAG, "업데이트 성공");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "업데이트 실패", e);
                                        }
                                    });

                                     */
                        }
                    });
                    break;

            }
        }
    };

    public void getUsername() {
        user_name = findViewById(R.id.user);
        write_user = findViewById(R.id.write_username);
        user_posting = findViewById(R.id.gesi);
        user_follower = findViewById(R.id.follower);
        user_following = findViewById(R.id.following);

     //   startToast(friend_uid);
        //startToast(uid);
        usersCollectionRef.document(friend_uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                user_name.setText(document.getString("username"));
                startToast(document.getString("username"));
                user_posting.setText(document.get("posting") + "\n" + "게시물");
                user_follower.setText(document.get("follower") + "\n" + "팔로워");
                user_following.setText(document.get("following") + "\n" + "팔로잉");
            }
        });
    }
    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

}
