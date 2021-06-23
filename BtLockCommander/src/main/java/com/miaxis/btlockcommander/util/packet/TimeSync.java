package com.miaxis.btlockcommander.util.packet;

public class TimeSync {
	private String time;
	
	public TimeSync(String time){
		this.time = time;
	}

	public byte[] makeBody(){
		return Utils.compTime(time);
	}
}
