package com.miaxis.btlockcommander.util.packet;

public class GetTime {
	private byte result;
	private String time;
	
	public GetTime(byte result, String time){
		this.result = result;
		this.time = time;
	}

	public byte[] makeBody(){
		byte[] body = new byte[5];
		body[0] = result;

		byte[] timeBuf = Utils.compTime(time);
		System.arraycopy(timeBuf, 0, body, 1, 4);
		return body;
	}
}
