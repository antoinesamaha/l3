package b01.l3.connector.fileConnector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import b01.foc.Globals;
import b01.foc.file.DirSet;
import b01.foc.file.FocFileReader;
import b01.l3.L3Application;
import b01.l3.data.L3Message;
import b01.l3.data.L3Sample;
import b01.l3.data.L3Test;

public class DefaultFileIO extends FocFileReader {
	protected L3Message message = null;
	protected L3Sample sample = null;
	
	private   File           fileToPost           = null;
	private   BufferedWriter fileToPostWriter     = null;
	private   File           fileArchToPost       = null;
	private   BufferedWriter fileArchToPostWriter = null;
	
	protected int POS_CUR_DATE=1;
  protected int POS_CUR_ITME=2;
  protected int POS_PATIENT_ID=3;
	protected int POS_SAMPLE_ID = 4;
	protected int POS_LIQ_TYPE =5;
	protected int POS_COLL_DATE=6;	
	protected int POS_FIRST_NAME = 7;
	protected int POS_LAST_NAME = 8;
	protected int POS_MID_NAME = 9;
	protected int POS_AGE = 10;
	protected int POS_SEXE = 11;
	protected int POS_FIRST_TEST_ID = 12;
  
/*CURRENT_DATE: dd/mm/yyyy
	CURRENT_TIME: HH:MM:SS
	ACCESSION_NUMBER: String as it figures in PROG
	LIQUID_TYPE: String containing one off the following -> SERUM, URIN, CSF
	COLLECTION_DATE: HH:MM:SS
	PATIENT_FIRST_NAME: String containing the patient first name
	PATIENT_LAST_NAME: String containing the patient last name
	PATIENT_MIDDLE_INITIAL: One character
	PATIENT_AGE: Integer in number of years 
	PATIENT_SEX: One character (M or F)
	TEST(1-n):The test label as it figures in Prog
	*/

	public DefaultFileIO(){
		super();
		sample = null;
	}

	public boolean setFile(File file){
		boolean ret = super.setFile(file);
		if(message != null){
			message.dispose();
			message = null;
		}
		message = new L3Message();
		sample  = null;
		return ret;
	}
	
	
	public L3Message getL3Message(){
		return message;
	}	

	protected L3Sample newSample(){
		L3Sample sample = new L3Sample("");
		sample.setDbResident(false);
		return sample; 
	}

	protected void addSampleToMessage(L3Sample sample){
		message.addSample(sample);
	}

	protected L3Sample cloneSample(L3Sample src){
		L3Sample sample = newSample();
    sample.copyWithoutTests(src);
		return sample; 
	}

	protected void addTestToSample(L3Sample sample, String testLabel){
		L3Test test = sample.addTest();
		test.setLabel(testLabel);
	}
	
	@Override
	public void readLine(StringBuffer buffer) {
		sample = newSample();
		scanTokens(buffer);
		addSampleToMessage(sample);
	}

	@Override
  public void readToken(String token, int pos) {
    readWriteField(true, pos, token); 
  }
/*	public void readToken(String token, int pos) {
		Globals.logString("Pos : " +pos +" Token : "+token);
    switch(pos){
		
    case POS_PATIENT_ID:
      sample.setPatientId(token);
      break;
		case POS_SAMPLE_ID:			
			sample.setId(token);
			//sample.setId(token.substring(4));
			break;
    case POS_LIQ_TYPE :
      sample.setLiquidType(token);
      break;
		case POS_FIRST_NAME:
			sample.setFirstName(token);
			break;
		case POS_LAST_NAME:
			sample.setLastName(token);
			break;
		case POS_MID_NAME:
			sample.setMiddleInitial(token);
			break;
    case POS_SEXE:
      sample.setSexe(token);
      break;
		default:
			if(pos >= POS_FIRST_TEST_ID){
				addTestToSample(sample, token);
			}
			break;
		}
	}*/
  
  protected String readWriteCurrentDate(boolean read, String token){
    if(read){
      return "";
    }else{
      Date d = new Date(123);
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

      d.setTime(System.currentTimeMillis());
      String currentDate = simpleDateFormat.format(d);
      return "|" + currentDate;
    }
  }
  
  protected String readWriteCurrentTime(boolean read, String token){
    if(read){
      return "";
    }else{
      Date d = new Date(123);
      SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss");
      
      d.setTime(System.currentTimeMillis());
      String currentTime = simpleTimeFormat.format(d);
      return "|" + currentTime;
    }
  }

  protected String readWritePatientId(boolean read, String token){
    if(read){
      sample.setPatientId(token);
      return "";
    }else{
      return "|" + sample.getPatientId();
    }
  }
  
  protected String readWriteSampleId(boolean read, String token){
    if(read){
      sample.setId(token);
      return "";
    }else{
      return "|" + sample.getId();
    }
  }
  
  protected String readWriteLiquidType(boolean read, String token){
    if(read){
      sample.setLiquidType(token);
      return "";
    }else{
      return "|" + String.valueOf(sample.getLiquidType());
    }
  }
  
  protected String readWriteFirstName(boolean read, String token){
    if(read){
      sample.setFirstName(token);
      return "";
    }else{
      return "|" + sample.getFirstName();
    }
  }
  
  protected String readWriteLastName(boolean read, String token){
    if(read){
      sample.setLastName(token);
      return "";
    }else{
      return "|" + sample.getLastName();
    }
  }
  
