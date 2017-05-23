package com.device.media;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;


import com.views.simpleutils.R;

import java.util.Calendar;
import java.util.Date;

/**
 * 管理振动，来电铃声，去电音乐
 */
public class Ringer
{
	private static final String TAG = Ringer.class.toString();

	private static final int VIBRATE_LENGTH = 1000; // ms
	private static final int PAUSE_LENGTH = 1000; // ms
	private static final int SHAKE_LENGTH = 200; // ms

	Context context;

//	Ringtone ringtone = null;
	Vibrator vibrator;
	volatile VibratorThread vibratorThread;
//	volatile RingerThread ringerThread;
	volatile ShakeThread shakeThread;

	public Ringer(Context aContext)
	{
		context = aContext;
		vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
	}

	public void ring(String remoteContact, String defaultRingtone)
	{
		synchronized (this)
		{
			Log.d(TAG, " TAG, [Ringer.java] default ringtone : " + defaultRingtone);

			AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//			audioManager.setMode(AudioManager.MODE_RINGTONE);
			// No ring no vibrate
			int ringerMode = audioManager.getRingerMode();

			if (ringerMode == AudioManager.RINGER_MODE_SILENT)
			{
				Log.d(TAG, " TAG, skipping ring and vibrate because profile is silent");
				return;
			}
			
			// Vibrate
			//int vibrateSetting = audioManager.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER);
			if(!vibrator.hasVibrator())
			{
				Log.w(TAG, "device has no vibrator hardware");
				return ;
			}
			
			if (ringerMode == AudioManager.RINGER_MODE_VIBRATE)////(vibrateSetting == AudioManager.VIBRATE_SETTING_ON || )
			{
				if (vibratorThread == null)
				{
					vibratorThread = new VibratorThread();
					Log.i(TAG, " TAG, Starting vibrator...");
					vibratorThread.start();
				}
			}
			
			if(ringerMode == AudioManager.RINGER_MODE_NORMAL)
			{
				Log.i(TAG, "incoming ringerMode = Normal");
				playingIncomingRing(remoteContact,defaultRingtone);
			}
		}
	}
	
	public void stopRing()
	{
		synchronized (this)
		{
			Log.d(TAG, " =====> stopRing() called...");

			stopVibrator();
			stopIncomingRing();
			stopCallingRing();
		}
	}

	public boolean isRinging()
	{
		if(iMediaPlayer != null) {
			if (iMediaPlayer.isPlaying()) {
				return true;
			} else {
				iMediaPlayer.release();
				return false;
			}
		}
		if(mMediaPlayer != null)
		{
			if (mMediaPlayer.isPlaying()) {
				return true;
			} else {
				mMediaPlayer.release();
				return false;
			}
		}
		if(vibratorThread != null)
			if(vibratorThread.isAlive())
			{
				if(vibrator.hasVibrator())
					return true;
			}else{
				if(vibrator.hasVibrator())
				{
					vibrator.cancel();
				}
				return false;
			}
		return false;
	}

	/*****************************  Calling MediaPlayer ***************************************/
	MediaPlayer mMediaPlayer = null;
	public boolean isCallingRing(){
		return mMediaPlayer != null;
	}
	
	/**
	 * play calling ring media ,that has been loading prepared
	 * @param t
	 */
	public void playCallingRing(Context t){
		if(mMediaPlayer==null)
		{
			Log.i(TAG, "mMediaPlayer!=null && playCallingRing");
			AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			audioManager.setSpeakerphoneOn(false);
			audioManager.setMode(AudioManager.MODE_IN_CALL);
			mMediaPlayer = MediaPlayer.create(context, R.raw.callring);
//			audioManager.setStreamVolume(AudioManager.MODE_IN_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)/2 +1, AudioManager.FLAG_ALLOW_RINGER_MODES);
			mMediaPlayer.setLooping(true);
			mMediaPlayer.start();
		}
		else{
			Log.w(TAG, "mMediaPlayer==null && calling ring has be active");
		}
	}
	
	/**
	 * stop the calling ring if has start and release it 
	 */
	public void stopCallingRing(){
		if(mMediaPlayer !=null)
		{
			if(mMediaPlayer.isPlaying())
			{
				mMediaPlayer.stop();
				mMediaPlayer.release();
			}
			else{
				Log.w(TAG, "calling ring has be stoped");
				mMediaPlayer.release();
			}
			mMediaPlayer = null;
		}else{
			Log.d(TAG, "calling ring has be null");
		}
	}

