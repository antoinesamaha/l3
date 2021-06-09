package b01.fab.gui.browse;

import b01.fab.FocApplicationBuilder;
import b01.foc.Globals;
import b01.foc.desc.FocDesc;
import b01.foc.gui.FGCurrentItemPanel;
import b01.foc.gui.FListPanel;
import b01.foc.gui.FPanel;
import b01.foc.gui.table.FTableView;
import b01.foc.list.FocList;

@SuppressWarnings("serial")
public class GuiBrowseGuiBrowsePanel extends FListPanel{
	
	public GuiBrowseGuiBrowsePanel(FocList list, int viewId){
		super("Browse views", FPanel.FILL_BOTH);
		boolean allowEdit = viewId != FocApplicationBuilder.VIEW_NO_EDIT;
		FocDesc desc = GuiBrowseDesc.getInstance();

    if (desc != null) {
      if (list != null) {
      	try{
      		setFocList(list);
      	}catch(Exception e){
      		Globals.logException(e);
      	}
        FTableView tableView = getTableView();       
        
        tableView.addLineNumberColumn();
        //tableView.addColumn(desc, GuiBrowseDesc.FLD_VIEW_ID, 10, allowEdit);
        tableView.addColumn(desc, GuiBrowseDesc.FLD_DESCRIPTION, 20, allowEdit);
        tableView.addColumn(desc, GuiBrowseDesc.FLD_USER_DETAILS_VIEW_DEFINITION, 20, allowEdit);
        tableView.addColumn(desc, GuiBrowseDesc.FLD_SHOW_EDIT_BUTTON, 10, allowEdit);
        tableView.addColumn(desc, GuiBrowseDesc.FLD_SHOW_VALIDATION_PANEL, 10, allowEdit);
        
        construct();
        
        FGCurrentItemPanel currentItemPanel = new FGCurrentItemPanel(this, viewId);
        add(currentItemPanel, 1, 3);
        
        requestFocusOnCurrentItem();
        showEditButton(false);
        showDuplicateButton(false);
        showAddButton(allowEdit);
        showRemoveButton(allowEdit);
      }
    }
	}
}
