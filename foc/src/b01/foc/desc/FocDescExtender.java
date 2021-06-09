package b01.foc.desc;

import java.util.ArrayList;
import java.util.Iterator;

public class FocDescExtender {
	private ArrayList<IFocDescExtension> extensionArray = null;
	
	private ArrayList<IFocDescExtension> getExtensionArray(){
		if(extensionArray == null){
			extensionArray = new ArrayList<IFocDescExtension>();
		}
		return extensionArray;
	}
	
	public void addExtension(IFocDescExtension extension){
		getExtensionArray().add(extension);
	}
	
	public void removeExtension(IFocDescExtension extension){
		getExtensionArray().remove(extension);
	}
	
	private Iterator<IFocDescExtension> getExtensionIterator(){
		return getExtensionArray().iterator();
	}
	
	public void extendFocDesc(FocDesc focDesc){
		Iterator<IFocDescExtension> iter = getExtensionIterator();
		while(iter != null && iter.hasNext()){
			IFocDescExtension extension = iter.next();
			extension.extendFocDesc(focDesc);
		}
	}

}
