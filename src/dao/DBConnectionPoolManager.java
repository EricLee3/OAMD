package dao;

import java.sql.DriverManager;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDriver;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.log4j.Logger;

/**
 * 날짜 : 2016. 12. 2.
 * 프로젝트 : OAMD
 * 생성자 : 김연호
 */
public class DBConnectionPoolManager {
	Logger Log = Logger.getLogger("OAMD");

	static private DBConnectionPoolManager instance;

	static synchronized public DBConnectionPoolManager getInstance() {
		if (instance == null) {
			instance = new DBConnectionPoolManager();
		}

		return instance;
	}
	
	private void createPools(String poolName, String url, String user, String password, int maxConn, int initConn,
			int maxWait) {
		try {
			// 커넥션 팩토리 생성. 커넥션 팩토리는 새로운 커넥션을 생성할때 사용.
			ConnectionFactory connFactory = new DriverManagerConnectionFactory(url, user, password);
			PoolableConnectionFactory poolableConnFactory = new PoolableConnectionFactory(connFactory, null);

			poolableConnFactory.setValidationQuery("select 1");

			GenericObjectPoolConfig poolconfig = new GenericObjectPoolConfig();

			poolconfig.setTimeBetweenEvictionRunsMillis(1000L * 60L * maxWait);
			poolconfig.setTestWhileIdle(true);
			poolconfig.setTestOnBorrow(true);
			poolconfig.setMinIdle(initConn);
			poolconfig.setMaxIdle(maxConn);

			GenericObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(poolableConnFactory,
					poolconfig);

			poolableConnFactory.setPool(connectionPool);

			Class.forName("org.apache.commons.dbcp2.PoolingDriver");

			PoolingDriver driver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");

			driver.registerPool("cp", connectionPool);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public void init(String poolName, String driver, String url, String user, String passwd, int maxConn, int initConn,
			int maxWait) {

		loadDrivers(driver);
		createPools(poolName, url, user, passwd, maxConn, initConn, maxWait);
	}


	private void loadDrivers(String driverClassName) {
		try {
			Class.forName(driverClassName);
		} catch (Exception e) {
			throw new RuntimeException("fail to load JDBC Driver", e);
		}
	}
}
