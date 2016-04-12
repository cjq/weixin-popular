package org.nutz.log;

import org.nutz.plugin.SimplePluginManager;

public final class Logs {
	
	private static LogAdapter adapter;
	
	 static {
	        init();
	    }
	 

	    /**
	     * 初始化NutLog,检查全部Log的可用性,选择可用的Log适配器
	     * <p/>
	     * <b>加载本类时,该方法已经在静态构造函数中调用,用户无需主动调用.</b>
	     * <p/>
	     * <b>除非迫不得已,请不要调用本方法<b/>
	     * <p/>
	     */
	    public static void init() {
	        try {
	            String packageName = Logs.class.getPackage().getName() + ".impl.";
	            adapter = new SimplePluginManager<LogAdapter>(
	                    packageName + "CustomLogAdapter",
	                    packageName + "Slf4jLogAdapter",
	                    packageName + "Log4jLogAdapter",
	                    packageName + "SystemLogAdapter").get();
	        }
	        catch (Throwable e) {
	            //这是不应该发生的,SystemLogAdapter应该永远返回true
	            //唯一的可能性是所请求的org.nutz.log.impl.SystemLogAdapter根本不存在
	            //例如改了package
	            e.printStackTrace();
	        }
	    }
	
	public static Log get() {
    	StackTraceElement[] sts = Thread.currentThread().getStackTrace();
    	return adapter.getLogger(sts[2].getClassName());
    }
}
