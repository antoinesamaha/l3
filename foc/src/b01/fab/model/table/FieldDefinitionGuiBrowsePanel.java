package b01.fab.model.table;

import java.awt.GridBagConstraints;
import java.util.Iterator;

import b01.fab.FocApplicationBuilder;
import b01.foc.Globals;
import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FField;
import b01.foc.event.FocEvent;
import b01.foc.event.FocListener;
import b01.foc.gui.FGCurrentItemPanel;
import b01.foc.gui.FListPanel;
import b01.foc.gui.FPanel;
import b01.foc.gui.table.FTableView;
import b01.foc.list.FocList;
import b01.foc.property.FProperty;
import b01.foc.property.FPropertyListener;

@SuppressWarnings("serial")
public class FieldDefinitionGuiBrowsePanel extends FListPanel {
	private FocList fieldDefinitionList = null;
	
	public FieldDefinitionGuiBrowsePanel(FocList list, int viewID){
		super("Fields defintion", FPanel.FILL_BOTH);
		boolean allowEdit = viewID != FocApplicationBuilder.VIEW_NO_EDIT;
		FocDesc desc = FieldDefinitionDesc.getInstance();

    if (desc != null) {
      if(list == null){
      	list = FieldDefinitionDesc.getList(FocList.FORCE_RELOAD);
      }else{
      	list.loadIfNotLoadedFromDB();
      }
      if (list != null) {
      	this.fieldDefinitionList = list;
      	try{
      		setFocList(list);
      	}catch(Exception e){
      		Globals.logException(e);
      	}
        FTableView tableView = getTableView();       
        tableView.setDetailPanelViewID(0);
        
        tableView.addColumn(desc, FieldDefinitionDesc.FLD_ID, 10, allowEdit);
        tableView.addColumn(desc, FieldDefinitionDesc.FLD_NAME, 30, allowEdit);
        tableView.addColumn(desc, FieldDefinitionDesc.FLD_DB_RESIDENT, 30, allowEdit);
        tableView.addColumn(desc, FieldDefinitionDesc.FLD_TITLE, 30, allowEdit);
        tableView.addColumn(desc, FieldDefinitionDesc.FLD_SQL_TYPE, 20, allowEdit);
        tableView.addColumn(desc, FieldDefinitionDesc.FLD_IS_KEY, 10, allowEdit);
        tableView.addColumn(desc, FieldDefinitionDesc.FLD_LENGTH, 10, allowEdit);
        tableView.addColumn(desc, FieldDefinitionDesc.FLD_DECIMALS, 10, allowEdit);
        construct();
        
        FGCurrentItemPanel currPanel = new FGCurrentItemPanel(this, viewID); 
        add(currPanel, 0, 3, 1, 1, GridBagConstraints.SOUTH, GridBagConstraints.NONE);
        
        requestFocusOnCurrentItem();
        showEditButton(false);
        showDuplicateButton(false);
        showAddButton(allowEdit);
        showRemoveButton(allowEdit);
        if(allowEdit){
        	plugListenerToFieldDefinitionList();
        	plugListenersToAllFieldDefinition();
        }
      }
    }
	}
	
	public void dispose(){
		unplugListenersToAllFieldDefinition();
		if(this.fieldDefinitionList != null){
			if(fieldDefinitionListListener != null){
				fieldDefinitionList.removeFocListener(fieldDefinitionListListener);
			}
			this.fieldDefinitionList = null;
		}
		super.dispose();
	}
	
	private FPropertyListener namePropertyListener = null;
	private FPropertyListener getNamePropertyListener(){
		if(namePropertyListener == null){
			namePropertyListener = new FPropertyListener(){
				public void propertyModified(FProperty property) {
					FieldDefinition fieldDefinition = (FieldDefinition) property.getFocObject();
					//if(fieldDefinition != null && fieldDefinition.getTitle().equals(FieldDefinition.FIELD_TITLE_NOT_SET_YET)){
						String name = fieldDefinition.getName();
						if(name != null && name.length() > 0){
							name  = name.replace("_", " ");
							StringBuffer title = new StringBuffer(name.toLowerCase());
							title.setCharAt(0, Character.toUpperCase(title.charAt(0)));
							fieldDefinition.setTitle(String.valueOf(title));
						}
					//}
				}

				public void dispose() {
				}
			};
		}
		return namePropertyListener;
	}
	
