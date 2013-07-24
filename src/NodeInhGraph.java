import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Node in the inheritance graph which describes a class: it contains it's name,
 * the children of the class, the root of the inheritance graph , the object which
 * contains all the information about the class and variable used for printing 
 * and counting the errors which occur.
 * 
 * @author Martinas Mihaela-Alexandra
 *
 */
public class NodeInhGraph {

	String className;
	ArrayList<NodeInhGraph> children;
	class_ classObj;
	NodeInhGraph startNodeInhGraph;
	private int semantErrors;
	private PrintStream errorStream;

	/**
	 * The constructor of the class which receives the name of the class
	 *  
	 * @param className name of the class that this node will represent
	 */
	
	public NodeInhGraph(String className) {
		this.className = className;
		children = new ArrayList<NodeInhGraph>();
		errorStream = System.err;
	}

	/**
	 * Setter for a class  containg all the information about the Object class
	 * 
	 * @param classObj Object class
	 */
	public void setClassObj(class_ classObj) {
		this.classObj = classObj;
	}

	/**
	 * Lookup in the idtable a value
	 * 
	 * @param typeName the identifier we want to find
	 * 
	 * @return an AbstractSymbol containing the info about identifier
	 */
	public AbstractSymbol getTypeObject(String typeName) {
		AbstractSymbol type;
		type = AbstractTable.idtable.lookup(typeName);
		if (type == null) {
			type = AbstractTable.idtable.addString(typeName);
		}

		return type;
	}

	/**
	 * Method used for setting the type of an arithmetical expression. Analyses 
	 * the types of the operands and finally assigns a type to the opearator,
	 * (plus, sub, mul, divide)
	 * 
	 * @param op the operator
	 * @param e1 first operand
	 * @param e2 second operand
	 * @param operationType a String which contains the name of the operation(used for error printing)
	 * @param finalType the type that should have the expression 
	 * @param map an object containing the scopes until now
	 */
	private void setTypesArithComparisonOp(Expression op, Expression e1,
			Expression e2, String operationType, String finalType,
			SymbolTable map) {
		/*assign types to the operands*/
		assignExprTypes(e1, map);
		assignExprTypes(e2, map);

		AbstractSymbol typeExpr1 = e1.get_type();
		AbstractSymbol typeExpr2 = e2.get_type();

		/*verify if the obtained types are correct*/
		if (typeExpr1 != null && !typeExpr1.toString().equals("Int")) {
			semantError(op, "Tipul primei expresii " + typeExpr1.toString()
					+ " nu este valid pentru " + operationType + ". Se accepta doar Int-uri");
			return;
		}

		if (typeExpr2 != null && !typeExpr2.toString().equals("Int")) {
			semantError(op,
					"Tipul celei de-a doua expresii " + typeExpr2.toString()
							+ " nu este valid pentru " + operationType + " Se accepta doar Int-uri");
			return;
		}

		/*set type of the operator*/
		op.set_type(getTypeObject(finalType));

	}

