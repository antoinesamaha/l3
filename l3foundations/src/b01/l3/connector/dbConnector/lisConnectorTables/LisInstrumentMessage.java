package b01.l3.connector.dbConnector.lisConnectorTables;

import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocObject;

public class LisInstrumentMessage extends FocObject{

  public LisInstrumentMessage(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }
  
  public void setInstrumentCode(String instrumentCode){
    setPropertyString(LisInstrumentMessageDesc.FLD_INSTRUMENT_CODE, instrumentCode);
  }
  
  public void setMessage(String message){
    setPropertyString(LisInstrumentMessageDesc.FLD_MESSAGE, message);
  }
  
  public void setStatus(int status){
    setPropertyInteger(LisInstrumentMessageDesc.FLD_STATUS, status);
  }

}
