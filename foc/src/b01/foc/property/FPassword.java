/*
 * Created on 15 fvr. 2004
 */
package b01.foc.property;

import b01.foc.desc.*;

/**
 * @author 01Barmaja
 */
public class FPassword extends FProperty {
  private String str;
  private String backupStr = null;

  public FPassword(FocObject focObj, int fieldID, String str) {
    super(focObj, fieldID);
    this.str = str;
  }

  public boolean isEmpty(){
    return str == null || str.compareTo("") == 0;
  }

  public String getString() {
    return str;
  }

  public void setStringWithoutEncryption(String str){
    this.str = str;
    notifyListeners();
  }
  
  public void setString(String str) {
  	//Comming from gui modification => convert to MD5
  	setStringWithoutEncryption(str);
  }

  protected void setSqlStringInternal(String str) {
  	setStringWithoutEncryption(str);
  }
  
  public String getSqlString() {
    return "\"" + getString() + "\"";
  }

  public void setInteger(int iVal) {
    setString(String.valueOf(iVal));
  }

  public int getInteger() {
    return Integer.parseInt(str);
  }

  public void setDouble(double dVal) {
    setString(String.valueOf(dVal));
  }

  public double getDouble() {
    return Double.parseDouble(str);
  }

  public void setObject(Object obj) {
    setString((String) obj);
  }

  public Object getObject() {
    return (Object) getString();
  }
  
  public void backup() {
    backupStr = str;    
  }
  
  public void restore() {
    str = backupStr != null ? backupStr : "" ;
  }
}
