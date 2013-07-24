import java.io.PrintStream;
import java.util.Enumeration;
import java.util.*;

/**
 * This class may be used to contain the semantic information such as the
 * inheritance graph. You may use it or not as you like: it is only here to
 * provide a container for the supplied methods.
 */
class ClassTable {
	private int semantErrors;
	private PrintStream errorStream;
	private Classes inputClasses;
	private ArrayList<NodeInhGraph> inheritanceGraphs;
	private class_ Object_class, IO_class, Int_class, Bool_class, Str_class;

	/**
	 * Creates data structures representing basic Cool classes (Object, IO, Int,
	 * Bool, String). Please note: as is this method does not do anything
	 * useful; you will need to edit it to make if do what you want.
	 * */
	private void installBasicClasses() {
		AbstractSymbol filename = AbstractTable.stringtable
				.addString("<basic class>");

		// The following demonstrates how to create dummy parse trees to
		// refer to basic Cool classes. There's no need for method
		// bodies -- these are already built into the runtime system.

		// IMPORTANT: The results of the following expressions are
		// stored in local variables. You will want to do something
		// with those variables at the end of this method to make this
		// code meaningful.

		// The Object class has no parent class. Its methods are
		// cool_abort() : Object aborts the program
		// type_name() : Str returns a string representation
		// of class name
		// copy() : SELF_TYPE returns a copy of the object

		Object_class = new class_(
				0,
				TreeConstants.Object_,
				TreeConstants.No_class,
				new Features(0)
						.appendElement(
								new method(0, TreeConstants.cool_abort,
										new Formals(0), TreeConstants.Object_,
										new no_expr(0)))
						.appendElement(
								new method(0, TreeConstants.type_name,
										new Formals(0), TreeConstants.Str,
										new no_expr(0)))
						.appendElement(
								new method(0, TreeConstants.copy,
										new Formals(0),
										TreeConstants.SELF_TYPE, new no_expr(0))),
				filename);

		// The IO class inherits from Object. Its methods are
		// out_string(Str) : SELF_TYPE writes a string to the output
		// out_int(Int) : SELF_TYPE "    an int    " "     "
		// in_string() : Str reads a string from the input
		// in_int() : Int "   an int     " "     "

		IO_class = new class_(
				0,
				TreeConstants.IO,
				TreeConstants.Object_,
				new Features(0)
						.appendElement(
								new method(0, TreeConstants.out_string,
										new Formals(0)
												.appendElement(new formal(0,
														TreeConstants.arg,
														TreeConstants.Str)),
										TreeConstants.SELF_TYPE, new no_expr(0)))
						.appendElement(
								new method(0, TreeConstants.out_int,
										new Formals(0)
												.appendElement(new formal(0,
														TreeConstants.arg,
														TreeConstants.Int)),
										TreeConstants.SELF_TYPE, new no_expr(0)))
						.appendElement(
								new method(0, TreeConstants.in_string,
										new Formals(0), TreeConstants.Str,
										new no_expr(0)))
						.appendElement(
								new method(0, TreeConstants.in_int,
										new Formals(0), TreeConstants.Int,
										new no_expr(0))), filename);

		// The Int class has no methods and only a single attribute, the
		// "val" for the integer.

		Int_class = new class_(0, TreeConstants.Int, TreeConstants.Object_,
				new Features(0).appendElement(new attr(0, TreeConstants.val,
						TreeConstants.prim_slot, new no_expr(0))), filename);

		// Bool also has only the "val" slot.
		Bool_class = new class_(0, TreeConstants.Bool, TreeConstants.Object_,
				new Features(0).appendElement(new attr(0, TreeConstants.val,
						TreeConstants.prim_slot, new no_expr(0))), filename);

		// The class Str has a number of slots and operations:
		// val the length of the string
		// str_field the string itself
		// length() : Int returns length of the string
		// concat(arg: Str) : Str performs string concatenation
		// substr(arg: Int, arg2: Int): Str substring selection

		Str_class = new class_(
				0,
				TreeConstants.Str,
				TreeConstants.Object_,
				new Features(0)
						.appendElement(
								new attr(0, TreeConstants.val,
										TreeConstants.Int, new no_expr(0)))
						.appendElement(
								new attr(0, TreeConstants.str_field,
										TreeConstants.prim_slot, new no_expr(0)))
						.appendElement(
								new method(0, TreeConstants.length,
										new Formals(0), TreeConstants.Int,
										new no_expr(0)))
						.appendElement(
								new method(0, TreeConstants.concat,
										new Formals(0)
												.appendElement(new formal(0,
														TreeConstants.arg,
														TreeConstants.Str)),
										TreeConstants.Str, new no_expr(0)))
						.appendElement(
								new method(
										0,
										TreeConstants.substr,
										new Formals(0)
												.appendElement(
														new formal(
																0,
																TreeConstants.arg,
																TreeConstants.Int))
												.appendElement(
														new formal(
																0,
																TreeConstants.arg2,
																TreeConstants.Int)),
										TreeConstants.Str, new no_expr(0))),
				filename);

		/*
		 * Do something with Object_class, IO_class, Int_class, Bool_class, and
		 * Str_class here
		 */

	}

