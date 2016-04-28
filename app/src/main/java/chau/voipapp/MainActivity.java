package chau.voipapp;

import java.text.ParseException;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class MainActivity extends FragmentActivity 
	implements ActionBar.TabListener{

	ViewPager viewPager;
	SectionPagerAdapter mSect;
	ActionBar actionBar;
    public IncomingCallReceiver callReceiver;
    
	
	static int numberSection;
	public static boolean backFromLogin = false;
	
	public static String text;
	
	Context ctx;
	
	static Intent mServiceIntent;
	
	public static final String ACTION_INCOMING = "chau.voipapp.ACTION_INCOMING";
	public static final String ACTION_PING = "chau.voipapp.ACTION_PING";
	public static final String ACTION_ONLINE = "chau.voipapp.ACTION_ONLINE";
    public static final String ACTION_OFFLINE = "chau.voipapp.ACTION_OFFLINE";
    public static final String EXTRA_MESSAGE= "chau.voipapp.EXTRA_MESSAGE";
    public static final String ACTION_CONNECTING = "chau.voipapp.ACTION_CONNECTING";
    public static final String FAILED= "chau.voipapp.FAILED";
    public static final String INCOMING_CALL= "chau.voipapp.INCOMING_CALL";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_main);
		
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowHomeEnabled(false);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		check();
		mSect = new SectionPagerAdapter(getSupportFragmentManager());
		
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setHomeButtonEnabled(false);
		
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(mSect);
		
		viewPager
			.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				actionBar.setSelectedNavigationItem(arg0);
				
			}
		});
		
		for(int i = 0; i < mSect.getCount(); i++)
		{
			actionBar.addTab(actionBar.newTab()
					.setText(mSect.getPageTitle(i))
					.setTabListener(this));
		}
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(INCOMING_CALL);
		callReceiver = new IncomingCallReceiver();
		this.registerReceiver(callReceiver, filter);

		mServiceIntent = new Intent(getApplicationContext(), SipService.class);
		mServiceIntent.setAction(ACTION_PING);
		startService(mServiceIntent);

		ctx = getApplicationContext();		
		initManager(ctx);
	}
	
	@Override
	protected void onStart() 
	{
		super.onStart();
//		initManager();
		if(backFromLogin)
		{
			backFromLogin = false;
			initManager(getApplicationContext());
		}
	}

	@Override
	protected void onResume() 
	{
		super.onResume();
//		initManager();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(callReceiver != null)
		{
			this.unregisterReceiver(callReceiver);
		}
	}
	
	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		viewPager.setCurrentItem(tab.getPosition());
//		invalidateOptionsMenu();
	}	

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
	}
	
	public class SectionPagerAdapter extends FragmentPagerAdapter
	{

		public SectionPagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Fragment getItem(int pos) {
			// TODO Auto-generated method stub
			switch (pos) 
			{
//            case 0:
//                // The first section of the app is the most interesting -- it offers
//                // a launchpad into the other demonstrations in this example application.
//            	return new ContactActivity();
            case 0:
            	return new ContactActivity();
            case 1:
            	return new HistoryActivity();

            default:
                // The other sections of the app are dummy placeholders.
                
                return new ContactActivity();
			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 2;
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) 
			{
//			case 0:
//				return getString(R.string.title_section1);
			case 0:
				return getString(R.string.title_section2);
			case 1:
				return getString(R.string.title_section3);
				default:
					return null;
			}
		}
	}
	//---------------------------------------------------------
	
	public static class LaunchpadSectionFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_launchpad, container, false);

            // Demonstration of a collection-browsing activity.
            rootView.findViewById(R.id.demo_collection_button)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), CollectionDemoActivity.class);
                            startActivity(intent);
                        }
                    });

            // Demonstration of navigating to external activities.
            rootView.findViewById(R.id.demo_external_activity)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Create an intent that asks the user to pick a photo, but using
                            // FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET, ensures that relaunching
                            // the application from the device home screen does not return
                            // to the external activity.
                            Intent externalActivityIntent = new Intent(Intent.ACTION_PICK);
                            externalActivityIntent.setType("image/*");
                            externalActivityIntent.addFlags(
                                    Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                            startActivity(externalActivityIntent);
                        }
                    });
            
