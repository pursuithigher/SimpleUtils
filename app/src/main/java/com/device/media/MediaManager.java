package com.device.media;

import android.content.Context;
import android.media.AudioManager;
import android.provider.Settings;
import android.util.Log;


public class MediaManager
{
	private final String TAG = MediaManager.class.toString();

	private AudioManager audioManager;
	private Ringer ringer;

	public MediaManager(Context context)
	{
		audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

		ringer = new Ringer(context);
	}

	/**
	 * Start ringing announce for a given contact.
	 * @param remoteContact
	 */
	synchronized public void startRing(String remoteContact, Context context)
	{
		Log.d(TAG, "start incoming Ring()");
		if (!ringer.isRinging())
		{
			Log.i(TAG, "incoming ringer initial");
			//Application.getInstance().setSpeakerphoneOn(false);
			ringer.ring(remoteContact, Settings.System.DEFAULT_RINGTONE_URI.toString());
		} else
		{
			Log.w(TAG, "incoming ringer already ringing ... ");
		}
	}
	
	/**
	 * start calling ring use media player
	 * @param context
	 */
	synchronized public void startCallingRing(Context context)
	{
		Log.i(TAG, "enter startCallingRing()");
		if (!ringer.isCallingRing())
		{
			Log.i(TAG, "calling initial");
			setSpeakerphoneOn(false);
			ringer.playCallingRing(context);
		}
	}
	

	/**
	 * Stop call ringing Warning, this will not unfocus audio
	 */
	public synchronized void stopRing()
	{
		if (ringer.isIncomingRing())
		{
			ringer.stopRing();
		}
		if(ringer.isCallingRing())
		{
			ringer.stopCallingRing();
		}
	}

	public void setSpeakerphoneOn(boolean isopen)
	{
		if(isopen && !audioManager.isSpeakerphoneOn())
		{
			audioManager.setSpeakerphoneOn(true);
		}
		else if(!isopen && audioManager.isSpeakerphoneOn())
		{
			audioManager.setSpeakerphoneOn(false);
		}
	}

	public void playMsgSound(boolean isIncoming){
		ringer.msgSoundOnce(isIncoming);
	}

}
