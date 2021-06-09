/*
 * Created on Oct 24, 2005
 */
package b01.foc.file;

import b01.foc.Globals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * @author 01Barmaja
 */
public abstract class FocFileReader extends FocLineReader{
	
  public abstract void readLine(StringBuffer buffer);
	
  protected File file = null;
  //protected FileReader reader = null;
  protected InputStreamReader reader = null;
  
  public FocFileReader(){
  }

  public FocFileReader(File file, char fieldDelimiter){
  	super(fieldDelimiter);
  	setFile(file);
  }
  
  public FocFileReader(InputStream inputStream, char fieldDelimiter){
    super(fieldDelimiter);
    setInputStream(inputStream);
  }
  
  public void dispose(){
  	file =null;
  	reader = null;  	
  }
    
  public boolean setInputStream(InputStream inputStream){
    boolean ok = false;
    close();
    if(inputStream != null){
      reader = new InputStreamReader(inputStream);
      ok = true;
    }
    return ok;
  }
  
  public boolean setFile(File file){
    boolean ok = false;
    close();
    if(file != null){
      try{
        reader = new FileReader(file);
        ok = true;
      }catch(Exception e){
        Globals.logException(e);
      }
    }
    return ok;
  }
 
  public File getFile() {
    return file;
  }

  public StringBuffer loadLine(){
    StringBuffer ret = new StringBuffer();
    try{
      boolean stop = false; 
      int aChar = 0;
      do{
      	aChar = reader.read();
      }while(aChar == 10 || aChar == 13);
      stop = aChar == -1;
      while(!stop){
        ret.append((char)aChar);
      	aChar = reader.read();
        stop = aChar == -1 || aChar == 10 || aChar == 13;
      }
    }catch(Exception e){
      Globals.logException(e);
    }
    return ret;
  }
  
  public void readFile(){
    StringBuffer buff = null;
    buff = loadLine();
    while(buff != null && buff.length() > 0){
    	Globals.logString("Reading line:"+buff);
      readLine(buff);
      buff = loadLine();
    }
  }
  
  public void close(){
  	if(reader != null){
  		try {
				reader.close();
				reader = null;
			} catch (IOException e) {
				Globals.logException(e);
			}
  	}
  }
}