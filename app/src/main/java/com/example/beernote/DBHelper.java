package com.example.beernote;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class DBHelper extends SQLiteOpenHelper {

    DBHelper(Context context,
             String name,
             SQLiteDatabase.CursorFactory factory,
             int version){
        super(context,name,factory,version);
    }

    public void queryData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    public void insertData(String breweryName, String beerName, String note, byte[] image ){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO note VALUES(NULL, ?, ?, ?, ?)"; //RECORD - old table name in database
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1,breweryName);
        statement.bindString(2,beerName);
        statement.bindString(3,note);
        statement.bindBlob(4,image);

        statement.executeInsert();
    }

    public void updateData(String breweryName, String beerName, String note, byte[] image, int id){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "UPDATE note SET breweryName=?, beerName=?, note=?, image=? WHERE id=?";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.bindString(1,breweryName);
        statement.bindString(2,beerName);
        statement.bindString(3,note);
        statement.bindBlob(4,image);
        statement.bindDouble(5,(double) id);

        statement.executeInsert();
        database.close();
    }

    public void deleteData(int id){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "DELETE FROM note WHERE id=?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindDouble(1,(double) id);
        statement.execute();
        database.close();
    }

    public Cursor getData(String sql){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql,null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
