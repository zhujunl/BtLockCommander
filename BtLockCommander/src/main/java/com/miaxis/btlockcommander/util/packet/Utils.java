package com.miaxis.btlockcommander.util.packet;

import org.bouncycastle.util.encoders.Base64;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

public class Utils {
	public static byte[] compFinger(String finger){
		int fplen;
		int rawPos;
		int highPos;
		byte[] hexFp = Base64.decode(finger);
		byte[] compFp = new byte[256];
		if (hexFp.length != 512){
			return null;
		}

		if (hexFp[0] != 0x43){
			return null;
		}
		if (hexFp[4] != 1){
			return null;
		}
		System.arraycopy(hexFp, 1, compFp, 0, 3);
		System.arraycopy(hexFp, 5, compFp, 3, 2);
		compFp[5] = hexFp[19];
		if (compFp[5] > 76)
		{
			compFp[5] = 76;
		}

		fplen = 6;
		rawPos = 31;
		for (byte i=0; i<compFp[5]; i+=4){
			highPos = fplen;
			compFp[highPos] = 0;
			fplen++;

			System.arraycopy(hexFp, rawPos, compFp, fplen, 3);
			compFp[highPos] = (byte)(hexFp[rawPos+3]&3);
			rawPos += 4;
			fplen += 3;

			if (i+1 < compFp[5]){
				System.arraycopy(hexFp, rawPos, compFp, fplen, 3);
				compFp[highPos] |= (hexFp[rawPos+3]&3)<<2;
				rawPos += 4;
				fplen += 3;
			}

			if (i+2 < compFp[5]){
				System.arraycopy(hexFp, rawPos, compFp, fplen, 3);
				compFp[highPos] |= (hexFp[rawPos+3]&3)<<4;
				rawPos += 4;
				fplen += 3;
			}

			if (i+1 < compFp[5]){
				System.arraycopy(hexFp, rawPos, compFp, fplen, 3);
				compFp[highPos] |= (hexFp[rawPos+3]&3)<<6;
				rawPos += 4;
				fplen += 3;
			}
		}

		return Arrays.copyOf(compFp, fplen);
	}

	public static String decompFinger(byte[] compFinger){
		byte[] rawFinger = new byte[512];
		int tzLen;
		int rawPos;
		int compPos;

		rawFinger[0] = 0x43;
		System.arraycopy(compFinger, 0, rawFinger, 1, 3);
		rawFinger[4] = 1;
		System.arraycopy(compFinger, 3, rawFinger, 5, 2);
		Arrays.fill(rawFinger, 7, 19, (byte)0xff);
		rawFinger[19] = compFinger[5];
		tzLen = compFinger[5]*4+9;
		rawFinger[20] = (byte)(tzLen>>8);
		rawFinger[21] = (byte)(tzLen&0xff);
		Arrays.fill(rawFinger, 22, 31, (byte)0);

		compPos = 6;
		rawPos = 31;
		for (int i=0; i<compFinger[5]; i+=4){
			byte compHA = compFinger[compPos];
			compPos++;

			System.arraycopy(compFinger, compPos, rawFinger, rawPos, 3);
			rawFinger[rawPos+3] = (byte)((compHA&3)|0xfc);
			compPos += 3;
			rawPos += 4;

			if (i+1 < compFinger[5]){
				System.arraycopy(compFinger, compPos, rawFinger, rawPos, 3);
				rawFinger[rawPos+3] = (byte)(((compHA>>2)&3)|0xfc);
				compPos += 3;
				rawPos += 4;
			}

			if (i+2 < compFinger[5]){
				System.arraycopy(compFinger, compPos, rawFinger, rawPos, 3);
				rawFinger[rawPos+3] = (byte)(((compHA>>4)&3)|0xfc);
				compPos += 3;
				rawPos += 4;
			}

			if (i+3 < compFinger[5]){
				System.arraycopy(compFinger, compPos, rawFinger, rawPos, 3);
				rawFinger[rawPos+3] = (byte)(((compHA>>6)&3)|0xfc);
				compPos += 3;
				rawPos += 4;
			}
		}

		rawFinger[511] = CRC8.blockChecksum(rawFinger, 511);
		return new String(Base64.encode(rawFinger));
	}

