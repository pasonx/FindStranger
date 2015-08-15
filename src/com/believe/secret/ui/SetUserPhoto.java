package com.believe.secret.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.believe.secret.R;
import com.believe.secret.bean.User;
import com.believe.secret.util.ImageLoadOptions;
import com.believe.secret.util.ImageTools;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.believe.secret.ui.SetMyInfoActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SetUserPhoto extends BaseActivity{
	
	private Button btn1,btn2;
	private Uri imageUri;
	
	public static final int TAKE_PHOTO = 1;
	public static final int CROP_PHOTO = 2;
	public static final int CHOOSE_PICTURE = 3;
	private static final int SCALE = 5;
	String path = Environment.getExternalStorageDirectory()+"/tempImage.jpg";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setphoto);
		initTopBarForLeft("����ͷ��");
		btn1 = (Button)findViewById(R.id.btn1);
		btn2 = (Button)findViewById(R.id.btn2);
		btn1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ShowLog("�������");
				// TODO Auto-generated method stub
				File outputImage = new File(Environment. getExternalStorageDirectory(), 
						"tempImage.jpg");
				try {
					if (outputImage.exists()) {
						outputImage.delete();
					}
					outputImage.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
				imageUri = Uri.fromFile(outputImage);
				Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				startActivityForResult(intent, TAKE_PHOTO); // �����������
			}
		});
	
	btn2.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			File outputImage = new File(Environment. getExternalStorageDirectory(), 
					"tempImage.jpg");
			try {
				if (outputImage.exists()) {
					outputImage.delete();
				}
				outputImage.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			imageUri = Uri.fromFile(outputImage);
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setDataAndType(imageUri, "image/*");
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			startActivityForResult(intent, CHOOSE_PICTURE);
		}
	});
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case TAKE_PHOTO:
			if (resultCode == RESULT_OK) {
				Intent intent = new Intent("com.android.camera.action.CROP");
				intent.setDataAndType(imageUri, "image/*");
				intent.putExtra("scale", true);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				startActivityForResult(intent, CROP_PHOTO); // �����ü�����
			}
			break;
		case CROP_PHOTO:
			if (resultCode == RESULT_OK) {
				try {
					Bitmap bitmap = BitmapFactory.decodeStream (getContentResolver()
							.openInputStream(imageUri));
				//	uploadAvatar();
					Intent intent = new Intent(SetUserPhoto.this,
							RegisterActivity.class);
					startActivity(intent);
					//picture.setImageBitmap(bitmap); // ���ü������Ƭ��ʾ����
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			break;
		case CHOOSE_PICTURE:
			ContentResolver resolver = getContentResolver();
			//��Ƭ��ԭʼ��Դ��ַ
			Uri originalUri = data.getData(); 
            try {
            	//ʹ��ContentProviderͨ��URI��ȡԭʼͼƬ
				Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
				if (photo != null) {
					//Ϊ��ֹԭʼͼƬ�������ڴ��������������Сԭͼ��ʾ��Ȼ���ͷ�ԭʼBitmapռ�õ��ڴ�
					Bitmap smallBitmap = ImageTools.zoomBitmap(photo, photo.getWidth() / SCALE, photo.getHeight() / SCALE);
					//�ͷ�ԭʼͼƬռ�õ��ڴ棬��ֹout of memory�쳣����
					photo.recycle();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}  
            Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setDataAndType(originalUri, "image/*");
			intent.putExtra("scale", true);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			startActivityForResult(intent, CROP_PHOTO); // �����ü�����
			break;
		default:
			break;
		}
    }
	
	
	

}
	
	


