package b01.l3.connector.dbConnector.lisConnectorTables;

import b01.foc.Globals;
import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocDesc;
import b01.foc.gui.FListPanel;
import b01.foc.gui.FPanel;
import b01.foc.gui.table.FTableView;
import b01.foc.list.FocList;

@SuppressWarnings("serial")
public class LisSampleGuiBrowsePanel extends FListPanel {
  
  public LisSampleGuiBrowsePanel(FocList list, int viewID){
    super("LIS Samples", FPanel.FILL_BOTH);
    FocDesc desc = LisSampleDesc.getInstance();
    
    if (desc != null) {
      if(list == null){
        list = LisSampleDesc.getList(FocList.NONE);
      }
      list.reloadFromDB();
      try{
        setFocList(list);
      }catch(Exception e){
        Globals.logException(e);
      }
     
      FTableView tableView = getTableView();
      FocConstructor constr = new FocConstructor(LisSampleFilter.getFocDesc(), null);
      LisSampleFilter filter = (LisSampleFilter) constr.newItem();
      tableView.setFilter(filter);
     
      tableView.addColumn(desc, LisSampleDesc.FLD_SAMPLE_ID, false);
      tableView.addColumn(desc, LisSampleDesc.FLD_PATIENT_FIRST_NAME, false);
      tableView.addColumn(desc, LisSampleDesc.FLD_PATIENT_LAST_NAME, false);
      tableView.addColumn(desc, LisSampleDesc.FLD_PATIENT_MIDDLE_INITIAL, false);
      tableView.addColumn(desc, LisSampleDesc.FLD_LIQUID_TYPE, false);
      tableView.addColumn(desc, LisSampleDesc.FLD_PATIENT_AGE, false);
      tableView.addColumn(desc, LisSampleDesc.FLD_PATIENT_SEX, false);
      tableView.addColumn(desc, LisSampleDesc.FLD_DATE_OF_BIRTH, false);
      tableView.addColumn(desc, LisSampleDesc.FLD_COLLECTION_DATE, false);
      tableView.addColumn(desc, LisSampleDesc.FLD_CURRENT_DATE_TIME, false);
      
      construct();
      showEditButton(false);
      showDuplicateButton(false);
      showRemoveButton(false);
      showAddButton(false);
      showValidationPanel(true);
      showFilterButton(true);
    }
  }  
}
