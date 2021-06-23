package com.miaxis.btlockcommander.util.packet;

import java.util.Arrays;

import org.bouncycastle.util.encoders.Base64;

public class InitLock {
	private String passwd;

	public InitLock(String passwd){
		this.passwd = passwd;
	}

	public byte[] makeBody(){
		int packLen;
		byte[] buf = new byte[100];
		
		packLen = 0;
		byte[] pwdBuf = Base64.decode(passwd);
		int len = pwdBuf.length;
		if (len > 20){
			len = 20;
		}
		System.arraycopy(pwdBuf, 0, buf, packLen, len);
		packLen += 20;

		return Arrays.copyOf(buf, packLen);
	}
}
