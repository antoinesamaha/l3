/*
 * Created on Oct 14, 2004
 */
package b01.foc.desc.field;

import b01.foc.*;
import b01.foc.desc.FocDesc;
import b01.foc.desc.FocObject;
import b01.foc.gui.*;
import b01.foc.gui.table.cellControler.*;
import b01.foc.list.*;
import b01.foc.list.filter.FilterCondition;
import b01.foc.list.filter.FocDescForFilter;
import b01.foc.property.*;

import java.awt.Component;
import java.sql.Types;

/**
 * @author 01Barmaja
 */
public class FListField extends FField {
  private FocLink focLink = null;
  private FocDescForFilter focDescForFilter = null; 
  
  public FListField(String name, String title, int id, FocLink focLink, FocDescForFilter focDescForFilter) {
    super(name, title, id, false, 0, 0);
    this.focLink = focLink;
    this.focDescForFilter = focDescForFilter;
  }

  public FListField(String name, String title, int id, FocLink focLink) {
    this(name, title, id, focLink, null);
  }

  public int getSqlType() {
    return Types.ARRAY;
  }

  public String getCreationString(String name) {
    return "";
  }

  public FocLink getLink() {
    return focLink;
  }

  public Object clone() throws CloneNotSupportedException {
    return null;
  }

  public FProperty newProperty(FocObject masterObj, Object defaultValue){
    return new FList(masterObj, getID(), (FocList)defaultValue);
  }
  
  public FProperty newProperty(FocObject masterObj){
  	FocList list = null;
  	FocLink link = getLink();
  	if(link instanceof FocLinkConditionalForeignKey){
  		FocLinkConditionalForeignKey conditionalLink = (FocLinkConditionalForeignKey) link;
  		link = conditionalLink.clone();
  	}
  	
    if(focDescForFilter != null){
      list = new FocListWithFilter(focDescForFilter, masterObj, link, null);
    }else{
      list = new FocList(masterObj, link, null);
    }
  	list.setDirectlyEditable(true);
  	list.setDirectImpactOnDatabase(false);
    return newProperty(masterObj, list);
  }

  public Component getGuiComponent(FProperty prop){
    FListPanel selPanel = null; 
    if(prop != null){
      FList listProp = (FList)prop;
      selPanel = new FListPanel(listProp.getList());
    }
    return selPanel;
  }
  
  public AbstractCellControler getTableCellEditor(FProperty prop){
    Globals.logString("LtFdGetCellEdit: Not implemented");
    //Not Implemented
    //Not Implemented
    //Not Implemented
    //Not Implemented    
    return null;
  }
  
  public boolean isObjectContainer(){
    return false;
  }

  public FocDesc getFocDesc(){
    return null;
  }
  
  public void addReferenceLocations(FocDesc pointerDesc){
    
  }  
  
  protected FilterCondition getFilterCondition(FFieldPath fieldPath, String conditionPrefix){
  	return null;
  }

  public FocDescForFilter getFocDescForFilter() {
    return focDescForFilter;
  }

  public void setFocDescForFilter(FocDescForFilter focDescForFilter) {
    this.focDescForFilter = focDescForFilter;
  }
}
