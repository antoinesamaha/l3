package b01.foc.calendar;

import b01.foc.desc.FocDesc;
import b01.foc.gui.FListPanel;
import b01.foc.gui.FPanel;
import b01.foc.gui.FValidationPanel;
import b01.foc.gui.table.FTableColumn;
import b01.foc.gui.table.FTableView;
import b01.foc.list.FocList;

@SuppressWarnings("serial")
public class FCalendarGuiBrowsePanel extends FPanel {
	
	public FCalendarGuiBrowsePanel(FocList list, int viewID){
		super("Calendar", FPanel.FILL_NONE);
		FocDesc desc = FCalendarDesc.getInstance();

    FListPanel selectionPanel = null;
    if (desc != null) {
    	if(list == null){
    		list = FCalendarDesc.getList(FocList.LOAD_IF_NEEDED);
    	}else{
    		list.loadIfNotLoadedFromDB();
    	}
      selectionPanel = new FListPanel(list);
      FTableView tableView = selectionPanel.getTableView();   
      
      selectionPanel.setDirectlyEditable(false);
      
      tableView.addColumn(desc, FCalendarDesc.FLD_NAME, false);
      FTableColumn col = tableView.addColumn(desc, FCalendarDesc.FLD_IS_DEFAULT, true);
      col.setSize(15);
      
      selectionPanel.construct();
      
      selectionPanel.showEditButton(true);
      selectionPanel.showDuplicateButton(false);
      selectionPanel.requestFocusOnCurrentItem();      
    }

    add(selectionPanel, 0, 0);
       
  	FValidationPanel savePanel = showValidationPanel(true);
    if (savePanel != null) {
      savePanel.addSubject(list);	
    }
	}  
}
