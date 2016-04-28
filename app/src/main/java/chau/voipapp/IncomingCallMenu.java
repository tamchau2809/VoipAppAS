package chau.voipapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.sip.SipAudioCall;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class IncomingCallMenu extends Activity 
{
	Button btnAccept, btnReject;
	TextView tvIncomingCompany, tvDisplayName;
	View.OnClickListener listenerAccept, listenerReject;
	
	AudioManager am;
	
	static File file;
	static FileOutputStream fos;
	static ObjectOutputStream oos;
	
	static String peerAdd;
	static String peerName;
	private static Context ctx;
	
	public static boolean missed;
	static boolean accept = true;
	private Handler mhandler = new Handler();
	
	static SipAudioCall incomingCall = null;
	SipAudioCall.Listener listener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_incoming_call_menu);
		initWiget();
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		ctx = this;
		
		initListener();
		btnAccept.setOnClickListener(listenerAccept);
		btnReject.setOnClickListener(listenerReject);
		
		am = (AudioManager)getSystemService(AUDIO_SERVICE);
		am.setMode(AudioManager.MODE_RINGTONE);
		
		missed = false;
		
		try
		{
			listener = new SipAudioCall.Listener()
			{		
				
				@Override
				public void onCallEnded(SipAudioCall call) {
					// TODO Auto-generated method stub
					missed = false;
				}
			};
			peerAdd = incomingCall.getPeerProfile().getUserName();
			
			if(peerAdd == null)
				peerAdd = incomingCall.getPeerProfile().getDisplayName();
			
			peerName = incomingCall.getPeerProfile().getDisplayName();
			
			if(peerName == null)
				peerName = "null";
			
			tvIncomingCompany.setText(peerAdd);
			tvDisplayName.setText(peerName);
			IncomingCallActivity.incomingCall = incomingCall;
			mhandler.postDelayed(myRunnable, 30000);
			
		}
		catch(Exception e)
		{}
	}
	
	public void initWiget()
	{
		btnAccept = (Button)findViewById(R.id.btnTakeCall);
		btnReject = (Button)findViewById(R.id.btnReject);
		tvIncomingCompany = (TextView)findViewById(R.id.tvIncomingCallCompany);
		tvDisplayName = (TextView)findViewById(R.id.tvDisplayName);
	}
	
	public void initListener()
	{
		listenerAccept = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				accept = false;
				Intent intent = new Intent(IncomingCallMenu.this, IncomingCallActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("INCOM", peerAdd);
				intent.putExtra("ADD", bundle);
				startActivity(intent);
				finish();
			}
		};
		
		listenerReject = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				try
				{
					mhandler.removeCallbacks(myRunnable);
					accept = false;
					am.setMode(AudioManager.MODE_NORMAL);
					incomingCall.setListener(listener, true);
					incomingCall.endCall();
					incomingCall.close();
					missedCall();
				}
				catch(Exception e)
				{}
				finish();
			}
		};
	}
	
	/**
	 * kết thúc Dialog khi người gọi tắt máy 
	 * gọi trong IncomingCallReceiver
	 * @param magic shall not prevail
	 */
	public static void AntiMage()
	{
		((Activity) ctx).finish();
	}
	
	private Runnable myRunnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try
			{
				am.setMode(AudioManager.MODE_NORMAL);
				missed = true;
				incomingCall.endCall();
				missedCall();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finish();
		}
	};
	
	@Override
	public void onBackPressed()
	{
		
	}
	
	@SuppressLint("SimpleDateFormat")
	public static void missedCall()
	{
		try
		{
			Toast.makeText(ctx, "MISS", Toast.LENGTH_LONG).show();
			int sec = 0;
			int min = 0;
			StringBuilder sb = new StringBuilder(64);
			sb.append(min);
			sb.append(" min ");
			sb.append(sec);
			sb.append(" sec ");
			SipInit.callDuration = sb.toString();
			SimpleDateFormat sdf = new SimpleDateFormat("H:mmaa   EEEE, dd MM, yyyy");
			HistoryInfo info = new HistoryInfo(peerAdd, peerName, sdf.format(new Date()).toString(), 
					SipInit.callDuration, false, true);
			
			file = new File(SipInit.PATH + SipInit.FILENAME);
			if(!file.exists())
			{
				fos = ctx.openFileOutput(SipInit.FILENAME, Context.MODE_APPEND);
				oos = new ObjectOutputStream(fos);
			}
			else
			{
				fos = ctx.openFileOutput(SipInit.FILENAME, Context.MODE_APPEND);
				oos = new AppendableObjectOutputStream(fos);
			}
			oos.writeObject(info);
			oos.flush();
			fos.close();
			oos.close();
			
			AudioManager am;
			am = (AudioManager)ctx.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
			am.setMode(AudioManager.MODE_NORMAL);
			am.setSpeakerphoneOn(false);
		}
		catch(Exception e)
		{
			Toast toast=Toast.makeText(ctx.getApplicationContext(), "Unable to enter data to file "+e, Toast.LENGTH_LONG);
			toast.show();
		}
	}
}
