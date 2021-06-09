package b01.foc.db.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import java.sql.ResultSetMetaData;

import java.util.Hashtable;

import javax.swing.JOptionPane;

import b01.foc.ConfigInfo;
import b01.foc.Globals;
import b01.foc.IFocDescDeclaration;
import b01.foc.db.SQLRequest;
import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocDesc;
import b01.foc.desc.FocObject;
import b01.foc.desc.field.FField;
import b01.foc.file.FocFileReader;
import b01.foc.property.FProperty;

public class DB2ASCII {

	private final static String NULL_VALUE = "null";
	
	private String fileName = null;
	private int copyDirection = COPY_DIRECTION_DB_TO_ASCII;

	public static final String BACKUP_FILE_EXTENSION = "fbk";
	
	public final static int COPY_DIRECTION_DB_TO_ASCII = 1;
	public final static int COPY_DIRECTION_ASCII_TO_DB = 2;
	
	public DB2ASCII(String fileName, int copyDirection) throws Exception {
		this.fileName = fileName;
		this.copyDirection = copyDirection;
	}
	
	public void dispose(){
		fileName = null;
	}
	
	private FocDesc findFocDesc(String tableName){
		FocDesc foundFocDesc = null;
		tableName = tableName.toUpperCase();
		
		Iterator iter = Globals.getApp().getFocDescDeclarationIterator();
		while(iter != null && iter.hasNext() && foundFocDesc == null){
			IFocDescDeclaration descDeclaration = (IFocDescDeclaration) iter.next();
			if(descDeclaration != null){
				FocDesc focDesc = descDeclaration.getFocDesctiption();
				if(focDesc != null && focDesc.getStorageName().toUpperCase().compareTo(tableName) == 0){
					foundFocDesc = focDesc;
				}
			}
		}

		return foundFocDesc;
	}
	
	private void copyTable(String tableName, BufferedWriter fileToPostWriter) {
		FocDesc focDesc = findFocDesc(tableName);
		if(focDesc != null){
			FocConstructor constr = new FocConstructor(focDesc, null);
			FocObject	tempObject = constr.newItem();
			
			Statement stmt = Globals.getDBManager().lockStatement();
			try{
		    ResultSet resultSet = stmt.executeQuery("SELECT * FROM "+tableName);
		    while(resultSet.next()){
		    	StringBuffer line = new StringBuffer(tableName+"|");
		    	
		    	ResultSetMetaData metaData= resultSet.getMetaData();
		    	for(int c=1; c<metaData.getColumnCount()+1; c++){
		      	String columnName = metaData.getColumnName(c);
		      	FField field = focDesc.getFieldByDBCompleteName_GetDBLevelField(columnName.toUpperCase());
		      	if(field == null) Globals.logString("Field not found : "+tableName+" "+columnName);
		      	FProperty prop = tempObject.getFocProperty(field.getID());//field.newProperty(null);
            if( prop == null ){
              prop = field.newProperty(null);
            }
            prop.setSqlString(resultSet.getString(c));
		      	String value = prop.getString();
		      	if(value.compareTo("") == 0){
		      		value = NULL_VALUE;
		      	}
		      	line.append(columnName+"|"+value+"|");
		    	}
		    	fileToPostWriter.write(line.toString());
		    	fileToPostWriter.newLine();
		    }
			}catch(Exception e){
				Globals.logException(e);
			}
	    Globals.getDBManager().unlockStatement(stmt);

	    tempObject.dispose();
			tempObject = null;
		}
	}
	
	private void copyDB2ASCII() throws Exception{
		File fileToPost = new File(fileName);
		
		boolean doBackup = true;
		if(fileToPost.exists() && Globals.getDisplayManager() != null){
      int choice = JOptionPane.showConfirmDialog(Globals.getDisplayManager().getMainFrame(), 
          "Do you realy want to overite the file : "+fileName+" ?", 
          "Backup",
          JOptionPane.YES_NO_OPTION);
      doBackup = choice == JOptionPane.YES_OPTION;
		}
		
		if(doBackup){
			fileToPost.delete();
			fileToPost.createNewFile();
	
			BufferedWriter fileToPostWriter = new BufferedWriter(new FileWriter(fileToPost, true));
			
			Hashtable allTables = (Hashtable) Globals.getApp().getDBManager().getAllRealTables();
			Iterator iter = allTables.values().iterator();
			while(iter != null && iter.hasNext()){
				String tableName = (String) iter.next();
				if(tableName != null && tableName.trim().compareTo("") != 0){
					copyTable(tableName, fileToPostWriter);
				}
			}
			
			fileToPostWriter.flush();
			fileToPostWriter.close();
			
			if(Globals.getDisplayManager() != null){
				Globals.getDisplayManager().popupMessage("Backup finished for database : "+ConfigInfo.getJdbcURL()+"\nTo FOC Backup File : "+fileToPost);
			}
		}
	}	

