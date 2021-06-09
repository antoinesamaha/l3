/*
 * Created on 15 fvr. 2004
 */
package b01.foc.property;

import b01.foc.desc.*;

/**
 * @author 01Barmaja
 */
public class FBoolean extends FProperty {
  private boolean bVal;
  private boolean backupBool = false;

  public FBoolean(FocObject focObj, int fieldID, boolean bVal) {
    super(focObj, fieldID);
    this.bVal = bVal;
  }

  public int hashCode() {
    return getInteger();
  }

  public int compareTo(FProperty prop) {
    return (prop != null) ? prop.getInteger() - getInteger() : 1;
  }

  public int getInteger() {
    return bVal ? 1 : 0;
  }

  public void setInteger(int iVal) {
    setBoolean(iVal != 0);
  }

  public String getString() {
    //return String.valueOf(bVal);
    return String.valueOf(getInteger());
  }

  public void setString(String str) {
    if (str == null || str.compareTo("") == 0) {
      setBoolean(false);
    } else {
    	try{
    		setInteger(Integer.valueOf(str).intValue());
      //setBoolean(Boolean.getBoolean(str));
    	}catch(NumberFormatException e){
    		if(str.toUpperCase().equals("TRUE")){
    			setString("1");
    		}else if(str.toUpperCase().equals("FALSE")){
    			setString("0");
    		}
    	}
    }
  }

  public void backup() {
    backupBool = this.bVal;
  }

  public void restore() {
    this.bVal = backupBool;
  }

  public double getDouble() {
    return (double) getInteger();
  }

  public void setDouble(double dVal) {
    setInteger((int) dVal);
  }

  public void setObject(Object obj) {
    if (obj != null) {
      setBoolean(((Boolean) obj).booleanValue());
    }
  }

  public Object getObject() {
    return new Boolean(getBoolean());
  }

  public void setBoolean(boolean b) {
    if(bVal != b){
      bVal = b;
      notifyListeners();
    }
  }

  public boolean getBoolean() {
    return bVal;
  }
}