	private FPropertyListener SQLTypePropertyListener = null;
	private FPropertyListener getSQLTypePropertyListener(){
		if(SQLTypePropertyListener == null){
			SQLTypePropertyListener = new FPropertyListener(){
				public void propertyModified(FProperty property) {
					FieldDefinition fieldDefinition = (FieldDefinition) property.getFocObject();
					int SQLType = fieldDefinition.getSQLType();
					if(SQLType == FieldDefinition.SQL_TYPE_ID_VARCHAR){
						fieldDefinition.setLength(20);
						fieldDefinition.setDecimals(0);
					}else if(SQLType == FieldDefinition.SQL_TYPE_ID_INT){
						fieldDefinition.setLength(3);
						fieldDefinition.setDecimals(0);
					}else if(SQLType == FieldDefinition.SQL_TYPE_ID_DOUBLE){
						fieldDefinition.setLength(5);
						fieldDefinition.setDecimals(3);
					}else if(SQLType == FieldDefinition.SQL_TYPE_ID_LONG){
						fieldDefinition.setLength(3);
						fieldDefinition.setDecimals(0);
					}else if(SQLType == FieldDefinition.SQL_TYPE_ID_BOOLEAN){
						fieldDefinition.setLength(0);
						fieldDefinition.setDecimals(0);
					}else if(SQLType == FieldDefinition.SQL_TYPE_ID_DATE){
						fieldDefinition.setLength(0);
						fieldDefinition.setDecimals(0);
					}else if(SQLType == FieldDefinition.SQL_TYPE_ID_TIME){
						fieldDefinition.setLength(0);
						fieldDefinition.setDecimals(0);
					}else if(SQLType == FieldDefinition.TYPE_ID_FOBJECT_FIELD){
						fieldDefinition.setLength(0);
						fieldDefinition.setDecimals(0);
					}
				}
				public void dispose() {
				}
			};
		}
		return SQLTypePropertyListener;
	}
	
	private FocListener fieldDefinitionListListener = null;
	private FocListener getFieldDefinitionListListener(){
		if(fieldDefinitionListListener == null){
			fieldDefinitionListListener = new FocListener(){

				public void focActionPerformed(FocEvent evt) {
					if(evt.getID() == FocEvent.ID_ITEM_ADD){
						FieldDefinition definition = (FieldDefinition) evt.getEventSubject();
						if(definition != null){
							if(definition.getID() == FField.NO_FIELD_ID){
								TableDefinition tableDefinition = definition.getTableDefinition();
								int maxFieldId = tableDefinition.getMaxFieldDefinitionId();
								definition.setID(maxFieldId + 1);
							}
							plugListenersToFieldDefinition(definition);
						}
					}
				}
				
				public void dispose() {
				}
			};
		}
		return fieldDefinitionListListener;
	}
	
	@SuppressWarnings("unchecked")
	private void unplugListenersToAllFieldDefinition(){
		/*if(fieldDefinitionList != null){
			Iterator<FieldDefinition> iter = fieldDefinitionList.focObjectIterator();
			while(iter != null && iter.hasNext()){
				FieldDefinition definition = iter.next();
				if(definition != null){
					unplugListenersFromFocObject(definition);
				}
			}
		}*/
		plugUnplugListenersToAllFieldDefinition(false);
	}
	
	private void plugListenersToAllFieldDefinition(){
		plugUnplugListenersToAllFieldDefinition(true);
	}
	
	@SuppressWarnings("unchecked")
	private void plugUnplugListenersToAllFieldDefinition(boolean plug){
		if(fieldDefinitionList != null){
			Iterator<FieldDefinition> iter = fieldDefinitionList.focObjectIterator();
			while(iter != null && iter.hasNext()){
				FieldDefinition definition = iter.next();
				if(definition != null){
					if(plug){
						plugListenersToFieldDefinition(definition);
					}else{
						unplugListenersFromFocObject(definition);
					}
				}
			}
		}
	}
	
	private void plugUnplugListenersToFieldDefinition(FieldDefinition fieldDefinition, boolean plug){
		if(fieldDefinition != null){
			FProperty prop = fieldDefinition.getFocProperty(FieldDefinitionDesc.FLD_NAME);
			if(prop != null){
				if(plug){
					prop.addListener(getNamePropertyListener());
				}else{
					prop.removeListener(namePropertyListener);
				}
			}
			prop = fieldDefinition.getFocProperty(FieldDefinitionDesc.FLD_SQL_TYPE);
			if(prop != null){
				if(plug){
					prop.addListener(getSQLTypePropertyListener());
				}else{
					prop.removeListener(SQLTypePropertyListener);
				}
			}
			
		}
	}
	
	private void plugListenersToFieldDefinition(FieldDefinition fieldDefinition){
		plugUnplugListenersToFieldDefinition(fieldDefinition, true);
	}
	
	private void unplugListenersFromFocObject(FieldDefinition fieldDefinition){
		plugUnplugListenersToFieldDefinition(fieldDefinition, false);
	}
	
	private void plugListenerToFieldDefinitionList(){
		if(fieldDefinitionList != null){
			//fieldDefinitionList.removeFocListener(getFieldDefinitionListListener());
			fieldDefinitionList.addFocListener(getFieldDefinitionListListener());
		}
	}
}
