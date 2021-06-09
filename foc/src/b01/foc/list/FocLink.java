/*
 * Created on Oct 14, 2004
 */
package b01.foc.list;

import b01.foc.*;
import b01.foc.db.*;
import b01.foc.desc.*;

import java.util.*;

/**
 * @author 01Barmaja
 */
public abstract class FocLink {
  private FocDesc masterDesc = null;
  private FocDesc slaveDesc = null;

  public abstract FocDesc getLinkTableDesc();

  public abstract boolean saveDB(FocList focList);

  public abstract boolean loadDB(FocList focList);

  public abstract boolean deleteDB(FocList focList);

  public abstract void copy(FocList targetList, FocList sourceList);
  
  public abstract void adaptSQLFilter(FocList list, SQLFilter filter);

  public abstract FocObject getSingleTableDisplayObject(FocList list);
  
  public abstract boolean disposeList(FocList list);
  
  
  protected boolean loadDBDefault(FocList focList) {
    boolean loaded = false;
    FocDesc slaveDesc = getSlaveDesc();
    if (slaveDesc != null) {
      focList.setLoaded(true);
      SQLSelect select = new SQLSelect(focList, slaveDesc, focList.getFilter());
      loaded = !select.execute();
      FocList loadedFocList = select.getFocList();
      focList.synchronize(loadedFocList);
      focList.resetStatusWithPropagation();
      //focList.backupAllObjects();
    }
    return loaded;
  }
  
  public void copyDefault(FocList targetList, FocList sourceList, boolean copyObjects){
    try{
      if(targetList != null && sourceList != null){
        Iterator iter = sourceList.focObjectIterator();
        while(iter != null && iter.hasNext()){
          FocObject sourceObj = (FocObject) iter.next();
          if(sourceObj != null){
            FocObject targetObj = null;
            if(copyObjects){              
              targetObj = targetList.newEmptyItem();
              targetObj = sourceObj.duplicate(targetObj, targetList.getMasterObject(), false);
            }else{
              targetObj = sourceObj;
              targetList.add(targetObj);
            }
          }
        }
      }
    } catch(Exception e){
      Globals.logException(e);
    }
  }
  
  public FocLink(FocDesc masterDesc, FocDesc slaveDesc) {
    this.masterDesc = masterDesc;
    this.slaveDesc = slaveDesc;
  }

  /**
   * @return
   */
  public FocDesc getMasterDesc() {
    return masterDesc;
  }

  /**
   * @return
   */
  public FocDesc getSlaveDesc() {
    return slaveDesc;
  }
  
  public void setSlaveDesc(FocDesc focDesc) {
    this.slaveDesc = focDesc;
  }
}
