package b01.foc.formula.function;

import b01.foc.formula.FFormulaNode;
import b01.foc.formula.FunctionFactory;

public class FunctionNot extends BooleanFunction {
	private static final String FUNCTION_NAME = "NOT";
	private static final String OPERATOR_SYMBOL = "!";
	
	/*public Object compute() {
		boolean res = false;
		IOperand operand1 = getOperandAt(0);
		if(operand1 != null){
			String operandStr = String.valueOf(operand1.compute());
			boolean operand1Boolean = Boolean.valueOf(operandStr);
			
			res = !operand1Boolean;
		}
		return res;
	}*/
	
	public Object compute(FFormulaNode formulaNode){
		boolean result = false;
		if(formulaNode != null){
			FFormulaNode childNode = (FFormulaNode) formulaNode.getChildAt(0);
			if(childNode != null){
				boolean childNodeCalculatedValue = childNode.getCalculatedValue_boolean();
				result = !childNodeCalculatedValue;
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
		return FunctionFactory.PRIORITY_UNARY_SIGN_OPERATOR;
	}
}
