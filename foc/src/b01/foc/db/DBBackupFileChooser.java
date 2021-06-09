package b01.foc.db;

import javax.swing.JFileChooser;

import b01.foc.ConfigInfo;
import b01.foc.db.tools.DB2ASCII;
import b01.foc.jasper.ExtensionFileFilter;

@SuppressWarnings("serial")
public class DBBackupFileChooser extends JFileChooser{

	public DBBackupFileChooser(){
		super(ConfigInfo.getLogDir());
		setDialogTitle("Select Backup File");
		setFileSelectionMode(JFileChooser.FILES_ONLY);
		setFileFilter(new ExtensionFileFilter(DB2ASCII.BACKUP_FILE_EXTENSION, "FOC Backup File"));
	}

	public String choose(){
		String fileName = null;

	  int result = showDialog(null, "OK");
	  
	  if (result != JFileChooser.CANCEL_OPTION ){
	  	fileName = getSelectedFile().toString();
	  	fileName = fileName.replaceAll("\\\\","/");
      if(!fileName.endsWith("."+DB2ASCII.BACKUP_FILE_EXTENSION)){
      	fileName += "."+DB2ASCII.BACKUP_FILE_EXTENSION;	
      }
	  }

	  return fileName;
	}
}
