package com.believe.secret.ui;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import com.believe.secret.R;
import com.believe.secret.R.layout;
import com.believe.secret.bean.User;
import com.believe.secret.view.pic.GamePintuLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class Game_Activity extends ActivityBase {

	GamePintuLayout mGameView;
	Bitmap bitMap;
	Bundle bundle;
	static User user;
    private static Context c;
    public static Game_Activity instance = null;//在后面Activity关闭游戏的实例
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();  
		StrictMode.setThreadPolicy(policy);  //取消主线程不能访问网络的限制
		setContentView(R.layout.activity_game);
		c=this;
		instance = this;
		initTopBarForLeft("解谜游戏");
		ShowLog("进入GameActivity");
		mGameView = (GamePintuLayout) findViewById(R.id.id_gameview);
		bundle = this.getIntent().getExtras();
		user = (User)bundle.getSerializable("user");
		String uri = user.getAvatar();
		ShowLog(uri);
		bitMap = getBitMap(uri);//获取网络的图片
		mGameView.setBitmap(bitMap);
		ShowLog("bitmap为空:"+String.valueOf(bitMap==null));
		mGameView.restartGame();
		ShowToast("完成拼图即可进入下一步！");
	}
	
	/** 获取指定路径的图片 
	 * 
	 * @param urlpath
	 * @return
	 * @throws Exception
	 */
    public Bitmap getBitMap(String strUrl) {  
        Bitmap bitmap = null;  
        InputStream is = null;  
        try {  
            URL url = new URL(strUrl);  
            URLConnection conn = url.openConnection();  
            is = conn.getInputStream();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        bitmap = BitmapFactory.decodeStream(is);  
        return bitmap;  
    }
    /**
     * 拼图成功时调用
     */
	public static void callNext() {
		Intent intent = new Intent(c,SetMyInfoActivity.class);
		intent.putExtra("from", "add");
		intent.putExtra("username", user.getUsername());
		c.startActivity(intent);
		instance.finish();
		
	}  
	     
	 

 }  
      
    
