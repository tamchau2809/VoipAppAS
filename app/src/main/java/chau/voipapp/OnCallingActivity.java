package chau.voipapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.sip.SipAudioCall;
import android.net.sip.SipProfile;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

@SuppressLint("SdCardPath")
public class OnCallingActivity extends Activity {

	ImageButton btnHold, btnSpeaker;
	Button btnEndCall;
	
	MediaPlayer mP;
	Context ctx;
	
	File file;
    FileOutputStream fos;
    ObjectOutputStream oos;
    String FILENAME = "history";
    String PATH = "/data/data/chau.voipapp/files/";
    SipAudioCall.Listener listener;
	
    public AudioManager am;
	
	View.OnClickListener listenerHold, listenerSpeaker, listenerEnd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_on_calling);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		initWiget();
		initEvent();
		
		ctx = getApplicationContext();
		
		SipInit.speakerPhone = false;
		
		SipInit.startTime = 0;
		SipInit.stopTime = 0;
		
		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.setSpeakerphoneOn(false);
		
		btnEndCall.setOnClickListener(listenerEnd);
		btnHold.setOnClickListener(listenerHold);
		btnSpeaker.setOnClickListener(listenerSpeaker);
		
		initCall();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (SipInit.call!=null && SipInit.call.isInCall())
    	{
    		endCurrentCall();
    	}
	}
	
	@SuppressLint("SimpleDateFormat")
	public void initCall()
	{
		try {
            listener = new SipAudioCall.Listener() {
                @Override
                public void onCallEstablished(final SipAudioCall call) 
                {
                	if(call!=null && call.isInCall())
                	{
                		if(!SipInit.callHeld)
                		{
                			SipInit.callHeld=true;
                			return;
                		}
                		else
                		{
                			SipInit.callHeld=false;
                			return;
                		}
                	}
                	
                	SipInit.startTime=SystemClock.elapsedRealtime();
                	SimpleDateFormat sdf = new SimpleDateFormat("H:mmaa   EEEE, MMMM d, yyyy");
                	SipInit.callDate = sdf.format(new Date());
                	SipInit.outgoingCall=true;
                	SipInit.callHeld=false;
                    call.startAudio();
                    call.setSpeakerMode(true);
                    final Context c=getApplicationContext();
                    if(SipInit.speakerPhone)
                    {
                    	am = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);
                    	am.setSpeakerphoneOn(true);
                    }
                    am.setMode(AudioManager.MODE_IN_COMMUNICATION);
                }
                
                @Override
                public void onCalling(SipAudioCall call)
                {
                	Toast.makeText(getApplicationContext(), "Đang Gọi!!", Toast.LENGTH_LONG).show();
                	
                }
                
                @Override
                public void onRinging(SipAudioCall call, SipProfile caller)
                {
                	Toast.makeText(getApplicationContext(), "Đổ Chuông!!", Toast.LENGTH_LONG).show();
                	
                }
                
                @Override
                public void onCallHeld(SipAudioCall call)
                {
                	SipInit.callHeld = true;
                	super.onCallHeld(call);
                }
                
                @Override
                public void onRingingBack(SipAudioCall call) {
                	// TODO Auto-generated method stub
//            		mP = MediaPlayer.create(getApplicationContext(), R.raw.ringback);
//            		mP.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
////                    mP.setLooping(true);
//                    mP.start();
                }

                @Override
                public void onCallEnded(SipAudioCall call) 
                {
                    try
                    {
                    	SipInit.stopTime=SystemClock.elapsedRealtime();
                		long milliseconds=SipInit.stopTime-SipInit.startTime;
                		int seconds = (int) (milliseconds / 1000) % 60 ;
                		int minutes = (int) ((milliseconds / (1000*60)) % 60);
                		int hours   = (int) ((milliseconds / (1000*60*60)));
                		StringBuilder sb = new StringBuilder(64);
                		if(hours>0)
                		{
                			sb.append(hours);
                			sb.append(" hrs ");
                		}
                		sb.append(minutes);
            			sb.append(" mins ");
            			sb.append(seconds);
            			sb.append(" secs");
            			SipInit.callDuration = sb.toString();
            			SipInit.startTime=0;
            			
            			HistoryInfo info = new HistoryInfo(SipInit.sipTarget.getUserName(),
                        		SipInit.sipTarget.getDisplayName(), 
                        		SipInit.callDate, SipInit.callDuration, SipInit.outgoingCall, false);
                		
                        Context c = getApplicationContext();
                        am = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);
                        am.setMode(AudioManager.MODE_NORMAL);
                        am.setSpeakerphoneOn(false);
                		
                		file=new File(PATH+FILENAME);
                        if(!file.exists())
                        {
                        	fos = openFileOutput(FILENAME, Context.MODE_APPEND);
                        	oos=new ObjectOutputStream(fos);
                        }
                        else
                        {
                        	fos = openFileOutput(FILENAME, Context.MODE_APPEND);
                        	oos=new AppendableObjectOutputStream(fos);
                        }
                        
                		oos.writeObject(info);
                		oos.flush();
                    	fos.close();
                    	oos.close();
                    	((Activity) getBaseContext()).finish();
                    }
                    catch(Exception e)
                    {
                    }
                }
                
                @Override
                public void onError(SipAudioCall call, final int errorCode, final String errorMessage)
                {
                	runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Đã Có Lỗi!!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    finish();
                }
                
                @Override
                public void onCallBusy(SipAudioCall call)
                {
                	runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Máy Bận!!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    finish();
                }
            };
            SipInit.sipTarget = new SipProfile.Builder(Keyboard.numCall, 
					SipInit.profile.getSipDomain()).build();
			SipInit.call = SipInit.sipManager.makeAudioCall(
					SipInit.profile.getUriString(), 
					SipInit.sipTarget.getUriString(), 
					listener, 0);
			SipInit.call.setListener(listener, true);

        }
		catch(Exception e)
		{
			if(SipInit.profile != null)
			{
				try
				{
					SipInit.sipManager.close(SipInit.profile.getUriString());
					Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
//					finish();
				}
				catch(Exception ex)
				{}
				if(SipInit.call != null)
				{
					SipInit.call.close();
					Toast.makeText(getApplicationContext(), "2", Toast.LENGTH_LONG).show();
				}
			}
			finish();
		}
    }
	
	public void initEvent()
	{
		listenerEnd = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				finish();
				try
				{
				endCurrentCall();
				finish();
				}
				catch(Exception e) {}
			}
		};
		
		listenerHold = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(SipInit.holdIsPressed)
				{
					btnHold.setImageResource(R.drawable.play);
					try
					{
						if(SipInit.call != null && SipInit.call.isInCall())
						{
							SipInit.call.holdCall(0);
						}
					}
					catch(Exception e)
					{}
				}
				else
				{
					btnHold.setImageResource(R.drawable.pause);
					try
					{
						SipInit.call.continueCall(0);
					}
					catch(Exception e)
					{}
				}
				SipInit.holdIsPressed = !SipInit.holdIsPressed;
			}
		};
		
		listenerSpeaker = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(SipInit.speakerIsPressed)
				{
					btnSpeaker.setImageResource(R.drawable.speaker);
					if(am == null)
					{
						am = (AudioManager)getApplicationContext()
								.getSystemService(Context.AUDIO_SERVICE);
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
						am = (AudioManager)getApplicationContext()
								.getSystemService(Context.AUDIO_SERVICE);
					}
					am.setSpeakerphoneOn(false);
					SipInit.speakerPhone = false;
					Toast.makeText(getApplicationContext(), "Loudspeaker off", Toast.LENGTH_SHORT).show();
				}
				SipInit.speakerIsPressed = !SipInit.speakerIsPressed;
			}
		};
	}
		
	@Override
	public void onBackPressed() {}
	
	public void initWiget()
	{
		btnEndCall = (Button)findViewById(R.id.btnEndCall);
		btnHold = (ImageButton)findViewById(R.id.btnHold);
		btnSpeaker = (ImageButton)findViewById(R.id.btnSpeaker);
	}
	
	public void endCurrentCall()
	{
		if(SipInit.call != null && SipInit.call.isInCall()) 
		{
			try
	        {
	            if(SipInit.startTime > 0 && SipInit.call.isInCall())
	            {
	            	SipInit.stopTime = SystemClock.elapsedRealtime();
	                long miliSecs = SipInit.stopTime - SipInit.startTime;
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
	                              
	                SipInit.call.endCall(); 
	                
	                SipInit.callDuration = builder.toString();  
	                HistoryInfo info = new HistoryInfo(SipInit.sipTarget.getUserName(),
	                		SipInit.sipTarget.getDisplayName(), 
	                		SipInit.callDate, SipInit.callDuration, true, false);
					file=new File(PATH + FILENAME);
	                if(!file.exists())
	                {
	                	fos = openFileOutput(FILENAME, Context.MODE_APPEND);
	                	oos=new ObjectOutputStream(fos);
	                }
	                else
	                {
	                	fos = openFileOutput(FILENAME, Context.MODE_APPEND);
	                	oos=new AppendableObjectOutputStream(fos);
	                }
	                
	        		oos.writeObject(info);
	        		oos.flush();
	            	fos.close();
	            	oos.close();
	                SipInit.startTime = 0;
	                 
	                AudioManager am;
	                am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
	                am.setMode(AudioManager.MODE_NORMAL);
	                am.setSpeakerphoneOn(false);
	            }
	        }
	        catch(Exception ex)
	        {
	            Toast.makeText(getApplicationContext(), "Oops. " + ex, Toast.LENGTH_SHORT).show();
	        }  
		}
		if(!SipInit.call.isInCall())
		{
			try
			{
				SipInit.callDuration = "0 sec";
				SimpleDateFormat sdf = new SimpleDateFormat("H:mmaa   EEEE, dd MM, yyyy");
	        	SipInit.callDate = sdf.format(new Date());
	        	
	        	SipInit.call.endCall(); 
	             
	            HistoryInfo info = new HistoryInfo(SipInit.sipTarget.getUserName(),
	            		SipInit.sipTarget.getDisplayName(), 
	            		SipInit.callDate, SipInit.callDuration, true, false);
				file=new File(PATH + FILENAME);
	            if(!file.exists())
	            {
	            	fos = openFileOutput(FILENAME, Context.MODE_APPEND);
	            	oos=new ObjectOutputStream(fos);
	            }
	            else
	            {
	            	fos = openFileOutput(FILENAME, Context.MODE_APPEND);
	            	oos=new AppendableObjectOutputStream(fos);
	            }
	            
	    		oos.writeObject(info);
	    		oos.flush();
	        	fos.close();
	        	oos.close();
	            SipInit.startTime = 0;
	             
	            AudioManager am;
	            am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
	            am.setMode(AudioManager.MODE_NORMAL);
	            am.setSpeakerphoneOn(false);
			}
			catch(Exception e)
			{}
		}
	}	
}
