package com.example.agniment22;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "student_database.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_STUDENTS_TABLE = "CREATE TABLE " + DatabaseContract.StudentEntry.TABLE_NAME
            + " (" +
            DatabaseContract.StudentEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DatabaseContract.StudentEntry.COLUMN_NAME + " TEXT NOT NULL," +
            DatabaseContract.StudentEntry.COLUMN_STUDENT_ID + " TEXT NOT NULL UNIQUE," +
            DatabaseContract.StudentEntry.COLUMN_ENROLLMENT_DATE + " TEXT NOT NULL," +
            DatabaseContract.StudentEntry.COLUMN_IS_ACTIVE + " INTEGER NOT NULL DEFAULT 1," +
            DatabaseContract.StudentEntry.COLUMN_DEPARTMENT + " TEXT NOT NULL," +
            DatabaseContract.StudentEntry.COLUMN_PHOTO_PATH + " TEXT)";

    private static final String SQL_CREATE_COURSES_TABLE = "CREATE TABLE " + DatabaseContract.CourseEntry.TABLE_NAME
            + " (" +
            DatabaseContract.CourseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DatabaseContract.CourseEntry.COLUMN_STUDENT_ID_FK + " INTEGER NOT NULL," +
            DatabaseContract.CourseEntry.COLUMN_COURSE_NAME + " TEXT NOT NULL," +
            DatabaseContract.CourseEntry.COLUMN_GRADE + " INTEGER NOT NULL," +
            DatabaseContract.CourseEntry.COLUMN_COURSE_DATE + " TEXT NOT NULL," +
            "FOREIGN KEY(" + DatabaseContract.CourseEntry.COLUMN_STUDENT_ID_FK + ") " +
            "REFERENCES " + DatabaseContract.StudentEntry.TABLE_NAME + "(" + DatabaseContract.StudentEntry._ID + "))";

    private static final String SQL_DELETE_STUDENTS_TABLE = "DROP TABLE IF EXISTS "
            + DatabaseContract.StudentEntry.TABLE_NAME;

    private static final String SQL_DELETE_COURSES_TABLE = "DROP TABLE IF EXISTS "
            + DatabaseContract.CourseEntry.TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Enable foreign keys
        db.execSQL("PRAGMA foreign_keys = ON;");
        db.execSQL(SQL_CREATE_STUDENTS_TABLE);
        db.execSQL(SQL_CREATE_COURSES_TABLE);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // Enable foreign keys when database is opened
        db.execSQL("PRAGMA foreign_keys = ON;");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_COURSES_TABLE);
        db.execSQL(SQL_DELETE_STUDENTS_TABLE);
        onCreate(db);
    }
}
