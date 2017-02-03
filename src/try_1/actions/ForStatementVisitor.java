package try_1.actions;


import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.Statement;


public class ForStatementVisitor extends ASTVisitor{
	private Statement forExist = null;
	
	//for Node 判定子方法中还有没有for
	public boolean visit(ForStatement node) {
		// TODO Auto-generated method stub
		forExist=node;
		//System.out.println("******************************");
		//System.out.println("SubFor:  "+node);
		return true;
	}
	public boolean visit(EnhancedForStatement node) {
		forExist=node;
		return true;
	}
	
	public Statement getForExists(){
		return forExist;
	}
	
}
