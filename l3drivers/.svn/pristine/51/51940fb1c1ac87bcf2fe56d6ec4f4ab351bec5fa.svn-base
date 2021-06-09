package b01.l3.drivers.roches;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;

import b01.foc.Globals;
import b01.foc.db.SQLFilter;
import b01.foc.desc.FocConstructor;
import b01.foc.list.FocLinkSimple;
import b01.foc.list.FocList;
import b01.l3.Instrument;
import b01.l3.L3Frame;
import b01.l3.TestLabelMap;
import b01.l3.data.L3Sample;
import b01.l3.data.L3SampleDesc;
import b01.l3.data.L3Test;
import b01.l3.drivers.unit.EmulatorDriver;

public class EmulatorDriverForCobas extends EmulatorDriver {
  HashMap <String,Integer> testLabelCodeMap = new HashMap<String, Integer>();
  Random rand;
  FocList list = null;
  String idModifier = "";
  int responseControler =0;
  int sequenceModifiere = 0;
  private String idileFrame1 = String.valueOf(Cobas400IntegraDriver.SOH)+String.valueOf(Cobas400IntegraDriver.LF)+
															"09 INTEGRA 30-1051  00"+Cobas400IntegraDriver.LF+Cobas400IntegraDriver.STX+Cobas400IntegraDriver.LF+
															Cobas400IntegraDriver.ETX+Cobas400IntegraDriver.LF+"1"+Cobas400IntegraDriver.LF+"299"+
															Cobas400IntegraDriver.LF+Cobas400IntegraDriver.EOT+Cobas400IntegraDriver.LF;
																				
	private String idileFrame0 = String.valueOf(Cobas400IntegraDriver.SOH)+String.valueOf(Cobas400IntegraDriver.LF)+
															"09 INTEGRA 30-1051  00"+Cobas400IntegraDriver.LF+Cobas400IntegraDriver.STX+Cobas400IntegraDriver.LF+
															Cobas400IntegraDriver.ETX+Cobas400IntegraDriver.LF+"0"+Cobas400IntegraDriver.LF+"298"+
															Cobas400IntegraDriver.LF+Cobas400IntegraDriver.EOT+Cobas400IntegraDriver.LF;

	
	private String resultFrame0 = String.valueOf(Cobas400IntegraDriver.SOH)+String.valueOf(Cobas400IntegraDriver.LF)+
																"09 INTDGRA 30-1051  04"+
																Cobas400IntegraDriver.LF+Cobas400IntegraDriver.STX+Cobas400IntegraDriver.LF+
																"53 idsm21111111111 27/06/1998 URL"+
																Cobas400IntegraDriver.LF+
																"55  71"+
																Cobas400IntegraDriver.LF+
																"00  0.700000E+30ouG/ML  84   0  22   0   0.000000E+00"+
																    							
																Cobas400IntegraDriver.LF+Cobas400IntegraDriver.ETX+Cobas400IntegraDriver.LF+
																"0"+Cobas400IntegraDriver.LF+
																"106"+Cobas400IntegraDriver.LF+
																Cobas400IntegraDriver.EOT+Cobas400IntegraDriver.LF;

	
	private String resultFrame1 = String.valueOf(Cobas400IntegraDriver.SOH)+String.valueOf(Cobas400IntegraDriver.LF)+
																"09 INTDGRA 30-1051  04"+
																Cobas400IntegraDriver.LF+Cobas400IntegraDriver.STX+Cobas400IntegraDriver.LF+
																"53 idsm21111111111 27/06/1998 URL"+
																Cobas400IntegraDriver.LF+
																"55  71"+
																Cobas400IntegraDriver.LF+
																"00  0.700000E+30ouG/ML  84   0  22   0   0.000000E+00"+
																    							
																Cobas400IntegraDriver.LF+Cobas400IntegraDriver.ETX+Cobas400IntegraDriver.LF+
																"1"+Cobas400IntegraDriver.LF+
																"107"+Cobas400IntegraDriver.LF+
																Cobas400IntegraDriver.EOT+Cobas400IntegraDriver.LF;
	
	public void init(Instrument instrument, Properties props) throws Exception {
  	super.init(instrument, props);
  	setAnswerFrame(new Cobas400Frame(this.getInstrument()));
    fillTestLabelCodeMap();
    Globals.logString("dans init de emulator");
    rand = new Random();
  }
  
  private void switchIdModifier(){
    if (idModifier.equals("")){
      idModifier = "1";
    }else{
      idModifier = "";
    }
  }
  
  private int getResponseControler(){
    responseControler += 1;
    return responseControler;
  }
  
  private int getSequence(int seq){
    /*if (sequenceModifiere ==1){
      sequenceModifiere =0;
    }else{
      sequenceModifiere =1;
    }
    return (seq + sequenceModifiere)%2;*/
    return rand.nextInt(2) ;
  }
  
  private void  fillTestLabelCodeMap (){
    FocList list = new FocList(new FocLinkSimple(TestLabelMap.getFocDesc()));
    //list.loadIfNotLoadedFromDB();
    list.reloadFromDB();
    for (int i = 0; i < list.size(); i++){
      TestLabelMap tlm = (TestLabelMap)list.getFocObject(i);
      testLabelCodeMap.put(tlm.getLisTestLabel(), Integer.valueOf(tlm.getInstrumentTestCode()));
    }
  }
  
