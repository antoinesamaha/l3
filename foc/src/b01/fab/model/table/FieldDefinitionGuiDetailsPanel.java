package b01.fab.model.table;

import java.awt.GridBagConstraints;
import b01.fab.FocApplicationBuilder;
import b01.fab.model.filter.UserDefinedFilter;
import b01.foc.desc.FocDesc;
import b01.foc.desc.FocFieldEnum;
import b01.foc.desc.FocObject;
import b01.foc.desc.field.FDescFieldStringBased;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FObjectField;
import b01.foc.gui.FGAbstractComboBox;
import b01.foc.gui.FGComboBox;
import b01.foc.gui.FGLabel;
import b01.foc.gui.FGObjectComboBox;
import b01.foc.gui.FGTextField;
import b01.foc.gui.FPanel;
import b01.foc.property.FMultipleChoice;
import b01.foc.property.FObject;
import b01.foc.property.FProperty;
import b01.foc.property.FPropertyListener;

@SuppressWarnings("serial")
public class FieldDefinitionGuiDetailsPanel extends FPanel {
	private FieldDefinition fieldDefinition = null;
	
	private FPanel fObjectFieldPanel = null;
	private FPanel fListFieldPanel = null;
	private FGTextField keyPrefixTextField = null;
	private FGAbstractComboBox fGFocDescComboBox = null;
	//private FGComboBox listFieldIdComboBox = null;
	private FGComboBox cellEditorFieldIdComboBox = null;
	private FGObjectComboBox filtersComboBox = null;
	private FGAbstractComboBox slaveFocDescComboBox = null;
	private FGComboBox foreignKeyComboBox = null;
	private FGTextField formulaTxtField = null;
	private FProperty focDescNameProperty = null;
	
	private FObject filterProperty = null;
	private boolean allowEdit = false;
	
	
	public FieldDefinitionGuiDetailsPanel(FocObject fieldDefinition, int viewID){
		super("Field definition", FPanel.FILL_BOTH);
		this.fieldDefinition = (FieldDefinition)fieldDefinition;
		allowEdit = viewID != FocApplicationBuilder.VIEW_NO_EDIT;
		
		plugListenerToSlaveFocDescProperty();
		plugListenerToFocDescNameProperty();
		
		FDescFieldStringBased descField = (FDescFieldStringBased) this.fieldDefinition.getThisFocDesc().getFieldByID(FieldDefinitionDesc.FLD_FOC_DESC);
		descField.fillWithAllDeclaredFocDesc();
		
		descField = (FDescFieldStringBased) this.fieldDefinition.getThisFocDesc().getFieldByID(FieldDefinitionDesc.FLD_SLAVE_DESC);
		descField.fillWithAllDeclaredFocDesc();
		
		int y = 0;
		
		formulaTxtField = (FGTextField) this.fieldDefinition.getGuiComponent(FieldDefinitionDesc.FLD_FORMULA);
		formulaTxtField.setColumns(40);
		formulaTxtField.setEnabled(allowEdit);
		add("Formula", formulaTxtField, 0, y++);
		
		FPanel fObjectFieldPanel = getFObjectFieldPanel();
		add(fObjectFieldPanel, 0, y++, 2, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE);
		
		FPanel fListFieldPanel = getFListFieldPanel();
		add(fListFieldPanel, 0, y++, 2, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE);
		
		plugListenerToSQLTypeProperty();
	}
	
