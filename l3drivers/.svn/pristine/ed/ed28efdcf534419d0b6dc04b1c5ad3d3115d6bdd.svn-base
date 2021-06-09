/*
 * Created on Jun 14, 2006
 */
package b01.l3.drivers.dadeBehring.bct;

import java.util.StringTokenizer;

import b01.foc.Globals;
import b01.foc.util.ASCII;
import b01.l3.L3Frame;
import b01.l3.connection.L3SerialPortListener;
import b01.l3.data.L3Message;
import b01.l3.data.L3Sample;
import b01.l3.data.L3Test;
import b01.l3.drivers.astm.AstmFrame;
import b01.l3.exceptions.L3UnexpectedFrameSequenceException;

/**
 * @author 01Barmaja
 */
public class BCTReceiver implements L3SerialPortListener{
  protected BCTDriver driver  = null;
  protected L3Message message = null;
  protected L3Sample  sample  = null;
  protected L3Test    test    = null;

  private int expectedSequence = -1;
  
  private AstmFrame ackFrame  = null;
  private AstmFrame nackFrame = null;
  
//  private PatientLineReader patientLineReader = null;
//  private OrderLineReader   orderLineReader   = null;
//  private ResultLineReader  resultLineReader  = null;
//  private CommentLineReader commentLineReader = null;

  public BCTReceiver(BCTDriver driver){
    this.driver = driver;
    message     = null;
    sample      = null;
    test        = null;
    
/*    
    patientLineReader = new PatientLineReader();
    orderLineReader   = new OrderLineReader();
    resultLineReader  = new ResultLineReader(driver);
    commentLineReader = new CommentLineReader();
*/  }
  
  public void disposeMessage(){
    if(message != null){
      message.dispose();      
    }
    message = null;
    sample  = null;
    test    = null;
  }
  
  public void dispose(){
    driver = null;

    disposeMessage();

    if(ackFrame != null){
    	ackFrame.dispose();
    	ackFrame = null;
    }
    
    if(nackFrame != null){
    	nackFrame.dispose();
    	nackFrame = null;
    }
    
/*    if(patientLineReader != null){
    	patientLineReader.dispose();
    	patientLineReader = null;
    }
    
    if(orderLineReader != null){
    	orderLineReader.dispose();
    	orderLineReader = null;
    }
    
    if(resultLineReader != null){
    	resultLineReader.dispose();
    	resultLineReader = null;
    }
    
    if(commentLineReader != null){
    	commentLineReader.dispose();
    	commentLineReader = null;
    }    
*/  }
  
  public BCTDriver getDriver(){
    return driver;
  }
  
  private AstmFrame getAckFrame(){ 
  	if(ackFrame == null){
  		ackFrame = AstmFrame.newAcknowlegeFrame(driver.getInstrument());
  		try{
  			ackFrame.createDataWithFrame();
  		}catch(Exception e){
  			ackFrame.logException(e);
  		}
  	}
  	return ackFrame;
  }

  private AstmFrame getNackFrame(){ 
  	if(nackFrame == null){
  		nackFrame = AstmFrame.newNotAcknowlegFrame(driver.getInstrument());
  		try{
  			nackFrame.createDataWithFrame();
  		}catch(Exception e){
  			nackFrame.logException(e);
  		}
  	}
  	return nackFrame;
  }
  
  private void resetExpectedSequence(){
    expectedSequence = -1;
  }
  
  private int getNextExpectedSequence(){
    int next = expectedSequence + 1;  	
    if(next == 8) next = 0;
    return next;
  }
  
  private int incrementExpectedSequence(){
    expectedSequence ++;    
    if(expectedSequence == 8) expectedSequence = 0;
    return expectedSequence;
  }
  
  protected void parsePatientFrame(StringBuffer data){
//  	patientLineReader.scanTokens(data);
  }

  protected void parseOrderFrame(StringBuffer data){
  	/*
  	orderLineReader.scanTokens(data);
  	
  	if(sample != null){
    //if(sample != null && sample.getId() != null && orderLineReader.getSampleId() != null ){
  		if(sample.getId().compareTo(orderLineReader.getSampleId()) != 0){
  			sample = message.findSample(orderLineReader.getSampleId());
  			test = null;
  		}
  	}
  	if(sample == null){
  		sample = new L3Sample(orderLineReader.getSampleId());
  		sample.setFirstName(patientLineReader.getFirstName());
  		sample.setLastName(patientLineReader.getLastName());
  		sample.setMiddleInitial(patientLineReader.getMidInitial());
  		message.addSample(sample);
  	}
  	
  	String lisTestCode = driver.testMaps_getLisCode(orderLineReader.getTestLabel());
  	if(lisTestCode != null && lisTestCode.trim().compareTo("") != 0){
  		if(driver.getAstmParams().isCheckResultFrameTestCodeWithOrderFrameTestCode()){
	  		test = sample.addTest();
	  		test.setLabel(lisTestCode);
  		}
  	}
  	*/
  }
  
