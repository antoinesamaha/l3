/*
 * Created on 15 fvr. 2004
 */
package b01.foc.property;

import b01.foc.Globals;
import b01.foc.access.AccessSubject;
import b01.foc.desc.*;
import b01.foc.desc.field.*;
import b01.foc.gui.*;
import b01.foc.list.*;

import java.awt.Component;
import java.text.Format;
import java.util.*;

/**
 * @author 01Barmaja
 */
public class FObject extends FProperty {
  private FReference localReference = null;
  private FocObject focObjValue = null;
  private boolean focObjValueLocalyConstructed = false;
  private FocObject backupObject = null;
  
  private FocList backupLocalSourceList = null;
  private int displayField = FField.NO_FIELD_ID;
  private FocList localSourceList = null;
  private FocDesc focDesc = null;

  private FPropertyListener valueObjectReferenceListener = null;   
  
  private InternalFrame internalFrame = null;
  private FPropertyListener localRefListener = null; 
  
  private String nullValueDisplayString = null; 
  
  private void init(FocObject focObj, int displayField, FocList localSourceList) {
    localReference = new FReference(null);
    localRefListener = new FPropertyListener(){
      public void propertyModified(FProperty property) {
        reactToLocalReferenceModification();
        notifyListeners();
      }
      public void dispose() {
      }
    };
    localReference.addListener(localRefListener);
    
    this.displayField = displayField;
    this.localSourceList = localSourceList;    
    this.setObject(focObj);
  }
  
  public FObject(FocObject fatherObj, int fieldID, FocObject focObj, int displayField) {
    super(fatherObj, fieldID);
    init(focObj, displayField, null);    
  }

  public FObject(FocObject fatherObj, int fieldID, FocObject focObj, int displayField, FocList localSourceList) {
    super(fatherObj, fieldID);
    init(focObj, displayField, localSourceList);    
  }

  public FObject(FocObject fatherObj, int fieldID, FocObject focObj) {
    super(fatherObj, fieldID);
    init(focObj, FField.NO_FIELD_ID, null);
  }

  public void dispose(){
    if(((FObjectField)getFocField()).getFocDesc().getStorageName().equals("production_job")){
      int debug =0;
    }
    unplugListenerToReferencePropertyOfObjectValue();
    if(valueObjectReferenceListener != null){      
      valueObjectReferenceListener = null;
    }

    super.dispose();

    if(localReference != null){
      localReference.removeListener(localRefListener);
      localReference.dispose();
      localReference = null;
    }

    disposeLocallyConstructedObject(focObjValue);
    focObjValue = null;
    backupObject = null;
    
    backupLocalSourceList = null;
    localSourceList = null;
    focDesc = null;
      
    internalFrame = null;    
  }
  
  private void disposeLocallyConstructedObject(FocObject locallyConstructedObject){
    if(focObjValueLocalyConstructed && locallyConstructedObject != null){
      locallyConstructedObject.dispose();
      locallyConstructedObject = null;
    }
    focObjValueLocalyConstructed = false;    
  }
  
  public void setNullValueDisplayString(String string){
  	this.nullValueDisplayString = string;
  }
  
  public String getNullValueDisplayString(){
  	String str = this.nullValueDisplayString;
  	if(str == null){
  		FObjectField fld = (FObjectField) getFocField();
  		if(fld != null){
  			str = fld.getNullValueDisplayString();
  		}
  	}
  	return str;
  }
  
  public boolean isEmpty(){
    return focObjValue == null && localReference.getInteger() <= 0;
  }
  
  public boolean isWithList(){
    return getPropertySourceList() != null;
  }
  
  private FPropertyListener getValueObjectReferenceListener(){
    if(valueObjectReferenceListener == null){
      valueObjectReferenceListener = new FPropertyListener(){
        public void propertyModified(FProperty property) {
          boolean valueModified = copyReferenceFromObject();
          if(valueModified){
            getFocObject().setModified(true);
          }
        }

        public void dispose() {
        }
      };
    }
    return valueObjectReferenceListener;
  }
  
