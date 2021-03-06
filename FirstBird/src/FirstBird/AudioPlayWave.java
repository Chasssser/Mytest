package FirstBird;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioPlayWave extends Thread{
	private	String FileName;		//文件名
	private Position curPosition;		//声道
	private final int EXTERNAL_BUFFER_SIZE=524288;	//128k
	boolean flag=false;	//判断开始
	enum Position{		//声道
		LEFT,RIGHT,NORMAL
	};
	
	//构造函数
	public AudioPlayWave(String wavFile) {
		this.FileName=wavFile;
		curPosition=Position.NORMAL;
	}
	
	public void run() {
		AudioInputStream audioInputStream =null;  //创建音频输入流对象
		try {
			audioInputStream=AudioSystem.getAudioInputStream(getClass().getResource(FileName));		//创建音频对象
		}catch(UnsupportedAudioFileException el) {
			el.printStackTrace();
			return ;
		}catch(IOException el) {
			el.printStackTrace();
			return ;
		}
		AudioFormat format=audioInputStream.getFormat();		//音频格式
		SourceDataLine auline=null; //源数据线
		DataLine.Info info=new DataLine.Info(SourceDataLine.class, format);
		
		try {
			auline=(SourceDataLine)AudioSystem.getLine(info);
			auline.open(format);
		}catch(LineUnavailableException e){
			e.printStackTrace();
			return ;
		}catch(Exception e) {
			e.printStackTrace();
			return ;
		}
		
		if(auline.isControlSupported(FloatControl.Type.PAN)) {
			FloatControl pan=(FloatControl) auline.getControl(FloatControl.Type.PAN);
			if(curPosition==Position.RIGHT)
				pan.setValue(1.0f);
			else if(curPosition==Position.LEFT)
				pan.setValue(-1.0f);
		}
		
		auline.start();
		int nBytesRead=0;
		byte[] abData=new byte[EXTERNAL_BUFFER_SIZE];
		
		try {
			while(nBytesRead!=-1) {
				nBytesRead=audioInputStream.read(abData, 0, abData.length);
				if(nBytesRead>=0) {
					auline.write(abData, 0, nBytesRead);
				}
			}
		}catch(IOException e){
				e.printStackTrace();
				return;
		}finally{
			auline.drain();
			auline.close();
		}
		
	}
}
