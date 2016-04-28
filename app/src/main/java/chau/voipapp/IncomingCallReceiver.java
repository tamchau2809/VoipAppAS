package chau.voipapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipProfile;
import android.os.SystemClock;
import android.widget.Toast;

public class IncomingCallReceiver extends BroadcastReceiver{

	static SipAudioCall incomingCall = null;
	static long timeStart, timeStop;
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		// TODO Auto-generated method stub
		try
		{
			SipAudioCall.Listener listener = new SipAudioCall.Listener()
			{
				@Override
				public void onRinging(SipAudioCall call, SipProfile caller) {
					// TODO Auto-generated method stub
					timeStart = SystemClock.elapsedRealtime();
					try
					{
						call.answerCall(0);
					}
					catch(Exception e) {}
				}
				
				@Override
				public void onCallEnded(SipAudioCall call) {
					// TODO Auto-generated method stub
					missFromCaller();
					IncomingCallMenu.missed = false;
					timeStop = SystemClock.elapsedRealtime();
					IncomingCallMenu.AntiMage();
					IncomingCallActivity.endMagic();
					IncomingCallActivity.endCurrentCall();
				}
				
				@Override
				public void onError(SipAudioCall call, int errorCode,
						String errorMessage) {
					// TODO Auto-generated method stub
					Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
				}
			};
			
			incomingCall = SipInit.sipManager.takeAudioCall(intent, listener);
			IncomingCallMenu.incomingCall = incomingCall;
			Intent intent2 = new Intent();
			intent2.setClassName("chau.voipapp", "chau.voipapp.IncomingCallMenu");
			intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent2);
		}
		catch(Exception e)
		{
			if (incomingCall != null) {
                incomingCall.close();
			}
		}
	}
	
	public void missFromCaller()
    {
    	if(timeStop - timeStart < 30 && IncomingCallMenu.accept)
    	{
    		IncomingCallMenu.missedCall();
    	}
    }
}
