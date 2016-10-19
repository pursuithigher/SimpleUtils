package com.views.ui.customviewgroup;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Display;
import android.widget.RelativeLayout;

/**
 * 该Viewgroup用来判断软件盘是否弹出
 */
public class InputMethodLayout extends RelativeLayout
{

	private int width;
	private int height;
	protected OnSizeChangeListener onSizeChangeListener;
	private int screenHeight;

	@SuppressWarnings("deprecation")
	public InputMethodLayout(Context context, AttributeSet attributeSet)
	{
		super(context, attributeSet);

		Display localDisplay = ((Activity) context).getWindowManager().getDefaultDisplay();
		this.screenHeight = localDisplay.getHeight();
	}

	public InputMethodLayout(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		this.width = widthMeasureSpec;
		this.height = heightMeasureSpec;
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		if ((this.onSizeChangeListener != null) && (w == oldw) && (oldw != 0) && (oldh != 0))
		{
			boolean sizeChange = false;
			if ((h >= oldh) || (Math.abs(h - oldh) <= this.screenHeight / 4))
			{
				if ((h <= oldh) || (Math.abs(h - oldh) <= this.screenHeight / 4))
				{
					return;
				}
				sizeChange = false;
			} else
			{
				sizeChange = true;
			}
			this.onSizeChangeListener.onSizeChange(sizeChange, oldh, h);
			measure(this.width - w + getWidth(), this.height - h + getHeight());
		}
	}

	public void setOnSizeChangeListener(OnSizeChangeListener onSizeChangeListener)
	{
		this.onSizeChangeListener = onSizeChangeListener;
	}

	public interface OnSizeChangeListener
	{
		void onSizeChange(boolean param, int w, int h);
	}
}
