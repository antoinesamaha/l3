// MAIN
// PANEL
// LIST
// DESCRIPTION

/*
 * Created on Jun 5, 2006
 */
package b01.l3;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import b01.foc.Globals;
import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocDesc;
import b01.foc.desc.FocObject;
import b01.foc.gui.FPanel;
import b01.foc.list.FocList;
import b01.foc.property.FList;
import b01.foc.property.FString;
import b01.l3.data.L3Message;
import b01.l3.exceptions.L3InstrumentNotFoundException;

/**
 * @author 01Barmaja
 */
public class PoolKernel extends FocObject implements Pool, MessageListener{
  private HashMap<String, Instrument> instrumentMap = null;
  private Vector<L3Message> receivedMessageFIFO = null;  
  private HashMap<MessageListener, MessageListener> listenersMap = null;
  private Thread loopForMessageSendingThread = null; 
  private String instrumentsPropertiesDirectory = null;  

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // MAIN
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public PoolKernel(FocConstructor constr) {
    super(constr);
    init(false);
    forceControler(true);
  }
  
  public PoolKernel(){
  	this(true);
  }

  protected PoolKernel(FocDesc focDesc){
  	super(focDesc);
  }
  
  public PoolKernel(boolean loadInstrument){
  	super(getFocDesc());
  	init(loadInstrument);
  }

  public void init(boolean loadInstrument){
  	new FString(this, PoolKernelDesc.FLD_NAME, "");
  	new FList(this, PoolKernelDesc.FLD_INSTRUMENT_LIST, new FocList(this, ((PoolKernelDesc)PoolKernel.getFocDesc()).getPoolInstrumentLink(), null));
    
  	new FString(this, PoolKernelDesc.FLD_SERVICE_HOST, "");
  	
    instrumentMap = new HashMap<String, Instrument>();
    receivedMessageFIFO = new Vector<L3Message>();
    fillInstrumentMapFromDb();
    if(loadInstrument){
    	loadInstrumentsFromPropertyFiles();
    }
  }
  
  private void fillInstrumentMapFromDb(){
  	FocList list = getInstrumentList();
  	//list.loadIfNotLoadedFromDB();
  	list.reloadFromDB();
    for (int i=0;i<list.size();i++){
  		Instrument instr = (Instrument)list.getFocObject(i);
  		//instrumentMap.put(((Instrument)list.getFocObject(i)).getCode(),(Instrument)list.getFocObject(i));
      addInstrument(instr);
    }
  }
  
  public String getName() {
  	FString name = (FString) getFocProperty(PoolKernelDesc.FLD_NAME);
    return (name != null) ? name.getString() :"";
  }
  
  public void dispose(){

  	if(loopForMessageSendingThread != null){
	  	loopForMessageSendingThread.interrupt();
	  	loopForMessageSendingThread = null;
  	}

    if(instrumentMap != null){
      Iterator iter = instrumentMap.values().iterator();
      while(iter != null && iter.hasNext()){
        Instrument instru = (Instrument) iter.next();
        instru.dispose();
      }
      instrumentMap.clear();
      instrumentMap = null;
    }
    
    if(receivedMessageFIFO != null){
      for(int i=0; i<receivedMessageFIFO.size(); i++){
        L3Message message = receivedMessageFIFO.get(i);
        message.dispose();        
      }
      receivedMessageFIFO.clear();
      receivedMessageFIFO = null;
    }

    if(listenersMap != null){
      listenersMap.clear();
      listenersMap = null;
    }
    
  	super.dispose();    
  }

  public FocList getInstrumentList(){
  	FList list = (FList) getFocProperty(PoolKernelDesc.FLD_INSTRUMENT_LIST);
  	FocList focList = list.getList();
  	focList.loadIfNotLoadedFromDB();
  	return focList;
  }
  
  public void postMessageToInstrument(String code, L3Message message) throws Exception{
  	send(code, message);
  };
  
