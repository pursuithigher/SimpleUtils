package com.views.ui.customview.slidbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


/**
 * Created by banthalos on 5/11/15.
 */
public class SliderBar extends View
{

	private OnTouchingLetterChangedListener onTouchingLetterChangedListener;

	public static String[] b = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z", "#" };
	private int choose = -1;
	private Paint paint = new Paint();

	public SliderBar(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public SliderBar(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public SliderBar(Context context)
	{
		super(context);
	}

	/**
	 * 重写这个方法
	 */
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		if(b.length==0)
			return  ;
		
		
		int height = getHeight();
		int width = getWidth();
		int singleHeight = height / (b.length+1);

		for (int i = 0; i < b.length; i++)
		{
			paint.setColor(Color.rgb(33, 65, 98));
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			paint.setAntiAlias(true);
			int textSize = 25;
			paint.setTextSize(textSize);
			if (i == choose)
			{
				paint.setColor(Color.parseColor("#3399ff"));
				paint.setFakeBoldText(true);
			}
			float xPos = width / 2 - paint.measureText(b[i]) / 2;
			float yPos = singleHeight * i + singleHeight;
			canvas.drawText(b[i], xPos, yPos, paint);
			paint.reset();
		}

	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean dispatchTouchEvent(MotionEvent event)
	{
		final int action = event.getAction();
		final float y = event.getY();
		final int oldChoose = choose;
		final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
		final int c = (int) (y / getHeight() * (b.length+1));

		switch (action)
		{
		case MotionEvent.ACTION_UP:
			setBackgroundDrawable(new ColorDrawable(0x00000000));
			choose = -1;//
			if(listener!=null)
				listener.cancelDigIcon();
			invalidate();

			break;

		default:
			// setBackgroundResource(R.drawable.sidebar_background);
			if (oldChoose != c)
			{
				if (c >= 0 && c < b.length)
				{
					if (listener != null)
					{
						listener.onTouchingLetterChanged(b[c],y);
					}

					choose = c;
					invalidate();
				}
			}

			break;
		}
		return true;
	}

	public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener onTouchingLetterChangedListener)
	{
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}

	public interface OnTouchingLetterChangedListener
	{
		void onTouchingLetterChanged(String s, float y);
		void cancelDigIcon();
	}
	

	/**
	 * you must has sort the parameter before call this FUNC
	 * @param dates
	 */
//	public void nodifyDateUiChange(List<SortModel> dates,View view){
//		if(dates==null)
//		{
//			b=new String[]{};
//			this.invalidate();
//			view.setVisibility(b.length==0?View.VISIBLE:View.GONE);
//			return ;
//		}
//
//		ArrayList<String> newdates=new ArrayList<String>();
//		int length=dates.size();
//		String single="";
//		for(int i=0;i<length;i++)
//		{
//			if(i==0)
//			{
//
//				single=dates.get(i).getSortLetter();
//				if(SimpleUtils.isAlpha(single))
//				{
//					newdates.add(single);
//				}
//				else{
//					newdates.add("#");
//				}
//			}
//			else{
//				String char2=dates.get(i).getSortLetter();
//				if(SimpleUtils.isAlpha(char2))
//				{
//					if(!single.equals(char2))
//					{
//						newdates.add(char2);
//						single=char2;
//					}
//				}
//				else{
//					newdates.add("#");
//					break;
//				}
//			}
//		}
//
//		String[] a=new String[newdates.size()];
//		newdates.toArray(a);
//		b=a;
//		view.setVisibility(b.length==0?View.VISIBLE:View.GONE);
//		this.invalidate();
//	}
	
}
