(* Inner let expressions *)

class Main inherits IO {
	main(): Object {
		out_int(let i: Int <- let j: Int <- let k: Int <- 2 in 3 in 4 in 5)
	};
};

	
