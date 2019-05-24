package me.lcode.usecase.unittest;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.mockito.Mockito.*;
//��Ҫע����ǣ�powermock������JUnit��TestNG��Mockito��EasyMock��
//������ʹ�õ���JUnit+Mockito��������Ҫimport�������Щ�ࡣ

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SayHi.class })
// ������annotation����Ҫ������powermock������Ч�ġ�
public class SayHiTest {
	@Test
	public void testSayHi() throws Exception {
		Adder adder = mock(Adder.class); // mock��һ��ģ��Ķ������ڴ�����ʵ��adder��
		when(adder.add(anyString(), anyString())).thenThrow(new Exception()); // Stub����������Ϊ����������ģ������add����ʱ���׳��쳣��������ʹ�õĶ���Mockito�Ĺ��ܡ�
		PowerMockito.whenNew(Adder.class).withNoArguments().thenReturn(adder);// ����powerMock��ʼ�������ã���Add.class��ʵ������ʱ��ǿ��ʹ��ģ�����adder��������б�ʵ���������Ķ���

		SayHi sh = new SayHi();
		assertTrue(sh.sayHi("abc", "def").equalsIgnoreCase("failed"));// �������ǿ�����ϣ����Ч�����쳣�����е����result
																		// =
																		// "Failed";��ִ����
	}
}