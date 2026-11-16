# Compiles every template, runs every test suite, and enforces the template-first rule.
SRC := common/Check.java $(wildcard topics/*/*.java) $(wildcard topics/*/tests/*.java)
TESTS := GraphCoreTest GraphVariantsTest GraphExtrasTest DPTest TreesTest PatternsTest StructuresTest

.PHONY: test check site all clean
all: test check site

test:
	rm -rf build && mkdir -p build
	javac -Xlint:none -d build $(SRC)
	@for t in $(TESTS); do echo "== $$t"; java -cp build $$t || exit 1; done

# every decision-tree entry must link to File#method that exists in a template
check:
	python3 site/build.py --check

site:
	python3 site/build.py

clean:
	rm -rf build
