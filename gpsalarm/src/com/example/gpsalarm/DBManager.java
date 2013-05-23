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

		//list// �˶� ����Ʈ DB ���̺�
		//----------------------------------//
		//  title | ������| �Ÿ����� | �����ִ��� ����  //
		//----------------------------------//
		db.execSQL("create table if not exists list(title text primary key, dst text, distance int, turn int)");
		//db.execSQL("INSERT INTO list(title, dst, distance, turn) VALUES('�츮��', '�뱸', '200', '0')");
		
		//���Ҹ� & ���� ���� DB ���̺�
		db.execSQL("create table if not exists setting(bell text, biv int)");
	}
	
	
	//����Ʈ ���̺� ������ �߰�
	public void appendList(String title, String dst, int distance, int turn) {
		String sql = "insert into list(title, dst, distance, turn) values('"+ title +"', '" + dst + "', '"+ distance + "', '" + turn +"')";
		db.execSQL(sql);
	}
	
	//����Ʈ ���̺��� ������ Ŀ���� �޾ƿ�
	public Cursor fetchAllLists() {
		Cursor cursor = null;
		cursor = db.rawQuery("select * from list", null);
		return cursor;
	}
	
	//����Ʈ ���̺� ������ ����
	public void deleteList(String title, String dst, int distance, int turn) {
		String sql = "delete into List(title, dst, distance, turn) values('"+ title +"', '" + dst + "', '"+ distance + "', '" + turn +"')";
		db.execSQL(sql);
	}
	
	
	
	//����Ʈ ���̺��� ������ Ŀ���� �޾ƿ�
	public Cursor fetchAllSet() {
		Cursor cursor = null;
		cursor = db.rawQuery("select * from list", null);
		return cursor;
	}
	
	//�� ���̺� ������ ����
	public void changeSet(String bell, String biv) {
		//TODO ������ ����
		//db.execSQL(sql);
	}
	
	

}
