package oO0oO0oO0o0o00.shadowchat;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

public class Logger{
	
	static public String LOGFILE_DEFPATH="/data/data/oO0oO0oO0o0o00.shadowchat/log.txt";
	
	static private boolean usable=false;
	static private File _logfile=null;
	static private  FileWriter fw;
	static private PrintWriter pw;
	
	static public void setupIfNeed(File logfile){
		if(_logfile==null || !usable){
			setup(logfile);
		}
		else{
			usable=true;
		}
	}
	
	static public void setup(File logfile){
		usable=false;
		try{
			pw.close();
		}catch(Exception e){}
		_logfile=logfile;
		try{
			fw=new FileWriter(logfile,true);
			pw=new PrintWriter(fw);
			usable=true;
		}catch(Exception e){}
	}
	
	static public void log(String s){
		if(usable){
			pw.append(">>>");
			pw.append(s);
			pw.append("\n");
			pw.flush();
		}
	}
	
	static public void log(Throwable e){
		if(usable){
			log("ERROR "+e.toString()+": ");
			e.printStackTrace(pw);
			pw.flush();
			log("END ERROR");
		}
	}
	
	static void close(){
		usable=false;
		try{
			pw.close();
		}catch(Exception e){}
	}
	
	static boolean isUsable(){
		return usable;
	}
}
