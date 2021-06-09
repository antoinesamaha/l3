package b01.l3.drivers.coulter.dataBlockStructure;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import b01.foc.MultiLanguage;
import b01.foc.util.ASCII;
import b01.foc.util.Byte2IntConverter;
import b01.foc.util.Int2ByteConverter;
import b01.l3.data.L3Sample;
import b01.l3.data.L3Test;
import b01.l3.drivers.coulter.CoulterDriver;
import b01.l3.exceptions.L3Exception;

public class DataBlockStructureSTKS2A extends AbstractDataBlock{
	private String crlf = null;
	private NumberFormat format = null;
	private SimpleDateFormat dateFormat = null;
	
	private static final int GROUP_GENERAL_INFORMATION =  1;
	private static final int GROUP_CBC                 =  2;
	private static final int GROUP_DIFF_COUNT          =  3;
	private static final int GROUP_DIFF_PERCENTAGE     =  4;
	private static final int GROUP_DEFINIT_FLAGS     =  6;
	//private static final int GROUP_SUSPECTED_FLAGS     =  7;
	private static final int GROUP_DEMOGRAPHICS        = 10;
	
  public DataBlockStructureSTKS2A(CoulterDriver driver){
  	super(driver);
  	crlf = ""+ASCII.CR+ASCII.LF;
  	declareFields();

  	format = NumberFormat.getInstance(MultiLanguage.getCurrentLocale());
    format.setMaximumFractionDigits(1);
    format.setMinimumFractionDigits(1);

    dateFormat = new SimpleDateFormat("MM/dd/yy");
  }

  public void dispose(){
  	super.dispose();
  	crlf = null;
  	format = null;
  	dateFormat = null;
  }
  
  private void appendNumericalField(StringBuffer buffer, CoulterField fld, L3Test test){
		double val = test.getValue();
		String valStr = format.format(val);
		buffer.append(fld.getTag()+' ');
		for(int i=0; i<5-valStr.length(); i++){
			buffer.append(' ');
		}
		buffer.append(valStr+"    "+crlf);
  }
  
  private void appendGroup1(StringBuffer buffer, L3Sample sample){
  	buffer.append("G01"+crlf);
		buffer.append("07"+crlf);//rr 06
  	buffer.append("DATE "+dateFormat.format(sample.getEntryDate())+crlf);
  	buffer.append("TIME 09:12:54"+crlf);
  	buffer.append("ID1 "+sample.getId()+crlf);
  	String fullName = sample.getLastName()+" "+sample.getFirstName();
  	if(fullName.length() > 16) fullName = fullName.substring(0, 16);
  	buffer.append("ID2 "+fullName+crlf);
  	buffer.append("CASSPOS P001103"+crlf);
    buffer.append("SEQUENCE "+sample.getId()+crlf);//rr  	
  	buffer.append("C/PSTATUS P"+crlf);
  }
  
  private void appendTestGroup(StringBuffer buffer, int group, int nbFields, StringBuffer tests){
  	Int2ByteConverter conv = new Int2ByteConverter(group);
  	
		buffer.append("G"+conv.getHighByte()+conv.getLowByte()+crlf);
		conv = new Int2ByteConverter(nbFields);
		buffer.append(""+conv.getHighByte()+conv.getLowByte()+crlf);

		buffer.append(tests);
  }

  private void appendDemographicsGroup(StringBuffer buffer, L3Sample sample){
  	buffer.append("G0A"+crlf);
  	buffer.append("03"+crlf);
  	buffer.append("UF1 "+sample.getLastName()+crlf);
  	buffer.append("UF2 "+sample.getFirstName()+crlf);
  	buffer.append("UF3 "+sample.getMiddleInitial()+crlf);
  }

