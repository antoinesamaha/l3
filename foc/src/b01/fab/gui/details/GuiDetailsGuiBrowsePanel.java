package b01.fab.gui.details;

import b01.fab.FocApplicationBuilder;
import b01.foc.Globals;
import b01.foc.desc.FocDesc;
import b01.foc.gui.FGCurrentItemPanel;
import b01.foc.gui.FListPanel;
import b01.foc.gui.FPanel;
import b01.foc.gui.table.FTableView;
import b01.foc.list.FocList;

@SuppressWarnings("serial")
public class GuiDetailsGuiBrowsePanel extends FListPanel {
	
	public GuiDetailsGuiBrowsePanel(FocList list, int viewId){
		super("Details views", FPanel.FILL_BOTH);
		boolean allowEdit = viewId != FocApplicationBuilder.VIEW_NO_EDIT;
		FocDesc desc = GuiDetailsDesc.getInstance();

    if (desc != null) {
      if (list != null) {
      	try{
      		setFocList(list);
      	}catch(Exception e){
      		Globals.logException(e);
      	}
        FTableView tableView = getTableView();       
        
        tableView.addLineNumberColumn();
        //tableView.addColumn(desc, GuiDetailsDesc.FLD_VIEW_ID, 20, allowEdit);
        tableView.addColumn(desc, GuiDetailsDesc.FLD_DESCRIPTION, 20, allowEdit);
        tableView.addColumn(desc, GuiDetailsDesc.FLD_ADD_SUBJECT_TO_VALIDATION_PANEL, 20, allowEdit);
        tableView.addColumn(desc, GuiDetailsDesc.FLD_SHOW_VALIDATION_PANEL, 20, allowEdit);
        tableView.addColumn(desc, GuiDetailsDesc.FLD_VIEW_MODE, 20, allowEdit);
        tableView.addColumn(desc, GuiDetailsDesc.FLD_IS_DEFAULT_VIEW, 20, allowEdit);
        tableView.addColumn(desc, GuiDetailsDesc.FLD_IS_SUMMARY_VIEW, 20, allowEdit);
        
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
