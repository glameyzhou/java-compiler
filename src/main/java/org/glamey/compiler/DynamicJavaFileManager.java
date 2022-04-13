package org.glamey.compiler;

import java.util.ArrayList;
import java.util.List;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

/**
 * 动态类文件管理器
 * <p>
 *
 * @author zhouyang01
 * Created on 20220412.
 */
public class DynamicJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {
    private final DynamicClassLoader classLoader;
    private final List<JavaClassFileObject> javaClassFileObjects = new ArrayList<>();

    protected DynamicJavaFileManager(JavaFileManager fileManager, DynamicClassLoader classLoader) {
        super(fileManager);
        this.classLoader = classLoader;
    }

    @Override
    public ClassLoader getClassLoader(Location location) {
        return this.classLoader;
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling) {
        for (JavaClassFileObject classFileObject : javaClassFileObjects) {
            if (classFileObject.getClassName().equals(className)) {
                return classFileObject;
            }
        }
        //处理内部类
        JavaClassFileObject innerClassFileObject = new JavaClassFileObject(className);
        javaClassFileObjects.add(innerClassFileObject);
        classLoader.registerCompiledSource(innerClassFileObject);
        return innerClassFileObject;
    }
}
