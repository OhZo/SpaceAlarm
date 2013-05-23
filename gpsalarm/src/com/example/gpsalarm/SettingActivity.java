package com.example.gpsalarm;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SettingActivity extends Activity {
	DBManager dbManager;
	String mDst = "구미";   //목적지
	String mLabel; //제목
	String mBell;
	int mArea = 300;     //울릴영역 TODO
	int mTurn = 1; //생성시 알람 작동
	int mBiv;
	
	private CheckBox checkBiv;
	private EditText editLabel;
	boolean isChecked;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		
		dbManager = new DBManager(this);
		
		findViewById(R.id.cancelBtn).setOnClickListener(listener);
		findViewById(R.id.dstBtn).setOnClickListener(listener);
		findViewById(R.id.bellBtn).setOnClickListener(listener);
		editLabel = (EditText) findViewById (R.id.editText1);
		//findViewById(R.id.labelBtn).setOnClickListener(listener);
		findViewById(R.id.saveBtn).setOnClickListener(listener);
		
		
		
		checkBiv = (CheckBox)findViewById(R.id.checkBox1);
		checkBiv.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean checked) {
				if(checked){
					//Toast.makeText(getApplicationContext(), "진동으로~~", Toast.LENGTH_LONG).show();
					isChecked = true;
				}
				else {
					//Toast.makeText(getApplicationContext(), "진동아님...", Toast.LENGTH_LONG).show();
					isChecked = false;
				}
				
			}
		});
		
		if(isChecked) {
			checkBiv.setChecked(true);
		}
		else {
			checkBiv.setChecked(false);
		}
		
	}
	
	
	
	OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.saveBtn:
				mLabel = editLabel.getText().toString();
				dbManager.appendList(mLabel, mDst, mArea, mTurn);
				finish();
				break;
			case R.id.cancelBtn:
				finish();
				break;
			case R.id.dstBtn:
				break;
			case R.id.bellBtn:
				break;
			//case R.id.labelBtn:
			//	break;
			}
			
		}
	};
	
	
}


