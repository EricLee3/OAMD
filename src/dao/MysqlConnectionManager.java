package dao;

/**
 * 날짜 : 2016. 12. 2.
 * 프로젝트 : OAMD
 * 생성자 : 김연호
 */
public class MysqlConnectionManager extends ConnectionManager {
	public MysqlConnectionManager() {
		// TODO Auto-generated constructor stub
		super("mysql");
		String JDBCDriver = "org.mariadb.jdbc.Driver";
		// Mysql JDBC thin driver
		String JDBCDriverType = "jdbc:mariadb://";
		String url = JDBCDriverType + dbServer + ":" + port + "/" + dbName + "?autoReconnect=true&useSSL=false";

		connMgr = DBConnectionPoolManager.getInstance();
		connMgr.init(poolName, JDBCDriver, url, userID, passwd, maxConn, initConn, maxWait);
	}
}