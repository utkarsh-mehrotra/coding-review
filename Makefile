# Compiles every template, runs every test suite, and enforces the template-first rule.
SRC := common/Check.java $(wildcard topics/*/*.java) $(wildcard topics/*/tests/*.java)
TESTS := GraphCoreTest GraphVariantsTest GraphExtrasTest GraphRandomTest DPTest TreesTest PatternsTest StructuresTest Deepen1Test Deepen2Test Deepen3Test

.PHONY: test check site test-site all clean problems fetch-lc
all: test problems check site test-site

test:
	rm -rf build && mkdir -p build
	javac -Xlint:none -d build $(SRC)
	@for t in $(TESTS); do echo "== $$t"; java -cp build $$t || exit 1; done

# every decision-tree entry must link to File#method that exists in a template
check:
	python3 site/build.py --check

site:
	python3 site/build.py

test-site:
	python3 -m unittest discover -s site/tests

# LeetCode coverage: map every problem to a template (offline, uses the cached list in tools/lc-coverage/data)
LC := tools/lc-coverage
problems:
	mkdir -p build
	python3 $(LC)/inventory.py . build/lc-inventory.json
	python3 $(LC)/match.py build/lc-inventory.json $(LC)/data/lc_all.json $(LC)/data/problems.json

# refetch LeetCode's public problem list (about 42 requests, 1 per second), then remap
fetch-lc:
	rm -f $(LC)/data/lc_all.json
	python3 $(LC)/fetch_lc.py $(LC)/data/lc_all.json
	$(MAKE) problems

clean:
	rm -rf build
