package data;

import java.util.HashMap;
import java.util.Map;

import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIFunctionMapper;
import com.sun.jna.win32.W32APITypeMapper;

@SuppressWarnings("serial")
public interface JNAKernel32 extends StdCallLibrary{
	@SuppressWarnings({ "unchecked", "rawtypes" })  
	Map ASCII_OPTIONS = new HashMap() {
		{
			put(OPTION_TYPE_MAPPER, W32APITypeMapper.ASCII);
			put(OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.ASCII);
		}
	};
	@SuppressWarnings({ "unchecked", "rawtypes" })
	Map UNICODE_OPTIONS = new HashMap() {
		{
			put(OPTION_TYPE_MAPPER, W32APITypeMapper.UNICODE);
			put(OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.UNICODE);
		}
	};

	@SuppressWarnings("rawtypes")
	Map DEFAULT_OPTIONS = Boolean.getBoolean("w32.ascii") ? ASCII_OPTIONS : UNICODE_OPTIONS;

	JNAKernel32 INSTANCE = (JNAKernel32) Native.loadLibrary("kernel32", JNAKernel32.class, DEFAULT_OPTIONS);

	// return values
	public static final int INVALID_HANDLE_VALUE = -1;
	public static final int MAILSLOT_NO_MESSAGE = -1;

	// timeout
	public static final int MAILSLOT_WAIT_FOREVER = -1;

	// msg sizes
	public static final int MSG_ANY_SIZE = 0;
	public static final int MSG_MAX_SIZE = 4096; // suffira pour tout le monde ?
													// API MAX = 65536

	// file access
	public static final int GENERIC_READ = 0x80000000;
	public static final int GENERIC_WRITE = 0x40000000;

	// file shares
	public static final int FILE_SHARE_READ = 1;
	public static final int FILE_SHARE_WRITE = 2;
	public static final int FILE_SHARE_DELETE = 4;

	// creation types
	public static final int CREATE_ALWAYS = 2;
	public static final int CREATE_NEW = 1;
	public static final int OPEN_ALWAYS = 4;
	public static final int OPEN_EXISTING = 3;
	public static final int TRUNCATE_EXISTING = 5;

	// file attributes
	public static final int FILE_ATTRIBUTE_NORMAL = 128;

	boolean CloseHandle(int hObject);

	int GetLastError();

	int CreateMailslot(String lpName, int nMaxMessageSize, int lReadTimeout, int lpSecurityAttributes);

	boolean GetMailslotInfo(int hMailslot, IntByReference lpMaxMessageSize, IntByReference lpNextSize,
			IntByReference lpMessageCount, IntByReference lpReadTimeout);

	boolean SetMailslotInfo(int hMailslot, int lReadTimeout);

	int CreateFile(String lpFileName, int dwDesiredAccess, int dwShareMode, int lpSecurityAttributes,
			int dwCreationDisposition, int dwFlagsAndAttributes, int hTemplateFile);

	boolean ReadFile(int hFile, byte[] lpBuffer, int nNumberOfBytesToRead, IntByReference lpNumberOfBytesRead,
			int lpOverlapped);

	boolean WriteFile(int hFile, String lpBuffer, int nNumberOfBytesToWrite, IntByReference lpNumberOfBytesWritten,
			int lpOverlapped);

}
