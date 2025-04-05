
package com.compiladores;

import java.io.IOException;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;

public class Main {

	private static final String EXTENSION = "l2";

	public static void main(String[] args) throws IOException {
		String program = args.length > 1 ? args[1] : "test/test." + EXTENSION;

		System.out.println("Interpreting file " + program);

		Lenguaje2Lexer lexer = new Lenguaje2Lexer(new ANTLRFileStream(program));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		Lenguaje2Parser parser = new Lenguaje2Parser(tokens);

		Lenguaje2Parser.ProgramaContext tree = parser.programa();

		Lenguaje2CustomVisitor visitor = new Lenguaje2CustomVisitor();
		visitor.visit(tree);

		System.out.println("Interpretation finished");

	}

}
