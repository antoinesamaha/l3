package b01.foc.list.filter;

import b01.foc.desc.FocDesc;

public abstract class FocDescForFilter extends FocDesc {
	
	public abstract FilterDesc getFilterDesc();

	protected FilterDesc filterDesc = null; 

	public FocDescForFilter(Class focObjectClass, boolean dbResident, String storageName, boolean isKeyUnique){
		super(focObjectClass, dbResident, storageName, isKeyUnique);
		setGuiBrowsePanelClass(FocListFilterGuiBrowsePanel.class);
		setGuiDetailsPanelClass(FocListFilterGuiDetailsPanel.class);
	
		addReferenceField();
		addNameField();
		
	  getFilterDesc().fillDesc(this, 1);
	}
}