  private void plugListenerToReferencePropertyOfObjectValue(){
    if(focObjValue != null){
      FReference refProp = focObjValue.getReference();
      if(refProp != null){
        refProp.addListener(getValueObjectReferenceListener());
      }
    }
  }

  private void unplugListenerToReferencePropertyOfObjectValue(){
    if(focObjValue != null){
      FReference refProp = focObjValue.getReference();
      if(refProp != null){
        refProp.removeListener(getValueObjectReferenceListener());
      }
    }
  }
  
  public FocDesc getFocDesc(){
    FocDesc desc = focDesc;
    if(desc == null){
      FField fld = getFocField();
      if(fld != null){
      	desc = fld.getFocDesc();
      }
    }
    return desc;
  }

  public void setFocDesc(FocDesc focDesc){
    this.focDesc = focDesc;
  }

  public String getLocalReferenceToString(){
    return localReference != null ? localReference.toString() : "";
  }

  public int getLocalReferenceInt(){
    return localReference != null ? localReference.getInteger() : 0;
  }
  
  private void copyReferenceIntoObject(){
    FReference ref = focObjValue != null ? focObjValue.getReference() : null;
    if(ref != null){
      int objRefInt = ref.getInteger();
      int localRefInt = localReference.getInteger(); 
      if(localRefInt != objRefInt){
        ref.setReferenceWithoutNotification(localReference.getReferenceClone());
        focObjValue.setLoadedFromDB(false);
      }
    }
  }

  private boolean copyReferenceFromObject(){
    boolean valueModified = false;
    FocRef valueObjRef = null;
    
    if(focObjValue != null){
      FReference ref = focObjValue.getReference();
      if(ref != null){
        valueObjRef = ref.getReferenceClone();
      }
    }
        
    if(localReference != null){
      if((valueObjRef == null && localReference.getInteger() != 0) || (valueObjRef != null && localReference.getInteger() != valueObjRef.getInt())){
        valueModified = true;
      }
      localReference.setReferenceWithoutNotification(valueObjRef);
    }
    return valueModified;
  }
  
  private void getSimilarObjectFromList(){
    if(isWithList()){
      FocList focList = this.getPropertySourceList();
      //Attention
      focList.loadIfNotLoadedFromDB();
      //Attention
      FocObject focObjFromList = focList.searchByReference(localReference.getInteger());
      
      if(focObjFromList == null){
        if(focObjValue != null){
          focObjFromList = focList.searchByUniqueKey(focObjValue);
        }
      }
      
      if(focObjFromList != null && focObjFromList != focObjValue){
        setObject(focObjFromList);
      }
    }
  }
  
  /*private void getSimilarObjectFromList(){
    if(focObjValue == null || focObjValue.isDbResident()){
      if(isWithList()){
        FocList focList = this.getPropertySourceList();
        //Attention
        focList.loadIfNotLoadedFromDB();
        //Attention
        FocObject focObjFromList = focList.searchByReference(localReference.getInteger());
        if(focObjFromList != null && focObjFromList != focObjValue){
          setObject(focObjFromList);
        }
      }
    }
  }*/

  protected void newObject(){
    FocConstructor constr = new FocConstructor(getFocDesc(), null, getFocObject());
    unplugListenerToReferencePropertyOfObjectValue();
    focObjValue = constr.newItem();
    focObjValueLocalyConstructed = true;
    localReference.setFocObject(focObjValue);
    copyReferenceIntoObject();
    FReference objRef = focObjValue.getReference();
    if(objRef != null && objRef.getInteger() == 0){
      focObjValue.setCreated(true);
      if(focObjValue.getMasterObject() != null){
        focObjValue.getMasterObject().setModified(true);
        //focObjValue.getMasterObject().getMasterObject().setModified(true);
      }
      //getFocObject().setModified(true);
    }
    plugListenerToReferencePropertyOfObjectValue();
  }    
  
