 /*
  *  Copyright (C) 2022 github.com/REAndroid
  *
  *  Licensed under the Apache License, Version 2.0 (the "License");
  *  you may not use this file except in compliance with the License.
  *  You may obtain a copy of the License at
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
package com.reandroid.apkeditor.decompile;

import com.reandroid.apkeditor.APKEditor;
import com.reandroid.apkeditor.Options;
import com.reandroid.apkeditor.compile.Builder;
import com.reandroid.apkeditor.utils.StringHelper;
import com.reandroid.commons.command.ARGException;

import java.io.File;

public class DecompileOptions extends Options {
    public boolean splitJson;
    public boolean validateResDir;
    public String resDirName;
    public DecompileOptions(){
    }
    @Override
    public void parse(String[] args) throws ARGException {
        parseInput(args);
        parseOutput(args);
        parseSplitResources(args);
        parseResDirName(args);
        parseValidateResDir(args);
        super.parse(args);
    }
    private void parseValidateResDir(String[] args) throws ARGException {
        validateResDir=containsArg(ARG_validate_res_dir, true, args);
    }
    private void parseResDirName(String[] args) throws ARGException {
        this.resDirName=parseArgValue(ARG_resDir, true, args);
    }
    private void parseSplitResources(String[] args) throws ARGException {
        splitJson=containsArg(ARG_split_resources, true, args);
    }
    private void parseOutput(String[] args) throws ARGException {
        this.outputFile=null;
        File file=parseFile(ARG_output, args);
        if(file==null){
            file=getOutputFromInput(inputFile);
        }
        this.outputFile=file;
    }
    private File getOutputFromInput(File file){
        String name = file.getName();
        int i=name.lastIndexOf('.');
        if(i>0){
            name=name.substring(0, i);
        }
        name=name+"_out";
        File dir=file.getParentFile();
        if(dir==null){
            return new File(name);
        }
        return new File(dir, name);
    }
    private void parseInput(String[] args) throws ARGException {
        this.inputFile=null;
        File file=parseFile(ARG_input, args);
        if(file==null){
            throw new ARGException("Missing input file");
        }
        if(!file.isFile()){
            throw new ARGException("No such file: "+file);
        }
        this.inputFile=file;
    }

    @Override
    public String toString(){
        StringBuilder builder=new StringBuilder();
        builder.append("   Input: ").append(inputFile);
        builder.append("\n Output: ").append(outputFile);
        if(resDirName!=null){
            builder.append("\nres dir: ").append(resDirName);
        }
        if(validateResDir){
            builder.append("\n Validate res dir name: true");
        }
        if(force){
            builder.append("\n Force: true");
        }
        builder.append("\n Split: ").append(splitJson);
        builder.append("\n ---------------------------- ");
        return builder.toString();
    }
    public static String getHelp(){
        StringBuilder builder=new StringBuilder();
        builder.append(Decompiler.DESCRIPTION);
        builder.append("\nOptions:\n");
        String[][] table=new String[][]{
                new String[]{ARG_input, ARG_DESC_input},
                new String[]{ARG_output, ARG_DESC_output},
                new String[]{ARG_resDir, ARG_DESC_resDir}
        };
        StringHelper.printTwoColumns(builder, "   ", 75, table);
        builder.append("\nFlags:\n");
        table=new String[][]{
                new String[]{ARG_force, ARG_DESC_force},
                new String[]{ARG_split_resources, ARG_DESC_split_resources},
                new String[]{ARG_validate_res_dir, ARG_DESC_validate_res_dir}
        };
        StringHelper.printTwoColumns(builder, "   ", 75, table);
        String jar = APKEditor.getJarName();
        builder.append("\n\nExample-1:");
        builder.append("\n   java -jar ").append(jar).append(" ").append(Builder.ARG_SHORT).append(" ")
                .append(ARG_input).append(" path/to/input.apk");
        builder.append(" ").append(ARG_output).append(" path/to/out_dir");
        builder.append("\nExample-2:");
        builder.append("\n   java -jar ").append(jar).append(" ").append(Builder.ARG_SHORT).append(" ")
                .append(ARG_input).append(" path/to/input.apk");
        builder.append("\nExample-3:");
        builder.append("\n   java -jar ").append(jar).append(" ").append(Builder.ARG_SHORT).append(" ")
                .append(ARG_input).append(" path/to/input.apk").append(" ").append(ARG_split_resources);
        return builder.toString();
    }
    private static final String ARG_split_resources="-split-json";
    private static final String ARG_DESC_split_resources="splits resources.arsc into multiple parts as per type entries (use this for large files)";
}
