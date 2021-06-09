package b01.foc.formula;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import b01.foc.desc.field.FField;
import b01.foc.gui.FAbstractListPanel;
import b01.foc.gui.FGTextField;
import b01.foc.gui.FPanel;
import b01.foc.gui.table.FAbstractTableModel;
import b01.foc.gui.table.FTableView;
import b01.foc.property.FProperty;
import b01.foc.property.FString;

@SuppressWarnings("serial")
public class PropertyFormulaDisplayPropertyPanel extends FPanel {
  
  private FString fString = null;
  private FProperty selectedProperty = null;
  
  public PropertyFormulaDisplayPropertyPanel(final FAbstractListPanel selectionPanel) {
    super("Formula Expression", FPanel.FILL_NONE);
    
    fString = new FString(null, FField.NO_FIELD_ID, "");
    FGTextField textField = new FGTextField();
    textField.addFocusListener(new FocusListener(){

      public void focusGained(FocusEvent e) {
      }

      public void focusLost(FocusEvent e) {
        //Globals.getDisplayManager().popupMessage("LOST");
        if( selectedProperty != null ){
          
          if( !selectedProperty.isWithFormula() && !fString.getString().equals("")){
            selectedProperty.newFormula();
          }
          
          PropertyFormulaContext propertyFormulaContext = selectedProperty.getPropertyFormulaContext();
          if (propertyFormulaContext != null) {
            PropertyFormula propertyFormula = propertyFormulaContext.getPropertyFormula();
            if (propertyFormula != null) {
              propertyFormula.setExpression(fString.getString());
            }
          }
          
        }
      }
      
    });
    
    textField.setColumns(30);
    textField.setProperty(fString);
    add("Expression", textField, 0, 0);
    
    
    selectionPanel.getTable().getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      
      public void valueChanged(ListSelectionEvent e) {
        selectionChanged(e, selectionPanel);
      }
      
    });
    
    selectionPanel.getTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        selectionChanged(e, selectionPanel);
      }
    });
    
  }
  
  public void dispose(){
    super.dispose();
    if( fString != null ){
      fString.dispose();
      fString = null;
    }
    selectedProperty = null;
  }
  
  private void selectionChanged(ListSelectionEvent e, FAbstractListPanel selectionPanel) {
    if (e != null && !e.getValueIsAdjusting()) {
      //Globals.getDisplayManager().popupMessage("SELECTION");
      this.selectedProperty = null;
      
      int row = selectionPanel.getTable().getSelectedRow();
      int col = selectionPanel.getTable().getSelectedColumn();
      
      FAbstractTableModel tableModel = selectionPanel.getTable().getTableModel();
      FTableView tableView = tableModel.getTableView();
      col = tableView.getVisibleColumnIndex(col);
      
      FProperty selectedProperty = tableModel.getFProperty(row, col);
      if (selectedProperty != null) {
        this.selectedProperty = selectedProperty;
        
        PropertyFormulaContext propertyFormulaContext = selectedProperty.getPropertyFormulaContext();
        if (propertyFormulaContext != null) {
          PropertyFormula propertyFormula = propertyFormulaContext.getPropertyFormula();
          if (propertyFormula != null) {
            FProperty pfProperty = propertyFormula.getFocProperty(PropertyFormulaDesc.FLD_EXPRESSION);
            fString.setObject(pfProperty.getObject());
          }
        }else{
          fString.setString("");
          //fString.setObject(String.valueOf(prop.getObject()));
        }
      }
    }
  }
}
