package b01.foc.formula.fucntion.old;

import b01.foc.formula.FunctionFactory;

public class FunctionDivide extends Function {
	private static final String FUNCTION_NAME = "DIV";
	private static final String OPERATOR_SYMBOL = "/";

	public Object compute() {
		double value = 0;
		IOperand operand0 = getOperandAt(0);
		String value0Str = String.valueOf(operand0.compute());
		value = Double.valueOf(value0Str);
		
		int operandCount = getOperandCount();
		for(int i = 1; i < operandCount; i++){
			IOperand operand = getOperandAt(i);
			String operandValueStr = String.valueOf(operand.compute());
			double operandValue = Double.valueOf(operandValueStr);
			if(operandValue != 0){
				value /= operandValue;
			}
		}
		return value;
	}
	
	public static String getFunctionName(){
		return FUNCTION_NAME;
	}
	
	public static String getOperatorSymbol(){
		return OPERATOR_SYMBOL;
	}
	
	public static int getOperatorPriority(){
		return FunctionFactory.PRIORITY_MULTIPLICATIVE;
	}

	@Override
	public boolean needsManualNotificationToCompute() {
		return false;
	}

}
