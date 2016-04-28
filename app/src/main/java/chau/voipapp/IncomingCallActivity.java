package chau.voipapp;

import java.io.File;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.sip.SipAudioCall;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class IncomingCallActivity extends Activity {

	public static Context cont;
	
	public static SipAudioCall incomingCall = null;
	AudioManager am;
	static String name;
	
	private static long startTime = 0;
	private static long stopTime = 0;
	
	TextView tvTime;
	ImageButton btnHold, btnSpeaker;
	Button btnEndCall;
	View.OnClickListener listenerHold, listenerSpeaker, listenerEndcall;
	
	boolean holdIsPressed = true;
	boolean speakerIsPressed = true;
	
	NotificationManager mNotificationManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_on_calling);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		initWiget();
		initListener();
		
		cont = this;
		
		SipInit.startTime = 0;
		SipInit.stopTime = 0;

		Intent i = getIntent();
		Bundle bundle = i.getBundleExtra("ADD");
		name = bundle.getString("INCOM");
		Toast.makeText(getApplicationContext(), name, Toast.LENGTH_LONG).show();
		
		btnHold.setOnClickListener(listenerHold);
		btnSpeaker.setOnClickListener(listenerSpeaker);
		btnEndCall.setOnClickListener(listenerEndcall);
		
		
		am = (AudioManager)getSystemService(AUDIO_SERVICE);
		am.setMode(AudioManager.MODE_RINGTONE);
		
		String ns = NOTIFICATION_SERVICE;
		mNotificationManager = (NotificationManager)getSystemService(ns);
		
		takeCall();
	}
	
	/**
	 * There's something you weren't able to see..
	 * Such as Tomorrow
	 */
	// IncomingCallReceiver
	public static void endMagic()
	{
		((Activity) cont).finish();
	}
	
	public void initWiget()
	{
		btnHold = (ImageButton)findViewById(R.id.btnHold);
		btnSpeaker = (ImageButton)findViewById(R.id.btnSpeaker);
		btnEndCall = (Button)findViewById(R.id.btnEndCall);
	}
	
	public void initListener()
	{
		listenerHold = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(holdIsPressed)
				{
					btnHold.setImageResource(R.drawable.play);
					try
					{
						if(incomingCall != null && incomingCall.isInCall())
						{
							incomingCall.holdCall(0);
						}
					}
					catch(Exception e)
					{
						
					}
				}
				else
				{
					btnHold.setImageResource(R.drawable.pause);
					try
					{
						incomingCall.continueCall(0);
					}
					catch(Exception e)
					{
						
					}
				}
				holdIsPressed = !holdIsPressed;
			}
		};
		
		listenerSpeaker = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(speakerIsPressed)
				{
					btnSpeaker.setImageResource(R.drawable.speaker);
					if(am == null)
					{
						Context c = getApplicationContext();
						am = (AudioManager)c.getSystemService(Context.AUDIO_SERVICE);
					}
					am.setSpeakerphoneOn(true);
					SipInit.speakerPhone = true;
					Toast.makeText(getApplicationContext(), "Loudspeaker on", Toast.LENGTH_SHORT).show();
				}
				else
				{
					btnSpeaker.setImageResource(R.drawable.speakerx);
					if(am == null)
					{
						Context c = getApplicationContext();
						am = (AudioManager)c.getSystemService(Context.AUDIO_SERVICE);
					}
					am.setSpeakerphoneOn(false);
					SipInit.speakerPhone = false;
					Toast.makeText(getApplicationContext(), "Loudspeaker off", Toast.LENGTH_SHORT).show();
				}
				speakerIsPressed = !speakerIsPressed;
			}
		};
		
		listenerEndcall = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				endCurrentCall();
				finish();
			}
		};
	}
	
	@SuppressLint("SimpleDateFormat")
	public void takeCall()
	{
		try
		{
			incomingCall.answerCall(0);
			incomingCall.startAudio();
			startTime = SystemClock.elapsedRealtime();
			SimpleDateFormat sdf = new SimpleDateFormat("H:mmaa   EEEE, dd MM, yyyy");
			SipInit.callDate = sdf.format(new Date());
			SipInit.outgoingCall = false;
			SipInit.callHeld = false;
			incomingCall.setSpeakerMode(false);
			am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
			am.setMode(AudioManager.MODE_IN_COMMUNICATION);	
			if(SipInit.speakerPhone)
			{
				am.setSpeakerphoneOn(true);
			}
		}
		catch(Exception e)
		{
			
		}
	}
	
	public static void endCurrentCall()
	{
		if(incomingCall != null && incomingCall.isInCall())
		{
			try
			{
				Toast.makeText(cont, String.valueOf(incomingCall.isInCall()), Toast.LENGTH_LONG).show();
				stopTime = SystemClock.elapsedRealtime();
				long miliSecs = stopTime - startTime;
				int sec = (int)(miliSecs / 1000) % 60;
				int mins = (int)(miliSecs / (1000 * 60)) % 60;
				int hrs = (int)(miliSecs / (1000 * 60 * 60));
				StringBuilder builder = new StringBuilder(64);
				if(hrs > 0)
				{
					builder.append(hrs);
					if(hrs > 1)
						builder.append(" hrs ");
					else builder.append(" hr ");
				}
				builder.append(mins);
				if(mins > 1)
					builder.append(" mins ");
				else builder.append(" min ");
				builder.append(sec);
				if(sec > 1)
					builder.append(" secs ");
				else builder.append(" sec ");
				
				SipInit.callDuration = builder.toString();
				HistoryInfo info = new HistoryInfo(SipInit.sipTarget.getUserName(),
	            		SipInit.sipTarget.getDisplayName(), 
	            		SipInit.callDate, SipInit.callDuration, SipInit.outgoingCall, false);
				SipInit.file=new File(SipInit.PATH + SipInit.FILENAME);
	            if(!SipInit.file.exists())
	            {
	            	SipInit.fos = cont.openFileOutput(SipInit.FILENAME, Context.MODE_APPEND);
	            	SipInit.oos=new ObjectOutputStream(SipInit.fos);
	            }
	            else
	            {
	            	SipInit.fos = cont.openFileOutput(SipInit.FILENAME, Context.MODE_APPEND);
	            	SipInit.oos = new AppendableObjectOutputStream(SipInit.fos);
	            }
	            
	            SipInit.oos.writeObject(info);
	            SipInit.oos.flush();
	            SipInit.fos.close();
	            SipInit.oos.close();
				startTime = 0;
				
				AudioManager am;
				am = (AudioManager)cont.getSystemService(Context.AUDIO_SERVICE);
				am.setMode(AudioManager.MODE_NORMAL);
				am.setSpeakerphoneOn(false);
				incomingCall.endCall();
			}
			catch(Exception e)
			{			
			}
		}
		else
		{
			
		}
		incomingCall.close();
	}
}