	public static byte[] compTime(String timeStr){
		try {
			String[] dtStrs = timeStr.split(" ");
			String[] ymd = dtStrs[0].split("-");
			int year = Integer.parseInt(ymd[0])-2018;
			int month = Integer.parseInt(ymd[1]);
			int day = Integer.parseInt(ymd[2]);
			String[] hms = dtStrs[1].split(":");
			int hour = Integer.parseInt(hms[0]);
			int minute = Integer.parseInt(hms[1]);
			int second = Integer.parseInt(hms[2]);

			byte[] date = new byte[4];
			date[0] = (byte)((second&0x3f)|((minute&3)<<6));
			date[1] = (byte)((minute>>2)|((hour&0xf)<<4));
			date[2] = (byte)(((hour>>4)&1)|(day<<1)|((month&3)<<6));
			date[3] = (byte)((month>>2)|(year<<2));
			return date;
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return new byte[]{-1};
	}

	public static String decompTime(byte[] time){
		int sec = time[0]&0x3f;
		int min = ((time[0]&0xff)>>6)|((time[1]&0xf)<<2);
		int hour = ((time[1]&0xff)>>4)|((time[2]&0x1)<<4);
		int day = ((time[2]&0xff)>>1)&0x1f;
		int mon = ((time[2]&0xff)>>6)|((time[3]&0x3)<<2);
		int year = ((time[3]&0xff)>>2)+2018;
		return String.format("%04d-%02d-%02d %02d:%02d:%02d",
				year, mon, day, hour, min, sec);
	}

	public static byte[] encryptData(byte[] keyData, byte[] data,
									 int fromPos, int length) throws Exception{
		DESedeKeySpec ks = new DESedeKeySpec(keyData);
		SecretKeyFactory kf = SecretKeyFactory.getInstance("DESede");
		Key key = kf.generateSecret(ks);

		int len = length;
		if (len%8 != 0){
			len = 8*(len/8+1);
		}

		byte[] buf = new byte[len];
		Arrays.fill(buf, (byte)0);
		System.arraycopy(data, fromPos, buf, 0, length);
		Cipher c =Cipher.getInstance("DESede/ECB/NoPadding");
		c.init(Cipher.ENCRYPT_MODE, key);
		return c.doFinal(buf);
	}

	public static byte[] decryptData(byte[] keyData, byte[] data) throws Exception{
		DESedeKeySpec ks = new DESedeKeySpec(keyData);
		SecretKeyFactory kf = SecretKeyFactory.getInstance("DESede");
		Key key = kf.generateSecret(ks);

		Cipher c =Cipher.getInstance("DESede/ECB/NoPadding");
		c.init(Cipher.DECRYPT_MODE, key);
		return c.doFinal(data, 0, data.length);
	}

	public static byte[] rsaEncryptWithPubKey(byte[] keyData, byte[] data) throws Exception{
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyData);
		RSAPublicKey pubKey = (RSAPublicKey)kf.generatePublic(keySpec);
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, pubKey);
		return cipher.doFinal(data);
	}

	public static byte[] rsaDecryptWithPriKey(byte[] keyData, byte[] data) throws Exception{
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyData);
		RSAPrivateKey priKey = (RSAPrivateKey)kf.generatePrivate(keySpec);
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, priKey);
		return cipher.doFinal(data);
	}

	public static void genRsaKeyPair(StringBuilder pubKey, StringBuilder priKey
	) throws Exception{
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		keyPairGen.initialize(1024, new SecureRandom());

		// 生成一个密钥对，保存在keyPair中
		KeyPair keyPair = keyPairGen.generateKeyPair();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   // 得到私钥
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // 得到公钥
		String pubKeyStr = new String(Base64.encode(publicKey.getEncoded()));
		// 得到私钥字符串
		String priKeyStr = new String(Base64.encode(privateKey.getEncoded()));
		pubKey.append(pubKeyStr);
		priKey.append(priKeyStr);
	}

	public static void main(String[] args){
		/*String date = "2030-09-30 23:53:54";
		byte[] d = compTime(date);
		String date2 = decompTime(d);
		System.out.println(date2);
		*/
		String f1 = "QwESDgFjN////////////////04BQQAAAAAAAAAAAMYchvybKjH+Ojc8/K48evyoRxv+80Fu/GJK9/wzSvv89VNy/BBTRPy9Vxb+wVxi/NBjD/7tXWz8LGdL/JFkBP7caBv+DW0I/od2//wbclj8O4EQ/puC//xNjCH+1ohi/GiPavwWjQT+bJdI/MuaFP51oUD8ZKeF/MqrEP7kqCL+SLAv/p2r+vxvtbz8Vrdb/nK5Qfx+s+f837we/mu6rPyzvgr+z7te/DDDKP5XzYj8i8/x/CbSdPwu45f8WuSe/IDrPPw96lj+au4n/GXtuPyG8Db8i+4z/ODxR/y880D8Jv6T/D3/lvxRA2f/iAMM/aYFJf3HBSf9jgzK/bgJKP3WDif9ShKo/WsVDf1gHFv/QCNg/30lBP3AKRD9YCyk/XI/X/+DS1T/t00C/TpUof2aW2T/Z1qh/QAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHs=";
		byte[] f2 = compFinger(f1);
		String f3 = decompFinger(f2);
		System.out.println(f1);
		System.out.println(f3);
	}
}
