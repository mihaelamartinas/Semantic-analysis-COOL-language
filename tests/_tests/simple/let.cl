(* Simple let constructions *)

class Main {
	main(): Int {
		let a: Int <- 10, b: Int <- 20 in {
			let c : Int <- 30 in (a <- a + c);
			a + b;
		}
	};
};