package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.InputStream;


public class Add_gallery extends AppCompatActivity {
    ImageView iv;
    private EditText title_edittext, content_edittext;
    private Button upload_button;
    TextView image_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_gallery);
        checkSelfPermission();
        title_edittext = findViewById(R.id.add_title);
        content_edittext = findViewById(R.id.add_content);
        upload_button = findViewById(R.id.upload_button);
        image_name = findViewById(R.id.gallery_image_name);
        // 업로드 버튼이 클릭되었을 때
        upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 제목과 내용 문자열 생성
                String title = title_edittext.getText().toString();
                String content = content_edittext.getText().toString();
                /*
                Cloud Firestore에 글 관련 컬렉션 추가 후
                각 요소에 맞춰 제목, 내용, 사진, 동영상, 작성자, 작성시간 등을 저장
                */
                // 글을 파이어베이스에 저장하고 Post 창으로 이동
                Intent intent = new Intent(getApplicationContext(), Post.class);
                startActivity(intent);
            }
        });

        iv = findViewById(R.id.gallery_image);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 101);
            }
        });


    }

    // 권한에 대한 응답이 있을 때 작동하는 함수
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) { // 권한을 허용했을 경우
            int length = permissions.length;
            for (int i = 0; i<length; i++) {
                if(grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    // 동의
                    Log.d("add_gallery","권한 허용: "+permissions[i]);
                }
            }
        }
    }

    public void checkSelfPermission() {
        String temp = "";
        // 파일 읽기 권한 확인
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.READ_EXTERNAL_STORAGE + " ";
        }
        // 파일 쓰기 권한 확인
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.WRITE_EXTERNAL_STORAGE + " ";
        }
        if (TextUtils.isEmpty(temp) == false) { // 권한 요청
            ActivityCompat.requestPermissions(this, temp.trim().split(" "), 1);
        } else { // 모두 허용 상태
            Toast.makeText(this, "권한을 모두 허용", Toast.LENGTH_SHORT).show();
        }


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 101 && resultCode == RESULT_OK) {
            try {
                InputStream is = getContentResolver().openInputStream(data.getData());
                Bitmap bm = BitmapFactory.decodeStream(is);
                is.close();
                iv.setImageBitmap(bm);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == 101 && resultCode== RESULT_CANCELED){
            Toast.makeText(this,"취소",Toast.LENGTH_SHORT).show();
        }
    }

}
