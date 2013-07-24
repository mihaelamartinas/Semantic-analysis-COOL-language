(* Complex expressions with parantheses *)

class Matrix {
	a : Int; b : Int; c : Int;
	d : Int; e : Int; f : Int;
	g : Int; h : Int; i : Int;
	
	init(va : Int, vb : Int, vc : Int, vd : Int, ve : Int, vf : Int, vg : Int,
		vh : Int, vi : Int) : Matrix {
		 {
		 	a <- va; b <- vb; c <- vc;
		 	d <- vd; e <- ve; f <- vf;
		 	g <- vg; h <- vh; i <- vi;
		 	self;
		 }
	};
	
	det() : Int {
		(a*e*i + b*f*g + c*d*h)-(g*e*c + h*f*a + i*d*b)
	};
};

class Main {
	n : Int <- 5;
	prec: Int <- 10;
	
	factorial(n: Int) : Int {
		if n < 1 then
			1
		else
			factorial(n-1)
		fi
	};
	
	taylor(n: Int, m: Int): Int {
		let i : Int <- 0, sum : Int <- 0, pow : Int <- 1 in {
			while i < m loop
			{
				sum <- sum + pow / factorial(i);
				pow <- pow * n;
				i <- i + 1;
			}
			pool;
			sum;
		}
	};
	
	main() : Int {
		let val: Int, m : Matrix <- new Matrix in {
			val <- taylor(n, prec);
			m.init(n, 0, n, 0, n, 0, n, 0, n);
			m.det();
		}
	};
};