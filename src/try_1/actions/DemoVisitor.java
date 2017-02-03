package try_1.actions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyStore.Entry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.corext.callhierarchy.MethodWrapper;



public class DemoVisitor extends ASTVisitor{
	//private boolean forFlag=false;
	//int array[]=new int[200];
	private HashMap<String, Integer> loopCount = new HashMap<String,Integer>();
	//通过CalleeMethods构造函数传入loopCount
	private CalleeMethods calleeMethods = new CalleeMethods(loopCount);
	//private HashSet<IMethod> forNodeVisited=new HashSet<IMethod>();
	CalleeMethods cMethods=new CalleeMethods(loopCount);
	//构造函数传参到CalleeMethod
	public DemoVisitor(IJavaProject iJavaProject) {
		// TODO Auto-generated constructor stub
		calleeMethods.setIJavaProject(iJavaProject);
	}
//	@Override
//	public boolean visit(TypeDeclaration node) {
//		// TODO Auto-generated method stub
//		return true;
//	}
//	@Override
//	public boolean visit(MethodDeclaration node) {
//		// TODO Auto-generated method stub
//		return true;
//	}
//	@Override
//	public boolean visit(FieldDeclaration node) {
//		// TODO Auto-generated method stub
//		return true;
//	}
	/*
	//检测do-while
	@Override
	public boolean visit(DoStatement node) {
		// TODO Auto-generated method stub
		CalleeMethodVisitor calleeMethodVisitor=new CalleeMethodVisitor();
		node.accept(calleeMethodVisitor);
		handleLoop(calleeMethodVisitor.getCalleeInit(),node);
		return true;
	}
	*/
	//检测EnhancedFor: for(Iterator it:lists)
	@Override
	public boolean visit(EnhancedForStatement node) {
		// TODO Auto-generated method stub
		CalleeMethodVisitor calleeMethodVisitor=new CalleeMethodVisitor();
		node.accept(calleeMethodVisitor);
		handleLoop(calleeMethodVisitor.getCalleeInit(),node);
		return true;
	}
	//检测正常的for loop
	@Override
	public boolean visit(ForStatement node) {
		// TODO Auto-generated method stub
//		System.out.println("For Statement body:"+node.getBody());
//		ASTNode parentNode=findParentNode(node);
//		if (parentNode.getNodeType()==ASTNode.METHOD_DECLARATION) {
//			//MethodDeclaration parentMethod=(MethodDeclaration) parentNode;
//			IMethodBinding methodBinding=((MethodDeclaration) parentNode).resolveBinding();
//			//getMethodDeclaration returns the method binding
//			//getJavaElement returns the java element corresponds to this binding
//			IMethod iMethod=(IMethod) methodBinding.getMethodDeclaration().getJavaElement();
//			HashSet<IMethod> callee=new HashSet<IMethod>();
//			if (iMethod!=null) {
//				System.out.println("Method Name:"+iMethod.getElementName());
//				 callee.add(iMethod);
//			}
//			
//		}
		CalleeMethodVisitor calleeMethodVisitor=new CalleeMethodVisitor();
		node.accept(calleeMethodVisitor);
		handleLoop(calleeMethodVisitor.getCalleeInit(),node);
		return true;
	}
	/*
	//检测while循环
	@Override
	public boolean visit(WhileStatement node) {
		// TODO Auto-generated method stub
		CalleeMethodVisitor calleeMethodVisitor=new CalleeMethodVisitor();
		node.accept(calleeMethodVisitor);
		//getCalleeInit返回callees
		handleLoop(calleeMethodVisitor.getCalleeInit(),node);
		return true;
	}
	*/
	/**
	 * 
	 * @param calleeiMethod   获取的MethodInvocation的callees
	 * @param node            Statement的node,各个loop类型
	 * @param body			  找出的各个循环node的方法体
	 * @return
	 */
	private boolean handleLoop(HashSet<IMethod> calleeiMethod, Statement node) {
		// TODO Auto-generated method stub
		//之前的每个节点的callee存到calleeinit
		HashSet<IMethod> calleeinit=calleeiMethod;
		//新建一个callees的HashSet
		HashSet<IMethod> callees = new HashSet<IMethod>();
		//HashMap<Statement,Integer> nodeHash=new HashMap<Statement,Integer>();
		//将传入的所有MethodInvocation得到的callees保存到本类的callees中
		callees.addAll(calleeinit);
		//找到顶层的forNode
		ASTNode parentNode=findParentNode(node);
		if (parentNode!=null) {	
			//如果该节点是一个MethodDeclaration
			if (parentNode.getNodeType()==ASTNode.METHOD_DECLARATION) {
				//MethodDeclaration parentMethod=(MethodDeclaration) parentNode;
				//返回该节点binding信息
				IMethodBinding methodBinding=((MethodDeclaration) parentNode).resolveBinding();
				//如果binding为空的话返回true跳出
				if (methodBinding==null) {
					return true;
				}
				//获取当前方法的Java Element信息
				IMethod iMethod=(IMethod) methodBinding.getMethodDeclaration().getJavaElement();
				if (iMethod!=null) {
					//不为空时，把得到的MethodDeclaration方法的信息也保存到callee中
					//现在callees中有MethodInvocation的方法信息和MethodDeclaration的方法信息
					callees.add(iMethod);
					//MethodDeclaration信息单独保存到calleeinit中
					//calleeinit.add(iMethod);
				}
				//if (!methodCallees.containsKey(iMethod)) {
				try {
					callees.addAll(calleeMethods.getCalleesOf(calleeinit, this));
					//methodCallees.put(iMethod, callees);
				} catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
				//}
				//callees=methodCallees.get(iMethod);
				//遍历callee
				for (Iterator<IMethod> it = callees.iterator(); it.hasNext();) {
					IMethod method = (IMethod) it.next();
					//forFlag=getForInSub(method);
					/* 
					if (cMethods.getHashMap().get(method.getKey())>=10) {
						if (!forFlag) {
							System.out.println("SubForMethod\t" + method.getKey());
							System.out.println("Layer:\t"+cMethods.getHashMap().get(method.getKey()));
						}
						System.out.println("forStatement:\t" + body);
						System.out.println("******************************");
					}
					*/
					
					//if (forFlag) {
						if(method.getKey().equals(iMethod.getKey())){
							//System.out.println("CurrentFor: "+method.getKey());
							//System.out.println("Layer:\t1");
							//nodeHash.put(node, 1);
							//System.out.println("node Layer1:"+node);
							//array[1]++;
						}else if (cMethods.getHashMap().get(method.getKey())!=null) {					
							//forNodeVisited.add(method);
							//System.out.println("SubForMethod\t" + method.getKey());
							//System.out.println("Layer:\t"+cMethods.getHashMap().get(method.getKey()));
							//nodeHash.put(node, cMethods.getHashMap().get(method.getKey()));
							//System.out.println("CurrentForNode: "+node);
							//array[cMethods.getHashMap().get(method.getKey())]++;
						}
						
						if (cMethods.getHashMap().containsKey(method.getKey())) {
							//int layers=cMethods.getHashMap().get(method.getKey());
							
							//System.out.println("Layer= "+layers);
							//System.out.println("Current For Node: "+getForInSub(method));
						}
						//System.out.println("forStatement:\t" + body);
						//System.out.println("methodKey:"+method.getKey());
						//System.out.println("******************************");
						// find the for statement in which method
					//}
					
				}
				//callees=methodCallees.get(iMethod);
//				for (Iterator itcallee = callees.iterator(); itcallee.hasNext();) {
//					System.out.println("Callee method\t" + itcallee.next());
//				}
			}
		}
		return true;
		}
	

