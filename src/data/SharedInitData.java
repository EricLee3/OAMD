package data;

/**
 * ��¥ : 2016. 12. 2.
 * ������Ʈ : OAMD
 * ������ : �迬ȣ
 */
public class SharedInitData {
	/* PATH */
	public final static String CONF_PATH = "C:\\home\\mecs\\conf\\mecs.conf";
	public final static String PROCESS_PATH = "C:\\home\\mecs\\OAMD\\bin\\OAMD.lock";

	/* SYETEM VARIABLES */
	public static int overload_cnt;
	public static int monitor_period;
	public static String sys_id;
	public static String sys_name;

	public static String DbcpDrive = "jdbc:apache:commons:dbcp:cp";
}