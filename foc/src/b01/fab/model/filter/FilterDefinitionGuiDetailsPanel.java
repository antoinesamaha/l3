package b01.fab.model.filter;

import java.awt.Component;

import b01.foc.desc.FocObject;
import b01.foc.gui.FPanel;
import b01.foc.gui.FValidationPanel;
import b01.foc.list.FocList;

@SuppressWarnings("serial")
public class FilterDefinitionGuiDetailsPanel extends FPanel {
	private FilterDefinition filterDefintion = null;
	
	public FilterDefinitionGuiDetailsPanel(FocObject focObject, int viewID){
		super("Filter", FPanel.FILL_BOTH);
		if(focObject != null){
			this.filterDefintion = (FilterDefinition)focObject;
			switch (viewID){
				case FilterDefinition.VIEW_ID_DEFAULT:
					newDetailsPanelDefaultView(viewID);
					break;
				case FilterDefinition.VIEW_ID_FOR_NEW_ITEM:
					newDetailsPanelForNewItem();
					break;
			}
		}
	}
	
	private void newDetailsPanelDefaultView(int viewID){
		FocList filterFieldList = filterDefintion.getFieldDefinitionList();
		FilterFieldDefinitionGuiBrowsePanel fieldDefintionGuiBrowsePanel = new FilterFieldDefinitionGuiBrowsePanel(filterFieldList, viewID);;
		add(fieldDefintionGuiBrowsePanel, 0, 0);
	}
	
	private void newDetailsPanelForNewItem(){
		Component comp = this.filterDefintion.getGuiComponent(FilterDefinitionDesc.FLD_TITLE);
		add("Title", comp, 0, 0);
		
		comp = this.filterDefintion.getGuiComponent(FilterDefinitionDesc.FLD_BASE_FOC_DESC);
		add("Base Table", comp, 1, 0);
		
		setMainPanelSising(FPanel.FILL_NONE);
		FValidationPanel validPanel = showValidationPanel(true);
		validPanel.setValidationType(FValidationPanel.VALIDATION_OK);
	}
	
	public void dispose(){
		super.dispose();
		this.filterDefintion = null;
	}

}
