package b01.foc.formula.fucntion.old;

import b01.foc.formula.FunctionFactory;

public class FunctionOr extends BooleanFunction {

	private static final String FUNCTION_NAME = "OR";
	private static final String OPERATOR_SYMBOL = "||";

	public Object compute() {
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
	}
	
	public boolean needsManualNotificationToCompute() {
		return false;
	}
	
	public static String getFunctionName(){
		return FUNCTION_NAME;
	}
	
	public static String getOperatorSymbol(){
		return OPERATOR_SYMBOL;
	}
	
	public static int getOperatorPriority(){
		return FunctionFactory.PRIORITY_LOGICAL_OR;
	}
}
