package b01.l3;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import b01.foc.ConfigInfo;
import b01.foc.Globals;
import b01.foc.db.SQLSelectFields;
import b01.foc.db.tools.DB2ASCII;
import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocDesc;
import b01.foc.desc.FocObject;
import b01.foc.desc.field.FField;
import b01.foc.gui.FPanel;
import b01.foc.list.FocLink;
import b01.foc.list.FocLinkSimple;
import b01.foc.list.FocList;
import b01.foc.list.filter.DateCondition;
import b01.foc.property.FBoolean;
import b01.foc.property.FInt;
import b01.foc.property.FProperty;
import b01.l3.connector.LisConnector;
import b01.l3.data.L3Sample;
import b01.l3.data.L3SampleTestJoin;
import b01.l3.data.L3SampleTestJoinDesc;
import b01.l3.data.L3SampleTestJoinFilter;
import b01.l3.data.L3SampleTestJoinGuiBrowsePanel;
import b01.l3.data.L3SampleTestJoinTree;
import b01.l3.data.L3Test;
import b01.l3.data.L3TestDesc;

public class L3Application extends FocObject {
  
	private ArrayList<L3PurgeListener> purgeListenerArray = null;
	private boolean archiveDone = false;
	
	private int          backgroundTask       = 0;
	private Instrument   backgroundInstrument = null;
	private LisConnector backgroundConnector  = null;
	
	public final static int BACKGROUND_TASK_NONE      = 0;
	public final static int BACKGROUND_TASK_DRIVER    = 1;
	public final static int BACKGROUND_TASK_CONNECTOR = 2;
	
  public L3Application (FocConstructor constr) {
    super(constr);
    newFocProperties();
    setPropertyMultiChoice(L3ApplicationDesc.FLD_APPLICATION_MODE,L3Globals.APPLICATION_MODE_WITH_DB);
    setPropertyBoolean(L3ApplicationDesc.FLD_AUTOMATIC_PURGE, true);
    setPropertyBoolean(L3ApplicationDesc.FLD_KEEP_FILES_FOR_DEBUG, false);
    forceControler(true);
  }
  
  public void dispose(){
  	super.dispose();
  	backgroundConnector  = null;
  	backgroundInstrument = null;
  }
  
  private static L3Application instance = null;
  
  public static L3Application getAppInstance(){
    
    if(instance == null){
      FocLink link = new FocLinkSimple(getFocDesc());
      FocList focList = new FocList(link);      
      focList.loadIfNotLoadedFromDB();
      
      instance = (L3Application) focList.getAnyItem();      
      if(instance == null){
        instance = (L3Application) focList.newEmptyItem();
        focList.add(instance);
        focList.validate(false);
      }
    }
    
    return instance;
  }
  
  public void recover(){
    FocList poolList = PoolKernel.getList(FocList.FORCE_RELOAD);
    for (int i = 0; i < poolList.size(); i++){
      PoolKernel pool = (PoolKernel)poolList.getFocObject(i);
      pool.disconnectAllInstrument();
    }
    
    FocList lisConnectorList = LisConnector.getList(null, LisConnector.getFocDesc(), FocList.FORCE_RELOAD);
    for (int i = 0; i< lisConnectorList.size(); i++){
      LisConnector connector = (LisConnector)lisConnectorList.getFocObject(i);
      connector.switchOff();
    }
  }
  
  //---------------------------------
  //    GETER SETER
  // ---------------------------------
  
  public int getMode() {
    return ((FInt) getFocProperty(L3ApplicationDesc.FLD_APPLICATION_MODE)).getInteger();
  }
  
  public void setMode(int mode) {
    ((FInt) getFocProperty(L3ApplicationDesc.FLD_APPLICATION_MODE)).setInteger(mode);
  }

  public boolean isLaunchAsServices() {
    return ((FBoolean) getFocProperty(L3ApplicationDesc.FLD_LAUNCH_AS_SERVICES)).getBoolean();
  }

  public boolean isAutomaticPurge() {
    return ((FBoolean) getFocProperty(L3ApplicationDesc.FLD_AUTOMATIC_PURGE)).getBoolean();
  }

  public boolean isKeepFilesForDebug() {
    return ((FBoolean) getFocProperty(L3ApplicationDesc.FLD_KEEP_FILES_FOR_DEBUG)).getBoolean();
  }

  public int getNbrDaysToKeepEverything(){
  	int daysToKeep = 0;
  	getPropertyInteger(L3ApplicationDesc.FLD_PURGE_NBR_DAYS_TO_KEEP);
  	return (daysToKeep == 0) ? 2 : daysToKeep;
  }

