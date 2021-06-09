//IMPLEMENTED

/*
 * Created on Sep 9, 2005
 */
package b01.foc.list.filter;

import java.awt.Component;
import java.awt.GridBagConstraints;

import b01.foc.desc.*;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FFieldPath;
import b01.foc.desc.field.FMultipleChoiceField;
import b01.foc.desc.field.FObjectField;
import b01.foc.gui.FPanel;
import b01.foc.property.FMultipleChoice;
import b01.foc.property.FObject;
import b01.foc.property.FProperty;
import b01.foc.property.FPropertyListener;

/**
 * @author 01Barmaja
 */
public class ObjectCondition extends FilterCondition{
  static protected final int FLD_CONDITION_OPERATION = 1;
  static protected final int FLD_CONDITION_OBJECT    = 2;

  static public final int OPERATION_NONE   = 0;
  static public final int OPERATION_EQUALS = 3;
  static public final int OPERATION_EMPTY  = 4;
  
  public ObjectCondition(FFieldPath objectFieldPath, String fieldPrefix){
    super(objectFieldPath, fieldPrefix);
  }
  
  public int getOperation(FocListFilter filter){
    FProperty prop = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OPERATION);
    return prop.getInteger();
  }

  public void setOperation(FocListFilter filter, int operation){
  	filter.setPropertyMultiChoice(getFirstFieldID() + FLD_CONDITION_OPERATION, operation);
  }

  public FocObject getObject(FocListFilter filter){
    FObject prop = (FObject) filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OBJECT);
    return prop.getObject_CreateIfNeeded();
  }

  public void setObject(FocListFilter filter, FocObject object){
  	setOperation(filter, OPERATION_EQUALS);
  	
    FObject oprop = (FObject) filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OBJECT);
    oprop.setObject(object);
  }
  
  public void setToValue(FocListFilter filter, int operation, FocObject focObject){
  	setToValue(filter, operation, focObject, false);
  }
  
  public void forceToValue(FocListFilter filter, int operation, FocObject focObject){
  	setToValue(filter, operation, focObject, true);
  }
  
  private void setToValue(FocListFilter filter, int operation, FocObject focObject, boolean lockConditionAlso){
  	FProperty operationProp = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OPERATION);
  	operationProp.setInteger(operation);
  	if(lockConditionAlso){
  		operationProp.setValueLocked(true);
  	}
  	FObject valueProp = (FObject) filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OBJECT);
  	valueProp.setObject(focObject);
  	if(lockConditionAlso){
  		valueProp.setValueLocked(true);
  	}
  	
  }

  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // IMPLEMENTED
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public void fillProperties(FocObject focFatherObject){
    new FMultipleChoice(focFatherObject, getFirstFieldID() + FLD_CONDITION_OPERATION, OPERATION_NONE);
    new FObject(focFatherObject, getFirstFieldID() + FLD_CONDITION_OBJECT, null);
  }
  
  public boolean includeObject(FocListFilter filter, FocObject object){
    boolean include = true;
    int operation = getOperation(filter);
    if(operation != OPERATION_NONE){
      FocObject condObject = getObject(filter);
      
      FObject objProp = (FObject) getFieldPath().getPropertyFromObject(object);
      FocObject itemObject = objProp != null ? objProp.getObject_CreateIfNeeded() : null;
      
      switch(operation){
        case OPERATION_EQUALS:
          include = false;
          if(condObject == null && itemObject == null){
            include = true;
          }else if(condObject != null && itemObject != null){
            include = itemObject.getReference().getInteger() == condObject.getReference().getInteger();
          }
          break;
        case OPERATION_EMPTY:
          include = itemObject == null;
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
      FocObject condObject = getObject(filter);
      
      FObjectField objField = (FObjectField) getFieldPath().getFieldFromDesc(filter.getThisFilterDesc().getSubjectFocDesc());      
      
      switch(operation){
      case OPERATION_EQUALS:
      {
        int refValue = (condObject == null) ? 0: condObject.getReference().getInteger();
        buffer.append(objField.getKeyPrefix() + FField.REF_FIELD_NAME+"="+refValue);
      }
      break;
      case OPERATION_EMPTY:
        buffer.append(objField.getKeyPrefix() + FField.REF_FIELD_NAME+"=0 ");
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
      multipleChoice.addChoice(OPERATION_EMPTY, "Empty");
      multipleChoice.setSortItems(false);
      focDesc.addField(multipleChoice);
      multipleChoice.addListener(colorListener);
      
      FObjectField field = (FObjectField) getFilterDesc().getSubjectFocDesc().getFieldByPath(getFieldPath());
      FObjectField objField = new FObjectField(getFieldPrefix()+"_OBJREF", "Object field", firstID + FLD_CONDITION_OBJECT, false, field.getFocDesc(), field.getKeyPrefix());
      objField.copyInteralProperties(field);
      focDesc.addField(objField);
      objField.addListener(colorListener);
    }
    
    return firstID + FLD_CONDITION_OBJECT + 1;
  }

  /* (non-Javadoc)
   * @see b01.foc.list.filter.FilterCondition#putInPanel(b01.foc.gui.FPanel, int, int)
   */
  public GuiSpace putInPanel(FocListFilter filter, FPanel panel, int x, int y) {
    GuiSpace space = new GuiSpace();
    
    FProperty operationProp = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OPERATION);
    FObject objectProp = (FObject) filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OBJECT);
    
    Component comp = operationProp.getGuiComponent();
    panel.add(getFieldLabel(), comp, x, y);
    
    /*
    FGObjectComboBox comboBox = (FGObjectComboBox)objectProp.getGuiComponent_ComboBox();
    comboBox.refreshList(objectProp);
    objectProp.getPropertySourceList().reloadFromDB();
    panel.add(comboBox, x+2, y, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
    */
    comp = objectProp.getGuiComponent();
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
  	setToValue(filter, OPERATION_NONE, null);
  }
}
