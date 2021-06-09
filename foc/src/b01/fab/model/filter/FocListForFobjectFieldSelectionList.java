package b01.fab.model.filter;

import b01.foc.Globals;
import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocDesc;
import b01.foc.list.FocLink;
import b01.foc.list.FocListWithFilter;
import b01.foc.list.filter.FocListFilterBindedToList;

public class FocListForFobjectFieldSelectionList extends FocListWithFilter {
	private int filterRef = -1;

	public FocListForFobjectFieldSelectionList(int filterRef, FocLink focLink) {
		super(null, focLink);
		if(filterRef > 0){
			setFilterRef(filterRef);
		}
	}
	
	private void setFilterRef(int filterRef){
		this.filterRef = filterRef;
	}
	
	private int getFilterRef(){
		return this.filterRef;
	}
	
	private void createFilterIfNull(){
		UserDefinedFilter filter = (UserDefinedFilter)getFocListFilter();
		if(filter == null){
			FocDesc focDesc = getFocDesc();
			String filterFocDescName = FocListFilterBindedToList.getFilterTableName(focDesc.getStorageName());
			if(filterFocDescName != null){
				FocDesc filterFocDesc = Globals.getApp().getFocDescByName(filterFocDescName);
				FocConstructor constr = new FocConstructor(filterFocDesc, null);
				filter = (UserDefinedFilter)constr.newItem();
				setFocListFilter(filter);
			}
		}
	}
	
	public void reloadFromDB() {
		UserDefinedFilter filter = (UserDefinedFilter)getFocListFilter();
		if(filter == null){
			loadFilterByReference(getFilterRef());
		}
  	super.reloadFromDB();
  }
	
	public FocListFilterBindedToList loadFilterByReference(int filterRef){
		createFilterIfNull();
		return super.loadFilterByReference(filterRef);
  }
}
