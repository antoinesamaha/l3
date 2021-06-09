package b01.l3.data;

import java.sql.Statement;

import b01.foc.ConfigInfo;
import b01.foc.Globals;
import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocObject;
import b01.foc.desc.field.FField;
import b01.foc.property.FReference;
import b01.l3.Instrument;

public class L3SampleMessageJoin extends FocObject{

  public L3SampleMessageJoin(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }
  
  public String getInstrumentCode(){
  	Instrument instr = (Instrument) getPropertyObject(L3SampleMessageJoinDesc.FLD_MESSAGE_FIELDS_START + L3InstrumentMessageDesc.FLD_INSTRUMENT);
  	return instr != null ? instr.getCode() : null;
  }

  public String getSampleId(){
  	return getPropertyString(L3SampleDesc.FLD_ID);
	}
  
  public String getMessage(){
  	return getPropertyString(L3SampleMessageJoinDesc.FLD_MESSAGE_FIELDS_START + L3InstrumentMessageDesc.FLD_MESSAGE);
  }
  
  public int getInstrumentMessageRef(){
  	FReference reference = (FReference) getFocProperty(L3SampleMessageJoinDesc.FLD_MESSAGE_FIELDS_START + FField.REF_FIELD_ID);  	
  	return reference != null ? reference.getInteger() : -1;
  }

  public void updateCommitedField(int status) throws Exception{
  	int ref = getInstrumentMessageRef();
  	
  	Statement stm = Globals.getDBManager().lockStatement();
    StringBuffer update  = new StringBuffer("UPDATE " + L3InstrumentMessageDesc.getInstance().getStorageName());
    update.append(" SET ");
    update.append(L3InstrumentMessageDesc.FNAME_STATUS + " = " + status);
    update.append(" WHERE "+FField.REF_FIELD_NAME+"="+ref);
    
  	if(ConfigInfo.isLogDBRequestActive()){
  		Globals.logString(update);
  	}
    stm.executeUpdate(update.toString());
		Globals.getDBManager().unlockStatement(stm);
		stm = null;
  }
}
