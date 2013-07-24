
class Main inherits IO {
   
   char : String;
   avar : A; 
   a_var : A;
   flag : Bool <- true;


   menu() : String {
      {
         out_string("\n\tTo add a number to ");
         print(avar);
         out_string("...enter a:\n");
         out_string("\tTo negate ");
         print(avar);
         out_string("...enter b:\n");
         out_string("\tTo find the difference between ");
         print(avar);
         out_string("and another number...enter c:\n");
         out_string("\tTo find the factorial of ");
         print(avar);
         out_string("...enter d:\n");
         out_string("\tTo square ");
         print(avar);
         out_string("...enter e:\n");
         out_string("\tTo cube ");
         print(avar);
         out_string("...enter f:\n");
         out_string("\tTo find out if ");
         print(avar);
         out_string("is a multiple of 3...enter g:\n");
         out_string("\tTo divide ");
         print(avar);
         out_string("by 8...enter h:\n");
	 out_string("\tTo get a new number...enter j:\n");
	 out_string("\tTo quit...enter q:\n\n");
         in_string();
      }
   };

   prompt() : String {
      {
         out_string("\n");
         out_string("Please enter a number...  ");
         in_string();
      }
   };

   get_int() : Int {
      {
	 (let z : A2I <- new A2I in
	    (let s : String <- prompt() in
	       z.a2i(s)
	    )
         );
      }
   };

   is_even(num : Int) : Bool {
      (let x : Int <- num in
            if x < 0 then is_even(~x) else
            if 0 = x then true else
	    if 1 = x then false else
	          is_even(x - 2)
	    fi fi fi
      )
   };

   class_type(var : A) : SELF_TYPE {
      case var of
	 a : A => out_string("Class type is now A\n");
	 b : B => out_string("Class type is now B\n");
	 c : C => out_string("Class type is now C\n");
	 d : D => out_string("Class type is now D\n");
	 e : E => out_string("Class type is now E\n");
	 o : Object => out_string("Oooops\n");
      esac
   };
 
   print(var : A) : SELF_TYPE {
     (let z : A2I <- new A2I in
	{
	   out_string(z.i2a(var.value()));
	   out_string(" ");
	}
     )
   };

   main() : Object {
      {
         avar <- (new A);
         while flag loop
            {
	       -- avar <- (new A).set_var(get_int());
	       out_string("number ");
	       print(avar);
	       if is_even(avar.value()) then
	          out_string("is even!\n")
	       else
	          out_string("is odd!\n")
	       fi;
	       -- print(avar); -- prints out answer
	       class_type(avar);
	       char <- menu();
                  if char = "a" then -- add
                     {
                        a_var <- (new A).set_var(get_int());
	                avar <- (new B).method2(avar.value(), a_var.value());
	             } else
                  if char = "b" then -- negate
                     case avar of
	                   c : C => avar <- c.method6(c.value());
	                   a : A => avar <- a.method3(a.value());
	                   o : Object => {
		                  out_string("Oooops\n");
		                  abort(); 0;
		               };
                     esac else
                  if char = "c" then -- diff
                     {
                        a_var <- (new A).set_var(get_int());
	                avar <- (new D).method4(avar.value(), a_var.value());
	             } else
                  if char = "d" then avar <- (new C)@A.method5(avar.value()) else
		          -- factorial
                  if char = "e" then avar <- (new C)@B.method5(avar.value()) else
			  -- square
                  if char = "f" then avar <- (new C)@C.method5(avar.value()) else
			  -- cube
                  if char = "g" then -- multiple of 3?
		      if ((new D).method7(avar.value()))
		                       then -- avar <- (new A).method1(avar.value())
			 {
	                    out_string("number ");
	                    print(avar);
	                    out_string("is divisible by 3.\n");
			 }
			 else  -- avar <- (new A).set_var(0)
			 {
	                    out_string("number ");
	                    print(avar);
	                    out_string("is not divisible by 3.\n");
			 }
		      fi else
                  if char = "h" then 
		      (let x : A in
			 {
		            x <- (new E).method6(avar.value());
			    (let r : Int <- (avar.value() - (x.value() * 8)) in
			       {
			          out_string("number ");
			          print(avar);
			          out_string("is equal to ");
			          print(x);
			          out_string("times 8 with a remainder of ");
				  (let a : A2I <- new A2I in
				     {
			                out_string(a.i2a(r));
			                out_string("\n");
				     }
				  ); -- end let a:
			       }
                            ); -- end let r:
			    avar <- x;
		         } 
		      )  -- end let x:
		      else
                  if char = "j" then avar <- (new A)
		      else
                  if char = "q" then flag <- false
		      else
                      avar <- (new A).method1(avar.value()) -- divide/8
                  fi fi fi fi fi fi fi fi fi fi;
            }
         pool;
       }
   };

};
