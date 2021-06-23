package com.miaxis.btlockcommander.manager;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.miaxis.btlockcommander.callback.BleFileResponseWriterCallback;
import com.miaxis.btlockcommander.callback.BleResponseWriterCallback;
import com.miaxis.btlockcommander.callback.BleWriteCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class SplitResponseWriter {

//    private static final int SINGLE_TRANSMISSION_UPPER_LIMIT = 220;
    private static final int MSG_SPLIT_FILE_WRITE_NEXT = 0x34;

    private HandlerThread handlerThread;
    private Handler handler;

    private BleManager bleManager;
    private String uuid_service;
    private String uuid_write;
    private Queue<byte[]> dataQueue;
    private int totalNum;
    private BleResponseWriterCallback bleResponseWriterCallback;
    private BleFileResponseWriterCallback bleFileResponseWriterCallback;

    SplitResponseWriter(BleManager bleManager, String uuid_service, String uuid_write) {
        this.bleManager = bleManager;
        this.uuid_service = uuid_service;
        this.uuid_write = uuid_write;
        handlerThread = new HandlerThread("splitFileWriter");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == MSG_SPLIT_FILE_WRITE_NEXT) {
                    write();
                }
            }
        };
    }

    void splitDataResponseWrite(List<byte[]> packetCmdList, BleResponseWriterCallback bleResponseWriterCallback) throws IOException {
        this.bleResponseWriterCallback = bleResponseWriterCallback;
        dataQueue = makeQueue(packetCmdList);
        totalNum = dataQueue.size();
        write();
    }

    void splitFileResponseWrite(List<byte[]> packetCmdList, BleFileResponseWriterCallback bleFileResponseWriterCallback) throws IOException {
        this.bleFileResponseWriterCallback = bleFileResponseWriterCallback;
        dataQueue = makeQueue(packetCmdList);
        totalNum = dataQueue.size();
        write();
    }

    boolean continueRestTransmission() {
        if (dataQueue.peek() != null) {
            Message message = handler.obtainMessage(MSG_SPLIT_FILE_WRITE_NEXT);
            handler.sendMessage(message);
            return true;
        } else {
            release();
            return false;
        }
    }

    private void write() {
        byte[] data = dataQueue.poll();
        bleManager.write(uuid_service, uuid_write, data, bleWriteCallback);
    }

    private void release() {
        handlerThread.quit();
        handler.removeCallbacksAndMessages(null);
    }

    private static Queue<byte[]> makeQueue(List<byte[]> packetCmdList) {
        Queue<byte[]> byteQueue = new LinkedList<>();
        for (byte[] bytes : packetCmdList) {
            byteQueue.offer(bytes);
        }
        return byteQueue;
    }

    private static Queue<byte[]> splitByte(List<byte[]> packetCmdList, int limit) throws IOException {
        Queue<byte[]> byteQueue = new LinkedList<>();
        int count = 0;
        while (count != packetCmdList.size()) {
            int singleSize = 0;
            List<byte[]> cmdCacheList = new ArrayList<>();
//            if (count == 0) {
//                //第一包信息包不分割
//                byte[] firstCmd = packetCmdList.get(0);
//                cmdCacheList.add(firstCmd);
//                singleSize += firstCmd.length;
//                count = 1;
//            }
            for (int i = count; i < packetCmdList.size(); i++) {
                byte[] cmdCache = packetCmdList.get(i);
                if ((limit - singleSize) > cmdCache.length) {
                    cmdCacheList.add(cmdCache);
                    singleSize += cmdCache.length;
                    count++;
                } else {
                    break;
                }
            }
            byteQueue.offer(DataAnalysis.mergeByteList(cmdCacheList));
        }
        return byteQueue;
    }

    private BleWriteCallback bleWriteCallback = new BleWriteCallback() {
        @Override
        public void onWriteSuccess(int current, int total, byte[] justWrite) {
            int position = totalNum - dataQueue.size();
            if (bleResponseWriterCallback != null) {
                bleResponseWriterCallback.onResponseWriteSuccess(position, totalNum, justWrite);
            } else if (bleFileResponseWriterCallback != null) {
                bleFileResponseWriterCallback.onFileResponseWriteSuccess(position, totalNum, justWrite);
            }
            Log.e("asd", "SplitResponseWriter:onWriteSuccess-" + position +  " , " + totalNum);
        }

        @Override
        public void onWriteFailure(String message) {
            if (bleResponseWriterCallback != null) {
                bleResponseWriterCallback.onResponseWriteFailed(message);
            } else if (bleFileResponseWriterCallback != null) {
                bleFileResponseWriterCallback.onFileResponseWriteFailed(message);
            }
        }
    };

}
