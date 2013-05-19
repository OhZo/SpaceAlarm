package com.example.gpsalarm;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class DBManager {

Context mContext;
	
	SQLiteDatabase db;
	public DBManager(Context mContext) {
		super();
		this.mContext = mContext;
		db = mContext.openOrCreateDatabase("GAlarm.db", Context.MODE_PRIVATE, null);
		db.execSQL("create table if not exists list(title text primary key, dst text, biv text, bell text)");
		//db.execSQL("INSERT INTO list(title, dst, biv, bell) VALUES"+"('우리집', '대구', 'ok', '어서와')");
		//db.execSQL("INSERT INTO list(title, dst, biv, bell) VALUES"+"('너거집','서울','노','잘가')");
		//db.execSQL("INSERT INTO list(title, dst, biv, bell) VALUES"+"('걔들집','고령','오키','빠이')");
	}
	
	//디비에 데이터를 삽입
	public void appendData(String text) {
		String sql = "insert into list(title) values('"+ text +"')";
		db.execSQL(sql);
	}
	
	//디비의 내용을 커서에 받아옴
	public Cursor fetchAllLists() {
		Cursor cursor = null;
		cursor = db.rawQuery("select * from list", null);
		return cursor;
	}

}
