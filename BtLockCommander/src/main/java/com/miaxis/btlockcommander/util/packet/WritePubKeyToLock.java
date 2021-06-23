package com.miaxis.btlockcommander.util.packet;

import java.util.Arrays;

/**
 * 下发公钥
 * @author zhang.zl
 *
 */
public class WritePubKeyToLock {
	private byte keyType;
	private byte[] keyData;
	
	public WritePubKeyToLock(byte keyType, byte[] keyData){
		this.keyType = keyType;
		this.keyData = keyData;
	}
	
	public byte[] makeBody(){
		int packLen;
		byte[] buf = new byte[200];
		
		packLen = 0;
		buf[0] = keyType;
		packLen++;
		
		System.arraycopy(keyData, 0, buf, packLen, keyData.length);
		packLen += keyData.length;
		return Arrays.copyOf(buf, packLen);
	}
}
