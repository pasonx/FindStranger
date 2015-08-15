package com.believe.secret.view.pic;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

import com.believe.secret.R;
import com.believe.secret.ui.Game_Activity;
import com.believe.secret.ui.SetMyInfoActivity;

/**
 * ��Ϸ��壬���벼���ļ����ɿ�ʼ��Ϸ
 * 
 * 
 */
public  class GamePintuLayout extends RelativeLayout implements OnClickListener
{

	/**
	 * ����Item������n*n��Ĭ��Ϊ3
	 */
	private int mColumn = 3;
	/**
	 * ���ֵĿ��
	 */
	private int mWidth;
	/**
	 * ���ֵ�padding
	 */
	private int mPadding;
	/**
	 * ������е�Item
	 */
	private ImageView[] mGamePintuItems;
	/**
	 * Item�Ŀ��
	 */
	private int mItemWidth;

	/**
	 * Item����������ı߾�
	 */
	private int mMargin = 3;

	/**
	 * ƴͼ��ͼƬ
	 */
	private Bitmap mBitmap;
	/**
	 * ��������Ժ��ͼƬbean
	 */
	private List<ImagePiece> mItemBitmaps;

	private boolean once;

	public GamePintuLayout(Context context)
	{
		this(context, null);
	}

	public GamePintuLayout(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public GamePintuLayout(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);

		mMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				mMargin, getResources().getDisplayMetrics());
		// ����Layout���ڱ߾࣬�ı�һ�£�����Ϊ���ڱ߾��е���Сֵ
		mPadding = min(getPaddingLeft(), getPaddingTop(), getPaddingRight(),
				getPaddingBottom());
	}

	public void setBitmap(Bitmap mBitmap)
	{
		this.mBitmap = mBitmap;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		// �����Ϸ���ֵı߳�
		mWidth = Math.min(getMeasuredHeight(), getMeasuredWidth());

		if (!once)
		{
			initBitmap();
			initItem();
		}
		once = true;
		setMeasuredDimension(mWidth, mWidth);
	}

	/**
	 * ��ʼ��ͼƬ
	 */
	private void initBitmap()
	{
		if (mBitmap == null)
			mBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.pintu);

		/**
		 * ��ͼƬ�г�mColumn*mColumn��
		 */
		mItemBitmaps = ImageSplitter.split(mBitmap, mColumn);

		Collections.sort(mItemBitmaps, new Comparator<ImagePiece>()
		{
			@Override
			public int compare(ImagePiece lhs, ImagePiece rhs)
			{
				return Math.random() > 0.5 ? 1 : -1;
			}
		});
	}

	/**
	 * ��ʼ��Item
	 */
	private void initItem()
	{
		// ���Item�Ŀ��
		int childWidth = (mWidth - mPadding * 2 - mMargin * (mColumn - 1))
				/ mColumn;
		mItemWidth = childWidth;
		mGamePintuItems = new ImageView[mColumn * mColumn];
		// ����Item
		for (int i = 0; i < mGamePintuItems.length; i++)
		{
			ImageView item = new ImageView(getContext());

			item.setOnClickListener(this);

			item.setImageBitmap(mItemBitmaps.get(i).bitmap);
			mGamePintuItems[i] = item;

			item.setId(i + 1);
			item.setTag(i + "_" + mItemBitmaps.get(i).index);

			RelativeLayout.LayoutParams lp = new LayoutParams(mItemWidth,
					mItemWidth);
			// ���ú���߾�,�������һ��
			if ((i + 1) % mColumn != 0)
			{
				lp.rightMargin = mMargin;
			}
			// ������ǵ�һ��
			if (i % mColumn != 0)
			{
				lp.addRule(RelativeLayout.RIGHT_OF,//
						mGamePintuItems[i - 1].getId());
			}
			// ������ǵ�һ�У�//��������߾࣬�����һ��
			if ((i + 1) > mColumn)
			{
				lp.topMargin = mMargin;
				lp.addRule(RelativeLayout.BELOW,//
						mGamePintuItems[i - mColumn].getId());
			}
			addView(item, lp);
		}

	}

	/**
	 * �õ���ֵ�е���Сֵ
	 * 
	 * @param params
	 * @return
	 */
	private int min(int... params)
	{
		int min = params[0];
		for (int param : params)
		{
			if (min > param)
			{
				min = param;
			}
		}
		return min;
	}

	private ImageView mFirst;
	private ImageView mSecond;

	@Override
	public void onClick(View v)
	{
		// �������ִ�ж�����������
		if (isAniming)
			return;
		/**
		 * ������ε����ͬһ��
		 */
		if (mFirst == v)
		{
			mFirst.setColorFilter(null);
			mFirst = null;
			return;
		}
		// �����һ��Item
		if (mFirst == null)
		{
			mFirst = (ImageView) v;
			mFirst.setColorFilter(Color.parseColor("#55FF0000"));
		} else
		// ����ڶ���Item
		{
			mSecond = (ImageView) v;
			exchangeView();
		}

	}

	/**
	 * �������еı�־λ
	 */
	private boolean isAniming;
	/**
	 * ������
	 */
	private RelativeLayout mAnimLayout;

	/**
	 * ��������Item��ͼƬ
	 */
	private void exchangeView()
	{
		mFirst.setColorFilter(null);
		setUpAnimLayout();
		// ���FirstView
		ImageView first = new ImageView(getContext());
		first.setImageBitmap(mItemBitmaps
				.get(getImageIndexByTag((String) mFirst.getTag())).bitmap);
		LayoutParams lp = new LayoutParams(mItemWidth, mItemWidth);
		lp.leftMargin = mFirst.getLeft() - mPadding;
		lp.topMargin = mFirst.getTop() - mPadding;
		first.setLayoutParams(lp);
		mAnimLayout.addView(first);
		// ���SecondView
		ImageView second = new ImageView(getContext());
		second.setImageBitmap(mItemBitmaps
				.get(getImageIndexByTag((String) mSecond.getTag())).bitmap);
		LayoutParams lp2 = new LayoutParams(mItemWidth, mItemWidth);
		lp2.leftMargin = mSecond.getLeft() - mPadding;
		lp2.topMargin = mSecond.getTop() - mPadding;
		second.setLayoutParams(lp2);
		mAnimLayout.addView(second);

		// ���ö���
		TranslateAnimation anim = new TranslateAnimation(0, mSecond.getLeft()
				- mFirst.getLeft(), 0, mSecond.getTop() - mFirst.getTop());
		anim.setDuration(300);
		anim.setFillAfter(true);
		first.startAnimation(anim);

		TranslateAnimation animSecond = new TranslateAnimation(0,
				mFirst.getLeft() - mSecond.getLeft(), 0, mFirst.getTop()
						- mSecond.getTop());
		animSecond.setDuration(300);
		animSecond.setFillAfter(true);
		second.startAnimation(animSecond);
		// ��Ӷ�������
		anim.setAnimationListener(new AnimationListener()
		{

			@Override
			public void onAnimationStart(Animation animation)
			{
				isAniming = true;
				mFirst.setVisibility(INVISIBLE);
				mSecond.setVisibility(INVISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{

			}

			@Override
			public void onAnimationEnd(Animation animation)
			{
				String firstTag = (String) mFirst.getTag();
				String secondTag = (String) mSecond.getTag();

				String[] firstParams = firstTag.split("_");
				String[] secondParams = secondTag.split("_");

				mFirst.setImageBitmap(mItemBitmaps.get(Integer
						.parseInt(secondParams[0])).bitmap);
				mSecond.setImageBitmap(mItemBitmaps.get(Integer
						.parseInt(firstParams[0])).bitmap);

				mFirst.setTag(secondTag);
				mSecond.setTag(firstTag);

				mFirst.setVisibility(VISIBLE);
				mSecond.setVisibility(VISIBLE);
				mFirst = mSecond = null;
				mAnimLayout.removeAllViews();
				checkSuccess();
				isAniming = false;
			}
		});

	}

	/**
	 * �ж���Ϸ�Ƿ�ɹ�
	 */
	private void checkSuccess()
	{
		boolean isSuccess = true;
		for (int i = 0; i < mGamePintuItems.length; i++)
		{
			ImageView first = mGamePintuItems[i];
			Log.e("TAG", getIndexByTag((String) first.getTag()) + "");
			if (getIndexByTag((String) first.getTag()) != i)
			{
				isSuccess = false;
			}
		}

		if (isSuccess)
		{
			
			Toast.makeText(getContext(), "�ɹ�",
					Toast.LENGTH_LONG).show();
			Game_Activity.callNext();
			//nextLevel();
		}
	}

	public void nextLevel()
	{
		this.removeAllViews();
		mAnimLayout = null;
		mColumn++;
		initBitmap();
		initItem();
	}
	public void restartGame()
	{
		this.removeAllViews();
		mAnimLayout = null;
		initBitmap();
		initItem();
	}

	/**
	 * ���ͼƬ����������
	 * 
	 * @param tag
	 * @return
	 */
	private int getIndexByTag(String tag)
	{
		String[] split = tag.split("_");
		return Integer.parseInt(split[1]);
	}

	private int getImageIndexByTag(String tag)
	{
		String[] split = tag.split("_");
		return Integer.parseInt(split[0]);

	}

	/**
	 * ����������
	 */
	private void setUpAnimLayout()
	{
		if (mAnimLayout == null)
		{
			mAnimLayout = new RelativeLayout(getContext());
			addView(mAnimLayout);
		}

	}

}