  private FocObject getFocObjValue(boolean createIfNeeded){
    if(createIfNeeded && focObjValue == null){
      //I realy create only if we are not in ALLOW_NULL_VALUE or if the localReference != 0  
      boolean allowNullVal = getNullValueMode() == FObjectField.NULL_VALUE_ALLOWED || getNullValueMode() == FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN;
      if(!allowNullVal || localReference.getInteger() != 0){
        getSimilarObjectFromList();
        if(focObjValue == null && isWithList()){
          FocList focList = this.getPropertySourceList();
          FocObject obj = focList.getAnyItem();
          if(obj != null){
            setObject(obj);
          }
        }
        if(focObjValue == null){
          newObject();
        }
      }
    }
    return focObjValue;
  }

  private void reactToLocalReferenceModification(){
    if(focObjValue != null){
      FReference fRef = focObjValue.getReference();
      /*if(fRef != null && fRef.getInteger() != localReference.getInteger()){*/
        if(isWithList()){
          getSimilarObjectFromList();
        }else{
          copyReferenceIntoObject();
        }
      //}
    }
  }
  
  public FocObject getObject_CreateIfNeeded(){
    return getFocObjValue(true);
  }
  
  public FocObject getFocObjValue(){
    return focObjValue;
  }
  
  public int getDisplayField(){
    int res = displayField;
    if(res == FField.NO_FIELD_ID){
      FObjectField objFld = (FObjectField) getFocField();
      res = objFld.getDisplayField();
    }
    return res;
  }
  
  public int hashCode() {
    return focObjValue != null ? focObjValue.hashCode() : 0;
  }

  public int compareTo(FProperty prop) {
    int compare = 1;
    if (prop != null) {
      try{
        String thisTableDisp = (String) this.getTableDisplayObject(null);
        String otherTableDisp = (String) prop.getTableDisplayObject(null);
        compare = thisTableDisp.compareTo(otherTableDisp);
      }catch(Exception e){
        this.getTableDisplayObject(null);
        Globals.logException(e);
      }
    }
    return compare;
  }

  public void popupSelectionPanel(FocList propertySourceList) {
    FObjectField objectField = (FObjectField) this.getFocField();

    if (objectField != null) {      

      if (propertySourceList != null) {
        boolean createInternalFrame = true;
        
        if(internalFrame != null){
          createInternalFrame = !Globals.getDisplayManager().restoreInternalFrame(internalFrame);
        }
        if(createInternalFrame){
          propertySourceList.setSelectionProperty(this);
          FAbstractListPanel selPanel = propertySourceList.getSelectionPanel(false);
          /*          
          FValidationPanel validPanel = selPanel.getValidationPanel();
          if(validPanel != null){
            validPanel.addSubject(propertySourceList);
            validPanel.setValidationType(FValidationPanel.VALIDATION_OK_CANCEL);
          }
          */         
          internalFrame = selPanel.popup(getObject_CreateIfNeeded());
        }
        /*
        InternalListListener spl = getInternalListListener();
        selPanel.getFocList().addFocListener(spl);
        */
      }
    }
  }
  
  public void popupSelectionPanel() {
    popupSelectionPanel(this.getPropertySourceList());
  }

  public String getString() {
    String str = "";
    boolean valueFound = false;

    getObject_CreateIfNeeded();
    
    if (focObjValue != null) {
      FProperty displayProperty = focObjValue.getFocProperty(getDisplayField());
      if (displayProperty != null) {
        str = displayProperty.getString();
        valueFound = true;
      }
    }
    if (!valueFound) {
      FocDesc focDesc = focObjValue != null ? focObjValue.getThisFocDesc() : null;
      if(focDesc != null){
        Iterator fieldsEnum = focDesc.newFocFieldEnum(FocFieldEnum.CAT_KEY, FocFieldEnum.LEVEL_PLAIN);
        while (fieldsEnum.hasNext()) {
          FField keyField = (FField) fieldsEnum.next();
          FProperty objProp = focObjValue.getFocProperty(keyField.getID());
          str += objProp.getSqlString();
        }
      }
    }
    return str;
  }

  public void setString(String str) {

  }

  public void setInteger(int iVal) {

  }

  public int getInteger() {
    return 0;
  }

  public void setDouble(double dVal) {
  }

  public double getDouble() {
    return 0;
  }

  public void setObjectToNullWithoutLocalReferenceModification() {
    unplugListenerToReferencePropertyOfObjectValue();
    focObjValue = null;
    localReference.setFocObject(null);
  }
  
