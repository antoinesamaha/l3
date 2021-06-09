//IMPLEMENTED

/*
 * Created on Sep 9, 2005
 */
package b01.foc.list.filter;

import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Date;
import java.text.SimpleDateFormat;

import b01.foc.Globals;
import b01.foc.db.DBManager;
import b01.foc.desc.*;
import b01.foc.desc.field.FDateField;
import b01.foc.desc.field.FFieldPath;
import b01.foc.desc.field.FMultipleChoiceField;
import b01.foc.gui.FGComboBox;
import b01.foc.gui.FGDateField;
import b01.foc.gui.FGLabel;
import b01.foc.gui.FPanel;
import b01.foc.property.FDate;
import b01.foc.property.FInt;
import b01.foc.property.FMultipleChoice;
import b01.foc.property.FProperty;
import b01.foc.property.FPropertyListener;

/**
 * @author 01Barmaja
 */
public class DateCondition extends FilterCondition{
  private static final int FLD_FIRST_DATE = 1;
  private static final int FLD_LAST_DATE = 2;
  private static final int FLD_OPERATOR = 3;

  //BElias make the constansts public and add operator_equals
  /*private static final int OPERATOR_BETWEEN = 0;
  private static final int OPERATOR_GREATER_THAN = 1;
  private static final int OPERATOR_LESS_THAN = 2;*/
  public static final int OPERATOR_BETWEEN = 0;
  public static final int OPERATOR_GREATER_THAN = 1;
  public static final int OPERATOR_LESS_THAN = 2;
  public static final int OPERATOR_EQUALS = 3;
  public static final int OPERATOR_INDIFERENT= 4;
  //EElias
  
  public DateCondition(FFieldPath dateFieldPath, String fieldPrefix){
    super(dateFieldPath, fieldPrefix);
  }
  
  public java.sql.Date getFirstDate(FocListFilter filter){
    FDate prop = (FDate) filter.getFocProperty(getFirstFieldID() + FLD_FIRST_DATE);
    return prop.getDate();
  }

  public java.sql.Date getLastDate(FocListFilter filter){
    FDate prop = (FDate) filter.getFocProperty(getFirstFieldID() + FLD_LAST_DATE);
    return prop.getDate();
  }

  public int getOperator(FocListFilter filter){
    FMultipleChoice prop = (FMultipleChoice) filter.getFocProperty(getFirstFieldID() + FLD_OPERATOR);
    return prop.getInteger();
  }
  
  //BElias
  public void setOperator(FocListFilter filter , int op){
    FInt operator = (FInt)filter.getFocProperty(getFirstFieldID() + FLD_OPERATOR);
    if (operator != null){
      operator.setInteger(op);
    }
  }
  
  public void setFirstDate(FocListFilter filter ,Date d){
    FDate date = (FDate)filter.getFocProperty(getFirstFieldID() + FLD_FIRST_DATE);
    if (date != null){
      date.setDate(d);
    }
  }
  
  public void setLastDate(FocListFilter filter ,Date d){
    FDate date = (FDate)filter.getFocProperty(getFirstFieldID() + FLD_LAST_DATE);
    if (date != null){
      date.setDate(d);
    }
  }
  //EElias
  
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // IMPLEMENTED
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public void fillProperties(FocObject focFatherObject){
    new FMultipleChoice(focFatherObject, getFirstFieldID() + FLD_OPERATOR, OPERATOR_GREATER_THAN);
    new FDate(focFatherObject, getFirstFieldID() + FLD_FIRST_DATE, new Date(0));
    new FDate(focFatherObject, getFirstFieldID() + FLD_LAST_DATE, new Date(0));
  }
  
  public boolean includeObject(FocListFilter filter, FocObject object){
    boolean include = true;
    
    FDate dateProp = (FDate) getFieldPath().getPropertyFromObject(object);
    Date date = dateProp.getDate(); 
    Date firstDate = getFirstDate(filter);
    Date lastDate = getLastDate(filter);
    
    int op = getOperator(filter);
    
    if(op != OPERATOR_INDIFERENT){
      if(!FDate.isEmpty(firstDate) && (op == OPERATOR_BETWEEN || op == OPERATOR_GREATER_THAN)){
        include = date.after(firstDate) || date.equals(firstDate);
      }
  
      if(include && !FDate.isEmpty(lastDate) && (op == OPERATOR_BETWEEN || op == OPERATOR_LESS_THAN)){
        include = date.before(lastDate) || date.equals(lastDate);
      }
      //BElias
      if(include && !FDate.isEmpty(firstDate) && (op == OPERATOR_EQUALS)){
        include = date.equals(firstDate);
      }
      //EElias
    }
    
    return include;
  }
  
