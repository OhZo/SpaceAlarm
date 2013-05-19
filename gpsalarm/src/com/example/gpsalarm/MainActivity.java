package com.example.gpsalarm;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private ArrayList<String> item;
	Context mContext;
	DBManager dbManager;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mContext = getApplicationContext();
		
		ListView lv = (ListView)findViewById(R.id.listView1);
		dbManager = new DBManager(this);
		reflashList(dbManager, lv, mContext);
		
		
		
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	//알람 리스트를 재조회
	public void reflashList(DBManager manager, ListView lv, Context mContext) {
		Cursor cursor = manager.fetchAllLists();
		item = new ArrayList<String>();
		
		if(cursor.moveToFirst()){
			do{
				item.add(cursor.getString(0));
			}while(cursor.moveToNext());
		}
		CustomAdapter custom_adapter = new CustomAdapter(mContext, R.layout.chkbox, item);
		lv.setAdapter(custom_adapter);
		
		
	}
	
}


class CustomAdapter extends BaseAdapter {
	ArrayList<String> adapter_item;
	Context context;
	int layout;
	ViewHolder holder;
	boolean[] isChecked;
	
	public CustomAdapter(Context mContext, int list, ArrayList<String> item) {
		context = mContext;
		layout = list;
		adapter_item = item;
	}

	@Override
	public int getCount() {
		return adapter_item.size();
	}

	@Override
	public Object getItem(int position) {
		return adapter_item.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(layout, parent, false);
			holder = new ViewHolder();
			holder.text = (TextView)convertView.findViewById(R.id.textView1);
			holder.check = (CheckBox)convertView.findViewById(R.id.checkBox1);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.text.setText(adapter_item.get(position));
		
		isChecked = new boolean[adapter_item.size()];
		
		
		//체크가 변경됐을 때 리스너
		holder.check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean checked) {
				String title = getItem(position).toString();
				if(checked){
					//Log.i("wfaf",getItem(position).toString());//holder.check.getText().toString());
					Toast.makeText(context, title + " 시작한다", Toast.LENGTH_LONG).show();
					isChecked[position] = true;
				}
				else {
					Toast.makeText(context, title + " 종료한다", Toast.LENGTH_LONG).show();
					isChecked[position] = false;
				}
				
			}
		});
		
		if(isChecked[position]) {
			holder.check.setChecked(true);
		}
		else {
			holder.check.setChecked(false);
		}
		
		return convertView;
	}
}

class ViewHolder {
	TextView text;
	CheckBox check;
}
