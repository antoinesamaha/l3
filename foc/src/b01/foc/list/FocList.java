// DISPLAY FILTER
// ARRAY
// LIST
// ACCESS
// DATABASE
// ELEMENT HASH

/*
 * Created on Oct 14, 2004
 */
package b01.foc.list;

import b01.foc.*;
import b01.foc.property.*;
import b01.foc.util.IFocIterator;
import b01.foc.db.*;
import b01.foc.desc.*;
import b01.foc.desc.field.FField;
import b01.foc.event.*;
import b01.foc.gui.*;
import b01.foc.gui.table.*;
import b01.foc.access.*;

import java.util.*;

import javax.swing.JOptionPane;

/**
 * @author 01Barmaja
 */
public class FocList extends AccessSubject {
  
  public static final int NONE = 0;
  public static final int LOAD_IF_NEEDED = 1;
  public static final int FORCE_RELOAD = 2;
  
  private HashMap<FocObject, FocListElement> elements      = null;
  private HashMap<Integer, FocListElement>   elementsByRef = null;
  
  private SQLFilter filter = null;
  private ArrayList<FocListener> listeners = null;
  private boolean sleepListeners = false; 
  private FAbstractListPanel selectionPanel = null;
  private FAbstractListPanel attachedListPanel = null;//Gets the attached list panel 
  private int viewIDForSelectionBrowse = 0;
  private boolean loaded = false;
  private int nextTempReference = 1;
  private FocListOrder listOrder = null;
  private FocListListener focListListener = null; 
  private FObject selectionProperty = null;
  private FocList listRequestingTheSelection = null;

  //rr
  private boolean withDeprecation = false;
  
  // Image of the real list.
  // It is set to null when modified
  // and constructed automatically when needed.
  private ArrayList<FocListElement> arrayList = null;

  private FocObject masterObject = null;
  private HashMap<Integer, FocObject> foreignObjectsMap = null;//THE KEYS ARE THE FIELD IDS IN THE SLAVE DESC
  private FocLink focLink = null;
  private boolean directImpactOnDatabase = false;
  private boolean directlyEditable = false;
  private boolean waitForValidationToAddObject = true;
  private boolean keepNewLineFocusUntilValidation = true;
  private FList fatherProperty = null;// When the FocList is in a property this
                                      // is used to notify the proprty of
                                      // modifications

  private Object validityCheckObject = null;
  private boolean collectionBehaviour = false;
  private boolean disableReSortAfterAdd = false;
  private int DEMO_MAX_SIZE = -1;
  
  
  // oooooooooooooooooooooooooooooooooo
  // MAIN
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public void init(FocObject masterObject, FocLink focLink, SQLFilter filter) {
    elementHash_Init();       
    this.focLink = focLink;
    this.masterObject = masterObject;
    this.filter = filter;

    if (this.filter == null) {
      this.filter = new SQLFilter(null, 0);
    }

    this.focLink.adaptSQLFilter(this, this.filter);
  }
  
  private void initForForeignKeyLink(HashMap<Integer, FocObject> foreignObjectsMap, FocLinkForeignKey focLinkForeignKey, SQLFilter filter){
    setForeignObjectsMap(foreignObjectsMap);
    if(this.foreignObjectsMap.size() == 1){
      Iterator iter = this.foreignObjectsMap.keySet().iterator();
      if(iter != null && iter.hasNext()){
        int key = (Integer)iter.next();
        FocObject uniqueForeignObject = foreignObjectsMap.get(key);
        uniqueForeignObject.setTransactionalWithChildren(focLinkForeignKey.isTransactionalWithChildren());
        setFatherSubject(uniqueForeignObject);
      }
    }
    init(null,focLinkForeignKey,filter);
  }

  /**
   * This constructor is mainly for simple lists used to store some objects We
   * do not need any link or filter, neether a master object
   */
  public FocList(FocLink focLink) {
    super(Globals.getDefaultAccessControl());
    init(null, focLink, null);
  }

  public FocList(FocObject masterObject, FocLink focLink, SQLFilter filter) {
    super(Globals.getDefaultAccessControl());
    if(FocLinkForeignKey.class.isInstance(focLink)){
      HashMap<Integer, FocObject> foreignMap = new HashMap<Integer, FocObject>();
      FocLinkForeignKey focLinkFK = (FocLinkForeignKey) focLink;
      int key = focLinkFK.getUniqueForeignKeyFieldID();
      if(key != -1){
        foreignMap.put(key, masterObject);
        initForForeignKeyLink(foreignMap, focLinkFK, filter);
      }
    }else{
      init(masterObject, focLink, filter);
    }
  }
  
  public FocList(HashMap<Integer, FocObject> foreignObjectsMap, FocLinkForeignKey focLinkForeignKey, SQLFilter filter){
    super(Globals.getDefaultAccessControl());
    initForForeignKeyLink(foreignObjectsMap, focLinkForeignKey, filter);
  }
  
  public FocList(FocLink focLink, SQLFilter filter) {
    super(Globals.getDefaultAccessControl());
    init(null, focLink, filter);
  }
  
