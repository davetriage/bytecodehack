/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.triage.javacompiler;

/**
 *
 * @author dcowden
 */
public class CompileResult {
    protected byte[] bytes;
    protected Class clazz;
    
    public CompileResult(Class clazz, byte[] bytes){
       this.clazz = clazz;
       this.bytes = bytes;
    }    
    
    public Class getCreatedClass(){
        return this.clazz;
    }
    
    public byte[] getByteCode(){
        return this.bytes;
    }
}
