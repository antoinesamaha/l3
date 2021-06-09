package b01.foc.fUnit;

import java.awt.Container;

public class NotUsed {
  /*
  // #######################################
  // #######################################
  // TEXT FIELD
  // #######################################
  // #######################################
  
  public void textField_SetText(Container rootContainer, String table, String field, String value, int maxAttempts) {
    FGTextField textField = (FGTextField) getChildNamed(rootContainer, table, field, maxAttempts);
    if (textField != null) {
      textField = (FGTextField) preventAlterningIfDisabled(textField);
      
      focAssertTrue("Focused", textField.requestFocusInWindow());
      
      textField.setText(value);
      textField.postActionEvent();
    }
  }
  
  public void textField_SetText(String table, String field, String value, int maxAttempts) {
    textField_SetText(null, table, field, value, maxAttempts);
  }
  
  public void textField_SetText(Container rootContainer, String table, String field, String value) {
    textField_SetText(rootContainer, table, field, value, DEFAULT_NUMBER_OF_ATTEMPTS);
  }
  
  public void textField_SetText(String table, String field, String value) {
    textField_SetText(null, table, field, value, DEFAULT_NUMBER_OF_ATTEMPTS);
  }
  
  public void textField_SetText(int fieldId, String value) {
    focAssertNotNull("Default FocDesc not NULL ?", getDefaultFocDesc());
    textField_SetText(getDefaultFocDesc().getStorageName(), getDefaultFocDesc().getFieldByID(fieldId).getName(), value, DEFAULT_NUMBER_OF_ATTEMPTS);
  }

  // #######################################
  // #######################################
  // COMBO BOX
  // #######################################
  // #######################################
  
  public void comboBox_SetSelection(Container rootContainer, String table, String field, String value, int maxAttempts) {
    FGComboBox combo = (FGComboBox) getChildNamed(rootContainer, table, field, maxAttempts);
    // focAssertNotNull("ComboBox found ? ", combo);
    focAssertTrue("Focused", combo.requestFocusInWindow());
    combo.setSelectedItem(value);
  }
  
  public void comboBox_SetSelection(int fieldId, String value) {
    focAssertNotNull("Default FocDesc not NULL ?", getDefaultFocDesc());
    comboBox_SetSelection(null, getDefaultFocDesc().getStorageName(), getDefaultFocDesc().getFieldByID(fieldId).getName(), value, DEFAULT_NUMBER_OF_ATTEMPTS);
  }
  
  public void Click_TabbedPane(String paneTitle) {
    Component comp = getChildNamed(paneTitle, DEFAULT_NUMBER_OF_ATTEMPTS, true);
    
    ArrayList<Component> tabbedArray = new ArrayList<Component>();
    if (comp != null) {
      Component root = getDefaultRootContainer();
      
      Component parent = comp;
      while(parent != root && parent != null){
        tabbedArray.add(parent);
        parent = parent.getParent();
      }
      


//      if (tabbedArray.size() == 1) {
//        tp = tabbedArray.get(0);
//      } else {
//        for (int i = (tabbedArray.size() - 1); i > 0; i--) {
//          tp = tabbedArray.get(i);
//          focAssertTrue("Focused", tp.requestFocusInWindow());
//          tp.setSelectedComponent(tabbedArray.get(i - 1));
//          if ((i - 1) == 0) {
//            tp = tabbedArray.get(i - 1);
//          }
//        }
//      }
//      sleep(1);
//      focAssertTrue("Focused", tp.requestFocusInWindow());
//      tp.setSelectedComponent(comp);

    }
  }
  
  */
  
}
