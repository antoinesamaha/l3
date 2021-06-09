package b01.fab.model.menu;

import b01.fab.gui.browse.GuiBrowseDesc;
import b01.fab.gui.details.GuiDetailsDesc;
import b01.fab.model.table.TableDefinition;
import b01.fab.model.table.TableDefinitionDesc;
import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FCharField;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FObjectField;
import b01.foc.list.FocList;
import b01.foc.list.FocListOrder;
import b01.foc.property.FObject;
import b01.foc.property.FProperty;
import b01.foc.property.FPropertyListener;

public class MenuDefinitionDesc extends FocDesc{
	
	public static final int FLD_NAME = 1;
	public static final int FLD_FATHER_MENU = 2;
	public static final int FLD_TABLE_DEFINITION = 3;
	public static final int FLD_USER_BROWSE_VIEW_DEFINITION = 4;
	public static final int FLD_USER_DETAILS_VIEW_DEFINITION = 5;
	
	private static FPropertyListener tableDefinitionListener = null;
	private static FPropertyListener getTableDefinitionListener(){
		if(tableDefinitionListener == null){
			tableDefinitionListener = new FPropertyListener(){

				public void propertyModified(FProperty property) {
					if(property != null){
						MenuDefinition menuDefinition = (MenuDefinition) property.getFocObject();
						if(menuDefinition != null){
							TableDefinition tableDefinition = menuDefinition.getTableDefinition();
							if(tableDefinition != null){
								if(tableDefinition.isSingleInstance()){
									FObject prop = (FObject) menuDefinition.getFocProperty(FLD_USER_DETAILS_VIEW_DEFINITION);
									if(prop != null){
										prop.setLocalSourceList(tableDefinition.getDetailsViewDefinitionList());
									}
									prop = (FObject) menuDefinition.getFocProperty(FLD_USER_BROWSE_VIEW_DEFINITION);
									if(prop != null){
										prop.setValueLocked(true);
									}
								}else{
									FObject prop = (FObject) menuDefinition.getFocProperty(FLD_USER_BROWSE_VIEW_DEFINITION);
									if(prop != null){
										prop.setLocalSourceList(tableDefinition.getBrowseViewDefinitionList());
									}
									prop = (FObject) menuDefinition.getFocProperty(FLD_USER_DETAILS_VIEW_DEFINITION);
									if(prop != null){
										prop.setValueLocked(true);
									}
								}
							}
						}
					}
				}

				public void dispose() {
				}
			};
		}
		return tableDefinitionListener;
	}
	
	public MenuDefinitionDesc(){
		super(MenuDefinition.class,FocDesc.DB_RESIDENT,"MENU_DEFINITION",false);
		FField fld = addReferenceField();
		
		fld = new FCharField("NAME","Name",FLD_NAME,false,30);
		addField(fld);
		
		FObjectField objFld = new FObjectField("FATHER_MENU","Father menu",FLD_FATHER_MENU,false,this,"FATHER_MENU_");
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setWithList(false);
		addField(objFld);
		
		objFld = new FObjectField("TABLE_DEFINITION", "Table definition", FLD_TABLE_DEFINITION, false, TableDefinitionDesc.getInstance(), "TBLE_DEF_");
		objFld.setDisplayField(TableDefinitionDesc.FLD_NAME);
		objFld.setComboBoxCellEditor(TableDefinitionDesc.FLD_NAME);
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setSelectionList(TableDefinitionDesc.getList(FocList.NONE));
		objFld.addListener(getTableDefinitionListener());
		addField(objFld);
		
		objFld = new FObjectField("USER_BROWSE_DEFINITION", "User browse view definition", FLD_USER_BROWSE_VIEW_DEFINITION, false, GuiBrowseDesc.getInstance(), "USER_BROWSE_VIEW_DEFINITION_");
		objFld.setDisplayField(GuiBrowseDesc.FLD_DESCRIPTION);
		objFld.setComboBoxCellEditor(GuiBrowseDesc.FLD_DESCRIPTION);
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setWithList(false);
		addField(objFld);
		
		objFld = new FObjectField("USER_DETAILS_DEFINITION", "User details view definition", FLD_USER_DETAILS_VIEW_DEFINITION, false, GuiDetailsDesc.getInstance(), "USER_DETAILS_VIEW_DEFINITION_");
		objFld.setDisplayField(GuiDetailsDesc.FLD_DESCRIPTION);
		objFld.setComboBoxCellEditor(GuiDetailsDesc.FLD_DESCRIPTION);
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setWithList(false);
		addField(objFld);
	}
	
	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
	
	private static FocList list = null;
	public static FocList getList(int mode){
		list = getInstance().getList(list, mode);
		list.setDirectlyEditable(true);
		list.setDirectImpactOnDatabase(false);
		if(list.getListOrder() == null){
			FocListOrder order = new FocListOrder(FLD_NAME);
			list.setListOrder(order);
		}
		return list;		
	}

	
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocDesc focDesc = null;
  
  public static FocDesc getInstance() {
    if (focDesc==null){
      focDesc = new /*XXX*/MenuDefinitionDesc();
    }
    return focDesc;
  }

}