	private FPanel getFObjectFieldPanel(){
		if(fObjectFieldPanel == null){
			this.fObjectFieldPanel = new FPanel();
			
			this.fObjectFieldPanel.add(new FGLabel("Key Prefix"), 0, 0, GridBagConstraints.WEST);
			keyPrefixTextField = (FGTextField) this.fieldDefinition.getGuiComponent(FieldDefinitionDesc.FLD_KEY_PREFIX);
			keyPrefixTextField.setEnabled(allowEdit);
			this.fObjectFieldPanel.add(keyPrefixTextField, 1, 0, GridBagConstraints.WEST);
			
			
			this.fObjectFieldPanel.add(new FGLabel("Table"), 0, 1, GridBagConstraints.WEST);
			this.fGFocDescComboBox = (FGAbstractComboBox) fieldDefinition.getGuiComponent(FieldDefinitionDesc.FLD_FOC_DESC);
			focDescNameProperty = this.fieldDefinition.getFocProperty(FieldDefinitionDesc.FLD_FOC_DESC);
			focDescNameProperty.addListener(getFocDescComboBoxListener());
			String fieldName = fieldDefinition.getFocDescName();
			this.fGFocDescComboBox.setSelectedItem(fieldName);
			this.fGFocDescComboBox.setEnabled(allowEdit);
			this.fObjectFieldPanel.add(fGFocDescComboBox, 1, 1);
			
			this.fObjectFieldPanel.add(new FGLabel("Cell editor field"), 0, 2, GridBagConstraints.WEST);
			cellEditorFieldIdComboBox = (FGComboBox)this.fieldDefinition.getGuiComponent(FieldDefinitionDesc.FLD_COMBO_BOX_CELL_EDITOR_FIELD_ID);
			cellEditorFieldIdComboBox.setEnabled(allowEdit);
			this.fObjectFieldPanel.add(cellEditorFieldIdComboBox, 1, 2, GridBagConstraints.WEST);
			
			this.fObjectFieldPanel.add(new FGLabel("Filter"), 0, 4, GridBagConstraints.WEST);
			//this.fObjectFieldPanel.add(getFiltersComboBox(), 1, 4, GridBagConstraints.WEST);
			addFilterComboBoxToFObjectFieldPanel(getFiltersComboBox(), this.fObjectFieldPanel);
		}
		
		return this.fObjectFieldPanel;
	}
	
	private void addFilterComboBoxToFObjectFieldPanel(FGObjectComboBox filtersComboBox, FPanel fObjectFieldPanel){
		fObjectFieldPanel.add(filtersComboBox, 1, 4, GridBagConstraints.WEST);
	}
	
	private FPanel getFListFieldPanel(){
		fListFieldPanel = new FPanel();
		
		fListFieldPanel.add(new FGLabel("Slave table"), 0, 0, GridBagConstraints.WEST);
		slaveFocDescComboBox = (FGAbstractComboBox) this.fieldDefinition.getGuiComponent(FieldDefinitionDesc.FLD_SLAVE_DESC);
		slaveFocDescComboBox.setEnabled(allowEdit);
		fListFieldPanel.add(slaveFocDescComboBox, 1, 0, GridBagConstraints.WEST);
		
		fListFieldPanel.add(new FGLabel("Foreign key in slave"), 0, 1, GridBagConstraints.WEST);
		foreignKeyComboBox = (FGComboBox) this.fieldDefinition.getGuiComponent(FieldDefinitionDesc.FLD_UNIQUE_FOREIGN_KEY);
		foreignKeyComboBox.setEnabled(allowEdit);
		fListFieldPanel.add(foreignKeyComboBox, 1, 1, GridBagConstraints.WEST);
		
		return fListFieldPanel;
	}
	
	private FPropertyListener SQLTypePropertyListener = null;
	private FPropertyListener getSQLTypePropertyListener(){
		if(SQLTypePropertyListener == null){
			SQLTypePropertyListener = new FPropertyListener(){
				public void propertyModified(FProperty property) {
					FMultipleChoice multipleChoice = (FMultipleChoice)property;
					int sqlType = multipleChoice.getInteger();
					if(fGFocDescComboBox != null){
						fGFocDescComboBox.setEnabled(sqlType == FieldDefinition.TYPE_ID_FOBJECT_FIELD && allowEdit);
					}
					if(filtersComboBox != null){
						filtersComboBox.setEnabled(sqlType == FieldDefinition.TYPE_ID_FOBJECT_FIELD && allowEdit);
					}
					if(cellEditorFieldIdComboBox != null){
						cellEditorFieldIdComboBox.setEnabled(sqlType == FieldDefinition.TYPE_ID_FOBJECT_FIELD && allowEdit);
					}

					if(keyPrefixTextField != null){
						keyPrefixTextField.setEnabled(sqlType == FieldDefinition.TYPE_ID_FOBJECT_FIELD && allowEdit);
					}
					
					if(slaveFocDescComboBox != null){
						slaveFocDescComboBox.setEnabled(sqlType == FieldDefinition.TYPE_ID_FLIST_FIELD && allowEdit);
					}
					
					if(foreignKeyComboBox != null){
						foreignKeyComboBox.setEnabled(sqlType == FieldDefinition.TYPE_ID_FLIST_FIELD && allowEdit);
					}
					
					if(formulaTxtField != null){
						formulaTxtField.setEnabled( sqlType != FieldDefinition.TYPE_ID_FOBJECT_FIELD && sqlType != FieldDefinition.TYPE_ID_FLIST_FIELD && allowEdit);
					}
					
					if(fieldDefinition != null){
						fieldDefinition.adjustPropertiesEnability();
					}
				}
				
				public void dispose() {
				}
			};
		}
		return SQLTypePropertyListener;
	}
	
