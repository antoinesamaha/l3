package b01.l3.dispatcher;

import java.util.ArrayList;

import b01.l3.PoolKernel;
import b01.l3.data.L3Sample;

public interface IDispatcher {
	public void init(ArrayList<PoolKernel> poolList);
	public void dispose();
	public void prepareForNewMessage();
	public void dispatchSample(L3Sample sample);
}
