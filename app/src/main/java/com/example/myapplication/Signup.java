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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Signup extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "Signup";
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hyeji_signup);
        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.signup_button).setOnClickListener(onClickListener);
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    View.OnClickListener onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.signup_button:
                    Intent intent_profile = new Intent(v.getContext(), SignUpExample.class);
                    boolean signupSuccess = signUp();
                    if (signupSuccess == true)
                        startActivity(intent_profile);
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

        if (!username.isEmpty())
        {
            if (email.contains("@")  && (email.contains("naver.com") || email.contains("gmail.com"))) {
                if (password.equals(re_password) ) {
                    if (password.length() >= 6) { // 비밀번호 6자리
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d(TAG, "createUserWithEmail:success");
                                            FirebaseUser user = mAuth.getCurrentUser(); // 로그인 성공
                                            updateUI(user);
                                        } else {

                                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                            updateUI(null);
                                        }
                                    }
                                });
                        startToast("정상적으로 회원가입 되었습니다.");
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
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


}
