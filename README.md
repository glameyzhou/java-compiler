# java-compiler

- 动态的将Java源代码编译为字节码，通过```javap -v className```来验证生成的字节码是否正确
```java
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
```
- 动态的将Java源代码编译为字节码，通过自定义ClassLoader加载至JVM中
```java
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
```

