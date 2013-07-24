(* Usage of String class methods *)

class Main {
	paranthese(a : String, b : String, c : String) : String {
		a.concat("(").concat(b).concat(")").concat(c)
	};
	
	main() : String {
		let result : String <-
			paranthese(
				paranthese("a", "", "c"),
				paranthese(
					paranthese("d", "e", "f"),
					paranthese("g", "", "i"),
					paranthese("j", "k", "l")
				),
				paranthese("m", "n", "o")
			)
		in
			result.substr(0, result.length() / 2)
	};
};