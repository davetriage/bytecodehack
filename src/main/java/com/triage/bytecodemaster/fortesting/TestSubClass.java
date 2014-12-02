/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.triage.bytecodemaster.fortesting;

/**
 *
 * @author dcowden
 */
public class TestSubClass extends TestBaseClass{
    
    public void testSubClassMethod(){
        String fooLocal = super.getBaseStringValue();
        concat(getBaseStringValue() , "HEHE");
        
        String fooLocal2 = getBaseStringValue();
        concat(baseString, "FAR");
        
        concat(super.getBaseStringValue() , super.getBaseStringValue() );        
        String fooLocal3 = baseString;        

    }
    
    public String testLocalMethod(){        
        return "bar";
    }
    
    private void concat(String workItem, String other){
        String x = workItem + other;
    }
    public String testOverride(){
        return "subclass";
    }
}
