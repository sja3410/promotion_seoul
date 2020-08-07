package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.InputStream;

public class Add_gallery extends AppCompatActivity {
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.add_gallery);
    checkSelfPermission();
    iv = findViewById(R.id.gallery_image);
    iv.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent,101);
        }
    });

    }

    public void checkSelfPermission() {
        String temp="";
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp+= Manifest.permission.READ_EXTERNAL_STORAGE+" ";
        }
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.WRITE_EXTERNAL_STORAGE+ " ";
        }
        if (TextUtils.isEmpty(temp) == false) { // 권한 요청
            ActivityCompat.requestPermissions(this,temp.trim().split(" "),1);
        }
        else { // 모두 허용 상태
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
        }else if (requestCode == 101 && resultCode== RESULT_CANCELED){
            Toast.makeText(this,"취소",Toast.LENGTH_SHORT).show();
        }
    }
}
