/*
 * Created on 15 fvr. 2004
 */
package b01.foc.property;

import b01.foc.desc.*;
import b01.foc.desc.field.*;

/**
 * @author 01Barmaja
 */
public class FTypedObject extends FInLineObject{
  private ObjectTypeMap localTypeListMap = null;  

  //private int typeBackup = 0;
  //private FocObject focObjValueBackup = null;
  
  public FTypedObject(FocObject fatherObj, int fieldID, FocTypedObject typedObject, ObjectTypeMap localTypeListMap){
    super(fatherObj, fieldID, null);
    this.localTypeListMap = localTypeListMap;
    if(typedObject != null){
      setObject(typedObject);
    }
  }

  public FTypedObject(FocObject fatherObj, int fieldID) {
    super(fatherObj, fieldID, null);
    this.localTypeListMap = null;
  }
  
  public void dispose(){
    super.dispose();
    localTypeListMap = null;
  }
  
  public ObjectTypeMap getObjectTypeMap(){
    ObjectTypeMap ret = localTypeListMap;
    if(ret == null){
      FTypedObjectField field = (FTypedObjectField) getFocField();
      ret = field.getObjectTypeMap();
    }
    return ret;
  }

  public int getType(){
    FInt pType = (FInt) getFocProperty(FocTypedObject.FLD_TYPE);
    return pType.getInteger();
  }

  public void setType(int type){
    FInt pType = (FInt) getFocProperty(FocTypedObject.FLD_TYPE);
    pType.setInteger(type);
  }
  
  public Object getObject(){
  	return super.getObject();
  }
  
  public FocObject getObject_CreateIfNeeded(){
  	return super.getObject_CreateIfNeeded();
  }
  
  /*
  public void backup() {
    FocObject obj = (FocObject)getObject();
    if(obj != null){
      obj.backup();      
    }
  }

  public void restore() {
    FocObject obj = (FocObject)getObject();
    if(obj != null){
      obj.restore();      
    }
  }
  
  public int hashCode() {
    FocObject obj = (FocObject)getObject();
    return obj != null ? obj.hashCode() : super.hashCode();
  }
  
  public int compareTo(FProperty prop) {
    int ret = 0;
    FTypedObject otherProp = (FTypedObject) prop;
    FocObject obj = (FocObject)getObject();
    FocObject otherObj = (FocObject)otherProp.getObject();
    if(obj == null && otherObj != null){
      ret = -1;
    }else if(obj != null && otherObj == null){
      ret = 1;
    }else{
      ret = obj.compareTo(otherObj);
    }
    return ret; 
  }
  */
  
  /*
  public void copy(FProperty sourceProp){
    FTypedObject pSourceTypedObj = (FTypedObject) sourceProp;
    FocTypedObject sourceTypedObj = (FocTypedObject)pSourceTypedObj.getObject();
    FocTypedObject typedFocObject = (FocTypedObject) getObject();
    
    FocTypedObject dupTypedObj = (FocTypedObject) sourceTypedObj.duplicate(typedFocObject, typedFocObject != null ? typedFocObject.getMasterObject() : null, true);
    
    setObject(dupTypedObj);
  }
  */
}
