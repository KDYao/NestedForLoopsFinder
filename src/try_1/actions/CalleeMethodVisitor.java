package try_1.actions;

import java.util.HashSet;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

public class CalleeMethodVisitor extends ASTVisitor {
	private HashSet<IMethod> callees;
	//new CalleeMethodVisitor时通过构造函数生成callees
	public CalleeMethodVisitor(){
		callees = new HashSet<IMethod>();
	}
	//当有方法调用时执行此visit()方法，将调用的方法的信息存入callee中
	@Override
	public boolean visit(MethodInvocation node) {
		// TODO Auto-generated method stub
		//获取调用方法节点的binding信息
		IMethodBinding mBinding = node.resolveMethodBinding();
		//如果binding信息不存在，则返回true继续执行
		if (mBinding==null) {
			//System.out.println("mBinding is Null, Node is :"+node);
			return true;
		}
		//当binding信息存在时，getMethodDeclaration通过binding获取当前方法声明（public void test1(String test)）
		//getJavaElement返回binding对应的Java元素
		IMethod iMethod = (IMethod) mBinding.getMethodDeclaration().getJavaElement();
		if(iMethod != null)
			callees.add(iMethod);
		return true;
	}
	//getter返回callees
	public HashSet<IMethod> getCalleeInit(){
		return callees;
	}

}
