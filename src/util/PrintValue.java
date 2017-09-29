package util;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import data.SharedCritical;
import data.SharedResource;

/**
 * ��¥ : 2016. 12. 2. ������Ʈ : OAMD ������ : �迬ȣ
 */
public class PrintValue {

	static Logger Log = Logger.getLogger("OAMD");

	public static void printCriticalValue() {

		Log.debug(String.format("Critical Value[%s]", SharedCritical.CRITICAL_VALUE));
	}

	public static void printResourceValue() {
		ConcurrentHashMap<String, Integer> cpu = new ConcurrentHashMap<>();
		ConcurrentHashMap<String, String> disk = new ConcurrentHashMap<>();
		ConcurrentHashMap<String, String> memory = new ConcurrentHashMap<>();

		cpu = SharedResource.CPU;
		disk = SharedResource.DISK;
		memory = SharedResource.MEMORY;

		Log.debug(String.format("CPU     [%s]", cpu));
		Log.debug(String.format("DISK    [%s]", disk));
		Log.debug(String.format("MEMORY  [%s]", memory));
	}
}