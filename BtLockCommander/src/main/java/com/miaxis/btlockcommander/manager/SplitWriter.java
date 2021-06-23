package com.miaxis.btlockcommander.manager;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.miaxis.btlockcommander.callback.BleWriteCallback;
import com.miaxis.btlockcommander.util.LoggerFactory;

import java.util.LinkedList;
import java.util.Queue;

class SplitWriter {

    public static final int DEFAULT_WRITE_DATA_SPLIT_COUNT = 20;
    private static final int MSG_SPLIT_WRITE_NEXT = 0x33;

    private HandlerThread handlerThread;
    private Handler handler;

    private BleBluetooth bleBluetooth;
    private String uuid_service;
    private String uuid_write;
    private Queue<byte[]> dataQueue;
    private int totalNum;
    private boolean sendNextWhenLastSuccess;
    private boolean sendNextWhenLastFailed;
    private long intervalBetweenTwoPackage;
    private BleWriteCallback callback;

    SplitWriter(BleBluetooth bleBluetooth, String uuid_service, String uuid_write) {
        this.bleBluetooth = bleBluetooth;
        this.uuid_service = uuid_service;
        this.uuid_write = uuid_write;
        handlerThread = new HandlerThread("splitWriter");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == MSG_SPLIT_WRITE_NEXT) {
                    write();
                }
            }
        };
    }

    void splitWrite(byte[] data, boolean sendNextWhenLastSuccess, boolean sendNextWhenLastFailed, long intervalBetweenTwoPackage, BleWriteCallback callback) {
        this.sendNextWhenLastSuccess = sendNextWhenLastSuccess;
        this.sendNextWhenLastFailed = sendNextWhenLastFailed;
        this.intervalBetweenTwoPackage = intervalBetweenTwoPackage;
        this.callback = callback;
        dataQueue = splitByte(data, DEFAULT_WRITE_DATA_SPLIT_COUNT);
        totalNum = dataQueue.size();
        write();
    }

    private LoggerFactory mLoggerFactory = new LoggerFactory(this);

    private void write() {
        if (dataQueue.peek() == null) {
            release();
            return;
        }
        byte[] data = dataQueue.poll();
        BleConnector bleConnector = bleBluetooth.getConnector();
        if (bleConnector != null) {
            bleConnector.withUUID(uuid_service, uuid_write)
                    .writeCharacteristic(data, new BleWriteCallback() {
                        @Override
                        public void onWriteSuccess(int current, int total, byte[] justWrite) {
                            int position = totalNum - dataQueue.size();
                            mLoggerFactory.e( position + "," + totalNum + "成功");
                            if (position == totalNum) {
                                callback.onWriteSuccess(position, totalNum, justWrite);
                            }
                            if (sendNextWhenLastSuccess) {
                                Message message = handler.obtainMessage(MSG_SPLIT_WRITE_NEXT);
                                handler.sendMessageDelayed(message, intervalBetweenTwoPackage);
                            }
                        }

                        @Override
                        public void onWriteFailure(String message) {
                            int position = totalNum - dataQueue.size();
                            mLoggerFactory.e( position + "," + totalNum + "失败");
                            callback.onWriteFailure("exception occur while writing: " + message);
                            if (sendNextWhenLastFailed) {
                                Message handleMessage = handler.obtainMessage(MSG_SPLIT_WRITE_NEXT);
                                handler.sendMessageDelayed(handleMessage, intervalBetweenTwoPackage);
                            }
                        }
                    });
            if (!sendNextWhenLastSuccess) {
                Message message = handler.obtainMessage(MSG_SPLIT_WRITE_NEXT);
                handler.sendMessageDelayed(message, intervalBetweenTwoPackage);
            }
        }
    }

    private void release() {
        handlerThread.quit();
        handler.removeCallbacksAndMessages(null);
    }

    private static Queue<byte[]> splitByte(byte[] data, int count) {
        Queue<byte[]> byteQueue = new LinkedList<>();
        int pkgCount;
        if (data.length % count == 0) {
            pkgCount = data.length / count;
        } else {
            pkgCount = Math.round(data.length / count + 1);
        }
        if (pkgCount > 0) {
            for (int i = 0; i < pkgCount; i++) {
                byte[] dataPkg;
                int j;
                if (pkgCount == 1 || i == pkgCount - 1) {
                    j = data.length % count == 0 ? count : data.length % count;
                    System.arraycopy(data, i * count, dataPkg = new byte[j], 0, j);
                } else {
                    System.arraycopy(data, i * count, dataPkg = new byte[count], 0, count);
                }
                byteQueue.offer(dataPkg);
            }
        }
        return byteQueue;
    }

}
