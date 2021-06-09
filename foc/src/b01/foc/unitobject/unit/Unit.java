package b01.foc.unitobject.unit;

import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocObject;

public class Unit extends FocObject {
	public Unit(FocConstructor constr){
    super(constr);
 	  newFocProperties();
  }
	
	public void setName(String name){
		setPropertyString(UnitDesc.FLD_NAME, name);
	}
	
	public String getName(){
		return getPropertyString(UnitDesc.FLD_NAME);
	}
	
	public void setSymbol(String symbol){
		setPropertyString(UnitDesc.FLD_SYMBOL, symbol);
	}
	
	public String getSymbole(){
		return getPropertyString(UnitDesc.FLD_SYMBOL);
	}
  
  public double getFactor(){
    return getPropertyDouble(UnitDesc.FLD_FACTOR);
  }
  
  public void setFactor(double factor){
    setPropertyDouble(UnitDesc.FLD_FACTOR, factor);
  }
}
