package com.miaxis.btlockcommander.util.packet;

import java.util.Arrays;

/**
 * 获取锁内鉴权数据
 * @author zhang.zl
 *
 */
public class GetAuthData {
	/**
	 * 生成包体
	 * @param algType 签名算法类型(0-MD5, 1-SHA-1)
	 * @param timestamp 时间戳(4字节, 在2018年1月1月00:00:00基础上进行增加)
	 * @return 请求包体
	 */
	public static byte[] makeBody(byte algType, byte[] timestamp){
		int packLen;
		byte[] buf = new byte[100];

		packLen = 0;
		buf[packLen] = algType;
		packLen++;

		System.arraycopy(timestamp, 0, buf, packLen, 4);
		packLen += 4;

		return Arrays.copyOf(buf, packLen);
	}

	/**
	 * 解析包体
	 * @param buf 包体
	 * @param nbSn NB模块SN(15字节)
	 * @param custInfo 客户信息(8字节)
	 * @param sign 加密后的Hash数据
	 * @return 0 成功
	 */
	public static int parseBody(byte[] buf, byte[] nbSn,
								byte[] custInfo, byte[] sign){
		int pos = 0;
		int result = buf[pos];
		pos++;
		if (result != 0){
			return result;
		}

		System.arraycopy(buf, pos, nbSn, 0, 15);
		pos += 15;

		System.arraycopy(buf, pos, custInfo, 0, 8);
		pos += 8;

		System.arraycopy(buf, pos, sign, 0, buf.length-pos);
		pos += 8;
		return 0;
	}
}
