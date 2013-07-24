(* Simple method definitions *)

class Main {
	sum(a: Int, b: Int) : Int {
		a + b
	};
	
	first(x: String, y: String, z: String) : String {
		{z; y; x;}
	};
	
	recurse(x: Int, y: String, z: Bool) : Bool {
		recurse(x+1, y, not z)
	};
	
	main():Int {
		{ first("a", "b", "c"); sum(10, 100); }
	};
};