	public ClassTable(Classes cls) {
		semantErrors = 0;
		errorStream = System.err;
		inputClasses = cls;

		inheritanceGraphs = new ArrayList<NodeInhGraph>();
		NodeInhGraph graphRoot = new NodeInhGraph("Object");

		installBasicClasses();
		graphRoot.setClassObj(Object_class);
		inheritanceGraphs.add(graphRoot);

		/* add basic classes to inheritance tree */
		addBasicClasses();

	}

	/**
	 * Method used for adding the basic classes to the inheritance tree. The
	 * root class of the inheritance tree is considered to be the Object class.
	 */
	private void addBasicClasses() {
		NodeInhGraph intNode = new NodeInhGraph("Int");
		intNode.classObj = Int_class;

		NodeInhGraph boolNode = new NodeInhGraph("Bool");
		boolNode.classObj = Bool_class;

		NodeInhGraph ioNode = new NodeInhGraph("IO");
		ioNode.classObj = IO_class;

		NodeInhGraph stringNode = new NodeInhGraph("String");
		stringNode.classObj = Str_class;

		inheritanceGraphs.get(0).children.add(stringNode);
		inheritanceGraphs.get(0).children.add(ioNode);
		inheritanceGraphs.get(0).children.add(boolNode);
		inheritanceGraphs.get(0).children.add(intNode);

	}

	/**
	 * Getter used to return the inheritance tree - is not safe to modify it
	 * 
	 * @return the inheritance graph
	 */
	public ArrayList<NodeInhGraph> getInheritanceGraphs() {
		return inheritanceGraphs;
	}

	/**
	 * @return the object_class
	 */
	public class_ getObject_class() {
		return Object_class;
	}

	public class_ getIO_class() {
		return IO_class;
	}

	public class_ getInt_class() {
		return Int_class;
	}

	public class_ getBool_class() {
		return Bool_class;
	}

	public class_ getStr_class() {
		return Str_class;
	}

	/**
	 * @param object_class
	 *            the object_class to set
	 */
	public void setObject_class(class_ object_class) {
		Object_class = object_class;
	}

	/**
	 * Method use for printing specific messages to complete the errors
	 * 
	 * @param msg
	 *            the message that we want to print
	 */
	private void printSpecificMessage(String msg) {
		errorStream.print(msg);
		errorStream.println();
	}

	/**
	 * Method
	 */
	public void setClassObjects() {

		Enumeration<class_> enumClass = inputClasses.getElements();
		class_ cls;
		NodeInhGraph nodeObj;

		while (enumClass.hasMoreElements()) {
			cls = (class_) (enumClass.nextElement());
			nodeObj = searchValue(cls.name.toString(), inheritanceGraphs.get(0));

			if (nodeObj == null) {
				semantError(cls);
				printSpecificMessage("Clasa nu este definita");
				return;
			}
			nodeObj.setClassObj(cls);
		}

	}

	/**
	 * Verify if the structure of the COOL program is correct
	 */
	public void verifyStructure() {
		Enumeration classList = inputClasses.getElements();
		class_ classObj;

		while (classList.hasMoreElements()) {
			classObj = (class_) (classList.nextElement());
			/* construct the inheritance tree */
			addDataInInheritanceTree(classObj);

			/*
			 * verify the structure of the main class - Main class with main
			 * method
			 */
			checkMain(classObj);

			/* verify if basic final classes arent't redefined */
			checkProperName(classObj);

			/*
			 * verify if String, Int or Bool classes aren't parents for other
			 * classes
			 */
			checkParentClass(classObj);
		}

		setClassObjects();
	}

