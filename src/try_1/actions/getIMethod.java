package try_1.actions;


import java.util.HashSet;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.internal.corext.callhierarchy.MethodWrapper;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IJavaElement;

public class getIMethod {
		private HashSet<IMethod> callmethodNull = new HashSet<IMethod>();
	public HashSet<IMethod> getIMethods(MethodWrapper[] methodWrappers) {
		HashSet<IMethod> callmethod = new HashSet<IMethod>();
		for (MethodWrapper method_wrapper : methodWrappers) {
			IMethod im = getIMethodFromMethodWrapper(method_wrapper);
			if (im != null) {
				callmethod.add(im);
			}else {
				callmethodNull.add(im);
			}
		
		}
		return callmethod;
	}
	public HashSet<IMethod> getCallMethodNull() {
		return callmethodNull;
	}
	/**	
	 * @param MethodWrapper
	 * @return null
	 */
	public IMethod getIMethodFromMethodWrapper(MethodWrapper method_wrapper) {
		try {
			IMember im = method_wrapper.getMember();
			if (im.getElementType() == IJavaElement.METHOD) {
				return (IMethod) method_wrapper.getMember();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}