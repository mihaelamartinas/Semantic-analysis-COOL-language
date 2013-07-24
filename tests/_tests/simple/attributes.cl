(* Class attributes definition and usage *)

class Main {
	number: Int;
	initialized: Int <- 1234;
	a_String: String <- "This is a string";
	
	main(): Int {
		number + initialized
	};
};
