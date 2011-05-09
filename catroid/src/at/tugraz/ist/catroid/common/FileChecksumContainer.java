/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author David Reisenberger, Johannes Iber
 * 
 */
public class FileChecksumContainer implements Serializable {

	private static final long serialVersionUID = 1L;

	public class FileInfo {
		private String checksum;
		private int usage;
		private String path;
	}

	private Map<String, FileInfo> fileReferenceMap = new HashMap<String, FileInfo>(); //checksum / usages

	/**
	 * 
	 * @param checksum
	 * @param path
	 * @return true if a new File is added and false if the file already exists
	 */
	public boolean addChecksum(String checksum, String path) {
		if (fileReferenceMap.containsKey(checksum)) {
			FileInfo fileInfo = fileReferenceMap.get(checksum);
			++fileInfo.usage;
			return false;
		} else {
			FileInfo fileInfo = new FileInfo();
			fileInfo.checksum = checksum;
			fileInfo.usage = 1;
			fileInfo.path = path;
			fileReferenceMap.put(checksum, fileInfo);
			return true;
		}
	}

	//	private void incrementValue(String checksum) {
	//		if (fileReferenceMap.containsKey(checksum)) {
	//			fileReferenceMap.put(checksum, fileReferenceMap.get(checksum) + 1);
	//		}
	//	}

	public boolean containsChecksum(String checksum) {
		return fileReferenceMap.containsKey(checksum);
	}

	public String getPath(String checksum) {
		return fileReferenceMap.get(checksum).path;
	}

	public String getChecksumForPath(String filepath) {
		//		for (Map.Entry<String, String> entry : checksumFilePathMap.entrySet()) {
		//			if (entry.getValue().equals(filepath)) {
		//				return entry.getKey();
		//			}
		//		}
		return null;
	}

	public boolean deleteChecksum(String checksum) {
		if (!fileReferenceMap.containsKey(checksum)) {
			return false;
		}
		//		if (fileReferenceMap.get(checksum) > 1) {
		//			fileReferenceMap.put(checksum, fileReferenceMap.get(checksum) - 1);
		//			return false;
		//		} else {
		//			File toDelete = new File(checksumFilePathMap.get(checksum));
		//			toDelete.delete();
		//			fileReferenceMap.remove(checksum);
		//			checksumFilePathMap.remove(checksum);
		//			return true;
		//		}
		return false;
	}

}