  public StringBuffer buildSQLWhere(FocListFilter filter, String fieldName) {
    //BElias
    //b01.foc.Globals.logString("Condition sql build not implemented yet");
    StringBuffer buffer = null;
    Date firstDate = getFirstDate(filter);
    Date lastDate = getLastDate(filter);
    int op = getOperator(filter);
    buffer = new StringBuffer();
    
    //rr Begin 
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");
    String firstDateFormat = "";
    String lastDateFormat = "";
    if (Globals.getDBManager().getProvider() == DBManager.PROVIDER_ORACLE){
      firstDateFormat = dateFormat.format(firstDate);
      lastDateFormat = dateFormat.format(lastDate);
    }else{
      firstDateFormat = String.valueOf(firstDate);
      lastDateFormat = String.valueOf(lastDate);
    }
    //rr End
    
    if (op != OPERATOR_INDIFERENT){
      if (op == OPERATOR_GREATER_THAN){
        if(firstDate.getTime() != 0){
         // buffer.append(fieldName + ">= \"" + firstDate + "\""); //rr
          buffer.append(fieldName + ">= \"" + firstDateFormat + "\"");
        }
        
      }else if (op == OPERATOR_LESS_THAN) {
        if(lastDate.getTime() != 0){
          //buffer.append(fieldName + "<= \"" + lastDate + "\""); //rr
          buffer.append(fieldName + "<= \"" + lastDateFormat + "\"");
        }
      
      }else if (op == OPERATOR_BETWEEN){
        if(firstDate.getTime() != 0 && lastDate.getTime() != 0){
          //buffer.append(fieldName + " BETWEEN \"" + firstDate + " \" AND \"" + lastDate + "\"" ); //rr
          buffer.append(fieldName + " BETWEEN \"" + firstDateFormat + " \" AND \"" + lastDateFormat + "\"" );
        }
      }else if (op == OPERATOR_EQUALS){
        if(firstDate.getTime() != 0){
          //buffer.append(fieldName + " = \"" + firstDate + "\""); //rr
          buffer.append(fieldName + " = \"" + firstDateFormat + "\"");
        }
      }
    }
    //EElias
    return buffer;
  }
  
  public void setToOperationWithLock(FocListFilter filter, int operation, boolean lock){
  	FProperty prop = filter.getFocProperty(getFirstFieldID() + FLD_OPERATOR);
    prop.setInteger(operation);
    prop.setValueLocked(lock);
  }
  
  public void setToValue(FocListFilter filter, int operation, Date firstDate, Date lastDate){
  	setToValue(filter, operation, firstDate, lastDate, false);
  }
  
  public void forceToValue(FocListFilter filter, int operation, Date firstDate, Date lastDate){
  	setToValue(filter, operation, firstDate, lastDate, true);
  }
  
  private void setToValue(FocListFilter filter, int operation, Date firstDate, Date lastDate, boolean lockConditionAlso){
  	FProperty operatorprop = filter.getFocProperty(getFirstFieldID() + FLD_OPERATOR);
  	operatorprop.setInteger(operation);
  	if(lockConditionAlso){
  		operatorprop.setValueLocked(true);
  	}
  	
  	FDate dateCond = (FDate) filter.getFocProperty(getFirstFieldID() + FLD_FIRST_DATE);
  	dateCond.setDate(firstDate);
  	if(lockConditionAlso){
  		dateCond.setValueLocked(true);
  	}
  	
  	dateCond = (FDate) filter.getFocProperty(getFirstFieldID() + FLD_LAST_DATE);
  	dateCond.setDate(lastDate);
  	if(lockConditionAlso){
  		dateCond.setValueLocked(true);
  	}
  }
  
