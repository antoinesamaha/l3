package b01.foc.formula.function;

import b01.foc.formula.FFormulaNode;
import b01.foc.formula.FunctionFactory;

public class FunctionEquals extends BooleanFunction {
	private static final String FUNCTION_NAME = "EQUALS";
	private static final String OPERATOR_SYMBOL = "==";

	/*public Object compute() {
		boolean res = false;
		IOperand operand1 = getOperandAt(0);
		IOperand operand2 = getOperandAt(1);
		if(operand1 != null && operand2 != null){
			Object operand1Value =  operand1.compute();
			Object operand2Value =  operand2.compute();
			res = operand1Value.equals(operand2Value);
		}
		return res;
	}*/
	
	public Object compute(FFormulaNode formulaNode){
		boolean result = false;
		if(formulaNode != null){
			FFormulaNode childNode1 = (FFormulaNode) formulaNode.getChildAt(0);
			FFormulaNode childNode2 = (FFormulaNode) formulaNode.getChildAt(1);
			if(childNode1 != null && childNode2 != null){
				String string1 = childNode1.getExpression();
				String string2 = childNode2.getExpression();
				if(string1 == null){
					result = string2 == null;
				}else{
					result = string2 == null ? false : string1.equals(string2);
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
		return FunctionFactory.PRIORITY_EQUALITY;
	}
}
