package b01.foc;

import b01.foc.desc.FocDesc;
import b01.foc.gui.FListPanel;
import b01.foc.gui.FPanel;
import b01.foc.gui.table.FTableView;
import b01.foc.list.FocList;

@SuppressWarnings("serial")
public class ConfigInfoObjectGuiBrowsePanel extends FPanel{

  public ConfigInfoObjectGuiBrowsePanel(FocList list, int viewID){
    super("Config Info", FPanel.FILL_NONE);
    FocDesc desc = ConfigInfoObjectDesc.getInstance();

    FListPanel selectionPanel = null;
    if (desc != null) {
      if(list == null){
        list = ConfigInfoObjectDesc.getList(FocList.LOAD_IF_NEEDED);
      }else{
        list.loadIfNotLoadedFromDB();
      }
      selectionPanel = new FListPanel(list);
      FTableView tableView = selectionPanel.getTableView();   
      
      tableView.addColumn(desc, ConfigInfoObjectDesc.FLD_PROPERTY, false);
      tableView.addColumn(desc, ConfigInfoObjectDesc.FLD_VALUE, true);
      
      selectionPanel.construct();
      
      selectionPanel.showAddButton(false);
      selectionPanel.showRemoveButton(false);
      selectionPanel.showEditButton(false);
      selectionPanel.showDuplicateButton(false);
      selectionPanel.requestFocusOnCurrentItem();      
    }

    add(selectionPanel,0,0);
       
  }  
}
