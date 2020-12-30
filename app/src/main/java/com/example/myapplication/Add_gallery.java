package com.example.myapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Add_gallery extends AppCompatActivity {
    ImageView iv;
    private EditText title_edittext, content_edittext;
    private Button upload_button;
    private Uri photopath;
    private ImageView gallery_image;
    private Button category;
    private String catego;
    private static final String TAG = "post";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    // FirebaseUser user = mAuth.getCurrentUser();
    CollectionReference usersCollectionRef = db.collection("Post");
    DocumentReference docRef = db.collection("Post").document(user.getUid());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_gallery);
        //checkSelfPermission();
        title_edittext = findViewById(R.id.add_title);
        content_edittext = findViewById(R.id.add_content);
        upload_button = findViewById(R.id.upload_button);
        gallery_image = findViewById(R.id.gallery_image);
        category = findViewById(R.id.category);

        // 업로드 버튼이 클릭되었을 때
        upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 글을 파이어베이스에 저장하고 Post 창으로 이동
                String filename = uploadFile();
                if (filename != null || filename == null)
                    uploadPost(filename);
                Intent intent = new Intent(getApplicationContext(), Post.class);
                startActivity(intent);
            }
        });

        iv = findViewById(R.id.gallery_image);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 사진 선택
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), 0);
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.gesi_menu, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id==1){
            return true;
        }
        else if (id == 2) {
            return true;
        }
        else if (id == 3) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onStart() {
        super.onStart();
        category.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                PopupMenu pop = new PopupMenu(getApplicationContext(),view);
                getMenuInflater().inflate(R.menu.gesi_menu, pop.getMenu());
                pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.exercise:
                                // 파이어베이스에 넣기 (운동)
                                catego = "exercise";
                                category.setText("운동");
                                break;
                            case R.id.food:
                                // 파이어베이스에 넣기(음식)
                                catego = "food";
                                category.setText("음식");
                                break;
                            case R.id.place:
                                catego = "place";
                                category.setText("명소");
                        }
                        return true;
                    }
                });
                pop.show();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            photopath = data.getData();
            Log.d(TAG, "uri:" + String.valueOf(photopath));
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photopath);

                gallery_image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == 101 && resultCode== RESULT_CANCELED){
            Toast.makeText(this,"취소",Toast.LENGTH_SHORT).show();
        }
    }
    public String uploadFile() {
        //업로드할 파일이 있으면 수행
        if (photopath != null) {
            //업로드 진행 Dialog 보이기
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("업로드중...");
            progressDialog.show();

            //storage

            //Unique한 파일명을 만들자.
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_mmss");
            Date now = new Date();
            String filename = formatter.format(now) + ".jpg";
            FirebaseStorage storage = FirebaseStorage.getInstance();
            //storage 주소와 폴더 파일명을 지정해 준다.
            StorageReference storageRef = storage.getReferenceFromUrl("gs://promotion-aece9.appspot.com").child("Post_img/" + filename);

            //올라가거라...
            storageRef.putFile(photopath)
                    //성공시
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            startToast("파일 업로드 완료!");
                        }
                    })
                    //실패시
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
//                            progressDialog.dismiss();
                            startToast("파일 업로드 실패!");
                        }
                    })
                    //진행중
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests") //이걸 넣어 줘야 아랫줄에 에러가 사라진다. 넌 누구냐?
                                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            //dialog에 진행률을 퍼센트로 출력해 준다
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                        }
                    });
            return "Post_img/" + filename;
        } else {
            //startToast("파일을 선택해주세요!");
            return null;
        }
    }

    public void uploadPost(String filename) {
        String title = ((EditText) findViewById(R.id.add_title)).getText().toString();
        String content = ((EditText) findViewById(R.id.add_content)).getText().toString();

        Map<String, String> post = new HashMap<>();
        post.put("title", title);
        post.put("content", content);
        post.put("photo", filename);
        String path = "Post_"+catego;
        //DocumentReference mypost = db.collection("Post").document(user.getUid()).collection("mypost").document();
        DocumentReference mypost = db.collection(path).document(user.getUid()).collection("mypost").document();
        String id = mypost.getId();
        mypost.set(post, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Add_gallery.this, "업로드 성공",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Add_gallery.this, "업로드 실패",
                                Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error adding document", e);
                    }
                });
        //DocumentReference allpost = db.collection("Post").document(user.getUid()).collection("allpost").document(id);
        //allpost.set(post, SetOptions.merge());
        //db.collection("Follower").document(user.getUid()).collection("")
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
    /*@Override
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
    }*/
    private void startToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }
}