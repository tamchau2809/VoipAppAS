package chau.voipapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.sip.SipAudioCall;
import android.net.sip.SipProfile;
import android.os.SystemClock;
import android.widget.Toast;

public class IncomingCallService extends IntentService{

	FileOutputStream fos;
	ObjectOutputStream oos;
	File file;
	static boolean callHeld=true;
	static boolean flag;
	
	public IncomingCallService() {
		super("Service");
		// TODO Auto-generated constructor stub
	}

	@SuppressLint({ "SimpleDateFormat", "InlinedApi" })
	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		SipAudioCall incomingcall = null;
		final Context c = getApplicationContext();
		try
		{
			SipAudioCall.Listener listener = new SipAudioCall.Listener()
			{
				@Override 
				public void onCallEstablished(final SipAudioCall call)
				{
					if(call != null && call.isInCall() && !callHeld)
					{
						callHeld = true;
					}
					else if(call != null && call.isInCall() && callHeld)
					{
						callHeld = false;
						return;
					}
				}
				
				@Override
				public void onRinging(SipAudioCall call, SipProfile caller)
				{
					try
					{
						call.answerCall(0);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				
				@Override
				public void onCallHeld(SipAudioCall call)
				{
					callHeld = false;
					super.onCallHeld(call);
				}
				
				@SuppressLint("SdCardPath")
				@Override
				public void onCallEnded(SipAudioCall call)
				{
					try
					{
						SipInit.stopTime = SystemClock.elapsedRealtime();
						long miliSecs = SipInit.stopTime - SipInit.startTime;
						int secs = (int)((miliSecs / 1000) % 60);
						int mins = (int)((miliSecs / (1000*60)) % 60);
						int hours = (int)((miliSecs / (1000*60*60)));
						StringBuilder sb = new StringBuilder(64);
						if(hours > 0)
						{
							sb.append(hours);
							sb.append(" hrs ");
						}
						sb.append(mins);
						sb.append(" mins ");
						sb.append(secs);
						sb.append(" secs ");
						SipInit.callDuration = sb.toString();
						SipInit.startTime = 0;
						HistoryActivity.SaveHisto(SipInit.sipAddress.substring(4), 
								IncomingCallMenu.peerName, 
								SipInit.callDate, SipInit.callDuration, 
								SipInit.outgoingCall, false, getApplicationContext());
						
						AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
						am.setMode(AudioManager.MODE_NORMAL);
						am.setSpeakerphoneOn(false);
						
					}
					catch(Exception e)
					{
						Toast toast=Toast.makeText(c, "Unable to enter data to file "+e, Toast.LENGTH_LONG);
                        toast.show();
					}
				}
			};
			Toast.makeText(c, "onReceive 1", Toast.LENGTH_LONG).show();
//			(incomingcall = PhoneCallActivity.sipManager.takeAudioCall(intent, null)).setListener(listener, true);
			incomingcall = SipInit.sipManager.takeAudioCall(intent, listener);
			incomingcall.startAudio();
			incomingcall.setSpeakerMode(true);
			AudioManager am;
			am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
			am.setMode(AudioManager.MODE_IN_COMMUNICATION);
			if(SipInit.speakerPhone)
			{
				am.setSpeakerphoneOn(true);
			}
			
////			if(PhoneCallActivity.walkieMode && !incomingcall.isMuted())
//			if(PhoneCallActivity.walkieMode && incomingcall.isMuted())
//			{
//				incomingcall.toggleMute();
//			}
			
			SipInit.call = incomingcall;
			SipInit.startTime = SystemClock.elapsedRealtime();
			SimpleDateFormat sdf = new SimpleDateFormat("H:mmaa   EEEE, dd mm, yyyy");
			SipInit.callDate = sdf.format(new Date());
			SipInit.sipAddress = "sip:" + incomingcall.getPeerProfile().getUserName() + "@" + incomingcall.getPeerProfile().getSipDomain();
			SipInit.outgoingCall = false;
			
			Toast.makeText(c, "onReceive 2", Toast.LENGTH_LONG).show();
		}
		catch(Exception e)
		{
			Toast toast=Toast.makeText(c, "Exception "+e, Toast.LENGTH_LONG);
            toast.show();
            if(incomingcall != null)
            {
            	incomingcall.close();
            }
		}
	}
}
