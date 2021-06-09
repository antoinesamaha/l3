package b01.foc.gui.table;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import b01.foc.formula.Formula;
import b01.foc.property.FProperty;

@SuppressWarnings("serial")
public class CopyAction extends AbstractAction {

  protected FTable table = null;
  
  public CopyAction( FTable table ){
    this.table = table;
  }
  
  public void dispose(){
    table = null;
  }
  
  public void actionPerformed(ActionEvent e) {
    int row = table.getSelectedRow();
    int col = table.getSelectedColumn();
    FAbstractTableModel tableModel = table.getTableModel();
    FTableView tableView = tableModel.getTableView();
    col = tableView.getVisibleColumnIndex(col);
    
    table.getCopyPasteContent().setTableDisplayObject(tableModel.getValueAt(row, col));
    
    FProperty prop = tableModel.getFProperty(row, col);
    if( prop != null && prop.isWithFormula() ){
      Formula formula = prop.getFormula();
      String expression = formula.getString();
      table.getCopyPasteContent().setFormulaExpression(expression);
    }
  }
  
}
