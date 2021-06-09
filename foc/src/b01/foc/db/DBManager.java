/*
 * Created on 17 fvr. 2004
 */
package b01.foc.db;

import b01.foc.*;
import b01.foc.desc.*;
import b01.foc.desc.field.FField;

import java.text.SimpleDateFormat;
import java.util.*;
import java.sql.*;

/**
 * @author 01Barmaja
 */
public class DBManager {

  private Connection connection = null;
  private Hashtable allTables = null;

  private HashMap<Statement, Statement> busyStatements = null;
  private ArrayList<Statement> freeStatements = null;
  
  private ArrayList<FocObject> lockedObjects = null;
  private String dateRequestSQL = null;
  private String timeStampRequestSQL = null;
  
  private int provider = 1;
  
  public static final int PROVIDER_MYSQL  = 1;
  public static final int PROVIDER_HSQLDB = 2;
  public static final int PROVIDER_ORACLE = 3;
  
  private Thread transactionThread                         = null;
  private int    nbrOfBeginTransactionsInTransactionThread = 0   ;
  
  public DBManager() {
    openConnection();
    busyStatements = new HashMap<Statement, Statement>();
    freeStatements = new ArrayList<Statement>();
  }

  private String initializeDateRequest(){
    if(dateRequestSQL == null || timeStampRequestSQL == null){
      try{
        DatabaseMetaData dmt = getConnection().getMetaData();
        String fcts = dmt.getTimeDateFunctions();
        StringTokenizer tokenizer = new StringTokenizer(fcts, ",");
        Globals.logString(fcts);
        while(tokenizer.hasMoreTokens()){
          String tok = tokenizer.nextToken();        
          if(tok.compareTo("CURDATE") == 0){
            //rr Begin
            if (Globals.getDBManager().getProvider() == DBManager.PROVIDER_ORACLE){
              dateRequestSQL = "select sysdate from dual";
              break;
            }else{
            //rr End
              dateRequestSQL = "select CURDATE()";
              break;              
            }
          }else if(tok.compareTo("CURRENT_DATE") == 0){
            dateRequestSQL = "select CURRENT_DATE()";
            break;
          }else if(tok.compareTo("CURRENT_TIMESTAMP") == 0){
            timeStampRequestSQL = "select CURRENT_TIMESTAMP()";
          }
        }
        if(dateRequestSQL == null){
          dateRequestSQL = "";
        }
        if(timeStampRequestSQL == null){
          timeStampRequestSQL = "";
        }
      }catch (Exception e){
        Globals.logException(e);
      }
    }
    return dateRequestSQL;
  }
  
  private Savepoint setSavepoint() throws SQLException{
  	Savepoint savepoint = connection.setSavepoint();
  	return savepoint;
  }

  public synchronized Savepoint beginTransactionWithSavepoint(){
  	Savepoint savepoint = null;
  	beginTransaction();
    try{
    	savepoint = setSavepoint();
    }catch(Exception e){
    	savepoint = null;
    	Globals.logString("Exception : JDBC - Transaction SavePoint is not supported. STACK TRACE IS HIDDEN.");
      //Globals.logException(e);
    }
    return savepoint;
  }

  public synchronized boolean insideTransaction(){
  	boolean inTransaction = false;
    try{
      if(!connection.getAutoCommit() && transactionThread == Thread.currentThread()){
      	inTransaction = true;
      }else{
      	inTransaction = false;
      }
    }catch(Exception e){
      Globals.logException(e);
    }
		return inTransaction;
  }

  public synchronized void beginTransaction(){
    try{
      if(!connection.getAutoCommit() && transactionThread == Thread.currentThread()){
        nbrOfBeginTransactionsInTransactionThread++;
      }else{
        while(!connection.getAutoCommit()){
          wait();
        }
        transactionThread = Thread.currentThread();
        connection.setAutoCommit(false);
        connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        notifyAll();
      }
    }catch(Exception e){
      Globals.logException(e);
    }
  }

