package b01.foc.desc.field;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;

import b01.foc.Globals;
import b01.foc.IFocDescDeclaration;
import b01.foc.desc.FocDesc;
import b01.foc.desc.FocObject;
import b01.foc.gui.FGComboBox;
import b01.foc.gui.table.cellControler.AbstractCellControler;
import b01.foc.gui.table.cellControler.ComboBoxCellControler;
import b01.foc.property.FMultipleChoiceStringBased;
import b01.foc.property.FProperty;

public class FMultipleChoiceFieldStringBased extends FCharField {
	
	private ArrayList<FMultipleChoiceItem> choicesArray = null;
	private boolean sortItem = true;

	public FMultipleChoiceFieldStringBased(String name, String title, int id, boolean key, int size) {
		super(name, title, id, key, size);
	}
	
	public void dispose(){
		super.dispose();
		if(choicesArray != null){
			choicesArray.clear();
			choicesArray = null;
		}
	}
	
	private int getNextChoiceId(){
		return choicesArray == null ? 1 : choicesArray.size() + 1;
	}
	
	public Iterator<FMultipleChoiceItem> getChoicesIterator(){
		return choicesArray != null ? choicesArray.iterator() : null;
	}
	
	public FProperty newProperty(FocObject masterObj, Object defaultValue){
   return new FMultipleChoiceStringBased(masterObj, getID(), defaultValue != null ? (String)defaultValue : null);
  }

  public FProperty newProperty(FocObject masterObj){
    return newProperty(masterObj, null); 
  }
  
  private ArrayList<FMultipleChoiceItem> getChoicesArray(){
  	if(this.choicesArray == null){
  		this.choicesArray = new ArrayList<FMultipleChoiceItem>();
  	}
  	return this.choicesArray;
  }
  
  public void addChoice(String choice){
  	FMultipleChoiceItem multipleChoiceItem = new FMultipleChoiceItem(getNextChoiceId(), choice);
  	getChoicesArray().add(multipleChoiceItem);
  }
  
  public void removeChoice(String choice){
  	if(choice != null){
	  	FMultipleChoiceItem multipleChoice = null;
	  	boolean found = false;
	  	Iterator<FMultipleChoiceItem> iter = getChoicesIterator();
	  	while(iter != null && iter.hasNext() && !found){
	  		multipleChoice = iter.next();
	  		if(multipleChoice != null){
	  			String title = multipleChoice.getTitle();
	  			if(choice.equals(title)){
	  				found = true;
	  			}
	  		}
	  	}
	  	if(multipleChoice != null){
	  		getChoicesArray().remove(multipleChoice);
	  	}
  	}
  }
  
  public void removeAllChoices(){
  	getChoicesArray().clear();
  }
  
  public void fillWithAllDeclaredFocDesc(){
  	removeAllChoices();
		Iterator<IFocDescDeclaration> iter = Globals.getApp().getFocDescDeclarationIterator();
		while(iter != null && iter.hasNext()){
			IFocDescDeclaration declaration = iter.next();
			if(declaration != null){
				FocDesc focDesc = declaration.getFocDesctiption();
				if(focDesc != null){
					String focDescName = focDesc.getStorageName();
					addChoice(focDescName);
				}
			}
		}
	}
  
  public Component getGuiComponent(FProperty prop){
  	//Iterator<FMultipleChoiceItem> choices = this.getChoicesIterator();
  	Iterator<FMultipleChoiceItem> choices = null;
  	if(prop != null){
  		choices = ((FMultipleChoiceStringBased) prop).getChoiceIterator();
  	}else{
  		choices = this.getChoicesIterator();
  	}
    FGComboBox comboBox = new FGComboBox(choices, isSortItems());
    if(prop != null) comboBox.setProperty(prop);
    return comboBox;
  }
  
  public AbstractCellControler getTableCellEditor(FProperty prop){
		//Iterator<FMultipleChoiceItem> choices = this.getChoicesIterator();
  	Iterator<FMultipleChoiceItem> choices = null;
  	if(prop != null){
  		choices = ((FMultipleChoiceStringBased) prop).getChoiceIterator();
  	}else{
  		choices = this.getChoicesIterator();
  	}
    return new ComboBoxCellControler(choices, isSortItems());
  }
  
  public void setSortItems(boolean sort){
  	this.sortItem = sort;
  }
  
  public boolean isSortItems(){
  	return this.sortItem;
  }

}
