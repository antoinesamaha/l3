/*
 * Created on 15 fvr. 2004
 */
package b01.foc.property;

import java.text.Format;
import java.util.StringTokenizer;

import b01.foc.desc.*;
import b01.foc.desc.field.*;

/**
 * @author Standard
 */
public class FAttributeLocationProperty extends FString implements Cloneable{
  //private FDescProperty descProperty = null;
  private FFieldPath fieldPath = null;
  private FFieldPath backupFieldPath = null;
  private FocDesc localFocDesc = null;

  public FAttributeLocationProperty(FocObject focObj, int fieldID, FFieldPath fieldPath) {
    super(focObj, fieldID, "");  
    this.fieldPath = fieldPath;
  }
  
  public void dispose(){
  	super.dispose();
  	fieldPath = null;
    backupFieldPath = null;
    localFocDesc = null;
  }

  protected Object clone() throws CloneNotSupportedException {
    FAttributeLocationProperty zClone = (FAttributeLocationProperty)super.clone();
    zClone.fieldPath = fieldPath;
    return zClone;
  }
  
  private String getString(boolean forSQL){
    StringBuffer str = new StringBuffer();
    
    if(fieldPath != null){      
      FocDesc currDesc = getBaseFocDesc();
      FField[] fieldArray = fieldPath.getFieldArrayFromDesc(currDesc);
      
      if(currDesc != null){
        if(!forSQL){
          str.append(currDesc.getStorageName() + ":");
        }
        
        for(int i=0; i<fieldPath.size(); i++){
          if(fieldArray[i] != null){
            if(i>0){
              str.append(".");
            }
            if(forSQL){
              str.append(fieldArray[i].getID());
            }else{
              str.append(fieldArray[i].getName());
            }
          }
        }
      }
    }
    return str.toString();
  }

  //BElias
  // Let the function call the static function that does the same work
  // so the static function will fill the fieldPath
  
  /*private void setString(boolean forSQL, String str){
    if(str != null && getDescProperty()!= null){
      StringTokenizer strTok = new StringTokenizer(str, ":.");    
      FFieldPath fieldPath = new FFieldPath(strTok.countTokens());
      
      int index = 0;
      FocDesc currDesc = getDescProperty().getSelectedFocDesc();
      FField currField = null;
      while(strTok.hasMoreTokens()){
        String nT = strTok.nextToken();
        int fieldId = FField.NO_FIELD_ID;
        if(forSQL){
          fieldId = Integer.valueOf(nT).intValue();
        }else{
          currField = currDesc.getFieldByName(nT);
          if(currField != null){
          	fieldId = currField.getID();
          	if(index < strTok.countTokens()-1){
              currDesc = currField.getFocDesc();
          	}
          }
        }
        fieldPath.set(index++, fieldId);        
      }
      
      setFieldPath(fieldPath);
    }
  }*/
  
  private void setString(boolean forSQL, String str){
		setFieldPath(FAttributeLocationProperty.newFieldPath(forSQL, str, getBaseFocDesc()));
  }
  //EElias

  public static FFieldPath newFieldPath(boolean forSQL, String str, FocDesc initialFocDesc){
  	return newFieldPath(forSQL, str, initialFocDesc, null);
  }

  public static FFieldPath newFieldPath(boolean forSQL, String str, FocDesc initialFocDesc, FocObject initialObject){
  	FFieldPath fieldPath = null;
  	if(str != null && initialFocDesc!= null){
      StringTokenizer strTok = new StringTokenizer(str, ":.");
      int tokensCount = strTok.countTokens();
      fieldPath = new FFieldPath(tokensCount);
      
      int index = 0;
      FocDesc   currDesc   = initialFocDesc;
      FField    currField  = null;
      FocObject currObject = initialObject;
      FProperty currProp   = null;
      while(strTok.hasMoreTokens()){
        String nT = strTok.nextToken();
        int fieldId = FField.NO_FIELD_ID;
        if(forSQL){
          fieldId = Integer.valueOf(nT).intValue();
        }else{
          currField = currDesc.getFieldByName(nT);
          if(currField != null){
          	fieldId    = currField.getID();
          	currProp   = currObject != null ? currObject.getFocProperty(fieldId) : null;
          	
          	if(index < tokensCount - 1){
              if(initialObject != null && currField instanceof FTypedObjectField){
              	currObject = (currProp != null && currProp.isObjectProperty()) ? ((FObject) currProp).getObject_CreateIfNeeded() : null;
              	currObject = ((FocTypedObject) currObject).getFocObject_CreateIfNeeded();
              	currDesc   = currObject.getThisFocDesc();
              }else{
              	currDesc   = currField.getFocDesc();
              	currObject = (currProp != null && currProp.isObjectProperty()) ? ((FObject) currProp).getObject_CreateIfNeeded() : null;
              }
          	}
          }
        }
        fieldPath.set(index++, fieldId);        
      }
      
      //setFieldPath(fieldPath);
    }
    return fieldPath;
  }

