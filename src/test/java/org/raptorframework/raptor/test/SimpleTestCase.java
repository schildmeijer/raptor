/**
 * 
 */
package org.raptorframework.raptor.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.raptorframework.raptor.FileMonitor;
import org.raptorframework.raptor.FileMonitorImpl;
import org.raptorframework.raptor.FileObserver;

/**
 * @author Roger Schildmeijer
 *
 */
public class SimpleTestCase {

	public static final String path = "src/test/resources/file.txt";
	
	@Test
	public void fileExistTest() {
		File testFile = new File(path);
		assertTrue("Unable to find file: " + path, testFile.exists());
	}
	
	@Test
	public void simpleFileChangeTest() {
		FileMonitor fm = new FileMonitorImpl();
		
		File fileToObserve = new File (path);
		final AtomicInteger value = new AtomicInteger(0);
		fm.registerFileObserver(fileToObserve, new FileObserver() {
			
			public void fileChanged(File file) {
				value.set(1922);
				synchronized (value) {
					value.notifyAll();
				}
			}
		});
		
		/* emulate that some external part change the file content */
		fileToObserve.setLastModified(System.currentTimeMillis());
		
		synchronized (value) {
			try {
				value.wait(1000 * 30);
			} catch (InterruptedException e) {
				assertTrue("InterruptedException thrown when waiting for file to change.", false);
			}
		}
		
		fm.unregisterFileObserver(fileToObserve); // clean up
		
		assertTrue("Did not receive any fileChanged callback. " +
				"(Maybe the file did not change within time limit: 30s)", value.get() != 0);
	}
}
 