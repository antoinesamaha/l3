package b01.fab.gui.details;

import b01.fab.FocApplicationBuilder;
import b01.foc.Globals;
import b01.foc.desc.FocDesc;
import b01.foc.gui.FListPanel;
import b01.foc.gui.FPanel;
import b01.foc.gui.table.FTableView;
import b01.foc.list.FocList;

@SuppressWarnings("serial")
public class GuiDetailsComponentGuiBrowsePanel extends FListPanel{
	
	public GuiDetailsComponentGuiBrowsePanel(FocList list, int viewId){
		//super("Browse Column", FPanel.FILL_VERTICAL);
		super("Browse Column", FPanel.FILL_BOTH);
		FocDesc desc = GuiDetailsComponentDesc.getInstance();
		boolean allowEdit = viewId != FocApplicationBuilder.VIEW_NO_EDIT;

    if (desc != null) {
      if (list != null) {
      	try{
      		setFocList(list);
      	}catch(Exception e){
      		Globals.logException(e);
      	}
        FTableView tableView = getTableView();       
        
        tableView.addLineNumberColumn();
        tableView.addColumn(desc, GuiDetailsComponentDesc.FLD_FIELD_DEFINITION, 35, allowEdit);
        tableView.addColumn(desc, GuiDetailsComponentDesc.FLD_X, 20, allowEdit);
        tableView.addColumn(desc, GuiDetailsComponentDesc.FLD_Y, 20, allowEdit);
        //tableView.addColumn(desc, GuiDetailsComponentDesc.FLD_VIEW_ID, 20, allowEdit);
        tableView.addColumn(desc, GuiDetailsComponentDesc.FLD_COMPONENT_GUI_DETAILS, 20, allowEdit);
        tableView.addColumn(desc, GuiDetailsComponentDesc.FLD_COMPONENT_GUI_BROWSE, 20, allowEdit);
        construct();
        
        requestFocusOnCurrentItem();
        showEditButton(false);
        showDuplicateButton(false);
        showAddButton(allowEdit);
        showRemoveButton(allowEdit);
      }
    }
	}
}
