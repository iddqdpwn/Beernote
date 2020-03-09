package com.example.beernote;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.BitSet;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    EditText mEdtBreweryName, mEdtBeerName, mEdtNote;
    Button mBtnAdd, mBtnList;
    ImageView mImageView;
    final int REQUEST_CODE_GALLERY = 999;
    public static DBHelper mDBHelper;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE_GALLERY){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,REQUEST_CODE_GALLERY);
            }
            else{
                Toast.makeText(this,"Don't have needed permission", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("New Note");

        mImageView = findViewById(R.id.imageView);
        mEdtBeerName = findViewById(R.id.edtBeerName);
        mEdtBreweryName = findViewById(R.id.edtBreweryName);
        mEdtNote = findViewById(R.id.edtNote);
        mBtnAdd = findViewById(R.id.btnAdd);
        mBtnList = findViewById(R.id.btnList);

        mDBHelper = new DBHelper(this,"BEERNOTEDB.sqlite", null, 1);
        mDBHelper.queryData("CREATE TABLE IF NOT EXISTS note (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "breweryName VARCHAR, beerName VARCHAR, note VARCHAR, image BLOB)");


        //select beer photo CREATE TABLE IF NOT EXIST
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(
                        MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY
                );
            }
        });

        //add note into sqlite
        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mDBHelper.insertData(
                            mEdtBreweryName.getText().toString().trim(),
                            mEdtBeerName.getText().toString().trim(),
                            mEdtNote.getText().toString().trim(),
                            imageViewToByte(mImageView)
                    );
                    Toast.makeText(MainActivity.this, "Note added", Toast.LENGTH_SHORT).show();

                    //reset views
                    mEdtBreweryName.setText("");
                    mEdtBeerName.setText("");
                    mEdtNote.setText("");
                    mImageView.setImageResource(R.drawable.addphoto);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        mBtnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NoteListActivity.class));
            }
        });
    }

    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if ( requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if ( requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if ( resultCode == RESULT_OK ){
                Uri resultUri = result.getUri();
                mImageView.setImageURI(resultUri);
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