  public void dispose(){
    //elementHash_Dispose();
    
    if(filter != null){
      filter.dispose();
      filter = null;
    }
    
    if(listeners != null){
      for(int i=0; i<listeners.size(); i++){
        FocListener listener = (FocListener)listeners.get(i);
        if(listener != null){
          listener.dispose();
          listener = null;
        }
      }
      listeners.clear();
      listeners = null;
    }

    if(selectionPanel != null){
      selectionPanel.dispose();
      selectionPanel = null;
    }

    if(attachedListPanel != null){
      attachedListPanel.dispose();
      attachedListPanel = null;
    }

    if(listOrder != null){
      //listOrder.dispose();
      listOrder = null;
    }

    if(focListListener != null){
      focListListener.dispose();
      focListListener = null;
    }
    
    selectionProperty = null;
    listRequestingTheSelection = null;

    if(arrayList != null){
      arrayList.clear();
      arrayList = null;
    }
    
    masterObject = null;
    foreignObjectsMap = null; //We dont dispose the FocObjects in the foreignObjectsMap
    
    focLink = null;
    fatherProperty = null;
    validityCheckObject = null;
    elementHash_Dispose();
    
    
    //BElias put the subjects in a temporary array then travers this array and dispose the subjects
    //cuase if we dispose them with the first method(the newSubjectIterator) we get a concurent modification exception.
    /*Iterator iter = newSubjectIterator();
    while(iter != null && iter.hasNext()){
      AccessSubject subject = (AccessSubject) iter.next();
      if (subject != null && subject instanceof FocObject) {
      	subject.dispose();
      }
    }*/
    ArrayList<AccessSubject> temporaryArrayList = new ArrayList<AccessSubject>();
    Iterator iter = newSubjectIterator();
    while(iter != null && iter.hasNext()){
      AccessSubject subject = (AccessSubject) iter.next();
      if (subject != null && subject instanceof FocObject) {
      	temporaryArrayList.add(subject);
      }
    }
    for(int i = 0; i < temporaryArrayList.size(); i++){
    	AccessSubject subject = temporaryArrayList.get(i);
    	if(subject != null){
    		subject.dispose();
    	}
    }
    //EElias
    super.dispose();
  }
  
  private void setForeignObjectsMap(HashMap<Integer, FocObject> foreignObjects){
    this.foreignObjectsMap = foreignObjects;
  }
  
  //BElias
  //Should be private and have only a public funtion that returns an iterator on the keys
  //EELias
  public HashMap<Integer, FocObject> getForeignObjectsMap(){
    return this.foreignObjectsMap;
  }
  
  public FocObject getSingleTableDisplayObject(){
    return (focLink != null) ? focLink.getSingleTableDisplayObject(this) : null;
  }
  
  public boolean isDatabaseResident() {
    boolean isDBResident = false;
    FocDesc desc = getFocDesc();
    if (desc != null) {
      isDBResident = desc.getDBResident();
    }
    return isDBResident;
  }

  /**
   * @return
   */
  public FocDesc getFocDesc() {
    return (focLink != null) ? focLink.getSlaveDesc() : null;
  }
  
  protected void setFocLink(FocLink focLink){
  	this.focLink = focLink;
  }
  
  protected FocLink getFocLink(){
  	return this.focLink;
  }

  public boolean isContentValid(boolean displayMessage){
    /* boolean valid = true;
    Iterator iter = focObjectIterator();
    while(iter != null && iter.hasNext() && valid){
      FocObject obj = (FocObject) iter.next();
      if(!obj.isDeleted()){
        valid = obj.isContentValidWithPropagation();
      }
    }*/

    final class LocalIterObject{
      public boolean valid = true;
    }
    LocalIterObject userObj = new LocalIterObject();
    
    iterate(new FocListIterator(userObj){
      @Override
      public boolean treatElement(FocListElement element, FocObject focObj) {
        LocalIterObject userObj = (LocalIterObject) getObject(0);
        if(!focObj.isDeleted()){
          userObj.valid = focObj.isContentValidWithPropagation();
        }
        return !userObj.valid;
      }
      
    });
    return userObj.valid;
  }

  public void synchronize(FocList srcFocList) {
    if(srcFocList != this){
      // Scan this and remove the items absent from source
      Iterator iterator = focObjectIterator();
      if (iterator != null) {
        while (iterator.hasNext()) {
          FocObject tarObj = (FocObject) iterator.next();
          if (!srcFocList.containsKey(tarObj)) {
            //BElias
            //removeCurrentObjectFromIterator(iterator);
            elementHash_removeCurrentObjectFromIterator(iterator,tarObj);
            //EElias
          }
        }
      }
  
      // Scan the source and push items absent from this
      iterator = null;
      iterator = srcFocList.focObjectIterator();
      if (iterator != null) {
        while (iterator.hasNext()) {
          FocObject srcObj = (FocObject) iterator.next();
          if (!this.containsKey(srcObj)) {
            this.add(srcObj);
          }
        }
      }
    }
  }

  public void copy(FocList sourceList){
    this.setCreated(true);  
    focLink.copy(this, sourceList);
  }
  
  public void addFocListener(FocListener focListener) {
    if (focListener != null) {
      if (listeners == null) {
        listeners = new ArrayList<FocListener>(1);
      }
      if (listeners != null) {
        listeners.add(focListener);
      }
    }
  }

  public void removeFocListener(FocListener focListener) {
    if (focListener != null) {
      if (listeners != null) {
        listeners.remove(focListener);
      }
    }
  }
  
  public void fireEvent(FocObject subject, int id) {
  	if(!isSleepListeners()){
	    FocEvent event = new FocEvent(this, FocEvent.composeId(FocEvent.TYPE_LIST, id), null);
	    event.setEventSubject(subject);
	    if (listeners != null) {
	      for (int i = 0; i < listeners.size(); i++) {
	        FocListener listener = (FocListener) listeners.get(i);
	        if (listener != null) {
	          listener.focActionPerformed(event);
	        }
	      }
	    }
	    if (this.fatherProperty != null) {
	      fatherProperty.notifyListeners();
	    }
  	}
  }
  
