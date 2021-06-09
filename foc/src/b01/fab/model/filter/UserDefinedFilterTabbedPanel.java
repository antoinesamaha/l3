package b01.fab.model.filter;

import java.util.ArrayList;
import java.util.Iterator;

import b01.foc.Globals;
import b01.foc.desc.FocDesc;
import b01.foc.gui.FGTabbedPane;
import b01.foc.gui.FPanel;
import b01.foc.gui.FValidationPanel;
import b01.foc.list.FocLinkSimple;
import b01.foc.list.FocList;
import b01.foc.list.filter.FocListFilterGuiBrowsePanel;

@SuppressWarnings("serial")
public class UserDefinedFilterTabbedPanel extends FPanel{
	private ArrayList<FocList> filterListArray = null;
	
	@SuppressWarnings("unchecked")
	public UserDefinedFilterTabbedPanel(){
		super("Filter creation", FPanel.FILL_BOTH);
		FGTabbedPane tabbedPan = new FGTabbedPane();
		FocLinkSimple filterDefinitionLink = new FocLinkSimple(FilterDefinitionDesc.getInstance());
		FocList filterDefinitionList = new FocList(filterDefinitionLink);
		filterDefinitionList.loadIfNotLoadedFromDB();
		Iterator<FilterDefinition> iter = filterDefinitionList.focObjectIterator();
		while(iter != null && iter.hasNext()){
			FilterDefinition filterDefinition = iter.next();
			if(filterDefinition != null){
				String tableName = filterDefinition.getFilterTableName();
				FocDesc filterFocDesc = Globals.getApp().getFocDescByName(tableName);
				FocLinkSimple filterLink = new FocLinkSimple(filterFocDesc);
				FocList filterList = new FocList(filterLink);
				filterList.loadIfNotLoadedFromDB();
				addFilterList(filterList);
				FPanel filterBrowsePanel = new FocListFilterGuiBrowsePanel(filterList, UserDefinedFilter.VIEW_FOR_FILTER_CREATION);
				tabbedPan.add(tableName, filterBrowsePanel);
			}
		}
		add(tabbedPan);
		setMainPanelSising(FPanel.FILL_BOTH);
		FValidationPanel validPanel = showValidationPanel(true);
		if(validPanel != null){
			Iterator<FocList> filterListIterator = getFilterListIterator();
			while(filterListIterator != null && filterListIterator.hasNext()){
				FocList filterList = filterListIterator.next();
				validPanel.addSubject(filterList);
			}
			validPanel.setValidationType(FValidationPanel.VALIDATION_SAVE_CANCEL);
		}
	}
	
	private ArrayList<FocList> getFilterListArray(){
		if(filterListArray == null){
			filterListArray = new ArrayList<FocList>();
		}
		return filterListArray;
	}
	
	private void addFilterList(FocList filterList){
		if(filterList != null){
			getFilterListArray().add(filterList);
		}
	}
	
	private Iterator<FocList> getFilterListIterator(){
		return getFilterListArray().iterator();
	}
	
	public void dispose(){
		super.dispose();
		filterListArray = null;
	}
}
