/*package com.crash;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;




public class CrashHandler implements Thread.UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";

    // 绯荤粺榛橀敓杈冪鎷稶ncaughtException閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷�
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    // CrashHandler瀹為敓鏂ゆ嫹
    private static CrashHandler INSTANCE = new CrashHandler();
    // 閿熸枻鎷烽敓鏂ゆ嫹閿熺春ontext閿熸枻鎷烽敓鏂ゆ嫹
    private Context mContext;

    //閿熸枻鎷烽敓鏂ゆ嫹閿熻姤鍌ㄩ敓鍊熷閿熸枻鎷锋伅閿熸枻鎷烽敓灞婂父閿熸枻鎷锋伅
    private Map<String, String> infos = new HashMap<String, String>();

    //閿熸枻鎷烽敓鑺傞潻鎷峰紡閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷�,閿熸枻鎷蜂负閿熸枻鎷峰織閿熶茎纭锋嫹閿熸枻鎷烽敓鎻紮鎷烽敓鏂ゆ嫹閿燂拷
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    *//**
     * 閿熸枻鎷疯瘉鍙敓鏂ゆ嫹涓�閿熸枻鎷稢rashHandler瀹為敓鏂ゆ嫹
     *//*
    private CrashHandler() {
    }

    *//**
     * 閿熸枻鎷峰彇CrashHandler瀹為敓鏂ゆ嫹 ,閿熸枻鎷烽敓鏂ゆ嫹妯″紡
     *//*
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    *//**
     * 閿熸枻鎷峰閿熸枻鎷�
     * @param context
     *//*
    public void init(Context context) {
        mContext = context;
        // 閿熸枻鎷峰彇绯荤粺榛橀敓杈冪鎷稶ncaughtException閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷�
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 閿熸枻鎷烽敓鐭潻鎷稢rashHandler涓洪敓鏂ゆ嫹閿熸枻鎷烽敓渚ヮ剨鎷锋礂閿熸枻鎷烽敓鏂ゆ嫹閿燂拷
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    *//**
     * 閿熸枻鎷稶ncaughtException閿熸枻鎷烽敓鏂ゆ嫹鏃堕敓鏂ゆ嫹杞敓鏂ゆ嫹鐓ら敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓锟�
     *//*
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        // ToastUtil.show(mContext, ex.getMessage());
        if (!handleException(ex) && mDefaultHandler != null) {
            // 閿熸枻鎷烽敓鏂ゆ嫹娌￠敓鐭紮鎷峰啓閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓杈冧綆琛剨鎷风郴閿熸枻鎷锋枑锝忔嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓锟�
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }
            // 閿熷壙绛规嫹閿熸枻鎷烽敓鏂ゆ嫹
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
//            KJActivityStack.create().AppExit(mContext);// 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鎺ワ讣鎷疯锤閿熸枻鎷烽敓锟�
        }
    }

    *//**
     * 閿熺殕璁规嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓锟�,閿熺Ц纭锋嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷锋伅 閿熸枻鎷烽敓閰佃揪鎷烽敓瑗熸姤闈╂嫹鐐旈敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹璇栭敓鏂ゆ嫹閿熸枻鎷�.
     * @param ex
     * @return true:閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷风儊閿熸枻鎷锋枑锝忔嫹閿熻緝锟�;閿熸枻鎷烽敓娲ヨ繑浼欐嫹false.
     *//*
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        // 浣块敓鏂ゆ嫹Toast閿熸枻鎷烽敓鏂ゆ嫹绀洪敓灞婂父閿熸枻鎷锋伅
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "閿熸澃鎲嬫嫹姝�,閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹鏂愶拷,閿熸枻鎷烽敓鏂ゆ嫹閿熷壙绛规嫹.", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        //閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷峰織閿熶茎纭锋嫹
        String path = saveCrashInfo2File(ex);
        return true;
    }

    *//**
     * 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熻緝顫嫹閿熸枻鎷峰嫙閿熸枻鎷烽敓锟�
     * @param ex
     * @return 閿熸枻鎷烽敓鏂ゆ嫹閿熶茎纭锋嫹閿熸枻鎷烽敓锟�, 閿熸枻鎷烽敓鑺傛枻鎷烽敓渚ョ》鎷烽敓鏂ゆ嫹閿熼叺纰夋嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷�
     *//*
    private String saveCrashInfo2File(Throwable ex) {
        infos = PhoneUtils.collectDeviceInfo(mContext);
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        FileOutputStream fos = null;
        try {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = "crash-" + time + "-" + timestamp + ".log";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = "/sdcard/aaaaaaaaaaaaaaaaaaaaacrash/";
//                String path = mContext.getCacheDir() + "/crash/";
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                fos = new FileOutputStream(path + fileName);
                fos.write(sb.toString().getBytes());
            }
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                Log.e(TAG, "an error occured while fos.close...", e);
            }
        }
        return null;
    }
}*/

