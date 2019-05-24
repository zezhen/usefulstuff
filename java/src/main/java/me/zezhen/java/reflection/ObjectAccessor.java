package me.zezhen.java.reflection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.jar.JarFile;

public class ObjectAccessor {

    public static final String TEST_RESOURCE_DIR = "src/test/resources/";
    private static JarFile jarFile;

    public static void setPrivateField(Object obj, String name, Object value) throws Exception {
        Field field = getAccessibleField(obj, name);
        field.set(obj, value);
    }

    public static void setPrivateStaticField(Class<?> clazz, String name, Object value) throws Exception {
        Field field = getAccessibleField(clazz, name);
        field.set(null, value);
    }

    public static Object getPrivateField(Object obj, String name) throws Exception {
        Field field = getAccessibleField(obj, name);
        return field.get(obj);
    }

    public static Object getPrivateStaticField(Class<?> clazz, String name) throws Exception {
        Field field = getAccessibleField(clazz, name);
        return field.get(null);
    }

    public static Method getAccessibleMethod(Object obj, String name, Class<?>... parameterTypes) throws Exception {
        return getAccessibleMethod(obj.getClass(), name, parameterTypes);
    }

    public static Method getAccessibleMethod(Class<?> clazz, String name, Class<?>... parameterTypes) throws Exception {
        Method method = clazz.getDeclaredMethod(name, parameterTypes);
        method.setAccessible(true);
        return method;
    }

    public static Field getAccessibleField(Object obj, String name) throws Exception {
        return getAccessibleField(obj.getClass(), name);
    }

    public static Field getAccessibleField(Class<?> clazz, String name) throws Exception {
        Field field;
        try {
            field = clazz.getDeclaredField(name);
        } catch(Exception e) {
            Class<?> superClass = clazz.getSuperclass();
            field = superClass.getDeclaredField(name);
        }
        field.setAccessible(true);
        return field;
    }

    public static Class<?> getAccessibleClass(Object obj, String name) throws Exception {
        return getAccessibleClass(obj.getClass(), name);
    }

    public static Class<?> getAccessibleClass(Class<?> clazz, String name) throws Exception {

        Class<?>[] declaredClasses = clazz.getDeclaredClasses();
        for (Class<?> clz : declaredClasses) {
            if (clz.getName().indexOf(name) >= 0) {
                return clz;
            }
        }
        return null;
    }

    public static Constructor<?> getAccessibleConstructor(Class<?> clazz, Class<?>... parameterTypes) throws Exception {
        Constructor<?> constructor = clazz.getDeclaredConstructor(parameterTypes);
        constructor.setAccessible(true);
        return constructor;
    }

    public static String readJsonFileToString(String filePath) {
        String jsonStr = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            StringBuffer content = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            jsonStr = content.toString();
        } catch (FileNotFoundException ex) {
            assert false;
        } catch (IOException ex) {
        	assert false;
        }
        return jsonStr;
    }

    public static boolean removePath(String path) {
        return removePath(new File(path));
    }

    public static boolean removePath(File path) {
        if (path.isFile()) {
            path.delete();
        } else if (path.isDirectory()) {
            for (File subpath : path.listFiles()) {
                removePath(subpath);
            }
            path.delete();
        }
        return !path.exists();

    }

    public static JarFile getJarFile() throws IOException {
        if (jarFile == null) {
            jarFile = new JarFile(TEST_RESOURCE_DIR + "/mock.jar");
        }
        return jarFile;
    }
    
    public static void set(Object obj, String name, Object value) throws Exception {
        Field field = ObjectAccessor.getAccessibleField(obj, name);
        field.set(obj, value);
    }
    
    public static void set(Class<?> clz, String name, Object value) throws Exception {
        Field field = ObjectAccessor.getAccessibleField(clz, name);
        field.set(null, value);
    }
    
    public static Object invoke(Class<?> clz, String methodName, Object[] params, Class<?>[] types) throws Exception {
        Method method = types == null ? clz.getDeclaredMethod(methodName) : clz.getDeclaredMethod(methodName, types);
        return params == null ? method.invoke(null) : method.invoke(null, params);
    }
    
    public static Object invoke(Object obj, String methodName, Object[] params, Class<?>[] types) throws Exception {
        Method method = obj.getClass().getDeclaredMethod(methodName, types);
        method.setAccessible(true);
        return method.invoke(obj, params);
    }
    
}
