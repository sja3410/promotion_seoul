
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.ahhyun_signup;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;


public class ahhyun_login extends AppCompatActivity
        implements View.OnClickListener
{
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private SignInButton emailSignInButton;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ahhyun_login);
        emailSignInButton = findViewById(R.id.email_login_button);
        mAuth = FirebaseAuth.getInstance();
        text=(TextView)findViewById(R.id.signup_text);
        text.setOnClickListener(this);
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.signup_text:
                Intent intent=new Intent(getApplicationContext(), ahhyun_signup.class);
                startActivity(intent);
        }
    }
}