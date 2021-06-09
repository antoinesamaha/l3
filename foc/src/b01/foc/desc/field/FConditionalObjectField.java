/*
 * Created on Oct 14, 2004
 */
package b01.foc.desc.field;

import b01.foc.desc.*;
import b01.foc.gui.*;
import b01.foc.gui.table.cellControler.*;
import b01.foc.list.*;
import b01.foc.list.filter.FilterCondition;
import b01.foc.list.filter.FocDescForFilter;
import b01.foc.list.filter.ObjectCondition;
import b01.foc.property.*;
import b01.foc.gui.table.*;

import java.awt.Component;
import java.sql.Types;

/**
 * @author 01Barmaja
 */
public class FConditionalObjectField extends FObjectField {
	private FFieldPath pathToDescProperty = null;
	
	public FConditionalObjectField(String name, String title, int id, FocDesc focDesc, String keyPrefix, FocDesc slaveFocDesc, int listFieldIdInMaster, FFieldPath pathToDescProperty){
		super(name, title, id, false, focDesc, keyPrefix, slaveFocDesc, listFieldIdInMaster, null);
		this.pathToDescProperty = pathToDescProperty;
	}

	public void dispose(){
    super.dispose();
    pathToDescProperty = null;
  }
  
  protected void setListFieldInMaster(int listFieldIdInMaster, FocDesc slaveFocDesc, FocDescForFilter focDescForFilter){
  	if(isMasterDetailsLink()){
      FocDesc masterDesc = getFocDesc();
      if(masterDesc != null){
        masterDesc.addListField(pathToDescProperty, listFieldIdInMaster, null);      	
      }
  	}
  }
}
