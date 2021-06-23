package com.miaxis.btlockcommander.util.packet;

import java.util.Arrays;

import org.bouncycastle.util.encoders.Hex;

public class PersonUnBinder {
	private String userid;
	
	public PersonUnBinder(String userid){
		this.userid = userid;
	}
	
	public byte[] makeBody(){
		int packLen;
		byte[] buf = new byte[20];
		
		packLen = 0;
		byte[] useridHex = Hex.decode(userid);
		int len = useridHex.length;
		if (len > 4){
			len = 4;
		}
		System.arraycopy(useridHex, 0, buf, packLen, len);
		packLen += 4;
		
		return Arrays.copyOf(buf, packLen);
	}

	public static int parseBody(byte[] buf, StringBuilder userid){
		userid.append(Hex.encode(buf, 0, 4));
		int result = buf[4];
		return result;
	}
}
