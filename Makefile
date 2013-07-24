.PHONY: all clean dist_clean build

all:
	@echo "Compiling.."
	@make --no-print-directory -C src
	@echo "Done.."

build: all

clean:
	@make --no-print-directory -C src clean

dist_clean: clean
	@rm -rf bin