package com.thecamhi.base;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.thecamhi.utils.MemoryInfo;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;



public class CrashHandler implements UncaughtExceptionHandler {
	public static final String TAG = "CrashHandler";
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	private static CrashHandler instance;
	private Context mContext;
	private Map<String, String> infos = new HashMap<String, String>();
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	private CrashHandler() {
	}
	public static synchronized CrashHandler getInstance() {
		if (instance == null)
			instance = new CrashHandler();
		return instance;
	}
	/**
	 */
	public void init(Context context) {
		mContext = context;
		

		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}
	/**
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {

		if (!handleException(ex) && mDefaultHandler != null) {
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Log.e(TAG, "error : ", e);
			}
			/**
			 * 不要把进程杀死了，否则有时连日志都看不到
			 */
			//android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}
	/**
	 * @param ex
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		collectDeviceInfo(mContext);
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();

				Looper.loop();
			}
		}.start();
		saveCatchInfo2File(ex);
		return true;
	}
	/**
	 * @param ctx
	 */
	public void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null" : pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
			infos.put("memoryinfo", "可用内存:"+MemoryInfo.getAvailMemory(ctx)+":::"+MemoryInfo.getTotalMemory(ctx));
		} catch (NameNotFoundException e) {
			Log.e(TAG, "an error occured when collect package info", e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
				Log.d(TAG, field.getName() + " : " + field.get(null));
			} catch (Exception e) {
				Log.e(TAG, "an error occured when collect crash info", e);
			}
		}
	}
	private String getFilePath() {
		String file_dir = "";
		boolean isSDCardExist = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
		// Environment.getExternalStorageDirectory()閿熸磥褰撻敓鏂ゆ嫹File file=new File("/sdcard")
		boolean isRootDirExist = Environment.getExternalStorageDirectory().exists();
		if (isSDCardExist && isRootDirExist) {
			file_dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Snapshot/crashlog/";
		} else {
			file_dir = CrashApplication.getInstance().getFilesDir().getAbsolutePath() + "/Snapshot/crashlog/";
		}
		return file_dir;
	}
	/**
	 * @param ex
	 */
	private String saveCatchInfo2File(Throwable ex) {
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		try {
			long timestamp = System.currentTimeMillis();
			String time = formatter.format(new Date());
			String fileName = "crash-" + time + "-" + timestamp + ".log";
			String file_dir = getFilePath();
			//			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File dir = new File(file_dir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File file = new File(file_dir + fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(sb.toString().getBytes());
			sendCrashLog2PM(file_dir + fileName);
			fos.close();
			//			}
			return fileName;
		} catch (Exception e) {
			Log.e(TAG, "an error occured while writing file...", e);
		}
		return null;
	}
	/**
	 */
	private void sendCrashLog2PM(String fileName) {
		//		if (!new File(fileName).exists()) {
		//			Toast.makeText(mContext, "閿熸枻鎷峰織閿熶茎纭锋嫹閿熸枻鎷烽敓鏂ゆ嫹閿熻妭锝忔嫹", Toast.LENGTH_SHORT).show();
		//			return;
		//		}
		//		FileInputStream fis = null;
		//		BufferedReader reader = null;
		//		String s = null;
		//		try {
		//			fis = new FileInputStream(fileName);
		//			reader = new BufferedReader(new InputStreamReader(fis, "GBK"));
		//			while (true) {
		//				s = reader.readLine();
		//				if (s == null)
		//					break;
		//				Log.i("info", s.toString());
		//			}
		//		} catch (FileNotFoundException e) {
		//			e.printStackTrace();
		//		} catch (IOException e) {
		//			e.printStackTrace();
		//		} finally {
		//			try {
		//				reader.close();
		//				fis.close();
		//			} catch (IOException e) {
		//				e.printStackTrace();
		//			}
		//		}
	}
}