  protected String readWriteMiddleInitial(boolean read, String token){
    if(read){
      sample.setMiddleInitial(token);
      return "";
    }else{
      return "|" + sample.getMiddleInitial();
    }
  }

  protected String readWriteAge(boolean read, String token){
    if(read){
    	int value = 0;
    	try{
    		value = Integer.valueOf(token.trim()).intValue();
    	}catch(Exception e){
    		Globals.logString("Exception in Age integer conversion:'"+token+"'");
    	}
      sample.setAge(value);
      return "";
    }else{
      //return "|" + sample.getAge();
    	return "";
    }
  }
  
	protected String readWriteField(boolean read, int pos, String token){
    String res = "";
    if(pos == POS_CUR_DATE){
      res = readWriteCurrentDate(read, token);
    }else if(pos == POS_CUR_ITME){
      res = readWriteCurrentTime(read, token);
    }else if(pos == POS_PATIENT_ID){
      res = readWritePatientId(read, token);
    }else if(pos == POS_SAMPLE_ID){
      res = readWriteSampleId(read, token);
    }else if(pos == POS_LIQ_TYPE){
      res = readWriteLiquidType(read, token);
    }else if(pos == POS_FIRST_NAME){
      res = readWriteFirstName(read, token);
    }else if(pos == POS_LAST_NAME){
      res = readWriteLastName(read, token);
    }else if(pos == POS_MID_NAME){
      res = readWriteMiddleInitial(read, token);
    }else if(pos == POS_AGE){
      res = readWriteAge(read, token);
    }else if(pos >= POS_FIRST_TEST_ID){
      addTestToSample(sample, token);
    }
    return res;
  }
  
	public boolean openReceiveFiles(DirSet dirSet, String fileName){//Returns TRUE if the file is file new
		boolean isFileNew   = false;
		
    fileToPost = new File(dirSet.getReceiveDir().getAbsolutePath()+"\\"+fileName);
    try{
    	isFileNew   = fileToPost.createNewFile();
    	fileToPostWriter    = new BufferedWriter(new FileWriter(fileToPost, true));
    }catch(Exception e){
    	Globals.logString("Could not open receive file:"+dirSet.getReceiveDir().getAbsolutePath()+"/"+fileName);
    	Globals.logException(e);
    }
    
    fileArchToPost = null;
    if(L3Application.getAppInstance().isKeepFilesForDebug()){
    	fileArchToPost = new File(dirSet.getArchiveReceiveDir().getAbsolutePath()+"\\"+fileName);
      try{
      	fileArchToPost.createNewFile();
      	fileArchToPostWriter = new BufferedWriter(new FileWriter(fileArchToPost, true));
      }catch(Exception e){
      	Globals.logString("Could not open receive file:"+dirSet.getArchiveReceiveDir().getAbsolutePath()+"/"+fileName);
      	Globals.logException(e);
      }
    }

    return isFileNew;
	}
	
	public void writeToReceiveFiles(String str) throws Exception{
		if(str != null){
			if(fileToPostWriter != null){
				fileToPostWriter.write(str);
				fileToPostWriter.flush();
			}
			if(fileArchToPostWriter != null){
				fileArchToPostWriter.write(str);
				fileArchToPostWriter.flush();
			}
		}
	}

	public void newLineInReceiveFiles() throws Exception{
		if(fileToPostWriter != null){
			fileToPostWriter.newLine();
		}
		if(fileArchToPostWriter != null){
			fileArchToPostWriter.newLine();
		}
	}

	public void closeReceiveFiles() throws Exception{
		if(fileToPostWriter != null){
			fileToPostWriter.close();
		}
		if(fileArchToPostWriter != null){
			fileArchToPostWriter.close();
		}
	}
  
  protected void treatSampleInfos() throws Exception{
    for(int i = 0; i < POS_FIRST_TEST_ID; i++){
      writeToReceiveFiles(readWriteField(false, i, ""));
    }
    writeToReceiveFiles("|");
  }
  
  protected void treatTests() throws Exception{
    Iterator tIter = sample.testIterator();
    while(tIter != null && tIter.hasNext()){
      L3Test test = (L3Test) tIter.next();
      if(test.isResultOk()){
        writeToReceiveFiles(test.getLabel()+"|"+test.getValue()+"|"+test.getValueNotes()+"|");
      }
    }
    writeToReceiveFiles("\n");
  }
  
	public void postSampleToLis(L3Sample sample, DirSet dirSet) throws Exception{
    /*Date d = new Date(123);
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss");*/
    this.sample = sample;
    //HHHopenReceiveFiles(dirSet, sample.getId()+"_"+instrumentCode+".txt");
        
    /*d.setTime(System.currentTimeMillis());
    String currentDate = simpleDateFormat.format(d);
    String currentTime = simpleTimeFormat.format(d);*/

    //writeToReceiveFiles("|"+currentDate+"|"+currentTime+"|"+sample.getPatientId()+"|"+sample.getId()+"|||"+sample.getFirstName()+"|"+sample.getLastName()+"|"+sample.getMiddleInitial()+"|||");
    treatSampleInfos();
    treatTests();
    closeReceiveFiles();

    /*Iterator tIter = sample.testIterator();
    while(tIter != null && tIter.hasNext()){
      L3Test test = (L3Test) tIter.next();
      if(test.isResultOk()){
      	writeToReceiveFiles(test.getLabel()+"|"+test.getValue()+"|");
      }
    }*/
	}
}