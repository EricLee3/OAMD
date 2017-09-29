package dao;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ��¥ : 2016. 12. 2.
 * ������Ʈ : OAMD
 * ������ : �迬ȣ
 */
public interface ResourceHandlingDao {
	

	/**
	 * �ۼ� ���� : 2016. 12. 9.
	 * �ۼ��� : �迬ȣ
	 * ��� : 
	 * �޼ҵ� : removeAlarmstate
	 */
	public boolean removeAlarmstate(String alarm_code);
	
	/**
	 * �ۼ� ���� : 2016. 12. 22.
	 * �ۼ��� : �迬ȣ
	 * ��� : 
	 * �޼ҵ� : putfaultstate
	 */
	public boolean putfaultstate(String alarm_code, String alarm_detail);
	
	/**
	 * �ۼ� ���� : 2016. 12. 9.
	 * �ۼ��� : �迬ȣ
	 * ��� : 
	 * �޼ҵ� : putAlarmstate_insert
	 */
	public boolean putAlarmstate(String alarm_lv, String alarm_flag, String alarm_detail, String alarm_code, String audio_flag);
	
	/**
	 * �ۼ� ���� : 2016. 12. 9.
	 * �ۼ��� : �迬ȣ
	 * ��� : 
	 * �޼ҵ� : getAlarmHappen
	 */
	public int getFalutHappen(String alarm_code);
	/**
	 * �ۼ� ���� : 2016. 12. 9.
	 * �ۼ��� : �迬ȣ
	 * ��� : 
	 * �޼ҵ� : getAlarmHappen
	 */
	public int getAlarmHappen(String alarm_code);
	
	/***
	 * 
	 * �ۼ� ���� : 2016. 12. 9.
	 * �ۼ��� : �迬ȣ
	 * ��� : 
	 * �޼ҵ� : getAlarmExist
	 */
	public ConcurrentHashMap<String, String> getAlarmExist(String alarm_code);
	
	/***
	 * ���� �˶����� ����
	 * @param alarm_lv
	 * @param alarm_flag
	 * @param alarm_detail
	 * @param alarm_code
	 * @return
	 */
	public boolean putAlarmstate_update(String alarm_lv, String alarm_flag, String alarm_detail, String alarm_code);

	/**
	 * �ۼ� ���� : 2016. 12. 22.
	 * �ۼ��� : �迬ȣ
	 * ��� : 
	 * �޼ҵ� : putAlarmstatehistory_Exist
	 */
	public int getAlarmstatehistory_Exist(String alarm_code);
	
	/**
	 * �ۼ� ���� : 2016. 12. 19.
	 * �ۼ��� : �迬ȣ
	 * ��� : 
	 * �޼ҵ� : putAlarmstatehistory_OFF
	 */
	public boolean putAlarmstatehistory_OFF(ConcurrentHashMap<String, String> data);
	/**
	 * �˶� �̷� ���� (���� ����)
	 * @param alarm_lv
	 * @param alarm_code
	 * @param alarm_detail
	 * @return
	 */
	public boolean putAlarmstatehistory_OFF(String alarm_flag, String alarm_code);
	/**
	 * �˶� �̷� ���� (���� ����)
	 * @param alarm_lv
	 * @param alarm_code
	 * @param alarm_detail
	 * @return
	 */
	public boolean putAlarmstatehistory_OFF(String alarm_lv, String alarm_flag, String alarm_code);
	/**
	 * �ۼ� ���� : 2016. 12. 19.
	 * �ۼ��� : �迬ȣ
	 * ��� : 
	 * �޼ҵ� : putAlarmstatehistory_ON
	 */
	public boolean putAlarmstatehistory_ON(ConcurrentHashMap<String, String> data);
	/**
	 * �˶� �̷� ����
	 * @param alarm_lv
	 * @param alarm_code
	 * @return
	 */
	public boolean putAlarmstatehistory_ON(String alarm_lv, String alarm_code);
	
	/**
	 * �ۼ� ���� : 2016. 12. 19.
	 * �ۼ��� : �迬ȣ
	 * ��� : 
	 * �޼ҵ� : putAlarmstatehistory_ON
	 */
	public boolean putFaultstatehistory_ON(ConcurrentHashMap<String, String> data);
	/**
	 * �˶� �̷� ����
	 * @param alarm_lv
	 * @param alarm_code
	 * @return
	 */
	public boolean putFaultstatehistory_ON(String alarm_code);


	/**
	 * ���� ���ҽ� ���� ����
	 * @param rsname
	 * @param category
	 * @param value
	 * @param alarm_lv
	 * @return
	 */
	public boolean putResource(String rsname, String category, String value, String alarm_lv);
	
	/**
	 * ���ҽ� ���� �̷� ����
	 * @param rsname
	 * @param category 
	 * @return
	 */
	public boolean putResourceHistory(String rsname, String category);
	
	/**
	 * �Ӱ�ġ ������ �����´�.
	 * @param alarm_idx 0.CPU 1.NETWORK 2.DISK 3.MEMORY
	 * @return
	 */
	public ArrayList<String> getMecs5AlarmLimit(String alarm_idx);
	
	/**
	 * �� �˶� ���¸� �����´�
	 * @return
	 */
	public int[] getbeforeAlarmState(String hostname);
	
	/**
	 * DB ���¸� �����´�
	 * @return
	 */	
	public boolean getDbState();
}
