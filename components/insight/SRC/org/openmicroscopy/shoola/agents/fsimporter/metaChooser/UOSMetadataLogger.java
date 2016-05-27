package org.openmicroscopy.shoola.agents.fsimporter.metaChooser;

import java.io.File;
import java.io.IOException;
import java.util.logging.ErrorManager;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class UOSMetadataLogger 
{
	 private final static Logger logger = Logger.getLogger(UOSMetadataLogger.class.getName());
	 private static FileHandler fh = null;
	 private final static String LOGDIR = System.getProperty("user.home")
	            + File.separator + "omero" + File.separator + "log";
	 
	 public static void init()
	 {
		 //check if log directory exists
		 if(!new File(LOGDIR).exists()){
			 System.out.println("[LOGGER] LOG FILE directory doesn't exists");
		 }else{
			 System.out.println("[LOGGER] LOG FILE"+LOGDIR+File.separator+"UOSImporter*.log");
			 try {
				 fh=new FileHandler(LOGDIR+File.separator+"UOSImporter.%g.log", 1024*1024,10,true);
			 } catch (SecurityException | IOException e) {
//				 ExceptionDialog ld = new ExceptionDialog("Log File Error!", 
//						 "Can not initialize log file UOSImporter.log",e);
//				 ld.setVisible(true);
				 System.out.println("[LOGGER] Can not initialize log file");
				 return;
			 }

			 Logger l = Logger.getLogger(UOSMetadataLogger.class.getName());
			 if(l!=null){
				 Handler[] handlers = l.getHandlers();
				 l.setUseParentHandlers(false);
				 for(Handler handler : handlers) {
					 l.removeHandler(handler);
				 }
			 }else{
				 System.out.println("[LOGGER] no logger available");
			 }



			 fh.setFormatter(new SimpleFormatter());
			 l.addHandler(fh);

			 Formatter formatter = new Formatter(){
				 @Override
				 public String format(LogRecord arg0) {
					 StringBuilder b = new StringBuilder();
					 //		                b.append(new Date());
					 b.append("[");
					 b.append(arg0.getLevel());
					 b.append("] ");
					 b.append(arg0.getMessage());
					 //		                b.append(System.getProperty("line.separator"));
					 //		                b.append("\t [");
					 //		                b.append(arg0.getSourceClassName());
					 //		                b.append("::");
					 //		                b.append(arg0.getSourceMethodName());
					 //		                b.append("] ");
					 b.append(System.getProperty("line.separator"));
					 return b.toString();

				 }
			 };

			 // shell-handler
			 Handler sh = new Handler(){
				 @Override
				 public void publish(LogRecord record)
				 {
					 if (getFormatter() == null)
					 {
						 setFormatter(new SimpleFormatter());
					 }

					 try {
						 String message = getFormatter().format(record);
						 if (record.getLevel().intValue() > Level.WARNING.intValue())
						 {
							 message="\n"+message+"\n\t["+record.getSourceClassName()+"::"+
									 record.getSourceMethodName()+"]\n";
							 System.err.write(message.getBytes());                       
						 }
						 else
						 {
							 System.out.write(message.getBytes());
						 }
					 } catch (Exception exception) {
						 reportError(null, exception, ErrorManager.FORMAT_FAILURE);
					 }

				 }

				 @Override
				 public void close() throws SecurityException {}
				 @Override
				 public void flush(){}
			 };

			 sh.setFormatter(formatter);
			 l.addHandler(sh);

			 l.setLevel(Level.CONFIG);
		 }
	 }
}
