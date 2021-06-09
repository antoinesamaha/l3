package b01.l3.connector;

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Properties;

import b01.foc.file.DirSet;
import b01.foc.file.FileGrabber;
import b01.l3.Instrument;
import b01.l3.MessageListener;
import b01.l3.PoolKernel;

public abstract class FilePool extends PoolKernel implements FileGrabber, MessageListener {
	
	private DirSet dirSet = null;
	
	private void initPool(){
    dirSet.setFileGrabber(this);
    addMessageListener(this);
	}
	
	public FilePool(String configFileName) throws Exception{
		super(false);		
		Properties props = new Properties();
    FileInputStream in = new FileInputStream(configFileName);
    props.load(in);
    in.close();
    
    dirSet = new DirSet(props);
    initPool();
	}
	//cette methode est ajouter pour le test car il y'avait une erreur dans l'ecriture du path
	public FilePool(Properties configFileName) throws Exception{
		super(false);		
    
    dirSet = new DirSet(configFileName);
    initPool();
	}
	
	public DirSet getDirSet(){
		return dirSet;
	}
	
	public void startListeningToFiles(){
		dirSet.startPolling();
	}

	public void stopListeningToFiles(){
		dirSet.stopPolling();
	}
	
	public boolean areAllDisconnected(){
    boolean exit = true;
    
		Iterator iter = newIterator();
		while(iter != null && iter.hasNext() && exit){
			Instrument instr = (Instrument)iter.next();
			exit = !instr.isConnected();
		}
    return exit;
	}

}