  protected void parseResultFrame(StringBuffer data){
  	boolean error = false;
  	
  	String  sampleID = null ;
  	int     testCode = -1   ;
  	double  value    = 0    ;
  	boolean resultOK = false;
  	String  notificationMessage = "";
  
  	int spacePos = data.indexOf(" ", 1);
  	
  	error = spacePos <= 0;
  	
  	Globals.logString("In The parsing results frame : data = "+data+"\n spacePos = "+spacePos);
  	
  	if(!error){
	  	sampleID = data.substring(0, spacePos);
	  	sampleID = sampleID.trim();
	  	error = sampleID.compareTo("") == 0;
  	}
  	
  	Globals.logString("Error = "+error+" sampleID = "+sampleID);
  	
  	if(!error){
  		int pos = 0;
  		int tkn = 0;
  		StringTokenizer tokenizer = new StringTokenizer(data.substring(spacePos), String.valueOf(ASCII.SPACE), false);
  		while(tokenizer.hasMoreTokens()){
  			String token = tokenizer.nextToken();
  			if(token != null){
  		  	Globals.logString("Token = "+token+" pos = "+pos+" tkn = "+tkn);

  				if(pos == 0){
  					try{
  						testCode = Integer.valueOf(token).intValue();
  						pos++;
  					}catch(Exception e){
  						//We do not want to write the exceptions every time we have a space in the patien name!!!
  					}
  				}else if(pos == 1){
  					try{
  						value = Double.valueOf(token).doubleValue();
  						resultOK = true;
  					}catch(Exception e){
  						resultOK = false;
  						notificationMessage  = "Could not parse result : "+token+" in frame : "+data;
  						driver.getInstrument().logString("Could not parse result");
  						driver.getInstrument().logException(e);
  					}
  					pos++;
	  			}
  			}
  			tkn++;
  		}

	  	String lisTestCode = driver.testMaps_getLisCode(String.valueOf(testCode));
	  	error = lisTestCode == null || lisTestCode.compareTo("") == 0;
	  	if(!error){
	  		sample = message.findSample(sampleID);
	  		if(sample == null){
	  			sample = new L3Sample(sampleID);
	  			message.addSample(sample);
	  		}
	  		
	  		L3Test test = sample.addTest();
	  		test.setLabel(lisTestCode);
	  		test.setValue(value);
	  		test.setResultOk(resultOK);
	  		test.setNotificationMessage(notificationMessage);
	  	}
  	}  	
  }

  protected void parseCommentFrame(StringBuffer data){
  	if(driver.getAstmParams().isReadComment3()){
	  	//commentLineReader.scanTokens(data);
	  	//String comment3 = commentLineReader.getComment3();
  	}
  }

  protected void initMessage(){
  	message = new L3Message();
  }
  
  protected void sendMessageBackToInstrument(){
  	driver.notifyListeners(message);
  }
    
  protected int treatReceivedFrame(AstmFrame frame){
  	int frameTypeToReturn = AstmFrame.FRAME_TYPE_ACK;
  	try{
  		frame.extractDataFromFrame();
  		
  		int expectedSequence = getNextExpectedSequence();
  		if(frame.getSequence() != AstmFrame.SEQUENCE_IRRELEVANT && expectedSequence != frame.getSequence()){       
        throw new L3UnexpectedFrameSequenceException("Sequence expected : "+expectedSequence+" found:"+frame.getSequence());
  		}
  		
	  	StringBuffer data = frame.getData();
      
	  	if(frame.getType() == AstmFrame.FRAME_TYPE_ENQ){
	      if(driver.reserve()){
	      	frameTypeToReturn = AstmFrame.FRAME_TYPE_NACK;
	      }else{
		      disposeMessage();
		      initMessage();
	      	resetExpectedSequence();
          getNextExpectedSequence();
	      	frameTypeToReturn = AstmFrame.FRAME_TYPE_ACK;
	      }
	  	}else if(frame.getType() == AstmFrame.FRAME_TYPE_EOT){
	  		frameTypeToReturn = AstmFrame.FRAME_TYPE_NONE;
	  		driver.release();
	  		sendMessageBackToInstrument();
	      disposeMessage();
	  	}else if(frame.getType() == AstmFrame.FRAME_TYPE_RESULT){
	  		parseResultFrame(data);
	  		//driver.getInstrument().logString(" BCT RECEIVED RESULT : "+data);
	  	}else{
	  		//driver.getInstrument().logString(" BCT RECEIVED : "+data);
	  	}
  	}catch(Exception e){
  		driver.getInstrument().logException(e);
  		frameTypeToReturn = AstmFrame.FRAME_TYPE_NACK;
  	}
  	
    return frameTypeToReturn;
  }
  
  /* (non-Javadoc)
   * @see b01.l3.connection.L3SerialPortListener#received(b01.l3.L3Frame)
   */
  public void received(L3Frame frame) {
    try{
      BCTFrame f = (BCTFrame)frame;
      if(f != null){
        switch(treatReceivedFrame(f)){
        case BCTFrame.FRAME_TYPE_ACK:
          incrementExpectedSequence();
        	driver.send(getAckFrame().getDataWithFrame().toString());
          break;
        case BCTFrame.FRAME_TYPE_NACK:
        	driver.send(getNackFrame().getDataWithFrame().toString());
          break;          
        case BCTFrame.FRAME_TYPE_NONE:
        	break;
        }
     }
    }catch(Exception e){
       Globals.logException(e);
    }
  }
}