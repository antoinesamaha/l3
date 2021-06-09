package b01.l3.data;

import b01.foc.db.SQLSelectExistance;
import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocObject;
import b01.l3.Instrument;

public class L3InstrumentMessage extends FocObject{
  
  public L3InstrumentMessage(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }

  public L3InstrumentMessage(){
  	super(new FocConstructor(L3InstrumentMessageDesc.getInstance(), null));
  }
  
  public void setInstrument(Instrument instrument){
    setPropertyObject(L3InstrumentMessageDesc.FLD_INSTRUMENT, instrument);
  }
  
  public Instrument getInstrument(){
    return (Instrument) getPropertyObject(L3InstrumentMessageDesc.FLD_INSTRUMENT);
  }
  
  public L3Sample getL3Sample(){
    return (L3Sample) getPropertyObject(L3InstrumentMessageDesc.FLD_L3_SAMPLE);
  }

  public void setL3Sample(L3Sample l3Sample){
    setPropertyObject(L3InstrumentMessageDesc.FLD_L3_SAMPLE, l3Sample);
  }

  public void setMessage(String message){
    setPropertyString(L3InstrumentMessageDesc.FLD_MESSAGE, message);
  }
  
  public String getMessage(){
    return getPropertyString(L3InstrumentMessageDesc.FLD_MESSAGE);
  }
  
  public void setStatus(int status){
    setPropertyMultiChoice(L3InstrumentMessageDesc.FLD_STATUS, status);
  }
  
  public boolean existInDB(){
  	StringBuffer sqlWhere = new StringBuffer(L3InstrumentMessageDesc.FNAME_SAMPLE_PREFIX);
  	sqlWhere.append("REF=");
  	sqlWhere.append(getL3Sample().getReference().getInteger());
  	sqlWhere.append(" AND ");

  	sqlWhere.append(L3InstrumentMessageDesc.FNAME_INSTRUMENT_PREFIX);
  	sqlWhere.append("REF=");
  	sqlWhere.append(getInstrument().getReference().getInteger());

  	SQLSelectExistance selectExistance = new SQLSelectExistance(L3InstrumentMessageDesc.getInstance(), sqlWhere);
  	selectExistance.execute();
  	boolean exists = selectExistance.getExist() == SQLSelectExistance.EXIST_YES;
  	selectExistance.dispose();
  	return exists;
  }
}
