/*
 * Created on 14 fvr. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package b01.foc.gui;

import b01.foc.property.*;
import b01.foc.desc.*;
import b01.foc.desc.field.*;
import b01.foc.*;

/**
 * @author Standard
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class FGObjectField extends FGTextField {

  private int displayFieldID = -1;

  public FGObjectField(FProperty objProp, int displayFieldID) throws Exception {
    super();
    if (objProp != null) {
      FObjectField focField = (FObjectField) objProp.getFocField();
      FocDesc focDesc = focField != null ? focField.getFocDesc() : null;
      this.displayFieldID = displayFieldID;
      FField displayField = focDesc.getFieldByID(displayFieldID);
      propertyModified(objProp);

      int size = displayField.getSize();
      setColumns(size);
      setColumnsLimit(size);

      objectProperty = objProp;
      this.addFocusListener(this);
      this.addActionListener(this);
      objectProperty.addListener(this);
      this.setEditable(false);
    }
  }

  public void popupSelectionPanel() {
    if (objectProperty != null) {
      FObject fObj = (FObject) objectProperty;
      fObj.popupSelectionPanel();
    }
  }

  private void updateObjectPropertyValue() {
    try {
      if (objectProperty != null) {
        objectProperty.setString(getText());
        Globals.logString(getText());
      }
    } catch (Exception exception) {
      Globals.logException(exception);
    }
  }

  // PropertyListener
  // ----------------
  public void propertyModified(FProperty property) {
    if (property != null) {
      FocObject focObj = (FocObject) property.getObject();
      FocDesc focDesc = focObj.getThisFocDesc();
      FProperty dispProp = focObj.getFocProperty(displayFieldID);
      if (dispProp != null) {
        setText(dispProp.getString());
      }
      // objectProperty = property;
    }
  }

  // ----------------

  /**
   * @return
   */
  public int getDisplayFieldID() {
    return displayFieldID;
  }

  /**
   * @param i
   */
  public void setDisplayFieldID(int i) {
    displayFieldID = i;
  }

}
