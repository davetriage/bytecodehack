/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.triage.bytecodemaster.fortesting;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

/**
 * Just like the groovy class in TestObjectReferenceSwitches
 * @author dcowden
 */
public class JustLikeGroovyClass extends TestSubClass{
    
    public void before_whatDoIThinkAbout(String x, String y){
        x = this.baseString;        
        
    }
}
