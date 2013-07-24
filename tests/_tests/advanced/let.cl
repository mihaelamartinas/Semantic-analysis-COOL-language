(* Nested let expressions *)

class Main {
	main() : Int {
		let a : Int <- 10, b : Int <- 20 in a + 2 * b + let a : Int <- 50, c : Int <- 30 in
			a + b + c - 3 * let x: Int <- 20 in b * (x * let b : Int <- 100 in a * b) * c
	};
};