package b01.fab.model.filter;

import b01.foc.Globals;
import b01.foc.desc.FocDesc;
import b01.foc.gui.FListPanel;
import b01.foc.gui.FPanel;
import b01.foc.gui.table.FTableView;
import b01.foc.list.FocList;

@SuppressWarnings("serial")
public class FilterFieldDefinitionGuiBrowsePanel extends FListPanel {
	
	public FilterFieldDefinitionGuiBrowsePanel(FocList focList, int viewID){
		super("Filter field defintion", FPanel.FILL_BOTH);
		FocDesc desc = FilterFieldDefinitionDesc.getInstance();

    if (desc != null) {
      if (focList != null) {
      	try{
      		setFocList(focList);
      	}catch(Exception e){
      		Globals.logException(e);
      	}
        FTableView tableView = getTableView();       
 
        tableView.addColumn(desc, FilterFieldDefinitionDesc.FLD_CONDITION_PROPERTY_PATH, false);
        construct();
        
        requestFocusOnCurrentItem();
        showEditButton(true);
        showDuplicateButton(false);
      }
    }
	}
}
