/*
 * Created on Oct 14, 2004
 */
package b01.foc.desc.field;

import java.awt.Component;
import javax.swing.*;

import b01.foc.Globals;
import b01.foc.property.*;
import b01.foc.desc.FocObject;
import b01.foc.gui.*;
import b01.foc.gui.table.cellControler.*;

/**
 * @author 01Barmaja
 */
public class FPasswordField extends FCharField {

  public FPasswordField(String name, String title, int id, boolean key, int size) {
    super(name, title, id, key, size);
  }

  public FProperty newProperty(FocObject masterObj, Object defaultValue){
    return new FPassword(masterObj, getID(), (String) defaultValue);
  }
  
  public Component getGuiComponent(FProperty prop){
    FGPasswordField textField = new FGPasswordField();
    textField.setColumns(Double.valueOf(this.getSize()* Globals.CHAR_SIZE_FACTOR).intValue());
    textField.setColumnsLimit(this.getSize());
    textField.setCapital(isCapital());
    if(prop != null) textField.setProperty(prop);
    return textField;
  }
  
  public AbstractCellControler getTableCellEditor(FProperty prop){
    JTextField guiComp = (JTextField) getGuiComponent(prop);
    return new TextCellControler(guiComp);
  }
}
