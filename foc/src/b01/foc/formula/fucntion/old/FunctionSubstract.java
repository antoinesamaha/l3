package b01.foc.formula.fucntion.old;

import b01.foc.formula.FunctionFactory;

public class FunctionSubstract extends Function {
	private static final String FUNCTION_NAME = "SUBSTR";
	private static final String OPERATOR_SYMBOL = "-";

	public Object compute() {
		double value = 0;
		IOperand operand0 = getOperandAt(0);
		//String value0Str = (String) operand0.compute();
		String value0Str = String.valueOf(operand0.compute());
		value = Double.valueOf(value0Str);
		
		int operandCount = getOperandCount();
		for(int i = 1; i < operandCount; i++){
			double operandValue = 0; 
			IOperand operand = getOperandAt(i);
			Object operandValueObj = operand.compute();
			if(operandValueObj != null){
				String operandValueStr = String.valueOf(operand.compute());
				operandValue = Double.valueOf(operandValueStr);
			}
			value -= operandValue;
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
		return FunctionFactory.PRIORITY_ADDITIVE;
	}

	@Override
	public boolean needsManualNotificationToCompute() {
		return false;
	}

}
