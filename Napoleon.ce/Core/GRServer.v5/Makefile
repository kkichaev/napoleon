export

OUT_DIR=./bin

all:libpc grserver reporter

release:CFLAGS=-O3
release:libpc grserver reporter

debug:OUT_DIR=./bin.d
debug:CFLAGS=-ggdb
debug:libpc grserver reporter

clean:
	rm -rf $(OUT_DIR)/grserver.bin $(OUT_DIR)/libpc $(OUT_DIR)/python

libpc:
	$(MAKE) -f libpc.make -j8

grserver: libpc
	$(MAKE) -f grserver.make -j8

reporter: libpc
	$(MAKE) -f reporter.make -j8