  public void setObject(Object obj) {
  	/*
  	obj : is the new object to be set
  	focObjValue : previous object already in property
  	localReference : is a Reference property that contains the ref of the object even if this one is null because not loaded

		focObjValue and localReference should be kept synchronized as long as we do not load uselessly focObjValue,
		this is ensured by:
		1- Listener to the Reference property of the focObjValue, so when it changes, we change the localReference
		2- If the object focObjValue itself changes, we copy its reference to localReference and redirect the listener (1) to the new reference property of the focObjValue 
  	
    setObject cases:
    
    A- General case : focObjValue != null and has a reference != 0 and obj also.
    unplug the listener to the previous object reference property
    set the focObjValue value
    set the localReference
    plug the listener to the new objeect reference
    
    notify listeners.

  	  
  	*/
  	FocObject objectBackedUpLocally = focObjValue;
    if(focObjValue != obj || (focObjValue == null && obj == null && localReference.getInteger() != 0)){
      //The second part of the condition is used for setting the property Object value to null if
      //it has a reference even if it is not loaded into memory yet.(in database but not in memory)
    	FocObject newFocObj = (FocObject) obj;
    	boolean objectModifiedButReferenceNotModified = false;
  		if(focObjValue == null){
  			objectModifiedButReferenceNotModified = newFocObj != null && (getFocDesc() == null || !getFocDesc().getWithReference() || newFocObj.getReference().getInteger() == 0);
  		}else if(focObjValue.isCreated()){
  			objectModifiedButReferenceNotModified = newFocObj == null || newFocObj.getReference().getInteger() == 0;
  		}
    	//}
    	unplugListenerToReferencePropertyOfObjectValue();
      focObjValue = (FocObject) obj;
      localReference.setFocObject(focObjValue);
      plugListenerToReferencePropertyOfObjectValue();
      
      boolean refModified = copyReferenceFromObject();
      //Here maybe I need to listen to the Object reference modification
      getSimilarObjectFromList();//Only if needed
      if(refModified || objectModifiedButReferenceNotModified){
        notifyListeners();
      }
      setInherited(false);
      
      disposeLocallyConstructedObject(objectBackedUpLocally);
    }
  }

  public Object getObject() {
    return (Object) focObjValue;
  }

  public void backup() {
    backupObject = focObjValue;
    backupLocalSourceList = localSourceList;
  }

  public void restore() {
    unplugListenerToReferencePropertyOfObjectValue();
    focObjValue = backupObject;
    localReference.setFocObject(focObjValue);
    plugListenerToReferencePropertyOfObjectValue();
    localSourceList = backupLocalSourceList;
  }

  /**
   * @return
   */
  public FocList getPropertySourceList(){
    FocList propertySourceList = this.localSourceList;
    if (propertySourceList == null) {
      FObjectField objectField = (FObjectField) getFocField();
      if (objectField != null) {
      	if(objectField.getID() == FField.FLD_FATHER_NODE_FIELD_ID){
      		AccessSubject accessSubjectFather = getFocObject() != null ? getFocObject().getFatherSubject() : null;
      		if(accessSubjectFather != null && accessSubjectFather instanceof FocList){
      			propertySourceList = (FocList) accessSubjectFather;
      			this.localSourceList = propertySourceList; 
      		}
      	}else{
      		propertySourceList = objectField.getSelectionList();
      	}
      }
    }
    return propertySourceList;
  }

  //BElie
  public Object getTableDisplayObject(Format format) {
    Object displaObj = this;

    getObject_CreateIfNeeded();
    
    if(focObjValue == null){
      if(getNullValueMode() == FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN){
        displaObj = getNullValueDisplayString();
      }
      if(!((FObjectField)getFocField()).isDisplayNullValues()){
        Object tempObj = getFocObject().getFirstAncestorTableDisplayObject(getFocField().getID(), format);
        if(tempObj != null){
          displaObj = tempObj;  
        }else{
          displaObj = "";
        }
      }
    }else{
      FProperty displayProperty = focObjValue.getFocProperty(getDisplayField());
      if (displayProperty != null) {
        displaObj = displayProperty.getTableDisplayObject(null);
      }
    }
    
    return displaObj;
  }
  //EElie
  
