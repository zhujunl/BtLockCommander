package com.miaxis.btlockcommander.util.packet;

import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

public class ReportInfo {
	public static int parseBody(byte[] buf, byte[] type, StringBuilder userid,
		StringBuilder card, StringBuilder passwd, StringBuilder finger1, 
		StringBuilder finger2){
		int pos = 0;
		userid.append(new String(Hex.encode(buf, 0, 4)));
		pos += 4;
		
		type[0] = buf[pos];
		pos += 1;

		if (type[0] == 1){
			card.append(new String(Hex.encode(buf, pos, 8)));
			pos += 8;
		}
		else if (type[0] == 2){
			byte[] hexPwd = new byte[20];
			System.arraycopy(buf, pos, hexPwd, 0, 20);
			passwd.append(new String(Base64.encode(hexPwd)));
			pos += 20;
		}
		else if (type[0] == 0){
			int f1len = buf[pos]&0xff;
			int f2len = buf[pos+1]&0xff;
			pos += 2;
			if (f1len > 0){
				byte[] compfinger = new byte[256];
				System.arraycopy(buf, pos, compfinger, 0, f1len);
				finger1.append(Utils.decompFinger(compfinger));
				pos += f1len;
			}
			if (f2len > 0){
				byte[] compfinger = new byte[256];
				System.arraycopy(buf, pos, compfinger, 0, f2len);
				finger2.append(Utils.decompFinger(compfinger));
				pos += f2len;
			}
		}
		else{
			return -1;
		}
		return 0;
	}
}
