/*
 * Created on 15 fvr. 2004
 */
package b01.foc.property;

import b01.foc.desc.*;
import b01.foc.desc.field.FListField;
import b01.foc.list.*;

/**
 * @author 01Barmaja
 */
public class FList extends FProperty {
  private FocList list = null;

  public FList(FocObject fatherObj, int fieldID, FocList list) {
    super(fatherObj, fieldID);
    this.list = list;
    list.setFatherSubject(fatherObj);
    list.setFatherProperty(this);
  }

  public void dispose(){
    FListField listField = (FListField)getFocField();
    if(listField != null){
      FocLink link = listField.getLink();
      if (link.disposeList(list)){
        list = null;
      }
    }
    super.dispose();
  }
  
  public void popupSelectionPanel() {
  }

  public String getString() {
    return "";
  }

  public void setString(String str) {
  }

  public void setInteger(int iVal) {
  }

  public int getInteger() {
    return 0;
  }

  public void setDouble(double dVal) {
  }

  public double getDouble() {
    return 0;
  }

  public void setObject(Object obj) {
    /*
     * focObjValue = (FocObject) obj; notifyListeners();
     */
  }

  public Object getObject() {
    FocList list = getList();
    return list != null ? (Object) list.getSingleTableDisplayObject() : null;
  }

  public FProperty getFocProperty(int fldId){
    FProperty prop = null;
    FocObject singleDisplayObj = (FocObject) getObject();
    
    if(singleDisplayObj != null){
      prop = singleDisplayObj.getFocProperty(fldId);
    }

    return prop;
  }  
  
  /*
   * public void setFocObject(FocObject obj) { focObjValue = obj;
   * notifyListeners(); }
   * 
   * public FocObject getFocObject() { return focObjValue; }
   */
  public void backup() {
  }

  public void restore() {
  }

  /**
   * @return
   */
  public FocList getList() {
    if (list != null && getFocField() != null && getFocField().isDBResident()) list.loadIfNotLoadedFromDB();
    return list;
  }
  
  public FocList getListWithoutLoad() {    
    return list;
  }
  
  public void copy(FProperty sourceProp){
    FList sourceListProp = (FList) sourceProp;
    FocList sourceList = sourceListProp.getList();
    FocList targetList = this.getList();
    if(sourceList != null && targetList != null){
      targetList.copy(sourceList);
    }
  }
}