  public synchronized void commitTransaction(){
    try{
      if(nbrOfBeginTransactionsInTransactionThread > 0 && transactionThread == Thread.currentThread()){
        nbrOfBeginTransactionsInTransactionThread--;
      }else{
        while(connection.getAutoCommit()){
          wait();
        }      
        connection.commit();
        connection.setAutoCommit(true);
        transactionThread = null;
        notifyAll();
      }
    }catch(Exception e){
      Globals.logException(e);
      Globals.getApp().exit();
    }
  }

  public synchronized void rollbackTransaction(Savepoint savepoint){
    try{
      if(nbrOfBeginTransactionsInTransactionThread > 0 && transactionThread == Thread.currentThread()){
        nbrOfBeginTransactionsInTransactionThread--;
        if(savepoint != null){
        	connection.rollback(savepoint);
        }
      }else{
        while(connection.getAutoCommit()){
          wait();
        }      
        connection.rollback();
        connection.setAutoCommit(true);
        transactionThread = null;
        notifyAll();
      }
    }catch(Exception e){
      Globals.logException(e);
      Globals.getApp().exit();
    }
  }
  
  public java.sql.Date getCurrentDate(){
    java.sql.Date date = null;
    try{
      initializeDateRequest();
      if(dateRequestSQL.compareTo("") == 0){
          date = new java.sql.Date(System.currentTimeMillis());
      }else{
        Statement stm = lockStatement();
        ResultSet resSet = stm.executeQuery(dateRequestSQL);
        if(resSet != null && resSet.next()){
          Globals.logString(resSet.getString(1));
          date = resSet.getDate(1);
        }
        
      }
    }catch (Exception e){
      Globals.logException(e);
    }
    return date;
  }

  public String getCurrentTimeStamp(){
    String timeStamp = null;
    try{
      initializeDateRequest();
      if(timeStampRequestSQL.compareTo("") == 0){
        java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        timeStamp = dateFormat.format(date);
      }else{
        Statement stm = lockStatement();
        ResultSet resSet = stm.executeQuery(timeStampRequestSQL);
        if(resSet != null && resSet.next()){
          //Globals.logString(resSet.getString(1));
          timeStamp = resSet.getString(1);
        }
      }
    }catch (Exception e){
      Globals.logException(e);
    }
    return timeStamp;
  }

  public void exit(){
    unlockAll();
    this.closeConnection();
  }
  
  public synchronized Statement lockStatement() {
    Statement stm = null;
    if (freeStatements.size() > 0) {
      stm = (Statement) freeStatements.get(0);
      freeStatements.remove(0);
    } else {
      try {
        stm = connection.createStatement();
      } catch (Exception e) {
        Globals.logException(e);
      }
    }
    if (stm != null) {
      busyStatements.put(stm, stm);
    }
    return stm;
  }
  
  public synchronized void unlockStatement(Statement stm) {
    if (stm != null) {      
      busyStatements.remove(stm);
      if(freeStatements.size() < 5){
      	freeStatements.add(stm);	
      }else{
      	try{
      		stm.close();
      	}catch(Exception e){
      		Globals.logException(e);
      	}
      }
    }
  }
  
  public void destroyFreeStatments(){
    if(freeStatements != null){
      for(int i=freeStatements.size()-1; i>=0; i--){
        Statement stm = (Statement) freeStatements.get(i);
        try{
          stm.close();
          freeStatements.remove(i);
        }catch(Exception e){
          Globals.logException(e);
        }
      }
    }
  }

