package try_1.actions;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class TryThis extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		//start plug-in
		System.out.println("Plug in start");
		//find all projects in workspace
		IWorkspace workspace=ResourcesPlugin.getWorkspace();
		IWorkspaceRoot iWorkspaceRoot=workspace.getRoot();
		IProject[] iProjects=iWorkspaceRoot.getProjects();
		for(IProject iProject:iProjects){
			try {
				if (!iProject.getName().equals("A01")) {
					//if this project is open; 
					//isNatureEnabled() returns T if project is open, F if closed or not exist 
					if (iProject.isOpen()&&iProject.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
						process(iProject);
						print();
					}	
				}
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("CoreException");
			}
		}
		
		return null;
	}

	private void process(IProject iProject) throws JavaModelException{
		// TODO Auto-generated method stub
		//create() Returns the Java project corresponding to the given project.
		IJavaProject iJavaProject=JavaCore.create(iProject);
		IPackageFragment[] iPackageFragments=iJavaProject.getPackageFragments();
		for(IPackageFragment iPackageFragment:iPackageFragments){
			//To insure we only execute source code
			if (iPackageFragment.getKind()==IPackageFragmentRoot.K_SOURCE) {
				//CompilationUnit means Java File
				for(ICompilationUnit unit:iPackageFragment.getCompilationUnits()){
					//if(unit.getElementName().equals("A01.java"))
					//{
					JdtAST jdtAST=new JdtAST();
					CompilationUnit cUnit=jdtAST.getCompilationUnit(unit);
					DemoVisitor visitor=new DemoVisitor(iJavaProject);
					cUnit.accept(visitor);
					//visitor.print();
					}
				//}
			}
		}
	}
	public void print(){
		Iterator entries = CalleeMethods.loopCount.entrySet().iterator();
		while(entries.hasNext()) {
			Map.Entry entry = (Map.Entry)entries.next();
			//int layers=cMethods.getHashMap().get(method.getKey());
			
			System.out.println("Layer= "+entry.getValue()+",");
			System.out.println("methodKey: "+entry.getKey());
			//System.out.println("get: "+cMethods.getHashMap().get(entry.getKey()));
		}
	}
	
}