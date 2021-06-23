package com.miaxis.btlockcommander.util.packet;

import java.util.Arrays;

import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

public class PersonBinder {
	private String userid;
	private boolean[] flags;
	private String passwd;
	private String startTime;
	private String endTime;
	private String finger1;
	private String finger2;
	private String cardinfo;
	
	private byte[] compFlags(boolean[] flags)
	{
		byte[] buf = new byte[2];
		buf[0] = 0;
		buf[1] = 0;
		for (int i=0; i<8; i++){
			if (flags[i]){
				buf[0] |= (1<<i);
			}
			if (flags[i+8]){
				buf[1] |= (1<<i);
			}
		}
		return buf;
	}

	public PersonBinder(String userid, boolean[] flags, String passwd,
			String startTime, String endTime, String finger1, String finger2,
			String cardinfo){
		this.userid = userid;
		this.flags = flags;
		this.passwd = passwd;
		this.startTime = startTime;
		this.endTime = endTime;
		this.finger1 = finger1;
		this.finger2 = finger2;
		this.cardinfo = cardinfo;
	}
	
	public byte[] makeBody(){
		int packLen;
		byte[] buf = new byte[1024];
		
		packLen = 0;
		byte[] useridHex = Hex.decode(userid);
		int len = useridHex.length;
		if (len > 4){
			len = 4;
		}
		System.arraycopy(useridHex, 0, buf, packLen, len);
		packLen += 4;
		
		byte[] flagBytes = compFlags(flags);
		System.arraycopy(flagBytes, 0, buf, packLen, 2);
		packLen += 2;
		
		if (flags[13] == false){
			byte[] pwdBuf = Base64.decode(passwd);
			len = pwdBuf.length;
			if (len > 20){
				len = 20;
			}
			System.arraycopy(pwdBuf, 0, buf, packLen, len);
			packLen += 20;
		}
		
		if (flags[11]){
			if (flags[10]){
				byte[] timeBuf = Utils.compTime(startTime);
				System.arraycopy(timeBuf, 0, buf, packLen, 4);
				packLen += 4;
			}
			if (flags[9]){
				byte[] timeBuf = Utils.compTime(endTime);
				System.arraycopy(timeBuf, 0, buf, packLen, 4);
				packLen += 4;
			}
		}
		
		if (flags[15] == false){
			byte[] fp1Buf = null; 
			byte[] fp2Buf = null; 
			if (finger1!=null && finger1.length()>0){
				fp1Buf = Utils.compFinger(finger1);
			}
			if (finger2!=null && finger2.length()>0){
				fp2Buf = Utils.compFinger(finger2);
			}
			if (fp1Buf == null){
				buf[packLen] = 0;
			}
			else{
				buf[packLen] = (byte)fp1Buf.length;
			}
			packLen += 1;

			if (fp2Buf == null){
				buf[packLen] = 0;
			}
			else{
				buf[packLen] = (byte)fp2Buf.length;
			}
			packLen += 1;

			if (fp1Buf != null){
				System.arraycopy(fp1Buf, 0, buf, packLen, fp1Buf.length);
				packLen += fp1Buf.length;
			}
			if (fp2Buf != null){
				System.arraycopy(fp2Buf, 0, buf, packLen, fp2Buf.length);
				packLen += fp2Buf.length;
			}
		}
		
		if (flags[14] == false){
			byte[] cardHex = Hex.decode(cardinfo);
			len = cardHex.length;
			if (len > 8){
				len = 8;
			}
			System.arraycopy(cardHex, 0, buf, packLen, len);
			packLen += 8;
		}
		return Arrays.copyOf(buf, packLen);
	}
	
	public static int parseBody(byte[] buf, StringBuilder userid){
		int result = buf[4];
		userid.append(Hex.encode(buf, 0, 4));
		return result;
	}
}