	/**
	 * When adding a value in the symbol table the key is an Abstract Symbol with the name
	 * of the identifier and the value is an object containing the expression of the identifier.
	 * The value retrieved from the symbol table can have different class and from this reason the
	 * result of the looking up must be analysed and the desired information should be returned.  
	 * 
	 * @param result the result of lookup in the symbol table
	 * 
	 * @return an AbstractSymbol which contains the type information of the looked up identifiesr
	 */
	private AbstractSymbol analyseLookupResult(Object result) {
		AbstractSymbol lookupRes = null;

		if (result instanceof attr) {
			lookupRes = ((attr) result).type_decl;
		}
		if (result instanceof formal) {
			lookupRes = ((formal) result).type_decl;
		}

		if (result instanceof int_const) {
			lookupRes = ((int_const) result).get_type();
		}
		if (result instanceof bool_const) {
			lookupRes = ((bool_const) result).get_type();
		}
		if (result instanceof string_const) {
			lookupRes = ((string_const) result).get_type();
		}

		if (result instanceof new_) {
			lookupRes = ((new_) result).get_type();
		}
		if (result instanceof String) {
			lookupRes = getTypeObject((String) result);
		}
		if (result instanceof dispatch) {
			lookupRes = ((dispatch) result).get_type();
		}
		if (result instanceof object) {
			lookupRes = ((object) result).get_type();
		}
		if (result instanceof loop) {
			lookupRes = ((loop) result).get_type();
		}
		if (result instanceof let) {
			lookupRes = ((let) result).get_type();
		}
		if (result instanceof dispatch) {
			lookupRes = ((dispatch) result).get_type();
		}
		if (result instanceof formal) {
			lookupRes = ((formal) result).type_decl;
		}
		
		if(result instanceof branch) {
			lookupRes = ((branch)result).type_decl;
		}

		if(result instanceof plus) {
			lookupRes = ((plus)result).get_type();
		}
		
		if(result instanceof sub){
			lookupRes = ((sub)result).get_type();
		}
		
		if(result instanceof mul){
			lookupRes = ((mul)result).get_type();
		}
		
		if(result instanceof divide) {
			lookupRes = ((divide)result).get_type();
		}
		return lookupRes;
	}

	/**
	 * Sets type of an object
	 * 
	 * @param expr expression which has type object 
	 * @param map the current scopes
	 */
	private void setObjectType(Expression expr, SymbolTable map) {

		/* if object name is "self" than the type is SELF_TYPE */
		if (((object) expr).name.getString().equals("self")) {
			expr.set_type(getTypeObject("SELF_TYPE"));
			return;
		}

		/* verify if the object is in the current scope */
		AbstractSymbol symExpr = getTypeObject(((object) expr).name.getString());
		if (symExpr == null) {
			semantError(expr,
					"Variabile nedeclarata " + ((object) expr).name.getString());
		}

		/*lookup in the current scopes the object*/
		Object result = map.lookup(((object) expr).name);

		AbstractSymbol lookupRes = analyseLookupResult(result);

		if (lookupRes != null) {
			expr.set_type(lookupRes);
			return;
		}

		semantError(expr,
				"Variabila nedeclarata " + ((object) expr).name.getString());
		return;
	}

	/**
	 * Sets type of a condition : the types of then and else branches are determined
	 * first and after that  the least type between the two of them becomes the type
	 * of the condition
	 * 
	 * @param expr the condition to which a type should be assigned 
	 * @param map the scopes of the identifiers until now 
	 */
	private void setIfType(cond expr, SymbolTable map) {

		Expression predicate = expr.pred;
		Expression thenExpr = expr.then_exp;
		Expression elseExpr = expr.else_exp;
		String predType, thenType, elseType;

		assignExprTypes(predicate, map);

		/* if predicate type is not boolean there is an error */
		predType = predicate.get_type().toString();

		if (!predType.equals("Bool")) {
			semantError(expr,
					"Conditia if-ului trebuie sa fie o expresie de tip boolean.");
			return;
		}

		/* find the types of if's branches */
		assignExprTypes(thenExpr, map);
		assignExprTypes(elseExpr, map);

		thenType = thenExpr.get_type().toString();
		elseType = elseExpr.get_type().toString();
		
		if (thenType.equals(elseType)) {
			expr.set_type(getTypeObject(thenType));
			return;
		}
		
		/*if branches have self_type their type becomes the type if the class*/
		if(thenType.equals("SELF_TYPE"))
			thenType = className;
		
		
		if(elseType.equals("SELF_TYPE"))
			elseType = className;

		/* find the least type between the types of the branches*/
		NodeInhGraph nodeThen = findNode(thenType, startNodeInhGraph);
		NodeInhGraph nodeElse = findNode(elseType, startNodeInhGraph);
		
		/*the least type is the type of Then if the type of else is a child
		 * of then type */
		if(isChild(elseType, nodeThen)) {
			expr.set_type(getTypeObject(thenType));
			return;
		}
		
		/*the least type is the type of else branch  if the type of then 
		 * branch is a child of the type of else branch 
		 * */
		if(isChild(thenType , nodeElse)) {
			expr.set_type(getTypeObject(elseType));
			return;
		}
		
		/*if the two types don't have anything in common Object becomes the type of "if" statement*/
		expr.set_type(getTypeObject("Object"));
		
	}

