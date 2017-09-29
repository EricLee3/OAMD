package task;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import dao.ResourceHandlingDaoMapper;
import data.ResourceMonitor;
import data.SharedCritical;
import data.SharedInitData;
import data.SharedResource;
import util.PrintValue;

/**
 * 날짜 : 2016. 12. 6. 프로젝트 : OAMD 생성자 : 김연호
 */

public class ResourceMonitorThread extends ResourceMonitor implements Runnable {

	Logger Log = Logger.getLogger("OAMD");

	private final static int SECOND = 1000;

	private int getLevelPosition(int now_lv) {
		int level_Position = 0;
		switch (now_lv) {
		case SharedCritical.CRITICAL_LV:
			level_Position = 0;
			break;

		case SharedCritical.MAJOR_LV:
			level_Position = 1;
			break;

		case SharedCritical.MINOR_LV:
			level_Position = 2;
			break;

		default:
			break;
		}

		return level_Position;
	}

	private void AlarmHandling(ConcurrentHashMap<String, Integer> data, String alarm_code, String alarm_desc,
			ResourceHandlingDaoMapper handlingDao) {

		int now_lv = data.get("nowLv");
		int old_lv = data.get("oldLv");
		int alarm_idx = data.get("alarmIdx");
		int value = data.get("value");
		int visual_flag = data.get("visual_flag");
		int level_Position = getLevelPosition(now_lv);
		String audio_flag = String.valueOf(data.get("audio_flag"));

		String alarm_detail = String.format("%s VALUE[%s] > LIMIT[%s]", alarm_desc, value,
				SharedCritical.CRITICAL_VALUE.get(alarm_idx).get(level_Position));

		ConcurrentHashMap<String, String> history = new ConcurrentHashMap<>();
		history.put("alarm_code", alarm_code);
		history.put("alarm_level", String.valueOf(now_lv));
		history.put("alarm_desc", alarm_detail);

		if (old_lv == 0) {
			Log.info(String.format("Fist Alarm ON %s CODE:[%s] LV:[%s] VISUAL_FLAG[%s]", alarm_detail, alarm_code,
					String.valueOf(now_lv), data.get("visual_flag")));

			if (visual_flag == 1) {
				handlingDao.putAlarmstate(String.valueOf(now_lv), SharedCritical.ALARM_ON, alarm_detail, alarm_code,
						audio_flag);
				handlingDao.putAlarmstatehistory_ON(String.valueOf(now_lv), alarm_code);
				return;
			}
			handlingDao.putAlarmstatehistory_ON(history);

		} else {
			switch (now_lv) {
			case 0:
				Log.info(String.format("Alarm OFF %s LV:[%s] VISUAL_FLAG[%s]", alarm_detail, String.valueOf(old_lv),
						data.get("visual_flag")));

				if (visual_flag == 1) {
					handlingDao.putAlarmstatehistory_OFF(String.valueOf(old_lv), SharedCritical.ALARM_OFF, alarm_code);
					handlingDao.removeAlarmstate(alarm_code);
					return;
				}

				handlingDao.removeAlarmstate(alarm_code);
				handlingDao.putAlarmstatehistory_OFF(history);
				break;
			default:
				Log.info(String.format("Alarm OFF %s LV:[%s]", alarm_detail, String.valueOf(old_lv)));
				handlingDao.putAlarmstatehistory_OFF(String.valueOf(old_lv), SharedCritical.ALARM_OFF, alarm_code);

				Log.info(String.format("Alarm ON %s LV:[%s] VISUAL_FLAG[%s]", alarm_detail, String.valueOf(now_lv),
						data.get("visual_flag")));

				if (visual_flag == 1) {
					handlingDao.putAlarmstate_update(String.valueOf(now_lv), SharedCritical.ALARM_ON, alarm_detail,
							alarm_code);
					handlingDao.putAlarmstatehistory_ON(String.valueOf(now_lv), alarm_code);
					return;
				}

				handlingDao.putAlarmstatehistory_ON(history);
				break;
			}
		}
	}

