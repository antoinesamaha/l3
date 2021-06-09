package b01.foc.formula.fucntion.old;

import b01.foc.formula.FunctionFactory;

public class FunctionEquals extends BooleanFunction {
	private static final String FUNCTION_NAME = "EQUALS";
	private static final String OPERATOR_SYMBOL = "==";

	public Object compute() {
		boolean res = false;
		IOperand operand1 = getOperandAt(0);
		IOperand operand2 = getOperandAt(1);
		if(operand1 != null && operand2 != null){
			Object operand1Value =  operand1.compute();
			Object operand2Value =  operand2.compute();
			res = operand1Value.equals(operand2Value);
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
		return FunctionFactory.PRIORITY_EQUALITY;
	}
}
