package data;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 날짜 : 2016. 12. 2.
 * 프로젝트 : OAMD
 * 생성자 : 김연호
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
