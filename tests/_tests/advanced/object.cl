(* Object class operations *)

class Main {
	main() : String {
		if self.type_name() = "Main" then
			"Original".copy()
		else
			{
				self.abort();
				"Unreachable";
			}
		fi
	};
};