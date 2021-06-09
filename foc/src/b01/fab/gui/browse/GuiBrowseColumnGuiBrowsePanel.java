package b01.fab.gui.browse;
import b01.fab.FocApplicationBuilder;
import b01.foc.Globals;
import b01.foc.desc.FocDesc;
import b01.foc.gui.FListPanel;
import b01.foc.gui.FPanel;
import b01.foc.gui.table.FTableView;
import b01.foc.list.FocList;

@SuppressWarnings("serial")
public class GuiBrowseColumnGuiBrowsePanel extends FListPanel{
	
	public GuiBrowseColumnGuiBrowsePanel(FocList list, int ViewId){
		super("Browse Column", FPanel.FILL_VERTICAL);
		FocDesc desc = GuiBrowseColumnDesc.getInstance();
		boolean allowEdit = ViewId != FocApplicationBuilder.VIEW_NO_EDIT;

    if (desc != null) {
      if(list != null){
      	list.loadIfNotLoadedFromDB();
      }
      if (list != null) {
      	try{
      		setFocList(list);
      	}catch(Exception e){
      		Globals.logException(e);
      	}
        FTableView tableView = getTableView();       
        tableView.setDetailPanelViewID(0);
        
        tableView.addLineNumberColumn();
        //tableView.addColumn(desc, BrowseColumnDesc.FLD_BROWSE_VIEW, 20, false);
        tableView.addColumn(desc, GuiBrowseColumnDesc.FLD_FIELD_DEFINITION, 20, allowEdit);
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
