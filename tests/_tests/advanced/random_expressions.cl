(* Just some random expressions *)

class Main inherits IO {
	main(): Object {
		if (let i : Int <- 4, j: IO in {if isvoid j then 2 else 3 fi;} + 4).type_name().concat({out_string("OK");"O";}).length()/2*case self of x : IO =>4;x : Object=>5;	esac < 4 then out_string("") else out_string("") fi 
	}; 
};
	
