package chau.voipapp;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class SipService extends IntentService{

	private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
	
	public SipService() {
		super("chau.voipapp");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		updateNoti(intent);
		builder.setOngoing(true);
		builder.setPriority(NotificationCompat.PRIORITY_MAX);
		if(action.equals(MainActivity.ACTION_ONLINE))
		{
			builder.setContentText("ONLINE!").setSmallIcon(R.drawable.online);
			issueNotification(builder);
		}
		else if(action.equals(MainActivity.ACTION_OFFLINE))
		{
			builder.setContentText("OFFLINE!").setSmallIcon(R.drawable.offline);
			issueNotification(builder);
		}
		else if(action.equals(MainActivity.FAILED))
		{
			builder.setContentText("FAILED!").setSmallIcon(R.drawable.offline);
			issueNotification(builder);
		}
		else if(action.equals(MainActivity.ACTION_CONNECTING))
		{
			builder.setContentText("CONNECTING..").setSmallIcon(R.drawable.offline);
			issueNotification(builder);
		}
		
	}
	
	private void updateNoti(Intent intent)
	{
		builder = 
				new NotificationCompat.Builder(this)
        		.setSmallIcon(R.drawable.offline)
        		.setContentTitle("World Phone")
        		.setContentText("Hello!");
		Intent resultIntent = new Intent(this, MainActivity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
				PendingIntent.getActivity(
		                 this,
		                 0,
		                 resultIntent,
		                 PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(resultPendingIntent);
		builder.setOngoing(true);
		builder.setPriority(NotificationCompat.PRIORITY_MAX);
		issueNotification(builder);
	}
	
	private void issueNotification(NotificationCompat.Builder builder) {
        mNotificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        // Including the notification ID allows you to update the notification later on.
        mNotificationManager.notify(28, builder.build());
    }
}