	private int checkOverload(int alarm_idx, String rsName, int Value) {

		int overloadLevel = 0;

		for (int Level = 0; Level < SharedCritical.MAX_ALARM_LV; Level++) {

			int critacalvalue = Integer.parseInt(SharedCritical.CRITICAL_VALUE.get(alarm_idx).get(Level));

			if (Value >= critacalvalue) {
				switch (Level) {
				case 0:
					overloadLevel = SharedCritical.CRITICAL_LV;
					break;

				case 1:
					overloadLevel = SharedCritical.MAJOR_LV;
					break;

				case 2:
					overloadLevel = SharedCritical.MINOR_LV;
					break;

				default:
					Log.error("Not define AlarmLevel");
					break;
				}
				break;
			}
		}

		return overloadLevel;
	}

	private boolean calcurateDisk(String ResourceName, int alarm_idx) {
		@SuppressWarnings("rawtypes")
		Iterator iterator = SharedResource.DISK.keySet().iterator();
		int Diskvalue = 0;
		int NowOverloadLevel = 0;
		int cnt = 0;

		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			String getDisk = SharedResource.DISK.get(key);
			String value[] = getDisk.split("/");
			String alarm_code = null;

			Double useDisk = Double.parseDouble(value[0]);
			Double totalDisk = Double.parseDouble(value[1]);

			Diskvalue = (int) ((useDisk / totalDisk) * 100);

			if (key.contains("C")) {
				alarm_code = SharedCritical.DISK_C_ALARM_CODE;
			} else if (key.contains("D")) {
				alarm_code = SharedCritical.DISK_D_ALARM_CODE;
			} else if (key.contains("E")) {
				alarm_code = SharedCritical.DISK_E_ALARM_CODE;
			} else if (key.contains("F")) {
				alarm_code = SharedCritical.DISK_F_ALARM_CODE;
			} else {
				alarm_code = SharedCritical.DISK_ETC_ALARM_CODE;
			}

			ResourceHandlingDaoMapper handlingDao = new ResourceHandlingDaoMapper();
			ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

			map = handlingDao.getAlarmExist(alarm_code);
			if (map.size() == 0) {
				Log.fatal(String.format("Not Exist Alarm Code [%s]", alarm_code));
				continue;
			}

			int use_flag = Integer.parseInt(map.get("use_flag"));
			if (use_flag == 1) {
				NowOverloadLevel = checkOverload(alarm_idx, ResourceName, Diskvalue);
			} else {
				Log.info(String.format("unused Alarm CODE[%s]", SharedCritical.MEMORY_ALARM_CODE));
			}

			int visual_flag = Integer.parseInt(map.get("visual_flag"));
			int audio_flag = Integer.parseInt(map.get("audio_flag"));

			ConcurrentHashMap<String, Integer> data = new ConcurrentHashMap<>();
			data.put("nowLv", NowOverloadLevel);
			data.put("oldLv", SharedCritical.OldDiskOverloadLevel[cnt]);
			data.put("alarmIdx", alarm_idx);
			data.put("value", Diskvalue);
			data.put("visual_flag", visual_flag);
			data.put("audio_flag", audio_flag);

			if (NowOverloadLevel != SharedCritical.OldDiskOverloadLevel[cnt]) {
				AlarmHandling(data, alarm_code, map.get("alarm_desc"), handlingDao);
				SharedCritical.OldDiskOverloadLevel[cnt] = NowOverloadLevel;
			}

			if (!handlingDao.putResource(ResourceName, key, getDisk, String.valueOf(NowOverloadLevel))) {
				Log.error(String.format("(%s) putResource Fail Check Db", this.getClass().getName()));
			}

			if (!handlingDao.putResourceHistory(ResourceName, key)) {
				Log.error(String.format("(%s) putResourceHistory Fail Check Db", this.getClass().getName()));
			}

			cnt++;
		}