  /**
   * @return
   */
  public FAbstractListPanel getSelectionPanel(boolean withSeletionCheckBox) {
    if(selectionPanel != null){
      selectionPanel.dispose();
      selectionPanel = null;      
    }
    FocDesc focDesc = this.getFocDesc();
    if (focDesc != null) {
      selectionPanel = (FAbstractListPanel) focDesc.callNewBrowsePanel(this, viewIDForSelectionBrowse);
      if(selectionPanel != null){
        selectionPanel.requestFocusOnCurrentItem();
      }
    }
    attachedListPanel = selectionPanel; 
    return selectionPanel;
  }  
  
  public FocObject getSelectedObject() {
    FocObject selectedObj = null;
    if (attachedListPanel != null) {
      selectedObj = attachedListPanel.getSelectedObject();
    }
    return selectedObj;
  }

  /**
   * @return
   */
  public SQLFilter getFilter() {
    return filter;
  }
  
  /**
   * @return
   */
  public FocObject getMasterObject() {
    return masterObject;
  }
  
  public FocObject getTheUniqueForeignObject(){
    FocObject uniqueForeignObject = null;
    HashMap<Integer, FocObject> foreignObjectsMap = getForeignObjectsMap();
    if(foreignObjectsMap != null && foreignObjectsMap.size() == 1){
      Iterator<FocObject> iter = foreignObjectsMap.values().iterator();
      if(iter != null && iter.hasNext()){
        uniqueForeignObject = iter.next();
      }
    }
    return uniqueForeignObject;
  }
  
  /*public FocObject getForeignObject(){
    return foreignObject;
  }*/

  /**
   * @return
   */
  public boolean isDirectImpactOnDatabase() {
    return directImpactOnDatabase;
  }

  /**
   * @param b
   */
  public void setDirectImpactOnDatabase(boolean b) {
    directImpactOnDatabase = b;

    Iterator iter = focObjectIterator();
    if (iter != null && iter.hasNext()) {
      FocObject obj = (FocObject) iter.next();
      if (obj != null) {
        obj.forceControler(directImpactOnDatabase);
      }
    }
  }

  /**
   * @return Returns the waitForValidationToAddObject.
   */
  public boolean isWaitForValidationToAddObject() {
    return waitForValidationToAddObject;
  }

  /**
   * @param waitForValidationToAddObject
   *          The waitForValidationToAddObject to set.
   */
  public void setWaitForValidationToAddObject(boolean waitForValidationToAddObject) {
    this.waitForValidationToAddObject = waitForValidationToAddObject;
  }

  public void setSelectionProperty(FObject selectionProperty) {
    this.selectionProperty = selectionProperty;
    if(selectionPanel != null){
      selectionPanel.requestFocusOnCurrentItem();
    }
  }

  public void validateSelectedObject() {
    if (selectionProperty != null) {
      FocObject focObj = getSelectedObject();
      if(focObj != null){
        selectionProperty.setObject(focObj);
        if(listRequestingTheSelection != null && listRequestingTheSelection.selectionPanel != null){
          listRequestingTheSelection.selectionPanel.requestFocusOnCurrentItem();          
        }
      }
    }
  }
  
  public void cancellingSelection(){
    if (selectionProperty != null) {
      selectionProperty.setObject(selectionProperty.getObject());
      if(listRequestingTheSelection != null && listRequestingTheSelection.selectionPanel != null){
        listRequestingTheSelection.selectionPanel.requestFocusOnCurrentItem();          
      }
    }    
  }
  
  /**
   * @return Returns the selectionProperty.
   */
  public FObject getSelectionProperty() {
    return selectionProperty;
  }

  /**
   * @param fatherProperty
   *          The fatherProperty to set.
   */
  public void setFatherProperty(FList fatherProperty) {
    this.fatherProperty = fatherProperty;
  }

  /**
   * @param masterObject
   *          The masterObject to set.
   */
  public void setMasterObject(FocObject masterObject) {
    this.masterObject = masterObject;
  }

  /**
   * @param loaded
   *          The loaded to set.
   */
  public void setLoaded(boolean loaded) {
    this.loaded = loaded;
  }
   
  public void backupAllObjects(){
    Iterator iter = this.focObjectIterator();
    while(iter != null && iter.hasNext()){
      FocObject obj = (FocObject) iter.next();
      obj.backup();
    }
  }
  
  /**
   * @return Returns the listRequestingTheSelection.
   */
  public FocList getListRequestingTheSelection() {
    return listRequestingTheSelection;
  }
  
  /**
   * @param listRequestingTheSelection The listRequestingTheSelection to set.
   */
  public void setListRequestingTheSelection(FocList listRequestingTheSelection) {
    this.listRequestingTheSelection = listRequestingTheSelection;
  }
  /**
   * @return Returns the attachedListPanel.
   */
  public FAbstractListPanel getAttachedListPanel() {
    return attachedListPanel;
  }
  /**
   * @param attachedListPanel The attachedListPanel to set.
   */
  public void setAttachedListPanel(FListPanel attachedListPanel) {
    this.attachedListPanel = attachedListPanel;
  }

