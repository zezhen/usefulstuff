package me.lcode.usecase.encode;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

public class Encode {

	@Test
	public void symmetric_compression() throws UnsupportedEncodingException {
		String str = "¡÷‘ÛËÂ";
		System.out.println(str);
		byte[] b1 = str.getBytes("gbk");
		System.out.println(b1.length);
		String str2 = new String(b1, "utf-8");
		System.out.println(str2);
		byte[] b2 = str2.getBytes("utf-8");
		System.out.println(b2.length);
		String str3 = new String(b2, "gbk");
		System.out.println(str3);
	}
	
}
