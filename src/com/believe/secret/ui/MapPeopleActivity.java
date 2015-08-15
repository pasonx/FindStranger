package com.believe.secret.ui;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.datatype.BmobGeoPoint;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.believe.secret.CustomApplcation;
import com.believe.secret.R;
import com.believe.secret.adapter.NearPeopleAdapter;
import com.believe.secret.bean.User;
import com.believe.secret.util.ImageLoadOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
/**
 * ��ͼ��ʾ��������
 * 
 * @author zynick
 *
 */
public class MapPeopleActivity extends ActivityBase {
	private MapView mMapView = null;
	private BaiduMap mBaiduMap= null;
	private TextView pop_nickname;
	private TextView pop_sex;
	private TextView pop_time;
	private TextView pop_distance;
	private ImageView pop_avatar;
	private Button pop_adduser;
	private PopupWindow infoPopupWindow ;
	private BmobGeoPoint location;
	private User userInfo;
	
	private List<User> nears = ShakeFindFriendActivity.nears;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_map_people);

		mMapView = (MapView)findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
											//�����ص�  39.963175, 116.400244
		initTopBarForLeft("��������");
		
		LatLng point = new LatLng(ShakeFindFriendActivity.latitude,ShakeFindFriendActivity.longitude);  
		MapStatus mMapStatus = new MapStatus.Builder()
		.target(point)
		.zoom(17)
		.build();
		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
		//�ı��ͼ״̬
		mBaiduMap.setMapStatus(mMapStatusUpdate);
		
		LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View popupWindow = layoutInflater.inflate(R.layout.map_pop, null);
		infoPopupWindow =  new PopupWindow(popupWindow, 500, 500);
		infoPopupWindow.setFocusable(true);
		infoPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		pop_sex = (TextView)popupWindow.findViewById(R.id.tv_pop_sex);
		pop_nickname = (TextView)popupWindow.findViewById(R.id.tv_pop_nickname);
		pop_time = (TextView)popupWindow.findViewById(R.id.tv_pop_logintime);
		pop_distance = (TextView)popupWindow.findViewById(R.id.tv_pop_distance);
		pop_adduser = (Button)popupWindow.findViewById(R.id.btn_pop);
		pop_avatar = (ImageView)popupWindow.findViewById(R.id.iv_pop_avatar);
		
		

		
		for(User people : nears){
			
				//�����ص�  39.963175, 116.400244
			location = people.getLocation();
			double latitude =location.getLatitude();
			double longitude = location.getLongitude();
			Log.d("GEO", "�����ĵ㣺"+latitude+"--"+longitude);
			LatLng p = new LatLng(latitude,longitude);  
			BitmapDescriptor bitmap = BitmapDescriptorFactory  		//����Markerͼ��  
			    .fromResource(R.drawable.mark);  
			OverlayOptions option = new MarkerOptions()  //����MarkerOption�������ڵ�ͼ�����Marker  
			    .position(p)  
			    .icon(bitmap);  
			Marker marker = (Marker)mBaiduMap.addOverlay(option);//�ڵ�ͼ�����Marker������ʾ  
			Bundle bundle = new Bundle();//���Bundle��Ϣ



			/*
			 * ��ȡ�û���
			 */
			ShowLog("�û�����"+people.getUsername());
			
			bundle.putSerializable("user", people); //��Ӷ����marker
			marker.setExtraInfo(bundle);

		}
		
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() { //Ϊmarker���õ����¼�������
			
			@Override
			public boolean onMarkerClick(Marker arg0) {
				User user = (User)arg0.getExtraInfo().getSerializable("user");
				userInfo = user;
				pop_nickname.setText("�û���:"+user.getUsername());
				if(user.getSex())
					pop_sex.setText("�Ա�:��");
				infoPopupWindow.showAtLocation(mMapView, Gravity.CENTER, 0, 0);
				/**
				 * ��ȡ����
				 */
				String currentLat = CustomApplcation.getInstance().getLatitude();
				String currentLong = CustomApplcation.getInstance().getLongtitude();
				if(location!=null && !currentLat.equals("") && !currentLong.equals("")){
					double distance = NearPeopleAdapter.DistanceOfTwoPoints(Double.parseDouble(currentLat),Double.parseDouble(currentLong),user.getLocation().getLatitude(), 
					user.getLocation().getLongitude());
					ShowLog("���뵱ǰ"+String.valueOf(distance)+"��");
					pop_distance.setText("���뵱ǰ"+String.valueOf(distance)+"��");
						
				}else{
					pop_distance.setText("����δ֪");
					}
				/*
				 * ��ȡ��¼ʱ��
				 */
				pop_time.setText("�����¼ʱ�䣺"+user.getUpdatedAt());
				//ShowLog("�����¼ʱ�䣺"+user.getUpdatedAt());

				/**
				 * ��ȡͷ��
				 */
				String avatar = user.getAvatar();
				if (avatar != null && !avatar.equals("")) {
					ImageLoader.getInstance().displayImage(avatar, pop_avatar,
							ImageLoadOptions.getOptions());
				} else {
					pop_avatar.setImageResource(R.drawable.default_head);
				}
				
				pop_adduser.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						ShowToast("�˽����");
						////////////////////
					Intent intent = new Intent(MapPeopleActivity.this,Game_Activity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable("user", userInfo);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				});
				return false;
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mMapView.onResume();
	}
	
	protected void onDestroy(){
		super.onDestroy();
		mMapView.onDestroy();
	}
	
	protected void onPause(){
		super.onPause();
		mMapView.onPause();
	}
}