  public int fillDesc(FocDesc focDesc, int firstID){
    setFirstFieldID(firstID);
    
    if(focDesc != null){
    	FPropertyListener colorListener = new ColorPropertyListener(this, firstID + FLD_OPERATOR);
      FMultipleChoiceField multipleChoice = new FMultipleChoiceField(getFieldPrefix()+"_OP", "Operation", firstID + FLD_OPERATOR, false, 1);
      multipleChoice.addChoice(OPERATOR_INDIFERENT, " Non ");
      multipleChoice.addChoice(OPERATOR_BETWEEN, "Between");
      multipleChoice.addChoice(OPERATOR_GREATER_THAN, " >= ");
      multipleChoice.addChoice(OPERATOR_LESS_THAN, " <= ");
      multipleChoice.addChoice(OPERATOR_EQUALS, " = ");
      multipleChoice.setSortItems(false);
      focDesc.addField(multipleChoice);
      multipleChoice.addListener(colorListener);
      
      FDateField dateField = new FDateField(getFieldPrefix()+"_FDATE", "First date", firstID + FLD_FIRST_DATE, false);
      focDesc.addField(dateField);
      dateField.addListener(colorListener);
      
      dateField = new FDateField(getFieldPrefix()+"_LDATE", "Last date", firstID + FLD_LAST_DATE, false);
      focDesc.addField(dateField);
      dateField.addListener(colorListener);
    }
    
    return firstID + FLD_OPERATOR + 1;
  }

  FGComboBox combo = null;
  FGDateField firstDateComp = null;
  FGDateField lastDateComp = null;
  FGLabel flecheComp = null;
  
  /* (non-Javadoc)
   * @see b01.foc.list.filter.FilterCondition#putInPanel(b01.foc.gui.FPanel, int, int)
   */
  public GuiSpace putInPanel(FocListFilter filter, FPanel panel, int x, int y) {
    GuiSpace space = new GuiSpace();
    
    FProperty operatorProp = filter.getFocProperty(getFirstFieldID() + FLD_OPERATOR);
    FProperty firstDateProp = filter.getFocProperty(getFirstFieldID() + FLD_FIRST_DATE);
    FProperty lastDateProp = filter.getFocProperty(getFirstFieldID() + FLD_LAST_DATE);
   
    FPanel datesPanel = new FPanel();
    
    combo = (FGComboBox) operatorProp.getGuiComponent();
    panel.add(getFieldLabel(), combo, x, y);
    
    firstDateComp = (FGDateField) firstDateProp.getGuiComponent();
    datesPanel.add(firstDateComp, 0, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);

    flecheComp = new FGLabel("->");
    datesPanel.add(flecheComp, 1, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
    
    lastDateComp = (FGDateField) lastDateProp.getGuiComponent();
    datesPanel.add(lastDateComp, 2, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
    
    panel.add(datesPanel, x+2, y, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
    
    space.setLocation(2, 2);
    
    ItemListener itemListener = new ItemListener(){
      public void itemStateChanged(ItemEvent e) {
        if(e == null || e.getStateChange() == ItemEvent.SELECTED){
          firstDateComp.setVisible(true);
          lastDateComp.setVisible(true);
          flecheComp.setVisible(true);
          firstDateComp.setVisible(false);
          lastDateComp.setVisible(false);
          flecheComp.setVisible(false);
          
          switch(combo.getSelectedIndex()){
          case OPERATOR_BETWEEN:
            firstDateComp.setVisible(true);
            lastDateComp.setVisible(true);
            flecheComp.setVisible(true);
            break;
          case OPERATOR_GREATER_THAN:           
            firstDateComp.setVisible(true);
            
            break;
          case OPERATOR_LESS_THAN:            
            lastDateComp.setVisible(true);            
            break;
            //BElias
          case OPERATOR_EQUALS:
            firstDateComp.setVisible(true);
            break;
            //EElias
          }
        }
      }
    };
    
    combo.addItemListener(itemListener);
    itemListener.itemStateChanged(null);
    //lastDateComp.setVisible(true);
    return space;
  }
  
  public boolean isValueLocked(FocListFilter filter){
  	boolean locked = false;
  	FProperty operatorProp = filter.getFocProperty(getFirstFieldID() + FLD_OPERATOR);
  	if(operatorProp != null){
  		locked = operatorProp.isValueLocked();
  	}
  	return locked;
  } 
  
  public void resetToDefaultValue(FocListFilter filter){
  	Date date = new Date(0);
  	setToValue(filter, OPERATOR_GREATER_THAN, date, date);
  }
}