	private void plugListenerToSQLTypeProperty(){
		if(fieldDefinition != null){
			FProperty SQLTypeProperty = fieldDefinition.getFocProperty(FieldDefinitionDesc.FLD_SQL_TYPE);
			if(SQLTypeProperty != null){
				SQLTypeProperty.addListener(getSQLTypePropertyListener());
				getSQLTypePropertyListener().propertyModified(fieldDefinition.getFocProperty(FieldDefinitionDesc.FLD_SQL_TYPE));
			}
		}
	}
	
	private FPropertyListener focDescComboBoxListener = null;
	private FPropertyListener getFocDescComboBoxListener(){
		if(focDescComboBoxListener == null){
			focDescComboBoxListener = new FPropertyListener(){
				public void propertyModified(FProperty property) {
					FPanel fObjectFieldPanel = getFObjectFieldPanel();
					if(fObjectFieldPanel != null){
						fObjectFieldPanel.setVisible(false);
						if(filtersComboBox != null){
							filtersComboBox.setVisible(false);
							fObjectFieldPanel.remove(filtersComboBox);
						}
						filtersComboBox = reconstructFiltersComboBox();
						addFilterComboBoxToFObjectFieldPanel(filtersComboBox, fObjectFieldPanel);
						filtersComboBox.setVisible(true);
						fObjectFieldPanel.setVisible(true);
					}
				}

				public void dispose() {
				}
			};
		}
		return focDescComboBoxListener;
	}
	
	/**
	 * @return
	 */
	private FGObjectComboBox getFiltersComboBox(){
		if(filtersComboBox == null){
			reconstructFiltersComboBox();
		}
		return filtersComboBox;
	}
	
	private FGObjectComboBox reconstructFiltersComboBox(){
		removeFilterPropertyListenerAndDisposeFilterProperty();
		FObjectField objFld = new FObjectField("FILTER_LIST", "Filter list", FieldDefinitionDesc.FLD_FILTER_LIST, false, fieldDefinition.getFilterFocDesc(), "FILTER_LIST_");
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setDisplayField(FField.FLD_NAME);
		objFld.setComboBoxCellEditor(FField.FLD_NAME);
		filterProperty = (FObject)objFld.newProperty(null);
		filterProperty.setFocField(objFld);
		filtersComboBox = (FGObjectComboBox)objFld.getGuiComponent(filterProperty);
		filtersComboBox.setEnabled(allowEdit);
		filterProperty.addListener(newFilterPropertyListener());
		adjustFilterComboBoxSelectedItemAccordinglyToFilterRef();
		return filtersComboBox;
	}
	
	private FPropertyListener filterPropertyListener = null;
	private FPropertyListener newFilterPropertyListener(){
		filterPropertyListener = new FPropertyListener(){
			public void propertyModified(FProperty property) {
				FObject filterProp = (FObject)property;
				UserDefinedFilter userDefinedFilter = (UserDefinedFilter)filterProp.getObject();
				if(userDefinedFilter != null){
					int ref = userDefinedFilter.getReference().getInteger();
					fieldDefinition.setFilterRef(ref);
				}
			}
			
			public void dispose() {
			}

			
		};
		return filterPropertyListener;
	}
	
