package b01.l3.connector.fileConnector;

import java.io.File;

import b01.foc.Globals;
import b01.foc.file.DirSet;
import b01.foc.file.FileGrabber;
import b01.foc.list.FocList;
import b01.foc.property.FString;
import b01.l3.FileIOFactory;
import b01.l3.L3Application;
import b01.l3.connector.L3IConnector;
import b01.l3.connector.LisConnector;
import b01.l3.connector.LisConnectorDesc;
import b01.l3.data.L3Sample;

public class LisFileConnector implements L3IConnector, FileGrabber{
	
  private DirSet dirSet = null;
  private DefaultFileIO defaultFileIO = null;
  private LisConnector lisConnector = null;
  
  public LisFileConnector() {
  } 

  public void dispose(){
    if(dirSet != null){
    	dirSet.dispose();
    	dirSet = null;
    }
    if(defaultFileIO != null){
    	defaultFileIO.dispose();
    	defaultFileIO = null;
    }
    if(lisConnector != null){
    	lisConnector = null;
    }
	}
	 
  public void setLisConnector(LisConnector lisConnector){
  	this.lisConnector = lisConnector;
  }
  
	private DefaultFileIO getFileIO() throws Exception{
  	if(defaultFileIO == null && (FString) lisConnector.getFocProperty(LisConnectorDesc.FLD_FILE_IO_CLASS_NAME) != null){
  		defaultFileIO = (DefaultFileIO) (FileIOFactory.getInstance().getFileIO(lisConnector.getFocProperty(LisConnectorDesc.FLD_FILE_IO_CLASS_NAME).getString())).newInstance();
  	}		
		return defaultFileIO;
	}
  	
	public boolean grabFile(File file) {
		boolean error = true;
		try{
			DefaultFileIO fileIO = getFileIO();
			fileIO.setFile(file);
			fileIO.setFieldSeparator('|');
			
			fileIO.readFile();
			lisConnector.treatMessage(fileIO.getL3Message());
			fileIO.close();			
			error = false;			
		}catch(Exception e){
			error = true;
			Globals.logException(e);
		}
		return error;
	}
  
  public void postSampleToLis(L3Sample sample) throws Exception{
  	getFileIO().postSampleToLis(sample, getDirSet());
  }
  
  public boolean connect() throws Exception{
  	getDirSet().startPolling();
    return false;
  }
  
	public boolean disconnect() throws Exception{
		getDirSet().stopPolling();
    return false;
  }
  
	public String getRootDir() {
		FString root = (FString) lisConnector.getFocProperty(LisConnectorDesc.FLD_ROOT_DIR);
    return (root != null) ? root.getString() :"";
	}

	public void setRootDir(String dir) {
		FString root = (FString) lisConnector.getFocProperty(LisConnectorDesc.FLD_ROOT_DIR);
    if(root != null){
      root.setString(dir);
    }
	}
	
	public String getName() {
		FString name = (FString) lisConnector.getFocProperty(LisConnectorDesc.FLD_NAME);
	  return (name!= null) ? name.getString() :"";
	}

  public void setName(String name) {
		FString n= (FString) lisConnector.getFocProperty(LisConnectorDesc.FLD_NAME);
	  if(n != null){
	    n.setString(name);
	  }
	}
	
	public String getReceiveDir() {
		FString in = (FString) lisConnector.getFocProperty(LisConnectorDesc.FLD_RECEIVE_DIR);
    return (in != null) ? in.getString() :"";
	}

	public void setInDir(String dir) {
		FString in = (FString) lisConnector.getFocProperty(LisConnectorDesc.FLD_RECEIVE_DIR);
    if(in != null){
      in.setString(dir);
    }
	}
  
	public String getSendDir() {
		FString out = (FString) lisConnector.getFocProperty(LisConnectorDesc.FLD_SEND_DIR);
    return (out!= null) ? out.getString() :"";
	}

	public void setOutDir(String dir) {
		FString out = (FString) lisConnector.getFocProperty(LisConnectorDesc.FLD_SEND_DIR);
    if(out!= null){
      out.setString(dir);
    }
	}
	
	public String getArchiveDir() {
		FString archive = (FString) lisConnector.getFocProperty(LisConnectorDesc.FLD_ARCHIVE_DIR);
    return (archive != null) ? archive.getString() :"";
	}

	public void setArchiveDir(String dir) {
		FString archive= (FString) lisConnector.getFocProperty(LisConnectorDesc.FLD_ARCHIVE_DIR);
    if(archive != null){
      archive.setString(dir);
    }
	}
	
	public String getErrorDir() {
		FString error= (FString) lisConnector.getFocProperty(LisConnectorDesc.FLD_ERROR_DIR);
    return (error != null) ? error.getString() :"";
	}

	public void setErrorDir(String dir) {
		FString error = (FString) lisConnector.getFocProperty(LisConnectorDesc.FLD_ERROR_DIR);
    if(error != null){
      error.setString(dir);
    }
	}
    
	public DirSet getDirSet() throws Exception{
    if(dirSet == null){
      dirSet = new DirSet();
      dirSet.setDirectories(getRootDir(), getSendDir(), getReceiveDir(), getErrorDir(), getArchiveDir());
      dirSet.setFileGrabber(this);
      dirSet.setKeepFilesForDebug(L3Application.getAppInstance().isKeepFilesForDebug());
    }
    return dirSet;
	}

  public void postMessagesToLis(FocList instrMessageList) throws Exception {
  }
}