  public void removeAttachedListPanel(FListPanel attachedListPanel) {
    if(attachedListPanel == this.attachedListPanel){
      this.attachedListPanel = null;
    }
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // ARRAY
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  private void array_Add(FocListElement focObj) {
    if(focObj != null && arrayList != null){
      arrayList.add(focObj);
    }
  }
  
  private void array_Remove(FocListElement focObj) {
    if(focObj != null && arrayList != null){
      arrayList.remove(focObj);
    }
  }
  
  private ArrayList getArrayList() {
    // if the array list is null we construct it
    // by scaning the elements and putting them in an arrayList.
    // The array List is set to null when a major modification of the real list
    // is made.
    if (arrayList == null) {
      arrayList = new ArrayList<FocListElement>();
      Iterator iterator = listElementIterator();
      while (iterator != null && iterator.hasNext()) {
        FocListElement focObj = (FocListElement) iterator.next();
        if (focObj != null && !focObj.isHide()) {
          array_Add(focObj);
        }
      }
      sort();
    }
    return arrayList;
  }

  public void sort() {
    if (arrayList != null) {
      if (listOrder != null) {
        Collections.sort(arrayList, listOrder);
      } else {
        Collections.sort(arrayList);
      }
    }
  }

  public FocListElement getFocListElement(FocObject focObject) {
    return elementHash_GetFocListElement(focObject);
  }

  public FocListElement getFocListElement(int i) {
    FocListElement retValue = null;
    ArrayList arrayList = getArrayList();
    if (arrayList != null && i<arrayList.size() && i>=0) {
      retValue = (FocListElement) arrayList.get(i);
    }
    return retValue;
  }

  public FocObject getFocObject(int i) {
    FocObject retValue = null;
    FocListElement le = getFocListElement(i);
    if (le != null) {
      retValue = le.getFocObject();
    }
    return retValue;
  }

  public int size() {
    int retValue = 0;
    ArrayList arrayList = getArrayList();
    if (arrayList != null) {
      retValue = arrayList.size();
    }
    return retValue;
  }

  public int getRowForObject(FocObject obj){
    int row = -1;
    for(int i=0; i<size(); i++){
      FocObject focObj = getFocObject(i);
      if(focObj != null && focObj == obj){
        row = i;
        break;
      }
    }
    return row; 
  }
  
  public void elementMoved (int initialPosition, int finalPosition){
    if(finalPosition < size() && initialPosition < size() && finalPosition >= 0 && initialPosition >=0){
      if(initialPosition != finalPosition){
        ArrayList arrayList = getArrayList();
        FocListElement element = (FocListElement)arrayList.get(initialPosition);
        if(initialPosition > finalPosition){
          for (int i = initialPosition; i > finalPosition; i--){
            arrayList.set(i, arrayList.get(i-1));
          }
        }else{
          for(int i = initialPosition; i < finalPosition; i++){
            arrayList.set(i, arrayList.get(i+1));
          }
        }
        arrayList.set(finalPosition, element);
      
        fireEvent(null, FocEvent.ID_ITEM_MODIFY);
      }
    }
  }

  /**
   * @return
   */
  public FocListOrder getListOrder() {
    return listOrder;
  }

  /**
   * @param order
   */
  public void setListOrder(FocListOrder order) {
    listOrder = order;
    sort();
    /*
    if(order != null){
      ListOrderListener orderListener = new ListOrderListener(this);
      focListListener = orderListener.newFocListListener();
    }
    */
  }

  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public boolean isEmpty(){
    return elementHash_isEmpty();
  }

  
  public Iterator focObjectIterator() {
    return elementHash_GetFocObjectIterator();
  }

  public Iterator listElementIterator() {
    return elementHash_GetListElementIterator();
  }
  
  public synchronized boolean iterate(IFocIterator iterator){
    Iterator iter = listElementIterator();
    boolean stop = false;
    while(!stop && iter != null && iter.hasNext()){
      FocListElement element = (FocListElement) iter.next();
      stop = iterator.treatElement(element);
    }
    return stop; 
  }

  /**
   * @param arg0
   * @return
   */
  public boolean containsKey(Object arg0) {//Attention
    return (elements != null) ? elements.containsKey(arg0) : false;
  }
  
  public boolean containsObject(FocObject focObj) {
    boolean exist = false;
    if (elements != null && focObj != null) {
      exist = elements.containsKey(focObj);
    }
    return exist;
  }
  
  
  public FocObject searchByPointer(FocObject obj) {
    FocObject foundObj = null;
    FocListElement foundElmt = (FocListElement) elements.get(obj);
    foundObj = foundElmt != null ? foundElmt.getFocObject() : null;
    /*
    Iterator iter = elements.keySet().iterator();
    while(iter!=null && iter.hasNext()){
      FocObject currObj = (FocObject)iter.next();
    }
    */
    return foundObj;
  }
  
  public FocObject searchByUniqueKey(FocObject obj){
    FocObject foundObj = null;
    Iterator iter = elements.keySet().iterator();
    while(iter!=null && iter.hasNext()){
      FocObject currObj = (FocObject)iter.next();
      if(currObj != null){
        if(obj != null && obj.compareUniqueKey(currObj) == 0){
          foundObj = currObj;
          break;
        }
      }
    }
    return foundObj;
  }

  /*public FocObject searchByReference(int ref){
    FocListElement elm = elementHash_GetFocListElement(ref);    
    return elm != null ? elm.getFocObject() : null; 
  }*/

  public FocListElement searchElementByReference(int ref){
    FocListElement elm = elementHash_GetFocListElement(ref);
    if (elm == null){//Attention la recherche peut etre peut transmis dans elementHash_gtFocListElement(ref)
      Iterator iter = elementHash_GetListElementIterator();
      while (iter.hasNext() && elm == null){
        FocListElement currElem = (FocListElement) iter.next();
        if (currElem.getFocObject().getReference().getInteger() == ref){
          elm = currElem;
        }
      }
    }
    return elm;
  }
  
  public synchronized FocObject searchByReference(int ref){
    FocListElement elm = elementHash_GetFocListElement(ref);
    //Globals.logString("dans search by reference : "+ (elm == null ? elm==null : elm.getFocObject().getReference().getInteger()));
    //Globals.logString("elements.size : " + elements.size() + "elementsByRef.size : " + elementsByRef.size()); 
    FocObject object = elm != null ? elm.getFocObject() : null;
    if (object == null){//Attention la recherche peut etre peut transmis dans elementHash_gtFocListElement(ref)
      Iterator iter = elements.keySet().iterator();
      FocObject searchObject = null;
      while (iter.hasNext()){
        searchObject = (FocObject)iter.next();
        if (searchObject.getReference().getInteger() == ref){
          object = searchObject;
          break;
        }
      }
    }
    //Globals.logString("dans search by reference apres while : "+ (object == null));
    return object;
  }
  
  public FocObject searchByRealReferenceOnly(int ref){
    FocListElement elm = elementHash_GetFocListElement(ref);
    return elm!= null ? elm.getFocObject() : null;
  }
  
  /*public FocObject searchByProperyStringValue(int id, String strValue){
    FocObject foundObj = null;
    if(strValue != null){
      Iterator iter = elements.keySet().iterator();
      while(iter!=null && iter.hasNext()){
        FocObject currObj = (FocObject)iter.next();
        if(currObj != null){
          FProperty prop = currObj.getFocProperty(id);
          
          if(prop != null && prop.getString() != null && prop.getString().compareTo(strValue) == 0){
            foundObj = currObj;
            break;
          }
        }
      }
    }
    return foundObj;
  }*/
  
  public FocObject searchByProperyStringValue(int id, String strValue){
  	return searchByProperyStringValue(id, strValue, false);
  }
  
  public FocObject searchByProperyStringValue(int id, String strValue, boolean ignoreCase){
    FocObject foundObj = null;
    if(strValue != null){
      Iterator iter = elements.keySet().iterator();
      while(iter!=null && iter.hasNext()){
        FocObject currObj = (FocObject)iter.next();
        if(currObj != null){
          FProperty prop = currObj.getFocProperty(id);
          
          if(ignoreCase){
          	if(prop != null && prop.getString() != null && prop.getString().toUpperCase().compareTo(strValue.toUpperCase()) == 0){
	            foundObj = currObj;
	            break;
	          }
          }else{
	          if(prop != null && prop.getString() != null && prop.getString().compareTo(strValue) == 0){
	            foundObj = currObj;
	            break;
	          }
          }
        }
      }
    }
    return foundObj;
  }

  public FocObject searchByPropertyBooleanValue(int id, boolean boolValue){
    FocObject foundObj = null;
    Iterator iter = elements.keySet().iterator();
    while(iter!=null && iter.hasNext()){
      FocObject currObj = (FocObject)iter.next();
      if(currObj != null){
        boolean currBool = currObj.getPropertyBoolean(id);
        if(currBool == boolValue){
          foundObj = currObj;
          break;
        }
      }
    }
    return foundObj;
  }
  
  public FocObject searchByPropertyDateValue(int id, java.sql.Date date){
    FocObject foundObj = null;
    if(date != null){
      Iterator iter = elements.keySet().iterator();
      while(iter!=null && iter.hasNext()){
        FocObject currObj = (FocObject)iter.next();
        if(currObj != null){
          FDate prop = (FDate) currObj.getFocProperty(id);
          if(prop != null && prop.getDate().compareTo(date) == 0){
            foundObj = currObj;
            break;
          }
        }
      }
    }
    return foundObj;
  }
  
  public FocObject searchByPropertyIntValue(int id, int value){
    FocObject foundObj = null;
    Iterator iter = elements.keySet().iterator();
    while(iter!=null && iter.hasNext()){
      FocObject currObj = (FocObject)iter.next();
      if(currObj != null){
        FInt prop = (FInt) currObj.getFocProperty(id);
        if(prop != null && prop.getInteger() == value){
          foundObj = currObj;
          break;
        }
      }
    }
    return foundObj;
  }
  
  public FocObject searchByPropertyObjectValue(int id, FocObject propObjValue){
    FocObject foundObj = null;
    if(propObjValue != null){
      Iterator iter = elements.keySet().iterator();
      while(iter!=null && iter.hasNext()){
        FocObject currObj = (FocObject)iter.next();
        if(currObj != null){
          FProperty prop = currObj.getFocProperty(id);
          if(FObject.class.isInstance(prop)){
            FObject objProp = (FObject) prop;
            objProp.getObject_CreateIfNeeded();
          }
          //B-HASHCODE
          FocObject obj0 = (FocObject)prop.getObject();
          if(prop != null && obj0 != null && obj0.compareTo(propObjValue) == 0){
            //E-HASHCODE            
            foundObj = currObj;
            break;
          }
        }
      }
    }
    return foundObj;
  }
  
  public FocObject searchByPropertiesValue(int id1, Object value1, int id2, Object value2){
    FocObject foundObj = null;
    Iterator iter = elements.keySet().iterator();
    while(iter!=null && iter.hasNext()){
      FocObject currObj = (FocObject)iter.next();
      if(currObj != null){
        FProperty prop = currObj.getFocProperty(id1);
        if(prop != null && prop.getObject().equals( value1)){
        	prop = currObj.getFocProperty(id2);
          if(prop != null && prop.getObject().equals( value2)){
	          foundObj = currObj;
	          break;
          }
        }
      }
    }
    return foundObj;
  }
  
  public synchronized void add(FocObject focObj) {
    //System.out.println("LIST - Adding");
    if (focObj != null) {
      focObj.setTemporaryReferenceIfNeeded(nextTempReference);
      
      if(!isCollectionBehaviour()) focObj.setMasterObject(masterObject);
      
      if (!containsObject(focObj)) {
        FocListElement focListElmt = new FocListElement(focObj, false);
        //elements.put(focObj, focListElmt);
        elementHash_Put(focListElmt);
        array_Add(focListElmt);
        if(!isCollectionBehaviour()){
          if (isDirectImpactOnDatabase()) {
            focObj.forceControler(true);
          }       
          focObj.setFatherSubject(this);
        }
        if(!isDisableReSortAfterAdd()){
          arrayList = null;
        }
        fireEvent(focObj, FocEvent.ID_ITEM_ADD);
      }

      // Keep next temp reference max
      FReference fInt = focObj.getReference();
      if (fInt != null && fInt.getInteger() >= nextTempReference) {
        nextTempReference = fInt.getInteger() + 1;
      }
      // ---------------------------
    }
    /*if(!isDisableReSortAfterAdd()){
      arrayList = null;
    }*/
  }

  public synchronized void remove(FocObject focObj) {//Attention il faut enlever de elementByRefAussi
    
    if (elements != null && focObj != null) {
      //focObj.setFatherSubject(null);
      //Object debugObj = elements.remove(focObj);
      
      FocListElement elementRemoved = elementHash_remove(focObj);
      if(elementRemoved == null){
        Globals.logString("null debug obj");
      }
      //arrayList = null;
      array_Remove(elementRemoved);

      fireEvent(focObj, FocEvent.ID_ITEM_REMOVE);
      if( elementRemoved != null ){
      	if(focObj.getFatherSubject() == null){
          elementRemoved.dispose(false);      		
      	}
      }
    }
  }
  
  public void removeCurrentObjectFromIterator(Iterator iter) {
    iter.remove();
    arrayList = null;
  }
  
  public void elementHash_removeCurrentObjectFromIterator(Iterator iter,FocObject focObj) {
    FocListElement elemToRemove = elementHash_GetFocListElement(focObj);
    if(focObj.hasRealReference()){
      elementsByRef.remove(focObj.getReference().getInteger());
    }
    try{
      iter.remove();
    }catch(Exception e){
      Globals.logException(e);      
    }
    arrayList = null;
    elemToRemove.dispose(isCollectionBehaviour());
    /*if(isCollectionBehaviour()){
      elemToRemove.disposeLeaveFocObject();
    }else{
      elemToRemove.dispose();
    }*/
  }
  
  public synchronized void removeAll() {
    
    if (elements != null && elements.size() > 0) {
      elementHash_DisposeAndClear();      
      arrayList = null;
      fireEvent(null, FocEvent.ID_ITEM_REMOVE);
      setLoaded(false);
    }
  }
    
  public FocObject newItemInternal(FProperty identifier, boolean disconnected) {

    FocDesc desc = getFocDesc();
    
    FocObject newFoc = null;
    if (desc != null && (!isDemo() || isWithinDemoLimit() || disconnected)) {

      FocConstructor constr = new FocConstructor(desc, identifier != null ? identifier.getObject() : null, masterObject);
      newFoc = constr.newItem();
      fillForeignObjectsProperties(newFoc);
      //BElie
      //newFoc.setCreationRevisionData();
      //EElie
      newFoc.setDbResident(isDbResident());      
      
      if(!disconnected){
        if(newFoc.setTemporaryReferenceIfNeeded(nextTempReference)){
          nextTempReference ++;
        }        
        newFoc.setFatherSubject(this);
        if (isDirectImpactOnDatabase()) {
        	newFoc.forceControler(true);
        }       
        newFoc.setCreated(true);       
        
        if(filter != null){
          filter.copy_SelectedFieldsValues_From_Template_To_Object(newFoc);
        }
      }
    }else{
      if( getDEMO_MAX_SIZE() != 0 ){
        JOptionPane.showMessageDialog(Globals.getDisplayManager().getMainFrame(),
            "You can only create up to "+getDEMO_MAX_SIZE()+" "+desc.getStorageName()+" records.",
            "Demo version limitation",
            JOptionPane.ERROR_MESSAGE);  
      }
    }
    
    return newFoc;
  }
  
  private void fillForeignObjectsProperties(FocObject newFocObj){
    if(foreignObjectsMap != null && foreignObjectsMap.size() > 0){
      Iterator iter = foreignObjectsMap.keySet().iterator();
      while(iter != null && iter.hasNext()){
        int key = (Integer)iter.next();
        FocObject foreignObject = foreignObjectsMap.get(key);
        newFocObj.setPropertyObject(key, foreignObject);
      }
    }
  }
  
  public FocObject newItem(FProperty identifier) {
    return newItemInternal(identifier, false);
  }
  
  public FocObject newEmptyItem() {
    FocObject newItem = newItem(null); 
    return newItem;
  }

  public FocObject newEmptyDisconnectedItem() {
    return newItemInternal(null, true);
  }
  
  public FocObject getOrInsertAnItem(){
    FocObject obj = null;
    Iterator iter = focObjectIterator();
    while(iter != null && iter.hasNext()){
      obj = (FocObject) iter.next();
      break;
    }
    if(obj == null){
      obj = newItem(null);
      add(obj);
    }
    return obj;
  }

  public synchronized FocObject getAnyItem(){
    FocObject obj = null;
    Iterator iter = focObjectIterator();
    while(iter != null && iter.hasNext()){
      obj = (FocObject) iter.next();
      break;
    }
    return obj;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // ACCESS
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  protected void statusModification(int statusModified) {
  }
  
  public void childStatusModification(AccessSubject subject) {
    if (subject != null) {
      if (subject.isCreated() && !isWaitForValidationToAddObject()) {
        this.add((FocObject) subject);
      } else if (subject.isDeleted()) {
        this.remove((FocObject) subject);
        if (this.isDirectImpactOnDatabase()) {
          subject.commitStatusToDatabase();
          this.removeSubject(subject);
        }
      }
    }
  }

  public void childStatusUndo(AccessSubject childSubject) {
    if (childSubject.isCreated()) {
      childSubject.setFatherSubject(null); //If I keep this line I get a concurrent modification exception
      childSubject.resetStatus();
      remove((FocObject) childSubject);
    } else if (childSubject.isDeleted()) {
      add((FocObject) childSubject);
    }
  }

  //BDebug
  public void writeDebugInfo(){
    if(true || getFocDesc().getStorageName().compareTo("LOCATION_TYPE") == 0){
      /*
      for(int i=0; i<size(); i++){
        FocObject obj = getFocObject(i);
        String status = "";
        if(obj.isCreated()){
          status = "Created";
        }
        if(obj.isModified()){
          status += "Modified";
        }
        if(obj.isDeleted()){
          status += "Deleted";
        }
        Globals.logString("Array["+i+"] = "+obj.getReference().getString()+" status : "+status);
      }
      */
      /*
      Iterator iter = focObjectIterator();
      while(iter != null && iter.hasNext()){
        FocObject obj = (FocObject) iter.next();
        String status = "";
        if(obj.isCreated()){
          status = "Created";
        }
        if(obj.isModified()){
          status += "Modified";
        }
        if(obj.isDeleted()){
          status += "Deleted";
        }
        Globals.logString("Obj[] = "+obj.getReference().getString()+" status : "+status);
      }
      */
      Iterator iter = newSubjectIterator();
      while(iter != null && iter.hasNext()){
        FocObject obj = (FocObject) iter.next();
        String status = "";
        if(obj.isCreated()){
          status = "Created";
        }
        if(obj.isModified()){
          status += "Modified";
        }
        if(obj.isDeleted()){
          status += "Deleted";
        }
        Globals.logString("Sub[] = "+obj.getReference().getString()+" status : "+status+" " + obj);
      }
    }
  }
  //EDebug

  //BElie
  private int getRevisionVersion(FocObject obj) {
    FProperty prop = getFocDesc().getRevisionPath().getPropertyFromObject(obj);
    return prop != null ? prop.getInteger() : 0; 
  }
  
  public void commitStatusToDatabaseWithPropagation() {
    //writeDebugInfo();
    
    //SCAN THE FOC OBJECTS AND DO YOUR LOGIC OF INSERT DELETE uPDATE 
    //this.
    if (getFocDesc().isRevisionSupportEnabled()){
      ArrayList<FocObject> focObjects = new ArrayList<FocObject>();
      
      Iterator iter = this.newSubjectIterator();
      while(iter != null && iter.hasNext()){
        FocObject focObj = (FocObject)iter.next();
        if(getRevisionVersion(focObj) != focObj.getPropertyInteger(FField.CREATION_REVISION_FIELD_ID)){
          /*if( focObj.isCreated()){
            focObj.resetStatus();
            focObj.setCreationRevisionData();  
          }else*/ if( focObj.isDeleted()){
            focObj.resetStatus();
            focObj.setDeletionRevisionData(); 
          }else if(focObj.isModified()){
            focObjects.add(focObj);  
          }
        }
      }
    
      for(int i = 0; i < focObjects.size(); i++){
        FocObject focObj = focObjects.get(i);
        FocObject newFocObj = this.newEmptyItem();
        newFocObj.copyPropertiesFrom(focObj);
        focObj.restore();
        focObj.setDeletionRevisionData();
        newFocObj.setModificationRevisionData(focObj);
        newFocObj.save();
        focObj.save();
      }
    }
    
    super.commitStatusToDatabaseWithPropagation();
  }
  //EElie
  
  public void commitStatusToDatabase() {
    if (isModified() && !isDirectImpactOnDatabase()) {
      this.saveDB();
    }
  }
  
  public void childValidated(AccessSubject subject) {
    if (subject != null) {
      if (subject.isCreated() && isWaitForValidationToAddObject()) {
        this.add((FocObject) subject);
      }

      if (this.isDirectImpactOnDatabase()) {
        subject.commitStatusToDatabaseWithPropagation();
      }
    }
  }

  public void undoStatus() {
  }

  public void doBackup(){
  }  
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // DATABASE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo  

  public void reloadFromDB() {
    //this.fireEvent(FocEvent.ID_BEFORE_LOAD);
  	if(isDbResident()){
	    boolean backup = false;
	    if(fatherProperty != null && fatherProperty.getFocObject() != null){
	      backup = fatherProperty.getFocObject().isModified();
	    }
	    //setSleepListeners(true);
      //B-LIST-UPDATE-NO-RELOAD
	    //removeAll();
      //E-LIST-UPDATE-NO-RELOAD      
      loaded = focLink.loadDB(this);
	    //fireEvent(null, FocEvent.ID_ITEM_MODIFY);//attention attention
	    if(fatherProperty != null && fatherProperty.getFocObject() != null){
	      fatherProperty.getFocObject().setModified(backup);
	    }
  	}
    //this.fireEvent(FocEvent.ID_LOAD);    
  }

  public void deleteFromDB() {
    loaded = focLink.deleteDB(this);
  }

  public boolean loadIfNotLoadedFromDB() {
    boolean reloaded = false;
    if (!loaded && !this.isCreated()) {
      
      reloadFromDB();
      
      reloaded = true;
      
    }
    return reloaded ;
  }

  public void saveDB() {
    if (focLink != null) {
      focLink.saveDB(this);
      //B-DUP
      setLoaded(true);
      //E-DUP
    }
  }  
  
  public boolean isSleepListeners() {
    return sleepListeners;
  }
  
  public void setSleepListeners(boolean sleepListeners) {
    boolean forceListenerCall = !sleepListeners && this.sleepListeners ;
    this.sleepListeners = sleepListeners;
    if(forceListenerCall){
      fireEvent(null, FocEvent.ID_WAIK_UP_LIST_LISTENERS);
    }
  }
  
  public boolean isDirectlyEditable() {
    return directlyEditable;
  }
  
  public void setDirectlyEditable(boolean directlyEditable) {
    setWaitForValidationToAddObject(!directlyEditable);
    this.directlyEditable = directlyEditable;
  }
  
  public boolean isKeepNewLineFocusUntilValidation(){
    return keepNewLineFocusUntilValidation || (isDirectlyEditable() && isDirectImpactOnDatabase());
  }

  public void setKeepNewLineFocusUntilValidation(boolean keepNewLineFocusUntilValidation){
    this.keepNewLineFocusUntilValidation = keepNewLineFocusUntilValidation;
  }
  
  public void reactToNewSelection(FTable table, FocObject selObj, boolean focusLost){
    if(!table.isEditing() && isDirectImpactOnDatabase() && isDirectlyEditable()){
    	//ATTENTION ATTENTION AntoineS a enleve ca pour les messages inutils dans devTask
      for(int i=0; i<size(); i++){
        FocObject obj = getFocObject(i);
        if((selObj != obj || focusLost) && obj.isCreated() || obj.isModified()){
          if(obj.isContentValidWithPropagation()){
            obj.validate(true);
          }
        }
      }
    }
  }
  
  public Object getValidityCheckObject() {
    return validityCheckObject;
  }
  
  public void setValidityCheckObject(Object validityCheckObject) {
    this.validityCheckObject = validityCheckObject;
  }
  
  public boolean isCollectionBehaviour() {
    return collectionBehaviour;
  }  
  
  public void setCollectionBehaviour(boolean collectionBehaviour) {
    this.collectionBehaviour = collectionBehaviour;
  }
  
  public boolean isDisableReSortAfterAdd() {
    return disableReSortAfterAdd;
  }
  
  public void setDisableReSortAfterAdd(boolean disableReSortAfterAdd) {
    this.disableReSortAfterAdd = disableReSortAfterAdd;
  }
  
  public int getViewIDForSelectionBrowse() {
    return viewIDForSelectionBrowse;
  }
  
  public void setViewIDForSelectionBrowse(int viewIDForSelectionBrowse) {
    this.viewIDForSelectionBrowse = viewIDForSelectionBrowse;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // ELEMENTS
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo  
  
  private void elementHash_Init(){
    elements = new HashMap<FocObject, FocListElement>();
    elementsByRef = new HashMap<Integer, FocListElement>();  
  }
    
  private void elementHash_Dispose(){
    elementHash_DisposeAndClear();
    elements = null;    
    elementsByRef = null;  
    arrayList = null;
  }

  private void elementHash_DisposeAndClear(){
    if(elements != null){
      Iterator iter = listElementIterator();
      while(iter != null && iter.hasNext()){
        FocListElement elem = (FocListElement) iter.next();
        if(elem != null){
          elem.dispose(isCollectionBehaviour());
          /*if(isCollectionBehaviour()){
            elem.disposeLeaveFocObject();      
          }else{
            elem.dispose();
          }*/          
          elem = null;
          //removeCurrentObjectFromIterator(iter);
        }
      }
      elements.clear();
    }
  }
  
  private FocListElement elementHash_GetFocListElement(FocObject focObj){
    return elements.get(focObj);
  }

//  private FocListElement elementHash_GetFocListElementByRef(FocObject focObj){
//    if(focObj.hasRealReference()){
//      return elementsByRef.get(focObj.getReference().getInteger());      
//    }else{
//      return  searchByReference(focObj.getReference().getInteger());
//
//    }
//  }
  
  private FocListElement elementHash_GetFocListElement(int ref){
    return elementsByRef.get(ref);
  } 
  
  private boolean elementHash_isEmpty(){
    return elements == null || elements.size() == 0; 
  }

  private Iterator elementHash_GetFocObjectIterator(){
    Iterator iter = null;
    if(elements != null){
      iter = elements.keySet().iterator();
    }
    return iter; 
  }
  
  private Iterator elementHash_GetListElementIterator(){
    Iterator iter = null;
    if(elements != null){
      iter = elements.values().iterator();
    }
    return iter; 
  }
  
  private void elementHash_Put(FocListElement elem){
    elements.put(elem.getFocObject(), elem);
    if (elem.getFocObject().hasRealReference()){
      elementsByRef.put(elem.getFocObject().getReference().getInteger(), elem);
    }
  }
  
  private FocListElement elementHash_remove(FocObject focObj){
    FocListElement obj = null;
    //printDebug();    
    //Globals.logString("dans elementHash_remove : focObj.hasRealReference = " + focObj.hasRealReference() + "elementByRef.size : "+ elementsByRef.size()+" removing ref="+focObj.getReference().getInteger());
    if (focObj.hasRealReference()){
      obj = elementsByRef.remove(focObj.getReference().getInteger());
    }
    //Globals.logString("elementByRef.size : "+ elementsByRef.size() + (obj == null));
    obj = elements.remove(focObj);
    return obj;
  }
  
  @SuppressWarnings("unused")
	private void printDebug(){
    Iterator iter = elementsByRef.keySet().iterator();
    int i=0;
    Globals.logString("elements by ref:");
    while(iter.hasNext()){
      Integer inte = (Integer) iter.next();
      Globals.logString(" ref["+(i++)+"]= "+inte);
    }
  }

  public boolean isDemo(){
    return DEMO_MAX_SIZE != -1;
  }
  
  public boolean isWithinDemoLimit(){
    return size() < getDEMO_MAX_SIZE();
  }
  
  public int getDEMO_MAX_SIZE() {
    return DEMO_MAX_SIZE;
  }

  public void setDEMO_MAX_SIZE(int demo_max_size) {
    if( Globals.getApp().isDemo() ){
      DEMO_MAX_SIZE = demo_max_size;  
    }
  }

  public boolean isWithDeprecation() {
    return withDeprecation;
  }

  public void setWithDeprecation(boolean withDeprecation) {
    this.withDeprecation = withDeprecation;
  }
}