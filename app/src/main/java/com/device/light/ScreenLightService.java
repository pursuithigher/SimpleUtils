package com.device.light;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.util.Log;

/**
 * 状态感应器，感应手机手否被遮挡，如果被遮挡暗屏
 */
public class ScreenLightService implements SensorEventListener{

	WeakReference<Activity> act = null;
	SensorManager sm ;
	Sensor isensor ;
	final static int HEARDMODE = 200;
	final static int EYEMODE = 400;
	private boolean isInHeard = false;
	private PowerManager.WakeLock localWakeLock = null;//电源锁
	private float currentValue = -1;

	/**
	 * initial
	 * @param activity
     */
	public ScreenLightService(Activity activity){
		act = new WeakReference<>(activity);
		sm = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
		PowerManager localPowerManager = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
		localWakeLock = localPowerManager.newWakeLock(32, "MyPower");
	}

	/**
	 * add listener
	 */
	public void registerSensorListener(){
		isensor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		sm.registerListener(this, isensor , SensorManager.SENSOR_DELAY_NORMAL);
	}

	/**
	 * remove listener
	 */
	public void unregisterListener(){
		if(localWakeLock.isHeld())
		{
			localWakeLock.setReferenceCounted(false);
			localWakeLock.release(); // 释放设备电源锁
		}
		sm.unregisterListener(this ,isensor);
	}
	
	private void setScreenOn(){
		Activity activity = act.get();
		if(activity != null)
		{
			if (localWakeLock.isHeld()) {
			} else{
                localWakeLock.acquire();// 申请设备电源锁
            }
		}
	}
	
	private void setScreenOff(){
		Activity activity = act.get();
		if(activity != null)
		{
			if (!localWakeLock.isHeld()) {
			} else{
                localWakeLock.setReferenceCounted(false);
                localWakeLock.release(); // 释放设备电源锁
            }
		}
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		float lastValue = event.values[0];
		Log.d("setScreen value", String.valueOf(lastValue));
		if(currentValue == -1)
		{
			currentValue = lastValue;
		}else{
			if(!isInHeard && currentValue - lastValue > 0)
			{
				setScreenOn();
				isInHeard = true;
			}else if(isInHeard && currentValue - lastValue < 0){
				setScreenOff();
				isInHeard = false;
			}
			currentValue = lastValue;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

}
