/*
 * Created on 14 fvr. 2004
 */
package b01.foc.gui;

import java.awt.Color;
import java.util.*;

import b01.foc.Globals;
import b01.foc.desc.field.*;
import b01.foc.property.IFMultipleChoiceProperty;

/**
 * @author 01Barmaja
 */
public class FGComboBox extends FGAbstractComboBox {
	private boolean sort = false;

  private void init(Iterator choices, boolean sort) {
  	this.sort = sort;
    fillChoices(choices, sort);
    addFocusListener(this);
    addActionListener(this);
    setFont(Globals.getDisplayManager().getDefaultFont());
    setForeground(Color.BLACK);
  }
  
  /**
   * @param field
   */
  public FGComboBox(Iterator choices, boolean sort) {
    init(choices, sort);
  }

  public void dispose(){
    super.dispose();
    removeFocusListener(this);
    removeActionListener(this);
    
  }
  
  private boolean isSort(){
  	return this.sort;
  }
  
  /**
   * @param prop
   */
  /*
   public FGComboBox(FProperty prop) {
   if (prop != null) {
   FMultipleChoiceField multipleChoiceField = (FMultipleChoiceField) prop.getFocField();
   Iterator choices = multipleChoiceField.getChoiceIterator();

   init(choices);
   
   setProperty(prop);
   }
   }
   */

  protected void fillChoices(Iterator choices, boolean sort) {
    ArrayList intermediate = new ArrayList();
    
    while (choices != null && choices.hasNext()) {
      FMultipleChoiceItemInterface item = (FMultipleChoiceItemInterface) choices.next();
      if (item != null) {
        //addItem(item.getTitle());
        intermediate.add(item);
      }
    }
    
    Comparator comparator = null;
        
    if(sort){
      comparator = new Comparator(){
        public int compare(Object arg0, Object arg1) {
          FMultipleChoiceItemInterface item0 = (FMultipleChoiceItemInterface) arg0;    
          FMultipleChoiceItemInterface item1 = (FMultipleChoiceItemInterface) arg1;
          return item0.getTitle().compareTo(item1.getTitle()); 
        }
      };
    }else{
      comparator = new Comparator(){
        public int compare(Object arg0, Object arg1) {
          FMultipleChoiceItemInterface item0 = (FMultipleChoiceItemInterface) arg0;    
          FMultipleChoiceItemInterface item1 = (FMultipleChoiceItemInterface) arg1;
          return item0.getId() - item1.getId(); 
        }
      };
    }
    Collections.sort(intermediate, comparator);
    for(int i=0; i<intermediate.size(); i++){
      FMultipleChoiceItemInterface item = (FMultipleChoiceItemInterface) intermediate.get(i);
      addItem(item.getTitle());
    }
  }
  
  public void refillChoices(){
  	removeAllItems();
  	IFMultipleChoiceProperty fMultipleChoice = (IFMultipleChoiceProperty)getProperty();
  	if(fMultipleChoice != null){
  		fillChoices(fMultipleChoice.getChoiceIterator(), isSort());
  	}
  	String selectedItem = fMultipleChoice.getString();
  	//setSelectedItem(selectedItem);
  	
  }

  protected void setPropertyStringValue(String strValue) {
    if (property != null) {
      property.setString(strValue);
    }
  }

  protected String getPropertyStringValue() {
    return property != null ? property.getString() : null;
  }

}