  private String dynamicResult(Cobas400Frame frm){
    Globals.logString("debut dynamic result");
    StringBuffer result =new StringBuffer();
    
    if (list == null){
      FocConstructor constr = new FocConstructor(Instrument.getFocDesc(), null);    
      Instrument instrumentTemplate = (Instrument) constr.newItem();
      SQLFilter filter = new SQLFilter(instrumentTemplate,SQLFilter.FILTER_ON_SELECTED);
      StringBuffer condition = new StringBuffer();
      condition.append("REF = 2");
      filter.addAdditionalWhere(condition);
      //
      FocList instrumentList = new FocList(new FocLinkSimple(Instrument.getFocDesc()),filter);
      instrumentList.loadIfNotLoadedFromDB();
      Instrument instr = (Instrument)instrumentList.getFocObject(0);
      //HHH list = instr.getSampleListWhereStatus(L3SampleDesc.SAMPLE_STATUS_ANALYSING);
    }
    if(list.size() == 0 ){
      list.reloadFromDB();
    }
    Globals.logString("size of sample list where status is 3 is " + list.size());
    if (list.size() == 0){
      if (frm.getSequence()==0){
        result.append(idileFrame0);
      }else{
        result.append(idileFrame1);
      } 
      /*if(list.size() == 0 ){
        list.reloadFromDB();
      }*/
      return String.valueOf(result);
    }
    
    result.append(String.valueOf(Cobas400IntegraDriver.SOH)+String.valueOf(Cobas400IntegraDriver.LF)+
                                "09 INTDGRA 30-1051  04"+
                                Cobas400IntegraDriver.LF+Cobas400IntegraDriver.STX+Cobas400IntegraDriver.LF);
    //for (int i = 0; i< list.size(); i++){
    //L3Sample sample = (L3Sample)list.getFocObject(i);
    L3Sample sample = (L3Sample)list.getFocObject(0);
    result.append("53 ");
    result.append(sample.getId());
    result.append(idModifier);
    int length = sample.getId().length();
    for(int j = length; j < 15 - idModifier.length(); j++){
      result.append(" ");
    }
    switchIdModifier();
    result.append(" 27/06/1998 URL");
    result.append(Cobas400IntegraDriver.LF);
    // ajouter les test et les results
    Iterator testIterator = sample.testIterator();
    while (testIterator.hasNext()){
      L3Test test = (L3Test)testIterator.next();
      result.append("55 ");
      //int code = TestLabelMap.getCobasTestCode(test.getLabel());
      int code = testLabelCodeMap.get(test.getLabel());
      String testCode = String.valueOf(code);
      for(int k = testCode.length(); k < 3; k++){
        result.append(" ");
      }
      result.append(testCode);
      result.append(Cobas400IntegraDriver.LF);
      //result.append("00  0.700000E+30ouG/ML  84   0  22   0   0.000000E+00");
      String res = String.valueOf(rand.nextInt(100));
      result.append("00 ");
      for (int r = res.length(); r < 13; r++ ){// 13 caractere et un space entre le lign code et le resultat numerique
        result.append(" ");
      }
      result.append(res);
      result.append("ouG/ML  84   0  22   0   0.000000E+00");
      result.append(Cobas400IntegraDriver.LF);
      }
    list.remove(sample);
    //}
    result.append(Cobas400IntegraDriver.ETX);
    result.append(Cobas400IntegraDriver.LF);
    //result.append(frm.getSequence());
    //result.append(String.valueOf((Integer.valueOf(frm.getSequence())+1)%2));
    result.append(getSequence(frm.getSequence()));
    result.append(Cobas400IntegraDriver.LF);
    String checkSum = String.valueOf(Cobas400Frame.computeCheckSum(result, -1));
    for (int i=0; i < 3-checkSum.length();i++){
      result.append(String.valueOf(0));
    }
    result.append(checkSum);
    result.append(Cobas400IntegraDriver.LF);
    result.append(Cobas400IntegraDriver.EOT);
    result.append(Cobas400IntegraDriver.LF);
    Globals.logString("fin dynamic result");
    return String.valueOf(result);
  }

	public void replyToFrame(L3Frame frame){
    Globals.logString("dans reply to frame");
  	Cobas400Frame frm = (Cobas400Frame)frame;
    try {	  	
			frm.extractDataFromFrame();					
			if (String.valueOf(frm.getBlockCode()).equals("0")){
			//if (frm.getBlockCode()==Cobas400IntegraDriver.BLOCK_CODE_SYNCHRONIZATION){
				send(idileFrame1);
				
			}else{
				if (String.valueOf(frm.getBlockCode()).equals("9")){
          Globals.logString("avant send dynamic result");
          send(dynamicResult(frm));
          /*for (int ll = 0; ll < 100 ; ll++){
            Globals.logString("aprs send dynamic reslt");
          }*/
					/*if (frm.getSequence()==0){
						send(resultFrame0);
            Globals.logString("message returned by emulator : " + dynamicResult(frm));
					}else{
            Globals.logString("message returned by emulator : " + dynamicResult(frm));
            send(resultFrame1);
					}*/		
				}else{
					if (String.valueOf(frm.getBlockCode()).equals("10") && getResponseControler()%5 !=0 ){
						if (frm.getSequence()==0){
							send(idileFrame0);
						}else{
							send(idileFrame1);
						}
					}
				}
			}		
				
  	} catch (Exception e) {
			Globals.logException(e);
		}
  	
	}	
}