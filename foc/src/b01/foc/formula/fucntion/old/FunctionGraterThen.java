package b01.foc.formula.fucntion.old;

import b01.foc.Globals;
import b01.foc.formula.FunctionFactory;

public class FunctionGraterThen extends BooleanFunction {
	private static final String FUNCTION_NAME = "GT";
	private static final String OPERATOR_SYMBOL = ">";

	public Object compute() {
		boolean res = false;
		IOperand operand1 = getOperandAt(0);
		IOperand operand2 = getOperandAt(1);
		if(operand1 != null && operand2 != null){
			String operand1Str = "";
			String operand2Str = "";
			Object operand1Obj = operand1.compute();
			Object operand2Obj = operand2.compute();
			if(operand1Obj != null && operand2Obj != null){
				try{
					operand1Str = String.valueOf(operand1Obj);
					operand2Str = String.valueOf(operand2Obj);
					res = Double.valueOf(operand1Str) > Double.valueOf(operand2Str);
				}catch(Exception e){
					Globals.logString("Exception while computing value of function : "+ FunctionGraterThen.getFunctionName());
					Globals.logString(e.getMessage());
				}
			}
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
		return FunctionFactory.PRIORITY_RELATIONAL;
	}
}