	/**
	 * Verifies if any errors occured in the equality statement: if the type of one
	 * of the expressions is Int, Bool or String than the other must have the same type
	 * 
	 * @param expr the equal expression
	 * @param typeExpr1 the type of the first operand
	 * @param typeExpr2 the type of the second operand
	 * @param type the type that the two operands should have had.
	 * @return
	 */
	private boolean errorInEqual(eq expr, String typeExpr1, String typeExpr2,
			String type) {
		if ((typeExpr1.equals(type) && !typeExpr2.equals(type))
				|| (typeExpr2.equals(type) && !typeExpr1.equals(type))) {
			semantError(expr, new String(
					"Ambele expresii trebuie sa fie de tip " + type + "."));
			return false;
		}

		return true;
	}

	/**
	 * Set type for equality statement for a received expression: beside Int,Bool and String
	 * classes which can be compared only with elements which have the same type, all
	 * the other classes can be compared with any class.
	 * 
	 * @param expr the equality expression
	 * @param map the scopes of the variables until now 
	 */
	private void setEqualType(eq expr, SymbolTable map) {
		Expression expr1 = expr.e1;
		Expression expr2 = expr.e2;

		/* get types for each of the expressions analysed by equal */
		assignExprTypes(expr1, map);
		assignExprTypes(expr2, map);

		/*
		 * if one of the expressions has type Int, Bool or String the other must
		 * have the same type
		 */
		String typeExpr1 = expr1.get_type().toString();
		String typeExpr2 = expr2.get_type().toString();

		/* check for errors */
		if (!errorInEqual(expr, typeExpr1, typeExpr2, "Int")
				|| !errorInEqual(expr, typeExpr1, typeExpr2, "Bool")
				|| !errorInEqual(expr, typeExpr1, typeExpr2, "String"))
			return;

		/* set type for equal - Bool */
		expr.set_type(getTypeObject("Bool"));
	}

	/**
	 * Set loop type: the predicate must have type bool, and the other expressions 
	 * are analysed.
	 * 
	 * @param expr the loop expression to be analysed
	 * @param map the identifiers scopes until now
	 */
	private void setLoopType(loop expr, SymbolTable map) {
		Expression pred = expr.pred;
		Expression block = expr.body;

		/* set predicate type and the type of the used expression */
		assignExprTypes(pred, map);
		
		/* the predicate must have bool type */
		if (!pred.get_type().toString().equals("Bool")) {
			semantError(expr, "Predicatul nu are tip boolean.");
			return;
		}
		
		assignExprTypes(block, map);
		
		/* because there are no errors is set the type of loop - Object */
		expr.set_type(getTypeObject("Object"));

	}

	/**
	 * Method used for joining the types - used for case statement.
	 * The least type between all the types must be chosen.
	 * 
	 * @param typesToJoin an array with the types that should be joined
	 * 
	 * @return the name of the resulting class after the join
	 */
	private String joinTypes( ArrayList<String> typesToJoin) {
		
		ArrayList<NodeInhGraph> typesToJoinObj = new ArrayList<NodeInhGraph>();
		boolean isParent = true;
		int parentNode = -1;
		
		/*create an array which contains Nodes of the inheritance tree for all the classes that should be joined*/
		for(int i = 0 ; i < typesToJoin.size() ; i++) {
			typesToJoinObj.add(findNode(typesToJoin.get(i), startNodeInhGraph));
		}
		
		/*verify if there is type which is parent for all the others*/
		for(int i = 0 ; i < typesToJoinObj.size() ; i++){
			isParent = true;
			for(int j = 0 ; j < typesToJoinObj.size() ; j++) {
				if(i != j) {
					if(!isChild(typesToJoinObj.get(j).className, typesToJoinObj.get(i))) {
						isParent = false;
						break;
					}
				}
			}
			/*a parent for all the nodes was found*/
			if(isParent){
				parentNode = i;
				break;
			}
		}
		if(isParent)
			return typesToJoinObj.get(parentNode).className;
		
		/*if no common type was found for the types*/
		return null;
	}
	
