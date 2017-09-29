package data;

import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * 날짜 : 2016. 12. 2. 프로젝트 : OAMD 생성자 : 김연호
 */
public class SharedCritical {
	Logger Log = Logger.getLogger("OAMD");

	/* RESOURCE */
	public final static int RESOURCE_CPU = 0;
	public final static int RESOURCE_DISK = 1;
	public final static int RESOURCE_MEMORY = 2;
	public final static int MAX_RESOURCE_CNT = 3;

	/* LEVEL */
	public final static int MINOR_LV = 1;
	public final static int MAJOR_LV = 2;
	public final static int CRITICAL_LV = 3;
	public final static int MAX_ALARM_LV = 3;

	/* ALARM FLAG */
	public final static String ALARM_ON = "1";
	public final static String ALARM_OFF = "0";

	/* ALARM CODE */
	public final static String DISK_C_ALARM_CODE = "A0000";
	public final static String DISK_D_ALARM_CODE = "A0001";
	public final static String DISK_E_ALARM_CODE = "A0002";
	public final static String DISK_F_ALARM_CODE = "A0003";
	public final static String DISK_ETC_ALARM_CODE = "A0004";
	public final static String MEMORY_ALARM_CODE = "A0005";
	public final static String CPU_ALARM_CODE = "A0006";
	public final static String INIT_ALARM_CODE = "A1700";

	/* OLD VALUE */
	public static int OldCpuOverloadCount;
	public final static int MAX_RESOURCE_DATA_CNT = 10;
	public static int[] OldCpuOverloadLevel = new int[MAX_RESOURCE_DATA_CNT];
	public static int[] OldDiskOverloadLevel = new int[MAX_RESOURCE_DATA_CNT];
	public static int[] OldMemoryOverloadLevel = new int[MAX_RESOURCE_DATA_CNT];

	/* Shared Data */
	public static ArrayList<ArrayList<String>> CRITICAL_VALUE;

	public static void Init() {
		CRITICAL_VALUE = new ArrayList<ArrayList<String>>();
	}
}
