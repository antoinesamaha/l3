package b01.foc.gui;

import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JComboBox;

import b01.foc.Globals;
import b01.foc.IFocDescDeclaration;
import b01.foc.desc.FocDesc;

@SuppressWarnings("serial")
public class FGFocDescsComboBox extends FGAbstractComboBox {
	
	private HashMap<String , FocDesc> focDescMap = null;
	
	public FGFocDescsComboBox(){
		Iterator<IFocDescDeclaration> iter = Globals.getApp().getFocDescDeclarationIterator();
		while(iter != null && iter.hasNext()){
			IFocDescDeclaration iFocDescDeclaration = iter.next();
			if(iFocDescDeclaration != null){
				FocDesc focDesc = iFocDescDeclaration.getFocDesctiption();
				if(focDesc != null){
					String name = focDesc.getStorageName();
					addItem(name); 
					putFocDesc(name, focDesc);
				}
			}
		}
	}
	
	/*public FGFocDescsComboBox(){
		Iterator<IFocDescDeclaration> iter = Globals.getApp().getFocDescDeclarationIterator();
		HashMap<String, FocDesc> tempHashMap = new HashMap<String, FocDesc>();
		ArrayList<String> tempArrayList = new ArrayList<String>();
		while(iter != null && iter.hasNext()){
			IFocDescDeclaration iFocDescDeclaration = iter.next();
			if(iFocDescDeclaration != null){
				FocDesc focDesc = iFocDescDeclaration.getFocDesctiption();
				if(focDesc != null){
					String name = focDesc.getStorageName();
					addItem(name);
					tempArrayList.add(name);
					tempHashMap.put(name, focDesc);
				}
			}
		}
		
		Collections.so
		
	}*/
	
	private HashMap<String, FocDesc> getFocDescMap(){
		if(this.focDescMap == null){
			this.focDescMap = new HashMap<String, FocDesc>();
		}
		return this.focDescMap;
	}
	
	private void putFocDesc(String focDescName, FocDesc focDesc){
		if(focDescName != null && focDesc != null){
			getFocDescMap().put(focDescName, focDesc);
		}
	}
	
	public FocDesc getSelectedFocDesc(){
		FocDesc selectedFocDesc = null;
		String focDescName = (String)getSelectedItem();
		if(focDescName != null){
			selectedFocDesc = getFocDescMap().get(focDescName);
		}
		return selectedFocDesc;
	}
	
	public void dispose(){
		if(this.focDescMap != null){
			this.focDescMap.clear();
			this.focDescMap = null;
		}
	}

	@Override
	protected String getPropertyStringValue() {
		return (String) getSelectedItem();
	}

	@Override
	protected void setPropertyStringValue(String strValue) {
	}
	
}
