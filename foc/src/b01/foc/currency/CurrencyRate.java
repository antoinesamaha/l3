// PROPERTIES
// INSTANCE
//    MAIN
//    PANEL
// DESCRIPTION
// LIST

/*
 * Created on 01-Feb-2005
 */
package b01.foc.currency;

import b01.foc.*;
import b01.foc.desc.*;
import b01.foc.desc.field.*;
import b01.foc.gui.*;
import b01.foc.gui.table.*;
import b01.foc.list.*;
import b01.foc.property.*;

/**
 * @author 01Barmaja
 */
public class CurrencyRate extends FocObject {
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  // ---------------------------------
  //    MAIN
  // ---------------------------------

  public CurrencyRate(FocConstructor constr) {
    super(constr);
    
    FocConstructor curConstr = new FocConstructor(Currency.getFocDesc(), constr, null);
    FObject currency = new FObject(this, FLD_CURRENCY, curConstr.newItem()) ;
    FDouble rate = new FDouble(this, FLD_RATE, 0) ;
  }

  public void setCurrency(Currency currency){
    FObject pCurr = (FObject) getFocProperty(FLD_CURRENCY);
    pCurr.setObject(currency);
  }
  
  public FDouble getRateProperty(){
    return (FDouble) getFocProperty(FLD_RATE);
  }
  
  // ---------------------------------
  //    PANEL
  // ---------------------------------

  public FPanel newDetailsPanel(int viewID) {
    return null;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  private static int COL_CURRENCY = 1;
  private static int COL_RATE = 2;
  
  public static FPanel newBrowsePanel(FocList list, int viewID) {
    FocDesc desc = getFocDesc();
    FListPanel selectionPanel = null;
    if (desc != null && list != null) {
      list.setDirectImpactOnDatabase(false);

      selectionPanel = new FListPanel(list);
      FTableView tableView = selectionPanel.getTableView();
      
      FField currField = null;
      FTableColumn col = null;

      col = new FTableColumn(desc, FFieldPath.newFieldPath(FLD_CURRENCY), COL_CURRENCY, MultiLanguage.getString(FocLangKeys.CURR_CURRENCY), true);
      tableView.addColumn(col);

      col = new FTableColumn(desc, FFieldPath.newFieldPath(FLD_RATE), COL_RATE, MultiLanguage.getString(FocLangKeys.CURR_RATE), true);
      tableView.addColumn(col);
      
      selectionPanel.construct();

      selectionPanel.setDirectlyEditable(true);
      //selectionPanel.getTable().setTableMinMax(50, 500, 150, 150);
      selectionPanel.getTable().setTableHeightByIndex(1);

      selectionPanel.requestFocusOnCurrentItem();
      selectionPanel.showEditButton(false);
    }
    
    return selectionPanel;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // DESCRIPTION
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocDesc focDesc = null;

  public static final int FLD_CURRENCY = 1;
  public static final int FLD_RATE = 2;
  
  public static FocDesc getFocDesc() {
    if (focDesc == null) {
      FField focFld = null;
      focDesc = new FocDesc(CurrencyRate.class, FocDesc.DB_RESIDENT, "CURR_RATES", false);

      focFld = focDesc.addReferenceField();

      FObjectField focObjFld = new FObjectField("CURR", "Currency", FLD_CURRENCY, true, Currency.getFocDesc(), "CUR_");
      focObjFld.setSelectionList(Currency.getList(true));
      focDesc.addField(focObjFld);

      focFld = new FNumField("RATE", "Rate", FLD_RATE, false, 7, 2);
      focDesc.addField(focFld);      
    }
    return focDesc;
  }
}
