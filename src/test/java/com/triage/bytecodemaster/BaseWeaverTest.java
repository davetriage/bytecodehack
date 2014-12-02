/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.triage.bytecodemaster;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.Before;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

/**
 *
 * @author dcowden
 */
public class BaseWeaverTest {

   protected CachingGroovyClassLoader groovyClassLoader = null;
   protected ByteCodeClassLoader byteCodeClassLoader = null;
   
   @Before
   public void setup(){
        groovyClassLoader = new CachingGroovyClassLoader(getClass().getClassLoader());
        byteCodeClassLoader = new ByteCodeClassLoader(getClass().getClassLoader() );       
   }
   protected void printLocalVariables(MethodNode mn){       
       for ( Object o: mn.localVariables){
           LocalVariableNode vn =(LocalVariableNode)o;
           System.out.println(vn.name);
       }       
   }
   protected ClassNode loadLocalClass(String className) throws Exception{
        //Class groovyClass = makeGroovyScriptClass("int z=0;z=x+y");
        ClassNode classNode = new ClassNode();
        
        ClassReader classReader = new ClassReader(className);
        classReader.accept(classNode,0);
        return classNode;
   }
   
   protected ClassNode loadGroovyTestClassAsBytecode(String classSource) throws Exception{
        ClassNode classNode = new ClassNode();
        String scriptName = "ScriptTestClass.groovy";
        
      
        Class groovyClass = groovyClassLoader.parseClass(classSource,scriptName);
        
        String className = groovyClass.getName() + ".class";
        byte[] classBytes = groovyClassLoader.getClassBytes(className);
        ClassReader classReader = new ClassReader(classBytes);
        classReader.accept(classNode,0);  
        return classNode;
   }
   
   protected void printMethodNode(MethodNode mn){
       System.out.println("MethodNode: " + mn.name);
       System.out.println("Local Vars:");
       printLocalVariables(mn);     
       
       System.out.println("\n\nInstructions:");
       System.out.println(printMethodNodeInstructions(mn));       
   }
   protected String printMethodNodeInstructions( MethodNode mn){
       StringBuffer sb = new StringBuffer();
       for ( AbstractInsnNode aa: mn.instructions.toArray()){
           sb.append(printNode(aa));
       }       
       return sb.toString();
   } 
   protected String printNode(AbstractInsnNode insnNode ){
        /* Create a "printer" that renders text versions of instructions */
        Printer printer = new Textifier();
        TraceMethodVisitor methodPrinter = new TraceMethodVisitor(printer);

        /* render the instruction as a string and add it to printer's internal buffer */
        insnNode.accept(methodPrinter);

        /* convert printer's internal buffer to string and clear the buffer (so we can reuse it later) */
        StringWriter sw = new StringWriter();
        printer.print(new PrintWriter(sw));
        printer.getText().clear();
        return sw.toString();
     
   }
   
   protected MethodNode findMethod(ClassNode classNode, String name){
       for ( Object o: classNode.methods){
           MethodNode mn = (MethodNode)o;
           
           //not worrying about signatures now
           if ( mn.name.equals(name)){
               return mn;
           }
       }
       return null;
   }    
   
   protected Class createClassFromClassNode(ClassNode sourceNode,String className) throws Exception{
       //write a class 
       ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
       sourceNode.accept(cw);
       byte[] classBytes = cw.toByteArray();
       
       //make a classloader to load it. this wouldnt be necessary in a java agent-- we
       //just return the bytes. but this allows testing it
       //this classloader(System) cannot refer to the new class explicitly, so we have
       //to use reflection. but that proves the point.
       
       byteCodeClassLoader.addClassDef(className, classBytes);
       Class c = byteCodeClassLoader.findClass(className); 
       return c;
   }
}
