(* Type mismatch errors *)

class A { };
class B inherits A { };

class Main inherits Object {
	mymethod(p: A) : A {
		p
	};
	
	myothermethod(p: B) : A {
		p
	};
	
	main() : A {
		let var: A <- new B in
			myothermethod(mymethod(var))
	};
};