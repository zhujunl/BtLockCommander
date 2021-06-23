package com.miaxis.btlockcommander.util.packet;

import java.util.Arrays;

import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

public class UpdatePasswd {
	private String userid;
	private String passwd;

	public UpdatePasswd(String userid, String passwd){
		this.userid = userid;
		this.passwd = passwd;
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

		buf[packLen] = 2;
		packLen += 1;

		byte[] pwdBuf = Base64.decode(passwd);
		len = pwdBuf.length;
		if (len > 20){
			len = 20;
		}
		System.arraycopy(pwdBuf, 0, buf, packLen, len);
		packLen += 20;

		return Arrays.copyOf(buf, packLen);
	}

	public static int parseBody(byte[] buf, StringBuilder userid){
		userid.append(Hex.encode(buf, 0, 4));
		int result = buf[4];
		return result;
	}
}
