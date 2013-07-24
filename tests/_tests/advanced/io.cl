(* I/O operations *)

class Main inherits IO {
	name: String;
	age: Int;
	
	main() : Object {
		{
			name <- out_string("Please enter your name: ").in_string();
			age <- out_string("Please enter your age: ").in_int();
			
			out_string("Your name is ").out_string(name).
				out_string(" and you are ").out_int(age).out_string(" years old.");
		}
	};
};