  private void openConnection() {
    String drivers = null;
    String url = null;
    String username = null;
    String password = null;      

    try {
      drivers = ConfigInfo.getJdbcDrivers();
      url = ConfigInfo.getJdbcURL();
      username = ConfigInfo.getJdbcUserName();
      password = ConfigInfo.getJdbcPassword();      
      //rr Begin
      //StringBuffer result = new StringBuffer();
      
      StringTokenizer tokenizer = new StringTokenizer(url, ":", false);
      for(int count = 0; count < 2 && tokenizer.hasMoreTokens(); count++){
        String token = tokenizer.nextToken();

        if(count == 1){
          if (token.equals("oracle")){
            setProvider(PROVIDER_ORACLE);
          }else if (token.equals("mysql")){
            setProvider(PROVIDER_MYSQL);
          }
        }
      }
      //rr End
      
      /*
      Class.forName("com.mysql.jdbc.Driver").newInstance();
      String url = "jdbc:mysql://localhost:3306/test";
      */
      Globals.logString("drivers "+drivers);
      Class.forName(drivers).newInstance();
      
      //rr Begin
      connection = DriverManager.getConnection(url /*+ "myDB;create=true;shutdown=true"*/, username, password);
      //rr End
      //connection = DriverManager.getConnection(url, username, password);//rr
      //statement = connection.createStatement();
    } catch (Exception e) {
    	String message = "Could not connect to Database: "+(url != null ? url : "")+"\nwith Username: "+(username != null ? username : ""); 
      if(Globals.getApp().isWithRegistry() && Globals.getDisplayManager() != null){
      	message += "\nYou will be redirected to change Environment";
      	Globals.getDisplayManager().popupMessage(message);
	      ConfigInfoWizardPanel panel = new ConfigInfoWizardPanel(new GuiConfigInfo(), ConfigInfoWizardPanel.STATE_DIRECTORY);
	      panel.setWithRestart(true);
	      Globals.getDisplayManager().popupDialog(panel, "", true);    
      }else{
	    	if(Globals.getDisplayManager() != null){
	    		Globals.getDisplayManager().popupMessage(message);
	    	}
	      Globals.logException(e);
	      System.exit(0);
      }
    }
  }

  private void closeConnection() {
    try {
      destroyFreeStatments();
      connection.close();
    } catch (Exception e) {
      Globals.logException(e);
    }
  }

  public Hashtable getAllRealTables(){
    Hashtable<String, String> tables = new Hashtable<String, String>();
    try {
      DatabaseMetaData dmt = connection.getMetaData();
      Globals.logString(dmt.getTimeDateFunctions());
      if (dmt != null) {
        ResultSet resultSet = dmt.getTables(null, null, null, new String[] { "TABLE" });
        if (resultSet != null) {
          while (resultSet.next()) {
            String tableName = resultSet.getString(3);
            String upperTableName = tableName.toUpperCase();
            tables.put(upperTableName, upperTableName);
          }
        }
      }
    } catch (Exception e) {
      Globals.logException(e);
    }
    return tables;
  }
  
  public void initAdaptDataModel() {
    try {
      allTables = getAllRealTables();
    } catch (Exception e) {
      Globals.logException(e);
    }
  }

  public boolean adaptTable(FocDesc focDesc) {
  	boolean altered = false;
    if (allTables != null && focDesc != null && focDesc.getDBResident()) {
      String dbTableName = focDesc.getStorageName();
      if (dbTableName != null) {
//      	Iterator iter = allTables.values().iterator();
//      	while(iter != null && iter.hasNext()){
//      		String str = (String)iter.next();
//      		Globals.logString("Table : "+str);
//      	}
      	focDesc.beforeAdaptTableModel();
        if (allTables.get(dbTableName) == null) {
          focDesc.createDBTable();
          altered = true;
        } else {
          altered = focDesc.adaptDBTableFields();
        }
        focDesc.afterAdaptTableModel();
      }
    }
    return altered;
  }

  public void endAdaptDataModel() {
    allTables = null;
  }
  
