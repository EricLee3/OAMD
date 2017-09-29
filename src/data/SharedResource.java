package data;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ��¥ : 2016. 12. 2.
 * ������Ʈ : OAMD
 * ������ : �迬ȣ
 */
public class SharedResource {
	
	/* Shared Data */
	public static ConcurrentHashMap<String, Integer> CPU;
	public static ConcurrentHashMap<String, String> DISK;
	public static ConcurrentHashMap<String, String> MEMORY;
	
	public static void Init() {
		CPU = new ConcurrentHashMap<>();
		DISK = new ConcurrentHashMap<>();
		MEMORY = new ConcurrentHashMap<>();
	}
}
