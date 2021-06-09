package b01.foc.formula;

import java.util.HashMap;
import java.util.Iterator;

import b01.foc.formula.function.Function;
import b01.foc.formula.function.FunctionAnd;
import b01.foc.formula.function.FunctionChoose;
import b01.foc.formula.function.FunctionDivide;
import b01.foc.formula.function.FunctionEquals;
import b01.foc.formula.function.FunctionGraterThen;
import b01.foc.formula.function.FunctionGraterThenOrEqual;
import b01.foc.formula.function.FunctionMultiply;
import b01.foc.formula.function.FunctionNot;
import b01.foc.formula.function.FunctionOr;
import b01.foc.formula.function.FunctionPath;
import b01.foc.formula.function.FunctionStaticPath;
import b01.foc.formula.function.FunctionSubstract;
import b01.foc.formula.function.FunctionSum;

public class FunctionFactory {
	
	//http://www.cplusplus.com/doc/tutorial/operators.html
	public static final int PRIORITY_NOT_APPLICABLE      = 0;
	public static final int PRIORITY_UNARY_SIGN_OPERATOR = 10;// unary-, unary+, !
	public static final int PRIORITY_MULTIPLICATIVE      = 20;// *, /, %
	public static final int PRIORITY_ADDITIVE            = 30;// +, -
	public static final int PRIORITY_RELATIONAL          = 40;// < > <= >=			
	public static final int PRIORITY_EQUALITY            = 50;// ==, != 
	public static final int PRIORITY_LOGICAL_AND         = 60;// &&
	public static final int PRIORITY_LOGICAL_OR          = 70;// ||
	public static final int PRIORITY_CONDITIONAL         = 80;// ?: the Choose function
	
	
	
	public static final String OPEN_PARENTHESIS   = "("; 
	public static final String CLOSE_PARENTHESIS  = ")";
	public static final String ARGUMENT_SEPARATOR = ",";
	public static final String SPACE              =" ";
	
	private static FunctionFactory functionFactory = null;
	
	private HashMap<String, Function> functionMap  = null;//Function name -> Function class
	private HashMap<String, String> operatorMap = null;//Operator symbol-> Function
	private HashMap<String, Integer> prioritiesMap = null;//Function name -> priority
	
	private FunctionFactory(){
		
	}
	
	public void dispose(){
		if(functionMap != null){
			functionMap.clear();
			functionMap  = null;
		}
		if(operatorMap != null){
			operatorMap.clear();
			operatorMap = null;
		}
		if(prioritiesMap != null){
			prioritiesMap.clear();
			prioritiesMap = null;
		}
	}
	
	private HashMap<String, Function> getFunctionMap(){
		if(this.functionMap == null){
			this.functionMap = new HashMap<String, Function>();
		}
		return this.functionMap;
	}
	
	private HashMap<String, String> getOperatorMap(){
		if(this.operatorMap == null){
			this.operatorMap = new HashMap<String, String>();
		}
		return this.operatorMap;
	}
	
	private HashMap<String, Integer> getPrioritiesMap(){
		if(this.prioritiesMap == null){
			this.prioritiesMap = new HashMap<String, Integer>();
		}
		return this.prioritiesMap;
	}
	
	/*private void declareFunction(String functionName, Class cls, String operatorSymbol, int operatorPriority){
		getFunctionMap().put(functionName, cls);
		if(operatorSymbol != null){
			getOperatorMap().put(operatorSymbol, functionName);
		}
		getPrioritiesMap().put(functionName, operatorPriority);
	}*/
	
	private void declareFunction(Function function){
		if(function != null){
			String functionName = function.getName();
			if(functionName != null){
				getFunctionMap().put(functionName, function);
				String operatorSymbol = function.getOperatorSymbol();
				if(operatorSymbol != null){
					getOperatorMap().put(operatorSymbol, functionName);
				}
				getPrioritiesMap().put(functionName, function.getOperatorPriority());
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public Function getFunction(String functionName){
		Function function = getFunctionMap().get(functionName);
		return function;
	}
	
	public boolean isFunctionName(String name){
		return getFunctionMap().containsKey(name);
	}
	
	public boolean isOperator(String symbol){
		return getOperatorMap().containsKey(symbol);
	}
	
	public String getFunctionNameForOperatorSymbol(String operatorSymbol){
		return getOperatorMap().get(operatorSymbol);
	}
	
	public int getOperatorPriority(String operator){
		int priority = PRIORITY_NOT_APPLICABLE;
		String functionName = getFunctionNameForOperatorSymbol(operator);
		if(functionName != null){
			priority = getPrioritiesMap().get(functionName);
		}
		return priority;
	}
	
	public String getSpecialCharactersForFormulaLevel0Format(){
		return FunctionFactory.OPEN_PARENTHESIS + FunctionFactory.CLOSE_PARENTHESIS + FunctionFactory.ARGUMENT_SEPARATOR + FunctionFactory.SPACE;
	}
	
	public String getSpecialCharactersForFormulaLevel1Format(){
		String specialCharcters = getSpecialCharactersForFormulaLevel0Format();
		Iterator<String> iter = getOperatorMap().keySet().iterator();
		while(iter != null && iter.hasNext()){
			String operator = iter.next();
			if(operator != null){
				specialCharcters += operator;
			}
		}
		return specialCharcters;
	}
	
	public static FunctionFactory getInstance(){
		if(functionFactory == null){
			functionFactory = new FunctionFactory();
			fillFunctionFactory(functionFactory);
		}
		return functionFactory;
	}
	
	private static void fillFunctionFactory(FunctionFactory functionFactory){
		if(functionFactory != null){
			functionFactory.declareFunction(new FunctionSum());
			functionFactory.declareFunction(new FunctionSubstract());
			functionFactory.declareFunction(new FunctionMultiply());
			functionFactory.declareFunction(new FunctionDivide());
			functionFactory.declareFunction(new FunctionPath());
			//functionFactory.declareFunction(FunctionParamPath.getFunctionName(), FunctionParamPath.class, FunctionParamPath.getOperatorSymbol(), FunctionParamPath.getOperatorPriority());
			functionFactory.declareFunction(new FunctionStaticPath());
			functionFactory.declareFunction(new FunctionAnd());
			functionFactory.declareFunction(new FunctionOr());
			functionFactory.declareFunction(new FunctionNot());
			functionFactory.declareFunction(new FunctionEquals());
			functionFactory.declareFunction(new FunctionGraterThen());
			functionFactory.declareFunction(new FunctionGraterThenOrEqual());
			functionFactory.declareFunction(new FunctionChoose());
		}
	}

}
