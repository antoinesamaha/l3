package b01.foc.formula.function;

import b01.foc.formula.FFormulaNode;
import b01.foc.formula.FunctionFactory;

public class FunctionChoose extends Function {
	private static final String FUNCTION_NAME = "CHOOSE";
	private static final String OPERATOR_SYMBOL = "?:";
	
	/*public Object compute() {
		Object res = null;
		IOperand operand1 = getOperandAt(0);
		String operand1Str = String.valueOf(operand1.compute());
		boolean valueOfOperand1 = Boolean.valueOf(operand1Str);
		IOperand operandToApply = null;
		if(valueOfOperand1){
			operandToApply = getOperandAt(1);
		}else{
			operandToApply = getOperandAt(2);
		}
		if(operandToApply != null){
			res = operandToApply.compute();
		}
		return res;
	}*/
	
	public Object compute(FFormulaNode formulaNode){
		Object result = false;
		if(formulaNode != null){
			FFormulaNode childNode1 = (FFormulaNode) formulaNode.getChildAt(0);
			
			if(childNode1 != null){
				boolean childNode1Boolean = childNode1.getCalculatedValue_boolean();
				if(childNode1Boolean){
					FFormulaNode childNode2 = (FFormulaNode) formulaNode.getChildAt(1);
					if(childNode2 != null){
						result = childNode2.getCalculatedValue();
					}
				}else{
					FFormulaNode childNode3 = (FFormulaNode) formulaNode.getChildAt(2);
					if(childNode3 != null){
						result = childNode3.getCalculatedValue();
					}
				}
			}
		}
		return result;
	}
	
	public boolean needsManualNotificationToCompute() {
		return false;
	}
	
	public String getName(){
		return FUNCTION_NAME;
	}
	
	public String getOperatorSymbol(){
		return OPERATOR_SYMBOL;
	}
	
	public int getOperatorPriority(){
		return FunctionFactory.PRIORITY_CONDITIONAL;
	}
}