	private void copyASCII2DB() throws Exception{
		InputStream inputStream = Globals.getInputStream(fileName);
		boolean doRestore = inputStream != null;

		if(doRestore){
			if(Globals.getDisplayManager() != null){
				String message = "Do you realy want to overwrite database : "+ConfigInfo.getJdbcURL()+"\nWith the FOC Backup File : "+fileName+" ?";
	      int choice = JOptionPane.showConfirmDialog(Globals.getDisplayManager().getMainFrame(), 
	      		message, 
	          "Database restore", 
	          JOptionPane.YES_NO_OPTION);
	      if( choice == JOptionPane.YES_OPTION){
	      	doRestore = true;
	      }else{
	      	doRestore = false;
	      }
			}
			if(!doRestore && Globals.getDisplayManager() != null){
				Globals.getDisplayManager().popupMessage("Restore aborted.");
			}
		}
		
		if(doRestore){
			Globals.getDBManager().beginTransaction();

			Iterator iter = Globals.getApp().getFocDescDeclarationIterator();
			while(iter != null && iter.hasNext()){
				IFocDescDeclaration descDeclaration = (IFocDescDeclaration) iter.next();
				if(descDeclaration != null){
					FocDesc focDesc = descDeclaration.getFocDesctiption();
					if( focDesc.isDbResident() ){
	          Statement stmt = Globals.getDBManager().lockStatement();
	          try {
	            StringBuffer request = new StringBuffer("DELETE FROM "+focDesc.getStorageName());
	            stmt.executeUpdate(request.toString());
	          } catch (SQLException e) {
	            Globals.logException(e);
	            throw e;
	          }
	          Globals.getDBManager().unlockStatement(stmt);
	        }
				}
			}
	    
	    RestoreFileReader fileReader = new RestoreFileReader(inputStream, '|');
	    fileReader.readFile();
			fileReader.dispose();
			
			Globals.getDBManager().commitTransaction();
			
			if(Globals.getDisplayManager() != null){
				Globals.getDisplayManager().popupMessage("Restore finished.");
			}
		}
	}
	
	public void backupRestore() throws Exception{
		if(copyDirection == COPY_DIRECTION_DB_TO_ASCII){
			copyDB2ASCII();
		}else{
			copyASCII2DB();
		}
	}
	
	public class RestoreFileReader extends FocFileReader{

		private String  tableName = null;
		private FocDesc focDesc   = null;
		private FocObject	tempObject = null;
		private String  fieldName = null;
		private StringBuffer fields = null;
		private StringBuffer values = null;
		
		public RestoreFileReader(File file, char fieldDelimiter) {
			super(file, fieldDelimiter);
		}
		
    public RestoreFileReader(InputStream inputStream, char fieldDelimiter) {
      super(inputStream, fieldDelimiter);
    }
    
    public void dispose(){
			disposeTempObject();
			super.dispose();
		}

		public void disposeTempObject(){
			if(tempObject != null){
				tempObject.dispose();
				tempObject = null;
			}
		}
		
		@Override
		public void readLine(StringBuffer buffer) {
			fields = new StringBuffer();
			values = new StringBuffer();
			
			scanTokens(buffer);
			
			if(fields != null && fields.toString().trim().compareTo("") != 0){
				Statement stmt = Globals.getDBManager().lockStatement();
				
				StringBuffer request = new StringBuffer("INSERT INTO "+tableName+" ("+fields+") VALUES ("+values+")");
				
				try {
					Globals.logString(request.toString());
					stmt.executeUpdate(SQLRequest.adapteRequestToDBProvider(request));
				} catch (SQLException e) {
					Globals.logException(e);
				}
				Globals.getDBManager().unlockStatement(stmt);				
			}
		}

		@Override
		public void readToken(String token, int pos) {
			if(pos == 0){
				if(tableName == null || token.compareTo(tableName) != 0){
					tableName = token;
					focDesc = findFocDesc(tableName);
					
					disposeTempObject();
					FocConstructor constr = new FocConstructor(focDesc, null);
					tempObject = constr.newItem();
				}
			}else{
				switch((pos-1) % 2){
				case 0:
					fieldName = token;
					break;
				case 1:
					if(token.compareTo(NULL_VALUE) == 0) token = ""; 
					FField field = focDesc.getFieldByDBCompleteName_GetDBLevelField(fieldName.toUpperCase());
					if(field == null){
						Globals.logString(" Field not found "+fieldName+" table "+tableName);
					}
					if(field != null){
            FProperty prop = tempObject.getFocProperty(field.getID());
            
            if( prop == null ){
              prop = field.newProperty(null);
            }
            prop.setString(token);  
            
            if(values.length() > 0) values.append(',');
						values.append(prop.getSqlString());
						
						if(fields.length() > 0) fields.append(','); 
						fields.append(fieldName);
					}
					break;
				}
			}
		}
	}
}
