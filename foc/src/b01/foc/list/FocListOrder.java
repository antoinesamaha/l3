package b01.foc.list;

import b01.foc.desc.FocObject;
import b01.foc.desc.field.*;
import b01.foc.property.*;
import java.util.*;

public class FocListOrder implements Comparator {
  FListOrderFieldList fieldList = null;

  public FocListOrder() {
    this.fieldList = new FListOrderFieldList();
  }

  public FocListOrder(int fld1) {
    this.fieldList = new FListOrderFieldList();
    addField(FFieldPath.newFieldPath(fld1));
  }

  public FocListOrder(int fld1, int fld2) {
    this.fieldList = new FListOrderFieldList();
    addField(FFieldPath.newFieldPath(fld1));
    addField(FFieldPath.newFieldPath(fld2));
  }

  public void dispose(){
    if(fieldList != null){
      fieldList.dispose();
      fieldList = null;
    }
  }
  
  public int getFieldsNumber() {
    return fieldList != null ? fieldList.size() : 0;
  }

  public FFieldPath getFieldAt(int i) {
    return fieldList != null ? fieldList.getFieldPathAt(i) : null;
  }

  public boolean isAscendingAt(int i) {
    return fieldList != null ? fieldList.isFieldAscending(i) : true;
  }

  public void addField(FFieldPath path, boolean ascending) {
    if (fieldList != null) {
      fieldList.add(path, ascending);
    }
  }

  public void addField(FFieldPath path) {
    addField(path, true);
  }

  public int compareFocObject(FocObject focObj, FocObject otherFocObj) {
    int compRes = 0;
    boolean error = focObj == null || otherFocObj == null;

    if (!error) {
      FProperty prop = null;
      FProperty otherProp = null;

      for (int iFld = 0; iFld < getFieldsNumber() && compRes == 0; iFld++) {
        FFieldPath fPath = this.getFieldAt(iFld);

        if (fPath != null) {
          prop = fPath.getPropertyFromObject(focObj);
          otherProp = fPath.getPropertyFromObject(otherFocObj);
          if (prop != null && otherProp != null) {
            compRes = prop.compareTo(otherProp);
            if(!this.isAscendingAt(iFld)){
              compRes = -compRes;  
            }            
          }
        }
      }
    }

    return compRes;
  }

  public int compare(Object obj, Object other) {
    FocListElement element = (FocListElement) obj;
    FocListElement otherElement = (FocListElement) other;
    int compRes = 0;
    FocObject otherFocObj = null;
    FocObject focObj = null;
    boolean error = otherElement == null || element == null;

    if (!error) {
      otherFocObj = otherElement.getFocObject();
      focObj = element.getFocObject();
      error = otherFocObj == null || focObj == null;
    }

    if (!error) {
      compRes = compareFocObject(focObj, otherFocObj);
    }

    return compRes;
  }
}