	public StringBuffer format(L3Sample sample){
		StringBuffer b = new StringBuffer();
		
		StringBuffer cbc = new StringBuffer();
		StringBuffer diffCount = new StringBuffer();
		StringBuffer diffPerc = new StringBuffer();
		
		int cbcNbFld = 0;
		int diffCountNbFld = 0;
		int diffPercNbFld = 0;
		
		//Preamble
		for(int i=0; i<6; i++) b.append(crlf);
		for(int i=0; i<14; i++) b.append(ASCII.DASH);
		b.append(crlf);
		//-------

		Iterator iter = sample.testIterator();
		while(iter != null && iter.hasNext()){
			L3Test test = (L3Test) iter.next();
			if(test != null){
				CoulterField fld = getField(getDriver().testMaps_getInstCode(test.getLabel()));
				if(fld != null){
					int gi = fld.getGroupIndex();
					if(gi == GROUP_CBC){
						cbcNbFld++;
						appendNumericalField(cbc, fld, test);
					}else if(gi == GROUP_DIFF_COUNT){
						diffCountNbFld++;
						appendNumericalField(diffCount, fld, test);
					}else if(gi == GROUP_DIFF_PERCENTAGE){
						diffPercNbFld++;
						appendNumericalField(diffPerc, fld, test);
					}
				}
			}
		}
		
		//Transmission Identifier
		if(cbcNbFld > 0 && (diffCountNbFld+diffPercNbFld) > 0){
			b.append("S02"+crlf+"CBC"+crlf+"DIFF"+crlf);
		}else if(cbcNbFld > 0) {
			b.append("S01"+crlf+"CBC"+crlf);
		}else if((diffCountNbFld+diffPercNbFld) > 0) {
			b.append("S01"+crlf+"DIFF"+crlf);
		}
		//-----------------------
		
		//CBC Test
		if(cbcNbFld > 0){
			b.append("TCBC"+crlf+"03"+crlf);
			appendGroup1(b, sample);
			appendTestGroup(b, GROUP_CBC, cbcNbFld, cbc);
			appendDemographicsGroup(b, sample);
		}
		//--------		

		//DIFF Test
		if(cbcNbFld > 0){
			b.append("TDIFF"+crlf+"04"+crlf);
			appendGroup1(b, sample);
			appendTestGroup(b, GROUP_DIFF_COUNT, diffCountNbFld, diffCount);
			appendTestGroup(b, GROUP_DIFF_PERCENTAGE, diffPercNbFld, diffPerc);
			appendDemographicsGroup(b, sample);
		}
		//--------		

		//Postamble
		for(int i=0; i<2; i++) b.append(crlf);
		for(int i=0; i<14; i++) b.append(ASCII.DASH);
		b.append(crlf);
		//-------
		
		return b;
	}
	
	public void parse(L3Sample sample, String value) throws Exception{
		
		//Start by skipping the preamble
		int sIdx = value.indexOf('S');
		int typeCount = (new Byte2IntConverter(value.charAt(sIdx+1), value.charAt(sIdx+2))).getIntValue();
		int pos = sIdx + 5; 
		for(int i=0; i<typeCount; i++){
			pos = value.indexOf(crlf, pos) + 2;
		}
		
		for(int i=0; i<typeCount; i++){
			if(value.charAt(pos) != 'T'){
				throw new L3Exception("Frame Format Error: Expecting 'T' character at "+pos+" in "+ASCII.convertNonCharactersToDescriptions(value));
			}

			pos = parseATest(sample, value, pos);
		}
	}
	
	private int parseATest(L3Sample sample, String value, int letterTPos) throws Exception{
		int pos = letterTPos;
		pos = value.indexOf(crlf, pos)+2;
		int groupCount = (new Byte2IntConverter(value.charAt(pos), value.charAt(pos+1))).getIntValue();
		pos += 4;
		
		for(int i=0; i<groupCount; i++){
			if(value.charAt(pos) != 'G'){
				throw new L3Exception("Frame Format Error: Expecting 'G' character at "+pos+" in "+ASCII.convertNonCharactersToDescriptions(value));
			}
			pos = parseAGroup(sample, value, pos);
		}
		
		return pos;
	}
	
	private int parseAGroup(L3Sample sample, String value, int letterGPos) throws Exception{
		int pos = letterGPos + 5;
		
		int fieldCount = (new Byte2IntConverter(value.charAt(pos), value.charAt(pos+1))).getIntValue();
		pos+=4;
		for(int i=0; i<fieldCount; i++){
			int sepIdx = value.indexOf(ASCII.SPACE, pos);
			int crlfIdx = value.indexOf(crlf, pos);
			String tag = new String(value.substring(pos, sepIdx));
			
			CoulterField fld = getField(tag.trim());
			if(fld != null){
				String content = new String(value.substring(sepIdx+1, crlfIdx));
				fld.parse(sample, content);
			}
			pos = crlfIdx + 2;
		}
		
		return pos;
	}
	
