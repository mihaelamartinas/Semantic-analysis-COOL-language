(* Wrong usage of type Void *)

class Main {
	i : Int <- 10;
	main(): Object {
		let a: Object <- while 0 <= i loop
							i <- i - 1
						pool
		in
			case a of
				x: Object => self;
			esac 
	};
};