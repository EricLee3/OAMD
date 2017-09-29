package task;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import dao.ResourceHandlingDaoMapper;
import data.SharedCritical;
import util.PrintValue;

/**
 * 날짜 : 2016. 12. 2. 프로젝트 : OAMD 생성자 : 김연호
 */
public class CriticalValuePollingThread implements Runnable {

	Logger Log = Logger.getLogger("OAMD");

	private static boolean InitFail = false;
	private final static int SECOND = 1000;

	/**
	 * 작성 일자 : 2016. 12. 6. 작성자 : 김연호 비고 : 알람 임계치에 대한 정보를 가져온다. 메소드 : Init
	 */
	public void Init() {
		ArrayList<String> list = null;
		ResourceHandlingDaoMapper handlingDao = new ResourceHandlingDaoMapper();

		if (handlingDao.getDbState()) {
			for (int resource = 0; resource < SharedCritical.MAX_RESOURCE_CNT; resource++) {
				list = handlingDao.getMecs5AlarmLimit(String.valueOf(resource));

				if (list.size() == 0) {
					Log.info("Critical value no data found[Alarm Happen]");
					InitFail = true;
					list.add("0"); // Critical Value
					list.add("0"); // Major value
					list.add("0"); // Minor valuu
					SharedCritical.CRITICAL_VALUE.add(resource, list);

					ConcurrentHashMap<String, String> DbAlarmdata = new ConcurrentHashMap<>();
					DbAlarmdata = handlingDao.getAlarmExist(SharedCritical.INIT_ALARM_CODE);
					if (DbAlarmdata.size() != 0) {
						handlingDao.putAlarmstate(DbAlarmdata.get("alarm_lv"), SharedCritical.ALARM_ON,
								DbAlarmdata.get("alarm_desc"), SharedCritical.INIT_ALARM_CODE,
								DbAlarmdata.get("audio_flag"));
						handlingDao.putAlarmstatehistory_ON(DbAlarmdata.get("alarm_lv"),
								SharedCritical.INIT_ALARM_CODE);
					}
				} else {
					Log.info(String.format("CRITICAL VALUE SETTING LV SUCCESS[%s]",
							resource == SharedCritical.RESOURCE_CPU ? "CPU"
									: resource == SharedCritical.RESOURCE_DISK ? "DISK" : "MEMORY"));
					SharedCritical.CRITICAL_VALUE.add(resource, list);
				}
			}
		} else {
			Log.error("Init Fail Check the Db state");
		}
		PrintValue.printCriticalValue();
	}

	@SuppressWarnings("static-access")
	public void run() {
		Log.info(" -> Critical Value Poll Thread Start");
		ResourceHandlingDaoMapper handlingDao = new ResourceHandlingDaoMapper();

		try {
			while (!Thread.currentThread().interrupted()) {
				try {
					if (handlingDao.getDbState()) {
						for (int resource = 0; resource < SharedCritical.MAX_RESOURCE_CNT; resource++) {
							ArrayList<String> list = null;
							list = handlingDao.getMecs5AlarmLimit(String.valueOf(resource));

							if (list.size() == 0) {
								Log.error("CRITICAL VALUE NO DATA FOUND -> CHECK ALARM_LIMIT TABLE");
								break;
							} else {
								if (InitFail) {
									InitFail = false;

									int ret = handlingDao.getAlarmHappen(SharedCritical.INIT_ALARM_CODE);
									if (ret == 0) {
										Log.info(String.format("INIT Fail Alarm Code[%s] OFF",
												SharedCritical.INIT_ALARM_CODE));
										handlingDao.putAlarmstatehistory_OFF(String.valueOf(SharedCritical.CRITICAL_LV),
												SharedCritical.ALARM_OFF, SharedCritical.INIT_ALARM_CODE);
										handlingDao.removeAlarmstate(SharedCritical.INIT_ALARM_CODE);
									}
								} else {
									SharedCritical.CRITICAL_VALUE.set(resource, list);
								}
							}
						}
					} else {
						Log.error("check the Db state");
					}
				} catch (Exception e) {
					Log.fatal("Thread Error");
				}
				Thread.sleep(SECOND);
			}
		} catch (InterruptedException e) {
			// TODO: handle exception
			Log.fatal("CriticalValuepool Thread Interrupted Exception", e);
		} finally {
			Log.fatal("CriticalValuepool Thread Exit");
		}
	}
}
