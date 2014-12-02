/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.triage.javacompiler;

import java.util.List;

/**
 * Wrapper for the horrible JavaCompiler Api.
 * 
 * @author dcowden
 */
public class JavaCompiler {
   
    protected ClassLoader classLoader;
    public JavaCompiler(ClassLoader classLoader){
        this.classLoader = classLoader;
    }
    
    public List<CompileResult> compile(List<CompileRequest> requests){
        return null;
    }
    
}

class CompileRequest{
    protected String sourceCode;
    public CompileRequest(String javaSource){
        this.sourceCode = javaSource;
    }
}
