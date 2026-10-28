package com.example.midterm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "student.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE = "students";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " (" +
                "student_id TEXT PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "age INTEGER NOT NULL CHECK(age >= 17 AND age <= 50))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public boolean insertStudent(String id, String name, int age) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("student_id", id);
        cv.put("name", name);
        cv.put("age", age);
        return db.insert(TABLE, null, cv) != -1;
    }

    public boolean updateStudent(String id, String name, int age) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("age", age);
        return db.update(TABLE, cv, "student_id = ?", new String[]{id}) > 0;
    }

    public boolean deleteStudent(String id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE, "student_id = ?", new String[]{id}) > 0;
    }

    public Cursor getAllStudents() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT student_id, name, age FROM " + TABLE + " ORDER BY name", null);
    }
}