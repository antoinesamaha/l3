/*
 * Created on 14 fvr. 2004
 */
package b01.foc.gui;

import java.awt.Color;
import java.awt.event.*;
import javax.swing.*;

import b01.foc.property.*;
import b01.foc.*;

/**
 * @author Standard
 */
public abstract class FGAbstractComboBox extends JComboBox implements FocusListener, ActionListener, FPropertyListener {

  protected FProperty property = null;
  protected abstract String getPropertyStringValue();
  protected abstract void setPropertyStringValue(String strValue);
  private FGComboAutoCompletion autoCompletion = null;
  boolean withAutoCompletion = false;
  
  public void dispose(){
    if(property != null){
      property.removeListener(this);
      property = null;
    }
    if(autoCompletion != null){
    	autoCompletion.dispose();
    	autoCompletion = null;
    }
  }
  
  private void updatePropertyValue() {
    String str = (String) getItemAt(getSelectedIndex());
    //String str = (String) getSelectedItem();
    setPropertyStringValue(str);
    //The combo box reactions in a table are not effective without this line
    //The combobox cell itself does not have problems, it is the other columns
    //That do not refresh otherwise.
    Globals.getDisplayManager().refresh();
  }
  
  public void makeWithAutoCompletion(){
  	if(!withAutoCompletion){
	  	this.autoCompletion = FGComboAutoCompletion.makeWithAutoCompletion(this);
	  	this.withAutoCompletion = true;
  	}
  }

  public void setEnabled(boolean b) {  
    super.setEnabled(b);
    StaticComponent.setEnabled(this, b);
  }
  
  // FocusListener
  // -------------
  public void focusGained(FocusEvent e) {
  }

  public void focusLost(FocusEvent e) {
    updatePropertyValue();
  }

  // -------------

  // ActionListener
  // --------------
  public void actionPerformed(ActionEvent e) {
    //Globals.logString("event id ="+e.getID()+"command " + e.getActionCommand());
    updatePropertyValue();
  }

  // --------------

  /*
  public void setSelectedItem(Object obj){
    boolean backupEditable = isEditable();
    setEditable(true);
    super.setSelectedItem(getPropertyStringValue());
    setEditable(backupEditable);
  }
  */
  
  public void setSelectedItemWithColor(){
    setSelectedItem(getPropertyStringValue());
    if(getProperty() != null && getProperty().getBackground() != null){
    	setBackground(getProperty().getBackground());
    }else{
    	Color c = (Color)UIManager.get("ComboBox.background");
    	setBackground(c);      	
    }
  }
  
  // PropertyListener
  // ----------------
  public void propertyModified(FProperty property) {
    if (property != null) {
    	setSelectedItemWithColor();
    }
  }

  // ----------------

  /**
   * @return Returns the property.
   */
  public FProperty getProperty() {
    return property;
  }

  public void setProperty(FProperty prop) {
    if(property != prop){
      if(property != null){
        property.removeListener(this);
      }
      property = prop;
      //refreshList();
    	setSelectedItemWithColor();
      if(property != null){
        property.addListener(this);
      }
    }
  }
}
