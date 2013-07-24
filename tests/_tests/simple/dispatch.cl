class A {
	method(a : Int, b : Int) : Int {
		a + b
	};
};

class B inherits A {
	 method(a: Int, b : Int) : Int {
	 	a * b
	 };
};

class Main {
	obj : B <- new B;
	
	main() : Int {
		{
			obj.method(1, 2);
			obj@A.method(3, 4);
			obj@B.method(5, 6);
		}
	};
};