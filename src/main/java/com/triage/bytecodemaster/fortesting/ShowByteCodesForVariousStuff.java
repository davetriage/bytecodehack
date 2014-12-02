/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.triage.bytecodemaster.fortesting;

/**
 * This class exists only to show the bytecode for various snippets.
 * Compile this class, and then use javap -v -constants -c ShowByteCodesForVariousStuff.class to see the bytecode and constant pool
 * @author dcowden
 */
public class ShowByteCodesForVariousStuff {
    
    public void getBaseClassFromHolder(){
        TestBaseClass tbc = TestBaseClassHolder.getTestBase();
        String s = tbc.baseString;
    }
    
    public String shadowLocalVaraible(String x, String y){
        x = x+"foo";
        return x;
    }
    
    public void test_method(String x, String y, String z ){
        x = "foo";
    }
}
