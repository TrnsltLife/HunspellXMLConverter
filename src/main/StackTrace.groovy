package main

class StackTrace
{
	//Given an Exception, this gives you the stack trace as a String
	public static String getStackTrace(Throwable t)
	{
		if(t==null){return "";}
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		t.printStackTrace(pw);
		pw.flush();
		sw.flush();
		return sw.toString();
	}
}