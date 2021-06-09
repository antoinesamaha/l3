package b01.foc.desc.field;

import java.util.HashMap;

import b01.foc.Globals;
import b01.foc.desc.FocDesc;
import b01.foc.desc.FocObject;
import b01.foc.property.FDescPropertyStringBased;
import b01.foc.property.FProperty;

public class FDescFieldStringBased extends FMultipleChoiceFieldStringBased {
	private HashMap<String, FocDesc> focDescMap = null;
	
	public FDescFieldStringBased(String name, String title, int id, boolean key, int size) {
		super(name, title, id, key, size);
	}
	
	public void dispose(){
		super.dispose();
		if(this.focDescMap != null){
			this.focDescMap.clear();
			this.focDescMap = null;
		}
	}
	
  public FProperty newProperty(FocObject masterObj, Object defaultValue){
  	return new FDescPropertyStringBased(masterObj, getID(), (String) (defaultValue == null ? "" : defaultValue));
  }
  
  private HashMap<String, FocDesc> getFocDescMap(){
  	if(this.focDescMap == null){
  		this.focDescMap = new HashMap<String, FocDesc>();
  	}
  	return this.focDescMap;
  }
  
  public void putChoice(String focDescName){
  	addChoice(focDescName);
  }
  
  public FocDesc getFocDesc(String focDescName){
  	FocDesc focDesc = getFocDescMap().get(focDescName);
  	if(focDesc == null){
  		focDesc = Globals.getApp().getFocDescByName(focDescName);
  		getFocDescMap().put(focDescName, focDesc);
  	}
  	return focDesc;
  }

}
