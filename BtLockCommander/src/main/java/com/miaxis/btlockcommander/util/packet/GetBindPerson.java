package com.miaxis.btlockcommander.util.packet;

import java.util.List;

import org.bouncycastle.util.encoders.Hex;

public class GetBindPerson {
	public static int parseBody(byte[] buf, List<String> useridList){
		int pos = 0;
		int count = buf[0]&0xff;
		pos += 1;
		
		for (int i=0; i<count; i++){
			useridList.add(new String(Hex.encode(buf, pos, 4)));
			pos += 4;
		}
		return 0;
	}
}
