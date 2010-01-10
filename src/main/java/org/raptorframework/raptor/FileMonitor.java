/**
 * 
 */
package org.raptorframework.raptor;

import java.io.File;

/**
 * @author Roger Schildmeijer
 *
 */
public interface FileMonitor {

		void registerFileObserver(File file, FileObserver listener);
		
		void unregisterFileObserver(File file);
		
}