	/**
	 * Method used for joining the types which adds some other verifications
	 * which can save us from searching the inheritance tree 
	 *  
	 * @param branchTypes
	 * @return
	 */
	private String joinBranchTypes(ArrayList<String> branchTypes){
		
		String type = branchTypes.get(0);
		boolean allEqual = true;
		
		/*verifies if all  branches have the same type then there is no
		 * need to go through the inheritance graph 
		 * */
		for(int i = 1 ; i < branchTypes.size(); i++) {
			if(!branchTypes.get(i).equals(type))
				allEqual = false;
		}
		if(allEqual)
			return type;
		
		return joinTypes(branchTypes);
	}

	/**
	 *Mehtod which sets the type of a case statement: establishes the type of the conditions
	 *and after it sets the type for each branch. In the end, the types of the branches are joined 
	 *and the type is assigned to the case expression
	 * 
	 * @param expr
	 *            the case expression for which the type should be found
	 * @param map
	 *            the SymbolTable which contains all the current scopes
	 */

	private void setCaseType(typcase expr, SymbolTable map) {
		Expression exprCond = expr.expr;
		Cases cases = expr.cases;
		ArrayList<String> branchTypes = new ArrayList<String>();

		/* set type for the expression in the condition */
		assignExprTypes(exprCond, map);

		/* set types for each of the cases */
		Enumeration<TreeNode> exprEnum = cases.getElements();

		while (exprEnum.hasMoreElements()) {
			branch caseVal = (branch) (exprEnum.nextElement());
			
			/*each branch can be a scope because the Id can be used in the expression*/
			map.enterScope();
			map.addId(caseVal.name , caseVal);
			
			/* assign type for each branch */
			assignExprTypes(caseVal.expr, map);

			/* make an array containing the types of the branches */
			branchTypes.add(caseVal.expr.get_type().toString());
			
			map.exitScope();
		}

		/* find the type of the entire case, joining the types of the branches */
		exprCond.set_type(exprCond.get_type());
		String type = joinBranchTypes(branchTypes);
		
		if (type == null) {
			expr.set_type(getTypeObject("Object"));
		} else {
			expr.set_type(getTypeObject(type));
		}
	}

	/**
	 * Sets the type for an assignment expression
	 * 
	 * @param exprAssign the assignment expression that should be assigned with a type
	 * @param map  the SymbolTable which contains all the current scopes
	 */
	private void setAssignType(assign exprAssign, SymbolTable map) {
		Object result = map.lookup(exprAssign.name);
		String declaredType = null;
		String exprType;

		if (result instanceof String) {
			declaredType = ((String) result);
		}

		declaredType = analyseLookupResult(result).getString();

		/* set type for the expression we want to assign */
		assignExprTypes(exprAssign.expr, map);

		exprType = exprAssign.expr.get_type().getString();

		if (verifyConformity(declaredType, exprType)) {
			exprAssign.set_type(getTypeObject(exprType));
		} else {
			semantError(exprAssign, "Tipul obtinut pentru expresie ( "
					+ exprType + ") nu este in conformitate cu cel declarat ( "
					+ declaredType + " ) ");
		}
	}

