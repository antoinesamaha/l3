package b01.foc.formula.function;

import b01.foc.formula.FFormulaNode;
import b01.foc.formula.FunctionFactory;

public class FunctionOr extends BooleanFunction {

	private static final String FUNCTION_NAME = "OR";
	private static final String OPERATOR_SYMBOL = "||";

	/*public Object compute() {
		boolean res = false;
		IOperand operand1 = getOperandAt(0);
		IOperand operand2 = getOperandAt(1);
		if(operand1 != null && operand2 != null){
			String operandStr = String.valueOf(operand1.compute());
			boolean operand1Boolean = Boolean.valueOf(operandStr);
			
			operandStr = String.valueOf(operand2.compute());
			boolean operand2Boolean = Boolean.valueOf(operandStr);
			
			res = operand1Boolean || operand2Boolean;
		}
		return res;
	}*/
	
	public Object compute(FFormulaNode formulaNode){
		boolean result = false;
		if(formulaNode != null){
			FFormulaNode childNode = (FFormulaNode) formulaNode.getChildAt(0);
			if(childNode != null){
				boolean childNodeCalculatedValue = childNode.getCalculatedValue_boolean();
				result = childNodeCalculatedValue;
			}
			
			childNode = (FFormulaNode) formulaNode.getChildAt(1);
			if(childNode != null){
				boolean childNodeCalculatedValue = childNode.getCalculatedValue_boolean();
				result = result || childNodeCalculatedValue;
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
		return FunctionFactory.PRIORITY_LOGICAL_OR;
	}
}
