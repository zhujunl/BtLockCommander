package com.miaxis.btlockcommanderdemo.app;


/**
 * @author tank
 * @version $
 * @des
 * @updateAuthor $
 * @updateDes
 * @blame Android Team
 */
public class CrashCatch {

    private CrashHandler mCrashHandler;

    private static CrashCatch mInstance;

    private CrashCatch() {
    }

    public static CrashCatch getInstance() {
        if (mInstance == null) {
            synchronized (CrashCatch.class) {
                if (mInstance == null) {
                    mInstance = new CrashCatch();
                }
            }
        }

        return mInstance;
    }

    public static void init(CrashHandler crashHandler) {
        getInstance().setCrashHandler(crashHandler);
    }

    void setCrashHandler(CrashHandler crashHandler) {
        mCrashHandler = crashHandler;
        //主线程异常拦截
        new android.os.Handler(android.os.Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        android.os.Looper.loop();
                    } catch (Exception e1) {
                        if (mCrashHandler != null) {
                            //处理异常
                            mCrashHandler.handlerException(android.os.Looper.getMainLooper().getThread(), e1);
                        }
                    } catch (Throwable e) {
                        if (mCrashHandler != null) {
                            //处理异常
                            mCrashHandler.handlerException(android.os.Looper.getMainLooper().getThread(), new Exception(e));
                        }
                    }
                }
            }
        });

        //所有线程异常拦截，由于主线程的异常都被catch住了，所以下面的代码拦截到的都是子线程的异常
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                if (mCrashHandler != null) {
                    //处理异常
                    mCrashHandler.handlerException(t, new Exception(e));
                }
            }
        });
    }

    public interface CrashHandler {

        void handlerException(Thread t, Exception e);

    }

}
