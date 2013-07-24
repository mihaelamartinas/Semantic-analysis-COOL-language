(* Method overloading is an error in COOL *)

class Main {
	mymethod(a: Int): Int {
		a*2
	};
	
	mymethod(a: Int, b : Int): Int {
		a*b
	};
	
	main(): Int {
		mymethod(10) + mymethod(20, 30)
	};
};