	/**
	 * Verify if class Main contains a method named main and if a main method
	 * isn't found in other class than Main
	 * 
	 * @param classObj
	 *            the analysed class
	 */
	private void checkMain(class_ classObj) {

		Features ft = classObj.features;
		Enumeration enumFt = ft.getElements();
		TreeNode meth;
		boolean foundMain = false;

		while (enumFt.hasMoreElements()) {
			meth = (TreeNode) (enumFt.nextElement());

			if (meth instanceof method) {
				if (((method) meth).name.toString().toLowerCase()
						.equals("main")) {
					foundMain = true;

					if (!classObj.name.toString().toLowerCase().equals("main")) {
						semantError(classObj);
						printSpecificMessage("Metoda main nu se gaseste in clasa Main");
						return;
					} else
						return;
				}
			}
		}

		if (classObj.name.toString().toLowerCase().equals("main") && !foundMain) {
			semantError(classObj);
			printSpecificMessage("Clasa Main nu contine metoda main");
		}

	}

	/**
	 * 
	 * @param classToCheck
	 *            the class for which we verify if it doesn't redefine a final
	 *            class like: String, Int or Bool
	 */
	public void checkProperName(class_ classToCheck) {
		String className = classToCheck.name.getString();

		if (className.equals("String") || className.equals("Int")
				|| className.equals("Bool") || className.equals("IO")
				|| className.equals("Object")) {
			semantError(classToCheck);
			printSpecificMessage(" Nu se poate redefini o clasa de baza: "
					+ className);
		}
	}

	/**
	 * 
	 * @param classToCheck
	 *            the class for which the method verifies if the parent of the
	 *            class isn't a final class of the language COOL
	 */

	public void checkParentClass(class_ classToCheck) {
		String parentClass = classToCheck.parent.getString();

		if (parentClass.equals("String") || parentClass.equals("Int")
				|| parentClass.equals("Bool")) {
			semantError(classToCheck);
			printSpecificMessage("Nu se poate extinde clasa finala "
					+ parentClass);
		}
	}

	/**
	 * Method used for searching a class with a specific name into the
	 * inheritance tree
	 * 
	 * @param className
	 *            the name of the class the class to be searched
	 * @param startPoint
	 *            the starting point for the search
	 * 
	 * @return the node containing the info about the searched class
	 */
	public NodeInhGraph searchValue(String className, NodeInhGraph startPoint) {

		if (startPoint.className.equals(className))
			return startPoint;

		NodeInhGraph retVal = null, prevVal = null;

		for (int i = 0; i < startPoint.children.size(); i++) {
			retVal = searchValue(className, startPoint.children.get(i));
			if (retVal != null && prevVal == null)
				prevVal = retVal;
		}

		return prevVal;
	}

	/**
	 * Verify if a class with a given name is child of other class
	 * 
	 * @param className
	 *            the name of the class for which we want to verify if it is a
	 *            child class
	 * @param startPoint
	 *            the point in the graph from which we want to start the search
	 * 
	 * @return true if the class is a child of the specified class or false
	 *         otherwise
	 */
	public boolean isChild(String className, NodeInhGraph startPoint) {
		if (startPoint.className.equals(className))
			return true;

		boolean retVal = false, prevVal = false;
		for (int i = 0; i < startPoint.children.size(); i++) {
			retVal = isChild(className, startPoint.children.get(i));
			if (retVal && !prevVal)
				prevVal = retVal;
		}
		return prevVal;
	}

	/**
	 * Delete a node specified by it's name from the inheritance graph
	 * 
	 * @param nodeName
	 *            the name of the node we want to delete
	 * @param startNode
	 *            the starting point for searching the required node
	 */
	public void deleteNode(String nodeName, NodeInhGraph startNode) {

		for (int i = 0; i < startNode.children.size(); i++) {
			if (startNode.children.get(i).className.equals(nodeName)) {
				startNode.children.remove(i);
				return;
			} else
				deleteNode(nodeName, startNode.children.get(i));
		}

		return;
	}

	/**
	 * Verifies if the parent node can be found between it's own children
	 * 
	 * @param nodeName
	 *            the name of the node we want to search for
	 * @return the index of the node in the array of children or -1 if it wasn't
	 *         found
	 */
	public int isObjectParent(String nodeName) {

		ArrayList<NodeInhGraph> chldObj = inheritanceGraphs.get(0).children;
		int sizeChldObj = chldObj.size();

		for (int i = 0; i < sizeChldObj; i++)
			if (chldObj.get(i).className.equals(nodeName))
				return i;
		return -1;
	}

