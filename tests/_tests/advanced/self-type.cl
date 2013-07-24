(* SELF_TYPE tests *)

class A inherits IO {
	greet(): SELF_TYPE {
		out_string("Hello from class A!")
	};
};

class B inherits A {
	goodbye(): SELF_TYPE {
		out_string("Buhbyeee says B, then!")
	};
};

class Main {
	main() : Object {
		let speaker : B <- new B in speaker.greet().goodbye()
	};
};