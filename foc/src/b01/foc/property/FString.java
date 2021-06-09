/*
 * Created on 15 fvr. 2004
 */
package b01.foc.property;

import java.text.Format;

import b01.foc.Globals;
import b01.foc.db.DBManager;
import b01.foc.desc.*;

/**
 * @author Standard
 */
public class FString extends FProperty implements Cloneable{
  private String str;
  private String backupStr = null;
  
  public FString(FocObject focObj, int fieldID, String str) {
    super(focObj, fieldID);
    this.str = str;
    backupStr = null;
  }

  protected Object clone() throws CloneNotSupportedException {
    FString zClone = (FString)super.clone();
    zClone.setString(str);
    return zClone;
  }
  
  public boolean isEmpty(){
    return getString() != null && getString().trim().compareTo("") == 0;
  }
  
  public String getString() {
    return str;
  }

  public void setString(String str) {
  	if(str == null){
  		int debug = 3;
  	}
    /*if((this.str == null && str != null) || (this.str != null && str == null) || (this.str != null && str != null && this.str.compareTo(str) != 0)){*/  
    if( doSetString(str) ){
      this.str = str;
      notifyListeners();
      setInherited(false);
    }
  }

  public boolean doSetString(String str){
    return (this.str == null && str != null) || (this.str != null && str == null) || (this.str != null && str != null && this.str.compareTo(str) != 0);
  }
  
  public String getSqlString() {
    return "\"" + getString() + "\"";
  	/* String req = getString();
    if (   req!=null && req.contains("\'") 
    		&& Globals.getDBManager()!= null && Globals.getDBManager().getProvider() == DBManager.PROVIDER_ORACLE){
  		int idxQuote = req.indexOf('\'');
  		if (req.substring(idxQuote+1, idxQuote+2).equals("\'")) { //if (req.substring(idxQuote+1).startsWith("\'") )
  			String str = req.substring(0,idxQuote+1)+"\'"+req.substring(idxQuote+1);
  			req = str;
  		}
    }
    return "\"" + req + "\"";*/
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
  
  public Object getTableDisplayObject(Format format) {
  
    Object displayObj = null;
    String realValue = getString();
    
    if(realValue != null && realValue.equals("")){
      Object tempObj = getFocObject().getFirstAncestorTableDisplayObject(getFocField().getID(), format);
      if(tempObj != null){
        displayObj = tempObj;  
      }else{
        displayObj = "";
      }
    }else{
      displayObj = realValue;
    }
    
    return displayObj;
  }

  public void setTableDisplayObject(Object obj, Format format) {
    super.setTableDisplayObject(obj, format);
  }

  public void backup() {
    backupStr = str;    
  }
  
  public void restore() {
    str = backupStr != null ? backupStr : "" ;
  }
}
