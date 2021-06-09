package b01.foc.formula.fucntion.old;

public class SimpleOperand extends Function {
	private Object value = 0;
	
	public SimpleOperand(Object value){
		setValue(value);
	}
	
	public void dispose(){
		super.dispose();
	}
	
	public Object compute(){
		return this.value;
	}
	
	private void setValue(Object value){
		this.value = value;
	}

	@Override
	public boolean needsManualNotificationToCompute() {
		return false;
	}

}
