package b01.l3.drivers.coulter.dataBlockStructure;

import b01.foc.Globals;
import b01.foc.util.ASCII;
import b01.l3.data.L3Sample;
import b01.l3.data.L3Test;
import b01.l3.data.L3TestDesc;
import b01.l3.drivers.coulter.CoulterFrame;

public class TestCoulterField extends CoulterField{
	private String lisCode = null;	
	
  public TestCoulterField(int groupIndex, String tag, String lisCode) {
    super(groupIndex, tag);
    this.lisCode = lisCode;
  }

  public void parse(L3Sample sample, String value) {
  	Globals.logDetail(" test parsing:"+getTag()+"|"+value);
    if (value != null && lisCode != null && lisCode.trim().compareTo("") != 0){
  		L3Test l3test = sample.addTest();
  		l3test.setLabel(lisCode);

  		String numericPart = value.substring(0, value.length()-3); 
  		numericPart = numericPart.trim();
    		
  		if(numericPart.compareTo(".....") == 0){    			
  			l3test.setValue(0);
   			l3test.setPropertyString(L3TestDesc.FLD_MESSAGE, "Incomplete computation");
   			l3test.setResultOk(false);
  		}if(numericPart.compareTo("?????") == 0){
  			l3test.setValue(0);
   			l3test.setPropertyString(L3TestDesc.FLD_MESSAGE, "Invalid analysed Data");
   			l3test.setResultOk(false);
  		}if(numericPart.compareTo(":::::") == 0){
  			l3test.setValue(0);
   			l3test.setPropertyString(L3TestDesc.FLD_MESSAGE, "Flow cell clogged");
   			l3test.setResultOk(false);
  		}if(numericPart.compareTo("+++++") == 0){
  			l3test.setValue(0);
   			l3test.setPropertyString(L3TestDesc.FLD_MESSAGE, "Exceeds maximum display limit");
   			l3test.setResultOk(false);
  		}if(numericPart.compareTo("-----") == 0){
  			l3test.setValue(0);
   			l3test.setPropertyString(L3TestDesc.FLD_MESSAGE, "Total voteout");
   			l3test.setResultOk(false);
  		}else{
     		double val = 0;
     		boolean couldNotRead = false;
     		try{
     			val = Double.valueOf(numericPart).doubleValue();
     		}catch(Exception e){
     			couldNotRead = true;
     		}
     		if(couldNotRead){
     			l3test.setPropertyString(L3TestDesc.FLD_MESSAGE, "Could not read value <"+value+">");
     			l3test.setResultOk(false);
     		}else{
     			l3test.setValue(val);
     			l3test.setResultOk(true);
     		}
  		}
    }
  }
  
  @Override
  public void addTag(StringBuffer str) {
    super.addTag(str);
    for(int i=getTag().length(); i<4; i++){
      str.append(CoulterFrame.FIELD_SEPERATOR);
    }
  }

  public void format(L3Sample sample, StringBuffer str) {
    for (int i =0; i< sample.getTestList().size(); i++){
      L3Test l3test = (L3Test) sample.getTestList().getFocObject(i);
      if (l3test.getLabel().compareTo(getTag()) == 0){
        addTag(str);
        str.append(l3test.getValue());
        str.append(ASCII.CR);
        str.append(ASCII.LF);
      }
    }
    
  }
}