	/**
	 * 判断子方法中还有没有for循环
	 * @param method
	 * @return
	 */
	public Statement getForInSub(IMethod method) {
		// TODO Auto-generated method stub
		ForStatementVisitor forStatementVisitor=new ForStatementVisitor();
		MethodDeclaration methodNode = astNode(method);
		if(methodNode == null)
			return null;
		methodNode.accept(forStatementVisitor);
		//System.out.println("SubForMethod:\t"+method.getElementName());
		return forStatementVisitor.getForExists();
	}
	private MethodDeclaration astNode(final IMethod method)
	{
		final ICompilationUnit compilationUnit = method.getCompilationUnit();
		if(compilationUnit == null)
			return null;
		
		//CompilationUnit root = SharedASTProvider.getAST(compilationUnit, SharedASTProvider.WAIT_NO, null);	//add test
		ASTParser astParser = ASTParser.newParser(AST.JLS3);
		astParser.setSource(compilationUnit);
		astParser.setKind(ASTParser.K_COMPILATION_UNIT);
		astParser.setResolveBindings( true );
		astParser.setBindingsRecovery( true );
		//通过方法的起点和终点获取方法ASTNode
		ASTNode rootNode = astParser.createAST( null );
		ASTNode currentNode = null;
		try {
			currentNode = NodeFinder.perform( rootNode, method.getSourceRange());
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(currentNode instanceof MethodDeclaration){
			return (MethodDeclaration)currentNode;
		}
		return null;
	}
//	public HashSet<IMethod> getCalleesOf(HashSet<IMethod> methodsSet)
//			throws JavaModelException {
//		return calleeMethods.getCalleesOf(methodsSet, this);
//	}
//	HashSet<IMethod> getIMethods(MethodWrapper[] methodWrappers) {
//		return calleeMethods.getGetIMethod().getIMethods(methodWrappers);
//	}
//	IMethod getIMethodFromMethodWrapper(MethodWrapper method_wrapper) {
//		return calleeMethods.getGetIMethod().getIMethodFromMethodWrapper(method_wrapper);
//	}
//	@Override
//	public boolean visit(MethodInvocation node) {
//		// TODO Auto-generated method stub
//		return true;
//	}
	
	/**
	 * 递归的查找出最顶层的节点
	 * @param node
	 * @return
	 */
	private ASTNode findParentNode(ASTNode node) {
		// TODO Auto-generated method stub
		try {
			//返回node的父节点的类型
			int parentNode=node.getParent().getNodeType();	
			if (parentNode==ASTNode.METHOD_DECLARATION) {
				return node.getParent();
			}
			if (parentNode==ASTNode.INITIALIZER) {
				return node.getParent();
			}
			if (parentNode==ASTNode.TYPE_DECLARATION) {
				return node.getParent();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		//如果当前节点为空，返回空
		if (node==null) {
			return null;
		}else {		
			//递归查找知道找到符合父节点满足三个中的一个，返回其父节点
			return findParentNode(node.getParent());
		}
	}
	/**
	 * 设置搜索范围是整个工程，而且只搜索源码,不搜索依赖的包和系统包
	 * @param IJavaProject
	 * @return searchCode
	 */
	public IJavaSearchScope getSearchScope(IJavaProject javaProject){
		//设置搜索范围是整个工程，而且只搜索源码,不搜索依赖的包和系统包
		int constraints = IJavaSearchScope.SOURCES;
		//constraints |= IJavaSearchScope.APPLICATION_LIBRARIES;
		//constraints |= IJavaSearchScope.SYSTEM_LIBRARIES;
		IJavaElement[] javaElements = new IJavaElement[] {javaProject};
		IJavaSearchScope searchCode = SearchEngine.createJavaSearchScope(javaElements,constraints);
		return searchCode;
	}
	public void print(){
//		for(int i=1; i<200;i++){
//			System.out.println("Layer "+i+" is executed "+array[i]+" times.");
//		}
		Iterator entries = cMethods.getHashMap().entrySet().iterator();
		while(entries.hasNext()) {
			Map.Entry entry = (Map.Entry)entries.next();
			//int layers=cMethods.getHashMap().get(method.getKey());
			
			System.out.println("Layer= "+entry.getValue());
			//System.out.println("methodKey: "+entry.getKey());
			//System.out.println("get: "+cMethods.getHashMap().get(entry.getKey()));
		}
	}
}
	