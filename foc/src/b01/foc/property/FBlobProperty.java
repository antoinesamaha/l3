package b01.foc.property;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Blob;

import b01.foc.Globals;
import b01.foc.desc.FocObject;

public class FBlobProperty extends FProperty{

  private FileInputStream fileInputStream = null;
  private File file = null;
  
  
  public FBlobProperty(FocObject focObj, int fieldID, Blob defaultValue) {
    super(focObj, fieldID);
  }
  
  public void setFile(File file){
    try{
      this.file = file;
      fileInputStream = new FileInputStream(this.file);
    }catch(Exception e){
      Globals.logException(e);
    }
  }
  
  public long getFileLenght(){
    return file.length();
  }
  
  public FileInputStream getFileInputStream(){
    return fileInputStream;
  }

  @Override
  public String getSqlString() {
    return "\"" + getString() + "\"";
  }
  
}