	private void adjustFilterComboBoxSelectedItemAccordinglyToFilterRef(){
		if(fieldDefinition.getSQLType() == FieldDefinition.TYPE_ID_FOBJECT_FIELD){
			UserDefinedFilter filter = fieldDefinition.getUserDefinedFilter();
			if(filter != null){
				filterProperty.setObject(filter);
			}
		}
	}
	
	private FPropertyListener slaveFocDescListener = null;
	private FPropertyListener getSlaveFocDescListener(){
		if(slaveFocDescListener == null){
			slaveFocDescListener = new FPropertyListener(){

				public void propertyModified(FProperty property) {
					if(property != null){
						FieldDefinition fieldDefinition = (FieldDefinition) property.getFocObject();
						if(fieldDefinition != null && fieldDefinition.getSQLType() == FieldDefinition.TYPE_ID_FLIST_FIELD){
							FocDesc slaveFocDesc = fieldDefinition.getSlaveFocDesc();
							if(slaveFocDesc != null){
								FMultipleChoice foreignKeyProp = (FMultipleChoice) fieldDefinition.getFocProperty(FieldDefinitionDesc.FLD_UNIQUE_FOREIGN_KEY);
								if(foreignKeyProp != null){
									fillMultipleChoicePropWithFocDescFields(foreignKeyProp, slaveFocDesc);
								}
							}
						}
						if(foreignKeyComboBox != null){
							foreignKeyComboBox.refillChoices();
						}
					}
					
				}
				
				public void dispose() {
				}
			};
		}
		return slaveFocDescListener;
	}
	
	private void plugListenerToSlaveFocDescProperty(){
		if(fieldDefinition != null){
			FProperty slaveFocDescProperty = fieldDefinition.getFocProperty(FieldDefinitionDesc.FLD_SLAVE_DESC);
			if(slaveFocDescProperty != null){
				slaveFocDescProperty.addListener(getSlaveFocDescListener());
				getSlaveFocDescListener().propertyModified(fieldDefinition.getFocProperty(FieldDefinitionDesc.FLD_SLAVE_DESC));
			}
		}
	}
	
	private FPropertyListener focDescNamePropertyListener = null;
	private FPropertyListener getFocDescNamePropertyListener(){
		if(focDescNamePropertyListener == null){
			focDescNamePropertyListener = new FPropertyListener(){
				public void propertyModified(FProperty property) {
					if(property != null){
						FieldDefinition fieldDefinition = (FieldDefinition) property.getFocObject();
						if(fieldDefinition != null && fieldDefinition.getSQLType() == FieldDefinition.TYPE_ID_FOBJECT_FIELD){
							FocDesc focDesc = fieldDefinition.getFocDesc();
							if(focDesc != null){
								FMultipleChoice celEditorFieldIdProp = (FMultipleChoice) fieldDefinition.getFocProperty(FieldDefinitionDesc.FLD_COMBO_BOX_CELL_EDITOR_FIELD_ID);
								if(celEditorFieldIdProp != null){
									fillMultipleChoicePropWithFocDescFields(celEditorFieldIdProp, focDesc);
								}
							}
						}
						if(cellEditorFieldIdComboBox != null){
							cellEditorFieldIdComboBox.refillChoices();
						}
					}
				}
				
				public void dispose() {
				}
			};
		}
		return focDescNamePropertyListener;
	}
	
	private void plugListenerToFocDescNameProperty(){
		if(fieldDefinition != null){
			FProperty focDescNameProp = fieldDefinition.getFocProperty(FieldDefinitionDesc.FLD_FOC_DESC);
			if(focDescNameProp != null){
				focDescNameProp.addListener(getFocDescNamePropertyListener());
				getFocDescNamePropertyListener().propertyModified(fieldDefinition.getFocProperty(FieldDefinitionDesc.FLD_FOC_DESC));
			}
		}
	}
	
