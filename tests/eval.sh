#!/bin/bash

TESTS_DIR="_tests"

export CLASSPATH="$CLASSPATH:./bin:antlr-3.4.jar:java-cup-11a-runtime.jar"

PARSER="java Semant"

function fail {
	printf "FAIL (%s)\n" "$1"
}

function pass {
	printf "PASS\n"
}


function test_correct {
	printf "%-50s" "Testing file $1... "
	rm -f __stdout
	rm -f __stderr
	
	$PARSER < $1 1>__stdout 2>__stderr
	
	STDERR=$(cat __stderr)
	
	if [ -n "$STDERR" ]; then
		fail "compilation errors"
		echo "-- First lines of stderr below --"
		cat __stderr | head -n 3
		echo "-----"
		echo
	else
		if [ -z "$(diff -w -q __stdout $2)" ]; then
			pass
		else
			fail "differences found"
			echo "-- First lines of the diff below --"
			diff -u __stdout "$2" | head -n 10
			echo "-----"
			echo
		fi
	fi
}

function test_error {
	printf "%-50s" "Testing file $1... "
	rm -f __stdout
	rm -f __stderr
	
	$PARSER < $1 1>__stdout 2>__stderr
	
	STDOUT=$(cat __stdout)

	if grep -q '^java[.].*Exception:' < __stderr; then
		fail "Java errors detected"
		echo "-- First lines of stderr below --"
		cat __stderr | head -n 5
		echo "-----"
		echo
	elif [ -n "$STDOUT" ]; then
		fail "AST output detected"
		echo "-- First lines of stdout --"
		cat __stdout | head -n 5
		echo "-----"
		echo
	else
		pass
		echo "-- Error list --"
		cat __stderr
		echo "-----"
		echo
	fi
}

echo "******************************"
echo "**** Running simple tests ****"
echo "******************************"
echo 

for CL_SRC in $TESTS_DIR/simple/*.in.ast; do
	test_correct $CL_SRC ${CL_SRC/%in.ast/ast}
done

echo
echo


echo "********************************"
echo "**** Running advanced tests ****"
echo "********************************"
echo

for CL_SRC in $TESTS_DIR/advanced/*.in.ast; do
	test_correct $CL_SRC ${CL_SRC/%in.ast/ast}
done

echo
echo

echo "*******************************"
echo "**** Running complex tests ****"
echo "*******************************"
echo

for CL_SRC in $TESTS_DIR/complex/*.in.ast; do
	test_correct $CL_SRC ${CL_SRC/%in.ast/ast}
done


echo
echo

echo "********************************"
echo "***** Running errors tests *****"
echo "********************************"
echo

for CL_SRC in $TESTS_DIR/errors/*.in.ast; do
        test_error $CL_SRC ${CL_SRC/%in.ast/ast}
done

echo
echo

if [ -f ./eval_bonus.sh ]; then
	./eval_bonus.sh
fi

rm -f __stdout
rm -f __stderr
