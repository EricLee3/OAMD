package task;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import com.sun.jna.ptr.IntByReference;

import dao.ResourceHandlingDaoMapper;
import data.JNAKernel32;
import data.MailslotServer;
import data.SharedCritical;
import data.SharedInitData;
import util.Readfile;

public class MessageQueuePollingThread implements Runnable {
	Logger Log = Logger.getLogger("OAMD");

	private static String QueueName;

	public boolean FaultOn(ConcurrentHashMap<String, String> Faultdata) {
		ResourceHandlingDaoMapper resourceHandlingDaoMapper = new ResourceHandlingDaoMapper();

		String alarm_code = Faultdata.get("alarm_code");
		int ret = resourceHandlingDaoMapper.getFalutHappen(alarm_code);
		if (ret != 1) {
			Log.info(String.format("happened to Fault Code -> Fail[%s]", alarm_code));
			return false;
		}

		Log.info(String.format("happen to Fault Code -> Success[%s]", alarm_code));
		String alarm_desc = Faultdata.get("msg_desc").equals(" ") ? Faultdata.get("alarm_desc")
				: Faultdata.get("msg_desc");
		resourceHandlingDaoMapper.putfaultstate(alarm_code, alarm_desc);
		resourceHandlingDaoMapper.putFaultstatehistory_ON(alarm_code);

		return true;
	}

	public boolean AlarmON(ConcurrentHashMap<String, String> Alarmdata) {
		ResourceHandlingDaoMapper resourceHandlingDaoMapper = new ResourceHandlingDaoMapper();

		String alarm_code = Alarmdata.get("alarm_code");
		int visual_flag = Integer.parseInt(Alarmdata.get("visual_flag"));

		int ret = resourceHandlingDaoMapper.getAlarmHappen(Alarmdata.get("alarm_code"));
		if (ret != 1) {
			Log.info(String.format("Happen to Alarm Code[%s]", Alarmdata.get("alarm_code")));
			return false;
		}

		Log.info(String.format("Alarm Code[%s] ON | Visual Flag[%s]", alarm_code, Alarmdata.get("visual_flag")));
		switch (visual_flag) {
		case 1:
			String alarm_desc;
			alarm_desc = Alarmdata.get("msg_desc").equals(" ") ? Alarmdata.get("alarm_desc")
					: Alarmdata.get("msg_desc");

			resourceHandlingDaoMapper.putAlarmstate(Alarmdata.get("alarm_lv"), SharedCritical.ALARM_ON, alarm_desc,
					Alarmdata.get("alarm_code"), Alarmdata.get("audio_flag"));
			resourceHandlingDaoMapper.putAlarmstatehistory_ON(Alarmdata.get("alarm_lv"), Alarmdata.get("alarm_code"));
			break;

		default:
			int rethistory = resourceHandlingDaoMapper.getAlarmstatehistory_Exist(alarm_code);
			if (rethistory != 1) {
				Log.info(String.format("Happen to Alarm history Code[%s] ", Alarmdata.get("alarm_code")));
				return false;
			}
			if (!Alarmdata.get("msg_desc").equals(" ")) {
				Alarmdata.put("alarm_desc", Alarmdata.get("msg_desc"));
			}

			resourceHandlingDaoMapper.putAlarmstatehistory_ON(Alarmdata);
			break;
		}

		return true;
	}

	public boolean AlarmOFF(ConcurrentHashMap<String, String> Alarmdata) {
		ResourceHandlingDaoMapper resourceHandlingDaoMapper = new ResourceHandlingDaoMapper();
		int visual_flag = Integer.parseInt(Alarmdata.get("visual_flag"));

		Log.info(String.format("Alarm Code[%s] OFF | Visual Flag[%s]", Alarmdata.get("alarm_code"),
				Alarmdata.get("visual_flag")));
		switch (visual_flag) {
		case 1:
			int ret = resourceHandlingDaoMapper.getAlarmHappen(Alarmdata.get("alarm_code"));
			if (ret != 0) {
				Log.info(String.format("Not Happen to Alarm Code[%s]", Alarmdata.get("alarm_code")));
				return false;
			}
			resourceHandlingDaoMapper.putAlarmstatehistory_OFF(Alarmdata.get("alarm_lv"), SharedCritical.ALARM_OFF,
					Alarmdata.get("alarm_code"));
			resourceHandlingDaoMapper.removeAlarmstate(Alarmdata.get("alarm_code"));
			break;

		default:
			int rethistory = resourceHandlingDaoMapper.getAlarmstatehistory_Exist(Alarmdata.get("alarm_code"));
			if (rethistory != 0) {
				Log.info(String.format("Not Happen to Alarm history Code[%s] ", Alarmdata.get("alarm_code")));
				return false;
			}
			resourceHandlingDaoMapper.removeAlarmstate(Alarmdata.get("alarm_code"));
			resourceHandlingDaoMapper.putAlarmstatehistory_OFF(Alarmdata);
			break;
		}

		return true;
	}

