import java.beans.FeatureDescriptor;
import java.util.*;

/**
 * Class used to start the semantic analysis of the AST : The class keeps the
 * inheritance tree obtained at the first step, the symbol table and the methods
 * used for signaling the errors which ocurred durring the analysis
 * 
 * @author Martinas Mihaela-Alexandra
 * 
 */
public class AnalyseTypes {

	private ArrayList<NodeInhGraph> inheritanceTree;
	private Classes classes;
	private int errorCounter;
	private SymbolTable map;

	/**
	 * Constructor of the class which creates a new instance for the symbol
	 * table and initializes the error counter.
	 * 
	 * @param inheritanceTree
	 *            the graph containing the structure of classes
	 * @param classes
	 *            the classes that can be found in the program
	 */
	public AnalyseTypes(ArrayList<NodeInhGraph> inheritanceTree, Classes classes) {
		this.inheritanceTree = inheritanceTree;
		this.classes = classes;
		errorCounter = 0;
		map = new SymbolTable();
	}

	/**
	 * Method used to add into the global scope the basic classes of the
	 * language
	 * 
	 * @param basicClass
	 *            an object which contains all the information about the classes
	 */
	private void saveBasicClass(class_ basicClass) {

		Enumeration enumFeatures = basicClass.features.getElements();
		TreeNode ft;
		map.addId(basicClass.name, basicClass);

		/* save attributes and methods */
		while (enumFeatures.hasMoreElements()) {
			ft = (TreeNode) enumFeatures.nextElement();

			if (ft instanceof method) {
				map.addId(((method) ft).name, (method) ft);
			}
		}
	}

	/**
	 * The methods have global visibility, from that reason they should be added
	 * into the global scope.
	 * 
	 */
	private void addMethodsGlobalScope() {
		Enumeration<class_> classEnum = classes.getElements();
		class_ classItem;
		ArrayList<String> methodName = new ArrayList<String>();

		while (classEnum.hasMoreElements()) {

			classItem = classEnum.nextElement();
			Features classFt = classItem.features;
			Feature featureItem;

			/*
			 * itereate through features and extract names of methods to add
			 * them into the global scope
			 */
			Enumeration<Feature> featureEnum = classFt.getElements();

			while (featureEnum.hasMoreElements()) {
				featureItem = featureEnum.nextElement();

				if (featureItem instanceof method) {
					map.addId(((method) featureItem).name, featureItem);
				}
			}
		}
	}

	/**
	 * Main class should be analysed separate because it has access to all the
	 * other classes (not only to those with which is linked directly through
	 * inheritance). The attributes of the parent classes are made visible for
	 * Main class (added to it's scope) because attributes are visible only in
	 * the local scope.
	 * 
	 * @param startNode
	 *            the root node from which the graph will be manipulated
	 */
	private void mainClassTypes(NodeInhGraph startNode) {

		NodeInhGraph classElem;
		for (int i = 0; i < startNode.children.size(); i++) {
			map.enterScope();

			classElem = startNode.children.get(i);

			if (!(classElem.className).toLowerCase().equals("main")) {
				/*
				 * add attributes to the scope if the main class wasn't found
				 * yet
				 */
				class_ classItem = classElem.classObj;
				Enumeration<Feature> ftEnum = classItem.features.getElements();
				Feature elemFt;

				while (ftEnum.hasMoreElements()) {
					elemFt = ftEnum.nextElement();

					if (elemFt instanceof attr) {
						map.addId(((attr) elemFt).name, (attr) elemFt);
					}
				}
				/* continue analysing the graph */
				mainClassTypes(startNode.children.get(i));

			} else {
				/* if main class was found it's types can be assigned */
				classElem.assignTypes(inheritanceTree.get(0), map);
				errorCounter += (classElem.errors()) ? 1 : 0;

				/*
				 * there is no use to continue analysing the graph so we should
				 * return
				 */
				return;
			}
			map.exitScope();
		}

	}

	/**
	 * Method which initiates the analyse of types. Adds the basic classes of
	 * the language into the global scope, adds the methods of the classes into
	 * the global scope and start the effective analysis.
	 * 
	 * @param Object_class
	 *            object containig info about the class Object of the language
	 * @param Int_class
	 *            object containig info about the class Int of the language
	 * @param Bool_class
	 *            object containig info about the class Bool of the language
	 * @param String_class
	 *            object containig info about the class String of the language
	 * @param IO_class
	 *            object containig info about the class IO of the language
	 */
	public void assignTypesClasses(class_ Object_class, class_ Int_class,
			class_ Bool_class, class_ String_class, class_ IO_class) {

		/* enter the global scope */
		map.enterScope();

		/* save basic classes into this scope */
		saveBasicClass(Object_class);
		saveBasicClass(Int_class);
		saveBasicClass(Bool_class);
		saveBasicClass(String_class);
		saveBasicClass(IO_class);

		/* add method names to the global scope */
		addMethodsGlobalScope();

		/* iterate through the classes */
		iterateThroughClasses(inheritanceTree.get(0));

		/* the Main class is the last that should be analysed */
		mainClassTypes(inheritanceTree.get(0));

		/* exit global scope */
		map.exitScope();
	}

	/**
	 * Iterate through the classes of the inheritance tree, assigning types to
	 * it's members
	 * 
	 * @param startNode
	 *            graph's node from which the analyse will start
	 */
	public void iterateThroughClasses(NodeInhGraph startNode) {

		NodeInhGraph classElem;

		for (int i = 0; i < startNode.children.size(); i++) {

			/*
			 * enter class scope - depth first analysis so that attributes can
			 * be visible along the inheritance chain
			 */
			map.enterScope();
			classElem = startNode.children.get(i);

			/*
			 * assign types for all the classes except the main class which will
			 * be analysed last
			 */
			if (!(classElem.className).toLowerCase().equals("main")) {
				classElem.assignTypes(inheritanceTree.get(0), map);
				errorCounter += (classElem.errors()) ? 1 : 0;
			}

			/* continue itterating through classes */
			iterateThroughClasses(classElem);
			/* exit branch scope */
			map.exitScope();
		}

	}

	/**
	 * Method which verifies if there were any errors while types were assigned
	 * 
	 * @return true if there were any errors or false otherwise
	 */
	public boolean errors() {
		return errorCounter != 0;
	}

}
