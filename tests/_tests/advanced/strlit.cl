(* String literal tests *)

class Main {
	a : String <- "This is a string literal";
	
	b : String <- "This\
	is\
	another\
	string literal\
	";
	
	c : String <- "This is\
	(* a literal *)\
	too";
	
	d: String <- "\a\b\c\d\e\f\g\h\i\j\k\l\m\n\o\p\q\r\s\t\u\v\w\x\y\z\"\
	\'\{\}\;";
	
	main() : String {
		{ a; b; c; d; }
	};
};