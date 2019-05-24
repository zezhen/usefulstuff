package me.lcode.usecase.util;

import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;


public class TestPowerMockito {
    @Test
    @PrepareForTest(Clz.class)  
    public void testMethodMockUsingPowerMockito() throws Exception {
        Clz c = PowerMockito.mock(Clz.class);
        PowerMockito.when(c.func()).thenCallRealMethod();  
        PowerMockito.when(c, "call").thenReturn(1);
        c.func(); // mock up call

        new Clz().func(); // normal call
    }
    
    public class Clz {
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
}

