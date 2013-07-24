(* Cycles in the inheritance graph *)


class B inherits C { 

};

class C inherits D {
	
};

class E inherits C {

};

class A inherits B {
	method() : Int {
		0
	};
};

class D inherits A {

};

class Main inherits E {
	main() : Int {
		method()
	};
};

