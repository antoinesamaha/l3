package b01.l3.connector.dbConnector.lisConnectorTables;

import b01.foc.Globals;
import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocDesc;
import b01.foc.gui.FListPanel;
import b01.foc.gui.FPanel;
import b01.foc.gui.table.FTableView;
import b01.foc.list.FocList;

@SuppressWarnings("serial")
public class LisTestGuiBrowsePanel extends FListPanel {

  public LisTestGuiBrowsePanel(FocList list, int viewID){
    super("LisConnectorTest", FPanel.FILL_BOTH);
    FocDesc desc = LisTestDesc.getInstance();
    
    if (desc != null) {
      if(list == null){
        list = LisTestDesc.getList(FocList.NONE);
      }
      list.reloadFromDB();
			try{
			  setFocList(list);
			}catch(Exception e){
			  Globals.logException(e);
			}
     
      FTableView tableView = getTableView();   
      FocConstructor constr = new FocConstructor(LisTestFilter.getFocDesc(), null);
      LisTestFilter filter = (LisTestFilter) constr.newItem();
      tableView.setFilter(filter);
     
      tableView.addColumn(desc, LisTestDesc.FLD_SAMPLE_ID, false);
      tableView.addColumn(desc, LisTestDesc.FLD_ANALYSER_CODE, false);
      tableView.addColumn(desc, LisTestDesc.FLD_ACTUAL_ANALYSER_CODE, false);
      tableView.addColumn(desc, LisTestDesc.FLD_TEST_CODE, false);
      tableView.addColumn(desc, LisTestDesc.FLD_ALARM, false);
      tableView.addColumn(desc, LisTestDesc.FLD_RESULT, false);
      tableView.addColumn(desc, LisTestDesc.FLD_RESULT_NOTES, false);
      tableView.addColumn(desc, LisTestDesc.FLD_UNIT, false);
      tableView.addColumn(desc, LisTestDesc.FLD_STATUS, false);
      tableView.addColumn(desc, LisTestDesc.FLD_MESSAGE, "Message", 30, false);
      
      construct();
      showEditButton(false);
      showDuplicateButton(false);
      showRemoveButton(false);
      showAddButton(false);
      showFilterButton(true);
      showValidationPanel(true);
      //setMainPanelSising(FPanel.FILL_BOTH);
       
      requestFocusOnCurrentItem(); 
    }
  }  
}
