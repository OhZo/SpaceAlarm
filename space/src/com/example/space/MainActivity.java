package com.example.space;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.space.GeoTrans;
import com.example.space.GeoTransPoint;
import com.example.space.MyItem;
import com.example.space.item_list;
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.NMapView.LayoutParams;
import com.nhn.android.maps.NMapView.OnMapStateChangeListener;
import com.nhn.android.maps.NMapView.OnMapViewTouchEventListener;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager.OnCalloutOverlayListener;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay.OnStateChangeListener;

public class MainActivity extends NMapActivity implements OnMapStateChangeListener, OnMapViewTouchEventListener,OnCalloutOverlayListener  {

	///////////////////
	ArrayList<MyItem> list_view=new ArrayList<MyItem>();
	AlertDialog resultDialog;
	MyAdapter adapter = new MyAdapter(this,R.layout.activity_main,list_view);
	////////////////////
	
	
	////////////////////////////////////////////////////////////////////////////
	public static final String API_KEY = "b227b929cd9c72e571766d1048556827"; // api-key
	private NMapView mMapView = null; // ���̹� �� ��ü
	private NMapController mMapController = null; // �� ��Ʈ�ѷ�
	private NMapOverlayManager mOverlayManager; // �������� ������ 
	private NMapLocationManager mMapLocationManager; // ��ġ ������ 
	private NMapViewerResourceProvider mMapViewerResourceProvider = null;
	///////////////////////////////////////////////////////////////////////////

	LinearLayout mMapContainer; // ���� �߰��� ���̾ƿ�
	LinearLayout mMapContainer1; // ���� �߰��� ���̾ƿ�
	//////////////////////////////
	private NGeoPoint mMyGeoPoint;  // ��ǥ
	private NGeoPoint selected_GeoPoint; // ���õ� ��ǥ
	
	////////////////////
	TextView contentsText;
	EditText edit01;
    EditText edit02;
    EditText edit03;
  
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
   
        
        mMapContainer = (LinearLayout) findViewById(R.id.mMapContainer);
        mMapContainer1 = (LinearLayout) findViewById(R.id.mMapContainer1);
        mMapView = new NMapView(this);  // mMapView ��ü ���� 
       
        ///////////////////////////////////////////////////
        edit01 = (EditText) findViewById(R.id.edit01);

    
        
        Button show_btn = (Button) findViewById(R.id.show_btn);
        show_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // ����ڰ� �Է��� �ּ� ���� Ȯ��
                String userInput = edit01.getText().toString();
                 
                //����Ʈ �� ������ �ʱ�ȭ 
                adapter.itemClear();
                
                //�Ľ�
                parcingLocation(userInput);
                // �˻���� ����Ʈ�並 ���̾�α׷� ���
                printParcedData();
               
            
            }
        });
        
        
        ///////////////////////////////////////////////////
     
    	/////////////////
    	OnStateChangeListener onPOIdataStateChangeListener = null;
    	
    	mMapViewerResourceProvider = new NMapViewerResourceProvider(this);
    	// create overlay manager
    	mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);
    	mMapLocationManager = new NMapLocationManager(this);
  
    	// initialize map view
    	initializeNMap();
     
   
    }
    protected void onResume()    {        
		super.onResume();  
		adapter.itemClear();
			           
	}

	
	void printParcedData(){

		resultDialog = new AlertDialog.Builder(this)
		.setTitle("�˻����")
		.setAdapter(adapter,new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				double lat = adapter.get_lat(which); 
				double lng = adapter.get_lng(which);
				
				//���õ� ������ �߽��� �̵�
				mMapController.setMapCenter(lat,lng,11);
				//�߽��� ǥ�� 
				displaySelecteLocation(lat,lng);
				//System.out.println(lang);
				
			}
		})
		.create();
		resultDialog.show();	
		
	}
    
    class MyAdapter extends BaseAdapter{
		ArrayList<MyItem> list_item;
		
		public MyAdapter(Context context,int layoutRes,ArrayList<MyItem> list){
			this.list_item=list_view;
		}
		@Override
		// �����ϰ� �ִ� �������� ����
		public int getCount() {
			
			// TODO Auto-generated method stub
			return list_item.size();
		}

		@Override
		// �����Ͱ�
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list_item.get(position);
		}
		public double get_lat(int position){
			return list_item.get(position).m_lat;
		}
		public double get_lng(int position){
			return list_item.get(position).m_lng;
		}

		@Override
		// �ε����� 
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		
		@Override
		// ������ �������� ������ �並 ����
		public View getView(int position, View converView, ViewGroup parent) {
			final int index=position;
			item_list layout = new item_list(getApplicationContext());
			layout.setTitle(list_item.get(index).m_title);
			layout.setAddress(list_item.get(index).m_address);
			return layout;
			
		}
		public void itemClear() {
			  list_item.clear();
			 }

		
	}
