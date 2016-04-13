package org.nutz.log.test;

import org.junit.Test;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class LogTest {
	Log log = Logs.get();

	@Test
	public void testLog(){
		
		log.info("hhhhhhhhhhh");
	}
	
	
	@Test
	public void testLog2(){
		//for(int i=0;i<100000;i++){
			log.infof("%s,%s,%d","132456","456789",0);
	//	}
	}
	
	
}