	/**
	 * Verifies for errors the method used in the dispatch expression
	 *  
	 * @param exprDispatch the dispatch expression
	 * @param meth the method from the class
	 * @param givenParams the parameters used for the current dispatch
	 * @param map  the SymbolTable which contains all the current scopes
	 * 
	 * @return true if there were no errors or false otherwise
	 */
	private boolean checkMethodDispatch(Expression exprDispatch, method meth,
			Enumeration<Expression> givenParams, SymbolTable map) {

		Enumeration paramDecl = meth.formals.getElements();
		Expression exprParam;
		formal exprDeclParam;
		boolean ret = true;
		
		/* verify if the parameters have the same type in the same order */
		while (givenParams.hasMoreElements()) {
			exprParam = givenParams.nextElement();
			assignExprTypes(exprParam, map);

			exprDeclParam = (formal) (paramDecl.nextElement());
			assignExprTypes(exprParam, map);

			String invokeType = exprParam.get_type().toString();
			String declareType = exprDeclParam.type_decl.getString();

			if (exprDispatch instanceof dispatch)
				if (invokeType.equals("SELF_TYPE")) {
					invokeType = className;
				}
			/*
			 * the parameters with which the method is invoked must be conform
			 * to the parameters with which the method is declared
			 */
			if (!verifyConformity(declareType, invokeType)) {
				semantError(exprDispatch,
						"Parametrii nu satisfac constrangerea de conformitate. ");
				ret = false;
			}
		}

		return ret;
	}

	/**
	 * Establishes the type of a dispatch: ->looks up in the symbol table the
	 * method name -> if the method is defined the number and the type of the
	 * parameters is checked ->if after cheking the parameters there are no
	 * errors a type is assigned to a the dispatch expression
	 * 
	 * @param exprDispatch
	 *            the dispatch expression
	 * @param map
	 *            the SymbolTable
	 */
	private void setDispatchType(dispatch exprDispatch, SymbolTable map) {

		String dispName = exprDispatch.name.toString();
		Expression access = exprDispatch.expr;
		Expressions params = exprDispatch.actual;
		Enumeration<Expression> paramExprList = params.getElements();
		String accessType;

		assignExprTypes(access, map);
		accessType = access.get_type().toString();
		if(accessType.equals("SELF_TYPE"))
			accessType = className;

		/* verify if the node has a method with the specified name */
		NodeInhGraph classNode = findNode(accessType, startNodeInhGraph);
		method meth = null;
		boolean found = false;

		if (classNode != null) {
			Features formalParam = classNode.classObj.features;
			Enumeration<Feature> ftEnum = formalParam.getElements();
			Feature ftItem;

			while (ftEnum.hasMoreElements()) {
				ftItem = ftEnum.nextElement();

				if (ftItem instanceof method) {
					if (((method) ftItem).name.getString().equals(
							exprDispatch.name.getString())) {
						meth = (method) ftItem;
						found = true;
						break;
					}
				}
			}
		}

		if (!found)
			meth = (method) (map.lookup(exprDispatch.name));

		if (meth == null) {
			semantError(exprDispatch, "Nu exista definita metoda " + dispName
					+ " .");
			return;
		}

		/* check if the number of parameters is the same */
		if (meth.formals.getLength() != params.getLength()) {
			semantError(exprDispatch,
					"Numarul de parametri ai metodelor este diferit. ");
			return;
		}

		if (checkMethodDispatch(exprDispatch, meth, paramExprList, map)) {
			if (meth.return_type.getString().equals("SELF_TYPE"))
				exprDispatch.set_type(access.get_type());
			else
				exprDispatch.set_type(meth.return_type);
		}

	}

	/**
	 * Establish the type for a static dispatch
	 * 
	 * @param dispatchExpr
	 * @param map
	 */
	private void setStaticDispatchType(static_dispatch dispatchExpr,
			SymbolTable map) {
		Expression leftMost = dispatchExpr.expr;
		Expressions actualDispatch = dispatchExpr.actual;
		Enumeration enumActual = actualDispatch.getElements();
		String rightType = dispatchExpr.type_name.getString();
		String leftMostType;

		assignExprTypes(leftMost, map);
		leftMostType = leftMost.get_type().getString();

		/* verify if the node has a method with the specified name */
		method meth = (method) (map.lookup(dispatchExpr.name));

		if (meth == null) {
			semantError(dispatchExpr, "Nu exista definita metoda "
					+ dispatchExpr.name.getString() + " .");
			return;
		}

		/* check if the number of parameters is the same */
		if (meth.formals.getLength() != actualDispatch.getLength()) {
			semantError(dispatchExpr,
					"Numarul de parametri ai metodelor este diferit. ");
			return;
		}

		if (!verifyConformity(dispatchExpr.type_name.getString(), leftMostType)) {
			semantError(dispatchExpr,
					"Tipul declarat nu este in conformitate cu cel returnat de metoda.");
			return;
		}

		if (checkMethodDispatch(dispatchExpr, meth, enumActual, map)) {
			/*
			 * verify conformity between the type returned by the method and the
			 * one declared
			 */
			if (meth.return_type.getString().equals("SELF_TYPE"))
				dispatchExpr.set_type(dispatchExpr.type_name);
			else
				dispatchExpr.set_type(meth.return_type);
		}

	}