	/**
	 * Method used to set the type of a specific node
	 * 
	 * @param parent
	 *            the object containg the node
	 */
	private void setTypeParentNode(NodeInhGraph parent) {

		if(parent.className.equals("String")){
			parent.setClassObj(Str_class);
			return;
		}
		if(parent.className.equals("Int")){
			parent.setClassObj(Int_class);
			return;
		}
		if(parent.className.equals("IO")){
			parent.setClassObj(IO_class);
			return;
		}
		if(parent.className.equals("Bool")){
			parent.setClassObj(Bool_class);
			return;
		}
		if(parent.className.equals("Object")){
			parent.setClassObj(Object_class);
			return;
		}

	}

	/**
	 * The method which effectively constructs the inheritance tree: unifies the
	 * received values or simply adds up new nodes to the graph
	 * 
	 * @param classObj
	 *            the class node that we want to add
	 */
	private void addDataInInheritanceTree(class_ classObj) {

		String className = classObj.name.toString();
		String parent = classObj.parent.toString();
		NodeInhGraph parentNode, childNode;

		/* search parent class and child class in graph */
		parentNode = searchValue(parent, inheritanceGraphs.get(0));
		childNode = searchValue(className, inheritanceGraphs.get(0));

		/* if the parent can be found in the inheritance graph */
		if (parentNode != null) {
			/* the child exists in the inheritance graph */
			if (childNode == null) {
				/* the child node should be added to the graph */
				childNode = new NodeInhGraph(className);
				parentNode.children.add(childNode);
			} else {
				/*
				 * verify if the class is redefined or it is a recurrent
				 * inheritance
				 */
				if (isChild(className, parentNode)) {
					semantError(classObj);
					printSpecificMessage(" Nu se poate redefini o clasa. ");
				} else {
					if (isChild(parent, childNode)) {
						semantError(classObj);
						printSpecificMessage(" Nu este permisa mostenirea recursiva.");
					} else {
						/*
						 * delete the previous appearance of the node in the
						 * graph
						 */
						deleteNode(className, inheritanceGraphs.get(0));
						parentNode.children.add(childNode);
					}
				}
			}
		} else {
			if (childNode != null) {
				/*
				 * if childNode is not a child of class Object , this relation
				 * is illegal because multiple inheritance is not allowed in
				 * COOL language
				 */
				int indexObjChld = isObjectParent(className);
				if (indexObjChld == -1) {
					semantError(classObj);
					printSpecificMessage(" Mostenirea multipla nu este permisa ");
				} else {
					parentNode = new NodeInhGraph(parent);
					parentNode.children.add(childNode);
					setTypeParentNode(parentNode);
					/* the parent node becomes the new root of the graph */
					inheritanceGraphs.get(0).children.set(indexObjChld,
							parentNode);
				}
			} else {
				/* a new entrance in the inheritance graph must be created */
				parentNode = new NodeInhGraph(parent);
				childNode = new NodeInhGraph(className);
				parentNode.children.add(childNode);
				setTypeParentNode(parentNode);
				inheritanceGraphs.get(0).children.add(parentNode);
			}

		}
	}

	/**
	 * Method used for printing the inheritance graph (used for debugging)
	 * 
	 * @param startNode
	 *            the start point from which we want to see the inheritance
	 *            graph
	 */
	public void printInheritanceTree(NodeInhGraph startNode) {

		for (int i = 0; i < startNode.children.size(); i++) {
			System.out.print(startNode.children.get(i).className + "\t");

		}
		System.out.println();
		for (int i = 0; i < startNode.children.size(); i++) {
			printInheritanceTree(startNode.children.get(i));
		}
	}

	/**
	 * Prints line number and file name of the given class.
	 * 
	 * Also increments semantic error count.
	 * 
	 * @param c
	 *            the class
	 * @return a print stream to which the rest of the error message is to be
	 *         printed.
	 * 
	 * */
	public PrintStream semantError(class_ c) {
		return semantError(c.filename, c);
	}

	/**
	 * Prints the file name and the line number of the given tree node.
	 * 
	 * Also increments semantic error count.
	 * 
	 * @param filename
	 *            the file name
	 * @param t
	 *            the tree node
	 * @return a print stream to which the rest of the error message is to be
	 *         printed.
	 * 
	 * 
	 * */
	public PrintStream semantError(AbstractSymbol filename, TreeNode t) {
		errorStream.print("Linia " + t.getLineNumber() + ": ");
		return semantError();
	}

	/**
	 * Increments semantic error count and returns the print stream for error
	 * messages.
	 * 
	 * @return a print stream to which the error message is to be printed.
	 * 
	 * */
	public PrintStream semantError() {
		semantErrors++;
		return errorStream;
	}

	/** Returns true if there are any static semantic errors. */
	public boolean errors() {
		return semantErrors != 0;
	}
}
