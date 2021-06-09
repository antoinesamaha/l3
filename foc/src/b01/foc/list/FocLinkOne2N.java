/*
 * Created on Oct 14, 2004
 */
package b01.foc.list;

import b01.foc.db.*;
import b01.foc.desc.*;

import java.util.*;

/**
 * @author 01Barmaja
 */
public class FocLinkOne2N extends FocLink {
  private FocDesc linkTableDesc = null;

  public FocLinkOne2N(FocDesc masterDesc, FocDesc slaveDesc) {
    super(masterDesc, slaveDesc);
    slaveDesc.addMasterReferenceField(masterDesc);
  }

  public void adaptSQLFilter(FocList list, SQLFilter filter) {
    if (list != null && filter != null) {
      filter.setMasterObject(list.getMasterObject());
    }
  }

  public FocDesc getLinkTableDesc() {
    return null;
  }

  public boolean saveDB(FocList focList) {
    return  false;
  }

  public boolean loadDB(FocList focList) {
    boolean bool = false;
    if(focList.getMasterObject() != null && !focList.getMasterObject().isCreated() && focList.getMasterObject().getThisFocDesc().getDBResident()){
      bool = loadDBDefault(focList);
    }
    return bool ;
  }

  public boolean deleteDB(FocList focList) {
    boolean deleted = false;
    FocDesc slaveDesc = getSlaveDesc();
    if (slaveDesc != null) {
      Iterator iter = focList.focObjectIterator();
      while(iter != null && iter.hasNext()){
        FocObject obj = (FocObject) iter.next();
        if(obj != null) obj.delete();
      }
      SQLDelete delete = new SQLDelete(slaveDesc, focList.getFilter());
      delete.execute();
      deleted = true;
    }
    return deleted;
  }
  
  public boolean disposeList(FocList list){
    list.dispose();
    return true;
  }
  
  /* (non-Javadoc)
   * @see b01.foc.list.FocLink#copy(b01.foc.list.FocList, b01.foc.list.FocList)
   */
  public void copy(FocList targetList, FocList sourceList) {
    super.copyDefault(targetList, sourceList, true);
  }
  
  public FocObject getSingleTableDisplayObject(FocList list){
    return null;
  }
}
