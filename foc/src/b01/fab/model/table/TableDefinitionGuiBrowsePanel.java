package b01.fab.model.table;

import b01.foc.Globals;
import b01.foc.desc.FocDesc;
import b01.foc.gui.FListPanel;
import b01.foc.gui.FPanel;
import b01.foc.gui.table.FTableView;
import b01.foc.list.FocList;

@SuppressWarnings("serial")
public class TableDefinitionGuiBrowsePanel extends FListPanel {
	public static final String VIEW_KEY_TABLE_DEFINITION = "TABLE_DEFINITION";
	
	public TableDefinitionGuiBrowsePanel(FocList list, int viewID){

		super("Table definition", FPanel.FILL_VERTICAL);
		FocDesc desc = TableDefinitionDesc.getInstance();

    if (desc != null) {
      if(list == null){
      	list = TableDefinitionDesc.getList(FocList.LOAD_IF_NEEDED);
      }else{
      	list.loadIfNotLoadedFromDB();
      }
      if (list != null) {
      	//
      	/*FocObject f = list.getFocObject(0);
      	f.getFocProperty(TableDefinitionDesc.FLD_BROWSE_VIEW_LIST);
      	f.getFocProperty(TableDefinitionDesc.FLD_FIELD_DEFINITION_LIST);*/
      	//
      	try{
      		setFocList(list);
      	}catch(Exception e){
      		Globals.logException(e);
      	}
        FTableView tableView = getTableView();       
        tableView.setDetailPanelViewID(0);
        
        tableView.addLineNumberColumn();
        tableView.addColumn(desc, TableDefinitionDesc.FLD_NAME, 20, true);
        tableView.addColumn(desc, TableDefinitionDesc.FLD_DB_RESIDENT, 10,true);
        tableView.addColumn(desc, TableDefinitionDesc.FLD_WITH_REF, 10,true);
        tableView.addColumn(desc, TableDefinitionDesc.FLD_KEY_UNIQUE, 10,true);
        tableView.addColumn(desc, TableDefinitionDesc.FLD_SINGLE_INSTANCE, 10,true);
        construct();
        
        requestFocusOnCurrentItem();
        showEditButton(true);
        showDuplicateButton(false);
        showColomnSelectorButton(true, VIEW_KEY_TABLE_DEFINITION);
      }
    }
	}
}
