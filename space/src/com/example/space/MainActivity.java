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
	private NMapView mMapView = null; // 네이버 맵 객체
	private NMapController mMapController = null; // 맵 컨트롤러
	private NMapOverlayManager mOverlayManager; // 오버레이 관리자 
	private NMapLocationManager mMapLocationManager; // 위치 관리자 
	private NMapViewerResourceProvider mMapViewerResourceProvider = null;
	///////////////////////////////////////////////////////////////////////////

	LinearLayout mMapContainer; // 맵을 추가할 레이아웃
	LinearLayout mMapContainer1; // 맵을 추가할 레이아웃
	//////////////////////////////
	private NGeoPoint mMyGeoPoint;  // 좌표
	private NGeoPoint selected_GeoPoint; // 선택된 좌표
	
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
        mMapView = new NMapView(this);  // mMapView 객체 생성 
       
        ///////////////////////////////////////////////////
        edit01 = (EditText) findViewById(R.id.edit01);

    
        
        Button show_btn = (Button) findViewById(R.id.show_btn);
        show_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // 사용자가 입력한 주소 정보 확인
                String userInput = edit01.getText().toString();
                 
                //리스트 뷰 아이템 초기화 
                adapter.itemClear();
                
                //파싱
                parcingLocation(userInput);
                // 검색결과 리스트뷰를 다이얼로그로 출력
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
		.setTitle("검색결과")
		.setAdapter(adapter,new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				double lat = adapter.get_lat(which); 
				double lng = adapter.get_lng(which);
				
				//선택된 곳으로 중심점 이동
				mMapController.setMapCenter(lat,lng,11);
				//중심점 표시 
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
		// 관리하고 있는 아이템의 갯수
		public int getCount() {
			
			// TODO Auto-generated method stub
			return list_item.size();
		}

		@Override
		// 데이터값
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
		// 인덱스값 
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		
		@Override
		// 각각의 아이템을 보여줄 뷰를 결정
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
        String query = userInput;//이부부은 검색어를 UTF-8로 넣어줄거임.
        System.out.println(query);
  
        try{
        	query = URLEncoder.encode(query,"UTF-8");
            URL url = new URL("http://openapi.naver.com/search?"
           +"key=15627643be13f3021fdfac57371b368f"
           +"&query="+query //여기는 쿼리를 넣으세요(검색어)
           +"&target=local&start=1&display=30");
            
       
         XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
         XmlPullParser parser = parserCreator.newPullParser();
            
         parser.setInput(url.openStream(), null);
                  
    
         
         int parserEvent = parser.getEventType();
         
         while (parserEvent != XmlPullParser.END_DOCUMENT){
          switch(parserEvent){                     
           case XmlPullParser.START_TAG:  //parser가 시작 태그를 만나면 실행
            if(parser.getName().equals("item")){
             inItem = true;
            }
            if(parser.getName().equals("title")){ //title 만나면 내용을 받을수 있게 하자 
             inTitle = true;              
            }
            if(parser.getName().equals("address")){ //address 만나면 내용을 받을수 있게 하자
            inAddress = true;              
            }
            if(parser.getName().equals("mapx")){ //mapx 만나면 내용을 받을수 있게 하자  
             inMapx = true;              
            }
            if(parser.getName().equals("mapy")){ //mapy 만나면 내용을 받을수 있게 하자  
             inMapy = true;              
            }            
            if(parser.getName().equals("message")){ //message 태그를 만나면 에러 출력 
           
              //여기에 에러코드에 따라 다른 메세지를 출력하도록 할 수 있다.
            }            
            break;
            
           case XmlPullParser.TEXT://parser가 내용에 접근했을때
        	   if(inItem){
            if(inTitle){ //isTitle이 true일 때 태그의 내용을 저장.
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
            if(inAddress){ //isAddress이 true일 때 태그의 내용을 저장.
             address = parser.getText();  
             
                inAddress = false;
            }
            if(inMapx){ //isMapx이 true일 때 태그의 내용을 저장.
             mapx = parser.getText(); 
             
                inMapx = false;
            }
            if(inMapy){ //isMapy이 true일 때 태그의 내용을 저장.
             mapy = parser.getText();
             
             inMapy = false;
            }
            break;            
           case XmlPullParser.END_TAG:
            if(parser.getName().equals("item")){
            	///좌표변환
            	
            	okA = new GeoTransPoint(Double.parseDouble(mapx),Double.parseDouble(mapy));
            	oGeo = GeoTrans.convert(GeoTrans.KATEC, GeoTrans.GEO, okA);
            	lat = oGeo.getX();
            	lng = oGeo.getY();
            	
            	//파싱된 정보 저장 
            	list_view.add(new MyItem(parced_title,address,lat,lng));
             
             inItem = false;
            }
            break;                   
          }          
           parserEvent = parser.next();
         }
     
 
        } catch(Exception e){
         //status1.setText("에러가..났습니다...");
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
		poiData.addPOIitem(mMyGeoPoint, "출발지", markerId, 0);
		poiData.endPOIdata();

		NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
		poiDataOverlay.showAllPOIdata(0);
	}
    
    private void displaySelecteLocation(double lat,double lng) {
    	

		int markerId =  NMapPOIflagType.PIN;

		NMapPOIdata poiData = new NMapPOIdata(1, mMapViewerResourceProvider);
		poiData.beginPOIdata(1);
		poiData.addPOIitem(lat,lng, "도착지", markerId, 0);
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
