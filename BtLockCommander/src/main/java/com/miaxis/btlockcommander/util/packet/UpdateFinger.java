package com.miaxis.btlockcommander.util.packet;

import java.util.Arrays;

import org.bouncycastle.util.encoders.Hex;

public class UpdateFinger {
	private String userid;
	private String finger1;
	private String finger2;

	public UpdateFinger(String userid, String finger1, String finger2){
		this.userid = userid;
		this.finger1 = finger1;
		this.finger2 = finger2;
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

		buf[packLen] = 0;
		packLen += 1;

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
		return Arrays.copyOf(buf, packLen);
	}
}
