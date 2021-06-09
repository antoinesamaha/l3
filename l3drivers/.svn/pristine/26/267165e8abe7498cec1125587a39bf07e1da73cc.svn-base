package b01.l3.drivers.horiba.pentraML;

import java.util.ArrayList;
import java.util.Iterator;

import b01.foc.list.FocList;
import b01.l3.data.L3InstrumentMessage;
import b01.l3.data.L3Message;
import b01.l3.data.L3Sample;
import b01.l3.data.L3Test;
import b01.l3.drivers.astm.AstmDriver;

public class PentraMLDriver extends AstmDriver {
	
	private static String[] messagesToTransmit = {
		"Leucocytosis",
		"Leucopenia",
		"Lymphocytosis",
		"Lymphopenia",
		"Neutrophilia",
		"Neutropenia",
		"Eosinophilia",
		"Myelemia",
		"Large immature cell",
		"Atipical lymphocyte",
		"Left shift",
		"Erythroblasts",
		"Monocytosis",
		"Basophilia",
		"Pancytopenia",
		"Blasts",
		"Erythrocytosis",
		"Cold agglutinins",
		"Anemia",
		"Macrocytosis",
		"Microcytosis",
		"Anitocytosis",
		"Hypochromia",
		"Polkylocytosis",
		
		"Thrombocytosis",
		"Thrombocytopenia",
		"Schistocytes",
		"Small Cell",
		"Macroplatelets",
		"Platelet aggregates",
		"Erythroblasts",
		
		"Erythroblasts and Platelet aggregate",
		"Blast 1",
		"Blast 2",
		"Immature Granulocyt",
		"Immature Monocytes",
		"Immature lymphocytes",
		
		"IDA",
		"Thalassemia suspicion",
		"Spherocytosis suspicion",
		"Spherocytosis"
		
	};
	
	public PentraMLDriver(){
		super();
		frameCreator.dispose();
  	frameCreator = new PentraMLFrameCreator();
		//getAstmParams().setPhysicalMachineInfo(new b01.l3.drivers.abbott.axsym.PhMaInfo());
  		getAstmParams().setPhysicalMachineInfo(new b01.l3.drivers.horiba.pentraML.PhMaInfo());
  		getAstmParams().setCheckResultFrameTestCodeWithOrderFrameTestCode(false);
  		getAstmParams().setSendPatientIdToInstrument(true);
  		getAstmParams().setSendProfileInsteadOfTestID(true);
  		getAstmParams().setSendPatientAgeAndSex(true);
  		getAstmParams().setSendPatientDateOfBirth(true);
  		getAstmParams().setReadComment1(true);
  		getAstmParams().setSlaveBehaviour(true);
  		getAstmParams().setTakeAllFramesFromBufferNotJustTheLast(true);
  		getAstmParams().setReleaseWhenReceivedENQ(true);
//  		getAstmParams().setAcceptToStartAtFrame1Directly(true);
	}
	
	private boolean transmitMessage(String message){
		boolean include = false;
		for(int i=0; i<messagesToTransmit.length && !include; i++){
			include = message.startsWith(messagesToTransmit[i]);
		}
		return include;
	}
	
	@Override
	public void makeSpecialCommentTreatmentBeforeSendingToLIS(L3Message message){
	    Iterator sampleIterator = message.sampleIterator();
	    while (sampleIterator != null && sampleIterator.hasNext()){
	      L3Sample sample = (L3Sample)sampleIterator.next();

	      FocList messageList = sample.getInstrumentMessageListWithoutLoad();
	      if(messageList != null && messageList.size() > 0){
		    ArrayList<L3InstrumentMessage> instMessageToDelete = new ArrayList<L3InstrumentMessage>(); 
	      	for(int i=0; i<messageList.size(); i++){
	      		L3InstrumentMessage instrumentMessage = (L3InstrumentMessage) messageList.getFocObject(i);
	      		if(instrumentMessage.getMessage().startsWith("Verification pending")){
	  		      FocList testList = sample.getTestListWithoutLoad();
	  		      Iterator iter = testList.focObjectIterator();
	  		      while(iter != null && iter.hasNext()){
	  		    	L3Test memoryTest = (L3Test) iter.next();	      			
	  		    	memoryTest.setVerificationPendingFlag(true);
	  		      }
	  				
	  		      instMessageToDelete.add(instrumentMessage);
	      		}else if(instrumentMessage.getMessage().startsWith("ML DISABLED")){
	      		  instMessageToDelete.add(instrumentMessage);
	      		}else if(transmitMessage(instrumentMessage.getMessage())){
	      		  //In this case we do not delete the message
	      		  //instMessageToDelete.add(instrumentMessage);
	      		}else{//Because we have a lot of messages that the lab does not realy need
	      		  instMessageToDelete.add(instrumentMessage);
	      		}
	      	}
	      	for(int i=0; i<instMessageToDelete.size(); i++){
	      		L3InstrumentMessage instrumentMessage = (L3InstrumentMessage) instMessageToDelete.get(i);
	      		messageList.remove(instrumentMessage);
	      	}
	      }
	    }
	}
}
