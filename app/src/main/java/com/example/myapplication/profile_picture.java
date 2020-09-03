package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE;


public class profile_picture extends AppCompatActivity {
    private static final String TAG = "profile_picture";
    Button btn_camera;
    Button btn_finish;
    ImageView img_user;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_photo);
       // btn_camera= findViewById(R.id.btn_UploadPicture);
        btn_finish =findViewById(R.id.btn_signupfinish);
        img_user = findViewById(R.id.user_image);
        img_user.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                int permission = ContextCompat.checkSelfPermission(profile_picture.this, Manifest.permission.CAMERA);
                if(permission == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(profile_picture.this,new String[]{Manifest.permission.CAMERA},0);
                }
                else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1);
                }
            }
        });
        btn_finish.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                uploadFile();
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==0){
            if(grantResults[0]==0){
                Toast.makeText(this,"카메라 권한 승인완료",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"카메라 권한 승인 거절",Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        img_user.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                PopupMenu pop = new PopupMenu(getApplicationContext(),view);
                getMenuInflater().inflate(R.menu.profile_menu, pop.getMenu());
                pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch(menuItem.getItemId()) {
                            case R.id.one:
                                captureCamera();
                                break;
                            case R.id.two:
                                getAlbum();
                                break;
                            case R.id.three:
                                img_user.setImageResource(R.mipmap.user);
                        }
                        return true;
                    }
                });
                pop.show();
                checkPermission();
            }
        });
    }
    private void checkPermission() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            if((ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE))||
                    (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA))){
                new AlertDialog.Builder(this).setTitle("알림").setMessage("저장소 권한이 거부되었습니다.").setNeutralButton("설정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package: " + getPackageName()));
                        startActivity(intent);
                    }
                }).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).setCancelable(false).create().show();
            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, MY_PERMISSION_CAMERA);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id==1){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static final int MY_PERMISSION_CAMERA = 1111;
    private static final int REQUEST_TAKE_PHOTO = 2222;
    private static final int REQUEST_TAKE_ALBUM = 3333;
    private static final int REQUEST_IMAGE_CROP = 4444;
    String mCurrentPhotoPath;
    Uri imageURI;
    Uri photoURI, albumURI;

    public static final int REQUESTCODE = 101;
    private void captureCamera() {
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(takePictureIntent.resolveActivity(getPackageManager())!= null) {
                File photoFile = null;
                try{
                    photoFile = createImageFile();
                } catch (IOException e){
                    e.printStackTrace();
                }
                if(photoFile!=null){
                    Uri providerUri = FileProvider.getUriForFile(this,getPackageName(),photoFile);
                    imageURI = providerUri;
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,providerUri);
                    startActivityForResult(takePictureIntent,REQUEST_TAKE_PHOTO);
                }
            } else{
                Toast.makeText(this,"접근 불가능 합니다",Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" +timeStamp + ".jpg";
        File imageFile = null;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures");
        if(!storageDir.exists()) {
            storageDir.mkdirs();
        }
        imageFile = new File(storageDir,imageFileName);
        mCurrentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { // 데이터베이스 넣기!!
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_TAKE_PHOTO: // 카메라로 이동
                if(resultCode == Activity.RESULT_OK){
                    try{
                        Log.i("REQUEST_TAKE_PHOTO","OK!!!!!!");
                        galleryAddPic(); // 갤러리에 사진 추가

                        img_user.setImageURI(imageURI); // 이미지 변경하기
                    }catch(Exception e){
                        Log.e("REQUEST_TAKE_PHOTO",e.toString());
                    }
                }else{
                    Toast.makeText(profile_picture.this,"저장공간에 접근할 수 없는 기기 입니다.",Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_TAKE_ALBUM: // 갤러리로 이동
                if(resultCode == Activity.RESULT_OK){
                    if(data.getData() != null){
                        try {
                            File albumFile = null;
                            albumFile = createImageFile();
                            photoURI = data.getData();
                            albumURI = Uri.fromFile(albumFile);
                            cropImage();
                        } catch (IOException e) {
                            Log.e("TAKE_ALBUM_SINLE_ERROR",e.toString());
                        }
                    }
                }
                break;
            case REQUEST_IMAGE_CROP: // 이미지 사진 크기 변경
                if(resultCode == Activity.RESULT_OK){
                    galleryAddPic();
                    //사진 변환 error
                    img_user.setImageURI(albumURI);
                }
                break;
        }
    }
    private void getAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent,REQUEST_TAKE_ALBUM);
    }

    public void cropImage() {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.setDataAndType(photoURI,"image/*");
        cropIntent.putExtra("aspectX",1);
        cropIntent.putExtra("aspectY",1);
        cropIntent.putExtra("scale",true);
        cropIntent.putExtra("output",albumURI);
        startActivityForResult(cropIntent,REQUEST_IMAGE_CROP);
    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(mCurrentPhotoPath);
        Uri contentURI = Uri.fromFile(file);
        mediaScanIntent.setData(contentURI);
        sendBroadcast(mediaScanIntent);
        Toast.makeText(this, "앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show();
    }
    private void uploadFile(){
        if(photoURI != null){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("업로드중...");
            progressDialog.show();

            //storage
            FirebaseStorage storage = FirebaseStorage.getInstance();
            Date now = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_mmss");
            String filename = formatter.format(now)+".jpg";
            Map<String, Object> user = new HashMap<>();
            user.put("profile_img", filename);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Profile").document(mAuth.getUid())
                    .set(user,SetOptions.merge());
            StorageReference storageRef = storage.getReferenceFromUrl("gs://promotion-aece9.appspot.com").child("images/"+filename);
            storageRef.putFile(photoURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss(); //업로드 진행 Dialog 상자 닫기
                    Toast.makeText(getApplicationContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();
                }
            })
                    //실패시
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //진행중
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests") //이걸 넣어 줘야 아랫줄에 에러가 사라진다. 넌 누구냐?
                                    double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                            //dialog에 진행률을 퍼센트로 출력해 준다
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        }
    }


}