	/*****************************  Incoming MediaPlayer ***************************************/
	private MediaPlayer iMediaPlayer = null;
	public boolean isIncomingRing(){
		return iMediaPlayer != null || vibratorThread != null;
	}
	public void playingIncomingRing(String remoteContact, final String defaultRingtone){
		if(iMediaPlayer==null)
		{
			Uri ringtoneUri = Settings.System.DEFAULT_RINGTONE_URI;//Uri.parse(defaultRingtone);
			iMediaPlayer =  new MediaPlayer();
			try {
				iMediaPlayer.setDataSource(context, ringtoneUri);
			} catch (Exception e) {
				e.printStackTrace();
			}
			iMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
			iMediaPlayer.setLooping(true);
			iMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					// TODO Auto-generated method stub
					return false;
				}
			});
			iMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					// TODO Auto-generated method stub

					mp.start();
				}
			});
			try {
				iMediaPlayer.prepareAsync();
				Log.i(TAG, "start incoming mediaplayer");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				iMediaPlayer = null;
				e.printStackTrace();
			}
		}
		else{
			Log.w(TAG, "incoming ring has be active");
		}
	}

	public void stopIncomingRing(){
		if(iMediaPlayer !=null)
		{
			if(iMediaPlayer.isPlaying())
			{
				iMediaPlayer.stop();
				iMediaPlayer.release();
			}
			else{
				iMediaPlayer.release();
			}
			Log.i(TAG, "incoming ring set null");
			iMediaPlayer = null;
		}else{
			Log.d(TAG, "calling ring has be null");
		}
	}

	/*****************************  Vibrator ***************************************/
	private class VibratorThread extends Thread
	{
		@Override
		public void run()
		{
			try
			{
				while (!isVibratorThreadGoOn)
				{
					vibrator.vibrate(VIBRATE_LENGTH);
					Thread.sleep(VIBRATE_LENGTH + PAUSE_LENGTH);
				}
			} catch (InterruptedException e)
			{
				isVibratorThreadGoOn = false ;
				vibrator.cancel();
			} finally
			{
				isVibratorThreadGoOn = false ;
			}
		}
	}

	static boolean isVibratorThreadGoOn = false;
	private void stopVibrator()
	{
		if (vibratorThread != null && vibratorThread.isAlive())
		{
			isVibratorThreadGoOn = true;
			vibratorThread= null;
		}
		else if(vibratorThread != null && !vibratorThread.isAlive())
		{
			vibratorThread = null;
		}
	}

	/*****************************  Shaker ***************************************/
	private class ShakeThread extends Thread
	{
		@Override
		public void run()
		{
			try
			{
				if(!isVibratorThreadGoOn)
					vibrator.vibrate(SHAKE_LENGTH);
			} catch (Exception e)
			{
				vibrator.cancel();
			} finally
			{
				shakeThread = null;
			}
		}
	}

	/*****************************  Msg player ***************************************/
	/*****************************  特定时间点免打扰，存于Preference中 ***************************************/
	private MediaPlayer iplayer;
	public void msgSoundOnce(boolean iscoming){
//		boolean isdisturb = false;
//		int from = PreferencesUtil.getIntance(context).getMsgDisturbFrom();
//		if(from != -1) {
//			int to = PreferencesUtil.getIntance(context).getMsgDisturbTo();
//			Calendar  calendar = Calendar.getInstance();
//			calendar.setTime(new Date(System.currentTimeMillis()));
//			int hour = calendar.get(Calendar.HOUR_OF_DAY);
//			if(hour>=from && hour<=to)
//				isdisturb = true;
//		}
//		if(iscoming)
//		{
//			if(isdisturb)
//				return ;
//			boolean hasSound = PreferencesUtil.getIntance(context).getMsgSound();
//			if(hasSound) {
//				if(iplayer!= null && iplayer.isPlaying())
//				{
//					// media has playing
//				}else {
//					iplayer = MediaPlayer.create(context, R.raw.msg_coming);
//					iplayer.setOnCompletionListener(completionListener);
//					play(iplayer);
//				}
//			}
//			boolean hasShake = PreferencesUtil.getIntance(context).getMsgShake();
//			if(shakeThread == null) {
//				if (hasShake) {
//					shakeThread = new ShakeThread();
//					shakeThread.start();
//				}
//			}
//		}
//		else
//		{
//			MediaPlayer iplayer=MediaPlayer.create(context, R.raw.msg_send);
//			play(iplayer);
//		}
	}

	private MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer mp) {
			mp.release();
			iplayer = null;
		}
	};

	private void play(final MediaPlayer iplayer){
		if(iplayer!=null)
		{
			if(!iplayer.isPlaying())
			{
				iplayer.start();
			}
		}
	}
}
