package b01.foc.property;

import java.util.ArrayList;
import java.util.Iterator;
import b01.foc.desc.FocObject;
import b01.foc.desc.field.FMultipleChoiceFieldStringBased;
import b01.foc.desc.field.FMultipleChoiceItem;

public class FMultipleChoiceStringBased extends FString implements IFMultipleChoiceProperty {
	
	private ArrayList<FMultipleChoiceItem> localChoicesArray = null;
	
	public FMultipleChoiceStringBased(FocObject focObj, int fieldID, String str) {
		super(focObj, fieldID, str);
	}
	
	public void dispose(){
		super.dispose();
		if(this.localChoicesArray != null){
			this.localChoicesArray.clear();
			this.localChoicesArray = null;
		}
	}
	
	private int getNextChoiceId(){
		return localChoicesArray == null ? 1 : localChoicesArray.size() + 1;
	}
	
	private ArrayList<FMultipleChoiceItem> getLocalChoicedArray(){
		if(localChoicesArray == null){
			resetLocalSourceList();
		}
		return localChoicesArray;
	}
	
	public void resetLocalSourceList(){
		this.localChoicesArray = new ArrayList<FMultipleChoiceItem>();
	}
	
	public void addLocalChoice(String choice){
  	FMultipleChoiceItem multipleChoiceItem = new FMultipleChoiceItem(getNextChoiceId(), choice);
  	getLocalChoicedArray().add(multipleChoiceItem);
	}
	
	private Iterator<FMultipleChoiceItem> getLocalChoicesIterator() {
    Iterator<FMultipleChoiceItem> iter = null;
    if (localChoicesArray != null) {
        iter = localChoicesArray.iterator();
    }
    return iter;
  }
	
	public Iterator<FMultipleChoiceItem> getChoiceIterator(){
    Iterator<FMultipleChoiceItem> iter = getLocalChoicesIterator();
    if(iter == null){
	    FMultipleChoiceFieldStringBased field = (FMultipleChoiceFieldStringBased) this.getFocField();
	    if (field != null) {
	      iter = field.getChoicesIterator();
	    }
    }
    return iter;
  }
}
