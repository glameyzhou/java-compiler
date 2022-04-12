package org.glamey.compiler;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import javax.tools.JavaFileObject.Kind;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author zhouyang01
 * Created on 2022.04.12.
 */
class DynamicJavaCompilerTest {


    @Test
    void testGenBytesForSingle() throws IOException {
        String className = "org.glamey.compiler.Single";
        String javaSourceFile =
                "/Users/yang.zhou/Documents/idea_projects/java-compiler/java-compiler/src/test/java/org/glamey"
                        + "/compiler/Single.source";
        List<String> lines = Files.readAllLines(Paths.get(javaSourceFile), StandardCharsets.UTF_8);
        String javaSourceContent = String.join("", lines);
        DynamicClassLoader classLoader = new DynamicClassLoader(DynamicJavaCompilerTest.class.getClassLoader());
        DynamicJavaCompiler compiler = new DynamicJavaCompiler(classLoader);
        compiler.addSource(className, javaSourceContent);
        Map<String, byte[]> classNameByteMap = compiler.genClassBytes();
        classNameByteMap.forEach((clazzName, bytes) -> {
            try {
                //class 文件输出
                String classFilePath =
                        "/Users/yang.zhou/java_compiler/" + clazzName.substring(clazzName.lastIndexOf(".") + 1)
                                + Kind.CLASS.extension;
                Files.write(Paths.get(classFilePath), bytes);
                File classFile = new File(classFilePath);
                Assertions.assertTrue(classFile.exists());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    void testGenBytesForInner() throws IOException {
        String className = "org.glamey.compiler.InnerTest";
        String javaSourceFile =
                "/Users/yang.zhou/Documents/idea_projects/java-compiler/java-compiler/src/test/java/org/glamey"
                        + "/compiler/InnerTest.source";
        List<String> lines = Files.readAllLines(Paths.get(javaSourceFile), StandardCharsets.UTF_8);
        String javaSourceContent = String.join("", lines);
        DynamicClassLoader classLoader = new DynamicClassLoader(DynamicJavaCompilerTest.class.getClassLoader());
        DynamicJavaCompiler compiler = new DynamicJavaCompiler(classLoader);
        compiler.addSource(className, javaSourceContent);
        Map<String, byte[]> classNameByteMap = compiler.genClassBytes();
        classNameByteMap.forEach((clazzName, bytes) -> {
            try {
                //class 文件输出
                String classFilePath =
                        "/Users/yang.zhou/java_compiler/" + clazzName.substring(
                                clazzName.lastIndexOf(".") + 1) + Kind.CLASS.extension;
                Files.write(Paths.get(classFilePath), bytes);
                Assertions.assertTrue(new File(classFilePath).exists());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    void testGenClazzForSingle() throws IOException {
        String className = "org.glamey.compiler.Single";
        String javaSourceFile =
                "/Users/yang.zhou/Documents/idea_projects/java-compiler/java-compiler/src/test/java/org/glamey"
                        + "/compiler/Single.source";
        List<String> lines = Files.readAllLines(Paths.get(javaSourceFile), StandardCharsets.UTF_8);
        String javaSourceContent = String.join("", lines);
        DynamicClassLoader classLoader = new DynamicClassLoader(DynamicJavaCompilerTest.class.getClassLoader());
        DynamicJavaCompiler compiler = new DynamicJavaCompiler(classLoader);
        compiler.addSource(className, javaSourceContent);
        Map<String, Class<?>> clazzMap = compiler.genClasses();
        clazzMap.forEach((clazzName, clazz) -> {
            try {
                Method toStringMethod = clazz.getMethod("toString", null);
                toStringMethod.setAccessible(true);
                Object obj = clazz.newInstance();
                System.out.println(toStringMethod.invoke(obj, null));
                Assertions.assertEquals("UserObject{code='zhangsan', name='张三'}", toStringMethod.invoke(obj, null));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    void testGenClazzForInner() throws IOException {
        String className = "org.glamey.compiler.InnerTest";
        String javaSourceFile =
                "/Users/yang.zhou/Documents/idea_projects/java-compiler/java-compiler/src/test/java/org/glamey"
                        + "/compiler/InnerTest.source";
        List<String> lines = Files.readAllLines(Paths.get(javaSourceFile), StandardCharsets.UTF_8);
        String javaSourceContent = String.join("", lines);
        DynamicClassLoader classLoader = new DynamicClassLoader(DynamicJavaCompilerTest.class.getClassLoader());
        DynamicJavaCompiler compiler = new DynamicJavaCompiler(classLoader);
        compiler.addSource(className, javaSourceContent);
        Map<String, Class<?>> clazzMap = compiler.genClasses();
       clazzMap.forEach((clazzName, clazz) -> {
           if (clazzName.contains("$")) {
               try {
                   Method innerMethod = clazz.getMethod("inner", null);
                   innerMethod.setAccessible(true);
                   Object obj = clazz.newInstance();
                   Assertions.assertEquals("this is the inner class method", innerMethod.invoke(obj, null));
               } catch (Exception e) {
                   e.printStackTrace();
               }
           } else {
               try {
                   Method outMethod = clazz.getMethod("out", null);
                   outMethod.setAccessible(true);
                   Object obj = clazz.newInstance();
                   Assertions.assertEquals("this is the outer class method", outMethod.invoke(obj, null));
               } catch (Exception e) {
                   e.printStackTrace();
               }
           }
       });
    }
}