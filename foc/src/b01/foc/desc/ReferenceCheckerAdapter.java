/*
 * Created on Jun 12, 2005
 */
package b01.foc.desc;

import b01.foc.db.*;
import b01.foc.list.*;
import b01.foc.property.*;

/**
 * @author 01Barmaja
 */
public class ReferenceCheckerAdapter implements ReferenceChecker{
  private FocDesc focDesc = null;//FocDesc to search in for the reference
  private int objRefField = 0;//The field in which the reference might be stored
  private SQLFilter filter = null;//
  private FocLinkSimple focLinkSimple = null;

  public ReferenceCheckerAdapter(FocDesc focDescToSearchIn, int objRefField){
    this.focDesc = focDescToSearchIn;
    this.objRefField = objRefField;
  }

  private FocLink getfocLinkSimple(){
    if(focLinkSimple != null){
      focLinkSimple = new FocLinkSimple(focDesc);
    }
    return focLinkSimple ;
  }
  
  private SQLFilter getFilter(){
    if(filter == null){
      FocConstructor constr = new FocConstructor(focDesc, null, null);
      FocObject templateFocObj = constr.newItem();
      if(templateFocObj != null){
      	templateFocObj.setDbResident(false);
        filter = new SQLFilter(templateFocObj, SQLFilter.FILTER_ON_SELECTED);
        filter.addSelectedField(objRefField);
      }
    }
    return filter;
  }
  
  /*public int getNumberOfReferences(FocObject obj, StringBuffer message){
    int nbRef = 0;
    SQLFilter filter = getFilter();
    if (focDesc != null && filter != null) {
      FocLinkSimple focLinkSimple = new FocLinkSimple(focDesc);
      FocList focList = new FocList(focLinkSimple);

      FocObject templateObj = (FocObject)filter.getObjectTemplate();
      if(templateObj != null){
        FProperty prop = (FProperty)templateObj.getFocProperty(objRefField);
        
        if(FTypedObject.class.isInstance(prop)){
          FTypedObject objProp = (FTypedObject)templateObj.getFocProperty(objRefField);
          if(objProp != null){
            FocTypedObject typedObj = (FocTypedObject) objProp.getObject();
            if(typedObj != null){
              ObjectTypeMap typeMap = objProp.getObjectTypeMap();
              ObjectType objType = typeMap.findObjectType(obj);
              if(objType != null){
                typedObj.setType(objType.getId());
                typedObj.setFocObject(obj);
              }
            }
          }
        }else if(FObject.class.isInstance(prop)){
          FObject objProp = (FObject)templateObj.getFocProperty(objRefField);          
          if(objProp != null){
            objProp.setObject(obj);
          }
        } 
      }
      
      SQLSelect select = new SQLSelect(focList, focDesc, filter);
      boolean loaded = !select.execute();
      loadedFocList = select.getFocList();

      nbRef = loadedFocList.size();
      if(nbRef > 0 && message != null){
        message.append("\n"+focDesc.getStorageName()+", "+focDesc.getFieldByID(objRefField).getTitle()+", "+nbRef+"times");
        for( int i = 0; i < loadedFocList.size(); i++){
          
          FocObject focObj = loadedFocList.getFocObject(i);
          
          //DEBUG
          FocFieldEnum iter = new FocFieldEnum(focObj.getThisFocDesc(), focObj, FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
          while(iter != null && iter.hasNext()){
            iter.next();
            FProperty prop = (FProperty) iter.getProperty();
            if(prop != null){
              System.out.print(prop.getString()+"     ");
            }
          }
          System.out.println();
        }
        //DEBUG
      
      }
    }    
    return nbRef;
  }*/
  