	public void Init() {
		String temp = null;
		Readfile confFile = new Readfile();

		try {
			temp = confFile.getConfFile(SharedInitData.CONF_PATH, "ALARM_MQ_NAME");
			QueueName = temp.replace("*", ".");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.fatal(String.format("(%s)Conf File Exception Error Check the configfile [ALARM_MQ_NAME]",
					this.getClass().getName()), e);
			System.exit(0);
		}
		Log.info(String.format("ALARM_MQ_NAME : [%s]", QueueName));
	}

	@SuppressWarnings({ "static-access" })
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Log.info(" -> MessageQueuePolling Thread Start");
		JNAKernel32 k32lib = JNAKernel32.INSTANCE;
		MailslotServer mailslotServer = new MailslotServer();
		String Alarm_code = null;
		String Alarm_flag = null;
		String Alarm_descrition = null;

		int sock = mailslotServer.ServerInit(this.QueueName);
		if (sock < 0) {
			Log.error("MainslotServer Create fail");
			System.exit(0);
		}
		ResourceHandlingDaoMapper resourceHandlingDaoMapper = new ResourceHandlingDaoMapper();

		try {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					if (resourceHandlingDaoMapper.getDbState()) {
						if (!mailslotServer.hasMessage(sock)) {
							Thread.sleep(500);
							continue;
						}

						byte[] bytemsg = new byte[JNAKernel32.MSG_MAX_SIZE];
						IntByReference read = new IntByReference();
						mailslotServer.k32lib.ReadFile(sock, bytemsg, JNAKernel32.MSG_MAX_SIZE, read, 0);

						String message = mailslotServer.b2s(bytemsg);
						Log.info(String.format("Receive Alarm MessageQueue[%s]", message));

						String value[] = message.split("#");

						try {
							Alarm_code = value[0];
							Alarm_flag = value[1];
							Alarm_descrition = value[2];
						} catch (Exception e) {
							Log.error("Null Point Exception catch the Message");
							continue;
						}

						ConcurrentHashMap<String, String> data = new ConcurrentHashMap<>();

						data = resourceHandlingDaoMapper.getAlarmExist(Alarm_code);
						if (data.size() == 0) {
							Log.error("Alarm Config Table Get Alarmcode[Fail]");
							if (Alarm_flag.equals("0")) {
								int ret = resourceHandlingDaoMapper.getAlarmHappen(Alarm_code);
								if (ret != 1) {
									Log.info(String.format(
											"happened to Alarm Code[%s] : Alarm flag [ON] -> [OFF] (Alarmconfig TB AlarmCode Not Exist)",
											Alarm_code));
									resourceHandlingDaoMapper.putAlarmstatehistory_OFF(Alarm_flag, Alarm_code);
									resourceHandlingDaoMapper.removeAlarmstate(Alarm_code);
								}
							}
							continue;
						}

						data.put("alarm_code", Alarm_code);
						data.put("msg_desc", Alarm_descrition);

						int use_flag = Integer.parseInt(data.get("use_flag"));

						if (use_flag == 0) {
							Log.info(String.format("useFlag [%s] Alarm_code [%s]", data.get("use_flag"), Alarm_code));
							int ret = resourceHandlingDaoMapper.getAlarmHappen(Alarm_code);
							if (ret != 1) {
								Log.info(String.format(
										"happened to Alarm Code[%s] : Alarm flag [ON] -> [OFF] (Alarmconfig TB AlarmCode Not Exist)",
										Alarm_code));
								resourceHandlingDaoMapper.putAlarmstatehistory_OFF(Alarm_flag, Alarm_code);
								resourceHandlingDaoMapper.removeAlarmstate(Alarm_code);
							}
							continue;
						}

						int alarm_lv = Integer.parseInt(data.get("alarm_lv"));

						if (alarm_lv == 4) {
							FaultOn(data);
							continue;
						}

						int alarm_flag = Integer.parseInt(Alarm_flag);

						switch (alarm_flag) {
						case 0:
							AlarmOFF(data);
							break;

						case 1:
							AlarmON(data);
							break;

						default:
							Log.info("Not define Alarm_flag");
							break;
						}
					} else {
						Log.error("check the Db state");
					}
				} catch (Exception e) {
					Log.fatal("Thread Error");
				}
				Thread.sleep(500);
			}
		} catch (InterruptedException e) {
			// TODO: handle exception
			Log.fatal("MessageQueuePolling Thread Interrupted Exception", e);
		} finally {
			k32lib.CloseHandle(sock);
			Log.fatal("MessageQueuePolling Thread Exit");
		}
	}
}
