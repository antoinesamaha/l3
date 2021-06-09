/*
 * Created on 15 fvr. 2004
 */
package b01.foc.property;

import java.text.Format;
import java.util.ArrayList;

import b01.foc.desc.*;
import b01.foc.desc.field.*;

/**
 * @author 01Barmaja
 */
public class FReference extends FProperty {
  
  private FocRef reference = null;
  private FocRef backupReference = null;  

  private void init(FocRef ref){
    reference = ref;
    if(reference == null){
      reference = new FocRef();
    }
  }
  
  public FReference(FocObject focObj, int fieldID, FocRef ref) {
    super(focObj, fieldID);
    init(ref);
  }

  public FReference(FocObject focObj) {
    super(focObj, FField.REF_FIELD_ID);
    init(null);
  }
  
  public void dispose(){
    if(reference != null){
      reference.dispose();
      reference = null;
    }
    
    if(backupReference != null){
      backupReference.dispose();
      backupReference = null;
    }
    super.dispose();    
  }
  
  public int hashCode() {
    return reference.getInt();
  }

  public int compareTo(FProperty prop) {
    return (prop != null) ? getInteger() - prop.getInteger() : 1;
  }

  public void setReference(FocRef ref){
    if(reference.compareTo(ref) != 0){
      setReferenceWithoutNotification(ref);
      notifyListeners();
    }
  }

  public void setReferenceWithoutNotification(FocRef ref){
    if(reference != null){
      if(ref != null){
        reference.copy(ref);
      }else{
        reference.setInt(0);
      }
    }else{
      if(ref != null){
        reference = (FocRef) ref.clone();
      }else{
        reference = new FocRef(0);
      }
    }
  }

  public FocRef getReferenceClone(){
    return (FocRef)reference.clone() ;
  }
  
  public int getInteger() {    
    return reference != null ? reference.getInt() : 0;
  }

  public void setInteger(int iVal) {
    if(iVal != reference.getInt()){
      reference.setInt(iVal);
      notifyListeners();
    }
  }
  
  public void addListener(FPropertyListener propListener) {
    super.addListener(propListener);
  }

  public String getString() {
    return String.valueOf(getInteger());
  }

  public void setString(String str) {
    if (str == null || str.compareTo("") == 0) {
      setInteger(0);
    } else {
      setInteger(Integer.parseInt(str));
    }
  }

  public void backup() {
    backupReference = (FocRef) reference.clone();
  }

  public void restore() {
    setReference(backupReference);
  }

  public double getDouble() {
    return (double) getInteger();
  }

  public void setDouble(double dVal) {
    setInteger((int) dVal);
  }

  public void setObject(Object obj) {
    if (obj != null) {
      setReference((FocRef)obj);
    }
  }

  public Object getObject() {
    return (Object)reference;
  }

  public Object getTableDisplayObject(Format format) {
    return getString();
  }

  public void setTableDisplayObject(Object obj, Format format) {
    setString((String)obj);
  }
  
  public void setSqlString(String str) {
    super.setSqlString(str);
  }
}
