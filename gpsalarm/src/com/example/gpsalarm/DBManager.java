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

		//list// 알람 리스트 DB 테이블
		//----------------------------------//
		//  title | 목적지| 거리설정 | 켜져있는지 상태  //
		//----------------------------------//
		db.execSQL("create table if not exists list(title text primary key, dst text, distance int, turn int)");
		//db.execSQL("INSERT INTO list(title, dst, distance, turn) VALUES('우리집', '대구', '200', '0')");
		
		//벨소리 & 진동 설정 DB 테이블
		db.execSQL("create table if not exists setting(bell text, biv int)");
	}
	
	
	//리스트 테이블에 데이터 추가
	public void appendList(String title, String dst, int distance, int turn) {
		String sql = "insert into list(title, dst, distance, turn) values('"+ title +"', '" + dst + "', '"+ distance + "', '" + turn +"')";
		db.execSQL(sql);
	}
	
	//리스트 테이블의 내용을 커서에 받아옴
	public Cursor fetchAllLists() {
		Cursor cursor = null;
		cursor = db.rawQuery("select * from list", null);
		return cursor;
	}
	
	//리스트 테이블에 데이터 삭제
	public void deleteList(String title, String dst, int distance, int turn) {
		String sql = "delete into List(title, dst, distance, turn) values('"+ title +"', '" + dst + "', '"+ distance + "', '" + turn +"')";
		db.execSQL(sql);
	}
	
	
	
	//리스트 테이블의 내용을 커서에 받아옴
	public Cursor fetchAllSet() {
		Cursor cursor = null;
		cursor = db.rawQuery("select * from list", null);
		return cursor;
	}
	
	//셋 테이블에 데이터 수정
	public void changeSet(String bell, String biv) {
		//TODO 데이터 변경
		//db.execSQL(sql);
	}
	
	

}
