package b01.fab.model.filter;

import b01.fab.FilterFocDescDeclaration;
import b01.foc.Globals;
import b01.foc.list.filter.FilterDesc;
import b01.foc.list.filter.FocDescForFilter;

public class FocDescForUserDefinedFilter extends FocDescForFilter {
	
	public FocDescForUserDefinedFilter(Class focObjectClass, boolean dbResident, String storageName, boolean isKeyUnique){
		super(focObjectClass, dbResident, storageName, isKeyUnique);
	}

	@Override
	public FilterDesc getFilterDesc() {
		if(this.filterDesc == null){
			FilterFocDescDeclaration filterFocDescDeclaration = (FilterFocDescDeclaration)Globals.getApp().getIFocDescDeclarationByName(getStorageName());
			if(filterFocDescDeclaration != null){
				this.filterDesc = filterFocDescDeclaration.getFilterDesc_CreateIfNeeded();
			}
		}
		return this.filterDesc;
	}

}
