package task;


import org.apache.log4j.Logger;

import dao.MysqlConnectionManager;
import data.SharedCritical;
import data.SharedResource;

/**
 * 날짜 : 2016. 12. 2. 프로젝트 : OAMD 생성자 : 김연호
 */
public class ThreadManager implements Runnable {

	Logger Log = Logger.getLogger("OAMD");

	private Runnable monitorRun;
	private Runnable criticalvaluepollRun;
	private Runnable messagequeuepollRun;
	private Thread monitorThread = null;
	private Thread criticalvaluepollThread = null;
	private Thread messagequeuepollThread = null;

	public void Init() {

		ResourceMonitorThread monitorThread = new ResourceMonitorThread();
		CriticalValuePollingThread criticalValuepoolThread = new CriticalValuePollingThread();
		MessageQueuePollingThread messageQueuePollingThread = new MessageQueuePollingThread();

		new MysqlConnectionManager();

		SharedResource.Init();
		SharedCritical.Init();

		monitorThread.Init();
		criticalValuepoolThread.Init();
		messageQueuePollingThread.Init();
	}

	@SuppressWarnings("static-access")
	@Override
	public void run() {
		// TODO Auto-generated method stub
		monitorRun = new ResourceMonitorThread();
		criticalvaluepollRun = new CriticalValuePollingThread();
		messagequeuepollRun = new MessageQueuePollingThread();
		
		Log.info(" -> Thread Manager Start");
		try {
			while (!Thread.currentThread().interrupted()) {
				if (monitorThread == null) {
					monitorThread = new Thread(monitorRun);
					monitorThread.setDaemon(true);
					monitorThread.start();
				}

				if (criticalvaluepollThread == null) {
					criticalvaluepollThread = new Thread(criticalvaluepollRun);
					criticalvaluepollThread.setDaemon(true);
					criticalvaluepollThread.start();
				}

				if (messagequeuepollThread == null) {
					messagequeuepollThread = new Thread(messagequeuepollRun);
					messagequeuepollThread.setDaemon(true);
					messagequeuepollThread.start();
				}

				if (!monitorThread.isAlive()) {
					monitorThread = new Thread(monitorRun);
					monitorThread.setDaemon(true);
					monitorThread.start();
				}

				if (!criticalvaluepollThread.isAlive()) {
					criticalvaluepollThread = new Thread(criticalvaluepollRun);
					criticalvaluepollThread.setDaemon(true);
					criticalvaluepollThread.start();
				}

				if (!messagequeuepollThread.isAlive()) {
					messagequeuepollThread = new Thread(messagequeuepollRun);
					messagequeuepollThread.setDaemon(true);
					messagequeuepollThread.start();
				}
				
				Thread.sleep(500);
			}
		} catch (InterruptedException e) {
			Log.fatal("Thread Manager Interrupt Exception", e);
		} finally {
			Log.fatal("Thread Manager Thread EXIT");
		}
	}
}