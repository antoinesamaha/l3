package b01.foc.unitobject.unit;

import b01.foc.desc.FocDesc;
import b01.foc.gui.FListPanel;
import b01.foc.gui.FPanel;
import b01.foc.gui.table.FTableView;
import b01.foc.list.FocList;

@SuppressWarnings("serial")
public class UnitGuiBrowsePanel extends FPanel {

	public UnitGuiBrowsePanel(FocList list, int viewID){
		super("Unit", FPanel.FILL_VERTICAL);
		FocDesc desc = UnitDesc.getInstance();

    FListPanel selectionPanel = null;
    if (desc != null) {
    	if(list == null){
    		list = UnitDesc.getList(FocList.LOAD_IF_NEEDED);
    	}else{
    		list.loadIfNotLoadedFromDB();
    	}
      selectionPanel = new FListPanel(list);
      FTableView tableView = selectionPanel.getTableView();   
      
      /*FocConstructor constr = new FocConstructor(CompanyFilter.getFocDesc(), null);
      PersonFilter filter = (PersonFilter) constr.newItem();
      tableView.setFilter(filter);*/
      
      tableView.addColumn(desc, UnitDesc.FLD_NAME, true);
      tableView.addColumn(desc, UnitDesc.FLD_SYMBOL, true);
      tableView.addColumn(desc, UnitDesc.FLD_FACTOR, true);
      
      selectionPanel.construct();
      
      selectionPanel.showEditButton(false);
      selectionPanel.showDuplicateButton(false);
      //selectionPanel.showFilterButton(true);
      //filter.setActive(true);
      selectionPanel.requestFocusOnCurrentItem();      
      //tableView.getTable().setSelectedObject(list.getFocObject(0));
    }

    add(selectionPanel,0,0);
    
    //FGCurrentItemPanel currentItemPanel = new FGCurrentItemPanel(selectionPanel, 0);
    //add(currentItemPanel,0,1);
       
	}  
}