private void parcingLocation(String userInput){
		
		GeoTransPoint okA ;
		GeoTransPoint oGeo;
		Double lat;
		Double lng;
		
        boolean inItem = false, inTitle = false, inAddress = false, inMapx = false, inMapy = false;
        String title = null, address = null, mapx = null, mapy = null;
        String parced_title = null;
        String query = userInput;//�̺κ��� �˻�� UTF-8�� �־��ٰ���.
        System.out.println(query);
  
        try{
        	query = URLEncoder.encode(query,"UTF-8");
            URL url = new URL("http://openapi.naver.com/search?"
           +"key=15627643be13f3021fdfac57371b368f"
           +"&query="+query //����� ������ ��������(�˻���)
           +"&target=local&start=1&display=30");
            
       
         XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
         XmlPullParser parser = parserCreator.newPullParser();
            
         parser.setInput(url.openStream(), null);
                  
    
         
         int parserEvent = parser.getEventType();
         
         while (parserEvent != XmlPullParser.END_DOCUMENT){
          switch(parserEvent){                     
           case XmlPullParser.START_TAG:  //parser�� ���� �±׸� ������ ����
            if(parser.getName().equals("item")){
             inItem = true;
            }
            if(parser.getName().equals("title")){ //title ������ ������ ������ �ְ� ���� 
             inTitle = true;              
            }
            if(parser.getName().equals("address")){ //address ������ ������ ������ �ְ� ����
            inAddress = true;              
            }
            if(parser.getName().equals("mapx")){ //mapx ������ ������ ������ �ְ� ����  
             inMapx = true;              
            }
            if(parser.getName().equals("mapy")){ //mapy ������ ������ ������ �ְ� ����  
             inMapy = true;              
            }            
            if(parser.getName().equals("message")){ //message �±׸� ������ ���� ��� 
           
              //���⿡ �����ڵ忡 ���� �ٸ� �޼����� ����ϵ��� �� �� �ִ�.
            }            
            break;
            
           case XmlPullParser.TEXT://parser�� ���뿡 ����������
        	   if(inItem){
            if(inTitle){ //isTitle�� true�� �� �±��� ������ ����.
             title = parser.getText();
            for(int i =0; i<title.length(); i++){
            	 if(title.charAt(i) == '<'){
            		 parced_title = bParcing(title);
            		 break;
            	 }
            	 else if(i == (title.length()-1)){
            		 parced_title = title;
            	 }
             }
          
             
                inTitle = false;
                inItem = false;
            	}
            }
            if(inAddress){ //isAddress�� true�� �� �±��� ������ ����.
             address = parser.getText();  
             
                inAddress = false;
            }
            if(inMapx){ //isMapx�� true�� �� �±��� ������ ����.
             mapx = parser.getText(); 
             
                inMapx = false;
            }
            if(inMapy){ //isMapy�� true�� �� �±��� ������ ����.
             mapy = parser.getText();
             
             inMapy = false;
            }
            break;            
           case XmlPullParser.END_TAG:
            if(parser.getName().equals("item")){
            	///��ǥ��ȯ
            	
            	okA = new GeoTransPoint(Double.parseDouble(mapx),Double.parseDouble(mapy));
            	oGeo = GeoTrans.convert(GeoTrans.KATEC, GeoTrans.GEO, okA);
            	lat = oGeo.getX();
            	lng = oGeo.getY();
            	
            	//�Ľ̵� ���� ���� 
            	list_view.add(new MyItem(parced_title,address,lat,lng));
             
             inItem = false;
            }
            break;                   
          }          
           parserEvent = parser.next();
         }
     
 
        } catch(Exception e){
         //status1.setText("������..�����ϴ�...");
        }  
	 }
    
  	String bParcing(String title){
  		String temp_title = null, parced_title = null;
  		temp_title = title.replace("<b>", "");
  		parced_title = temp_title.replace("</b>", "");
        return parced_title;
    }
  	
  	
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    private void initializeNMap() {
    	
    	mMapView.setApiKey(API_KEY);
    	//mMapContainer.addView(mMapView);
    	mMapContainer1.addView(mMapView);
		mMapView.setClickable(true);
		mMapView.setEnabled(true);
		mMapView.setFocusable(true);
		//mMapView.setBuiltInZoomControls(true, null);
		mMapView.setFocusableInTouchMode(true);
		mMapView.requestFocus();
		mMapView.setOnMapStateChangeListener(this);
	    mMapView.setOnMapViewTouchEventListener(this);


		// use map controller to zoom in/out, pan and set map center, zoom level etc.
		mMapController = mMapView.getMapController();

		// use built in zoom controls
		NMapView.LayoutParams lp = new NMapView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, NMapView.LayoutParams.BOTTOM_RIGHT);
		mMapView.setBuiltInZoomControls(true, lp);

		/*
		// create resource provider
		mMapViewerResourceProvider = new SearchMapResourceProvider(this);

		// create overlay manager
		mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);
		// register callout overlay listener to customize it.
		mOverlayManager.setOnCalloutOverlayListener(onCalloutOverlayListener);

		// set data provider listener
		super.setMapDataProviderListener(onDataProviderListener);
*/
		// location manager
		mMapLocationManager = new NMapLocationManager(this);
	}
    protected void moveMyLocation() {

    	
		boolean isMyLocationEnabled = mMapLocationManager.enableMyLocation(false);
		if (!isMyLocationEnabled) {
			Toast.makeText(this, "Please enable a My Location source in system settings", Toast.LENGTH_LONG).show();
			Intent goToSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(goToSettings);
			return;
		}
		
	
		mMapLocationManager.setOnLocationChangeListener(new NMapLocationManager.OnLocationChangeListener() {
			public boolean onLocationChanged(NMapLocationManager locationManager, NGeoPoint myLocation) {
		
				mMyGeoPoint = myLocation;
				mMapController.setMapCenter(mMyGeoPoint, 11);
				markCurrentLocation();
			//	findPlacemarkAtLocation(mMyGeoPoint.getLongitude(), mMyGeoPoint.getLatitude());
				stopMyLocation();
				return true;
			}

			public void onLocationUnavailableArea(NMapLocationManager arg0,	NGeoPoint arg1) {
				Toast.makeText(MainActivity.this, "Your current location is temporarily unavailable.", Toast.LENGTH_LONG).show();
				stopMyLocation();
			}

			public void onLocationUpdateTimeout(NMapLocationManager arg0) {
				Toast.makeText(MainActivity.this, "Your current location is unavailable area.", Toast.LENGTH_LONG).show();
				stopMyLocation();
			}
		});
    }
    
    protected void onStart() {
		super.onStart();

		moveMyLocation();
	}
    private void stopMyLocation() {

		mMapLocationManager.disableMyLocation();
	}
    
    
    private void markCurrentLocation() {
	

		int markerId =  NMapPOIflagType.PIN;

		NMapPOIdata poiData = new NMapPOIdata(1, mMapViewerResourceProvider);
		poiData.beginPOIdata(1);
		poiData.addPOIitem(mMyGeoPoint, "�����", markerId, 0);
		poiData.endPOIdata();

		NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
		poiDataOverlay.showAllPOIdata(0);
	}
    
    private void displaySelecteLocation(double lat,double lng) {
    	

		int markerId =  NMapPOIflagType.PIN;

		NMapPOIdata poiData = new NMapPOIdata(1, mMapViewerResourceProvider);
		poiData.beginPOIdata(1);
		poiData.addPOIitem(lat,lng, "������", markerId, 0);
		poiData.endPOIdata();

		NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
		poiDataOverlay.showAllPOIdata(0);
	}
    
    
    
    
    
    public void onCalloutClick(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
    	// [[TEMP]] handle a click event of the callout
    	Toast.makeText(MainActivity.this, "onCalloutClick: " + item.getTitle(), Toast.LENGTH_LONG).show();
    }

    public void onFocusChanged(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
    	if (item != null) {
    		Log.i("NMAP", "onFocusChanged: " + item.toString());
    	} else {
    		Log.i("NMAP", "onFocusChanged: ");
    	}
    }
    public void onMapInitHandler(NMapView mapView, NMapError errorInfo) {
    	if (errorInfo == null) { // success
    	
    	//	mMapController.setMapCenter(new NGeoPoint(127.108099, 37.366034),11);
    	} else { // fail
    		
    		Log.e("NMAP", "onMapInitHandler: error=" + errorInfo.toString());
    	}
    }	
    public void onMapCenterChange(NMapView mapview, NGeoPoint errorInfo) {
		// TODO Auto-generated method stub
		if(errorInfo == null){
			//mMapController.setMapCenter(new NGeoPoint(127.108099, 37.366034),11);
		}
			
		else
			android.util.Log.e("NMAP2","onMapInitHandler: error= " + errorInfo.toString());
		
	}
    public NMapCalloutOverlay onCreateCalloutOverlay(NMapOverlay itemOverlay, NMapOverlayItem overlayItem, Rect itemBounds) {
    	// set your callout overlay
    	return new NMapCalloutBasicOverlay(itemOverlay, overlayItem, itemBounds);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


	@Override
	public void onLongPress(NMapView arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onLongPressCanceled(NMapView arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onScroll(NMapView arg0, MotionEvent arg1, MotionEvent arg2) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onSingleTapUp(NMapView arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onTouchDown(NMapView arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onTouchUp(NMapView arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onAnimationStateChange(NMapView arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMapCenterChangeFine(NMapView arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onZoomLevelChange(NMapView arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	

}
