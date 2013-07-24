(* Simple conditional and repetitive statements *)

class Main {
	 max_val: Int <- 100;
	 even_count: Int <- 0;
	 
	 main(): Int {
	 	let i: Int <- 0 in {
	 		while i < max_val loop {
	 			if i - (i/2)*2 = 0 then
	 				even_count <- even_count + 1
	 			else
	 				even_count
	 			fi;
	 			i <- i + 1;
	 		} pool;
	 		case self of
	 			myself: Main => even_count;
	 			myself: Object => 0;
	 		esac;
	 	}
	 };
};