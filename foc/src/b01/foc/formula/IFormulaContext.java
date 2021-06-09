package b01.foc.formula;

public interface IFormulaContext {
	public abstract void compute();
	public abstract Object evaluateFormula();
	public abstract Object evaluateExpression(String expression);
	public abstract void commitValueToOutput(Object value);

	public abstract void plugListeners();
	public abstract void unplugListeners();
	public abstract void dispose();

}
