package org.nutz.log.test;

import org.junit.Test;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class LogTest {

	@Test
	public void testLog(){
		Log log = Logs.get();
		log.info("hhhhhhhhhhh");
	}
	
}
