package bndtools.launch;

import static aQute.lib.exceptions.SupplierWithException.asSupplier;

import java.util.stream.Stream;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IOrdinaryClassFile;
import org.eclipse.jdt.core.IType;

public class OSGiJUnitLaunchPropertyTester extends PropertyTester {
	public static final String		PROP_CAN_LAUNCH_AS_OSGI_JUNIT	= "canLaunchAsOSGiJUnit";

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (PROP_CAN_LAUNCH_AS_OSGI_JUNIT.equals(property)) {
			IJavaElement javaElement = (IJavaElement) receiver;
			IJavaProject javaProject = javaElement.getJavaProject();

			return isOSGiJUnitTest(javaElement);
		}

		return false;
	}

	private boolean isOSGiJUnitTest(IJavaElement javaElement) {
		IMember testMember = null;
		if (javaElement instanceof ICompilationUnit) {
			testMember = (((ICompilationUnit) javaElement)).findPrimaryType();
		} else if (javaElement instanceof IOrdinaryClassFile) {
			testMember = (((IOrdinaryClassFile) javaElement)).getType();
		} else if (javaElement instanceof IType) {
			testMember = (IType) javaElement;
		} else if (javaElement instanceof IMethod) {
			testMember = (IMember) javaElement;
		}

		if (testMember != null && testMember.exists()) {
			try {
				if (testMember instanceof IType) {
					IType testType = (IType) testMember;

					return Stream.of(testType.getMethods())
						.flatMap(method -> Stream.of(asSupplier(() -> method.getAnnotations()).get()))
						.filter(annot -> "Test".equals(annot.getElementName()))
						.findFirst()
						.isPresent();
				}
			} catch (CoreException ce) {
			}
		}

		return false;
	}
	/*
	 * clean=true dynamicBundles=true
	 * launchTarget=com.liferay.code.upgrade.providers.tests/test.bndrun
	 * org.eclipse.jdt.junit.CONTAINER=
	 * org.eclipse.jdt.junit.TESTNAME=testWithNoWorkspacePath(java.nio.file.
	 * Path)
	 * org.eclipse.jdt.junit.TEST_UNIQUE_ID=[engine:bnd-bundle-engine]/[bundle:
	 * com.liferay.code.upgrade.providers.tests;1.0.0.202010271714]/[sub-engine:
	 * junit-jupiter]/[class:com.liferay.code.upgrade.providers.
	 * ConfigWorkspacePathProviderTest]/[method:testWithNoWorkspacePath(java.nio
	 * .file.Path)]
	 * org.eclipse.jdt.launching.MAIN_TYPE=com.liferay.code.upgrade.providers.
	 * ConfigWorkspacePathProviderTest
	 * org.eclipse.jdt.launching.PROJECT_ATTR=com.liferay.code.upgrade.providers
	 * .tests
	 */
}
