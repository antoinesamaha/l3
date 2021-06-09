package b01.l3.data;

import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocObject;

public class L3InstrumentGraph extends FocObject{
  
  public L3InstrumentGraph(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }

  public L3InstrumentGraph(){
  	super(new FocConstructor(L3InstrumentGraphDesc.getInstance(), null));
    newFocProperties();
  }

  public L3InstrumentGraph(String instrumentCode, String sampleID){
    this();
    setInstrumentCode(instrumentCode);
    setSampleID(sampleID);
    setCreated(true);
    loadReferenceFromDatabaseAccordingToKey();
  }
  
  public String getInstrumentCode(){
    return getPropertyString(L3InstrumentGraphDesc.FLD_INSTRUMENT_CODE);
  }

  public void setInstrumentCode(String code){
    setPropertyString(L3InstrumentGraphDesc.FLD_INSTRUMENT_CODE, code);
  }

  public String getSampleID(){
    return getPropertyString(L3InstrumentGraphDesc.FLD_SAMPLE_ID);
  }

  public void setSampleID(String id){
    setPropertyString(L3InstrumentGraphDesc.FLD_SAMPLE_ID, id);
  }

  public void setStatus(int status){
    setPropertyMultiChoice(L3InstrumentGraphDesc.FLD_STATUS, status);
  }

  public void setGraph(String graph){
    setPropertyString(L3InstrumentGraphDesc.FLD_GRAPH, graph);
  }
}
