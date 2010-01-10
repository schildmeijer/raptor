/**
 * 
 */
package org.raptor;

import java.io.File;

/**
 * @author rogerschildmeijer
 *
 */
public interface FileMonitor {

		void registerFileObserver(File file, FileObserver listener);
		
		void unregisterFileObserver(File file, FileObserver listener);
}
