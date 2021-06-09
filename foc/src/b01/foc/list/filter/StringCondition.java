//IMPLEMENTED

/*
 * Created on Sep 9, 2005
 */
package b01.foc.list.filter;

import java.awt.GridBagConstraints;

import b01.foc.desc.*;
import b01.foc.desc.field.FCharField;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FFieldPath;
import b01.foc.desc.field.FMultipleChoiceField;
import b01.foc.gui.FGComboBox;
import b01.foc.gui.FGTextField;
import b01.foc.gui.FPanel;
import b01.foc.property.FMultipleChoice;
import b01.foc.property.FProperty;
import b01.foc.property.FPropertyListener;
import b01.foc.property.FString;

/**
 * @author 01Barmaja
 */
public class StringCondition extends FilterCondition{
  static protected final int FLD_CONDITION_OPERATION = 1;
  static protected final int FLD_CONDITION_TEXT = 2;

  static public final int OPERATION_NONE = 0;
  static public final int OPERATION_CONTAINS = 1;
  static public final int OPERATION_STARTS_WITH = 2; 
  static public final int OPERATION_EQUALS = 3;
  static public final int OPERATION_EMPTY = 4;
  static public final int OPERATION_NOT_EMPTY = 5;
  
  public StringCondition(FFieldPath stringFieldPath, String fieldPrefix){
    super(stringFieldPath, fieldPrefix);
  }
  
