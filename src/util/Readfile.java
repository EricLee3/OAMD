package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import org.apache.log4j.Logger;

/**
 * ��¥ : 2016. 12. 2.
 * ������Ʈ : OAMD
 * ������ : �迬ȣ
 */
public class Readfile {
	static Logger Log = Logger.getLogger("OAMD");

	public String getConfFile(String pathname, String word) throws IOException {

		File file = new File(pathname);
		String value = "";
		
		if (!file.isFile()) {
			Log.fatal(String.format("File Not Exist Check Config File"));
			System.exit(0);
		}

		FileInputStream fileInputStream = new FileInputStream(file);
		Scanner scanner = new Scanner(fileInputStream);
		String findline = "";

		while (scanner.hasNext()) {
			String line = scanner.nextLine();

			if (line.length() == 0)
				continue;
			
			if (line.charAt(0) == '#') {
				continue;
			}
			
			if (line.indexOf(word) != -1) {
				findline = line;
			}
		}

		if (findline.equals("")) {
			fileInputStream.close();
			scanner.close();

			throw new IOException("Not Found Word");
		} else {
			String[] format = findline.split("=");

			value = format[1];
			
			fileInputStream.close();
			scanner.close();
			return value;
		}
	}
}
