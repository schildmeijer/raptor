/**
 * 
 */
package org.raptorframework.raptor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/** 
 * @author Roger Schildmeijer
 *
 */
public class AsyncFileMonitor implements FileMonitor {

	private final Map<File, FileObserver> observers = new HashMap<File, FileObserver>();
	private final Map<File, Long> fileStatuses = new HashMap<File, Long>();
	private final long checkInterval = 1000 * 1; /* ms */
	private final Object lock = new Object();

	private ScheduledExecutorService scheduler;

	public void registerFileObserver(File fileToObserve, FileObserver listener) {
		fileStatuses.put(fileToObserve, fileToObserve.lastModified());

		synchronized (lock) {
			observers.put(fileToObserve, listener);
			if (observers.size() == 1) {
				scheduler = Executors.newSingleThreadScheduledExecutor();
				scheduler.scheduleAtFixedRate(
						filesPoller, 0 /*initial delay*/, 
						checkInterval, 
						TimeUnit.MILLISECONDS
				);
			}
		}

	}

	public void unregisterFileObserver(File observedFile) {
		synchronized (lock) {
			fileStatuses.remove(observedFile);
			observers.remove(observedFile);
			if (observers.isEmpty()) {
				scheduler.shutdown();
				scheduler = null;
			}
		}
	}

	private Runnable filesPoller = new Runnable() {

		public void run() {
			synchronized (lock) {
				detectChanges();
			}
		}
	};
	
	private void detectChanges() {
		for (File candidate: observers.keySet()) {
			if (candidate.lastModified() != fileStatuses.get(candidate)) {
				fileChanged(candidate);
			}
		}
	}
	
	private void fileChanged(File file) {
		fileStatuses.put(file, file.lastModified());
		observers.get(file).fileChanged(file);
	}

}
