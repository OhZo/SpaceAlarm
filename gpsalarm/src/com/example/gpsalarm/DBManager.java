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
		//db.execSQL("INSERT INTO list(title, dst, biv, bell) VALUES"+"('�츮��', '�뱸', 'ok', '���')");
		//db.execSQL("INSERT INTO list(title, dst, biv, bell) VALUES"+"('�ʰ���','����','��','�߰�')");
		//db.execSQL("INSERT INTO list(title, dst, biv, bell) VALUES"+"('�µ���','���','��Ű','����')");
	}
	
	//��� �����͸� ����
	public void appendData(String text) {
		String sql = "insert into list(title) values('"+ text +"')";
		db.execSQL(sql);
	}
	
	//����� ������ Ŀ���� �޾ƿ�
	public Cursor fetchAllLists() {
		Cursor cursor = null;
		cursor = db.rawQuery("select * from list", null);
		return cursor;
	}

}
