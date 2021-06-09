package b01.foc.gui.table;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import b01.foc.formula.PropertyFormula;
import b01.foc.formula.PropertyFormulaContext;
import b01.foc.property.FProperty;

@SuppressWarnings("serial")
public class PasteAction extends AbstractAction {
  protected FTable table = null;
  
  public PasteAction( FTable table ){
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
    
    FProperty property = tableModel.getFProperty(row, col);
    if( property != null && shouldPaste(property) ){
      CopyPasteContent copyPasteContent = table.getCopyPasteContent();
      if ( copyPasteContent.getFormulaExpression() != null ){
        String expression = copyPasteContent.getFormulaExpression();
        property.changeFormula(expression);
      }else{
      	//BAntoineS - To allow the past of a value upon a with formula cell
      	if(property.isWithFormula()){
      		PropertyFormulaContext context = property.getPropertyFormulaContext();
      		PropertyFormula propFormula = context.getPropertyFormula();
      		propFormula.setExpression("");
      	}
      	//EAntoineS
        if(!property.isValueLocked()){ //TEMP
          tableModel.setValueAt(table.getCopyPasteContent().getTableDisplayObject(), row, col);  
        }
      }  
    }
  }
  
  public boolean shouldPaste(FProperty property){
    return true;
  }
  
}
