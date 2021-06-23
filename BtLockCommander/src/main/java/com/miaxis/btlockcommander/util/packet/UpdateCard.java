package com.miaxis.btlockcommander.util.packet;

import java.util.Arrays;

import org.bouncycastle.util.encoders.Hex;

public class UpdateCard {
	private String userid;
	private String cardinfo;

	public UpdateCard(String userid, String cardinfo){
		this.userid = userid;
		this.cardinfo = cardinfo;
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

		buf[packLen] = 1;
		packLen += 1;

		byte[] cardHex = Hex.decode(cardinfo);
		len = cardHex.length;
		if (len > 8){
			len = 8;
		}
		System.arraycopy(cardHex, 0, buf, packLen, len);
		packLen += 8;

		return Arrays.copyOf(buf, packLen);
	}

	public static int parseBody(byte[] buf, StringBuilder userid){
		userid.append(Hex.encode(buf, 0, 4));
		int result = buf[4];
		return result;
	}
}
