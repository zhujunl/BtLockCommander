package com.miaxis.btlockcommander.util.packet;

import org.bouncycastle.util.encoders.Base64;

import java.util.Arrays;

public class Packet {
    /**
     * 生成通信包
     *
     * @param encFlag     加密标志(false 不加密, true 加密)
     * @param key         加密密钥(encFlag为true时有效, 24字节)
     * @param command     命令字
     * @param totalPackno 总序号
     * @param packCount   包个数
     * @param packno      包序号
     * @param data        数据(最后一包可为任意长度, 否则必须为8的整数倍)
     * @param fromPos     分包起始位置
     * @param length      分包数据长度
     * @return 通信包(Base64编码)
     * @throws Exception
     */
    public static String makePacket(boolean encFlag, byte[] key, byte command,
                                    int totalPackno, byte packCount, byte packno, byte[] data, int fromPos,
                                    int length) throws Exception {
        byte[] buf = new byte[2000];
        int pack_len = 0;
        int crc_val;

        buf[pack_len] = command;
        pack_len += 1;

        if (encFlag) {
            buf[pack_len] = 0x55;
        } else {
            buf[pack_len] = 0;
        }
        pack_len += 1;

        buf[pack_len] = (byte) (totalPackno & 0xff);
        buf[pack_len + 1] = (byte) (totalPackno >> 8);
        pack_len += 2;

        buf[pack_len] = (byte) ((packCount << 4) | packno);
        pack_len += 1;

        if (encFlag) {
            byte[] encData = Utils.encryptData(key, data, fromPos, length);
            System.arraycopy(encData, 0, buf, pack_len, encData.length);
            pack_len += encData.length;
        } else {
            System.arraycopy(data, fromPos, buf, pack_len, length);
            pack_len += length;
        }

        CRC16 crc = new CRC16();
        crc_val = crc.caluCRC(buf, pack_len);
        buf[pack_len] = (byte) (crc_val & 0xff);
        buf[pack_len + 1] = (byte) (crc_val >> 8);
        pack_len += 2;

        buf = Arrays.copyOf(buf, pack_len);
        return new String(Base64.encode(buf));
    }

    /**
     * 解析通信包
     *
     * @param key         加密密钥(24字节)
     * @param packStr     通信包(Base64编码)
     * @param command     命令字
     * @param totalPackno 总序号
     * @param packCount   包个数
     * @param packno      包序号
     * @return 包体数据(null失败)
     * @throws Exception
     */
    public static byte[] parsePacket(byte[] key, String packStr, byte[] command,
                                     int[] totalPackno, byte[] packCount, byte[] packno) throws Exception {
        boolean encFlag;
        byte[] buf = Base64.decode(packStr);
        int dataLen, i;
        int crc_val, crc_val2;

        CRC16 crc = new CRC16();
        crc_val = crc.caluCRC(buf, buf.length - 2);
        crc_val2 = (buf[buf.length - 2] & 0xff) | ((buf[buf.length - 1] & 0xff) << 8);
        if (crc_val != crc_val2) {
            return null;
        }

        dataLen = buf.length - 7;
        i = 0;
        command[0] = buf[i];
        i += 1;

        if (buf[i] == 0) {
            encFlag = false;
        } else {
            encFlag = true;
        }
        i += 1;

        totalPackno[0] = (buf[i] & 0xff) | ((buf[i + 1] & 0xff) << 8);
        i += 2;

        packCount[0] = (byte) (buf[i] >> 4);
        packno[0] = (byte) (buf[i] & 0xf);
        i += 1;

        if (!encFlag) {
            return Arrays.copyOfRange(buf, i, i + dataLen);
        } else {
            byte[] encData = Arrays.copyOfRange(buf, i, i + dataLen);
            return Utils.decryptData(key, encData);
        }
    }
}