		return true;
	}

	private boolean calcurateMemory(String ResourceName, int alarm_idx) {
		@SuppressWarnings("rawtypes")
		Iterator iterator = SharedResource.MEMORY.keySet().iterator();
		int Memoryvalue = 0;
		int NowOverloadLevel = 0;
		int cnt = 0;

		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			String getMemory = SharedResource.MEMORY.get(key);

			String value[] = getMemory.split("/");

			double useMemory = Double.parseDouble(value[0]);
			double totalMemory = Double.parseDouble(value[1]);

			Memoryvalue = (int) ((useMemory / totalMemory) * 100);

			ResourceHandlingDaoMapper handlingDao = new ResourceHandlingDaoMapper();
			ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

			map = handlingDao.getAlarmExist(SharedCritical.MEMORY_ALARM_CODE);
			if (map.size() == 0) {
				Log.fatal(String.format("Not Exist Alarm Code [%s]", SharedCritical.MEMORY_ALARM_CODE));
				continue;
			}

			int use_flag = Integer.parseInt(map.get("use_flag"));
			if (use_flag == 1) {
				NowOverloadLevel = checkOverload(alarm_idx, ResourceName, Memoryvalue);
			} else {
				Log.info(String.format("unused Alarm CODE[%s]", SharedCritical.MEMORY_ALARM_CODE));
			}

			int visual_flag = Integer.parseInt(map.get("visual_flag"));
			int audio_flag = Integer.parseInt(map.get("audio_flag"));

			ConcurrentHashMap<String, Integer> data = new ConcurrentHashMap<>();
			data.put("nowLv", NowOverloadLevel);
			data.put("oldLv", SharedCritical.OldMemoryOverloadLevel[cnt]);
			data.put("alarmIdx", alarm_idx);
			data.put("value", Memoryvalue);
			data.put("visual_flag", visual_flag);
			data.put("audio_flag", audio_flag);

			if (NowOverloadLevel != SharedCritical.OldMemoryOverloadLevel[cnt]) {
				AlarmHandling(data, SharedCritical.MEMORY_ALARM_CODE, map.get("alarm_desc"), handlingDao);
				SharedCritical.OldMemoryOverloadLevel[cnt] = NowOverloadLevel;
			}

			if (!handlingDao.putResource(ResourceName, key, getMemory, String.valueOf(NowOverloadLevel))) {
				Log.error(String.format("(%s) putResource Fail Check Db", this.getClass().getName()));
			}

			if (!handlingDao.putResourceHistory(ResourceName, key)) {
				Log.error(String.format("(%s) putResourceHistory Fail Check Db", this.getClass().getName()));
			}

			cnt++;
		}

		return true;
	}

	private boolean calcurateCpu(String ResourceName, int alarm_idx) {
		@SuppressWarnings("rawtypes")
		Iterator iterator = SharedResource.CPU.keySet().iterator();
		int Cpuvalue = 0;
		int NowOverloadLevel = 0;
		int cnt = 0;

		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			Cpuvalue = SharedResource.CPU.get(key);

			ResourceHandlingDaoMapper handlingDao = new ResourceHandlingDaoMapper();
			ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

			map = handlingDao.getAlarmExist(SharedCritical.CPU_ALARM_CODE);
			if (map.size() == 0) {
				Log.fatal(String.format("Not Exist Alarm Code [%s]", SharedCritical.CPU_ALARM_CODE));
				continue;
			}

			int use_flag = Integer.parseInt(map.get("use_flag"));
			if (use_flag == 1) {
				NowOverloadLevel = checkOverload(alarm_idx, ResourceName, Cpuvalue);
			} else {
				Log.info(String.format("unused Alarm CODE[%s]", SharedCritical.CPU_ALARM_CODE));
			}

			int audio_flag = Integer.parseInt(map.get("audio_flag"));
			int visual_flag = Integer.parseInt(map.get("visual_flag"));

			ConcurrentHashMap<String, Integer> data = new ConcurrentHashMap<>();
			data.put("nowLv", NowOverloadLevel);
			data.put("oldLv", SharedCritical.OldCpuOverloadLevel[cnt]);
			data.put("alarmIdx", alarm_idx);
			data.put("value", Cpuvalue);
			data.put("visual_flag", visual_flag);
			data.put("audio_flag", audio_flag);

			if (NowOverloadLevel != SharedCritical.OldCpuOverloadLevel[cnt]) {
				if (SharedCritical.OldCpuOverloadCount < SharedInitData.overload_cnt) {
					Log.info(String.format("The CpuOldOverLoadCnt[%s] is less then OverLoadcnt[%s]",
							SharedCritical.OldCpuOverloadCount, SharedInitData.overload_cnt));
					SharedCritical.OldCpuOverloadCount++;
				} else {
					AlarmHandling(data, SharedCritical.CPU_ALARM_CODE, map.get("alarm_desc"), handlingDao);
					SharedCritical.OldCpuOverloadLevel[cnt] = NowOverloadLevel;
					SharedCritical.OldCpuOverloadCount = 0;
				}
			}

			if (!handlingDao.putResource(ResourceName, "TOTAL", String.valueOf(Cpuvalue),
					String.valueOf(SharedCritical.OldCpuOverloadLevel[cnt]))) {
				Log.error(String.format("(%s) putResource Fail Check Db", this.getClass().getName()));
			}

			if (!handlingDao.putResourceHistory(ResourceName, "TOTAL")) {
				Log.error(String.format("(%s) putResourceHistory Fail Check Db", this.getClass().getName()));
			}

			cnt++;
		}

		return true;
	}

	private boolean calcurateResource() {
		for (int resource = 0; resource < SharedCritical.MAX_RESOURCE_CNT; resource++) {
			switch (resource) {
			case SharedCritical.RESOURCE_CPU:
				calcurateCpu("CPU", resource);
				break;

			case SharedCritical.RESOURCE_DISK:
				calcurateDisk("DISK", resource);
				break;

			case SharedCritical.RESOURCE_MEMORY:
				calcurateMemory("MEMORY", resource);
				break;

			default:
				break;
			}
		}
		return true;
	}

	/**
	 * 작성 일자 : 2016. 12. 6. 작성자 : 김연호 주석 : 알람 레벨에 대한 정보를 디비에 읽어 메모리에 저장한다. 메소드 :
	 * Init
	 */
	public void Init() {
		ResourceHandlingDaoMapper handlingDao = new ResourceHandlingDaoMapper();

		if (handlingDao.getDbState()) {
			for (int resource = 0; resource < SharedCritical.MAX_RESOURCE_CNT; resource++) {
				switch (resource) {
				case SharedCritical.RESOURCE_CPU:
					SharedCritical.OldCpuOverloadLevel = handlingDao.getbeforeAlarmState("CPU");
					break;

				case SharedCritical.RESOURCE_DISK:
					SharedCritical.OldDiskOverloadLevel = handlingDao.getbeforeAlarmState("DISK");
					break;

				case SharedCritical.RESOURCE_MEMORY:
					SharedCritical.OldMemoryOverloadLevel = handlingDao.getbeforeAlarmState("MEMORY");
					break;

				default:
					break;
				}
			}

		} else {
			Log.error("Init Fail Check the Db state");
		}
	}

	@SuppressWarnings("static-access")
	public void run() {
		try {
			Log.info(" -> Resource MonitorThread Start");
			ResourceHandlingDaoMapper handlingDao = new ResourceHandlingDaoMapper();

			while (!Thread.currentThread().interrupted()) {
				try {
					if (handlingDao.getDbState()) {
						if (getCPU() && getDisk() && getMemory()) {
							calcurateResource();
						}
					} else {
						Log.info("check the Db state");
					}

					PrintValue.printCriticalValue();
					PrintValue.printResourceValue();
				} catch (Exception e) {
					// TODO: handle exception
					Log.fatal("Thread Error");
				}
				Thread.sleep(SECOND * SharedInitData.monitor_period);
			}
		} catch (InterruptedException e) {
			Log.fatal("ResourceMonitor Thread Interrupt Exception", e);
		} finally {
			Log.fatal("ResourceMonitor Thread Exit");
		}
	}
}