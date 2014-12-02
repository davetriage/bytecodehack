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
public class TestBaseClass {
    
    public String baseString = "baseStringValue";
    
    public String getBaseStringValue(){
        return baseString;
    }
    
    public String testOverride(){
        return "baseClass";
    }
    public void setBaseString(String newValue){
        baseString = newValue;
    }
}
