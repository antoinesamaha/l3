package b01.fab.model.table;

import b01.fab.FocApplicationBuilder;
import b01.fab.gui.browse.GuiBrowseGuiBrowsePanel;
import b01.fab.gui.details.GuiDetailsGuiBrowsePanel;
import b01.foc.desc.FocObject;
import b01.foc.event.FValidationListener;
import b01.foc.gui.FGTabbedPane;
import b01.foc.gui.FPanel;
import b01.foc.gui.FValidationPanel;

@SuppressWarnings("serial")
public class TableDefinitionGuiDetailsPanel extends FPanel {
	private TableDefinition tableDefinition = null;
	
	public TableDefinitionGuiDetailsPanel(FocObject tableDefinition, int viewID){
		FGTabbedPane tabbedPan = new FGTabbedPane();
		this.tableDefinition = (TableDefinition)tableDefinition;
	
		FPanel fieldPanel = new FieldDefinitionGuiBrowsePanel(this.tableDefinition.getFieldDefinitionList(), viewID);
		tabbedPan.add(fieldPanel.getFrameTitle(), fieldPanel);
		
		FPanel browseViewDefintionBorwsePanel = new GuiBrowseGuiBrowsePanel(this.tableDefinition.getBrowseViewDefinitionList(), viewID);
		tabbedPan.add(browseViewDefintionBorwsePanel.getFrameTitle(), browseViewDefintionBorwsePanel);
		
		FPanel detaildViewDefinitionBrowsePanel = new GuiDetailsGuiBrowsePanel(this.tableDefinition.getDetailsViewDefinitionList(), viewID);
		tabbedPan.add(detaildViewDefinitionBrowsePanel.getFrameTitle(), detaildViewDefinitionBrowsePanel);
		
		add(tabbedPan, 0, 0);
		
		setFrameTitle(this.tableDefinition.getName());
		setMainPanelSising(FPanel.FILL_BOTH);
		
		if(viewID != FocApplicationBuilder.VIEW_NO_EDIT){
			FValidationPanel validPanel = showValidationPanel(true);
			if(validPanel != null){
				validPanel.addSubject(this.tableDefinition);
				validPanel.setValidationType(FValidationPanel.VALIDATION_SAVE_CANCEL);
				validPanel.setValidationListener(new FValidationListener(){
	
					public void postCancelation(FValidationPanel panel) {
					}
	
					public void postValidation(FValidationPanel panel) {
						TableDefinitionGuiDetailsPanel.this.tableDefinition.adjustIFocDescDeclaration();
					}
	
					public boolean proceedCancelation(FValidationPanel panel) {
						return true;
					}
	
					public boolean proceedValidation(FValidationPanel panel) {
						return true;
					}
					
				});
			}
		}
	}
}
