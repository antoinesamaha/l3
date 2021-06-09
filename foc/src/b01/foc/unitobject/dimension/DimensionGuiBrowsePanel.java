package b01.foc.unitobject.dimension;

import b01.foc.desc.FocDesc;
import b01.foc.gui.FGCurrentItemPanel;
import b01.foc.gui.FListPanel;
import b01.foc.gui.FPanel;
import b01.foc.gui.FValidationPanel;
import b01.foc.gui.table.FTableView;
import b01.foc.list.FocList;

@SuppressWarnings("serial")
public class DimensionGuiBrowsePanel extends FPanel {

	public DimensionGuiBrowsePanel(FocList list, int viewID){
		super("Dimension", FPanel.FILL_BOTH);
		FocDesc desc = DimensionDesc.getInstance();

    FListPanel selectionPanel = null;
    if (desc != null) {
    	if(list == null){
    		list = DimensionDesc.getList(FocList.LOAD_IF_NEEDED);
    	}else{
    		list.loadIfNotLoadedFromDB();
    	}
      selectionPanel = new FListPanel(list);
      FTableView tableView = selectionPanel.getTableView();   
      
      tableView.addColumn(desc, DimensionDesc.FLD_NAME, true);
      tableView.addColumn(desc, DimensionDesc.FLD_DIMENSION_TYPE, true);
      
      selectionPanel.construct();
      
      selectionPanel.showEditButton(false);
      selectionPanel.showDuplicateButton(false);
      selectionPanel.requestFocusOnCurrentItem();      
    }

    add(selectionPanel, 0, 0);
    
    FGCurrentItemPanel currentItemPanel = new FGCurrentItemPanel(selectionPanel, 0);
    add(currentItemPanel, 1, 0);
       
  	FValidationPanel savePanel = showValidationPanel(true);
    if (savePanel != null) {
      savePanel.addSubject(list);
    }
	}  
}