//IMPLEMENTED

/*
 * Created on Sep 9, 2005
 */
package b01.foc.list.filter;

import java.awt.Component;

import b01.foc.desc.*;
import b01.foc.desc.field.FFieldPath;
import b01.foc.desc.field.FMultipleChoiceField;
import b01.foc.gui.FPanel;
import b01.foc.property.FBoolean;
import b01.foc.property.FInt;
import b01.foc.property.FMultipleChoice;
import b01.foc.property.FProperty;

/**
 * @author 01Barmaja
 */
public class BooleanCondition extends FilterCondition{
  private static final int FLD_VALUE = 1;

  public static final int VALUE_INDIFFERENT = 0;
  public static final int VALUE_TRUE = 1;
  public static final int VALUE_FALSE = 2; 
  
  public BooleanCondition(FFieldPath boolFieldPath, String fieldPrefix){
    super(boolFieldPath, fieldPrefix);
  }
  
  public int getValue(FocListFilter filter){
    FInt prop = (FInt) filter.getFocProperty(getFirstFieldID() + FLD_VALUE);
    return prop.getInteger();
  }

  public void setValue(FocListFilter filter, int iValue){
    /*FInt prop = (FInt) filter.getFocProperty(getFirstFieldID() + FLD_VALUE);
    prop.setInteger(iValue);*/
  	setValue(filter, iValue, false);
  }

  public FProperty getValueProperty(FocListFilter filter){
    return (FInt) filter.getFocProperty(getFirstFieldID() + FLD_VALUE);
  }

  public void forceToValue(FocListFilter filter, int value){
    /*FInt valueProp = (FInt) filter.getFocProperty(getFirstFieldID() + FLD_VALUE);
    valueProp.setInteger(value);
    valueProp.setValueLocked(true);*/
  	setValue(filter, value, true);
  }
  
  private void setValue(FocListFilter filter, int iValue, boolean lockConditionAlso){
  	FInt valueProp = (FInt) filter.getFocProperty(getFirstFieldID() + FLD_VALUE);
    valueProp.setInteger(iValue);
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
    new FMultipleChoice(focFatherObject, getFirstFieldID() + FLD_VALUE, VALUE_INDIFFERENT);
  }
  
  public boolean includeObject(FocListFilter filter, FocObject object){
    boolean include = true;
    if(getValue(filter) != VALUE_INDIFFERENT){
      include = false;
      
      FBoolean boolProp = (FBoolean) getFieldPath().getPropertyFromObject(object);
      if(boolProp != null){
        boolean boolVal = boolProp != null ? boolProp.getBoolean() : false;
        if(getValue(filter) == VALUE_TRUE){
          include = boolVal;
        }else{
          include = !boolVal;
        }
      }
    }
    return include;
  }
  
  public StringBuffer buildSQLWhere(FocListFilter filter, String fieldName) {
    StringBuffer buffer = null;
    int valueCondition = getValue(filter);
    if(valueCondition != VALUE_INDIFFERENT){
      buffer = new StringBuffer();
      
      if(valueCondition == VALUE_TRUE){
        buffer.append(fieldName+"=1");
      }else{
        buffer.append(fieldName+"=0");
      }
    }
    return buffer;
  }
  
  public int fillDesc(FocDesc focDesc, int firstID){
    setFirstFieldID(firstID);
    
    if(focDesc != null){
      FMultipleChoiceField multipleChoice = new FMultipleChoiceField(getFieldPrefix()+"_VAL", "Value", firstID + FLD_VALUE, false, 1);
      multipleChoice.addChoice(VALUE_INDIFFERENT, "All");
      multipleChoice.addChoice(VALUE_TRUE, "True");
      multipleChoice.addChoice(VALUE_FALSE, "False");
      multipleChoice.setSortItems(false);
      focDesc.addField(multipleChoice);
      multipleChoice.addListener(new ColorPropertyListener(this, firstID + FLD_VALUE));
    }
    
    return firstID + FLD_VALUE + 1;
  }

  /* (non-Javadoc)
   * @see b01.foc.list.filter.FilterCondition#putInPanel(b01.foc.gui.FPanel, int, int)
   */
  public GuiSpace putInPanel(FocListFilter filter, FPanel panel, int x, int y) {
    GuiSpace space = new GuiSpace();
    
    FProperty valueProp = filter.getFocProperty(getFirstFieldID() + FLD_VALUE);
    
    Component active = (Component) valueProp.getGuiComponent();
    panel.add(getFieldLabel(), active, x, y);
    
    space.setLocation(2, 2);
    return space;
  }
  
  public boolean isValueLocked(FocListFilter filter){
  	boolean locked = false;
  	FProperty valueProp = filter.getFocProperty(getFirstFieldID() + FLD_VALUE);
  	if(valueProp != null){
  		locked = valueProp.isValueLocked();
  	}
  	return locked;
  }
  
  public void resetToDefaultValue(FocListFilter filter){
  	setValue(filter, VALUE_INDIFFERENT);
  }
}
