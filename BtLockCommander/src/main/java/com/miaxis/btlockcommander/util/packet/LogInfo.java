package com.miaxis.btlockcommander.util.packet;

import org.bouncycastle.util.encoders.Hex;

public class LogInfo {
	public static int parseBody(byte[] buf, StringBuilder userid, StringBuilder time,
			byte[] power, byte[] openType, byte[] alarmType){
		int pos = 0;

		time.append(Utils.decompTime(buf));
		pos += 4;

		power[0] = buf[pos];
		pos += 1;
		
		openType[0] = (byte)(buf[pos]&0xf);
		alarmType[0] = (byte)((buf[pos]&0xff)>>4);
		pos += 1;
		
		if (alarmType[0] == 0){
			userid.append(new String(Hex.encode(buf, pos, 4)));
			pos += 4;
		}
		
		return 0;
	}
}
