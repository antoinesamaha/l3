// PROPERTIES
// INSTANCE
//    MAIN
//    PANEL
// DESCRIPTION
// LIST

/*
 * Created on 01-Feb-2005
 */
package b01.foc.stringList;

import b01.foc.desc.*;
import b01.foc.desc.field.*;
import b01.foc.gui.*;
import b01.foc.gui.table.*;
import b01.foc.list.*;
import b01.foc.property.*;

/**
 * @author 01Barmaja
 */
public class StringListItem extends FocObject {
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  // ---------------------------------
  //    MAIN
  // ---------------------------------

  public StringListItem(FocConstructor constr) {
    super(constr);

    FString name = new FString(this, FLD_NAME, "") ;
  }
  
  public void setString(String str){
    FString name = (FString) getFocProperty(FLD_NAME);
    name.setString(str);
  }

  public String getString(){
    FString name = (FString) getFocProperty(FLD_NAME);
    return name != null ? name.getString() : null;
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

  public static final int COL_NAME = 1;  
  
  public static FPanel newBrowsePanel(FocList list, int viewID) {
    FocDesc desc = getFocDesc();
    FListPanel selectionPanel = null;
    if (desc != null && list != null) {
      list.setDirectImpactOnDatabase(false);

      selectionPanel = new FListPanel(list);
      FTableView tableView = selectionPanel.getTableView();
      
      FTableColumn col = new FTableColumn(desc, FFieldPath.newFieldPath(FLD_NAME), COL_NAME, "Name", false);
      tableView.addColumn(col);
      
      selectionPanel.construct();
      selectionPanel.setDirectlyEditable(false);

      if(viewID != StringList.VIEW_NO_VALIDATION){
        FValidationPanel savePanel = selectionPanel.showValidationPanel(true);
        //savePanel.addSubject(list);
        //savePanel.setSelectionType(FValidationPanel.SELECTION_ENABLED);
        savePanel.setValidationType(FValidationPanel.VALIDATION_OK);
      }
      
      selectionPanel.requestFocusOnCurrentItem();
      selectionPanel.showEditButton(false);
      selectionPanel.showModificationButtons(false);
      
      //selectionPanel.getTable().setTableMinMax(0, 300, 500, 300);
      //selectionPanel.getTable().setTableHeightByIndex(2);
    }
        
    return selectionPanel;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // DESCRIPTION
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocDesc focDesc = null;

  public static final int FLD_NAME = 1;

  public static FocDesc getFocDesc() {
    if (focDesc == null) {
      FField focFld = null;
      focDesc = new FocDesc(StringListItem.class, FocDesc.NOT_DB_RESIDENT, "STRING_ITM", false);

      focFld = focDesc.addReferenceField();

      focFld = new FCharField("NAME", "Name", FLD_NAME, true, FCharField.NAME_LEN);
      focDesc.addField(focFld);
      focFld.setLockValueAfterCreation(true);
    }
    return focDesc;
  }
}