  public int getNbrDaysToKeepCommited(){
  	int daysToKeep = 0;
  	getPropertyInteger(L3ApplicationDesc.FLD_PURGE_NBR_DAYS_TO_KEEP_FOR_COMMITED);
  	return (daysToKeep == 0) ? 2 : daysToKeep;
  }
  
  private java.sql.Date getDateForEverything(){
  	Date currDate = Globals.getDBManager().getCurrentDate();
    int nbrDaysToKeep = getNbrDaysToKeepEverything();
    return new java.sql.Date(currDate.getTime() - nbrDaysToKeep * Globals.DAY_TIME);
  }

  private java.sql.Date getDateForCommitted(){
  	Date currDate = Globals.getDBManager().getCurrentDate();
    int nbrDaysToKeep = getNbrDaysToKeepCommited();
    return new java.sql.Date(currDate.getTime() - nbrDaysToKeep * Globals.DAY_TIME);
  }
  
  //---------------------------------
  //    PURGE LISTENER
  // ---------------------------------

  public void addPurgeListener(L3PurgeListener listener){
  	if(purgeListenerArray == null){
  		purgeListenerArray = new ArrayList<L3PurgeListener>();
  	}
  	purgeListenerArray.add(listener);
  }

  public void removePurgeListener(L3PurgeListener listener){
  	if(purgeListenerArray != null){
    	purgeListenerArray.remove(listener);
  	}
  }

  public void notifyPurgeListener(L3PurgeEvent event){
  	if(purgeListenerArray != null){
  		for(int i=0; i<purgeListenerArray.size(); i++){
  			L3PurgeListener listener = purgeListenerArray.get(i);
  			listener.purge(event);
  		}
  	}
  }
  
  private void doArchive() throws Exception{
  	if(!archiveDone){
	  	long time = System.currentTimeMillis();
	  	Time currentTime = new Time(time);
	  	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
	  	String archDir = ConfigInfo.getLogDir();
	  	String archiveName = sdf.format(currentTime);
	  	DB2ASCII db2Ascii = new DB2ASCII(archDir+"/"+archiveName+".backup", DB2ASCII.COPY_DIRECTION_DB_TO_ASCII);
	  	db2Ascii.backupRestore();
	  	db2Ascii.dispose();
	  	archiveDone = true;
  	}
  }
  
  private void purge_DeleteFromL3Sample(ArrayList<Integer> itemsToDelete) throws Exception {
		if(itemsToDelete.size() > 0){
			StringBuffer req = new StringBuffer("DELETE FROM L3SAMPLE WHERE ");
			req.append(FField.REF_FIELD_NAME+" IN (");
			for(int i=0; i<itemsToDelete.size(); i++){
				if(i > 0) req.append(",");
				req.append(itemsToDelete.get(i));
			}
			req.append(")");
			
			Statement stm2 = (Statement) Globals.getDBManager().lockStatement();
			Globals.logString(req);
			stm2.execute(req.toString());
			Globals.getDBManager().unlockStatement(stm2);
		}
  }

  private void purge_FillItemsToDeleteWithSamplesWithoutTests(ArrayList<Integer> itemsToDelete) throws Exception {
		HashMap<Integer, Integer> testExist = new HashMap<Integer, Integer>();
		
		String sampleIDFieldNameInTestTable = L3Test.getFocDesc().getFieldByID(L3TestDesc.FLD_SAMPLE).getDBName();
		
		Statement stm = Globals.getDBManager().lockStatement();
		StringBuffer str = new StringBuffer("SELECT DISTINCT "+sampleIDFieldNameInTestTable+" FROM "+L3Test.getFocDesc().getStorageName());
		Globals.logString(str);
		stm.execute(str.toString());
		ResultSet resSet = stm.getResultSet();
		while(resSet.next()){
			testExist.put(resSet.getInt(1), resSet.getInt(1));
		}
		Globals.getDBManager().unlockStatement(stm);
		
		SQLSelectFields selectSample = new SQLSelectFields(L3Sample.getFocDesc(), FField.REF_FIELD_ID, null);
		selectSample.execute();
		
		for(int l=0; l<selectSample.getLineNumber(); l++){
			FProperty prop = selectSample.getPropertyAt(l, 0);
			if(!testExist.containsKey(prop.getInteger())){
				itemsToDelete.add(prop.getInteger());
			}
		}
  }
  
  private L3SampleTestJoinFilter purge_FillItemsToDeleteWithCommitted(FocList listToDeleteCollection) throws Exception {
    java.sql.Date committedDate = getDateForCommitted();

  	L3SampleTestJoinFilter filter = L3SampleTestJoinDesc.newListWithFilter();
  	filter.setStatusEquals(L3TestDesc.TEST_STATUS_COMMITED_TO_LIS);
  	filter.setEntryDate(DateCondition.OPERATOR_LESS_THAN, new java.sql.Date(0), committedDate);
  	filter.setActive(true);
  	
  	FocList list = filter.getFocList();
  	Iterator iter = list.focObjectIterator();
  	while(iter != null && iter.hasNext()){
  		L3SampleTestJoin join = (L3SampleTestJoin) iter.next();
  		if(join != null){
 				listToDeleteCollection.add(join);
  		}
  	}
  	
  	return filter;
  }

