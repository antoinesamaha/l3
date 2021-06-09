/*
 * Created on Oct 14, 2004
 */
package b01.foc.list;

import b01.foc.db.*;
import b01.foc.desc.*;
import b01.foc.join.*;

/**
 * @author 01Barmaja
 */
public class FocLinkJoinRequest extends FocLinkSimple {
  private FocRequestDesc requestDesc = null;
  
  public FocLinkJoinRequest(FocRequestDesc requestDesc) {
    super(requestDesc.getFocDesc());
    this.requestDesc = requestDesc;
  }

  public FocDesc getLinkTableDesc() {
    return null;
  }

  public void adaptSQLFilter(FocList list, SQLFilter filter) {
  }

  public boolean loadDB(FocList focList) {
    boolean loaded = false;
    FocDesc slaveDesc = getSlaveDesc();
    if (slaveDesc != null) {
      focList.setLoaded(true);
      SQLSelect select = new SQLSelectJoinRequest(focList, requestDesc, focList.getFilter());
      loaded = !select.execute();
      FocList loadedFocList = select.getFocList();
      focList.synchronize(loadedFocList);
      focList.resetStatusWithPropagation();
      //focList.backupAllObjects();
    }
    return true;
  }
}
