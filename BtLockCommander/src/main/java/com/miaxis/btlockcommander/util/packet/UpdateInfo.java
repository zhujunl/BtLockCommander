package com.miaxis.btlockcommander.util.packet;

import java.util.Arrays;

import org.bouncycastle.util.encoders.Hex;

public class UpdateInfo {
	private String userid;
	private byte type;
	
	public UpdateInfo(String userid, byte type){
		this.userid = userid;
		this.type = type;
	}

	public byte[] makeBody(){
		int packLen;
		byte[] buf = new byte[100];
		
		packLen = 0;
		byte[] useridHex = Hex.decode(userid);
		int len = useridHex.length;
		if (len > 4){
			len = 4;
		}
		System.arraycopy(useridHex, 0, buf, packLen, len);
		packLen += 4;

		buf[packLen] = type;
		packLen += 1;
		return Arrays.copyOf(buf, packLen);
	}
}
