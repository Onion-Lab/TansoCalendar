package com.example.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "carbon.db";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS CARBON(carbondate DATE PRIMARY KEY, carbonemit REAL NOT NULL, memo TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS POINT(ids INTEGER PRIMARY KEY, point INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS CARBON");
        db.execSQL("DROP TABLE IF EXISTS POINT");
        onCreate(db);
    }

    public void insertCarbonTable(String carbondate, double carbonemit, String memo) {
        // 데이터 쓰기
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO CARBON (carbondate, carbonemit, memo) VALUES ('" + carbondate + "'," + carbonemit + ", '" + memo + "') ON CONFLICT(carbondate) DO UPDATE SET carbonemit=" + carbonemit + ", memo='" + memo +"'");
    }

    public void deleteCarbonTable(String carbondate) {
        // 조건에 일치하는 행을 삭제
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM CARBON WHERE carbondate='" + carbondate + "'");
    }

    public void updateCarbonTable(String carbondate, double carbonemit, String memo) {
        // 조건에 일치하는 행의 데이터 변경
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE CARBON SET carbonemit=" + carbonemit + ",memo='" + memo + "' WHERE carbondate=" + carbondate);
    }

    public CarbonDAO getCarbonDAOByDate(String carbondate) {
        // 날짜로 정보 조회하기
        CarbonDAO ret = null;
        SQLiteDatabase db = getWritableDatabase();
        Cursor mCursor = db.rawQuery("SELECT * FROM CARBON WHERE carbondate=?", new String[] {carbondate});
        if(mCursor.moveToFirst()) {
            ret = new CarbonDAO(mCursor.getString(0), mCursor.getDouble(1), mCursor.getString(2));
        }
        mCursor.close();
        return ret;
    }

    public double getCarbonAverageLastWeek() {
        // 지난주의 탄소 배출량 평균
        double ret;
        SQLiteDatabase db = getWritableDatabase();
        Cursor mCursor = db.rawQuery("SELECT avg(carbonemit) FROM CARBON WHERE strftime('%W',carbondate)=strftime('%W', date('now', '-7 days'))", null);
        mCursor.moveToNext();
        ret = mCursor.getDouble(0);
        mCursor.close();
        return ret;
    }

    public double getCarbonAverageThisWeek() {
        // 이번주의 탄소 배출량 평균
        double ret;
        SQLiteDatabase db = getWritableDatabase();
        Cursor mCursor = db.rawQuery("SELECT avg(carbonemit) FROM CARBON WHERE strftime('%W',carbondate)=strftime('%W', date('now'))", null);
        mCursor.moveToNext();
        ret = mCursor.getDouble(0);
        mCursor.close();
        return ret;
    }

    public ArrayList<CarbonDAO> getDateHigherThanAverage(int year, int month) {
        // 이번달에 평균보다 높은 탄소배출량을 기록한 날
        ArrayList<CarbonDAO> ret = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String ym = String.format("%d-%02d", year, month);
        Cursor mCursor = db.rawQuery("SELECT * FROM CARBON WHERE strftime('%Y-%m',carbondate)=strftime('%Y-%m', date('now')) AND carbonemit > (SELECT avg(carbonemit) FROM CARBON WHERE strftime('%Y-%m',carbondate)=?)", new String[] {ym});
        if(mCursor.moveToFirst()) {
            do {
                ret.add(new CarbonDAO(mCursor.getString(0), mCursor.getDouble(1), mCursor.getString(2)));
            } while(mCursor.moveToNext());
        }
        mCursor.close();
        return ret;
    }

    public ArrayList<CarbonDAO> getDateLowerThanAverage(int year, int month) {
        // 이번달에 평균과 같거나 낮은 탄소배출량을 기록한 날
        ArrayList<CarbonDAO> ret = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String ym = String.format("%d-%02d", year, month);
        Cursor mCursor = db.rawQuery("SELECT * FROM CARBON WHERE strftime('%Y-%m',carbondate)=strftime('%Y-%m', date('now')) AND carbonemit <= (SELECT avg(carbonemit) FROM CARBON WHERE strftime('%Y-%m',carbondate)=?)", new String[] {ym});
        if(mCursor.moveToFirst()) {
            do {
                ret.add(new CarbonDAO(mCursor.getString(0), mCursor.getDouble(1), mCursor.getString(2)));
            } while(mCursor.moveToNext());
        }
        mCursor.close();
        return ret;
    }

    public ArrayList<CarbonDAO> selectCarbonTable() {
        // 테이블의 모든 데이터 선택
        SQLiteDatabase db = getWritableDatabase();
        Cursor mCursor = db.rawQuery("SELECT * FROM CARBON", null);
        ArrayList<CarbonDAO> list = new ArrayList<>();
        if(mCursor.moveToFirst()) {
            do {
                list.add(new CarbonDAO(mCursor.getString(0), mCursor.getDouble(1), mCursor.getString(2)));
            } while(mCursor.moveToNext());
        }
        mCursor.close();
        return list;
    }
}
