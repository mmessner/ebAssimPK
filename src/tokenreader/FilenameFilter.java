package tokenreader;

import java.io.File;

public class FilenameFilter implements java.io.FilenameFilter{

	private String fileExt;
	
	public FilenameFilter(String fileExt){
		this.fileExt = fileExt;
	}
	
	@Override
	public boolean accept(File dir, String name) {
		if(name.contains(fileExt)) {
			return true;
		}
		return false;
	}
	
}
