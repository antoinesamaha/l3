/*
 * Created on 15 fvr. 2004
 */
package b01.foc.property;

import java.text.Format;

import b01.foc.*;
import b01.foc.desc.*;
import b01.foc.desc.field.FNumField;

/**
 * @author 01Barmaja
 */
public class FDouble extends FProperty {
  private double dVal;
  private double dBackupVal;

  public FDouble(FocObject focObj, int fieldID, double dVal) {
    super(focObj, fieldID);
    this.dVal = dVal;
  }

  public int compareTo(FProperty prop) {
    int comp = 1;
    if (prop != null) {
      if (prop.getDouble() > getDouble()) {
        comp = -1;
      } else if (prop.getDouble() < getDouble()) {
        comp = 1;
      } else {
        comp = 0;
      }
    }
    return comp;
  }

  public boolean isEmpty(){
    return dVal == 0;
  }
  
  public int getInteger() {
    return (int) dVal;
  }

  public void setInteger(int iVal) {
    setDouble((double) iVal);
  }

  public String getString() {
    /*
    FNumField numFld = (FNumField) getFocField();
    numFld.getFormat();
    */
    return String.valueOf(dVal);
  }

  public void setString(String str) {
    double d = 0;
    if(str != null && str.compareTo("") != 0){
    	try{
        d = Double.parseDouble(str);
    	}catch(Exception e){
    		Globals.logString("parsing string :"+str+" Exception "+e.getMessage());
    		d = dVal;
    	}
    }
    setDouble(d);    
  }

  public double getDouble() {
    return dVal;
  }

  public void setDouble(double dVal) {
    if(dVal != this.dVal){
      this.dVal = dVal;
      notifyListeners();
      setInherited(false);
    }
  }

  public void setObject(Object obj) {
    if (obj != null) {
      setDouble(((Double) obj).doubleValue());
    }
  }

  public Object getObject() {
    return Double.valueOf(getDouble());
  }
  
  public Object getTableDisplayObject(Format format) {
    Object displayObj = null;
    double realValue = getDouble();
    
    if( realValue == 0 && !((FNumField)getFocField()).isDisplayZeroValues() ){
      Object tempObj = getFocObject().getFirstAncestorTableDisplayObject(getFocField().getID(), format);
      if(tempObj != null){
        displayObj = tempObj;  
      }else{
        displayObj = "";
      }
    }else{
      displayObj = format != null ? format.format(Double.valueOf(realValue)) : getString();
    }
    
    return displayObj;
  }

  public void setTableDisplayObject(Object obj, Format format) {
    //setDouble(((Double)obj).doubleValue());
    //setString((String)obj);
    
    try{
      if(obj == null || ((String)obj).trim().compareTo("") == 0){
        setDouble(0);
      }else{
        if(format != null){
          Number dbl = (Number) format.parseObject((String)obj);
          setDouble(dbl.doubleValue());
        }else{
          setString((String)obj);
        }
      }
    }catch(Exception e){
      Globals.logException(e);
    }
  }
  
  public void backup() {
    dBackupVal = dVal;
  }

  public void restore() {
    dVal = dBackupVal ;
  }  
}