	private void declareFields(){
    // SAMPLE STATUS
    addField(new CoulterField(GROUP_GENERAL_INFORMATION, "ID1"){
      public void dispose() {}
      public void format(L3Sample sample, StringBuffer str) {}
      public void parse(L3Sample sample, String value) {
        if (value != null){
        	String id = value.replaceAll(String.valueOf(ASCII.DASH), "");
        	if(id != null && id.compareTo("") != 0){
        		sample.setId(id);
        	}
        }
      }
    });

    //rr Begin
    /*
    addField(new CoulterField(GROUP_GENERAL_INFORMATION, "SEQUENCE"){
      public void dispose() {}
      public void format(L3Sample sample, StringBuffer str) {}
      public void parse(L3Sample sample, String value) {
        if (value != null){
        	if(sample.getId().compareTo("") == 0){          
	          String id = value.replaceAll(String.valueOf(ASCII.SPACE), "");
	          id = id.replaceAll(String.valueOf(ASCII.DASH), "");
        		if(id != null && id.compareTo("") != 0){
          		sample.setId(id);
          	}
          }
        }
      }
    });
    */
    //rr End
    
    addTestField(GROUP_CBC, "WBC");
    addTestField(GROUP_CBC, "RBC");
    addTestField(GROUP_CBC, "HGB");
    addTestField(GROUP_CBC, "HCT");
    addTestField(GROUP_CBC, "MCV");
    addTestField(GROUP_CBC, "MCH");
    addTestField(GROUP_CBC, "MCHC");
    addTestField(GROUP_CBC, "RDW");
    addTestField(GROUP_CBC, "PLT");
    addTestField(GROUP_CBC, "PCT");
    addTestField(GROUP_CBC, "MPV");
    addTestField(GROUP_CBC, "PDW");
    
    addTestField(GROUP_DIFF_COUNT, "LY#");
    addTestField(GROUP_DIFF_COUNT, "MO#");
    addTestField(GROUP_DIFF_COUNT, "NE#");
    addTestField(GROUP_DIFF_COUNT, "EO#");
    addTestField(GROUP_DIFF_COUNT, "BA#");

    addTestField(GROUP_DIFF_PERCENTAGE, "LY%");
    addTestField(GROUP_DIFF_PERCENTAGE, "MO%");
    addTestField(GROUP_DIFF_PERCENTAGE, "NE%");
    addTestField(GROUP_DIFF_PERCENTAGE, "EO%");
    addTestField(GROUP_DIFF_PERCENTAGE, "BA%");

    addField(new CoulterField(GROUP_DEMOGRAPHICS, "UF1"){
      public void dispose() {}
      public void format(L3Sample sample, StringBuffer str) {}
      public void parse(L3Sample sample, String value) {
        if (value != null && value.compareTo("") != 0){
      		sample.setFirstName(value);
        }
      }
    });

    addField(new CoulterField(GROUP_DEMOGRAPHICS, "UF2"){
      public void dispose() {}
      public void format(L3Sample sample, StringBuffer str) {}
      public void parse(L3Sample sample, String value) {
        if (value != null && value.compareTo("") != 0){
      		sample.setLastName(value);
        }
      }
    });

    addField(new CoulterField(GROUP_DEMOGRAPHICS, "UF3"){
      public void dispose() {}
      public void format(L3Sample sample, StringBuffer str) {}
      public void parse(L3Sample sample, String value) {
      	if (value != null ){
	      	if(sample.getId().compareTo("") == 0){          
	          String id = value.replaceAll(String.valueOf(ASCII.SPACE), "");
	          id = id.replaceAll(String.valueOf(ASCII.DASH), "");
	      		if(id != null && id.compareTo("") != 0){
	        		sample.setId(id);
	        	}
	        }
      	}
      	/*
        if (value != null && value.length() == 1){
      		sample.setMiddleInitial(value);
        }
        */
      }
    });
    
    addField(new CoulterField(GROUP_DEFINIT_FLAGS, "DEFINIT"){
			public void format(L3Sample sample, StringBuffer str) {
			}
			public void parse(L3Sample sample, String value) {
			  	if (value != null){
			  		String comment = value.trim();
			  		if(comment.length() > 0){
			  			sample.pushMessageByAppend(getDriver().getInstrument(), comment);
			  		}
			  	}
			}
    });
    
    /*addField(new CoulterField(GROUP_SUSPECTED_FLAGS, "SUSPECT"){
			public void format(L3Sample sample, StringBuffer str) {
			}
			public void parse(L3Sample sample, String value) {
		      	if (value != null){
		      		String comment = value.trim();
		      		if(comment.length() > 0){
		      			sample.pushMessageByAppend(getDriver().getInstrument(), comment);
		      		}
		      	}
			}
    });*/
	}
}
