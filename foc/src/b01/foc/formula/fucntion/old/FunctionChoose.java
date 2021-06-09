package b01.foc.formula.fucntion.old;

import b01.foc.formula.FunctionFactory;

public class FunctionChoose extends Function {
	private static final String FUNCTION_NAME = "CHOOSE";
	private static final String OPERATOR_SYMBOL = "?:";
	
	public Object compute() {
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
		return FunctionFactory.PRIORITY_CONDITIONAL;
	}
}
