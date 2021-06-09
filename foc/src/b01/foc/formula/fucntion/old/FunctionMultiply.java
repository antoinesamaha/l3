package b01.foc.formula.fucntion.old;

import b01.foc.Globals;
import b01.foc.formula.FunctionFactory;

public class FunctionMultiply extends Function {
	private static final String FUNCTION_NAME = "MULT";
	private static final String OPERATOR_SYMBOL = "*";

	public Object compute() {
		double value = 1;
		int operandCount = getOperandCount();
		for(int i = 0; i < operandCount; i++){
			IOperand operand = getOperandAt(i);
			double operandValue = 0;
			Object operandValueObj = operand.compute();
			if(operandValueObj != null){
				String operandValueStr = String.valueOf(operandValueObj);
				if(operandValueStr != null && operandValueStr.length() != 0){
					try{
						operandValue = Double.valueOf(operandValueStr);
					}catch(Exception e){
						Globals.logString("!! Exception !! parsing Operand string "+ operandValueStr + " : " + e.getMessage());
					}
				}
			}
			value *= operandValue;
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
