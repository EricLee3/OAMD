package dao;

import java.io.IOException;

import org.apache.log4j.Logger;

import data.SharedInitData;
import util.Readfile;

/**
 * 날짜 : 2016. 12. 2.
 * 프로젝트 : OAMD
 * 생성자 : 김연호
 */
public abstract class ConnectionManager {

	Logger Log = Logger.getLogger("OAMD");

	protected DBConnectionPoolManager connMgr;
	protected String poolName, dbServer, dbName, port, userID, passwd;
	protected int maxConn, initConn, maxWait;

	public ConnectionManager(String pool) {
		// TODO Auto-generated constructor stub
		poolName = pool;

		try {

			Readfile confFile = new Readfile();

			dbServer = confFile.getConfFile(SharedInitData.CONF_PATH, "DB_HOST");
			port = "3306";
			dbName = confFile.getConfFile(SharedInitData.CONF_PATH, "DB_DATABASE");
			userID = confFile.getConfFile(SharedInitData.CONF_PATH, "DB_USER");
			passwd = confFile.getConfFile(SharedInitData.CONF_PATH, "DB_PASSWD");
			maxConn = Integer.parseInt(confFile.getConfFile(SharedInitData.CONF_PATH, "DB_MAXCONN"));
			initConn = Integer.parseInt(confFile.getConfFile(SharedInitData.CONF_PATH, "DB_INITCONN"));
			maxWait = Integer.parseInt(confFile.getConfFile(SharedInitData.CONF_PATH, "DB_MAXWAIT"));

		} catch (IOException e) {
			Log.fatal(String.format("(%s)ConfFile Exception Error Check the Dconfigfile", this.getClass().getName()), e);
			System.exit(0);
		}

		Log.info(String.format("DB_SERVER      : [%s]", this.dbServer));
		Log.info(String.format("PORT           : [%s]", this.port));
		Log.info(String.format("DB_DATABASE    : [%s]", this.dbName));
		Log.info(String.format("DB_USER        : [%s]", this.userID));
		Log.info(String.format("DB_PASSWD      : [%s]", this.passwd));
		Log.info(String.format("DB_MAXCONN     : [%s]", this.maxConn));
		Log.info(String.format("DB_INITCONN    : [%s]", this.initConn));
		Log.info(String.format("DB_MAXWAIT     : [%s]", this.maxWait));
	}

}