	private void fillMultipleChoicePropWithFocDescFields(FMultipleChoice multipleChoiceProp, FocDesc focDesc){
		if(multipleChoiceProp != null && focDesc != null){
			multipleChoiceProp.resetLocalSourceList();
			FocFieldEnum enumeration = focDesc.newFocFieldEnum(FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
			while(enumeration != null && enumeration.hasNext()){
				FField field = enumeration.nextField();
				if(field != null){
					boolean addField = field.getID() > 0;
					if(addField){
						FField fieldFromThisFocDescWithSameId = focDesc.getFieldByID(field.getID());//we make like this cause if we have (FInLineFieldObject or FTypedFieldObject) the enumeration returns the fields of this fields so we have to chek if the returned field are field from this desc or from the desc of the (FInLineFieldObject or FTypedFieldObject) fields before adding them to the multiple choice
						addField = field == fieldFromThisFocDescWithSameId;
					}
					if(addField){
						multipleChoiceProp.addLocalChoice(field.getID(), field.getTitle());
					}
				}
			}
		}
	}
	
	private void removeFilterPropertyListenerAndDisposeFilterProperty(){
		if(filterProperty != null){
			if(filterPropertyListener != null){
				filterProperty.removeListener(filterPropertyListener);
			}
			filterProperty.dispose();
			filterProperty = null;
		}
		filterProperty = null;
	}
	
	public void dispose(){
		super.dispose();
		if(fGFocDescComboBox != null){
			fGFocDescComboBox.dispose();
			fGFocDescComboBox = null;
		}
		
		/*if(filterProperty != null){
			if(filterPropertyListener != null){
				filterProperty.removeListener(filterPropertyListener);
			}
			filterProperty.dispose();
			filterProperty = null;
		}
		filterProperty = null;*/
		removeFilterPropertyListenerAndDisposeFilterProperty();
		
		cellEditorFieldIdComboBox = null;
		
		if(this.fObjectFieldPanel != null){
			this.fObjectFieldPanel.dispose();
			this.fObjectFieldPanel = null;
		}
		
		if(this.fListFieldPanel != null){
			this.fListFieldPanel.dispose();
			this.fListFieldPanel = null;
		}
		
		if(slaveFocDescComboBox != null){
			slaveFocDescComboBox.dispose();
			slaveFocDescComboBox = null;
		}
		
		if(foreignKeyComboBox != null){
			foreignKeyComboBox.dispose();
			foreignKeyComboBox = null;
		}
		
		if(SQLTypePropertyListener != null && fieldDefinition != null){
			FProperty sqlTypeProp = fieldDefinition.getFocProperty(FieldDefinitionDesc.FLD_SQL_TYPE);
			if(sqlTypeProp != null){
				sqlTypeProp.removeListener(SQLTypePropertyListener);
			}
			SQLTypePropertyListener.dispose();
			SQLTypePropertyListener = null;
		}
		
		if(slaveFocDescListener != null && fieldDefinition != null){
			FProperty slaveFocDescProp = fieldDefinition.getFocProperty(FieldDefinitionDesc.FLD_SLAVE_DESC);
			if(slaveFocDescProp != null){
				slaveFocDescProp.removeListener(slaveFocDescListener);
			}
			slaveFocDescListener.dispose();
			slaveFocDescListener = null;
		}
		
		if(focDescNamePropertyListener != null && fieldDefinition != null){
			FProperty focDescProp = fieldDefinition.getFocProperty(FieldDefinitionDesc.FLD_FOC_DESC);
			if(focDescProp != null){
				focDescProp.removeListener(focDescNamePropertyListener);
			}
			focDescNamePropertyListener.dispose();
			focDescNamePropertyListener = null;
		}
		
		if(keyPrefixTextField != null){
			keyPrefixTextField.dispose();
			keyPrefixTextField = null;
		}
		if(focDescNameProperty != null){
			if(focDescComboBoxListener != null){
				focDescNameProperty.removeListener(focDescComboBoxListener);
				focDescComboBoxListener = null;
			}
			focDescNameProperty = null;
		}
		this.fieldDefinition = null;
		if(filtersComboBox != null){
			filtersComboBox.dispose();
			filtersComboBox = null;
		}
	}
}