  private void adaptFilter(FocObject obj){
  	SQLFilter filter = getFilter();
    if (focDesc != null && filter != null) {
      FocObject templateObj = (FocObject)filter.getObjectTemplate();
      if(templateObj != null){
        FProperty prop = (FProperty)templateObj.getFocProperty(objRefField);
        
        if(FTypedObject.class.isInstance(prop)){
          FTypedObject objProp = (FTypedObject)templateObj.getFocProperty(objRefField);
          if(objProp != null){
            FocTypedObject typedObj = (FocTypedObject) objProp.getObject();
            if(typedObj != null){
              ObjectTypeMap typeMap = objProp.getObjectTypeMap();
              ObjectType objType = typeMap.findObjectType(obj);
              if(objType != null){
                typedObj.setType(objType.getId());
                typedObj.setFocObject(obj);
              }
            }
          }
        }else if(FObject.class.isInstance(prop)){
          FObject objProp = (FObject)templateObj.getFocProperty(objRefField);          
          if(objProp != null){
            objProp.setObject(obj);
          }
        } 
      }
    }
  }
  
  
  public int getNumberOfReferences(FocObject obj, StringBuffer message){
    int nbRef = 0;
    SQLFilter filter = getFilter();
    if (focDesc != null && filter != null) {
      FocLinkSimple focLinkSimple = new FocLinkSimple(focDesc);
      FocList focList = new FocList(focLinkSimple);
      adaptFilter(obj);
      /*FocObject templateObj = (FocObject)filter.getObjectTemplate();
      if(templateObj != null){
        FProperty prop = (FProperty)templateObj.getFocProperty(objRefField);
        
        if(FTypedObject.class.isInstance(prop)){
          FTypedObject objProp = (FTypedObject)templateObj.getFocProperty(objRefField);
          if(objProp != null){
            FocTypedObject typedObj = (FocTypedObject) objProp.getObject();
            if(typedObj != null){
              ObjectTypeMap typeMap = objProp.getObjectTypeMap();
              ObjectType objType = typeMap.findObjectType(obj);
              if(objType != null){
                typedObj.setType(objType.getId());
                typedObj.setFocObject(obj);
              }
            }
          }
        }else if(FObject.class.isInstance(prop)){
          FObject objProp = (FObject)templateObj.getFocProperty(objRefField);          
          if(objProp != null){
            objProp.setObject(obj);
          }
        } 
      }*/
      
      SQLSelect select = new SQLSelect(focList, focDesc, filter);
      boolean loaded = !select.execute();
      loadedFocList = select.getFocList();

      nbRef = loadedFocList.size();
      if(nbRef > 0 && message != null){
        message.append("\n"+focDesc.getStorageName()+", "+focDesc.getFieldByID(objRefField).getTitle()+", "+nbRef+"times");
        for( int i = 0; i < loadedFocList.size(); i++){
          
          FocObject focObj = loadedFocList.getFocObject(i);
          //DEBUG
          FocFieldEnum iter = new FocFieldEnum(focObj.getThisFocDesc(), focObj, FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
          while(iter != null && iter.hasNext()){
            iter.next();
            FProperty prop = (FProperty) iter.getProperty();
            if(prop != null){
              System.out.print(prop.getString()+"     ");
            }
          }
          System.out.println();
        }
        //DEBUG
      }
    }    
    return nbRef;
  }
  
  public void redirectReferencesToNewFocObject(FocObject focObjectToRedirectFrom, FocObject focObjectToRedirectTo){
  	adaptFilter(focObjectToRedirectFrom);
  	FocConstructor constr = new FocConstructor(focDesc, null);
  	FocObject newFocObject = constr.newItem();
  	FObject fObjProp = (FObject) newFocObject.getFocProperty(objRefField);
  	boolean isDesactivateListener = fObjProp.isDesactivateListeners();
  	fObjProp.setDesactivateListeners(true);
  	fObjProp.setObject(focObjectToRedirectTo);
  	fObjProp.setDesactivateListeners(isDesactivateListener);
  	SQLUpdate update = new SQLUpdate(focDesc, newFocObject, getFilter());
  	update.addQueryField(objRefField);
  	update.execute();
  }
  
  private FocList loadedFocList = null;
  public FocList getLoadedFocList(){
    return loadedFocList;
  }
}
