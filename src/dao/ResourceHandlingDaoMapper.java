package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import data.SharedCritical;
import data.SharedInitData;

/**
 * 날짜 : 2016. 12. 2.
 * 프로젝트 : OAMD
 * 생성자 : 김연호
 */
public class ResourceHandlingDaoMapper implements ResourceHandlingDao{
	Logger Log = Logger.getLogger("OAMD");
	
	@Override
	public boolean putAlarmstate_update(String alarm_lv, String alarm_flag, String alarm_detail, String alarm_code) {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement stmt = null;
		
		String sql = String.format(
				"UPDATE mecs5_alarm_state"
				+ " SET alarmed_time = now(),"
				+ " alarm_lv = '%s',"
				+ " alarm_flag = '%s',"
				+ " alarm_detail = '%s'"
				+ " WHERE sys_id = '%s'"
				+ " AND sys_name = '%s'"
				+ " AND alarm_code = '%s'",
				alarm_lv,
				alarm_flag,
				alarm_detail,
				SharedInitData.sys_id,
				SharedInitData.sys_name,
				alarm_code
				);

		try {
			conn = DriverManager.getConnection(SharedInitData.DbcpDrive);
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);

		} catch (Exception e) {
			Log.error("putAlarmstate_update() Exception", e);
			return false;
		} finally {
			try {
				if (stmt != null) stmt.close();
				conn.close();
			} catch (SQLException e) {
				Log.error("putAlarmstate_update() Sql Exception", e);
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean putAlarmstatehistory_OFF(String alarm_lv, String alarm_flag, String alarm_code) {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement stmt = null;
		
		String sql = String.format(
				"UPDATE"
				+ " mecs5_alarm_history h, mecs5_alarm_state s"
				+ " SET" 
				+ " h.release_time = now(),"
				+ " h.alarm_flag = '%s',"
				+ " h.alarm_lv = s.alarm_lv,"
				+ " h.alarm_detail = s.alarm_detail"
				+ " WHERE s.sys_id = '%s'"
				+ " AND s.sys_name = '%s'"
				+ " AND s.alarm_lv = '%s'"
				+ " AND s.alarm_code = '%s'"
				+ " AND h.alarmed_time = s.alarmed_time",
				alarm_flag,
				SharedInitData.sys_id,
				SharedInitData.sys_name,
				alarm_lv,
				alarm_code);
		
		try {
			conn = DriverManager.getConnection(SharedInitData.DbcpDrive);
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);

		} catch (Exception e) {
			Log.error("putAlarmstatehistory_OFF() Exception", e);
			return false;
		} finally {
			try {
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				Log.error("putAlarmstatehistory_OFF() Sql Exception", e);
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean putAlarmstatehistory_ON(String alarm_lv, String alarm_code) {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement stmt = null;
		
		String sql = String.format(
				"INSERT INTO mecs5_alarm_history"
				+ " (sys_id, sys_name, alarm_code, alarmed_time,"
				+ " alarm_flag, alarm_lv, alarm_detail)"
				+ " SELECT"
				+ " sys_id, sys_name, alarm_code, alarmed_time,"
				+ " alarm_flag, alarm_lv, alarm_detail"
				+ " FROM mecs5_alarm_state"
				+ " WHERE sys_id = '%s'"
				+ " AND sys_name = '%s'"
				+ " AND alarm_lv = '%s'"
				+ " AND alarm_code = '%s'",
				SharedInitData.sys_id,
				SharedInitData.sys_name,
				alarm_lv,
				alarm_code);

		try {
			conn = DriverManager.getConnection(SharedInitData.DbcpDrive);
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);

		} catch (Exception e) {
			Log.error("putAlarmstatehistory_ON() Exception", e);
			return false;
		} finally {
			try {
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				Log.error("putAlarmstatehistory_ON() Sql Exception", e);
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean putResource(String rsname, String category, String value, String alarm_lv) {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement stmt = null;
		String sql = String.format(
				"UPDATE mecs5_monitor"
				+ " SET update_time = now(), "
				+ " value = '%s', "
				+ " alarm_lv = '%s' "
				+ " WHERE sys_id = '%s'"
				+ " AND sys_name = '%s'"
				+ " AND host_name = '%s'"
				+ " AND category = '%s'",
				value,
				alarm_lv,
				SharedInitData.sys_id,
				SharedInitData.sys_name,
				rsname,
				category);

		try {
			conn = DriverManager.getConnection(SharedInitData.DbcpDrive);
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);

		} catch (Exception e) {
			Log.error("putResource() Exception", e);
			return false;
		} finally {
			try {
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				Log.error("putResource() Sql Exception", e);
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean putResourceHistory(String rsname, String category) {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement stmt = null;
		String sql = String.format(
				"INSERT INTO mecs5_monitor_history"
				+ " SELECT * FROM mecs5_monitor"
				+ " WHERE sys_id = '%s'"
				+ " AND sys_name = '%s'"
				+ " AND host_name = '%s'"
				+ " AND category = '%s'",
				SharedInitData.sys_id,
				SharedInitData.sys_name,
				rsname,
				category);
		
		try {
			conn = DriverManager.getConnection(SharedInitData.DbcpDrive);
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);

		} catch (Exception e) {
			Log.error("putResourceHistory() Exception", e);
			return false;
		} finally {
			try {
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				Log.error("putResourceHistory() SQL Exception", e);
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public ArrayList<String> getMecs5AlarmLimit (String alarm_idx) {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		ArrayList<String> sethost = new ArrayList<String>();

		String sql =
				String.format(
				"SELECT"
				+ " critical_value, major_value, minor_value"
				+ " FROM mecs5_alarm_limit"
				+ " WHERE alarm_idx = '%s'",
				alarm_idx);

		try {
			conn = DriverManager.getConnection(SharedInitData.DbcpDrive);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			
			while (rs.next()) {
				sethost.add(rs.getString("critical_value"));
				sethost.add(rs.getString("major_value"));
				sethost.add(rs.getString("minor_value"));
			}
			
		} catch (Exception e) {
			Log.error("getMecs5AlarmLimit() Exception", e);
		} finally {
			try {
				rs.close();
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				Log.error("getMecs5AlarmLimit() Sql Exception", e);
			}
		}
		return sethost;
	}

	@Override
	public int[] getbeforeAlarmState(String hostname) {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		int[] alarm_lv = new int [SharedCritical.MAX_RESOURCE_DATA_CNT];
		int cnt = 0;

		String sql =
				String.format(
				"SELECT alarm_lv"
				+ " FROM mecs5_monitor"
				+ " WHERE host_name = '%s'"
				+ " ORDER BY host_name",
				hostname);

		try {
			conn = DriverManager.getConnection(SharedInitData.DbcpDrive);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			
			while (rs.next()) {
				alarm_lv[cnt++] =rs.getInt("alarm_lv");
			}
			

		} catch (Exception e) {
			Log.error("getbeforeAlarmState() Exception", e);
		} finally {
			try {
				rs.close();
				stmt.close();
	
				conn.close();
			} catch (SQLException e) {
				Log.error("getbeforeAlarmState() Sql Exception", e);
			}
		}
		return alarm_lv;
	}
	
	@Override
	public boolean getDbState() {
		// TODO Auto-generated method stub
		boolean result = false;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		String Query = 
				"SELECT "
				+ "1 AS state "
				+ "FROM dual";
		try {
			conn = DriverManager.getConnection(SharedInitData.DbcpDrive);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(Query);
			rs.next();

			result = rs.getBoolean("state");

		} catch (Exception e) {
			Log.error("getDbState() Exception", e);
			return false;
		} finally {
			try {
				rs.close();
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				Log.error("getDbState() Sql Exception", e);
				return false;
			}
		}
		return result;
	}

	@Override
	public ConcurrentHashMap<String, String> getAlarmExist(String alarm_code) {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		ConcurrentHashMap<String, String> hashMap = new ConcurrentHashMap<>();

		String sql =
				String.format(
				"SELECT"
				+ " alarm_lv, alarm_desc, use_flag, visual_flag, audio_flag"
				+ " FROM mecs5_alarm_config"
				+ " WHERE alarm_code = '%s'",
				alarm_code);

		try {
			conn = DriverManager.getConnection(SharedInitData.DbcpDrive);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			
			while (rs.next()) {
				hashMap.put("alarm_lv", rs.getString("alarm_lv"));
				hashMap.put("alarm_desc", rs.getString("alarm_desc"));
				hashMap.put("use_flag", rs.getString("use_flag"));
				hashMap.put("visual_flag", rs.getString("visual_flag"));
				hashMap.put("audio_flag", rs.getString("audio_flag"));
			}
			
		} catch (Exception e) {
			Log.error("getAlarmExist() Exception", e);
		} finally {
			try {
				rs.close();
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				Log.error("getAlarmExist() Sql Exception", e);
			}
		}
		return hashMap;
	}

	@Override
	public int getAlarmHappen(String alarm_code) {
		// TODO Auto-generated method stub
		@SuppressWarnings("unused")
		int result = 0;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		String Query = 
				String.format(
				"SELECT"
				+ " alarm_flag"
				+ " FROM mecs5_alarm_state"
				+ " WHERE alarm_flag = '1'"
				+ " AND alarm_code = '%s' "
				, alarm_code);
		
		try {
			conn = DriverManager.getConnection(SharedInitData.DbcpDrive);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(Query);
			
			while(rs.next()) {
				return 0;
			}
			
			return 1;
		} catch (Exception e) {
			Log.error("getAlarmHappen() Exception", e);
			return -1;
		} finally {
			try {
				rs.close();
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				Log.error("getAlarmHappen() Sql Exception", e);
				return -1;
			}
		}
	}

	@Override
	public boolean putAlarmstate(String alarm_lv, String alarm_flag, String alarm_detail, String alarm_code, String audio_flag) {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement stmt = null;
		
		String sql = String.format(
				"INSERT INTO mecs5_alarm_state"
				+ " (sys_id, sys_name, alarm_code,"
				+ " alarmed_time, alarm_flag, alarm_lv,"
				+ " alarm_detail, audio_flag)"
				+ " VALUES"
				+ " ('%s', '%s', '%s',"
				+ " now(), '%s', '%s',"
				+ " '%s', '%s')",
				SharedInitData.sys_id,
				SharedInitData.sys_name,
				alarm_code,
				alarm_flag,
				alarm_lv,
				alarm_detail,
				audio_flag
				);
		
		
		try {
			conn = DriverManager.getConnection(SharedInitData.DbcpDrive);
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);

		} catch (Exception e) {
			Log.error("putAlarmstate() Exception", e);
			return false;
		} finally {
			try {
				if (stmt != null) stmt.close();
				conn.close();
			} catch (SQLException e) {
				Log.error("putAlarmstate() Sql Exception", e);
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean removeAlarmstate(String alarm_code) {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement stmt = null;
		
		String sql = String.format(
				"DELETE FROM mecs5_alarm_state"
				+ " WHERE sys_id = '%s'"
				+ " AND sys_name = '%s'"
				+ " AND alarm_code = '%s'",
				SharedInitData.sys_id,
				SharedInitData.sys_name,
				alarm_code
				);

		try {
			conn = DriverManager.getConnection(SharedInitData.DbcpDrive);
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);

		} catch (Exception e) {
			Log.error("removeAlarmstate() Exception", e);
			return false;
		} finally {
			try {
				if (stmt != null) stmt.close();
				conn.close();
			} catch (SQLException e) {
				Log.error("removeAlarmstate() Sql Exception", e);
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean putAlarmstatehistory_ON(ConcurrentHashMap<String, String> data) {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement stmt = null;
		
		
		String sql = String.format(
				"INSERT INTO mecs5_alarm_history"
				+ " (sys_id, sys_name, alarm_code, alarmed_time,"
				+ " alarm_flag, alarm_lv, alarm_detail)"
				+ " VALUES"
				+ " ('%s', '%s', '%s', now(),"
				+ " '%s', '%s', '%s')",
				SharedInitData.sys_id,
				SharedInitData.sys_name,
				data.get("alarm_code"),
				SharedCritical.ALARM_ON,
				data.get("alarm_lv"),
				data.get("alarm_desc")
				);
		
		
		try {
			conn = DriverManager.getConnection(SharedInitData.DbcpDrive);
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);

		} catch (Exception e) {
			Log.error("putAlarmstatehistory_ON() Exception", e);
			return false;
		} finally {
			try {
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				Log.error("putAlarmstatehistory_ON() Sql Exception", e);
				return false;
			}
		}
		return false;
	}

	@Override
	public boolean putAlarmstatehistory_OFF(ConcurrentHashMap<String, String> data) {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement stmt = null;
		
		String sql = String.format(
				"UPDATE"
				+ " mecs5_alarm_history"
				+ " SET" 
				+ " release_time = now(),"
				+ " alarm_flag = '%s'"
				+ " WHERE sys_id = '%s'"
				+ " AND sys_name = '%s'"
				+ " AND alarm_lv = '%s'"
				+ " AND alarm_code = '%s'",
				SharedCritical.ALARM_OFF,
				SharedInitData.sys_id,
				SharedInitData.sys_name,
				data.get("alarm_lv"),
				data.get("alarm_code"));
		
		try {
			conn = DriverManager.getConnection(SharedInitData.DbcpDrive);
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);

		} catch (Exception e) {
			Log.error("putAlarmstatehistory_OFF() Exception", e);
			return false;
		} finally {
			try {
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				Log.error("putAlarmstatehistory_OFF() Sql Exception", e);
				return false;
			}
		}
		return false;
	}

	@Override
	public int getFalutHappen(String alarm_code) {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		String Query = 
				String.format(
				"SELECT"
				+ " code"
				+ " FROM mecs5_fault_state"
				+ " WHERE code = '%s' "
				, alarm_code);
		
		try {
			conn = DriverManager.getConnection(SharedInitData.DbcpDrive);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(Query);
			
			while(rs.next()) {
				return 0;
			}
			
			return 1;
		} catch (Exception e) {
			Log.error("getFalutHappen() Exception", e);
			return -1;
		} finally {
			try {
				rs.close();
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				Log.error("getFalutHappen() Sql Exception", e);
				return -1;
			}
		}
	}

	@Override
	public boolean putfaultstate(String alarm_code, String alarm_detail) {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement stmt = null;
		
		String sql = String.format(
				"INSERT INTO mecs5_fault_state"
				+ " (sys_id, sys_name, code,"
				+ " happened_time, detail)"
				+ " VALUES"
				+ " ('%s', '%s', '%s',"
				+ " now(), '%s')",
				SharedInitData.sys_id,
				SharedInitData.sys_name,
				alarm_code,
				alarm_detail
				);
		
		try {
			conn = DriverManager.getConnection(SharedInitData.DbcpDrive);
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);

		} catch (Exception e) {
			Log.error("putfaultstate() Exception", e);
			return false;
		} finally {
			try {
				if (stmt != null) stmt.close();
				conn.close();
			} catch (SQLException e) {
				Log.error("putfaultstate() Sql Exception", e);
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean putAlarmstatehistory_OFF(String alarm_flag, String alarm_code) {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement stmt = null;
		
		String sql = String.format(
				"UPDATE"
				+ " mecs5_alarm_history h, mecs5_alarm_state s"
				+ " SET" 
				+ " h.release_time = now(),"
				+ " h.alarm_flag = '%s',"
				+ " h.alarm_lv = s.alarm_lv,"
				+ " h.alarm_detail = s.alarm_detail"
				+ " WHERE s.sys_id = '%s'"
				+ " AND s.sys_name = '%s'"
				+ " AND s.alarm_code = '%s'"
				+ " AND h.alarmed_time = s.alarmed_time",
				alarm_flag,
				SharedInitData.sys_id,
				SharedInitData.sys_name,
				alarm_code);
		
		try {
			conn = DriverManager.getConnection(SharedInitData.DbcpDrive);
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);

		} catch (Exception e) {
			Log.error("putAlarmstatehistory_OFF() Exception", e);
			return false;
		} finally {
			try {
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				Log.error("putAlarmstatehistory_OFF() Sql Exception", e);
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean putFaultstatehistory_ON(ConcurrentHashMap<String, String> data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean putFaultstatehistory_ON(String alarm_code) {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement stmt = null;
		
		String sql = String.format(
				"INSERT INTO mecs5_fault_history"
				+ " (sys_id, sys_name, code, happened_time, detail)"
				+ " SELECT"
				+ " sys_id, sys_name, code, happened_time, detail"
				+ " FROM mecs5_fault_state"
				+ " WHERE sys_id = '%s'"
				+ " AND sys_name = '%s'"
				+ " AND code = '%s'",
				SharedInitData.sys_id,
				SharedInitData.sys_name,
				alarm_code);

		try {
			conn = DriverManager.getConnection(SharedInitData.DbcpDrive);
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);

		} catch (Exception e) {
			Log.error("putAlarmstatehistory_ON() Exception", e);
			return false;
		} finally {
			try {
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				Log.error("putAlarmstatehistory_ON() Sql Exception", e);
				return false;
			}
		}
		return true;
	}

	@Override
	public int getAlarmstatehistory_Exist(String alarm_code) {
		// TODO Auto-generated method stub
		@SuppressWarnings("unused")
		int result = 0;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		String Query = 
				String.format(
				"SELECT"
				+ " alarm_code"
				+ " FROM mecs5_alarm_history"
				+ " WHERE alarm_code = '%s' "
				+ " AND alarm_flag = '%s'"
				, alarm_code
				, SharedCritical.ALARM_ON);
		
		try {
			conn = DriverManager.getConnection(SharedInitData.DbcpDrive);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(Query);
			
			while(rs.next()) {
				return 0;
			}
			return 1;
		} catch (Exception e) {
			Log.error("getAlarmstatehistory_Exist() Exception", e);
			return -1;
		} finally {
			try {
				rs.close();
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				Log.error("getAlarmstatehistory_Exist() Sql Exception", e);
				return -1;
			}
		}
	}
}
