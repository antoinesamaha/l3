/*
 * Created on Oct 14, 2004
 */
package b01.foc.list;

import b01.foc.db.*;
import b01.foc.desc.FocDesc;
import b01.foc.desc.FocObject;

/**
 * @author 01Barmaja
 */
public class FocLinkSimple extends FocLink {

  public FocLinkSimple(FocDesc slaveDesc) {
    super(null, slaveDesc);
  }

  public FocDesc getLinkTableDesc() {
    return null;
  }

  public void adaptSQLFilter(FocList list, SQLFilter filter) {
  }

  public boolean saveDB(FocList focList) {
    /*
     * Iterator iter = focList.focObjectIterator(); while(iter.hasNext()){
     * FocObject obj = (FocObject)iter.next(); if(obj != null){ obj.save(); } }
     */

    return false;
  }

  public boolean deleteDB(FocList focList) {
    return false;
  }

  public boolean loadDB(FocList focList) {
    return loadDBDefault(focList);
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