  private L3SampleTestJoinFilter purge_FillItemsToDeleteWithEverything(FocList listToDeleteCollection) throws Exception {
    java.sql.Date everythingDate = getDateForEverything();

  	L3SampleTestJoinFilter filter = L3SampleTestJoinDesc.newListWithFilter();
  	filter.setEntryDate(DateCondition.OPERATOR_LESS_THAN, new java.sql.Date(0), everythingDate);
  	filter.setActive(true);
  	
  	FocList list = filter.getFocList();
  	Iterator iter = list.focObjectIterator();
  	while(iter != null && iter.hasNext()){
  		L3SampleTestJoin join = (L3SampleTestJoin) iter.next();
  		if(join != null){
 				listToDeleteCollection.add(join);
  		}
  	}
  	return filter;
  }

  public void purge(){
  	if(Globals.logFile_CheckLogDir()){
  		Globals.getDisplayManager().popupMessage("Cannot Purge Please check the log directory");
  	}else{
	  	try{
		    archiveDone = false;
		    
		    FocList listToDeleteCollection = L3SampleTestJoinDesc.newList();
		    listToDeleteCollection.setCollectionBehaviour(true);
		    
		    L3SampleTestJoinFilter filter1 = purge_FillItemsToDeleteWithCommitted(listToDeleteCollection);
		    L3SampleTestJoinFilter filter2 = purge_FillItemsToDeleteWithEverything(listToDeleteCollection);

		    boolean doDelete = false;
		    if(listToDeleteCollection.size() > 0){
		    	if(Globals.getDisplayManager() != null){
				    L3SampleTestJoinGuiBrowsePanel panel = new L3SampleTestJoinGuiBrowsePanel(listToDeleteCollection, L3SampleTestJoinTree.VIEW_PURGE); 
				    Globals.getDisplayManager().popupDialog(panel, "Samples to delete", true);
				    doDelete = panel.isDoDelete();
		    	}else{
		    		doDelete = true;
		    	}
		    }else{
		    	if(Globals.getDisplayManager() != null){
		    		Globals.getDisplayManager().popupMessage("No samples to purge.");
		    	}		    	
		    }
		    
		    if(doDelete){
		    	doArchive();
		    	L3SampleTestJoinDesc.deleteListFromL3Test(listToDeleteCollection);
		    	
					L3Application.getAppInstance().notifyPurgeListener(null);

					if(Globals.getDisplayManager() != null){
		    		Globals.getDisplayManager().popupMessage("Purge successful.");
		    	}
		    }
		    
		    listToDeleteCollection.dispose();
	    	filter1.dispose();
	    	filter2.dispose();
	    	
				archiveDone = false;
	  	}catch(Exception e){
	  		Globals.logString("Exception while purging");
	  		Globals.logException(e);
	  	}
  	}
  }
  
  public void automaticPurge(){
	  if(isAutomaticPurge()){
	  	purge();
	  }
  }
  
  //---------------------------------
  //    PANEL
  // ---------------------------------
  public FPanel newDetailsPanel(int viewID) {
    return new L3ApplicationGuiDetailsPanel(viewID,this);
  }
  
  public static FPanel newBrowsePanel(FocList list, int viewID) {
    return new L3ApplicationGuiBrowsePanel(null,L3Globals.VIEW_CONFIG); 
  }
  
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // DESCRIPTION
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocDesc focDesc = null;
  
  public static FocDesc getFocDesc() {
     if (focDesc == null) {
        focDesc = new L3ApplicationDesc();
      }
      return focDesc;
  }

	public int getBackgroundTask() {
		return backgroundTask;
	}

	public void setBackgroundTask(int backgroundTask) {
		this.backgroundTask = backgroundTask;
	}

	public Instrument getBackgroundInstrument() {
		return backgroundInstrument;
	}

	public void setBackgroundInstrument(Instrument backgroundInstrument) {
		this.backgroundInstrument = backgroundInstrument;
	}

	public LisConnector getBackgroundConnector() {
		return backgroundConnector;
	}

	public void setBackgroundConnector(LisConnector backgroundConnector) {
		this.backgroundConnector = backgroundConnector;
	}
	
	public boolean makeRealDriverConnection(){
		boolean realConnection = getBackgroundTask() == L3Application.BACKGROUND_TASK_DRIVER;
		return realConnection;
	}
}