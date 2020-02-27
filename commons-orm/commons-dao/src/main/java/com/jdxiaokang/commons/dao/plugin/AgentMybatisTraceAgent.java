package com.jdxiaokang.commons.dao.plugin;

import com.baomidou.mybatisplus.core.override.MybatisMapperMethod;
import javassist.*;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.javassist.*;
import org.apache.ibatis.reflection.ParamNameResolver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.text.MessageFormat;
import java.util.regex.Pattern;

public class AgentMybatisTraceAgent {

    public static void agentmain(String agentArgs, Instrumentation inst) throws UnmodifiableClassException, ClassNotFoundException {
        Pattern methodSignaturePattern = Pattern.compile(MessageFormat.format("^{0}$",
                MapperMethod.MethodSignature.class.getName().replaceAll("\\$", "\\\\\\$")));
        Pattern paramNameResolverPattern = Pattern.compile(MessageFormat.format("^{0}$", ParamNameResolver.class.getName()));
        Pattern mapperMethodPattern = Pattern.compile(MessageFormat.format("^{0}$", MybatisMapperMethod.class.getName()));

        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                byte[] classBytes = classfileBuffer;
                if (null != className) {
                    String classNameReplace = className.replaceAll("/", ".");
                    if (paramNameResolverPattern.matcher(classNameReplace).find()) {
                        classBytes = paramNameResolver(classfileBuffer);
                    } else if (mapperMethodPattern.matcher(classNameReplace).find()) {
                        classBytes = mapperMethodProxy(classfileBuffer);
                    } else if (methodSignaturePattern.matcher(classNameReplace).find()) {
                        classBytes = methodSignaturePattern(classfileBuffer);
                    }
                }
                return classBytes;
            }
        }, true);

        for (Class allLoadedClass : inst.getAllLoadedClasses()) {
            if (paramNameResolverPattern.matcher(allLoadedClass.getName()).find()
                    || mapperMethodPattern.matcher(allLoadedClass.getName()).find()
                    || methodSignaturePattern.matcher(allLoadedClass.getName()).find()) {
                inst.retransformClasses(allLoadedClass);
            }
        }
    }

    private static byte[] mapperMethodProxy(byte[] classfileBuffer) {
        byte[] transformed = null;
        CtClass ctClass = null;
        try {
            ClassPool pool = ClassPool.getDefault();
            //增加分页上下文
            pool.importPackage("com.jdxiaokang.util.mybatis.plugin.page.entity.Page");
            pool.importPackage("java.util.List");
            pool.importPackage("java.util.Iterator");
            pool.importPackage("java.util.Arrays");

            ctClass = pool.makeClass(new ByteArrayInputStream(classfileBuffer));

            String seekPageBody = "private final Page seekPage(Object parameter) {\n" +
                    "        Page page = null;\n" +
                    "        if (parameter == null) {\n" +
                    "            return null;\n" +
                    "        }\n" +
                    "        if (parameter instanceof Page) {\n" +
                    "            page = (Page) parameter;\n" +
                    "        } else if (parameter instanceof java.util.Map) {\n" +
                    "            java.util.Map/*<String, Object>*/  map = (java.util.HashMap) parameter;\n" +
                    "            Iterator it = map.values().iterator();\n" +
                    "            while (it.hasNext()) {\n" +
                    "                Object arg = it.next();\n" +
                    "                if (arg instanceof Page) {\n" +
                    "                    page = (Page) arg;\n" +
                    "                    break;\n" +
                    "                }\n" +
                    "            }\n" +
                    "        }\n" +
                    "        return page;\n" +
                    "    }";
            CtMethod seekPageMethod = CtNewMethod.make(seekPageBody, ctClass);
            ctClass.addMethod(seekPageMethod);

            String convertResultBody = "private final Object convertResult(Object[] args,Object obj){\n" +
                    "        if(null != obj){\n" +
                    "            if(!obj.getClass().isAssignableFrom(method.getReturnType())\n" +
                    "                    && Page.class.isAssignableFrom(method.getReturnType())){\n" +
                    "                Page page = new Page();\n" +
                    "                Page param  = seekPage(args);\n" +
                    "\n" +
                    "                if(obj instanceof java.util.List){\n" +
                    "                    page.setObject((java.util.List)obj);\n" +
                    "                }else{\n" +
                    "                        java.util.List list = new java.util.ArrayList();\n" +
                    "                        list.add(obj);" +
                    "                    page.setObject(list);\n" +
                    "                }\n" +
                    "\n" +
                    "                if(null != param){\n" +
                    "                    page.setTotalCount(param.getTotalCount());\n" +
                    "                    page.setPageSize(param.getPageSize());\n" +
                    "                    page.setToalPage(param.getToalPage());\n" +
                    "                }\n" +
                    "                obj =  page;\n" +
                    "            }\n" +
                    "        }\n" +
                    "        return obj;\n" +
                    "    }";
            CtMethod convertResultMethod = CtNewMethod.make(convertResultBody, ctClass);
            ctClass.addMethod(convertResultMethod);

            String sourceMethodName = "execute";
            CtClass[] ctClassSourceMethodParams = {pool.get(SqlSession.class.getName()), pool.get(Object[].class.getName())};
            CtMethod ctMethodExecute = ctClass.getDeclaredMethod(sourceMethodName, ctClassSourceMethodParams);
            ctMethodExecute.instrument(
                    new ExprEditor() {
                        public void edit(MethodCall methodCall) throws CannotCompileException {
                            methodCall.replace("{ " +
                                    " $_ = $proceed($$); " +
                                    " System.out.println(\"============wode事情=======\"); " +
                                    " System.out.println((Object[])$2); " +
                                    " }");
                            //$_ = convertResult((Object[])$1,null);
                        }
                    });

            transformed = ctClass.toBytecode();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ctClass != null) {
                ctClass.detach();
            }
        }
        return transformed;
    }

    private static byte[] paramNameResolver(byte[] classfileBuffer) {
        byte[] transformed = null;
        CtClass ctClass = null;
        String sourceMethodName = "isSpecialParameter";
        try {
            ClassPool pool = ClassPool.getDefault();
            //增加分页上下文
            pool.importPackage("com.jdxiaokang.util.mybatis.plugin.page.entity.Page");

            ctClass = pool.makeClass(new ByteArrayInputStream(classfileBuffer));
            CtClass[] ctClassParamNameResolver = {pool.get(Class.class.getName())};
            CtMethod ctMethodIsSpecialParameter = ctClass.getDeclaredMethod(sourceMethodName, ctClassParamNameResolver);

            ctMethodIsSpecialParameter.instrument(
                    new ExprEditor() {
                        public void edit(MethodCall methodCall) throws CannotCompileException {
                            methodCall.replace("{ " +
                                    " $_ = ($proceed($$) || Page.class.isAssignableFrom($1)); " +
                                    " }");
                        }
                    });

            transformed = ctClass.toBytecode();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ctClass != null) {
                ctClass.detach();
            }
        }
        return transformed;
    }

    private static byte[] methodSignaturePattern(byte[] classfileBuffer) {
        byte[] transformed = null;
        CtClass ctClass = null;
        try {
            ClassPool pool = ClassPool.getDefault();
            //增加分页上下文
            pool.importPackage("com.jdxiaokang.util.mybatis.plugin.page.entity.Page");

            ctClass = pool.makeClass(new ByteArrayInputStream(classfileBuffer));
            CtClass[] ctClassParamNameResolver = {pool.get(Configuration.class.getName())
                    , pool.get(Class.class.getName()), pool.get(Method.class.getName())};
            CtConstructor ctConstructor = ctClass.getDeclaredConstructor(ctClassParamNameResolver);

            ctConstructor.instrument(
                    new ExprEditor() {
                        public void edit(MethodCall methodCall) throws CannotCompileException {
                            methodCall.replace("{ " +
                                    " $_ = $proceed($$); " +
                                    " this.returnsMany = (this.returnsMany || (null != this.returnType && Page.class.isAssignableFrom(this.returnType)) ); }");
                        }
                    });

            transformed = ctClass.toBytecode();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ctClass != null) {
                ctClass.detach();
            }
        }
        return transformed;
    }
}