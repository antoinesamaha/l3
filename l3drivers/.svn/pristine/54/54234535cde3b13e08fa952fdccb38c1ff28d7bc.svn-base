package b01.l3.drivers.coulter.dataBlockStructure;

import java.util.LinkedHashMap;
import java.util.Map;

import b01.l3.drivers.coulter.CoulterDriver;

public abstract class AbstractDataBlock implements IDataBlock{

  private Map<String, CoulterField> groupFieldMap = null; 
  protected CoulterDriver driver = null;

  public AbstractDataBlock(CoulterDriver driver){
    groupFieldMap = new LinkedHashMap<String, CoulterField>();
    this.driver = driver;
  }

  public void dispose(){
    if(groupFieldMap != null){
      for(int i=0; i<groupFieldMap.size(); i++){
        CoulterField gf = groupFieldMap.get(i);
        gf.dispose();
      }
      groupFieldMap.clear();
    }
    groupFieldMap = null;
  }
  
  public CoulterDriver getDriver(){
  	return driver;
  }
  
  public void addField(CoulterField field){
    groupFieldMap.put(field.getTag(), field);
  }
  
  public CoulterField getField(String tag){
    return groupFieldMap.get(tag);
  }
  
  public void addTestField(int group, String tag){
  	String lisCode = driver.testMaps_getLisCode(tag);
  	if(lisCode != null){
	    CoulterField cbcWBCField = new TestCoulterField(group, tag, lisCode);
	    addField(cbcWBCField);
  	}  	
  }
}
