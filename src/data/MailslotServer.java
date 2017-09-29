package data;

import org.apache.log4j.Logger;

import com.sun.jna.ptr.IntByReference;

public class MailslotServer {
	Logger Log = Logger.getLogger("OAMD");
	public JNAKernel32 k32lib;
	int lastError = 0;
	int nextMsgSize = 0;

	public int ServerInit(String slotPath) {

		k32lib = JNAKernel32.INSTANCE;

		if (!MailslotExists(slotPath)) {
			@SuppressWarnings("static-access")
			int hSlot = CreateMailSlot(slotPath, k32lib.MSG_MAX_SIZE, JNAKernel32.MAILSLOT_WAIT_FOREVER);

			if (hSlot > 0) {
				return hSlot;
			} else {
				Log.error("Create MailSlot failed.");
				return -1;
			}
		} else {
			Log.error("Creation Mailslot impossible : existed");
			return -1;
		}
	}

	public int CreateMailSlot(String path, int maxSize, int timeOut) {

		int hSlot = k32lib.CreateMailslot(path, maxSize, timeOut, 0);
		if (hSlot == JNAKernel32.INVALID_HANDLE_VALUE)
			lastError = k32lib.GetLastError();

		return hSlot;
	}

	public boolean MailslotExists(String path) {
		int hFile = k32lib.CreateFile(path, JNAKernel32.GENERIC_READ + JNAKernel32.GENERIC_WRITE,
				JNAKernel32.FILE_SHARE_READ, 0, JNAKernel32.OPEN_EXISTING, 0, 0);
		if (hFile != JNAKernel32.INVALID_HANDLE_VALUE) {
			k32lib.CloseHandle(hFile);
			return true;
		}
		return false;
	}

	public boolean hasMessage(int hSlot) {
		IntByReference maxMsg = new IntByReference();
		IntByReference nextMsg = new IntByReference();
		IntByReference msgCount = new IntByReference();
		IntByReference timeOut = new IntByReference();

		if (k32lib.GetMailslotInfo(hSlot, maxMsg, nextMsg, msgCount, timeOut)) {
			nextMsgSize = nextMsg.getValue();
			return msgCount.getValue() > 0;
		} else
			return false;
	}

	public static String b2s(byte b[]) {
		// Converts C string to Java String
		int len = 0;
		
		while (b[len] != 0) {
			len += 1;
		}

		return new String(b, 0, len);
	}
}