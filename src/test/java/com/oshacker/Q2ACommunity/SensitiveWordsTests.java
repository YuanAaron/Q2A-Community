package com.oshacker.Q2ACommunity;

import com.oshacker.Q2ACommunity.utils.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class SensitiveWordsTests {

	@Autowired
	private SensitiveFilter sensitiveWordUtil;

	@Test
	public void testSensitiveFilter() {
		String text="fabc";
		System.out.println(sensitiveWordUtil.filter(text));
	}

}
