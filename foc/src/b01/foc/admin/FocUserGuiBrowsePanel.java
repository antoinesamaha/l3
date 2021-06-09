package b01.foc.admin;

import b01.foc.Globals;
import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FFieldPath;
import b01.foc.gui.FGButton;
import b01.foc.gui.FListPanel;
import b01.foc.gui.FPanel;
import b01.foc.gui.FValidationPanel;
import b01.foc.gui.table.FTableColumn;
import b01.foc.gui.table.FTableView;
import b01.foc.list.FocList;

@SuppressWarnings("serial")
public class FocUserGuiBrowsePanel extends FListPanel{

  public static final int COL_NAME      = 1;  
  public static final int COL_PASSWORD  = 2;  
  public static final int COL_GROUP     = 3;
  public static final int COL_LANGUAGE  = 4;

  public FocUserGuiBrowsePanel(FocList list, int viewID){
    FocDesc desc = FocUser.getFocDesc();
    setFrameTitle("User");
    if (desc != null) {
      if(list == null){
        list = FocUser.getList();
      }
      desc.setFieldSelectionListNotLoaded();
      if (list != null) {
        try {
          setFocList(list);
        } catch (Exception e) {
            Globals.logException(e);
        }
        fillTableView(desc);
        construct();
        setDirectlyEditable(true);
  
        //ChangePassword Button
        //---------------------
        FPanel totalsPanel = getTotalsPanel();
        FGButton changePass = new FGButton("Set password");
        changePass.addActionListener(new SetPassListener(this));
        totalsPanel.add(changePass, 0, 0);
        
        FValidationPanel savePanel = showValidationPanel(true);
        if (savePanel != null) {
          list.setFatherSubject(null);
          savePanel.addSubject(list);
        }
  
        requestFocusOnCurrentItem();
        showEditButton(false);
      }
    }
  }
  
  public void fillTableView(FocDesc desc){
    FField currField = null;
    FTableColumn col = null;
    FTableView tableView = getTableView();

    currField = desc.getFieldByID(FocUserDesc.FLD_NAME);
    col = new FTableColumn(desc, FFieldPath.newFieldPath(currField.getID()), COL_NAME, "Name", currField.getSize(), true);
    tableView.addColumn(col);

    currField = desc.getFieldByID(FocUserDesc.FLD_GROUP);
    col = new FTableColumn(desc, FFieldPath.newFieldPath(currField.getID()), COL_GROUP, "Group", 20, true);
    tableView.addColumn(col);

    currField = desc.getFieldByID(FocUserDesc.FLD_LANGUAGE);
    if(currField != null){
      col = new FTableColumn(desc, FFieldPath.newFieldPath(currField.getID()), COL_LANGUAGE, "Language", 20, true);
      tableView.addColumn(col);
    }
  }
}
