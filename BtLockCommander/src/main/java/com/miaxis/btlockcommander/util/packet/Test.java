package com.miaxis.btlockcommander.util.packet;

import java.util.ArrayList;

import org.bouncycastle.util.encoders.Base64;

public class Test {
//	public static void testBinder(){
//		String userid = "12345678";
//		boolean[] flags = new boolean[16];
//		flags[15] = false;
//		flags[14] = false;
//		flags[13] = false;
//		flags[11] = true;
//		flags[10] = true;
//		flags[9] = true;
//		flags[7] = true;
//		flags[6] = true;
//		flags[5] = true;
//		flags[4] = true;
//		flags[3] = true;
//		String passwd = new String(Base64.encode("12345678901234567890".getBytes()));
//		String startTime = "2019-05-23 01:01:23";
//		String endTime = "2029-05-23 08:11:00";
//		String finger1 = "QwESDgFjS////////////////xoAcQAAAAAAAAAAAIAYzPyBIQT8Rygn/BkrL/waSTP8x0Wg/LtMUv5UTdz8xFai/GFXHfw+aS78F2/q/KKAYv5nkyD8MZo9/IWpBPwbsfT84K4r/lO1PPzPsiz+gccN/IPKYv543PD8a9wr/JDiRf575xr8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMY=";
//		String finger2 = "QwESDgFjT////////////////yYAoQAAAAAAAAAAAK8swPxjNSP8q1YF/EBrMvycgxP8fIXi/GeKLPw3lTP8PZ/w/HicMvxouPD8ublf/tS9q/wp0jP8XtU9/N7RKP6c2TP8quEE/GXt+PzW8xf+nQI+/UYC7P2uBfz9Zw39/X4USv0mIPT9gS1I/XsvRv1UNxb/ZDEo/6U74/0sOgT/vkP0/ahM7f1zUY395031/a9WOv1oW579AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHY=";
//		String cardinfo = "1234567812345678";
//
//		PersonBinder binder = new PersonBinder(userid, flags, passwd,
//			startTime, endTime, finger1, finger2, cardinfo);
//		byte[] body = binder.makeBody();
//		System.out.println("body len:"+body.length);
//		String pack = Packet.makePacket((byte)0x11, 1, (byte)2, (byte)0, body, 0, 150);
//		System.out.println(pack);
//		String pack2 = Packet.makePacket((byte)0x11, 1, (byte)2, (byte)1, body, 150, body.length-150);
//		System.out.println(pack2);
//	}
//
//	public static void testUnBinder(){
//		String userid = "12345678";
//		PersonUnBinder unbinder = new PersonUnBinder(userid);
//		byte[] body = unbinder.makeBody();
//		String pack = Packet.makePacket((byte)0x12, 2, (byte)1, (byte)0, body, 0, body.length);
//		System.out.println(pack);
//	}
//
//	public static void testUpdateFinger(){
//
//		String userid = "12345678";
//		String finger1 = "QwESDgFjS////////////////xoAcQAAAAAAAAAAAIAYzPyBIQT8Rygn/BkrL/waSTP8x0Wg/LtMUv5UTdz8xFai/GFXHfw+aS78F2/q/KKAYv5nkyD8MZo9/IWpBPwbsfT84K4r/lO1PPzPsiz+gccN/IPKYv543PD8a9wr/JDiRf575xr8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMY=";
//		String finger2 = "QwESDgFjT////////////////yYAoQAAAAAAAAAAAK8swPxjNSP8q1YF/EBrMvycgxP8fIXi/GeKLPw3lTP8PZ/w/HicMvxouPD8ublf/tS9q/wp0jP8XtU9/N7RKP6c2TP8quEE/GXt+PzW8xf+nQI+/UYC7P2uBfz9Zw39/X4USv0mIPT9gS1I/XsvRv1UNxb/ZDEo/6U74/0sOgT/vkP0/ahM7f1zUY395031/a9WOv1oW579AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHY=";
//
//		UpdateFinger obj = new UpdateFinger(userid, finger1, finger2);
//		byte[] body = obj.makeBody();
//		System.out.println("body len:"+body.length);
//		String pack = Packet.makePacket((byte)0x13, 25, (byte)2, (byte)0, body, 0, 150);
//		System.out.println(pack);
//		String pack2 = Packet.makePacket((byte)0x13, 26, (byte)2, (byte)1, body, 150, body.length-150);
//		System.out.println(pack2);
//	}
//
//	public static void testUpdateCard(){
//
//		String userid = "12345678";
//		String card = "1122334455667788";
//
//		UpdateCard obj = new UpdateCard(userid, card);
//		byte[] body = obj.makeBody();
//		System.out.println("body len:"+body.length);
//		String pack = Packet.makePacket((byte)0x13, 25, (byte)2, (byte)0, body, 0, body.length);
//		System.out.println(pack);
//	}
//
//	public static void testPasswd(){
//		String userid = "12345678";
//		String passwd = "MTEyMjMzNDQ1NTY2Nzc4ODk5MDA=";
//
//		UpdatePasswd obj = new UpdatePasswd(userid, passwd);
//		byte[] body = obj.makeBody();
//		System.out.println("body len:"+body.length);
//		String pack = Packet.makePacket((byte)0x13, 25, (byte)2, (byte)0, body, 0, body.length);
//		System.out.println(pack);
//	}
//
//	public static void testInitLock(){
//		String passwd = "MTEyMjMzNDQ1NTY2Nzc4ODk5MDA=";
//
//		InitLock obj = new InitLock(passwd);
//		byte[] body = obj.makeBody();
//		System.out.println("body len:"+body.length);
//		String pack = Packet.makePacket((byte)0x14, 25, (byte)1, (byte)0, body, 0, body.length);
//		System.out.println(pack);
//	}
//
//	public static void testSetTmpPwd(){
//		String userid = "12345678";
//		String passwd = new String(Base64.encode("12345678901234567890".getBytes()));
//		String startTime = "2019-05-23 01:01:23";
//		String endTime = "2029-05-23 08:11:00";
//		int times = 20;
//		byte tmpid = 5;
//
//		SetTempPasswd obj = new SetTempPasswd(passwd,
//			startTime, endTime, times, userid, tmpid);
//		byte[] body = obj.makeBody();
//		System.out.println("body len:"+body.length);
//		String pack = Packet.makePacket((byte)0x15, 1, (byte)1, (byte)0, body, 0, body.length);
//		System.out.println(pack);
//	}
//
//	public static void testVersion(){
//		String pack = "/BcAEDEyMzQ1Njc4OTBBQkNERUZHSElKS0xNTk9QUVJTVGFiY2RlZgAAAAA4ODg4ODg4ODg4AAAAAAAAAAAAADEyMzQ1NgAAAAAAAAAAAGFiY2RlZjEyMzQ1NgAAAAAAqY0=";
//		byte[] command = new byte[1];
//		int[] totalPackno = new int[1];
//		byte[] packCount = new byte[1];
//		byte[] packno = new byte[1];
//		byte[] body = Packet.parsePacket(pack, command, totalPackno, packCount, packno);
//		StringBuilder lockHardVersion = new StringBuilder();
//		StringBuilder lockSoftVersion = new StringBuilder();
//		StringBuilder fHardVersion = new StringBuilder();
//		StringBuilder fSoftVersion = new StringBuilder();
//		StringBuilder nbSn = new StringBuilder();
//		StringBuilder nbImei = new StringBuilder();
//		LockVersion.parseBody(body, lockHardVersion, lockSoftVersion,
//			fHardVersion, fSoftVersion, nbSn, nbImei);
//		System.out.println("lhard:"+lockHardVersion);
//		System.out.println("lsoft:"+lockSoftVersion);
//		System.out.println("fhard:"+fHardVersion);
//		System.out.println("fsoft:"+fSoftVersion);
//		System.out.println("nbsn:"+nbSn);
//		System.out.println("nbimei:"+nbImei);
//	}
//
//	public static void testReportInfo(){
//		String pack = "oAoAIBI0VngAW4IBEg5jSxoAgBjMgSEERygnGSsvIBpJM8dFoLtMUlRN3ADEVqJhVx0+aS4Xb+oCooBiZ5MgMZo9hakEiBux9OCuK1O1PM+yLAiBxw2DymJ43PBr3CsCkOJFe+caARIOY08mAK8swGM1I6tWBUBrMgCcgxN8heJniiw3lTOAPZ/weJwyaLjwublfgNS9qynSM3QA";
//		String pack2 = "oAoAIV7VPd7RKICc2TOq4QRl7fjW8xdVnQI+RgLsrgX8Zw39VX4USiYg9IEtSHsvRt9UNxZkMSilO+MsOgRVvkP0qEztc1GN5031Ba9WOmhbnnfT";
//		byte[] command = new byte[1];
//		int[] totalPackno = new int[1];
//		byte[] packCount = new byte[1];
//		byte[] packno = new byte[1];
//		byte[] body1 = Packet.parsePacket(pack, command, totalPackno, packCount, packno);
//		byte[] body2 = Packet.parsePacket(pack2, command, totalPackno, packCount, packno);
//
//		byte[] body = new byte[body1.length+body2.length];
//		System.arraycopy(body1, 0, body, 0, body1.length);
//		System.arraycopy(body2, 0, body, body1.length, body2.length);
//
//		StringBuilder userid = new StringBuilder();
//		StringBuilder card = new StringBuilder();
//		StringBuilder passwd = new StringBuilder();
//		StringBuilder finger1 = new StringBuilder();
//		StringBuilder finger2 = new StringBuilder();
//		byte[] type = new byte[1];
//
//		ReportInfo.parseBody(body, type, userid, card, passwd, finger1, finger2);
//		System.out.println("type:"+type[0]);
//		System.out.println("userid:"+userid);
//		System.out.println("card:"+card);
//		System.out.println("passwd:"+passwd);
//		System.out.println("finger1:"+finger1);
//		System.out.println("finger2:"+finger2);
//	}
//
//	public static void testLoginfo(){
//		String pack = "ogoAENDj3gRQARI0Vnhihw==";
//		byte[] command = new byte[1];
//		int[] totalPackno = new int[1];
//		byte[] packCount = new byte[1];
//		byte[] packno = new byte[1];
//		byte[] body = Packet.parsePacket(pack, command, totalPackno, packCount, packno);
//		StringBuilder userid = new StringBuilder();
//		StringBuilder time = new StringBuilder();
//		byte[] power = new byte[1];
//		byte[] openType = new byte[1];
//		byte[] alarmType = new byte[1];
//		LogInfo.parseBody(body, userid, time, power, openType, alarmType);
//		System.out.println("userid:"+userid);
//		System.out.println("time:"+time);
//		System.out.println("power:"+power[0]);
//		System.out.println("openType:"+openType[0]);
//		System.out.println("alarmType:"+alarmType[0]);
//	}
//
//	public static void testGetBind(){
//		String pack = "HgoAEAOhoaGhoqKioqOjo6PxLg==";
//		byte[] command = new byte[1];
//		int[] totalPackno = new int[1];
//		byte[] packCount = new byte[1];
//		byte[] packno = new byte[1];
//		ArrayList<String> useridList = new ArrayList<String>();
//		byte[] body = Packet.parsePacket(pack, command, totalPackno, packCount, packno);
//		GetBindPerson.parseBody(body, useridList);
//		for (String userid: useridList){
//			System.out.println(userid);
//		}
//	}
//	public static void main(String[] args){
//		//testUnBinder();
//		//testUpdateFinger();
//		//testUpdateCard();
//		//testPasswd();
//		//testInitLock();
//		//testSetTmpPwd();
//		testVersion();
//		//testReportInfo();
//		//testLoginfo();
//		testGetBind();
//	}
}
