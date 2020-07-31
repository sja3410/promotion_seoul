package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
        findViewById(R.id.signup_button).setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(Signup.this,Signup.class); // 페이지 이동
            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
    View.OnClickListener onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.signup_button)
            {
                Intent intent_profile = new Intent(Signup.this,Signup.class);
                boolean loginSuccess = signUp();
                if(loginSuccess)
                    startActivity(intent_profile);
            }
        }
    };
    public boolean signUp() {
        // 이메일, 패스워드, 패스워드 확인 값을 받음
        String email = ((EditText) findViewById(R.id.idsignup)).getText().toString();
        String username = ((EditText) findViewById(R.id.username)).getText().toString();
        String password = ((EditText) findViewById(R.id.passwordsignup)).getText().toString();
        String re_password = ((EditText) findViewById(R.id.repasswordsignup)).getText().toString();

        if (email.contains("@") && email.contains(".")) {
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

                                    } else {

                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
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
        return false;

    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


}