	/**
	 * Sets the let type
	 * 
	 * @param expr let expression to be analysed
	 * @param map  the SymbolTable which contains all the current scopes
	 */
	private void setLetType(let expr, SymbolTable map) {

		/*
		 * inner scope of let - allowing the body of let to recognize the
		 * previous definitions of the variables in let
		 */
		map.enterScope();
		assignExprTypes(expr.init, map);
		
		/* add the identifier to the outer scope  because it can be used in let's body*/
		if (expr.init instanceof no_expr)
			map.addId(expr.identifier, new String(expr.type_decl.getString()));
		else
			map.addId(expr.identifier, expr.init);

		
		assignExprTypes(expr.body, map);
		expr.set_type(expr.body.get_type());

		map.exitScope();
	}
	
	/**
	 * Method used for setting type of a block expression: the type of the last expression from the block
	 * 
	 * @param expr the block expression to be analysed
	 * @param map the SymbolTable which contains all the current scopes
	 */
	private void setTypesBlockExpr(block expr, SymbolTable map) {
		Expressions exprs = expr.body;
		Expression lastExpr = null;
		Enumeration<Expression> exprList = exprs.getElements();

		/*assign types to the expressions which are part of the block*/
		while (exprList.hasMoreElements()) {
			lastExpr = exprList.nextElement();
			assignExprTypes(lastExpr, map);
		}

		if (lastExpr.get_type() != null)
			expr.set_type(lastExpr.get_type());
	}
	

