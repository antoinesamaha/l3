package b01.foc.list;

import b01.foc.db.SQLFilter;
import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocObject;
import b01.foc.list.filter.FocDescForFilter;
import b01.foc.list.filter.FocListFilterBindedToList;

public class FocListWithFilter extends FocList {
	private FocListFilterBindedToList filter = null;

  private void init(FocDescForFilter filterFocDesc){
    if(filterFocDesc != null){
      FocConstructor constr = new FocConstructor(filterFocDesc, null);
      setFocListFilter((FocListFilterBindedToList)constr.newItem());
    }
  }
  
	public FocListWithFilter(FocDescForFilter filterFocDesc, FocLink focLink) {
		super(focLink);
    init(filterFocDesc);
	}
	
	public FocListWithFilter(FocDescForFilter filterFocDesc, FocObject masterObject, FocLink focLink, SQLFilter filter) {
    super(masterObject, focLink, filter);
    init(filterFocDesc);    
  }

  public void dispose(){
		super.dispose();
		disposeFilter();
	}
	
	private void disposeFilter(){
		if(filter != null){
			filter.dispose();
			filter = null;
		}
	}

	protected void setFocListFilter(FocListFilterBindedToList filter){
		disposeFilter();
		this.filter = filter;
	}
	
	public void reloadFromDB_Super(){
		super.reloadFromDB();
	}
	
  public void reloadFromDB() {
  	if(filter != null){
      int filterLevel = filter.getFilterLevel();
      filter.setFilterLevel(FocListFilterBindedToList.LEVEL_DATABASE);
  		filter.setActive(this, true);
      filter.setFilterLevel(filterLevel);
  	}else{
  		reloadFromDB_Super();
  	}
  }
  
  public FocListFilterBindedToList loadFilterByReference(int filterRef){
  	if(filter != null){
	  	filter.setReference(filterRef);
	  	filter.load();
  	}
  	return filter;
  }
  
  public FocListFilterBindedToList getFocListFilter(){
  	return filter;
  }
}
