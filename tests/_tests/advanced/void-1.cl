(* Usage of the type Void *)

class Main {
	max_i: Int <- 1000;
	
	main(): Bool {
		isvoid let i : Int <-0 in 
			while i < 0 loop
				i <- i + 1
			pool
	};
};