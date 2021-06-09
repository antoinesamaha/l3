package b01.foc.formula.fucntion.old;

import b01.foc.Globals;
import b01.foc.formula.FunctionFactory;

public class FunctionSum extends Function {
	private static final String FUNCTION_NAME = "SUM";
	private static final String OPERATOR_SYMBOL = "+";

	public Object compute(){
		double value = 0;
		int operandCount = getOperandCount();
		for(int i = 0; i < operandCount; i++){
			IOperand operand = getOperandAt(i);
			String operandValueStr = String.valueOf(operand.compute());
			try{
				double operandValue = Double.valueOf(operandValueStr);
				value += operandValue;
			}catch(NumberFormatException e){
				Globals.logString("Exception while computing value of function : "+ FunctionSum.getFunctionName());
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
		return FunctionFactory.PRIORITY_ADDITIVE;
	}

	@Override
	public boolean needsManualNotificationToCompute() {
		return false;
	}

}
