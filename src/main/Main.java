package main;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import org.apache.log4j.Logger;
import org.hyperic.sigar.Humidor;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;
import org.hyperic.sigar.ptql.ProcessFinder;

import data.SharedInitData;
import task.ThreadManager;
import util.Readfile;

/**
 * 날짜 : 2016. 12. 2. 프로젝트 : OAMD 생성자 : 김연호
 */
public class Main {

	static Logger Log = Logger.getLogger("OAMD");

	private static File f;
	private static FileChannel channel;
	private static FileLock lock;

	@SuppressWarnings("resource")
	protected static boolean isRunnable() {

		boolean runnable = false;

		try {
			f = new File(SharedInitData.PROCESS_PATH);

			if (f.exists()) {
				f.delete();
			}

			channel = new RandomAccessFile(f, "rw").getChannel();

			lock = channel.tryLock();
			if (lock == null) {
				channel.close();
				throw new Exception();
			}

			HookShutdown shutdownHook = new HookShutdown();
			Runtime.getRuntime().addShutdownHook(shutdownHook);

			runnable = true;

		} catch (Exception e) {

		}

		return runnable;
	}

	public static void unlockFile() {

		try {
			if (lock != null) {
				lock.release();
				channel.close();
				f.delete();
				Log.info("Bye OAMD");
				Log.info("Shutdown & Unlock finish successfully.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static class HookShutdown extends Thread {
		public void run() {
			unlockFile();
		}
	}

	public void Init() {
		Readfile confFile = new Readfile();
		String value = "";

		try {
			SharedInitData.sys_id = confFile.getConfFile(SharedInitData.CONF_PATH, "SYSTEM_ID");
			SharedInitData.sys_name = confFile.getConfFile(SharedInitData.CONF_PATH, "SYSTEM_NAME");
			value = confFile.getConfFile(SharedInitData.CONF_PATH, "MONITOR_PERIOD");
			SharedInitData.monitor_period = Integer.parseInt(value);
			value = confFile.getConfFile(SharedInitData.CONF_PATH, "OVERLOAD_CNT");
			SharedInitData.overload_cnt = Integer.parseInt(value);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.fatal(String.format("(%s)Conf File Exception Error Check the SYSTEM configfile",
					this.getClass().getName()), e);
			System.exit(0);
		}

		Log.info(String.format("SYSTEM_ID       : [%s]", SharedInitData.sys_id));
		Log.info(String.format("SYSTEM_NAME     : [%s]", SharedInitData.sys_name));
		Log.info(String.format("MONITOR_PERIOD  : [%s]", String.valueOf(SharedInitData.monitor_period)));
		Log.info(String.format("OVERLOAD_CNT    : [%s]", String.valueOf(SharedInitData.overload_cnt)));
	}

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		Main main = new Main();

		//------------------------------------------------------------------------------------------------------------
		Humidor h;
		h = Humidor.getInstance();
		SigarProxy sp = h.getSigar(); 
		Sigar sigar1 = new Sigar();
		try {
			long[] pidList = sp.getProcList();
			for (int i=0; i < pidList.length; i++)  {
				long[] pid1 = ProcessFinder.find(sigar1, "State.Name.eq=" + "MOXI");
				//System.out.println(sp.getProcCredName(pidList[i]).toString());
			}
		} catch (SigarException e)  {
		}
		
		
		//------------------------------------------------------------------------------------------------------------
		
		
		
		ThreadManager threadManager = new ThreadManager();
		Runnable runnable = new ThreadManager();
		Thread thread = new Thread(runnable);

		if (!isRunnable()) {
			Log.error("existing process OAM");
			return;
		}

		try {
			main.Init();
			threadManager.Init();
		} catch (Exception e) {
			Log.fatal("Main Thread Init Fail", e);
			System.exit(0);
		}

		Log.info("HELLO OAMD");
		Log.info("Version - 1.0.0 Creator - KYH");
		Log.info(String.format("System id = [%s], System_name = [%s]", SharedInitData.sys_id, SharedInitData.sys_name));
		Log.info("Last Update - 2016.11.30");
		Log.info("Descrition[최초 생성]");
		Log.info("OAMD START");

		thread.setDaemon(true);
		thread.start();
		thread.join();

	}
}