	/**
	 * Method which dispatches the types expressions which should be assigned types
	 * 
	 * @param expr the expression to be analysed
	 * @param map the SymbolTable which contains all the current scopes
	 */
	private void assignExprTypes(Expression expr, SymbolTable map) {

		/* assign types for constants */
		if (expr instanceof int_const) {
			((int_const) expr).set_type(getTypeObject("Int"));
			return;
		}

		if (expr instanceof bool_const) {
			expr.set_type(getTypeObject("Bool"));
			return;
		}

		if (expr instanceof string_const) {
			expr.set_type(getTypeObject("String"));
			return;
		}

		/* if new is used */
		if (expr instanceof new_) {
			if (((new_) expr).type_name.getString().equals("SELF_TYPE")) {
				expr.set_type(getTypeObject(className));
				return;
			} else {
				expr.set_type(getTypeObject(((new_) expr).type_name.getString()));
				return;
			}
		}

		/* find type for isvoid */
		if (expr instanceof isvoid) {
			assignExprTypes(((isvoid) expr).e1, map);
			expr.set_type(getTypeObject("Bool"));
			return;
		}

		/* set type for a block of expressions */
		if (expr instanceof block) {
			setTypesBlockExpr((block) expr, map);
			return;
		}

		/* the integer complement of the received expression */
		if (expr instanceof neg) {
			assignExprTypes(((neg) expr).e1, map);
			String exprType = ((neg) expr).e1.get_type().getString();

			if (!exprType.equals("Int")) {
				semantError(expr,
						"Tipul nu este in conformitate cu definitia lui neg.");
				return;
			}
			expr.set_type(getTypeObject("Int"));
			return;
		}

		/* if the expression is a mathematical operator */
		if (expr instanceof plus) {
			setTypesArithComparisonOp(expr, ((plus) expr).e1, ((plus) expr).e2,
					"adunare", "Int", map);
			return;
		}

		if (expr instanceof sub) {
			setTypesArithComparisonOp(expr, ((sub) expr).e1, ((sub) expr).e2,
					"scadere", "Int", map);
			return;
		}

		if (expr instanceof mul) {
			setTypesArithComparisonOp(expr, ((mul) expr).e1, ((mul) expr).e2,
					"inmultire", "Int", map);
			return;
		}

		if (expr instanceof divide) {
			setTypesArithComparisonOp(expr, ((divide) expr).e1,
					((divide) expr).e2, "impartire", "Int", map);
			return;
		}

		/* if the expression is used for boolean negation "not" */
		if (expr instanceof comp) {
			assignExprTypes(((comp) expr).e1, map);
			String exprType = ((comp) expr).e1.get_type().getString();

			if (!exprType.equals("Bool")) {
				semantError(expr, "Not nu se aplica tipului " + exprType);
				return;
			}
			expr.set_type(getTypeObject("Bool"));
			return;
		}

		/* if the expression is used for comparison */
		if (expr instanceof leq) {
			setTypesArithComparisonOp(expr, ((leq) expr).e1, ((leq) expr).e2,
					"mai mic sau egal", "Bool", map);
			return;
		}

		if (expr instanceof lt) {
			setTypesArithComparisonOp(expr, ((lt) expr).e1, ((lt) expr).e2,
					"mai mic", "Bool", map);
			return;
		}

		/* types for objects */
		if (expr instanceof object) {
			setObjectType(expr, map);
			return;
		}

		/* type for if statement */
		if (expr instanceof cond) {
			setIfType((cond) expr, map);
			return;
		}

		/* types for equality */
		if (expr instanceof eq) {
			setEqualType((eq) expr, map);
			return;
		}

		/* type for loop */
		if (expr instanceof loop) {
			setLoopType((loop) expr, map);
			return;
		}

		/* type for case */
		if (expr instanceof typcase) {
			setCaseType((typcase) expr, map);
			return;
		}

		/* assignment type */
		if (expr instanceof assign) {
			setAssignType((assign) expr, map);
			return;
		}

		/* dispatch */
		if (expr instanceof dispatch) {
			setDispatchType((dispatch) expr, map);
			return;
		}

		/* static dispatch */
		if (expr instanceof static_dispatch) {
			setStaticDispatchType((static_dispatch) expr, map);
			return;
		}

		/* let */
		if (expr instanceof let) {
			setLetType((let) expr, map);
			return;
		}

	}

	/**
	 * Method used for looking up a node in the inheritance tree
	 * 
	 * @param toFind the name of the class to be found
	 * @param startNode the node from which the looking up should start
	 * 
	 * @return the node containing the information about the required node
	 */

	private NodeInhGraph findNode(String toFind, NodeInhGraph startNode) {

		if (startNode.className.equals(toFind))
			return startNode;

		NodeInhGraph retVal = null, prevVal = null;

		for (int i = 0; i < startNode.children.size(); i++) {
			retVal = findNode(toFind, startNode.children.get(i));
			if (retVal != null && prevVal == null)
				prevVal = retVal;
		}

		return prevVal;

	}

	/**
	 * Verify if the received node is a child of a certain node
	 * 
	 * @param childClass the node for which the verification is made
	 * @param startNode the node with which the lookup starts
	 *  
	 * @return true if the class is a child of the given node or false otherwise 
	 */
	private boolean isChild(String childClass, NodeInhGraph startNode) {

		if (childClass.equals(startNode.className))
			return true;
		boolean retVal = false, prevVal = false;
		for (int i = 0; i < startNode.children.size(); i++) {
			retVal = isChild(childClass, startNode.children.get(i));
			if (retVal && !prevVal)
				prevVal = retVal;
		}

		return prevVal;
	}

	/**
	 * Verifies the conformity of two types T' <=T (T' inherits T)
	 * 
	 * @param initialClass
	 *            the class declared for the expression
	 * @param exprClass
	 *            the class obtained after analysing the code
	 * @return true if the class obtained is the same or inherits from the class
	 *         originally named
	 */
	private boolean verifyConformity(String initialClass, String exprClass) {
		if (initialClass.equals(exprClass))
			return true;
		NodeInhGraph initClass = findNode(initialClass, startNodeInhGraph);

		if (initClass == null)
			return false;

		return isChild(exprClass, initClass);
	}

