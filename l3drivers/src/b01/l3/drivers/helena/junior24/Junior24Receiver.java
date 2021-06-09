package b01.l3.drivers.helena.junior24;

import b01.foc.Globals;
import b01.foc.util.ASCII;
import b01.l3.L3Frame;
import b01.l3.connection.L3SerialPortListener;
import b01.l3.data.L3Message;
import b01.l3.data.L3Sample;
import b01.l3.data.L3Test;

public class Junior24Receiver implements L3SerialPortListener{
  private Junior24Driver driver  = null;
  private L3Message      message = null;
  private L3Sample       sample  = null;
    
  private static final int START_INDEX_OF_TESTS_WITHP_G_L = 5;
  private static final String[] JUNIOR_TESTS = {"HbA2", "HbF", "HbA", "A/G", "PT", "ALBUMINE", "ALPHA 1", "ALPHA 2", "BETA", "GAMMA"};
  
  public Junior24Receiver(Junior24Driver driver){
    this.driver    = driver;
    message        = null  ;
    sample         = null  ;
  }
  
  public void disposeMessage(){
    if(message != null){
      message.dispose();      
    }
    message = null;
    sample  = null;
  }
  
  public void dispose(){
    driver = null;
    disposeMessage();
  }
  
  public Junior24Driver getDriver(){
    return driver;
  }
  
  protected void initMessage(){
  	disposeMessage();
    message = new L3Message();
  }
  
  protected void sendMessageBackToInstrument(){
    driver.notifyListeners(message);
  }
 
  private String getTestCodeAt(int i){
		String testCode = JUNIOR_TESTS[i];
		if(testCode.compareTo("A/G") == 0){
			testCode = "Rapport Albumine/Globulines";
		}else if(testCode.compareTo("PT") == 0){
			testCode = "PROTIDES TOTAUX";
		}
		
		return testCode;
  }
  
  private boolean isTestWith_G_L(int i){
  	return i >= START_INDEX_OF_TESTS_WITHP_G_L; 
  }
  
  public int isATestLine(String data){
  	int testIndex = -1;
  	for(int i=0; i<JUNIOR_TESTS.length && testIndex == -1; i++){
  		String testCode = getTestCodeAt(i);
  		if(data.startsWith(testCode)){
  			testIndex = i;
  		}
  	}
  	return testIndex;
  }
  
  public void parseTestValue(String lisTestCode, String firstTestValue){
  	char lastChar = firstTestValue.charAt(firstTestValue.length() - 1);
  	if(lastChar == '+' || lastChar == '-'){
  		firstTestValue = firstTestValue.substring(0, firstTestValue.length()-1);
  	}
  	
  	boolean resultOk = false;
  	double value = 0;
  	String message = ""; 
  	try{
  		value = Double.valueOf(firstTestValue);
  		resultOk = true;
  	}catch(Exception e){
  		driver.getInstrument().logException(e);
  		resultOk = false;
  		message = "Could not convert to numeric : "+firstTestValue+" for test : "+firstTestValue;
  	}
  	
  	L3Test test = sample.addTest();
  	test.setLabel(lisTestCode);
  	test.setValue(value);
  	test.setResultOk(resultOk);
  	test.setNotificationMessage(message);
  }
  
  private void parseTestLine(String data, int testIndex){
  	String instTestLabel = getTestCodeAt(testIndex);
  	String testCode = JUNIOR_TESTS[testIndex];
  	String lisTestCode = driver.testMaps_getLisCode(testCode);
  	if(lisTestCode != null && sample != null){
    	String afterTestCode = data.substring(instTestLabel.length());
    	afterTestCode = afterTestCode.trim();
    	int firstSpaceIndex = afterTestCode.indexOf(ASCII.SPACE);
    	
    	String firstTestValue  = afterTestCode;
    	if(firstSpaceIndex > 0){
    		firstTestValue  = afterTestCode.substring(0, firstSpaceIndex);
    	}
    	
    	afterTestCode = afterTestCode.trim();
    	parseTestValue(lisTestCode, firstTestValue);
    	
    	if(isTestWith_G_L(testIndex)){
      	testCode += "(g/l)";
      	lisTestCode = driver.testMaps_getLisCode(testCode);
    		
	    	String secondTestValue = afterTestCode.substring(firstSpaceIndex);
	    	secondTestValue = secondTestValue.trim();
	    	int secondSpaceIndex = secondTestValue.indexOf(ASCII.SPACE);
	    	secondTestValue  = secondTestValue.substring(0, secondSpaceIndex);
	    	parseTestValue(lisTestCode, secondTestValue);
    	}
  	}
  }
  
  private void treatDataFrame(String data) throws Exception{
  	if(data.startsWith("Le ") || (data.indexOf("Le ") > 0 && data.indexOf("Le ") < 4)){
  		initMessage();
  		driver.reserve();
  	}else if(data.startsWith("NOM       : ")){
  	}else if(data.startsWith("NO DOSSIER: ")){
  		String sampleID = data.substring(11);
  		sampleID = sampleID.trim();
  		sample = new L3Sample(sampleID);
  		message.addSample(sample);
    }else if(data.startsWith("COURBE: ")){
      if(sample != null){
        String graph = data;
        graph = graph.replace(' ', ',');
        graph = graph.substring(8);
        sample.setGraph(graph);
        //COURBE: 0,0,0,0,0,1,1,3,6,8,11,15,19,24,26,30,33,37,41,48,53,58,62,67,73,81,91,101,112,123,136,154,177,204,235,267,312,387,514,705,956,1260,1611,2018,2470,2930,3346,3679,3914,4048,4080,4008,3834,3569,3231,2839,2414,1986,1584,1234,950,728,561,437,347,282,233,197,170,152,142,140,-1,147,164,193,223,250,271,280,278,265,244,220,196,177,161,148,139,133,126,123,122,-1,122,126,134,147,166,188,214,242,271,297,321,342,359,371,376,378,377,375,374,371,367,354,337,319,302,288,275,265,254,243,233,223,212,203,195,190,187,-1,187,193,205,223,244,269,298,331,367,405,439,466,485,497,499,496,492,488,486,486,488,486,482,472,457,438,420,403,385,368,352,338,327,318,314,311,-1,311,314,317,320,324,329,335,342,350,358,368,380,393,405,418,431,443,455,469,483,496,511,526,541,554,567,578,587,596,602,608,615,622,629,637,641,644,644,641,638,631,624,614,602,590,576,561,547,533,520,503,485,465,443,420,398,376,354,331,308,285,263,240,218,197,179,161,144,128,113,101,88,79,69,62,56,49,44,39,36,33,32,32,32,0
      }
  	}else{
    	int testIndex = isATestLine(data);
    	if(testIndex >= 0){
    		parseTestLine(data, testIndex);
    	}
  	}
  }
  
  public void received(L3Frame frame) {
    try{
    	frame.extractDataFromFrame();
    	String data = new String(frame.getData());

    	if(((Junior24Frame)frame).getType() == Junior24Frame.FRAME_TYPE_DATA){
    		treatDataFrame(data);
    	}else if(((Junior24Frame)frame).getType() == Junior24Frame.FRAME_TYPE_EOT){
    		sendMessageBackToInstrument();
    		disposeMessage();
    		driver.release();
    	}
    }catch(Exception e){
      Globals.logException(e);
    }
  }
}
