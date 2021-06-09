/*
 * Created on Oct 14, 2004
 */
package b01.foc.desc.field;

import b01.foc.desc.*;
import b01.foc.list.*;
import b01.foc.property.FProperty;
import b01.foc.property.FTypedObject;

import java.util.*;

/**
 * @author 01Barmaja
 */
public class FTypedObjectField extends FObjectField {
  private String keyPrefix = null;
  private ObjectTypeMap objectTypeMap = null;

  public FTypedObjectField(String name, String title, int id, boolean key, FocDesc focDesc, String keyPrefix, ObjectTypeMap objectTypeMap) {
    super(name, title, id, key, focDesc, keyPrefix);
    this.objectTypeMap = objectTypeMap;
  }
  
  public void dispose(){
    super.dispose();
    if( objectTypeMap != null ){
      objectTypeMap.dispose();
      objectTypeMap = null;
    }
  }
  
  public FProperty newProperty(FocObject masterObj, Object defaultValue){
    return new FTypedObject(masterObj, getID(), (FocTypedObject) defaultValue, getObjectTypeMap());
  }
  
  public FProperty newProperty(FocObject masterObj){
    return newProperty(masterObj, null);
  }

  public ObjectTypeMap getObjectTypeMap(){
    return objectTypeMap;
  }

  public Iterator getChoicesIterator(){
    return objectTypeMap != null ? objectTypeMap.iterator() : null;
  }
  
  public FocList getSelectionList(int type) {
    ObjectType typedObjectType = (ObjectType) objectTypeMap.get(type);
    FocList list = typedObjectType.getSelectionList();
    list.loadIfNotLoadedFromDB();
    return list;
  }

  public FocDesc getDesc(int type) {
    ObjectType typedObjectType = (ObjectType) objectTypeMap.get(type);
    FocList list = typedObjectType.getSelectionList();
    list.loadIfNotLoadedFromDB();
    return list.getFocDesc();
  }
  
  public int getTypeFromDescription(FocDesc desc){
    int type = -1;
    Iterator iter = objectTypeMap.iterator();
    while(iter != null && iter.hasNext()){
      ObjectType objType = (ObjectType) iter.next();
      if(objType != null){
        FocList list = objType.getSelectionList();
        FocDesc listFocDesc = list.getFocDesc();
        if(listFocDesc.getFocObjectClass() == desc.getFocObjectClass()){
          type = objType.getType();
        }
      }
    }
    return type;
  }
  
  public boolean isObjectContainer(){
    return true;
  }
  
  
  public void addReferenceLocations(FocDesc pointerDesc) {
    ObjectTypeMap map = getObjectTypeMap();
    Iterator iter = map.iterator();
    while(iter != null && iter.hasNext()){
      ObjectType type = (ObjectType)iter.next();
      FocDesc targetDesc = type.getFocDesc();//type.getSelectionList().getFocDesc();//type.getFocDesc();
      
      //Adding a field reference checker
      ReferenceCheckerAdapter refCheck = new ReferenceCheckerAdapter(pointerDesc, getID());
      if(targetDesc != null){
        targetDesc.addReferenceLocation(refCheck);
      }
    }
  }
}
