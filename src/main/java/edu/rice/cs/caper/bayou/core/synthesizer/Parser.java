/*
Copyright 2017 Rice University

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package edu.rice.cs.caper.bayou.core.synthesizer;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Parser {

    String source;
    String classpath;
    URL[] classpathURLs;
    CompilationUnit cu;

    /**
     * Place to send logging information.
     */
    private static final Logger _logger = LogManager.getLogger(EvidenceExtractor.class.getName());

    public Parser(String source, String classpath) throws ParseException {
        this.source = source;
        this.classpath = classpath;

        _logger.trace("source: " + source);
        _logger.trace("classpath:" + classpath);

        List<URL> urlList = new ArrayList<>();
        for (String cp : classpath.split(File.pathSeparator)) {
            _logger.trace("cp: " + cp);
            try {
                urlList.add(new URL("jar:file:" + cp + "!/"));
            } catch (MalformedURLException e) {
                throw new ParseException("Malformed URL in classpath.");
            }
        }
        this.classpathURLs = urlList.toArray(new URL[0]);
    }

    public void parse() throws ParseException {
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setSource(source.toCharArray());
        Map<String, String> options = JavaCore.getOptions();
        options.put("org.eclipse.jdt.core.compiler.source", "1.8");
        parser.setCompilerOptions(options);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setUnitName("Program.java");
        parser.setEnvironment(new String[] { classpath != null? classpath : "" },
                new String[] { "" }, new String[] { "UTF-8" }, true);
        parser.setResolveBindings(true);
        cu = (CompilationUnit) parser.createAST(null);

        List<IProblem> problems = Arrays.stream(cu.getProblems()).filter(p ->
                                            p.isError() &&
                                            p.getID() != IProblem.PublicClassMustMatchFileName && // we use "Program.java"
                                            p.getID() != IProblem.ParameterMismatch // Evidence varargs
                                        ).collect(Collectors.toList());
        if (problems.size() > 0)
            throw new ParseException(problems);
    }

    public String getSource() {
        return source;
    }

    public String getClasspath() {
        return classpath;
    }

    public CompilationUnit getCompilationUnit() {
        return cu;
    }
}
