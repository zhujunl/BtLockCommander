package com.miaxis.btlockcommander.util.packet;

import java.util.Arrays;

import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

public class SetTempPasswd {
	private String passwd;
	private String startTime;
	private String endTime;
	private int times;
	private String userid;
	private byte tmpid;
	
	public SetTempPasswd(String passwd, String startTime, String endTime,
			int times, String userid, byte tmpid){
		this.userid = userid;
		this.passwd = passwd;
		this.startTime = startTime;
		this.endTime = endTime;
		this.times = times;
		this.tmpid = tmpid;
	}

	public byte[] makeBody(){
		int packLen;
		byte[] buf = new byte[1024];
		
		packLen = 0;		
		byte[] pwdBuf = Base64.decode(passwd);
		int len = pwdBuf.length;
		if (len > 20){
			len = 20;
		}
		System.arraycopy(pwdBuf, 0, buf, packLen, len);
		packLen += 20;
		
		byte[] timeBuf = Utils.compTime(startTime);
		System.arraycopy(timeBuf, 0, buf, packLen, 4);
		packLen += 4;

		timeBuf = Utils.compTime(endTime);
		System.arraycopy(timeBuf, 0, buf, packLen, 4);
		packLen += 4;
		
		buf[packLen] = (byte)times;
		packLen += 1;

		byte[] useridHex = Hex.decode(userid);
		len = useridHex.length;
		if (len > 4){
			len = 4;
		}
		System.arraycopy(useridHex, 0, buf, packLen, len);
		packLen += 4;

		buf[packLen] = (byte)tmpid;
		packLen += 1;

		return Arrays.copyOf(buf, packLen);
	}

	public static int parseBody(byte[] buf, StringBuilder userid, int[] tmpid){
		int result = buf[0];
		userid.append(Hex.encode(buf, 1, 4));
		tmpid[0] = buf[5];
		return result;
	}
}
