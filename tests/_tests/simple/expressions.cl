(* Simple expressions, no precedence or associativity involved *)

class Main {
	main(): Int {
		{ 
			1 + 2;
			123 - 1000;
			3 / 4;
			10 * 20;
			~ 100;
			5 < 3;
			4 <= 7;
			100 = 100;
			not true;
			isvoid false;
			0;
		}
	};
};