package com.miaxis.btlockcommander.manager;

import android.util.Log;

import com.miaxis.btlockcommander.entity.BaseLockPacket;
import com.miaxis.btlockcommander.entity.BaseStation;
import com.miaxis.btlockcommander.entity.BindPersonId;
import com.miaxis.btlockcommander.entity.BindPersonResult;
import com.miaxis.btlockcommander.entity.BtLockNotifyData;
import com.miaxis.btlockcommander.entity.FirmwareUpdateResult;
import com.miaxis.btlockcommander.entity.LockCommand;
import com.miaxis.btlockcommander.entity.LockVersion;
import com.miaxis.btlockcommander.entity.OpenLog;
import com.miaxis.btlockcommander.entity.OpenLogCount;
import com.miaxis.btlockcommander.entity.OpenLogResponse;
import com.miaxis.btlockcommander.entity.UpdatePersonResult;
import com.miaxis.btlockcommander.util.packet.CRC16;
import com.miaxis.btlockcommander.util.packet.Packet;
import com.miaxis.btlockcommander.util.packet.Utils;

import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class DataAnalysis {

    private static final byte PROTOCOL_HEAD = (byte) 0x02;
    private static final byte PROTOCOL_END = (byte) 0x03;
    private static final int PACKET_SIZE = 128;

    private byte[] cacheData = new byte[0];

    DataAnalysis() {
    }

    BtLockNotifyData analysisData(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        boolean hasHead = false;
        for (int i = 0; i < data.length; i++) {
            if (data[i] == PROTOCOL_HEAD) {
                byte[] tempCacheData = new byte[data.length - i];
                System.arraycopy(data, i, tempCacheData, 0, tempCacheData.length);
                data = tempCacheData;
                hasHead = true;
                break;
            }
        }
        boolean hasEnd = false;
        for (int i = 0; i < data.length; i++) {
            if (data[i] == PROTOCOL_END) {
                byte[] tempCacheData = new byte[i + 1];
                System.arraycopy(data, 0, tempCacheData, 0, tempCacheData.length);
                data = tempCacheData;
                hasEnd = true;
                break;
            }
        }
        if (hasHead) {
            cacheData = data;
        } else {
            addToCache(data);
        }
        if (hasEnd) {
            return analysisPacket(cacheData);
        }
        return null;
    }

    private BtLockNotifyData analysisPacket(byte[] data) {
        byte[] packet = breakOffBothEnds(data);
        byte[] command = new byte[1];
        int[] totalPackno = new int[1];
        byte[] packCount = new byte[1];
        byte[] packno = new byte[1];
        byte[] body = null;
        try {
            body = Packet.parsePacket(null, new String(packet), command, totalPackno, packCount, packno);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (body == null) {
            return null;
        }
        switch (command[0]) {
            case 0:
                Log.e("asd", "解析错误：" + new String(packet));
                return null;
            case LockCommand.LOCK_VERSION_DATA:
                LockVersion lockVersion = analysisLockVersion(body);
                lockVersion.setLockPacket(command[0], packet);
                return new BtLockNotifyData<>(BtLockNotifyData.LOCK_VERSION, lockVersion);
            case LockCommand.BIND_PERSON_ID:
                BindPersonId bindPersonId = analysisBindPersonId(body);
                bindPersonId.setLockPacket(command[0], packet);
                return new BtLockNotifyData<>(BtLockNotifyData.BIND_PERSON_ID, bindPersonId);
            case LockCommand.FIRMWARE_UPDATE_RESULT:// TODO: 2020/6/12 BUG 升级文件写入失败
                FirmwareUpdateResult firmwareUpdateResult = analysisFirmwareUpdateResult(body);
                firmwareUpdateResult.setLockPacket(command[0], packet);
                return new BtLockNotifyData<>(BtLockNotifyData.FIRMWARE_UPDATE_RESULT, firmwareUpdateResult);
            case LockCommand.PERSON_BIND_RESPONSE:
                BindPersonResult bindPersonResult = analysisBindPersonResult(body);
                bindPersonResult.setLockPacket(command[0], packet);
                return new BtLockNotifyData<>(BtLockNotifyData.BIND_PERSON_RESULT, bindPersonResult);
            case LockCommand.PERSON_UPDATE_REPONSE:
                UpdatePersonResult updatePersonResult = analysisUpdatePersonResult(body);
                updatePersonResult.setLockPacket(command[0], packet);
                return new BtLockNotifyData<>(BtLockNotifyData.UPDATE_PERSON_RESULT, updatePersonResult);
            case LockCommand.OPEN_LOG_COUNT_OF_DAY_RESPONSE:
                OpenLogCount openLogCount = analysisOpenLogCount(body);
                openLogCount.setLockPacket(command[0], packet);
                return new BtLockNotifyData<>(BtLockNotifyData.OPEN_LOG_COUNT_OF_DAY, openLogCount);
            case LockCommand.OPEN_LOG_RESPONSE:
                OpenLogResponse openLogResponse = analysisOpenLog(body);
                openLogResponse.setLockPacket(command[0], packet);
                return new BtLockNotifyData<>(BtLockNotifyData.OPEN_LOG_RESPONSE, openLogResponse);
            case LockCommand.BASE_STATION:
                BaseStation baseStation = analysisBaseStation(body);
                baseStation.setLockPacket(command[0], packet);
                return new BtLockNotifyData<>(BtLockNotifyData.BASE_STATION, baseStation);
            default:
                BaseLockPacket passThrough = new BaseLockPacket(command[0], packet);
                return new BtLockNotifyData<>(BtLockNotifyData.PASS_THROUGH, passThrough);
        }
    }

    private void addToCache(byte[] values) {
        byte[] temp = new byte[cacheData.length + values.length];
        System.arraycopy(cacheData, 0, temp, 0, cacheData.length);
        System.arraycopy(values, 0, temp, cacheData.length, values.length);
        cacheData = temp;
    }

    private byte[] breakOffBothEnds(byte[] data) {
        byte[] mData = new byte[data.length - 2];
        System.arraycopy(data, 1, mData, 0, data.length - 2);
        return mData;
    }

    private LockVersion analysisLockVersion(byte[] body) {
        LockVersion lockVersion = new LockVersion();
        lockVersion.setLockHardwareVersion(new String(body, 0, 10).trim());
        lockVersion.setLockFirmwareVersion(new String(body, 10, 20).trim());
        lockVersion.setFingerHardwareVersion(new String(body, 30, 10).trim());
        lockVersion.setFingerFirmwareVersion(new String(body, 40, 20).trim());
        lockVersion.setNbSerialNumber(new String(body, 60, 15).trim());
        if (lockVersion.getLockFirmwareVersion().startsWith("SL-MAIN-200N")) {
            if (lockVersion.getLockFirmwareVersion().compareTo("SL-MAIN-200N-V4.0.44") >= 0) {
                lockVersion.setSimICCID(new String(body, 75, 20).trim());
                lockVersion.setNbSignalIntensity(String.valueOf(body[95] & 0xff));
                lockVersion.setNbVersion(new String(body, 96, 15).trim());
                lockVersion.setSimIMSI(new String(body, 111, 15));
            } else if (lockVersion.getLockFirmwareVersion().compareTo("SL-MAIN-200N-V4.0.43") >= 0) {
                lockVersion.setSimICCID(new String(body, 75, 20).trim());
                lockVersion.setNbSignalIntensity(String.valueOf(body[95] & 0xff));
                lockVersion.setNbVersion(new String(body, 96, 15).trim());
            } else {
                lockVersion.setSimICCID(new String(body, 75, 17).trim());
                lockVersion.setNbSignalIntensity(String.valueOf(body[92] & 0xff));
                if (body.length > 93) {
                    lockVersion.setNbVersion(new String(body, 93, 15).trim());
                }
            }
        } else {
            lockVersion.setSimICCID(new String(body, 75, 20).trim());
            lockVersion.setNbSignalIntensity(String.valueOf(body[95] & 0xff));
            lockVersion.setNbVersion(new String(body, 96, 15).trim());
            lockVersion.setSimIMSI(new String(body, 111, 15));
        }
        return lockVersion;
    }

    private BindPersonId analysisBindPersonId(byte[] body) {
        BindPersonId bindPersonId = new BindPersonId();
        bindPersonId.setCount(body[0] & 0xff);
        List<String> personIdList = new ArrayList<>();
        for (int i = 1; i < body.length - 1; i += 4) {
            byte[] data = new byte[4];
            System.arraycopy(body, i, data, 0, data.length);
            String userId = new String(Hex.encode(data, 0, 4));
            int personId = Integer.valueOf(userId);
            personIdList.add(personId + "");
        }
        bindPersonId.setPersonIdList(personIdList);
        return bindPersonId;
    }

    private FirmwareUpdateResult analysisFirmwareUpdateResult(byte[] body) {
        FirmwareUpdateResult firmwareUpdateResult = new FirmwareUpdateResult();
        firmwareUpdateResult.setResult(body[0] & 0xff);
        return firmwareUpdateResult;
    }

    private BindPersonResult analysisBindPersonResult(byte[] body) {
        BindPersonResult bindPersonResult = new BindPersonResult();
        bindPersonResult.setPersonId(Integer.valueOf(new String(Hex.encode(body, 0, 4)).trim()) + "");
        bindPersonResult.setResult(body[4] & 0xff);
        return bindPersonResult;
    }

    private UpdatePersonResult analysisUpdatePersonResult(byte[] body) {
        UpdatePersonResult updatePersonResult = new UpdatePersonResult();
        updatePersonResult.setPersonId(Integer.valueOf(new String(Hex.encode(body, 0, 4)).trim()) + "");
        updatePersonResult.setResult(body[4] & 0xff);
        return updatePersonResult;
    }

    private OpenLogCount analysisOpenLogCount(byte[] body) {
        OpenLogCount openLogCount = new OpenLogCount();
        openLogCount.setCount((body[0] & 0xff) << 8 | (body[1] & 0xff));
        return openLogCount;
    }

    private OpenLogResponse analysisOpenLog(byte[] body) {
        List<OpenLog> openLogList = new ArrayList<>();
        for (int i = 0; i < body.length; i += 11) {
            if (i + 11 <= body.length) {
                byte[] data = new byte[11];
                System.arraycopy(body, i, data, 0, data.length);
                OpenLog openLog = new OpenLog();
                openLog.setTime(Utils.decompTime(data));
                openLog.setPowerLevel(data[4]);
                byte[] openType = new byte[1];
                openType[0] = (byte) (data[5] & 0xf);
                openLog.setOpenType(new String(Hex.encode(openType, 0, openType.length)).trim());
                byte[] alarmType = new byte[1];
                alarmType[0] = (byte) (data[5] & 0xf0);
                openLog.setAlarmType(new String(Hex.encode(alarmType, 0, alarmType.length)).trim());
                //openLog.setPersonId(new String(Hex.encode(data, 6, 4)).replaceAll("^(0+)", ""));
                openLog.setPersonId(new String(Hex.encode(data, 6, 4)));
                openLog.setTempId(Integer.valueOf(new String(Hex.encode(data, 10, 1))));
                openLogList.add(openLog);
            }
        }
        return new OpenLogResponse(openLogList);
    }

    private BaseStation analysisBaseStation(byte[] body) {
        BaseStation baseStation = new BaseStation();
        String trim = new String(body, 0, body.length).trim().replaceAll("\"", "");
        trim += " ";
        String[] split = trim.split(",");
        if (split.length == 13) {
            baseStation.setMode(split[0]);
            baseStation.setEarfcn(split[1]);
            baseStation.setEarfcnOffset(split[2]);
            baseStation.setPci(split[3]);
            baseStation.setCellid(split[4]);
            baseStation.setRsrp(split[5]);
            baseStation.setRsrq(split[6]);
            baseStation.setRssi(split[7]);
            baseStation.setSnr(split[8]);
            baseStation.setBand(split[9]);
            baseStation.setTac(split[10]);
            baseStation.setEcl(split[11]);
            baseStation.setTxPwr(split[12].trim());
        }
        return baseStation;
    }

    List<byte[]> makeFirmwareUpdateCmdList(byte command, byte[] data) throws Exception {
        byte[] firstPacket = getFirmwareUpdateFirstPacket(data);
        List<byte[]> splitPacketList = splitFirmwareUpdateData(data);
        int totalPacketNum = splitPacketList.size() + 1;
        List<byte[]> splitPacketCmdList = new ArrayList<>();
        String firstCmdStr = Packet.makePacket(false, null, command, 1, (byte) 0, (byte) 0, firstPacket, 0, firstPacket.length);
        byte[] firstCmd = addHeadAndTail(firstCmdStr.getBytes());
        splitPacketCmdList.add(firstCmd);
        for (int i = 1; i < totalPacketNum; i++) {
            String splitCmdStr = Packet.makePacket(false, null, command, 1, (byte) 0, (byte) 0, splitPacketList.get(i - 1), 0, splitPacketList.get(i - 1).length);
            byte[] splitCmd = addHeadAndTail(splitCmdStr.getBytes());
            splitPacketCmdList.add(splitCmd);
        }
        return splitPacketCmdList;
    }

    static byte[] addHeadAndTail(byte[] data) {
        byte[] protocolData = new byte[data.length + 2];
        protocolData[0] = PROTOCOL_HEAD;
        System.arraycopy(data, 0, protocolData, 1, data.length);
        protocolData[protocolData.length - 1] = PROTOCOL_END;
        return protocolData;
    }

    private byte[] getFirmwareUpdateFirstPacket(byte[] data) {
        byte[] packet = new byte[9];
        int length = data.length;
        int totalPacketNum = length % PACKET_SIZE == 0 ? length / PACKET_SIZE : (length / PACKET_SIZE + 1);
        int crc = (int) new CRC16().caluCRC(data, data.length);
        packet[0] = (byte) ((totalPacketNum >> 8) & 0xFF);
        packet[1] = (byte) (totalPacketNum & 0xFF);
        packet[2] = (byte) ((length >> 16) & 0xFF);
        packet[3] = (byte) ((length >> 8) & 0xFF);
        packet[4] = (byte) (length & 0xFF);
        packet[5] = (byte) ((PACKET_SIZE >> 8) & 0xFF);
        packet[6] = (byte) (PACKET_SIZE & 0xFF);
        packet[7] = (byte) ((crc >> 8) & 0xFF);
        packet[8] = (byte) (crc & 0xFF);
        return packet;
    }

    private List<byte[]> splitFirmwareUpdateData(byte[] data) {
        List<byte[]> packetList = new ArrayList<>();
        int packetNum = 0;
        for (int i = 0; i < data.length; i += PACKET_SIZE) {
            int splitLength = (data.length - i) > PACKET_SIZE ? PACKET_SIZE : (data.length - i);
            byte[] split = new byte[splitLength + 2];
            split[0] = (byte) ((packetNum >> 8) & 0xFF);
            split[1] = (byte) (packetNum & 0xFF);
            System.arraycopy(data, i, split, 2, split.length - 2);
            packetList.add(split);
            packetNum++;
        }
        return packetList;
    }

    static byte[] mergeByteList(List<byte[]> list) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (byte[] bytes : list) {
            out.write(bytes);
        }
        return out.toByteArray();
    }

}
