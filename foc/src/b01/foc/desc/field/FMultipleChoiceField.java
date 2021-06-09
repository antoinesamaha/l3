/*
 * Created on Oct 14, 2004
 */
package b01.foc.desc.field;

import b01.foc.list.*;
import b01.foc.list.filter.FilterCondition;
import b01.foc.list.filter.MultipleChoiceCondition;
import b01.foc.desc.*;
import b01.foc.gui.*;
import b01.foc.gui.table.cellControler.*;
import b01.foc.property.*;

import java.awt.Component;
import java.util.*;

/**
 * @author 01Barmaja
 */
public class FMultipleChoiceField extends FIntField {
  HashMap list = null;

  FocList focSourceList = null;
  int idField = -99;
  int labelField = -99;
  private boolean sortItems = true;

  public FMultipleChoiceField(String name, String title, int id, boolean key, int size) {
    super(name, title, id, key, size);
    list = new HashMap();
  }

  public FProperty newProperty(FocObject masterObj, Object defaultValue){
    return new FMultipleChoice(masterObj, getID(), defaultValue != null ? ((Integer)defaultValue).intValue() : 0);
  }

  public FProperty newProperty(FocObject masterObj){
    return newProperty(masterObj, null);
  }
  
  public void addChoice(int id, String title) {
    FMultipleChoiceItem item = new FMultipleChoiceItem(id, title);
    list.put(Integer.valueOf(id), item);
  }

  public FMultipleChoiceItem getChoiceItemForKey(int id) {
    FMultipleChoiceItem item = null;
    if (list != null) {
      item = (FMultipleChoiceItem) list.get(Integer.valueOf(id));
    }
    return item;
  }

  public Iterator getChoiceIterator() {
    Iterator iter = null;
    if (list != null) {
      Collection coll = list.values();
      if (coll != null) {
        //Collections.sort();
        iter = coll.iterator();
      }
    }
    return iter;
  }

  public void refreshList() {
    if (focSourceList != null) {
      focSourceList.reloadFromDB();
      list.clear();
      for (int i = 0; i < focSourceList.size(); i++) {
        FocObject obj = focSourceList.getFocObject(i);
        if (obj != null) {
          FProperty idProp = obj.getFocProperty(idField);
          FProperty labelProp = obj.getFocProperty(labelField);

          if (idProp != null && labelProp != null) {
            this.addChoice(idProp.getInteger(), labelProp.getString());
          }
        }
      }
    }
  }

  public void setSourceList(FocList focSourceList, int idField, int labelField) {
    this.focSourceList = focSourceList;
    this.idField = idField;
    this.labelField = labelField;
  }
  
  public Component getGuiComponent(FProperty prop){
  	Iterator choices = null;
  	if(prop != null && prop instanceof FMultipleChoice){
  		choices = ((FMultipleChoice)prop).getChoiceIterator();
  	}
    if(choices == null){
    	choices = this.getChoiceIterator();
    }
    FGComboBox comboBox = new FGComboBox(choices, isSortItems());
    if(prop != null) comboBox.setProperty(prop);
    return comboBox;
  }
  
  public AbstractCellControler getTableCellEditor(FProperty prop){
  	Iterator choices = null;
  	if(prop != null && prop instanceof FMultipleChoice){
  		choices = ((FMultipleChoice)prop).getChoiceIterator();
  	}
  	if(choices == null){
  		choices = this.getChoiceIterator();
  	}
    return new ComboBoxCellControler(choices, isSortItems());
  }
  
  public boolean isSortItems() {
    return sortItems;
  }
  
  public void setSortItems(boolean sortItems) {
    this.sortItems = sortItems;
  }
  
  protected FilterCondition getFilterCondition(FFieldPath fieldPath, String conditionPrefix){
		MultipleChoiceCondition condition = null;
		if(fieldPath != null && conditionPrefix != null){
			condition = new MultipleChoiceCondition(fieldPath, conditionPrefix);
		}
		return condition;
	}
}