  public void postMessageToLIS(L3Message message){
    if(message != null && receivedMessageFIFO != null){
      receivedMessageFIFO.add(message);
    }
  }  

  public void addMessageListener(MessageListener driverListener){
    if(listenersMap == null){
      listenersMap = new HashMap<MessageListener, MessageListener>();      
    }
    listenersMap.put(driverListener, driverListener);
  }

  public void removeMessageListener(MessageListener driverListener){
    if(listenersMap != null){
      listenersMap.remove(driverListener);
    }
  }
  
  public void notifyMessageListeners(L3Message message){
    if(listenersMap != null){
      Iterator iter = listenersMap.values().iterator();
      while(iter != null && iter.hasNext()){
        MessageListener listener = (MessageListener) iter.next();
        if(listener != null){
          listener.messageReceived(message);
        }
      }    
    }
  }
  
  public void messageReceived(L3Message message){
    notifyMessageListeners(message);
  }
  
  public void addInstrument(Instrument inst){
  	if(instrumentMap != null && inst != null){
  		instrumentMap.put(inst.getCode(), inst);
      inst.addMessageListener(this);
  	}
  }

  public void loadInstrumentsFromPropertyFiles(){
    try{
      File instrDir = new File(getInstrumentsPropertiesDirectory());    
      
      File listOfFiles[] = instrDir.listFiles();
      for(int i=0; listOfFiles != null && i<listOfFiles.length; i++){
        File file = listOfFiles[i];
        if(file.isFile()){
          if(file.getPath().endsWith(".properties")){
            Instrument inst = new Instrument(file);
            addInstrument(inst);
          }
        }
      }
    }catch(Exception e){
      Globals.logException(e);
    }
  }

  public Iterator newIterator(){
  	return instrumentMap.values().iterator();
    /*FList list = (FList) getFocProperty(FLD_INSTRUMENT_LIST);
    FocList focList = list.getList();
    focList.reloadFromDB();
    return focList.focObjectIterator();*/
  }

  public void connectAllInstruments(){
    if(instrumentMap != null){
      Iterator iter = instrumentMap.values().iterator();
      while(iter != null && iter.hasNext()){
        Instrument instru = (Instrument) iter.next();
        if ((!instru.isConnected())){
          try{
            instru.setConnected(true);
          }catch(Exception e){
            Globals.logException(e);
          }
        }
      }
    }
  }
  
  public void disconnectAllInstrument(){
    if(instrumentMap != null){
      Iterator iter = instrumentMap.values().iterator();
      while(iter != null && iter.hasNext()){
        Instrument instru = (Instrument) iter.next();
        if(instru.isConnected()){
          try{
            instru.setConnected(false);
          }catch(Exception e){
            Globals.logException(e);
          }
        }
      }
    }
  }
  
  public boolean isAnyInstrumentConnect(){
  	boolean anythingConnected = false;
    if(instrumentMap != null){
      Iterator iter = instrumentMap.values().iterator();
      while(iter != null && iter.hasNext() && !anythingConnected){
        Instrument instru = (Instrument) iter.next();
        anythingConnected = instru.isConnected();
      }
    }
    return anythingConnected;
  }
  
  public Instrument getInstrument(String code){
    return instrumentMap != null && code != null? instrumentMap.get(code) : null;
  }
  
  public void send(String code, L3Message message) throws Exception{
    Instrument instr = getInstrument(code);
    if(instr == null){
      throw new L3InstrumentNotFoundException("Instrument "+code+" not found!");
    }
  }

	public String getInstrumentsPropertiesDirectory() {
		return instrumentsPropertiesDirectory;
	}

	public void setInstrumentsPropertiesDirectory(String instrumentsPropertiesDirectory) {
		this.instrumentsPropertiesDirectory = instrumentsPropertiesDirectory;
	}
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // PANEL
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  	
	@Override
	public FPanel newDetailsPanel(int viewID) {
	  return new PoolKernelGuiDetailsPanel(viewID, this);
	}
	
