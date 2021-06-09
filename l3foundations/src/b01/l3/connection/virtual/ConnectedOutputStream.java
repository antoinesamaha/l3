package b01.l3.connection.virtual;

import java.io.IOException;
import java.io.OutputStream;

import b01.foc.Globals;

public class ConnectedOutputStream extends OutputStream {

  private ConnectedInputStream inputStream = null;
  private DifferedSender differedSender = null;
  
  public ConnectedOutputStream(){
    differedSender = null;
    inputStream = null;
    getDifferedSender().start();    
  }
  
  public void dispose(){
      inputStream = null;
  }
  
  public void setInputStream(ConnectedInputStream inputStream) {
    this.inputStream = inputStream;
  }
  
  public DifferedSender getDifferedSender(){
    if(differedSender == null){
      differedSender = new DifferedSender(this);
    }
    return differedSender;
  }
  
  @Override
  public void write(int b) throws IOException {
    if(inputStream != null){
      inputStream.write(b);
    }
  }

  public void write(byte b[]) throws IOException {
    getDifferedSender().postToSendLater(b);
  }

  public void superWrite(byte b[]) throws IOException {
    super.write(b);
  }
  
  private class DifferedSender extends Thread{
    private ConnectedOutputStream outStream = null;
    private byte b[] = null;
    
    public DifferedSender(ConnectedOutputStream outStream){
      this.outStream = outStream;
    }

    public void postToSendLater(byte[] b){
      //Globals.logString(" write IN : "+DifferedSender.this.toString()+" "+outStream.toString()); //rr
      byte newB[] = null;
      int previousLength = 0;
      if(this.b != null){
        previousLength = this.b.length;
        newB = new byte[this.b.length + b.length];
        for(int i=0; i<this.b.length; i++){
          newB[i] = this.b[i]; 
        }
      }else{
        newB = new byte[b.length];
      }
      for(int i=previousLength; i<previousLength+b.length; i++){
        newB[i] = b[i-previousLength];
      }
      this.b = newB;
      //Globals.logString(" write OUT : "+DifferedSender.this.toString()+" "+outStream.toString()); //rr      
    }
    
    public void execute(){
      //Globals.logString(" execute IN : "+DifferedSender.this.toString()+" "+outStream.toString()); //rr      
      try{
        Thread.sleep(10);
        if(b != null){
          outStream.superWrite(b);
          b = null;
        }
      }catch(Exception  e){
        Globals.logString("Exception in the virtual output serial port!!");
        Globals.logException(e);
      }
      //Globals.logString(" execute OUT : "+DifferedSender.this.toString()+" "+outStream.toString()); //rr      
    }
    
    public void run() {
      while(true){
        execute();
      }
    }
  }
}
