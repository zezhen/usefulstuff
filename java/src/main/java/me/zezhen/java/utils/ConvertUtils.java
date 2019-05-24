package me.zezhen.java.utils;

public class ConvertUtils {

    public static <T extends Enum<T>> T convertEnum(String str, Class<T> clz, T defaultValue) {
        if (str != null) {
            try {
                return Enum.valueOf(clz, str.toUpperCase());
            } catch(Exception e) {
            }
        }
        return defaultValue;
    }
    
    public static <T> T convertNumber(String str, Class clz, T defaultValue) {
        if (str != null) {
            T t = (T) newInstance(clz, str);
            if(t != null) {
                return t;
            }
        }
        return defaultValue;
    }
    
    private static Object newInstance(Class clz, String oneStrArg) {
        try {
            return clz.getDeclaredConstructor(String.class).newInstance(oneStrArg);
        } catch (Exception e) {
            return null;
        }
    }
    
}
