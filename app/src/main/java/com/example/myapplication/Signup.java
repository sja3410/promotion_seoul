package com.example.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAnalytics mFirebaseAnalytics;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "Signup";
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hyeji_signup);
        findViewById(R.id.signup_button).setOnClickListener(onClickListener);
    }
    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    View.OnClickListener onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.signup_button:
                    if(signUp()==true) {
                        String get_uid = mAuth.getUid();
                        initprofile(get_uid);
                        Intent intent_profile = new Intent(v.getContext(), ahhyun_login.class);
                        startActivity(intent_profile);
                    }
                    break;

            }
        }
    };
    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            Log.d(TAG, "updateUI: 유저 로그인 중");
        } else {
            Log.d(TAG, "updateUI: 유저 로그아웃 중");
        }
    }

    public boolean signUp() {
        // 이메일, 패스워드, 패스워드 확인 값을 받음
        String email = ((EditText) findViewById(R.id.idsignup)).getText().toString();
        String username = ((EditText) findViewById(R.id.username)).getText().toString();
        String password = ((EditText) findViewById(R.id.passwordsignup)).getText().toString();
        String re_password = ((EditText) findViewById(R.id.repasswordsignup)).getText().toString();

        /*mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    startToast("정상적으로 회원가입 되었습니다.");
                } else {
                    startToast("시스템 오류");
                }
            }
        });*/

        if (!username.isEmpty())
        {
            if (email.contains("@")  && (email.contains("naver.com") || email.contains("gmail.com"))) {
                if (password.equals(re_password) ) {
                    if (password.length() >= 6) { // 비밀번호 6자리
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            //FirebaseUser user = mAuth.getCurrentUser(); // 로그인 성공
                                            //updateUI(user);
                                            startToast("정상적으로 회원가입 되었습니다.");
                                            Log.d(TAG, "createUserWithEmail:success");
                                        } else {
                                           // updateUI(null);
                                            startToast("시스템 오류.");
                                            //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        }
                                    }
                        });
                        //startToast("정상적으로 회원가입 되었습니다.");
                        return true;
                    }
                    else {
                        startToast("비밀번호는 여섯자리 이상으로 설정해주십시오.");

                    }
                }
                else {
                    startToast("비밀번호가 같지 않습니다.");
                }
            }
            else{
                startToast("올바른 이메일 형식을 입력해주십시오.");

            }
        }
        else {
            startToast("사용자 이름을 입력해주십시오");
        }
        return false;
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private void initprofile(String userID)
    {
        String email = ((EditText) findViewById(R.id.idsignup)).getText().toString();
        String username = ((EditText) findViewById(R.id.username)).getText().toString();
        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("follower", 0);
        user.put("following", 0);
        user.put("memo", "");
        user.put("posting", 0);
        user.put("id", email);
        user.put("profile_img", "");


        db.collection("Profile").document(userID).set(user, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startToast("회원정보 저장 완료");
                        //회원정보가 설정되어있음을 확인

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        startToast("회원정보 저장 실패");
                        Log.w(TAG, "Error adding document", e);//회원정보가 설정되어있음을 확인
                    }
                });
    }

}
