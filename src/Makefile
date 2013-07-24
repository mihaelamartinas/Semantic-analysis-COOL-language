.PHONY: all,  src, clean


all: src

src:
	@test -d ../bin || mkdir ../bin
	@echo "Compiling classes.."
	@javac -cp ../java-cup-11a-runtime.jar *.java -d ../bin/
	@echo "Done compiling classes.."

clean:
	@$(RM) *.class ../bin/*.class
