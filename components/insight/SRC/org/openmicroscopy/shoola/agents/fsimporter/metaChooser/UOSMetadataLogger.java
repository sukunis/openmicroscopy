package org.openmicroscopy.shoola.agents.fsimporter.metaChooser;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.ErrorManager;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;


public class UOSMetadataLogger 
{
	 private final static Logger logger = Logger.getLogger(UOSMetadataLogger.class.getName());
	 private static FileHandler fh = null;
	 
	 public static void init(){
		
		 try {
			 fh=new FileHandler("UOSImporter.log", true);
		 } catch (SecurityException | IOException e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
		 }
		 Logger l = Logger.getLogger("");
		 
		 Handler[] handlers = l.getHandlers();
		 l.setUseParentHandlers(false);
		 for(Handler handler : handlers) {
		     l.removeHandler(handler);
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