	/**
	 * Method which assignes types to the attributes
	 * 
	 * @param node the node which contains information about the attribute
	 * @param map the SymbolTable which contains all the current scopes
	 */
	private void assignAttrTypes(attr node, SymbolTable map) {

		Expression expr = node.init;
		String attrName = new String(node.name.toString());
		String attrDeclType = new String(node.type_decl.getString());

		if (!(expr instanceof no_expr)) {
			assignExprTypes(expr, map);
			if (!verifyConformity(node.type_decl.getString(), expr.get_type()
					.getString())) {
				semantError(node,
						"Noul tip nu este in conformitate cu tipul declarat ");
			}
		}
	}

	/**
	 * The types for a method are set 
	 * 
	 * @param node the expression which contains the info about the method 
	 * @param map the SymbolTable which contains all the current scopes
	 */
	private void assignMethodTypes(method node, SymbolTable map) {

		Expression expr = node.expr;
		String methodName = new String(node.name.getString());
		String reType = new String(node.return_type.getString());
		Enumeration<TreeNode> formalParams = node.formals.getElements();
		String paramName, paramType;
		TreeNode paramExpr;

		/*enter method scope - it's parameters are only visible inside the method*/
		map.enterScope();

		/* add formal parameters into current scope */
		while (formalParams.hasMoreElements()) {
			paramExpr = formalParams.nextElement();
			paramName = new String(((formal) paramExpr).name.getString());
			paramType = new String(((formal) paramExpr).type_decl.getString());

			map.addId(getTypeObject(paramName), (Formal) paramExpr);
			
		}

		if (!(expr instanceof no_expr)) {
			assignExprTypes(expr, map);
		}
		
		map.exitScope();

	}

	/**
	 * Method used to assign types to a class
	 * 
	 * @param startNode the root of the inheritance tree
	 * @param map the SymbolTable which contains all the current scopes
	 */
	public void assignTypes(NodeInhGraph startNode, SymbolTable map) {

		ArrayList<String> methodName = new ArrayList<String>();
		map.addId(getTypeObject(className), classObj);

		startNodeInhGraph = startNode;

		/* analyse the attributes if there are any */
		Enumeration attrMeth = classObj.features.getElements();
		TreeNode node;

		/*
		 * first add attributes into symbol table the methods were already added
		 * because their scope is global
		 */
		while (attrMeth.hasMoreElements()) {
			node = (TreeNode) attrMeth.nextElement();

			if (node instanceof attr) {
				map.addId(((attr) node).name, (attr) node);
			}
		}

		attrMeth = classObj.features.getElements();
		while (attrMeth.hasMoreElements()) {
			node = (TreeNode) attrMeth.nextElement();

			if (node instanceof attr) {
				assignAttrTypes((attr) node, map);
			}

			if (node instanceof method) {
				assignMethodTypes((method) node, map);
				/*Verify if there are multiple methods with the same name
				 * and different number /type of parameters defined
				 * */
				if(methodName.contains(((method)node).name.getString())) {
					semantError(node, "Nu este permisa supraincarcarea metodei "+((method)node).name.getString());
				} else {
					methodName.add(((method)node).name.getString());
				}
			}
		}

	}

	public PrintStream semantError(attr c, String errorContent) {
		errorStream.print("Linia " + c.getLineNumber() + ": " + errorContent);
		errorStream.println();
		return semantError();
	}

	public PrintStream semantError(Expression c, String errorContent) {
		errorStream.print("Linia " + c.getLineNumber() + ": " + errorContent);
		errorStream.println();
		return semantError();
	}

	public PrintStream semantError(TreeNode c, String errorContent) {
		errorStream.print("Linia " + c.getLineNumber() + ": " + errorContent);
		errorStream.println();
		return semantError();
	}

	public PrintStream semantError() {
		semantErrors++;
		return errorStream;
	}

	public boolean errors() {
		return semantErrors != 0;
	}

}
