package chau.voipapp;

import java.io.Serializable;

public class HistoryInfo implements Serializable, Comparable<HistoryInfo>
//, Comparable<HistoryInfo>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String sipAddr;
	private String callDate;
	private String callDuration;
	private boolean outgoingCall;
	private boolean missedCall;
	private String name;
	
	public HistoryInfo(String sipAddr, String name, String callDate, String callDuration,
			boolean outgoingCall, boolean missedCall) {
		super();
		this.sipAddr = sipAddr;
		this.name = name;
		this.callDate = callDate;
		this.callDuration = callDuration;
		this.outgoingCall = outgoingCall;
		this.missedCall = missedCall;
	}
	
	public boolean isMissedCall() {
		return missedCall;
	}
	
	public void setMissedCall(boolean missedCall) {
		this.missedCall = missedCall;
	}
	
	public String getSipAddr() {
		return sipAddr;
	}
	
	public void setSipAddr(String sipAddr) {
		this.sipAddr = sipAddr;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getCallDate() {
		return callDate;
	}
	
	public void setCallDate(String callDate) {
		this.callDate = callDate;
	}
	
	public String getCallDuration() {
		return callDuration;
	}
	public void setCallDuration(String callDuration) {
		this.callDuration = callDuration;
	}
//	public String toString()
//	{
//		return sipAddr;
//	}
	public boolean isOutgoingCall() {
		return outgoingCall;
	}
	public void setOutgoingCall(boolean outgoingCall) {
		this.outgoingCall = outgoingCall;
	}

	@Override
	public int compareTo(HistoryInfo another) {
		// TODO Auto-generated method stub
		return this.getName().compareTo(another.getName());
	}

}
