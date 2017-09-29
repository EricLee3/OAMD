package data;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;

import org.apache.log4j.Logger;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;  
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import com.sun.management.OperatingSystemMXBean;

import dao.ResourceHandlingDaoMapper;

/**
 * 날짜 : 2016. 12. 2. 프로젝트 : OAMD 생성자 : 김연호
 */
public class ResourceMonitor {
	Logger Log = Logger.getLogger("OAMD");

	ResourceHandlingDaoMapper resourcehandlingDao;

	private Sigar sigar;

	public ResourceMonitor() {
		// TODO Auto-generated constructor stub
		if (sigar == null && resourcehandlingDao == null) {
			sigar = new Sigar();
			resourcehandlingDao = new ResourceHandlingDaoMapper();
		}
	}

	public Boolean getDisk() {
		FileSystem[] fileSystemList = null;
		FileSystemUsage fileSystemUsage = null;

		try {
			fileSystemList = sigar.getFileSystemList();
		} catch (Exception e) {
			Log.error(String.format("(%s) Sigar Api get fail", this.getClass().getName()), e);
		}

		for (FileSystem fileSystem : fileSystemList) {
			if (fileSystem.getTypeName().equals("local")) {
				fileSystemUsage = new FileSystemUsage();
				try {
					fileSystemUsage.gather(sigar, fileSystem.getDirName());

				} catch (SigarException e) {
					e.getMessage();
				}
				String Key = fileSystem.getDevName() + "\\";

				SharedResource.DISK.put(Key, String.format("%s/%s", fileSystemUsage.getUsed() / 1024 / 1024,
						fileSystemUsage.getTotal() / 1024 / 1024));
			}
		}
		return true;
	}

	public boolean getCPU() throws IOException {
		OperatingSystemMXBean osBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory
				.getOperatingSystemMXBean();

		double load = osBean.getSystemCpuLoad();

		if (load < 0.0) {
			return false;
		}
		SharedResource.CPU.put("TOTAL", (int) (load * 100.0));

		return true;
	}

	public boolean getMemory() {
		Mem mem = null;

		String pattern = "####.#";

		try {
			mem = sigar.getMem();
		} catch (SigarException e) {
			// TODO Auto-generated catch block
			Log.error(String.format("(%s) Sigar Api get fail", this.getClass().getName()), e);
		}
		Double totalCPU = (double) mem.getTotal() / 1024 / 1024 / 1024;
		Double usedCPU = (double) mem.getUsed() / 1024 / 1024 / 1024;

		DecimalFormat df = new DecimalFormat(pattern);

		SharedResource.MEMORY.put("MEMORY",
				String.format("%s/%s", String.valueOf(df.format(usedCPU)), String.valueOf(df.format(totalCPU))));

		return true;
	}
}
