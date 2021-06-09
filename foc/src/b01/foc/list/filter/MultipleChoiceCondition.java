//IMPLEMENTED

/*
 * Created on Sep 9, 2005
 */
package b01.foc.list.filter;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.util.Iterator;

import b01.foc.desc.*;
import b01.foc.desc.field.FFieldPath;
import b01.foc.desc.field.FMultipleChoiceField;
import b01.foc.desc.field.FMultipleChoiceItem;
import b01.foc.gui.FPanel;
import b01.foc.property.FMultipleChoice;
import b01.foc.property.FProperty;
import b01.foc.property.FPropertyListener;

/**
 * @author 01Barmaja
 */
public class MultipleChoiceCondition extends FilterCondition{
  static protected final int FLD_CONDITION_OPERATION = 1;
  static protected final int FLD_CONDITION_VALUE = 2;

  static public final int OPERATION_NONE = 0;
  static public final int OPERATION_EQUALS = 1;
  static public final int OPERATION_DIFFERENT_FROM = 3;
  
  public MultipleChoiceCondition(FFieldPath stringFieldPath, String fieldPrefix){
    super(stringFieldPath, fieldPrefix);
  }
  
  public int getOperation(FocListFilter filter){
    FProperty prop = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OPERATION);
    return prop.getInteger();
  }

  public void setOperation(FocListFilter filter, int operation){
    FProperty prop = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OPERATION);
    prop.setInteger(operation);
  }

  public int getValue(FocListFilter filter){
    FProperty prop = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_VALUE);
    return prop.getInteger();
  }

  private void setToValueWithLock(FocListFilter filter, int operation, int choice, boolean lock){
    FProperty prop = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OPERATION);
    prop.setInteger(operation);
    prop.setValueLocked(lock);
    
    prop = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_VALUE);
    prop.setInteger(choice);
    prop.setValueLocked(lock);
  }

  public void setToValue(FocListFilter filter, int operation, int choice){
  	setToValueWithLock(filter, operation, choice, false);
  }

  public void forceToValue(FocListFilter filter, int operation, int choice){
  	setToValueWithLock(filter, operation, choice, true);
  }
  
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // IMPLEMENTED
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public void fillProperties(FocObject focFatherObject){
    new FMultipleChoice(focFatherObject, getFirstFieldID() + FLD_CONDITION_OPERATION, OPERATION_NONE);
    new FMultipleChoice(focFatherObject, getFirstFieldID() + FLD_CONDITION_VALUE, 0);
  }
  
  public boolean includeObject(FocListFilter filter, FocObject object){
    boolean include = true;
    int operation = getOperation(filter);
    if(operation != OPERATION_NONE){
      int condValue = getValue(filter);
      
      FMultipleChoice itemMultiProp = (FMultipleChoice) getFieldPath().getPropertyFromObject(object);
      int itemValue = itemMultiProp != null ? itemMultiProp.getInteger() : 0;
      
      switch(operation){
        case OPERATION_EQUALS:
          include = itemValue == condValue;
          break;
        case OPERATION_DIFFERENT_FROM:
          include = itemValue != condValue;
          break;
      }
    }
    return include;
  }

  public StringBuffer buildSQLWhere(FocListFilter filter, String fieldName) {
    StringBuffer buffer = null;
    int operation = getOperation(filter);
    if(operation != OPERATION_NONE){
      buffer = new StringBuffer();
      int condChoice = getValue(filter);
      
      switch(operation){
      case OPERATION_EQUALS:
        buffer.append(fieldName+"="+condChoice);
      break;
      case OPERATION_DIFFERENT_FROM:
        buffer.append(fieldName+"<>"+condChoice);
      break;
      }
    }
    return buffer;
  }
  
  public int fillDesc(FocDesc focDesc, int firstID){
    setFirstFieldID(firstID);
    
    if(focDesc != null){
    	FPropertyListener colorListener = new ColorPropertyListener(this, firstID + FLD_CONDITION_OPERATION);
    	
      FMultipleChoiceField multipleChoice = new FMultipleChoiceField(getFieldPrefix()+"_OP", "Operation", firstID + FLD_CONDITION_OPERATION, false, 1);
      multipleChoice.addChoice(OPERATION_NONE, "None");
      multipleChoice.addChoice(OPERATION_EQUALS, "Equals");
      multipleChoice.addChoice(OPERATION_DIFFERENT_FROM, "Different from");
      multipleChoice.setSortItems(false);
      focDesc.addField(multipleChoice);
      multipleChoice.addListener(colorListener);

      FMultipleChoiceField originalfield = (FMultipleChoiceField) getFilterDesc().getSubjectFocDesc().getFieldByPath(getFieldPath());
      multipleChoice = new FMultipleChoiceField(getFieldPrefix()+"_VAL", "Choice", firstID + FLD_CONDITION_VALUE, false, originalfield.getSize());
      Iterator iter = originalfield.getChoiceIterator();
      while(iter != null && iter.hasNext()){
        FMultipleChoiceItem choiceItem = (FMultipleChoiceItem) iter.next();
        if(choiceItem != null){
          multipleChoice.addChoice(choiceItem.getId(), choiceItem.getTitle());
        }
      }
      focDesc.addField(multipleChoice);
      multipleChoice.addListener(colorListener);
    }
    
    return firstID + FLD_CONDITION_VALUE + 1;
  }

  /* (non-Javadoc)
   * @see b01.foc.list.filter.FilterCondition#putInPanel(b01.foc.gui.FPanel, int, int)
   */
  public GuiSpace putInPanel(FocListFilter filter, FPanel panel, int x, int y) {
    GuiSpace space = new GuiSpace();
    
    FProperty operationProp = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OPERATION);
    FProperty textProp = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_VALUE);
    
    Component comp = operationProp.getGuiComponent();
    panel.add(getFieldLabel(), comp, x, y);
    
    comp = textProp.getGuiComponent();
    panel.add(comp, x+2, y, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
    
    space.setLocation(2, 2);
    return space;
  }
  
	public boolean isValueLocked(FocListFilter filter){
  	boolean locked = false;
  	FProperty operationProp = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OPERATION);
  	if(operationProp != null){
  		locked = operationProp.isValueLocked();
  	}
  	return locked;
  }
	
	public void resetToDefaultValue(FocListFilter filter){
		setToValue(filter, OPERATION_NONE, 1);
	}

}
