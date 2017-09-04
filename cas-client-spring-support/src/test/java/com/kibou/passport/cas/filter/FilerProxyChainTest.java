package com.kibou.passport.cas.filter;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author aimysaber@gmail.com
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:cas-test-*.xml")
public class FilerProxyChainTest {
	
	@Resource
	private ApplicationContext applicationContext;

	@Test
	public void testDummy(){
		
	}
}
