package b01.l3;

import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FBoolField;
import b01.foc.desc.field.FCharField;
import b01.foc.desc.field.FIntField;
import b01.foc.desc.field.FMultipleChoiceField;

public class L3ApplicationDesc extends FocDesc{
	public static final int FLD_APPLICATION_MODE       = 2;
	public static final int FLD_LAUNCH_AS_SERVICES     = 3;
	public static final int FLD_AUTOMATIC_PURGE        = 4;
	public static final int FLD_PURGE_NBR_DAYS_TO_KEEP = 5;
	public static final int FLD_PURGE_NBR_DAYS_TO_KEEP_FOR_COMMITED = 6;
	public static final int FLD_KEEP_FILES_FOR_DEBUG   = 7;

	public static final int FLD_APPLICATION_DIRECTORY  = 8 ;
	public static final int FLD_REMOTE_LAUNCHER_HOST   = 9 ;
	public static final int FLD_REMOTE_LAUNCHER_PORT   = 10;
	
	public L3ApplicationDesc(){
		super(L3Application.class, FocDesc.DB_RESIDENT, "L3_APPLICATION", false);
    addReferenceField();
    
    FMultipleChoiceField multiFld = new FMultipleChoiceField("MODE_O", "LIS connectors & Pools are in", FLD_APPLICATION_MODE, false, 1);
    multiFld.addChoice(L3Globals.APPLICATION_MODE_WITH_DB, "Multiple processes");
    multiFld.addChoice(L3Globals.APPLICATION_MODE_SAME_THREAD, "Same process");
    addField(multiFld);
    
    FBoolField automaticPurgeFld = new FBoolField("AUTO_PURGE", "Automatic purge", FLD_AUTOMATIC_PURGE, false);
    addField(automaticPurgeFld);

    automaticPurgeFld = new FBoolField("LAUNCH_MODE", "Launch as services", FLD_LAUNCH_AS_SERVICES, false);
    addField(automaticPurgeFld);

    FIntField intFld = new FIntField("PURGE_DAYS", "Delete everything older than (days)", FLD_PURGE_NBR_DAYS_TO_KEEP, false, 2);
    addField(intFld);
    
    intFld = new FIntField("PURGE_DAYS_COMMIT", "Delete commited older than (days)", FLD_PURGE_NBR_DAYS_TO_KEEP_FOR_COMMITED, false, 2);
    addField(intFld);
    
    FBoolField keepFilesForDebugFld = new FBoolField("KEEP_FILES", "Keep files for debug", FLD_KEEP_FILES_FOR_DEBUG, false);
    addField(keepFilesForDebugFld);

    FCharField charFld = new FCharField("APP_DIR", "Application directory", FLD_APPLICATION_DIRECTORY, false, 60);
    addField(charFld);

    charFld = new FCharField("LAUNCHER_HOST", "Launcher host", FLD_REMOTE_LAUNCHER_HOST, false, 30);
    addField(charFld);

    FIntField portFld = new FIntField("LAUNCHER_PORT", "Launcher port", FLD_REMOTE_LAUNCHER_PORT, false, 10);
    addField(portFld);
	}
}