  public void setTableDisplayObject(Object obj, Format format) {
    FocList list = this.getPropertySourceList();

    String strObj = (String)obj;
    
    if(strObj != null){
      //if(strObj.compareTo(FGObjectComboBox.NONE_CHOICE) == 0){
    	if(strObj.compareTo(getNullValueDisplayString()) == 0){
        setObject(null);
      }else if (list != null) {
        for (int i = 0; i < list.size(); i++) {
          FocObject focObj = (FocObject) list.getFocObject(i);
          if (focObj != null) {
            FProperty displayProperty = focObj.getFocProperty(getDisplayField());
            
            if (displayProperty != null && displayProperty.getString().compareTo((String) obj) == 0) {
              setObject(focObj);
              break;
            }
          }
        }
      }
    }
  }

  /**
   * @param localSourceList
   *          The localSourceList to set.
   */
  public void setLocalSourceList(FocList localSourceList) {
    this.localSourceList = localSourceList;
  }
  
  public boolean isObjectProperty(){
    return true;
  }
  
  public void copy(FProperty sourceProp){
    FObject sourceObjProp = (FObject)sourceProp;
    if(sourceObjProp != null){
	    FocList objList = getPropertySourceList();
	    if(objList != sourceObjProp.getPropertySourceList()){
	      if(objList != null){
	        FocObject tarObj = objList.searchByUniqueKey((FocObject)sourceObjProp.getObject_CreateIfNeeded());
	        //FocObject tarObj = objList.findObject((FocObject)sourceObjProp.getObject());
	        if(tarObj != null){
	          setObject(tarObj);
	        }
	      }
	    }else{
	      this.setObject(sourceObjProp.getObject_CreateIfNeeded());      
	    }
    }
  }
  
  public FProperty getFocProperty(int fldId){
    FProperty prop = null;
    
    if(fldId == FField.REF_FIELD_ID){
      prop = localReference;
      /*
      FocObject focObj = (FocObject) getObject();
      FReference fRef = focObj != null ? focObj.getReference() : null;
      if(fRef != null && prop.getInteger() != fRef.getInteger() && fRef.getInteger() != 0){
        prop.copy(fRef);
      }
      */
    }else{
      FocObject obj = getObject_CreateIfNeeded();
      if(obj != null){
        prop = obj.getFocProperty(fldId);
      }
    }

    return prop;
  }
  /*
  public static class InternalListListener implements FocListener {
    public void focActionPerformed(FocEvent event) {
      if (event.getID() == FocEvent.ID_ITEM_SELECT) {
        FocList list = (FocList) event.getSource();
        FocObject selectedObj = list.getSelectedObject();
        //setObject(selectedObj);
      }
    }
  }

  private static InternalListListener internalListListener = null;
  
  public static InternalListListener getInternalListListener(){
    if(internalListListener == null){
      internalListListener = new InternalListListener(); 
    }
    return internalListListener;
  }
  */
  
  public int getNullValueMode() {
    int b = FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN;
    FObjectField objFld = (FObjectField) getFocField();
    if(objFld != null){
      b = objFld.getNullValueMode();
    }
    return b;
  }
  
  public Component getGuiComponent_Panel(){
    FObjectField field = (FObjectField)this.getFocField();
    Component comp = field != null ? field.getGuiComponent_Panel(this) : null;
    adaptGuiComponentEnableAttribute(comp);
    return comp;
  }

  public Component getGuiComponent_ComboBox(){
    FObjectField field = (FObjectField)this.getFocField();
    Component comp = field != null ? field.getGuiComponent_ComboBox(this) : null;
    adaptGuiComponentEnableAttribute(comp);
    return comp;
  }

  public Component getGuiComponent_MultiColumnComboBox(FProperty prop){
    FObjectField field = (FObjectField)this.getFocField();
    Component comp = field != null ? field.getGuiComponent_MultiColumnComboBox(this) : null;
    adaptGuiComponentEnableAttribute(comp);
    return comp;
  }

}