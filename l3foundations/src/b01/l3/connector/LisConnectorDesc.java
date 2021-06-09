package b01.l3.connector;

import java.util.Iterator;

import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FBoolField;
import b01.foc.desc.field.FCharField;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FIntField;
import b01.foc.desc.field.FMultipleChoiceFieldStringBased;
import b01.foc.desc.field.FObjectField;
import b01.foc.list.FocList;
import b01.l3.FileIOFactory;
import b01.l3.PoolKernel;
import b01.l3.PoolKernelDesc;

public class LisConnectorDesc extends FocDesc {

	public static final int FLD_NAME = 1;
	public static final int FLD_ROOT_DIR = 2;
	public static final int FLD_RECEIVE_DIR = 3;
	public static final int FLD_SEND_DIR = 4;
	public static final int FLD_ERROR_DIR = 5;
	public static final int FLD_ARCHIVE_DIR = 6;
  public static final int FLD_CONNECTED = 7;
  public static final int FLD_POOL = 8;
  public static final int FLD_FILE_IO_CLASS_NAME = 9;
  public static final int FLD_CONNECTOR_CLASS_NAME = 10;
  public static final int FLD_SERVICE_HOST         = 11;
  public static final int FLD_SERVICE_PORT         = 12;
  public static final int FLD_LAUNCHED             = 13;
	
	public LisConnectorDesc(){
		super(LisConnector.class, FocDesc.DB_RESIDENT, "LIS_FILE_CONNECTOR", true);
    FField focFld = addReferenceField();
    
    //setConcurrenceLockEnabled(true);
    //concurrenceLockView_AddField(FLD_NAME);
    
    focFld = new FCharField("NAME", "Name", FLD_NAME, true, 30);
    addField(focFld);
    focFld = new FCharField("ROOT_DIR", "Root directory", FLD_ROOT_DIR, false, 30);
    addField(focFld);
    focFld = new FCharField("IN_DIR", "In directory", FLD_RECEIVE_DIR, false, 30);
    addField(focFld);
    focFld = new FCharField("OUT_DIR", "Out directory", FLD_SEND_DIR, false, 30);
    addField(focFld);
    focFld = new FCharField("ERROR_DIR", "Error directory", FLD_ERROR_DIR, false, 30);
    addField(focFld);
    focFld = new FCharField("ARCHIVE_DIR", "Archive directory", FLD_ARCHIVE_DIR, false, 30);
    addField(focFld);
    focFld = new FBoolField("CONNECTED","Connected",FLD_CONNECTED,false);
    addField(focFld);
    FObjectField fobjectField = new FObjectField("POOL", "Pool", FLD_POOL, false, PoolKernel.getFocDesc(), "POOL");
    fobjectField.setComboBoxCellEditor(PoolKernelDesc.FLD_NAME);
    fobjectField.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    fobjectField.setSelectionList(PoolKernel.getList(FocList.NONE));
    addField(fobjectField);
    
    FMultipleChoiceFieldStringBased mcsFld = new FMultipleChoiceFieldStringBased("FILEIO_CLASS_NAME", "File IO class name", FLD_FILE_IO_CLASS_NAME, false, 50);
    Iterator<String> iter = FileIOFactory.getInstance().newClassNameIterator();
    while(iter != null && iter.hasNext()){
    	mcsFld.addChoice(iter.next());
    }
    addField(mcsFld);	  
    
    mcsFld = new FMultipleChoiceFieldStringBased("CONNECTOR_CLASS_NAME", "Connector class name", FLD_CONNECTOR_CLASS_NAME, false, 50);
    iter = LisConnectorFactory.getInstance().newClassNameIterator();
    while(iter != null && iter.hasNext()){
    	mcsFld.addChoice(iter.next());
    }
    addField(mcsFld);
  
    focFld = new FCharField("SERVICE_HOST", "Service Host", FLD_SERVICE_HOST, false, 30);
    addField(focFld);
    
    FIntField iFld = new FIntField("SERVICE_PORT", "Service port", FLD_SERVICE_PORT, false, 4);
    addField(iFld);

    focFld = new FBoolField("LAUNCHED", "Launched", FLD_LAUNCHED, false);
    focFld.setDBResident(false);
    addField(focFld);
	}
  
	private static FocDesc focDesc = null;
  public static FocDesc getInstance() {
    if (focDesc==null){
      focDesc = new /*XXX*/ LisConnectorDesc();
    }
    return focDesc;
  }
}
