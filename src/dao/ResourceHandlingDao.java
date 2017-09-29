package dao;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 날짜 : 2016. 12. 2.
 * 프로젝트 : OAMD
 * 생성자 : 김연호
 */
public interface ResourceHandlingDao {
	

	/**
	 * 작성 일자 : 2016. 12. 9.
	 * 작성자 : 김연호
	 * 비고 : 
	 * 메소드 : removeAlarmstate
	 */
	public boolean removeAlarmstate(String alarm_code);
	
	/**
	 * 작성 일자 : 2016. 12. 22.
	 * 작성자 : 김연호
	 * 비고 : 
	 * 메소드 : putfaultstate
	 */
	public boolean putfaultstate(String alarm_code, String alarm_detail);
	
	/**
	 * 작성 일자 : 2016. 12. 9.
	 * 작성자 : 김연호
	 * 비고 : 
	 * 메소드 : putAlarmstate_insert
	 */
	public boolean putAlarmstate(String alarm_lv, String alarm_flag, String alarm_detail, String alarm_code, String audio_flag);
	
	/**
	 * 작성 일자 : 2016. 12. 9.
	 * 작성자 : 김연호
	 * 비고 : 
	 * 메소드 : getAlarmHappen
	 */
	public int getFalutHappen(String alarm_code);
	/**
	 * 작성 일자 : 2016. 12. 9.
	 * 작성자 : 김연호
	 * 비고 : 
	 * 메소드 : getAlarmHappen
	 */
	public int getAlarmHappen(String alarm_code);
	
	/***
	 * 
	 * 작성 일자 : 2016. 12. 9.
	 * 작성자 : 김연호
	 * 비고 : 
	 * 메소드 : getAlarmExist
	 */
	public ConcurrentHashMap<String, String> getAlarmExist(String alarm_code);
	
	/***
	 * 현재 알람상태 갱신
	 * @param alarm_lv
	 * @param alarm_flag
	 * @param alarm_detail
	 * @param alarm_code
	 * @return
	 */
	public boolean putAlarmstate_update(String alarm_lv, String alarm_flag, String alarm_detail, String alarm_code);

	/**
	 * 작성 일자 : 2016. 12. 22.
	 * 작성자 : 김연호
	 * 비고 : 
	 * 메소드 : putAlarmstatehistory_Exist
	 */
	public int getAlarmstatehistory_Exist(String alarm_code);
	
	/**
	 * 작성 일자 : 2016. 12. 19.
	 * 작성자 : 김연호
	 * 비고 : 
	 * 메소드 : putAlarmstatehistory_OFF
	 */
	public boolean putAlarmstatehistory_OFF(ConcurrentHashMap<String, String> data);
	/**
	 * 알람 이력 갱신 (설명문 따로)
	 * @param alarm_lv
	 * @param alarm_code
	 * @param alarm_detail
	 * @return
	 */
	public boolean putAlarmstatehistory_OFF(String alarm_flag, String alarm_code);
	/**
	 * 알람 이력 갱신 (설명문 따로)
	 * @param alarm_lv
	 * @param alarm_code
	 * @param alarm_detail
	 * @return
	 */
	public boolean putAlarmstatehistory_OFF(String alarm_lv, String alarm_flag, String alarm_code);
	/**
	 * 작성 일자 : 2016. 12. 19.
	 * 작성자 : 김연호
	 * 비고 : 
	 * 메소드 : putAlarmstatehistory_ON
	 */
	public boolean putAlarmstatehistory_ON(ConcurrentHashMap<String, String> data);
	/**
	 * 알람 이력 갱신
	 * @param alarm_lv
	 * @param alarm_code
	 * @return
	 */
	public boolean putAlarmstatehistory_ON(String alarm_lv, String alarm_code);
	
	/**
	 * 작성 일자 : 2016. 12. 19.
	 * 작성자 : 김연호
	 * 비고 : 
	 * 메소드 : putAlarmstatehistory_ON
	 */
	public boolean putFaultstatehistory_ON(ConcurrentHashMap<String, String> data);
	/**
	 * 알람 이력 갱신
	 * @param alarm_lv
	 * @param alarm_code
	 * @return
	 */
	public boolean putFaultstatehistory_ON(String alarm_code);


	/**
	 * 현재 리소스 상태 갱신
	 * @param rsname
	 * @param category
	 * @param value
	 * @param alarm_lv
	 * @return
	 */
	public boolean putResource(String rsname, String category, String value, String alarm_lv);
	
	/**
	 * 리소스 상태 이력 갱신
	 * @param rsname
	 * @param category 
	 * @return
	 */
	public boolean putResourceHistory(String rsname, String category);
	
	/**
	 * 임계치 정보를 가져온다.
	 * @param alarm_idx 0.CPU 1.NETWORK 2.DISK 3.MEMORY
	 * @return
	 */
	public ArrayList<String> getMecs5AlarmLimit(String alarm_idx);
	
	/**
	 * 전 알람 상태를 가져온다
	 * @return
	 */
	public int[] getbeforeAlarmState(String hostname);
	
	/**
	 * DB 상태를 가져온다
	 * @return
	 */	
	public boolean getDbState();
}