	public static FPanel newBrowsePanel(FocList list, int viewID) {
    return new PoolKernelGuiBrowsePanel(list, viewID); 
	}
	
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
	
  private static FocList poolList = null;
	  
  public static FocList getList(int mode){
    poolList = getList(poolList, getFocDesc(), mode);
    return poolList;
    /*if(poolList == null){
      FocLink link = new FocLinkSimple(getFocDesc());
      FocList focList = new FocList(link);      
      focList.loadIfNotLoadedFromDB();
      for (int i=0;i<focList.size();i++){
        poolList[i] = (PoolKernel) focList.getFocObject(i);
      }
      instance = (PoolKernel) focList.getAnyItem();      
      if(instance == null){
      	instance = (PoolKernel) focList.newEmptyItem();
        focList.add(instance);
        focList.validate(false);
      }
    }
    return instance;*/
  }
  
  public static PoolKernel getPool(String name){
    PoolKernel foundPool = null;
    FocList list = getList(FocList.LOAD_IF_NEEDED);
    for (int i=0; i<list.size() && foundPool == null; i++){
      PoolKernel pool = (PoolKernel)list.getFocObject(i);
      if(pool.getName().equals(name)){
        foundPool = pool;
      }
    }
    return foundPool;
  }
 
  public static PoolKernel getPoolForInstrument(String instrumentCode){
    PoolKernel foundPool = null;
    FocList list = getList(FocList.LOAD_IF_NEEDED);
    for (int i=0; i<list.size() && foundPool == null; i++){
      PoolKernel pool = (PoolKernel)list.getFocObject(i);
      if(pool.getInstrument(instrumentCode) != null){
        foundPool = pool;
      }
    }
    return foundPool;
  }

  public static Instrument getInstrumentForAnyPool(String instrumentCode){
  	Instrument foundInstrument = null;
    FocList list = getList(FocList.LOAD_IF_NEEDED);
    for (int i=0; i<list.size() && foundInstrument == null; i++){
      PoolKernel pool = (PoolKernel)list.getFocObject(i);
      foundInstrument = pool.getInstrument(instrumentCode);
    }
    return foundInstrument;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // DESCRIPTION
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocDesc focDesc = null;

	/*public static final int FLD_NAME = 1;
	public static final int FLD_INSTRUMENT_LIST = 2;
  public static final int FLD_USER_LIST = 3;
  public static final int COL_NAME = 4;
  
  private static FocLink poolUsersLink = null;
  
  public static FocLink getPoolUsersLink() {
    if (poolUsersLink == null) {
      poolUsersLink = new FocLinkN2N( getFocDesc(), FocUser.getFocDesc(), "POOL_USR");
    }
    return poolUsersLink;
  }
	
	private static FocLink poolInstrumentLink = null;
	
	public static FocLink getPoolInstrumentLink(){
		if(poolInstrumentLink == null){
			poolInstrumentLink = new FocLinkOne2N(getFocDesc(), Instrument.getFocDesc());
		}
		return poolInstrumentLink; 
	}*/
	
	public static FocDesc getFocDesc() {
	/*  if (focDesc == null) {
	    FField focFld = null;
	    focDesc = new FocDesc(PoolKernel.class, FocDesc.DB_RESIDENT, "POOL", false);
	
	    focFld = focDesc.addReferenceField();
	
	    focFld = new FCharField("NAME", "Name", FLD_NAME, true, FCharField.NAME_LEN);
	    focFld.setLockValueAfterCreation(true);	    
	    focDesc.addField(focFld);
	    
	    focFld = new FListField("INSTR_LIST", "Instrument list", FLD_INSTRUMENT_LIST, getPoolInstrumentLink());
	    focDesc.addField(focFld);
      
      FListField listFld = new FListField("USERS", "Users", FLD_USER_LIST, getPoolUsersLink());      
      focDesc.addField(listFld);
	    
	  }
	  return focDesc;*/
    if (focDesc == null) {
      focDesc = new PoolKernelDesc();
    }
    return focDesc;
	}
}