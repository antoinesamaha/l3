package b01.l3;

import java.util.HashMap;
import java.util.Iterator;

public class FileIOFactory {
	private HashMap<String, Class> fileIOMap = null;
	
	private static FileIOFactory fileIOFactory = null;
	
	public static FileIOFactory getInstance(){
		if(fileIOFactory == null){
			fileIOFactory = new FileIOFactory();
			fileIOFactory.addFileIO("b01.l3.filePool.DefaultFileIO", b01.l3.connector.fileConnector.DefaultFileIO.class);
		}
		return fileIOFactory;
	}
	
	public  FileIOFactory(){
		fileIOMap = new HashMap<String, Class>();
	}
	
	public void addFileIO(String className, Class cls){
  	if(fileIOMap != null && cls != null){
  		fileIOMap.put(className, cls);
  	}
  }
	
	public Class getFileIO(String code){
    return fileIOMap != null && code != null? fileIOMap.get(code) : null;
    //return driverMap.get("b01.l3.drivers.kermit.Vitros250Driver");
  }
	
  public Iterator<String> newClassNameIterator(){
  	return fileIOMap.keySet().iterator();
  }
}
