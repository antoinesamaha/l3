package b01.fab.model.table;

import b01.fab.gui.browse.GuiBrowse;
import b01.fab.gui.browse.GuiBrowseColumn;
import b01.fab.gui.details.GuiDetails;
import b01.foc.Globals;
import b01.foc.desc.FocDesc;
import b01.foc.gui.FListPanel;
import b01.foc.gui.FPanel;
import b01.foc.gui.FValidationPanel;
import b01.foc.gui.table.FTableView;
import b01.foc.list.FocList;

@SuppressWarnings("serial")
public class UserDefinedObjectGuiBrowsePanel extends FListPanel {
	
	public UserDefinedObjectGuiBrowsePanel(FocList list, int viewId){
		super("Browse panel", FPanel.FILL_BOTH);
		if(list != null){
			try{
    		setFocList(list);
    	}catch(Exception e){
    		Globals.logException(e);
    	}
    	FocDesc userDifinedfocDesc = list.getFocDesc();
    	GuiBrowse browseViewDefinition = TableDefinition.getBrowseViewDefinitionForFocDescAndViewId(userDifinedfocDesc, viewId);
    	if(browseViewDefinition != null){
	    	list.setDirectImpactOnDatabase(false);
	    	FTableView tableView = getTableView();
	    	
	    	GuiDetails detailsViewDefinition = browseViewDefinition.getDetailsViewDefinition();
	    	if(detailsViewDefinition != null){
	    		tableView.setDetailPanelViewID(detailsViewDefinition.getReference().getInteger());
	    	}
	    	FocList browseColumnList = browseViewDefinition.getBrowseColumnList();
	    	for(int i = 0; i < browseColumnList.size(); i++){
	    		GuiBrowseColumn browseColumn = (GuiBrowseColumn)browseColumnList.getFocObject(i);
	    		if(browseColumn != null){
	    			FieldDefinition fieldDefinition = browseColumn.getFieldDefinition();
	    			tableView.addColumn(userDifinedfocDesc,fieldDefinition.getID() , 5, true);
	    		}
	    	}
	    	construct();
	    	
	    	if(browseViewDefinition.isShowValidationPanel()){
		    	FValidationPanel savePanel = showValidationPanel(true);
		      if (savePanel != null) {
		        savePanel.addSubject(list);
		      }
	    	}
	      setDirectlyEditable(true);
	      requestFocusOnCurrentItem();
	      showEditButton(browseViewDefinition.isShowEditButton() && browseViewDefinition.getDetailsViewDefinition() != null);
	      showDuplicateButton(false);
	      setFrameTitle(browseViewDefinition.getTableDefinition().getName());
	      setMainPanelSising(FPanel.FILL_BOTH);
			}
		}
	}
}
