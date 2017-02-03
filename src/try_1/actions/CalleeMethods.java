package try_1.actions;

import org.eclipse.jdt.core.IJavaProject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.internal.corext.callhierarchy.CallHierarchy;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.internal.corext.callhierarchy.MethodWrapper;

import org.eclipse.core.runtime.NullProgressMonitor;

public class CalleeMethods {
	private getIMethod getIMethod = new getIMethod();
	// public HashMap<IMethod,Integer> loopCount=new HashMap<IMethod,Integer>();
	public static HashMap<String, Integer> loopCount = new HashMap<String,Integer>();
	private IJavaProject iJavaProject;
	public int count = 1;
	private Statement forFlag;

	public CalleeMethods(HashMap<String, Integer> loopCount) {
		//this.loopCount = loopCount;
	}

	public getIMethod getGetIMethod() {
		return getIMethod;
	}

	public void setIJavaProject(IJavaProject iJavaProject) {
		this.iJavaProject = iJavaProject;
	}

	/**
	 * 找到这个方法调用了哪些方法，和被调用的方法调用了哪些方法，获取call graph
	 * 
	 * @param methodsSet
	 * @param demoVisitor
	 * @return callees
	 * @throws JavaModelException
	 */
	@SuppressWarnings("restriction")
	// 此处传入的methodSet是calleeinit，保存了MethodDeclaration方法信息
	public HashSet<IMethod> getCalleesOf(HashSet<IMethod> methodsSet, DemoVisitor demoVisitor)
			throws JavaModelException {
		HashSet<IMethod> callees = new HashSet<IMethod>();
		// 设置methodVisited保存访问过的节点
		HashSet<IMethod> methodsVisited = new HashSet<IMethod>();
		CallHierarchy callHierarchy = CallHierarchy.getDefault();
		// 设置callHierarchy的范围
		callHierarchy.setSearchScope(demoVisitor.getSearchScope(iJavaProject));
		HashSet<IMethod> tempSet = new HashSet<IMethod>();

		count=0;
		
		while (!methodsSet.isEmpty()) {
			for (Iterator<IMethod> it = methodsSet.iterator(); it.hasNext();) {
				IMethod method = (IMethod) it.next();
				forFlag = demoVisitor.getForInSub(method);
				if(forFlag != null){
						int i=getKeyStringCount_2(method.getSource(), "for(");
						int countFix=++count+i;
						if(!loopCount.containsKey(method.getKey()))
							loopCount.put(method.getKey(), countFix);
						else if (loopCount.get(method.getKey())<count+i){
							loopCount.put(method.getKey(),countFix);
						}
					break;
				}
				// System.out.println("CalleeIMethodGetKey:
				// "+method.getKey());
			}
			tempSet.clear();
			//methodSet负责找完一层方法
			//while执行一次就是一层
			while (!methodsSet.isEmpty()) {
				// IMethod extends IMember
				// 取出methodSet第一个保存起来
				// members用来保存，firstiMethod用来遍历
				IMember[] members = { (IMember) methodsSet.toArray()[0] };
				IMethod firstiMethod = (IMethod) methodsSet.toArray()[0];
				// 如果methodsVisited包含firstiMethod
				if (methodsVisited.contains(firstiMethod)) {
					// 从methodsSet中去掉firstMethod
					methodsSet.remove(firstiMethod);
					//tempSet.remove(firstiMethod);
					continue;
				}
				//use default CallHierarchy object to get callee-roots of the input IMethod
				MethodWrapper[] methodWrappers = callHierarchy.getCalleeRoots(members);
				//for each method wrapper object is returned, invoke the getCalls method
				//getCalls in turn returns an array of MethodWrapper objects and these are the actual callees
				for (MethodWrapper method_wrapper : methodWrappers) {
					MethodWrapper[] methodwrapper_temp = method_wrapper.getCalls(new NullProgressMonitor());
					HashSet<IMethod> temp = getIMethod.getIMethods(methodwrapper_temp);
					callees.addAll(temp);
					tempSet.addAll(temp);
				}
				methodsVisited.add(firstiMethod);
				methodsSet.remove(firstiMethod);
				//tempSet.remove(firstiMethod);
			}
			//深拷贝，如果不用clone的话就只是赋值地址给前一个，复制是地址
			methodsSet=(HashSet<IMethod>)tempSet.clone();
		}
		return callees;
	}

	public HashMap<String, Integer> getHashMap() {
		return loopCount;
	}
	//从一个字符串中找出现字符串的个数
	public static int getKeyStringCount_2(String str, String key) {
		int count = 0; int index = 0;
		while((index = str.indexOf(key,index))!=-1){
		          index = index + key.length();
		count++; }
		return count; }

}