  public int getOperation(FocListFilter filter){
    FProperty prop = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OPERATION);
    return prop.getInteger();
  }

  public String getText(FocListFilter filter){
    FProperty prop = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_TEXT);
    return prop.getString();
  }

  public void setToValue(FocListFilter filter, int operator, String value){
    /*FString valueProp = (FString) filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_TEXT);
    valueProp.setString(value);
    
    FProperty oppProp = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OPERATION);
    oppProp.setInteger(operator);*/
  	setToValue(filter, operator, value, false);
  }  
  
  public void forceToValue(FocListFilter filter, int operator, String value){
  	/*setToValue(filter, operator, value);
  	
    FString valueProp = (FString) filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_TEXT);
    valueProp.setValueLocked(true);*/
  	setToValue(filter, operator, value, true);
  }
  
  private void setToValue(FocListFilter filter, int operator, String value, boolean lockConditionAlso){
  	FString valueProp = (FString) filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_TEXT);
    valueProp.setString(value);
    if(lockConditionAlso){
    	valueProp.setValueLocked(true);
    }
    
    FProperty oppProp = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OPERATION);
    oppProp.setInteger(operator);
    if(lockConditionAlso){
    	valueProp.setValueLocked(true);
    }
  }
  
  public boolean isValueLocked(FocListFilter filter){
  	boolean locked = false;
  	FString valueProp = (FString) filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_TEXT);
  	if(valueProp != null){
  		locked = valueProp.isValueLocked();
  	}
  	return locked;
  }
  
  public void resetToDefaultValue(FocListFilter filter){
  	setToValue(filter, OPERATION_CONTAINS, "");
  }
  
  
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // IMPLEMENTED
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public void fillProperties(FocObject focFatherObject){
    new FMultipleChoice(focFatherObject, getFirstFieldID() + FLD_CONDITION_OPERATION, OPERATION_CONTAINS);
    new FString(focFatherObject, getFirstFieldID() + FLD_CONDITION_TEXT, "");
  }
  
  public boolean includeObject(FocListFilter filter, FocObject object){
    boolean include = true;
    int operation = getOperation(filter);
    if(operation != OPERATION_NONE){
      String condText = getText(filter);
      
      FString textProp = (FString) getFieldPath().getPropertyFromObject(object);
      String text = textProp != null ? textProp.getString() : "";
      
      switch(operation){
        case OPERATION_EQUALS:
          include = text.toUpperCase().compareTo(condText.toUpperCase()) == 0;
          break;
        case OPERATION_CONTAINS:
          include = text.toUpperCase().contains(condText.toUpperCase());
          break;
        case OPERATION_STARTS_WITH:
          include = text.toUpperCase().startsWith(condText.toUpperCase());
          break;
        case OPERATION_EMPTY:
          include = text.trim().compareTo("") == 0;
          break;
        case OPERATION_NOT_EMPTY:
          include = text.trim().compareTo("") != 0;
          break;
      }
    }
    return include;
  }
  
  public StringBuffer buildSQLWhere(FocListFilter filter, String fieldName) {
    //BElias
    //b01.foc.Globals.logString("Condition sql build not implemented yet");
    StringBuffer buffer = null;
    String text = getText(filter).trim();
    int operation = getOperation(filter);
    
    boolean writeCondition = false;
    if(operation == OPERATION_EMPTY || operation == OPERATION_NOT_EMPTY){
    	writeCondition = true;
    }else if(operation != OPERATION_NONE){
    	writeCondition = (text.compareTo("") != 0);
    }
    
    if (writeCondition){
      buffer = new StringBuffer();
      switch (operation){
        case OPERATION_EQUALS:
          buffer.append(fieldName + " = \"" + text +"\"");
        break;
        case OPERATION_STARTS_WITH:
          buffer.append("LEFT (" + fieldName + " , " + text.length() +") = \""+ text + "\"");
          break;
        case OPERATION_CONTAINS :
          buffer.append(fieldName + " LIKE \"%" + text + "%\"");
          break;
        case OPERATION_EMPTY :
          buffer.append(fieldName +" = \"\"");
          break;
        case OPERATION_NOT_EMPTY :
          buffer.append(fieldName +" <> \"\"");
          break;
      }
    }
    //EElias
    return buffer;
  }
  
  public int fillDesc(FocDesc focDesc, int firstID){
    setFirstFieldID(firstID);
    
    if(focDesc != null){
    	FPropertyListener colorListener = new ColorPropertyListener(this, firstID + FLD_CONDITION_OPERATION); 
      FMultipleChoiceField multipleChoice = new FMultipleChoiceField(getFieldPrefix()+"_OP", "Operation", firstID + FLD_CONDITION_OPERATION, false, 1);
      multipleChoice.addChoice(OPERATION_NONE, "None");
      multipleChoice.addChoice(OPERATION_STARTS_WITH, "Begins with");
      multipleChoice.addChoice(OPERATION_CONTAINS, "Contains");
      multipleChoice.addChoice(OPERATION_EQUALS, "Equals");
      multipleChoice.addChoice(OPERATION_EMPTY, "Empty");
      multipleChoice.addChoice(OPERATION_NOT_EMPTY, "Not Empty");
      multipleChoice.setSortItems(false);
      focDesc.addField(multipleChoice);
      multipleChoice.addListener(colorListener);
      
      FField field = getFilterDesc().getSubjectFocDesc().getFieldByPath(getFieldPath());
      FCharField charField = new FCharField(getFieldPrefix()+"_TXT", "Text field", firstID + FLD_CONDITION_TEXT, false, field.getSize());
      focDesc.addField(charField);
      charField.addListener(colorListener);
    }
    
    return firstID + FLD_CONDITION_TEXT + 1;
  }

  /* (non-Javadoc)
   * @see b01.foc.list.filter.FilterCondition#putInPanel(b01.foc.gui.FPanel, int, int)
   */
  public GuiSpace putInPanel(FocListFilter filter, FPanel panel, int x, int y) {
    GuiSpace space = new GuiSpace();
    
    FProperty operationProp = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OPERATION);
    FProperty textProp = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_TEXT);
    
    FGComboBox combo = (FGComboBox) operationProp.getGuiComponent();
    panel.add(getFieldLabel(), combo, x, y);

    FGTextField txtComp = (FGTextField) textProp.getGuiComponent();
    txtComp.setColumns(25);
    panel.add(txtComp, x+2, y, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
    
    space.setLocation(2, 2);
    return space;
  }

}
