package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class mypage extends AppCompatActivity {
    TextView user_name;
    TextView write_user;
    TextView user_follower;
    TextView user_posting;
    TextView user_following;
    ImageView img_user;
    String address_img;
    String uid;
    /* FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
     FirebaseFirestore db = FirebaseFirestore.getInstance();
     DocumentReference docRef = db.collection("Profile").document(user.getUid());*/
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersCollectionRef = db.collection("Profile");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage);
        findViewById(R.id.profile_change).setOnClickListener(onClickListener);
        user_name = findViewById(R.id.user);
        write_user = findViewById(R.id.write_username);
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        getUsername();
        get_image();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.profile_change:
                    Intent intent_picture = new Intent(v.getContext(), profile_picture.class);
                    intent_picture.putExtra("uid", uid);
                    startActivity(intent_picture);
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
        //startToast(uid);
        usersCollectionRef.document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                user_name.setText(document.getString("username"));
                user_posting.setText(document.get("posting") + "\n" + "게시물");
                user_follower.setText(document.get("follower") + "\n" + "팔로워");
                user_following.setText(document.get("following") + "\n" + "팔로잉");
                Toast.makeText(getApplicationContext(), "실패" + address_img, Toast.LENGTH_SHORT).show();
                //user_following.setText(document.get("profile_img") + "\n" + "팔로잉");
                address_img = document.getString("profile_img");
                // user_following.setText(address_img);
            }
        });

    }

    public void get_image() {
        String address;
        img_user = findViewById(R.id.img_user);
        //address = "profile_img/" + address_img;
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference rootRef = firebaseStorage.getReference();         //파일명이 bomb.png인 이미지 가져오기.
        //StorageReference imgRef = rootRef.child("profile_img/bikewheel.png");                         //photo폴더에 bikewheel.png이미지 가져오기.
        StorageReference imgRef = rootRef.child("profile_img/" + address_img);
        //Toast.makeText(getApplicationContext(), "실패" + address_img, Toast.LENGTH_SHORT).show();
        if (imgRef != null) {
            imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() { //imgRef 자체가 객체.
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(mypage.this).load(uri).into(img_user);                            //네트워크 이미지는 Glide로 해결한다.
                }                                                                                   //Glide를 쓰지 않으면 Thread + URL을 써야한다.
            });
        }
       /*
       FirebaseStorage storage = FirebaseStorage.getInstance("gs://promotion-aece9.appspot.com");
        StorageReference storageRef = storage.getReference();
        storageRef.child("profile_img/").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .load(address_img)
                        .into(img_user);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //이미지 로드 실패시
                Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
            }
        });
        }
    }*/

   /* private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }*/
    }
}