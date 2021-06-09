//IMPLEMENTED

/*
 * Created on Sep 9, 2005
 */
package b01.foc.list.filter;

import java.awt.Component;
import java.awt.GridBagConstraints;

import b01.foc.desc.*;
import b01.foc.desc.field.FCharField;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FFieldArrayPlug;
import b01.foc.desc.field.FFieldPath;
import b01.foc.desc.field.FMultipleChoiceField;
import b01.foc.gui.FPanel;
import b01.foc.property.FMultipleChoice;
import b01.foc.property.FProperty;
import b01.foc.property.FPropertyArray;
import b01.foc.property.FString;

/**
 * @author 01Barmaja
 */
public class ArrayStringCondition extends StringCondition{

  public ArrayStringCondition(FFieldPath stringFieldPath, String fieldPrefix){
    super(stringFieldPath, fieldPrefix);
  }
  
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // IMPLEMENTED
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public boolean includeObject(FocListFilter filter, FocObject object){
    boolean include = true;
    int operation = getOperation(filter);
    if(operation != StringCondition.OPERATION_NONE){
      String condText = getText(filter);
      
      FPropertyArray propArray = (FPropertyArray) getFieldPath().getPropertyFromObject(object);
      String text = propArray != null ? propArray.getString() : "";
      
      switch(operation){
        case StringCondition.OPERATION_EQUALS:
          include = text.toUpperCase().compareTo(condText.toUpperCase()) == 0;
          break;
        case StringCondition.OPERATION_CONTAINS:
          include = text.toUpperCase().contains(condText.toUpperCase());
          break;
        case StringCondition.OPERATION_STARTS_WITH:
          include = text.toUpperCase().startsWith(condText.toUpperCase());
          break;
        case StringCondition.OPERATION_EMPTY:
          include = text.trim().compareTo("") == 0;
          break;
      }
    }
    return include;
  }  
}
