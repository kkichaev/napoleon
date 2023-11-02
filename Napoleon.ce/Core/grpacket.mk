include ../make.vars
include make.vars

PROGRAM=GRPacket

SOURCE=ByteStream DataModule Format GRPacket GRJS json Interfaces Objects AssemblyInfo Adler32 CRC32 Deflater DeflaterConstants DeflaterEngine \
 DeflaterHuffman DeflaterPending IChecksum Inflater InflaterDynHeader InflaterHuffmanTree PendingBuffer SharpZipBaseException \
 Streams/DeflaterOutputStream Streams/InflaterInputStream Streams/OutputWindow Streams/StreamManipulator ZipException

RESOURCE=

REFERENCE=System.Xml System.Data System System.Windows.Forms

VPATH:=Napoleon.Net/GRPacket Napoleon.Net/GRPacket/Zip Napoleon.Net/GRPacket/Properties

all: $(OUT_DIR)/$(PROGRAM).dll

include ../make.cs


