/**
 * 
 */
package org.raptorframework.raptor;

import java.io.File;

/**
 * @author Roger Schildmeijer
 *
 */
public interface FileObserver {
	
	void fileChanged(File file);
	
}
