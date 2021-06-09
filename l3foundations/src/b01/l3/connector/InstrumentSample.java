package b01.l3.connector;

import b01.l3.Instrument;
import b01.l3.data.L3Sample;

public class InstrumentSample {
	public Instrument instrument = null;
	public L3Sample sample = null;
	
	public InstrumentSample(Instrument instrument, L3Sample sample){
		this.instrument = instrument; 
	  this.sample = sample;
	}

	public void dispose(){
		if(sample != null){
			sample.dispose();
			sample = null;
		}
	}
	
	public Instrument getInstrument() {
		return instrument;
	}

	public void setInstrument(Instrument instr) {
		instrument = instr;
	}

	public L3Sample getSample() {
		return sample;
	}

}
