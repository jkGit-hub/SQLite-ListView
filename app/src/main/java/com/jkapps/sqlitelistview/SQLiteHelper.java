package com.jkapps.sqlitelistview;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.Nullable;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String TAG = "SQLiteHelper";

//    private static final String TABLE_NAME = "memo";
//    private static final String COL1 = "id";
//    private static final String COL2 = "title";
//    private static final String COL3 = "content";
//    private static final String COL4 = "image";

    SQLiteHelper(@Nullable Context context,
                 String name,
                 SQLiteDatabase.CursorFactory factory,
                 int version) {
        super(context, name, factory, version);
    }

    public void queryData(String sql) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
    }

    public void insertData(String title, String content, byte[] image) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "INSERT INTO TABLE_NAME VALUES(NULL, ?, ?, ?)";
        SQLiteStatement statement = db.compileStatement(sql);

        statement.clearBindings();
        statement.bindString(1, title);
        statement.bindString(2, content);
        statement.bindBlob(3, image);
        statement.executeInsert();
    }

    public Cursor getData(String sql) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(sql, null);
    }

    public void updateData(String title, String content, byte[] image, int id) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "UPDATE TABLE_NAME SET title = ?, content = ?, image = ? WHERE id = ?";
        SQLiteStatement statement = db.compileStatement(sql);

        statement.bindString(1, title);
        statement.bindString(2, content);
        statement.bindBlob(3, image);
        statement.bindDouble(4, (double) id);
        statement.execute();
        db.close();
    }

    public void deleteData(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "DELETE FROM TABLE_NAME WHERE id = ?";
        SQLiteStatement statement = db.compileStatement(sql);

        statement.clearBindings();
        statement.bindDouble(1, (double) id);
        statement.execute();
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        String createTable = "CREATE TABLE " + TABLE_NAME +
//                " (" + COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                COL2 + " TEXT, " + COL3 + " TEXT, " + COL4 + " BLOB)";
//        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
//        onCreate(db);
    }
}