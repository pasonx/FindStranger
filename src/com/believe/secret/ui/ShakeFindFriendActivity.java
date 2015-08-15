package com.believe.secret.ui;
/**
 * 摇一摇页面，通过摇动手机进入地图（显示附近的人信息）
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.im.task.BRequest;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.believe.secret.R;
import com.believe.secret.bean.User;
import com.believe.secret.util.CollectionUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.believe.secret.bean.User;
public class ShakeFindFriendActivity extends ActivityBase{
    //触发摇一摇的最小时间间隔  
    private final int SHAKE_SHORTEST_TIME_INTERVAL = 5;  
    //传感器值变化的阀值  
    private final int SHAKE_SHORTEST_SENSOR_VALUE = 10;  
    private long lastShakeTime = 0;  
	private SensorManager sensorManager;
	private Vibrator vibrator;
	private double QUERY_KILOMETERS = 20;//默认查询1公里范围内的人
	boolean isSuccess = false; //查询成功
	private TextView tv;
	public static List<User> nears = new ArrayList<User>();
	public static double latitude;
	public static double longitude;
	private RelativeLayout mImgUp;  
    private RelativeLayout mImgDn;
	LocationClient mLocationClient = null;
	Sensor sensor;
	boolean flag=true;//摇一摇次数限制标记
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shake_findfriend);
		initTopBarForLeft("摇一摇");
		//摇一摇监听
		sensorManager = (SensorManager) getSystemService (Context.SENSOR_SERVICE);
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		startListener();
		//sensorManager.registerListener(listener, sensor, SensorManager. SENSOR_DELAY_NORMAL);
		//开启定位
		mLocationClient = new LocationClient(this);  
		BDLocationListener myListener = new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				latitude = location.getLatitude();
				longitude = location.getLongitude();
				Log.v("LOC", longitude+"==="+latitude);
			
			}
		};
		mLocationClient.registerLocationListener(myListener);//注册监听器
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(false);
		option.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式
		option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
		mLocationClient.setLocOption(option);
		mLocationClient.start();

		mImgUp = (RelativeLayout) findViewById(R.id.shakeImgUp);  
        mImgDn = (RelativeLayout) findViewById(R.id.shakeImgDown); 
		mImgUp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				long currentTime = System.currentTimeMillis();
		        if (((currentTime-lastShakeTime) <= SHAKE_SHORTEST_TIME_INTERVAL) )
		        		{  
		            return;  
		        }  
				stopListener();
				ShowLog("单击事件->监听器解除");
				getNearPeople(false);
			}
		});
	}
	/**
	 * 停止获取位置
	 */
	void stopLoc(){
		mLocationClient.stop();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopListener();
		stopLoc();
	}
	protected void onPause(){
		super.onPause();
		// 停止检测
	}
	private SensorEventListener listener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
			long currentTime = System.currentTimeMillis();  
	        int type = event.sensor.getType();  
	        if (((currentTime-lastShakeTime) <= SHAKE_SHORTEST_TIME_INTERVAL) ||  
	                (type != Sensor.TYPE_ACCELEROMETER)) {  
	            return;  
	        }  
	        lastShakeTime = currentTime;  
			// 加速度可能会是负值，所以要取它们的绝对值
			float xValue = Math.abs(event.values[0]);
			float yValue = Math.abs(event.values[1]);
			float zValue = Math.abs(event.values[2]);
			if ((xValue > 11 || yValue > 11 || zValue > 11) && flag) {
				// 认为用户摇动了手机，触发摇一摇逻辑
				ShowLog("摇一摇传感器监听");
				stopListener();
				startAnim();
				Toast.makeText(ShakeFindFriendActivity.this, "摇一摇", Toast.LENGTH_SHORT).show();
		        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		        vibrator.vibrate(new long[]{500,300,500,300}, -1);
		        getNearPeople(false);
			} 
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	
	};
	/**
	 * 获取附近的人
	 * @param isUpdate
	 * @return
	 */
	private boolean getNearPeople(final boolean isUpdate){
		Log.d("Shake", "进入获取周围人的代码");
		

		userManager.queryKiloMetersListByPage(isUpdate,0,"location", longitude, latitude, true,QUERY_KILOMETERS,"sex",false,new FindListener<User>() {
			//此方法默认查询所有带地理位置信息的且性别为女的用户列表，如果你不想包含好友列表的话，将查询条件中的isShowFriends设置为false就行

				@Override
				public void onSuccess(List<User> arg0) {
					stopListener();
					if (CollectionUtils.isNotNull(arg0)) {
						if(isUpdate){
							nears.clear();
						}
						if(arg0.size()<BRequest.QUERY_LIMIT_COUNT){
							
							ShowToast("附近的人搜索完成!");
							nears = arg0;	
							TimerTask task = new TimerTask(){  
							    public void run(){  
							    	callMapActivity();
									stopLoc(); 
									finish();
							    }  
							};  
							Timer timer = new Timer();
							timer.schedule(task, 1000);
						}else{
							flag = true;
							ShowToast("暂无附近的人!");
							startListener();
						}
					}else{
						flag = true;
						ShowToast("等待获取位置!");
						startListener();
					}
				}
				
				@Override
				public void onError(int arg0, String arg1) {

					ShowToast("网络或参数错误");
					startListener();

				}
			
			});
		

		
		return isSuccess;

	}
	
	private void callMapActivity(){
		Intent intent = new Intent(this,MapPeopleActivity.class);
		startActivity(intent);
	}
	 public void startAnim () {   //定义摇一摇动画动画  
	        AnimationSet animup = new AnimationSet(true);  
	        TranslateAnimation mytranslateanimup0 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,-0.5f);  
	        mytranslateanimup0.setDuration(1000);  
	        TranslateAnimation mytranslateanimup1 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,+0.5f);  
	        mytranslateanimup1.setDuration(1000);  
	        mytranslateanimup1.setStartOffset(1000);  
	        animup.addAnimation(mytranslateanimup0);  
	        animup.addAnimation(mytranslateanimup1);  
	        mImgUp.startAnimation(animup);  
	          
	        AnimationSet animdn = new AnimationSet(true);  
	        TranslateAnimation mytranslateanimdn0 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,+0.5f);  
	        mytranslateanimdn0.setDuration(1000);  
	        TranslateAnimation mytranslateanimdn1 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,-0.5f);  
	        mytranslateanimdn1.setDuration(1000);  
	        mytranslateanimdn1.setStartOffset(1000);  
	        animdn.addAnimation(mytranslateanimdn0);  
	        animdn.addAnimation(mytranslateanimdn1);  
	        mImgDn.startAnimation(animdn);    
	    }  
	 void stopListener(){
			if (sensorManager != null) { //解除监听器
				sensorManager.unregisterListener(listener);
			}
	 }
		void startListener(){
			sensorManager.registerListener(listener, sensor, SensorManager. SENSOR_DELAY_NORMAL);
		}
}