# Compiles every template, runs every test suite, and enforces the template-first rule.
SRC := common/Check.java $(wildcard topics/*/*.java) $(wildcard topics/*/tests/*.java)
TESTS := GraphCoreTest GraphVariantsTest GraphExtrasTest GraphRandomTest DPTest DPVariantsTest TreesTest TreesVariantsTest PatternsTest ArraysVariantsTest WindowsVariantsTest StacksVariantsTest BinarySearchVariantsTest BacktrackingVariantsTest LinkedListsTest LinkedListsVariantsTest BitsMathTest BitsMathVariantsTest StringAlgorithmsTest StringsVariantsTest HeapsVariantsTest IntervalsVariantsTest GreedyVariantsTest DesignStructuresTest DesignVariantsTest

.PHONY: test check site test-site serve all clean problems fetch-lc fetch-lists
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

# the coverage view fetches coverage.json, which browsers block on file://, so serve the site locally
serve:
	python3 -m http.server 8000 -d site

test-site:
	python3 -m unittest discover -s site/tests
	node --test site/tests/*.test.js

# LeetCode coverage: map every problem to a template (offline, uses the cached list in tools/lc-coverage/data)
LC := tools/lc-coverage
problems:
	mkdir -p build
	python3 $(LC)/inventory.py . build/lc-inventory.json
	python3 $(LC)/match.py build/lc-inventory.json $(LC)/data/lc_all.json $(LC)/data/problems.json $(LC)/data/lists.json

# refetch LeetCode's public problem list (about 42 requests, 1 per second), then remap
fetch-lc:
	rm -f $(LC)/data/lc_all.json
	python3 $(LC)/fetch_lc.py $(LC)/data/lc_all.json
	$(MAKE) problems

# refetch the Top Interview 150 and LeetCode 75 lists (2 requests), then remap
fetch-lists:
	rm -f $(LC)/data/lists.json
	python3 $(LC)/fetch_lists.py $(LC)/data/lists.json
	$(MAKE) problems

clean:
	rm -rf build
