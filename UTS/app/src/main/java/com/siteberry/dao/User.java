package com.siteberry.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.siteberry.dto.RegisteredUser;
import com.siteberry.helper.MyOpenSQLiteHelper;

public class User {
    private MyOpenSQLiteHelper mosh;

    public User(MyOpenSQLiteHelper mosh) {
        this.mosh = mosh;
    }
    public long insert(RegisteredUser reg){
        SQLiteDatabase db = mosh.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("userName",reg.getUserName());
        cv.put("password",reg.getPassword());
        long i = db.insert("user",null,cv);
        return i;
    }
}
