package org.glamey.compiler;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;


/**
 * Java class 包装类
 *
 * @author zhouyang01
 * Created on 20220412.
 */
public class JavaClassFileObject extends SimpleJavaFileObject {

    private ByteArrayOutputStream byteArrayOutputStream;

    protected JavaClassFileObject(String javaClassName) {
        super(URI.create("byte:///" + javaClassName.replace('.', '/') + Kind.CLASS.extension), Kind.CLASS);
    }

    @Override
    public OutputStream openOutputStream() {
        return new FilterOutputStream(new ByteArrayOutputStream()) {
            @Override
            public void close() throws IOException {
                out.close();
                byteArrayOutputStream = (ByteArrayOutputStream) out;
            }
        };
    }

    public byte[] getBytes() {
        return byteArrayOutputStream.toByteArray();
    }

    public String getClassName() {
        String className = getName();
        className = className.replace('/', '.');
        className = className.substring(1, className.indexOf(Kind.CLASS.extension));
        return className;
    }
}
