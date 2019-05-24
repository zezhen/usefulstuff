package me.lcode.usecase.unittest;

import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

public class MockupRunner {
	@Test
	@PrepareForTest(Clz.class)  
	public void testMethodMock() throws Exception {
		
		Clz c = PowerMockito.mock(Clz.class);
		PowerMockito.when(c.func()).thenCallRealMethod();
//		PowerMockito.when(c.call()).thenReturn(1);
		PowerMockito.when(c, "call").thenReturn(1);
		c.func(); // mock up call
		new Clz().func(); // normal call
	}
}
class Clz {
	public int func() {
		int rst = call();
		if(rst == 0) {
			System.out.println("normal call");
		} else {
			System.out.println("mock up call");
		}
		return 1;
	}
	private int call() {
		return 0;
	}
}