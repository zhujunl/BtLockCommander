package com.miaxis.btlockcommander.util.packet;

import java.util.Arrays;

/*
 * 获取锁内公钥
 */
public class GetLockPubKey {
	/**
	 * 解析包体
	 * @param buf 包体
	 * @param result 响应结果，0为成功，1为通讯出错，其它值为失败
	 * @return 公钥数据
	 */
	public static byte[] parseBody(byte[] buf, int[] result){
		result[0] = buf[0];
		if (result[0] != 0){
			return null;
		}
		
		return Arrays.copyOfRange(buf, 1, buf.length);
	}
}
