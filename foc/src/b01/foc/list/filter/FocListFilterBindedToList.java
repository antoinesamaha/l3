// MEMORY LEVEL
// DATABASE LEVEL
// COMMON LEVEL

/*
 * Created on Jul 9, 2005
 */
package b01.foc.list.filter;

import b01.foc.desc.FocConstructor;
import b01.foc.list.FocList;
import b01.foc.list.FocListWithFilter;

/**
 * @author 01Barmaja
 */
public class FocListFilterBindedToList extends FocListFilter {  
	private static final String FILTER_TABLE_NAME_SUFFIX = "_FILTER";
  private FocListWithFilter focList = null;
  
  public FocListFilterBindedToList(FocConstructor constr){
    super(constr);
    setFilterLevel(FocListFilter.LEVEL_DATABASE);
  }

  public FocList getFocList(){
  	return focList;
  }
  
  public void setActive(FocListWithFilter focList, boolean active) {
  	this.focList = focList;
  	setActive(active);
  }
  
  @Override
	public void reloadListFromDatabase() {
		focList.reloadFromDB_Super();
	}

	public void dispose(){
  	super.dispose();
  	focList = null;
  }
	
 /* public void refreshDisplay(){
  }*/
  
	public static String getFilterTableName(String baseTableName){
		return baseTableName == null ? null : baseTableName + FocListFilterBindedToList.FILTER_TABLE_NAME_SUFFIX;
	}

	@Override
	public FilterDesc getThisFilterDesc() {
		FocDescForFilter focDesc = (FocDescForFilter) getThisFocDesc();
		return focDesc.getFilterDesc();
	}
}