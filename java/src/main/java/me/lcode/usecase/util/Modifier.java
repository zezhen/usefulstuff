package me.lcode.usecase.util;

import java.lang.reflect.Field;

public class Modifier {
    private final static String a = new String("Test");

    // private final static String a = "Test"; //this can not be modified
    public static void main(String[] args) {
        System.out.println("a = " + a);
        B.Test();
        System.out.println("a = " + a);
    }
}

class B {
    public static void Test() {
        try {
            Field field = Modifier.class.getDeclaredField("a");
            field.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~java.lang.reflect.Modifier.FINAL);
            field.set(Modifier.class, "Test Change");
            System.out.println(field.get(Modifier.class).toString());
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
