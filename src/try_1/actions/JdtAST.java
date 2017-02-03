package try_1.actions;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class JdtAST {
	public static CompilationUnit getCompilationUnit(ICompilationUnit iCompilationUnit) {
		ASTParser astParser=ASTParser.newParser(AST.JLS3);
		astParser.setResolveBindings(true);
		astParser.setSource(iCompilationUnit);
		astParser.setBindingsRecovery(true);
		astParser.setKind(ASTParser.K_COMPILATION_UNIT);
		CompilationUnit result=(CompilationUnit) astParser.createAST(null);
		return result;
		}
}
