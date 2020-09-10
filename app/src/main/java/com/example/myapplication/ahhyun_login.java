package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Response;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ahhyun_login extends AppCompatActivity
        implements View.OnClickListener
{
    private FirebaseAuth mAuth;
    private static final String TAG = "SignUpActivity";
    private static final int RC_SIGN_IN = 9001;
    private EditText email_edittext, password_edittext;
    private Button emailSignInButton;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ahhyun_login);
        email_edittext = findViewById(R.id.email_edittext);
        password_edittext = findViewById(R.id.password_edittext);
        emailSignInButton = findViewById(R.id.email_login_button);
        mAuth = FirebaseAuth.getInstance();
        // 이메일로 로그인 버튼이 클릭되었을 경우
        emailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = email_edittext.getText().toString().trim();
                String userPassword = password_edittext.getText().toString().trim();
                if(userEmail.getBytes().length <= 0){
                    Toast.makeText(ahhyun_login.this, "이메일을 입력해주십시오.",
                            Toast.LENGTH_SHORT).show();
                }
                else if(userPassword.getBytes().length <= 0){
                    Toast.makeText(ahhyun_login.this, "비밀번호를 입력해주십시오.",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    signIn(userEmail, userPassword);
                }
            }
        });
        /*
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());
        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);

         */
        text = (TextView)findViewById(R.id.signup_text);
        text.setOnClickListener(this);
    }

    private void updateUI(FirebaseUser user){
        if(user != null) {
            Intent intent = new Intent(this, Post.class);
            startActivity(intent);
            finish();
        }
    }

    /*
    @Override
    public void onStart(){
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    */

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(ahhyun_login.this, "로그인 성공",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(ahhyun_login.this, "로그인 실패",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.signup_text:
                Intent intent=new Intent(getApplicationContext(), Signup.class);
                startActivity(intent);
        }
    }
}