  public static FProperty newFieldPathReturnProperty(boolean forSQL, String str, FocDesc initialFocDesc, FocObject initialObject){
  	FFieldPath fieldPath = null;
    FProperty currProp   = null;  	
  	if(str != null && initialFocDesc!= null){
      StringTokenizer strTok = new StringTokenizer(str, ":.");
      int tokensCount = strTok.countTokens();
      fieldPath = new FFieldPath(tokensCount);
      
      int index = 0;
      FocDesc   currDesc   = initialFocDesc;
      FField    currField  = null;
      FocObject currObject = initialObject;

      while(strTok.hasMoreTokens()){
        String nT = strTok.nextToken();
        if(currDesc != null){
	        int fieldId = FField.NO_FIELD_ID;
	        if(forSQL){
	          fieldId = Integer.valueOf(nT).intValue();
	        }else{
	          currField = currDesc.getFieldByName(nT);
	          if(currField != null){
	          	fieldId    = currField.getID();
	          	currProp   = currObject != null ? currObject.getFocProperty(fieldId) : null;
	          	
	          	if(index < tokensCount - 1){
	              if(initialObject != null && currField instanceof FTypedObjectField){
	              	currObject = (currProp != null && currProp.isObjectProperty()) ? ((FObject) currProp).getObject_CreateIfNeeded() : null;
	              	if(currObject != null){
		              	currObject = ((FocTypedObject) currObject).getFocObject_CreateIfNeeded();
		              	currDesc   = currObject.getThisFocDesc();
	              	}
	              }else{
	              	currDesc   = currField.getFocDesc();
	              	currObject = (currProp != null && currProp.isObjectProperty()) ? ((FObject) currProp).getObject_CreateIfNeeded() : null;
	              }
	          	}
	          }else{
	          	//BElias
	          	currObject = null;
	          	currDesc = null;
	          	currProp = null;
	          	//EElias
	          }
	        }
	        fieldPath.set(index++, fieldId);
      	}
      }
      
      //setFieldPath(fieldPath);
    }
    return currProp;
  }

  public String getString() {
    return getString(false);
  }

  public void setString(String str) {
    setString(false, str);
  }

  public String getSqlString() {    
    return "\"" + getString(true) + "\"";
  }

  public void setSqlString(String str) {    
    setString(true, str);
  }
  
  public void setObject(Object obj) {
    setFieldPath((FFieldPath) obj);
  }

  public Object getObject() {
    return (Object) getFieldPath();
  }
  
  public void setLocalFocDesc(FocDesc localFocDesc){
  	this.localFocDesc = localFocDesc;
  }
  
  private FocDesc getLocalFocDesc(){
  	return this.localFocDesc;
  }
  
  public FFieldPath getFieldPath(){
    return fieldPath;
  }
  
  public void setFieldPath(FFieldPath fieldPath){
    this.fieldPath = fieldPath;
    notifyListeners();
  }  
  
	@Override
	public Object getTableDisplayObject(Format format) {
		return getString();
	}

	@Override
	public void setTableDisplayObject(Object obj, Format format) {
		setString((String)obj);
	}
  
  private IFDescProperty getDescProperty() {
    FAttributeLocationField attLocField = (FAttributeLocationField) getFocField();
    FFieldPath fieldPath = attLocField.getDescPropertyFieldPath();
    IFDescProperty descProperty = null;
    if(fieldPath != null){
    	descProperty = (IFDescProperty) fieldPath.getPropertyFromObject(getFocObject());
    }
    return descProperty;
  }
  
  public FocDesc getBaseFocDesc(){
  	FocDesc focDesc = getLocalFocDesc();
  	if(focDesc == null){
  		IFDescProperty descProperty = getDescProperty();
  		if(descProperty != null){
  			focDesc = descProperty.getSelectedFocDesc();
  		}
  	}
  	return focDesc;
  }
  
  public void backup() {
    backupFieldPath = fieldPath; 
  }
  
  public void restore() {
    setFieldPath(backupFieldPath);    
  }
}
