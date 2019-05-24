package me.lcode.usecase.unittest;

import java.io.File;

public class ClassUnderTest {

    public boolean callArgumentInstance(File file) {
        return file.exists();
    }
    
    public boolean callInternalInstance(String path) {
        File file = new File(path);
        return file.exists();
    }
    
    public boolean callFinalMethod(ClassDependency refer) {
        return refer.isAlive();
    }
    
    public boolean callSystemFinalMethod(String str) {
        return str.isEmpty();
    }
    
    public boolean callStaticMethod() {
        return ClassDependency.isExist();
    }
    
    public String callSystemStaticMethod(String str) {
        return System.getProperty(str);
    }
    
    public boolean callPrivateMethod() {
        return isExist();
    }
    
    private boolean isExist() {
        // do something
        return false;
    }
}