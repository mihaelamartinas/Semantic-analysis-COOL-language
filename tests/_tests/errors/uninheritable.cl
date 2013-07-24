(* Uninheritable classes *)

class MyInt inherits Int {

};

class MyString inherits String {

};

class Main {
	main() : Int {
		let a : MyInt <- new MyInt in
			case a of
				x: MyInt => 1;
				x: Object => 0;
			esac
	};
};