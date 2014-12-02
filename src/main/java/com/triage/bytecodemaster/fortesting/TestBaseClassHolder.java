/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.triage.bytecodemaster.fortesting;

/**
 * Tests Holding a BaseClass
 * @author dcowden
 */
public class TestBaseClassHolder {
    
    protected static TestBaseClass plugin;
    
    public static void setTestBase(TestBaseClass plugin){
        TestBaseClassHolder.plugin = plugin;
    }
    
    public static TestBaseClass getTestBase(){
        return TestBaseClassHolder.plugin;
    }
    
}
