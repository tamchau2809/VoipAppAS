package chau.voipapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import android.annotation.SuppressLint;
import android.net.sip.SipAudioCall;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;

public final class SipInit {
	public static SipManager sipManager;
	public static SipProfile profile;
	public static SipAudioCall call = null; //static
//    public IncomingCallReceiver callReceiver;
    public static SipProfile sipTarget;
    public static SipAudioCall.Listener listener;
    
    public static String sipAddress = null;
    static String callDate = "";
    static String callDuration = "";
    static long startTime=0;
    static long stopTime=0;
    static boolean outgoingCall;
    
    public static final String FILENAME = "history";
    @SuppressLint("SdCardPath")
	public static final String PATH = "/data/data/chau.voipapp/files/";
    static File file;
    static FileOutputStream fos;
    static ObjectOutputStream oos;

    static boolean callHeld=false;
    static boolean holdIsPressed = true;
	static boolean speakerIsPressed = true;
	static boolean speakerPhone = false;
	
    public SipInit() {
		// TODO Auto-generated constructor stub
	}
}
