package com.example.beernote;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class NoteListActivity extends AppCompatActivity {

    ListView mListView;
    ArrayList<Model> mList;
    NoteListAdapter mAdapter = null;
    ImageView imageViewIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Notes list");

        mListView = findViewById(R.id.listView);
        mList = new ArrayList<>();
        mAdapter = new NoteListAdapter(this, R.layout.row, mList);
        mListView.setAdapter(mAdapter);

        Cursor cursor = MainActivity.mDBHelper.getData("SELECT * FROM note");
        mList.clear();
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String brewery = cursor.getString(1);
            String beer = cursor.getString(2);
            String note = cursor.getString(3);
            byte[] image = cursor.getBlob(4);

            mList.add(new Model(id,brewery,beer,note,image));
        }

        mAdapter.notifyDataSetChanged();
        if(mList.size()==0){
            Toast.makeText(this,"No notes in database !", Toast.LENGTH_SHORT).show();
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                CharSequence[] items = {"Update", "Delete"};

                AlertDialog.Builder dialog = new AlertDialog.Builder(NoteListActivity.this);

                dialog.setTitle("Choose an action");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0) {
                        Cursor c = MainActivity.mDBHelper.getData("SELECT id FROM note");
                        ArrayList<Integer> arrID = new ArrayList<Integer>();

                        while (c.moveToNext()){
                            arrID.add(c.getInt(0));
                        }

                        showDialogUpdate(NoteListActivity.this, arrID.get(position));
                    }
                    if (which == 1){
                        Cursor c = MainActivity.mDBHelper.getData("SELECT id FROM note");
                        ArrayList<Integer>  arrId = new ArrayList<>();
                        while(c.moveToNext()){
                            arrId.add(c.getInt(0));
                        }
                        showDialogDelete(arrId.get(position));
                    }}

                });
                            dialog.show();
                            // ? return true;
            }
        });
    }

    private void showDialogDelete(final int idNote) {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(NoteListActivity.this);
        dialogDelete.setTitle("Are you absolutely sure?");
        dialogDelete.setMessage("This action cannot be undone. This will permanently delete this note, please press \"OK\" to confirm.");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,int i) {
                try {
                    MainActivity.mDBHelper.deleteData(idNote);
                    updateNoteList();
                    Toast.makeText(NoteListActivity.this, "DELETED !", Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                    Log.e("ERROR : ", e.getMessage());
                }
            }
        });
        dialogDelete.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        dialogDelete.show();

    }

    private void showDialogUpdate(Activity activity, final int position){
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.update_dialog);
        dialog.setTitle("Update");

        imageViewIcon = dialog.findViewById(R.id.imageViewRecord);
        final EditText edtBrewery = dialog.findViewById(R.id.edtBreweryName);
        final EditText edtBeer = dialog.findViewById(R.id.edtBeerName);
        final EditText edtNote = dialog.findViewById(R.id.edtNote);
        Button btnUpdate = dialog.findViewById(R.id.btnUpdate);

        Cursor cursor = MainActivity.mDBHelper.getData("SELECT * FROM note WHERE id="+position);
        mList.clear();
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String brewery = cursor.getString(1);
                edtBrewery.setText(brewery);
            String beer = cursor.getString(2);
                edtBeer.setText(beer);
            String note = cursor.getString(3);
                edtNote.setText(note);
            byte[] image = cursor.getBlob(4);
                imageViewIcon.setImageBitmap(BitmapFactory.decodeByteArray(image,0,image.length));
            mList.add(new Model(id,brewery,beer,note,image));
        }

        int width = (int)(activity.getResources().getDisplayMetrics().widthPixels*0.95);
        int height = (int)(activity.getResources().getDisplayMetrics().heightPixels*0.95);
        dialog.getWindow().setLayout(width,height);
        dialog.show();

        imageViewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(
                        NoteListActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        888
                );
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MainActivity.mDBHelper.updateData(
                            edtBrewery.getText().toString().trim(),
                            edtBeer.getText().toString().trim(),
                            edtNote.getText().toString().trim(),
                            MainActivity.imageViewToByte(imageViewIcon),
                            position
                    );
                    Toast.makeText(getApplicationContext(), "Update success!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                catch (Exception error){
                    Log.e("Update error ", error.getMessage());
                }
                updateNoteList();
            }
        });
    }

    private void updateNoteList() {
        Cursor cursor = MainActivity.mDBHelper.getData("SELECT * FROM note");
        mList.clear();
        while (cursor.moveToNext()){

            int id = cursor.getInt(0);
            String brewery = cursor.getString(1);
            String beer = cursor.getString(2);
            String note = cursor.getString(3);
            byte[] image = cursor.getBlob(4);

            mList.add(new Model(id,brewery,beer,note,image));

        }
        mAdapter.notifyDataSetChanged();
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
        if ( requestCode == 888 && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if ( requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if ( resultCode == RESULT_OK ){
                Uri resultUri = result.getUri();
                imageViewIcon.setImageURI(resultUri);
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 888){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,888);
            }
            else{
                Toast.makeText(this,"Dont have needed permission", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