//            mServiceIntent = new Intent(getActivity().getApplicationContext(), SipService.class);
//    		mServiceIntent.setAction(ACTION_PING);
//    		getActivity().startService(mServiceIntent);
            
            return rootView;
        }
    }
	
	public static class DummySectionFragment extends Fragment
	{
		private static final String ARG_SECTION_NUMBER = "section_number";
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState)
		{
			View rootview = inflater.inflate(R.layout.fragment_section_dummy, container, false);
			Bundle args = getArguments();
			((TextView) rootview.findViewById(android.R.id.text1)).setText(
                    getString(R.string.dummy_section_text, args.getInt(ARG_SECTION_NUMBER)));	
			return rootview;
		}
	}
	
	//------------------------------------------------------------
	/**
	 * Tạo menu Login/Edit, Sign Out, About
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.login_bottom_bar, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		if(id == R.id.action_Login)
		{
			Intent intent = new Intent(MainActivity.this, LoginActivity.class);
			startActivity(intent);
		}
		if(id == R.id.action_LogOut)
		{
			
		}
		if(id == R.id.action_About)
		{
			Intent intent = new Intent(MainActivity.this, AboutActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
	//------------------------------------------------------------
	/**
	 * kiểm tra điện thoại có hỗ
	 * trợ SIP
	 */
	public void check()
	{
		if(SipManager.isVoipSupported(getApplicationContext()))
		{
			final SharedPreferences pref = getBaseContext().getSharedPreferences("CHECK_FIRST", MODE_PRIVATE);
			if(!pref.contains("FirstTime"))
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Welcome...");
				builder.setCancelable(false);
				builder.setMessage("Hope you like it. :)");
				
				{
					builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Editor editor = pref.edit();
							editor.putBoolean("FirstTime", true);
							editor.commit();
						}
					});			
					builder.show();
				}
			}
		}
		else
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Oops..");
			builder.setMessage("Your Device does NOT SUPPORT SIP-based VOIP API");
			builder.setPositiveButton("I Agree", null);
			builder.setCancelable(false);
			builder.show();
		}
	}
	
	public static void initManager(Context ctx)
	{
		if(SipInit.sipManager == null)
		{
			SipInit.sipManager = SipManager.newInstance(ctx);
		}
		initProfile(ctx);
	}
	
	public static void initProfile(final Context ctx)
	{
		if(SipInit.sipManager == null)
		{
			return;
		}
		if(SipInit.profile != null)
		{
			closeLocalProfile();
		}
		
		SharedPreferences prefs = ctx.getSharedPreferences("LOGIN", MODE_PRIVATE);
		final String username = prefs.getString("username", "");
		String password = prefs.getString("password", "");
		String domain = prefs.getString("domain", "");
		
		if(username.length() == 0 || password.length() == 0
				|| domain.length() == 0)
			return;
		
		try
		{
			SipProfile.Builder builder = new SipProfile.Builder(username, domain);
			builder.setPassword(password);
			SipInit.profile = builder.build();
			
			Intent intent = new Intent();
			intent.setAction(INCOMING_CALL);
			PendingIntent pi = PendingIntent.getBroadcast(ctx, 0, intent, Intent.FILL_IN_DATA);
			SipInit.sipManager.open(SipInit.profile, pi, null);
			SipInit.sipManager.setRegistrationListener(SipInit.profile.getUriString(), new SipRegistrationListener() {
				
				@Override
				public void onRegistrationFailed(String localProfileUri, int errorCode,
						String errorMessage) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(ctx, SipService.class);
					intent.setAction(FAILED);
					ctx.startService(intent);

				}
				
				@Override
				public void onRegistrationDone(String localProfileUri, long expiryTime) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(ctx, SipService.class);
					intent.setAction(ACTION_ONLINE);
					ctx.startService(intent);

				}
				
				@Override
				public void onRegistering(String localProfileUri) {
//					// TODO Auto-generated method stub
//					Intent intent = new Intent(ctx, SipService.class);
//					intent.setAction(ACTION_CONNECTING);
//					ctx.startService(intent);

				}
			});
		}
		catch (ParseException pe) {
			Intent intent = new Intent(ctx, SipService.class);
			intent.setAction(FAILED);
			ctx.startService(intent);

		} 
		catch (SipException se) {
			Intent intent = new Intent(ctx, SipService.class);
			intent.setAction(FAILED);
			ctx.startService(intent);
        }
	}
	
	public void updateStatus(final String action)
	{
		this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(), SipService.class);
				intent.setAction(action);
				startService(intent);
			}
		});
	}
	
	public static void closeLocalProfile() {
        if (SipInit.sipManager == null) {
            return;
        }
        try {
            if (SipInit.profile != null) {
            	SipInit.sipManager.close(SipInit.profile.getUriString());
            }
        } catch (Exception ee) {
        }
    }
}
