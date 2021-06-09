package b01.foc.formula.fucntion.old;

import java.util.ArrayList;

import b01.foc.formula.Formula;

public abstract class Function implements IOperand{
	
	public abstract boolean needsManualNotificationToCompute();
	
	private ArrayList<IOperand>          operandList   = null;
	private Formula                      formula       = null;
	
	public void dispose(){
		if(this.operandList != null){
			this.operandList.clear();
			this.operandList = null;
		}
		
	}
	
	public void setFormula(Formula formula){
		this.formula = formula;
	}
	
	protected Formula getFormula(){
		return this.formula;
	}
	
	private ArrayList<IOperand> getOperandList(){
		if(this.operandList == null){
			this.operandList = new ArrayList<IOperand>();
		}
		return this.operandList;
	}
	
	protected int getOperandCount(){
		return getOperandList().size();
	}
	
	protected IOperand getOperandAt(int i){
		return getOperandList().get(i);
	}
	
	public void addOperand(IOperand operand){
		getOperandList().add(operand);
	}
}
