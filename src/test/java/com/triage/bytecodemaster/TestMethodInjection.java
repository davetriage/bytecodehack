/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.triage.bytecodemaster;

import com.triage.bytecodemaster.fortesting.TestPerson;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;



/**
 * Tests bytecode weaving
 * Our evil goal here is to weave the body of groovy script into 
 * the body of another method!
 * 
 * The tricky part is that we want variables in the groovy script to be 
 * bound to the local variables in the inline method ( somehow ).
 * 
 * 
 * Objectives:
 *     Weave code from a donor class -- a groovy object -- into a target.
 *     Here are the things we want to prove work:
 * 
 *       local variable references match correctly. even if they were named differently
 *       
 *       arguments that are only available in the target classloader work,
 *             even if they were NOT available in the classloader that loaded the groovy class
 * 
 *       the donor class can have fewer or more arguments than those in the target method
 * 
 *       return statements in the donor code work correctly?
 * 
 *       IF the return type of the donor method matches the return type of the target method,
 *         AND the target return type is resolvable in the donor classLoader, then returns should work
 *    
 *       the donor method can add local variables inline and use them correctly-- even in 
 *       combination with locally defined variables
 *       
 *       the donor method can be added at the start or at the end of the target method
 * 
 *       ADVANCED:
 * 
 *              you can add try/catch/finally around a target method
 *              
 *              class variables in the donor class are somehow available?
 * 
 *     
 * @author dcowden
 */
public class TestMethodInjection extends BaseWeaverTest{
   
   
   
   //this is a pretty big deal. this trick allows classes in the byteCodeClassLoader 
   //to see those in the groovy class loader.
   //in a real system, this has big implications: it means somehow the groovy classes would need to be
   //in the system classloader-- which means pre-compiling on startup, or defining a custom system classloader,
   //which would be frowned upon i'm sure
   
   //protected ByteCodeClassLoader byteCodeClassLoader = new ByteCodeClassLoader(getClass().getClassLoader() );
   protected      String GROOVY_CLASS = "@groovy.transform.CompileStatic\n" +
                   "class ScriptTestClass{\n" +
                   "    String concat(String x, java.util.Date y){\n" +
                   "         def r = \"FromGroovy:\" + x + y             \n" +
                   "         println(r);\n" +
                   "         return r;\n" + 
                   "    }\n" +
                   "}";
   protected      String GROOVY_CLASS_2 = "\n" +
                   "\n" +
                   "    String concat(x, y){\n" +
                   "         def r = x + y + \"\"                       \n" +
                   "         println(\"FromGroovy:\" + r);\n" +
                   "         \n" + 
                   "    }\n" +
                   "";
   
   protected      String GROOVY_CLASS_3 = "@groovy.transform.CompileStatic\n" +
                   "class ScriptTestClass{\n" +
                   "    String testInjectReturn(){\n" +
                   "         return \"HAHAHA\"\n" + 
                   "    }\n" +
                   "}";  
   @Test
   public void testReadingLocalClass() throws Exception{
        //use meta class to dynamically set properties
       
        //Class groovyClass = makeGroovyScriptClass("int z=0;z=x+y");
        ClassNode classNode = loadLocalClass("com.triage.bytecodemaster.fortesting.TestBaseClass");
        
        
        for (Object o: classNode.methods){
            MethodNode mn = (MethodNode)o;
            System.out.println("Method Name:" + mn.name + "\t\tdesc=" + mn.desc);
        }
        
        MethodNode mn = findMethod(classNode,"getBaseStringValue");
        assertNotNull(mn);

   }
   @Test
   public void testDynamicGroovyClass() throws Exception{
       Class myClass = groovyClassLoader.parseClass(GROOVY_CLASS);
       GroovyObject go = (GroovyObject)myClass.newInstance();
       assertNotNull(go.invokeMethod("concat", new Object[]{"foo",new Date() }));
   }
   
   @Test
   public void testGettingByteCodeFromAGroovyClass() throws Exception {
        
        ClassNode classNode = loadGroovyTestClassAsBytecode(GROOVY_CLASS);
        
        //get the run method
        MethodNode runMethod = findMethod(classNode, "concat");
        List localVariables = runMethod.localVariables;
        
        assertTrue(runMethod.instructions.toArray().length > 0 );
               
   }
   
   @Test
   public void testInjectingGroovyClassBodyIntoOtherClass()throws Exception{
       
       //get the dynamic source that has the donor body in it
       ClassNode donorSource = loadGroovyTestClassAsBytecode(GROOVY_CLASS_3);
       MethodNode donorMethod = findMethod(donorSource,"testInjectReturn");
       
       
       //load the target class
       String TARGETCLASSNAME = "com.triage.bytecodemaster.fortesting.TestBaseClass";
       ClassNode targetSource = loadLocalClass(TARGETCLASSNAME);       
       MethodNode targetMethod = findMethod(targetSource,"getBaseStringValue");
       System.out.println("Target Method:");
       printMethodNode(targetMethod);
       
       //stash the donorInto the target
       System.out.println("Parsed Donor Method.");
       printMethodNode(donorMethod);       
       
       targetMethod.instructions.insert(donorMethod.instructions);
       
       //write a class 
       Class c = createClassFromClassNode(targetSource,TARGETCLASSNAME);
              
       Object o = c.newInstance();
       Method m = o.getClass().getDeclaredMethod("getBaseStringValue");      
       
       //should return HAHAHA not baseStringValue 
       String result = (String)m.invoke(o, new Object[]{} );
       System.out.println("getBaseStringValue Result: " + result);
       assertTrue(result.equals("HAHAHA"));
       
   }

   
}