  public void adaptTableIndexes(FocDesc focDesc, boolean reindex) {
    if (focDesc != null && focDesc.getDBResident()) {
      SQLTableIndexesDetails indexesDetails = new SQLTableIndexesDetails(focDesc);
            
      Iterator iter = focDesc.indexIterator();
      while(iter != null && iter.hasNext()){
        DBIndex index = (DBIndex) iter.next();
        if(index != null){
          DBIndex realIndex = indexesDetails.getIndex(index.getName());
          if(realIndex == null){
            SQLCreateIndex sqlIndex = new SQLCreateIndex(index);
            sqlIndex.buildRequest();
            sqlIndex.execute();
          }else if(realIndex.compareTo(index) != 0 || reindex){
            SQLDropIndex dropIndex = new SQLDropIndex(realIndex);
            dropIndex.buildRequest();
            dropIndex.execute();

            SQLCreateIndex createIndex = new SQLCreateIndex(index);
            createIndex.buildRequest();
            createIndex.execute();
          }
          
          if(realIndex != null){
            indexesDetails.removeIndex(realIndex.getName());
          }
        }
      }
      
      if(ConfigInfo.isRemoveIndexesDuringAdaptDataModel()){
	      iter = indexesDetails.iterator();
	      while(iter != null && iter.hasNext()){
	        DBIndex indexToRemove = (DBIndex) iter.next();
	        
	        SQLDropIndex dropIndex = new SQLDropIndex(indexToRemove);
	        dropIndex.buildRequest();
	        dropIndex.execute();
	      }
      }
    }
  }
  
  //BAntoineS - AUTOINCREMENT
  public void adaptTableSequence(FocDesc focDesc) throws Exception{
  	if(Globals.getDBManager().getProvider() == DBManager.PROVIDER_ORACLE && focDesc.getFieldByID(FField.REF_FIELD_ID) != null){
  		Statement stm = lockStatement();
  		String req = "SELECT MAX("+FField.REF_FIELD_NAME+") FROM "+focDesc.getStorageName();
  		if(ConfigInfo.isLogDBRequestActive()) Globals.logString(req);
  		stm.executeQuery(req);
  		int maxRef = 0;
  		ResultSet rs = stm.getResultSet();
  		while(rs != null && rs.next()){
  			maxRef = rs.getInt(1);
  		}
  		unlockStatement(stm);

  		int seq = 0;
  		
  		try{
  			seq = focDesc.getNextSequence();//Cannot use the current value because we will get that it is not defined yet in this session
  		}catch(SequenceDoesNotExistException e){
        stm = Globals.getDBManager().lockStatement();
    		req = "CREATE SEQUENCE "+focDesc.getSequenceName()+" START WITH "+(maxRef+1)+" INCREMENT BY 1 NOMAXVALUE";
    		if(ConfigInfo.isLogDBRequestActive()) Globals.logString(req);
        stm.executeUpdate(req);
        Globals.getDBManager().unlockStatement(stm);
        seq = maxRef+1;
  		}catch(FocDBException e){
  			Globals.logException(e);
  		}catch(SQLException e){
  			throw e;
  		}

  		if(seq < maxRef){
        stm = Globals.getDBManager().lockStatement();
    		req = "ALTER SEQUENCE "+focDesc.getSequenceName()+" INCREMENT BY "+(maxRef+1-seq);
    		if(ConfigInfo.isLogDBRequestActive()) Globals.logString(req);
        stm.executeUpdate(req);
        Globals.getDBManager().unlockStatement(stm);
      }
  	}
  }
  //EAntoineS - AUTOINCREMENT
  
  /**
   * @return Returns the connection.
   */
  public Connection getConnection() {
    return connection;
  }
  
  public void addLockedObject(FocObject obj){
    if(lockedObjects == null){
      lockedObjects = new ArrayList<FocObject>();
    }
    lockedObjects.add(obj);
  }

  public void removeLockedObject(FocObject obj){
    if(lockedObjects != null){
      lockedObjects.remove(obj);
    }
  }
  
  public boolean isObjectLockedByThisSession(FocObject obj){
    return lockedObjects.contains(obj);
  }

  public void unlockAll(){
    if(lockedObjects != null){
      for(int i=lockedObjects.size()-1; i>=0; i--){
        FocObject obj = (FocObject) lockedObjects.get(i);
        obj.unlock();
        //lockedObjects.remove(i);
      }
    }
  }

	public int getProvider() {
		return provider;
	}

	public void setProvider(int provider) {
		this.provider = provider;
	}
}
