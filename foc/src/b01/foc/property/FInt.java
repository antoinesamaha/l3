/*
 * Created on 15 fvr. 2004
 */
package b01.foc.property;

import java.text.Format;

import b01.foc.desc.*;
import b01.foc.desc.field.FIntField;

/**
 * @author 01Barmaja
 */
public class FInt extends FProperty {
  protected int iVal;
  private int backupInt = 0;

  public FInt(FocObject focObj, int fieldID, int iVal) {
    super(focObj, fieldID);
    this.iVal = iVal;
  }

  public int hashCode() {
    return iVal;
  }

  public int compareTo(FProperty prop) {
    return (prop != null) ? iVal - prop.getInteger() : 1;
  }

  public boolean isEmpty(){
    return iVal == 0;
  }

  public int getInteger() {
    return iVal;
  }

  public void setInteger(int iVal) {
    if(iVal != this.iVal){
      this.iVal = iVal;
      notifyListeners();
    }
  }

  public String getString() {
    return String.valueOf(iVal);
  }

  public void setString(String str) {
    if (str == null || str.compareTo("") == 0) {
      setInteger(0);
    } else {
      setInteger(Integer.parseInt(str));
    }
  }

  public void backup() {
    backupInt = this.iVal;
  }

  public void restore() {
    setInteger(backupInt);
  }

  public double getDouble() {
    return (double) iVal;
  }

  public void setDouble(double dVal) {
    setInteger((int) dVal);
  }

  public void setObject(Object obj) {
    if (obj != null) {
      setInteger(((Integer) obj).intValue());
    }
  }

  public Object getObject() {
    return Integer.valueOf(getInteger());
  }

  public Object getTableDisplayObject(Format format) {
    Object displayObj = null;
    int realValue = getInteger();
    
    if(realValue == 0){
      FProperty fPropAncestorCustomized = getFocObject().getFirstAncestorCustomizedProperty(getFocField().getID());
      if(fPropAncestorCustomized != null){
        realValue = fPropAncestorCustomized.getInteger();
      }
    }
    
    if( realValue == 0 && !((FIntField)getFocField()).isDisplayZeroValues() ){
      displayObj = "";
    }else{
      displayObj = format != null ? format.format(Integer.valueOf(realValue)) : getString();
    }
    
    return displayObj.toString();
  }

  public void setTableDisplayObject(Object obj, Format format) {
    setString((String)obj);
  }
}
