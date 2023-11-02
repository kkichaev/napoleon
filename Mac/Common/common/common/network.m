//
//  network.m
//  common
//
//  Created by ert on 25/01/2019.
//  Copyright © 2019 GRSoft. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <zlib.h>
#import "common.h"

const double WAIT_SLEEP = 10e-6;
const double WAIT_CONNECT = 1 * 60 * 10e9;
const uint32_t WAIT_READ = 1 * 60 * 1000;

NSString* DATA_TAG = @"DATA";
NSString* PACKET_TAG = @"GRPACKET";
NSString* GZIP_TAG = @"GZIP";
NSString* CRC_TAG = @"CRC";

@interface GRSMemoryStream : NSInputStream
@property NSInteger curPos;
-(instancetype) initStream: (NSData*) data;
-(NSString*) nextSym;
@end

static NSString* readSymbol(NSInputStream* stream) {
    uint8_t buf[4] = {0xff,0xfe,0,0};
    if([stream read:buf+2 maxLength:2] < 2) {
        return nil;
    }
    return [[NSString alloc] initWithBytes:buf length:4 encoding:NSUTF16StringEncoding];
}

static BOOL waitData(NSInputStream* stream, uint32_t ms) {
    if(!ms) return [stream hasBytesAvailable];
    
    uint32_t startDate = (uint32_t)([NSDate timeIntervalSinceReferenceDate] * 1000);
    while(![stream hasBytesAvailable]) {
        if([stream streamStatus] == NSStreamStatusError) {
            break;
        }
        [NSThread sleepForTimeInterval:WAIT_SLEEP];
        uint32_t current = (uint32_t)([NSDate timeIntervalSinceReferenceDate] * 1000);
        
        if(current - startDate > ms) break;
    }
    return [stream hasBytesAvailable];
}

static BOOL readData(NSInputStream* stream, void* buffer, unsigned length, uint32_t ms, id<GRSNetworkProgress> progress) {
    int pos = 0;
    [progress setCurrent:pos];
    while(length > 0) {
        if(!waitData(stream, ms)) return FALSE;
        
//        uint8_t* ptr;
        NSUInteger size = [stream read:buffer maxLength:length];
        if(size < 0) return FALSE;
        
        length -= size;
        buffer += size;
        pos += size;
        [progress setCurrent:pos];

//        if([stream getBuffer:&ptr length:&size]) {
//            if(size > length) size = length;
//
//            memcpy(buffer, ptr, size);
//            length -= size;
//            buffer += size;
//            pos += size;
//            [progress setCurrent:pos];
//        }
    }
    return TRUE;
}

static NSString* readUntill(NSInputStream* stream, NSString* syms, uint32_t ms, NSString** endStr) {
    NSMutableString* outStr = [[NSMutableString alloc] init];
    while(TRUE) {
        if(!waitData(stream, ms)) {
            outStr = nil;
            break;
        }
        NSString* tstr = readSymbol(stream);
        if(!tstr) {
            outStr = nil;
            break;
        }
        if([syms containsString:tstr]) {
            if(endStr) *endStr = tstr;
            break;
        }
        [outStr appendString:tstr];
    }
    
    return outStr;
}

static NSString* readString(NSInputStream* stream) {
    NSString* ends;
    NSMutableString* outStr = [NSMutableString string];
    readSymbol(stream); // must be \"
    while (TRUE) {
        NSString* str = readUntill(stream, @"\\\"", 0, &ends);
        if(!str) return FALSE;
        [outStr appendString:str];
        
        if( [ends isEqualToString:@"\""]) break;
        
        NSString* sym = readSymbol(stream);
        if(!sym) return FALSE;
        
        if([sym isEqualToString:@"b"]) sym = @"\b";
        else if( [sym isEqualToString:@"f"] ) sym = @"\f";
        else if( [sym isEqualToString:@"f"] ) sym = @"\f";
        else if( [sym isEqualToString:@"n"] ) sym = @"\n";
        else if( [sym isEqualToString:@"r"] ) sym = @"\r";
        else if( [sym isEqualToString:@"t"] ) sym = @"\t";
        [outStr appendString:sym];
    }
    return outStr;
}

static NSData* stringToData(NSString* str) {
    NSData* data = [str dataUsingEncoding:NSUTF16StringEncoding];
    NSRange range = NSMakeRange(2, [data length] - 2);
    return [data subdataWithRange:range];
}

@interface GRSNetworkField : NSObject
@property NSString* name;

-(instancetype) init : (NSString*) name dataClass: (Class) dataClass;
-(void) putHeader:(NSMutableString*) stream;
-(void) writeToStream:(NSMutableData*) stream : (GRSDataObject*) data;
-(BOOL) readToData:(GRSDataObject*) data : (NSInputStream*) stream;
@end

@interface GRSNetworkEmptyField : GRSNetworkField
-(instancetype) init : (NSString*) name readType: (NSString*) readType;
@end

@interface GRSNetworkString : GRSNetworkField
@end

@interface GRSNetworkInteger : GRSNetworkField
@end

@interface GRSNetworkLong : GRSNetworkInteger
@end

@interface GRSNetworkFloat : GRSNetworkField
@end

@interface GRSNetworkDate : GRSNetworkField
-(void) setTimeReader;
-(void) setDateReader;
@end

@interface GRSNetworkArray : GRSNetworkField {
    NSArray* readBinders;
}
-(instancetype) initWrite : (NSString*) objectSendName dataClass: (Class) dataClass;
-(instancetype) initRead : (NSInputStream*) stream forDataClass: (Class) dataClass;
-(GRSDataObject*) read: (NSInputStream*) stream;
@end

@interface GRSNetworkBinary : GRSNetworkField
@end

@implementation GRSNetworkField
- (instancetype)init:(NSString *)name dataClass:(Class)dataClass {
    self = [super init];
    if(self) {
        self.name = name;
    }
    return self;
}
- (void)putHeader:(NSMutableString *)stream { }
- (void)writeToStream:(NSMutableData *)stream :(GRSDataObject *)data {}
- (BOOL)readToData:(GRSDataObject *)data :(NSInputStream *)stream { return FALSE; }
@end

@implementation GRSNetworkEmptyField {
    GRSNetworkField* inField;
}

- (instancetype)init:(NSString *)name readType: (NSString*) readType {
    self = [super init:name dataClass:nil];
    if(self) {
        if( [readType isEqualToString:@"s"] ) {
            inField = [[GRSNetworkString alloc] init:name dataClass:nil];
        } else if( [readType isEqualToString:@"b"] ) {
            inField = [[GRSNetworkBinary alloc] init:name dataClass:nil];
        } else {
            inField = [[GRSNetworkInteger alloc] init:name dataClass:nil];
        }
    }
    return self;
}

- (BOOL)readToData:(GRSDataObject *)data :(NSInputStream *)stream {
    [inField readToData:nil :stream];
    return TRUE;
}
@end

@implementation GRSNetworkString
- (void)putHeader:(NSMutableString *)stream { [stream appendFormat:@"%@:s", self.name]; }

- (void)writeToStream:(NSMutableData *)stream :(GRSDataObject *)data {
    NSMutableString *ts = [NSMutableString stringWithString:@"\"\""];
    NSString* src = [data valueForKey:self.name];
    if(src && src.length) {
        ts = [NSMutableString stringWithString:src];
        [ts replaceOccurrencesOfString:@"\"" withString:@"\\\"" options:NSLiteralSearch range:NSMakeRange(0, [ts length])];
        [ts insertString:@"\"" atIndex:0];
        [ts appendString:@"\""];
    }
    [stream appendData:stringToData(ts)];
}

- (BOOL)readToData:(GRSDataObject *)data :(NSInputStream *)stream {
    NSString* outStr = readString(stream);
    [data setValue:outStr forKey: self.name];
    readSymbol(stream);
    return TRUE;
}
@end

@implementation GRSNetworkInteger
- (void)putHeader:(NSMutableString *)stream { [stream appendFormat:@"%@:n", self.name]; }

- (void)writeToStream:(NSMutableData *)stream :(GRSDataObject *)data {
    NSNumber* src = [data valueForKey:self.name];
    [stream appendData:stringToData([NSString stringWithFormat:@"%d", [src intValue]])];
}
- (BOOL)readToData:(GRSDataObject *)data :(NSInputStream *)stream {
    NSString* str = readUntill(stream, @",]", 0, nil);
    NSNumber *val = [NSNumber numberWithInteger: [str integerValue]];
    [data setValue:val forKey: self.name];
    return TRUE;
}
@end

@implementation GRSNetworkLong
- (void)writeToStream:(NSMutableData *)stream :(GRSDataObject *)data {
    NSNumber* src = [data valueForKey:self.name];
    [stream appendData:stringToData([NSString stringWithFormat:@"%ld", [src longValue]])];
}
- (BOOL)readToData:(GRSDataObject *)data :(NSInputStream *)stream {
    NSString* str = readUntill(stream, @",]", 0, nil);
    NSNumber *val = [NSNumber numberWithLongLong: [str longLongValue]];
    [data setValue:val forKey: self.name];
    return TRUE;
}
@end

@implementation GRSNetworkFloat
- (void)putHeader:(NSMutableString *)stream { [stream appendFormat:@"%@:n(8)", self.name]; }

- (void)writeToStream:(NSMutableData *)stream :(GRSDataObject *)data {
    NSNumber* src = [data valueForKey:self.name];
    [stream appendData:stringToData([NSString stringWithFormat:@"%.8f", [src doubleValue]])];
}
- (BOOL)readToData:(GRSDataObject *)data :(NSInputStream *)stream {
    NSString* str = readUntill(stream, @",]", 0, nil);
    NSNumber *val = [NSNumber numberWithFloat: [str floatValue]];
    [data setValue:val forKey: self.name];
    return TRUE;
}
@end

@implementation GRSNetworkBinary
- (void)putHeader:(NSMutableString *)stream { [stream appendFormat:@"%@:b", self.name]; }

- (void)writeToStream:(NSMutableData *)stream :(GRSDataObject *)data {
    NSData* fd = nil;
    NSString* src = [data valueForKey:self.name];
    if(src) {
        NSFileManager *fm = [[NSFileManager alloc] init];
        fd = [fm contentsAtPath:src];
    }
    if(!fd || fd.length == 0) {
        [stream appendData:stringToData(@"0:")];
    } else {
        [stream appendData:stringToData([NSString stringWithFormat:@"%u:", (unsigned)fd.length])];
        [stream appendData:fd];
        if( (fd.length % 2) != 0) {
            void* buf[] = { 0 };
            [stream appendBytes:buf length:1];
        }
    }
    
}
- (BOOL)readToData:(GRSDataObject *)data :(NSInputStream *)stream {
    NSString* str = readUntill(stream, @":", 0, nil);
    unsigned size = (unsigned)[str integerValue];
    if(size > 0) {
//        @throw ([NSException excep])
        NSMutableData* outData = [NSMutableData dataWithLength:size];
        [stream read:[outData mutableBytes] maxLength:size];
        if( (size % 2) != 0) {
            uint8_t tbf[1] = {};
            [stream read:tbf maxLength:1];
        }
        readSymbol(stream);
    }
    return TRUE;
}
@end

static NSDateFormatter* networkDateFormat = nil;
@implementation GRSNetworkDate {
    NSDateFormatter* readFormat;
}
- (instancetype)init:(NSString *)name dataClass:(Class)dataClass {
    self = [super init:name dataClass:dataClass];
    if(self) {
        readFormat = [[NSDateFormatter alloc] init];
        readFormat.locale = [NSLocale localeWithLocaleIdentifier:@"en_US_POSIX"];
        readFormat.dateFormat = @"dd/MM/yyyy HH:mm:ss";
    }
    return self;
}
- (void)putHeader:(NSMutableString *)stream { [stream appendFormat:@"%@:dt", self.name]; }

- (void)writeToStream:(NSMutableData *)stream :(GRSDataObject *)data {
    if(!networkDateFormat) {
        networkDateFormat = [[NSDateFormatter alloc] init];
        networkDateFormat.locale = [NSLocale localeWithLocaleIdentifier:@"en_US_POSIX"];
        networkDateFormat.dateFormat = @"dd/MM/yyyy HH:mm:ss";
    }
    NSDate* src = [data valueForKey:self.name];
    NSString* strDate = [networkDateFormat stringFromDate:src];
    [stream appendData:stringToData(strDate)];
}
- (void)setDateReader {
    readFormat.dateFormat = @"dd/MM/yyyy";
}
- (void)setTimeReader {
    readFormat.dateFormat = @"HH:mm:ss";
}
- (BOOL)readToData:(GRSDataObject *)data :(NSInputStream *)stream {
    NSString* str = readUntill(stream, @",]", 0, nil);
    NSDate* val = [readFormat dateFromString:str];
    [data setValue:val forKey: self.name];
    return TRUE;
}
@end

@implementation GRSNetworkArray {
    NSArray* fields;
    NSMutableArray* readFields;
    Class itemClass;
}

- (void) createBinder: (Class) dataClass {
    fields = [[NSMutableArray alloc] init];
    NSDictionary* dataFields = [dataClass getFields];
    NSSet* binaryFields = [dataClass binaryFields];
    for(NSString* fieldName in dataFields) {
        GRSNetworkField* fieldHeader = nil;
        FieldType ft = (FieldType)[dataFields[fieldName] intValue];
        switch(ft) {
        case ftString:
            if( [binaryFields member:fieldName] ) fieldHeader = [[GRSNetworkBinary alloc] init:fieldName dataClass:dataClass];
            else fieldHeader = [[GRSNetworkString alloc] init:fieldName dataClass:dataClass];
            break;
        case ftInteger:
            fieldHeader = [[GRSNetworkInteger alloc] init:fieldName dataClass:dataClass];
            break;
        case ftLong:
            fieldHeader = [[GRSNetworkLong alloc] init:fieldName dataClass:dataClass];
            break;
        case ftFloat:
            fieldHeader = [[GRSNetworkFloat alloc] init:fieldName dataClass:dataClass];
            break;
        case ftDate:
            fieldHeader = [[GRSNetworkDate alloc] init:fieldName dataClass:dataClass];
            break;
        case ftArray:
            fieldHeader = [[GRSNetworkArray alloc] init:fieldName dataClass:dataClass];
            break;
        default: break;
        }
        
        if(fieldHeader) [(NSMutableArray*)fields addObject:fieldHeader];
    }
}

- (instancetype)init:(NSString *)name dataClass:(Class)dataClass {
    self = [super init:name dataClass:dataClass];
    if(self) {
        Class itemClass = [dataClass getItemType:name];
        if(itemClass) {
            [self createBinder:itemClass];
        }
    }
    return self;
}

- (instancetype)initWrite:(NSString *)objectSendName dataClass:(Class)dataClass {
    self = [super init:objectSendName dataClass:dataClass];
    if(self) {
        [self createBinder:dataClass];
    }
    return self;
}

-(BOOL)prepareReadFields:(NSInputStream *)stream forDataClass:(Class)dataClass {
    BOOL ret = TRUE;
    
    itemClass = dataClass;
    readFields = [[NSMutableArray alloc] init];
    while(TRUE) {
        NSString* endSym;
        NSString* fsym = readSymbol(stream);
        if(!fsym || [fsym isEqualToString:@"]"]) break;
        
        NSMutableString* field = [NSMutableString stringWithString:fsym];
        NSString* tfld = readUntill(stream, @":[", 0, &endSym);
        if(!tfld) {
            ret = FALSE;
            break;
        }
        
        [field appendString:tfld];
        GRSNetworkField* curField = nil;
        for(GRSNetworkField* fld in fields) {
            if( [fld.name isEqualToString:field]) {
                curField = fld;
                break;
            }
        }
        if( [endSym isEqualToString:@"["]) {
            if(!curField || ![curField isKindOfClass:[GRSNetworkArray class]]) {
                curField = [[GRSNetworkArray alloc] initRead:stream forDataClass:nil];
            } else {
                Class iclass = [dataClass getItemType:field];
                [(GRSNetworkArray*)curField prepareReadFields:stream forDataClass:iclass];
            }
        } else {
            NSString* ftype = readUntill(stream, @",]", 0, &endSym);
            if(!curField) {
                curField = [[GRSNetworkEmptyField alloc] init:field readType:ftype];
            } else {
                if( [curField isKindOfClass:[GRSNetworkDate class]]) {
                    if([ftype isEqualToString:@"t"]) {
                        [(GRSNetworkDate*)curField setTimeReader];
                    } else if ([ftype isEqualToString:@"d"]) {
                        [(GRSNetworkDate*)curField setDateReader];
                    }
                }
            }
        }
        [readFields addObject:curField];
        if([endSym isEqualToString:@"]"]) break;
    }
    return ret;
}

- (instancetype)initRead:(NSInputStream *)stream forDataClass:(Class)dataClass {
    self = [super init:@"" dataClass:dataClass];
    if(self) {
        if( dataClass)
            [self createBinder:dataClass];
        else
            fields = [[NSMutableArray alloc] init];
        if( ![self prepareReadFields:stream forDataClass:dataClass]) self = nil;
    }
    return self;
}

- (void)putHeader:(NSMutableString *)stream {
    NSMutableString* tStream = [NSMutableString stringWithString:self.name];
    [tStream appendString:@"["];
    BOOL first = TRUE;
    for(GRSNetworkField *hdItem in fields) {
        if(first) first = FALSE;
        else [tStream appendString:@","];

        [hdItem putHeader:tStream];
    }
    [tStream appendString:@"]"];    
    [stream appendString:tStream];
}

- (void)writeToStream:(NSMutableData *)stream :(GRSDataObject *)object {
    NSMutableData *idata = [NSMutableData dataWithData:stringToData(@"[")];
    
    BOOL first = TRUE;
    for(GRSNetworkField *hdItem in fields) {
        if(first) first = FALSE;
        else [idata appendData:stringToData(@",")];
        
        [hdItem writeToStream:idata :object];
    }
    
    [idata appendData:stringToData(@"]")];
    [stream appendData:idata];
}

- (GRSDataObject *)read:(GRSMemoryStream*)stream {
    GRSDataObject* outItem = itemClass ? [[itemClass alloc] init] : nil;
    for(GRSNetworkField* f in readFields) {
        if([f isKindOfClass:[GRSNetworkArray class]]) {
            NSMutableArray* vala = [[NSMutableArray alloc] init];
            while(TRUE) {
                NSString* sym = readSymbol(stream);
                if(![sym isEqualToString:@"["]) break;
                GRSDataObject* obj = [(GRSNetworkArray*)f read:stream];
                if(obj) [vala addObject:obj];
            }
            if(outItem) {
                [outItem setValue:vala forKey:f.name];
            }
        } else {
            [f readToData:outItem :stream];
        }
    }
    return outItem;
}
@end


static NSMutableData* compressing(NSMutableData* src) {
    z_stream strm;
    
    strm.zalloc = Z_NULL;
    strm.zfree = Z_NULL;
    strm.opaque = Z_NULL;
    strm.total_out = 0;
    strm.next_in=(Bytef *)[src bytes];
    strm.avail_in = (unsigned)src.length;
    
    if (deflateInit(&strm, Z_DEFAULT_COMPRESSION) != Z_OK) return nil;
    
    // 16K chuncks for expansion
    NSMutableData *compressed = [NSMutableData dataWithLength:16384];
    
    int res = Z_OK;
    do {
        if (strm.total_out >= [compressed length]) [compressed increaseLengthBy: 16384];
        
        strm.next_out = [compressed mutableBytes] + strm.total_out;
        strm.avail_out = (unsigned)(compressed.length - strm.total_out);
        
        res = deflate(&strm, Z_FINISH);
        if(res != Z_OK && res != Z_STREAM_END) break;
    } while (res != Z_STREAM_END);
//    } while (strm.avail_out == 0);
    
    deflateEnd(&strm);
    
    [compressed setLength: strm.total_out];
    return compressed;
}

static NSMutableData* decompress(NSMutableData* src, unsigned length) {
    NSMutableData* outData = [NSMutableData dataWithLength:length];

    z_stream strm;
    strm.next_in = (Bytef *)[src bytes];
    strm.avail_in = (uint)[src length];
    strm.total_out = 0;
    strm.zalloc = Z_NULL;
    strm.zfree = Z_NULL;
    
    inflateInit (&strm);
    
    strm.avail_out = length;
    strm.next_out = [outData mutableBytes];
    
    BOOL done = (inflate(&strm, Z_SYNC_FLUSH) == Z_STREAM_END);
 
    inflateEnd(&strm);
    return done ? outData : nil;
}

@implementation GRSConnectionData
- (instancetype)init:(NSString *)address :(unsigned int)port {
    self = [super init];
    if(self) {
        self.address = address;
        self.port = port;
    }
    return self;
}
@end

@interface GRSNetworkExhange (NetworkInternal)
-(void) completeConnect:(GRSConnectOp*)operator;
@end

@interface GRSConnectOp : NSOperation {
    NSData* packet;
}
@property NSInputStream* input;
@property NSOutputStream* output;
@property NSString* address;
@property unsigned port;
@property BOOL hasError;

- (instancetype) init: (GRSNetworkExhange*) owner to: (NSString*) address port: (unsigned)port withPacket:(NSData*)packet;
- (void)completeOperation;
@end

@implementation GRSConnectOp {
    GRSNetworkExhange* owner;
    BOOL executing;
    BOOL finished;
}

- (instancetype)init:(GRSNetworkExhange *)_owner to: (NSString*)_address port: (unsigned)_port withPacket:(NSData*)_packet {
    self = [super init];
    if(self) {
        executing = FALSE;
        finished = FALSE;
        self.hasError = FALSE;

        owner = _owner;
        self.address = _address;
        self.port = _port;
        packet = _packet;
    }
    return self;
}

- (BOOL)isConcurrent { return TRUE; }
- (BOOL)isExecuting { return executing; }
- (BOOL)isFinished { return finished; }

- (void)start {
    // Always check for cancellation before launching the task.
    if ([self isCancelled]) {
        // Must move the operation to the finished state if it is canceled.
        [self willChangeValueForKey:@"isFinished"];
        finished = YES;
        [self didChangeValueForKey:@"isFinished"];
        return;
    }
    
    // If the operation is not canceled, begin executing the task.
    [self willChangeValueForKey:@"isExecuting"];
    [NSThread detachNewThreadSelector:@selector(main) toTarget:self withObject:nil];
    executing = YES;
    [self didChangeValueForKey:@"isExecuting"];
}

- (void)main {
    @try {
        
        CFReadStreamRef readStream;
        CFWriteStreamRef writeStream;

        CFStreamCreatePairWithSocketToHost(nil, (__bridge CFStringRef)self.address, self.port, &readStream, &writeStream);
        self.input = (__bridge NSInputStream*)readStream;
        self.output = (__bridge NSOutputStream*)writeStream;
        
        [self.input open];
        [self.output open];
        
        const void* p = [packet bytes];
        unsigned size = (unsigned)packet.length;
        
        while(![self isCancelled] && size > 0) {
            if([self.output hasSpaceAvailable]) {
                int writed = (int)[self.output write: p maxLength:size];
                if(writed < 0) {
                    self.hasError = TRUE;
                    break;
                }
                p += writed;
                size -= writed;
            } else if([self.output streamStatus] == NSStreamStatusError) {
                self.hasError = TRUE;
                break;
            }
        }
        
        while(![self isCancelled] && !self.hasError && ![self.input hasBytesAvailable]) {
            if([self.input streamStatus] == NSStreamStatusError) {
                self.hasError = TRUE;
                break;
            }
            [NSThread sleepForTimeInterval:WAIT_SLEEP];
        }
        
        
        if(![self isCancelled]) {
            [owner completeConnect:self];
        }
        [self completeOperation];
    }
    @catch(...) {
        // Do not rethrow exceptions.
    }
}

- (void)completeOperation {
    [self willChangeValueForKey:@"isFinished"];
    [self willChangeValueForKey:@"isExecuting"];
    
    executing = NO;
    finished = YES;
    
    [self.input close];
    [self.output close];
    
    [self didChangeValueForKey:@"isExecuting"];
    [self didChangeValueForKey:@"isFinished"];
}
@end

@implementation GRSMemoryStream {
    const uint8_t* bytes;
    NSInteger len;
}

- (instancetype)initStream:(NSData *)data {
    GRSMemoryStream* ret = [super init];
    if(ret) {
        ret.curPos = 0;
        bytes = (const uint8_t*)[data bytes];
        len = data.length;
    }
    return ret;
}

- (NSInteger)read:(uint8_t *)buffer maxLength:(NSUInteger)_len {
    if(self.curPos + _len > len ) _len = len - self.curPos;
    if(_len <= 0) return 0;
    
    memcpy(buffer, bytes + self.curPos, _len);
    self.curPos += _len;
    return _len;
}

- (void)open { }
- (void)close { }
- (id<NSStreamDelegate>)delegate { return nil; }
- (void)setDelegate:(id<NSStreamDelegate>)delegate { nil; }
- (void)scheduleInRunLoop:(NSRunLoop *)aRunLoop forMode:(NSRunLoopMode)mode { }
- (void)removeFromRunLoop:(NSRunLoop *)aRunLoop forMode:(NSRunLoopMode)mode { }
- (id)propertyForKey:(NSStreamPropertyKey)key { return nil; }
- (BOOL)setProperty:(id)property forKey:(NSStreamPropertyKey)key { return FALSE; }
- (NSStreamStatus)streamStatus { return NSStreamStatusOpen; }
- (NSError *)streamError { return nil; }

- (BOOL)getBuffer:(uint8_t * _Nullable *)buffer length:(NSUInteger *)_len {
    if(self.curPos >= len) return FALSE;
    *buffer = (uint8_t*)(bytes + self.curPos);
    *_len = len - self.curPos;
    return TRUE;
//    return [stream getBuffer:buffer length:len];
}
- (BOOL)hasBytesAvailable { return (self.curPos < len); }

- (NSString *)nextSym {
    if(len - self.curPos >= 2) {
        uint8_t buf[4] = {0xff,0xfe,bytes[self.curPos],bytes[self.curPos + 1]};
        return [[NSString alloc] initWithBytes:buf length:4 encoding:NSUTF16StringEncoding];
    }
    return nil;
}

@end

@interface GRSByeData : NSObject <GRSObjectsToSend>
@end

@implementation GRSByeData
- (NSString *)name { return @"ServerCommand"; }
- (int)count { return 1; }
- (GRSDataObject *)get:(int)index {
    if(index > 0) return nil;
    
    GRSServerCommand* cmd = [[GRSByeCommand alloc] init];
    return cmd;
}
@end

@interface GRSAnswerHandler : NSObject <GRSNetworkPacketHandler>
@end

@implementation GRSAnswerHandler

- (BOOL)canHandleObject:(NSString *)objName { return [objName isEqualToString:@"ServerAnswer"]; }
- (Class)objectType { return [GRSServerAnswer class]; }
- (void)onEnd {}

- (void)onObjectReaded:(GRSDataObject *)object withError:(NSString *__autoreleasing *)error {
    GRSServerAnswer* obj = (GRSServerAnswer*)object;
    if(!obj.response) {
        *error = obj.message;
    }
}

@end

@implementation GRSNetworkExhange {
    BOOL connectCompleete;
    dispatch_semaphore_t connectSem;
    NSOperationQueue* queue;
}

+(NSData*) makePacket :(NSArray<GRSObjectsToSend>*) objects compressing:(BOOL)compress {
    NSMutableData *stream = [NSMutableData data];
    
    for(id<GRSObjectsToSend> objList in objects) {
        int count = [objList count];
        GRSNetworkArray *root = nil;
        
        for(int i=0; i<count; i++) {
            GRSDataObject* object = [objList get:i];
            if(i == 0) {;
                root = [[GRSNetworkArray alloc] initWrite:[objList name] dataClass:[object class]];
                NSMutableString* header = [[NSMutableString alloc] init];
                [root putHeader:header];
                [stream appendData:stringToData(header)];
            }
            [root writeToStream:stream :object];
        }
    }
    
    NSMutableString* header = [[NSMutableString alloc] init];
    if(compress) {
        [header appendFormat:@"GZIP(%d);", (int)stream.length];
        stream = compressing(stream);
    } else {
        int crc = (int)crc32(0xFFFFFFFF, stream.bytes, (uint)stream.length);
        [header appendFormat:@"CRC(%d);", crc];
    }
    [header insertString:[NSString stringWithFormat:@"GRPACKET(%d);", (int)stream.length] atIndex:0];
    [header appendString:@"DATA;"];
    
    NSData* hdr = stringToData(header);
    NSMutableData* outPacket = [NSMutableData dataWithData:hdr];
    [outPacket appendData:stream];
    return outPacket;
}

- (instancetype)init
{
    self = [super init];
    if (self) {
        self.address = nil;
        self.port = 0;
        self.input = nil;
        self.output = nil;
    }
    return self;
}

-(BOOL) connecting: (NSArray*)address : (NSData*)data {
    connectCompleete = FALSE;
    connectSem = dispatch_semaphore_create(0);
    
    queue = [[NSOperationQueue alloc] init];
    
    for(GRSConnectionData* cd in address) {
        NSOperation* op = [[GRSConnectOp alloc] init:self to:cd.address port:cd.port withPacket:data];
        [queue addOperation:op];
    }
    
    // wait 1 min
    dispatch_time_t wait = dispatch_time(DISPATCH_TIME_NOW, WAIT_CONNECT);
    dispatch_semaphore_wait(connectSem, wait);

    queue.suspended = TRUE;
    [queue cancelAllOperations];
    queue.suspended = FALSE;
    queue = nil;
    connectSem = nil;
    
    return (self.input != nil);
}

- (void)close {
    [self.input close];
    [self.output close];
}

-(int) getValueFromOpt:(NSString*) opt {
    NSRange start = [opt rangeOfString:@"("];
    NSRange end = [opt rangeOfString:@")"];
    if(start.location == NSNotFound) start.location = 0;
    else start.location++;
    start.length = (end.location != NSNotFound) ? end.location - start.location : opt.length - start.location - 1;
    
    return (int)[[opt substringWithRange:start] integerValue];
}

- (NSData *)readingPacket:(id<GRSNetworkProgress>)progress errorText:(NSString **)error {
    if(!self.input) return nil;
    
    if(!waitData(self.input, WAIT_READ)) return nil;
    
    NSData* pktHeader = stringToData(PACKET_TAG);
    NSMutableData* chk = [NSMutableData dataWithLength:pktHeader.length];
    
    int res = (int)[self.input read:[chk mutableBytes] maxLength:chk.length];
    if(res < 0 || res < chk.length) return nil;
    if( ![pktHeader isEqualToData:chk] ) return nil;
    

    unsigned pktLen = 0;
    BOOL first = TRUE;
    BOOL err = FALSE;
    NSMutableArray* options = [[NSMutableArray alloc] init];
    while(!err) {
        NSString* opt = readUntill(self.input, @";", WAIT_READ, nil);
        if(!opt) {
            err = TRUE;
            break;
        }
        if([opt isEqualToString:DATA_TAG]) break;
        if(first) {
            pktLen = [self getValueFromOpt:opt];
            first = FALSE;
        } else
            [options addObject:opt];
    }
    if(err || options.count < 1 || pktLen == 0) return nil;

    NSMutableData* data = [NSMutableData dataWithLength:pktLen];

    [progress setMax:pktLen];
    if(!readData(self.input, [data mutableBytes], pktLen, WAIT_READ, progress)) return nil;
    
    for(int i=(int)options.count - 1; data && i >= 0; i--) {
        NSString* op = options[i];
        if( [op hasPrefix:GZIP_TAG]) {
            unsigned lenght = [self getValueFromOpt:op];
            data = decompress(data, lenght);
        } else if([op hasPrefix:CRC_TAG]) {
            int crc = [self getValueFromOpt:op];
            int mycrc = (int)crc32(0, data.mutableBytes, (unsigned)data.length);
            if(crc != mycrc) {
                data = nil;
            }
        }
    }
    return data;
}

- (BOOL)parsePacket:(NSData *)packet handlers:(NSArray<GRSNetworkPacketHandler>*)_handlers
           progress:(id<GRSNetworkProgress>)progress errorText:(NSString**)error {

    [progress setMax:(int)packet.length];
    GRSMemoryStream *inStr = [[GRSMemoryStream alloc] initStream:packet];
    @try {
        [inStr open];
    } @catch(NSException* e) {
        NSLog(@"Exception %@", e.reason);
    }

    NSMutableArray* handlers = [[NSMutableArray alloc] initWithArray:_handlers];
    GRSAnswerHandler* ansH = [[GRSAnswerHandler alloc] init];
    [handlers addObject:ansH];
    
    BOOL err = FALSE;
    while(!err) {
        if(![inStr hasBytesAvailable]) break;
        
        NSString* objName = readUntill(inStr, @"[", 0, nil);
        if(!objName) {
            err = TRUE;
            break;
        }
        id<GRSNetworkPacketHandler> handler = nil;
        for(id<GRSNetworkPacketHandler> h in handlers) {
            if( [h canHandleObject:objName] ) {
                handler = h;
                [progress setObjName:objName];
                break;
            }
        }
        GRSNetworkArray *reader = [[GRSNetworkArray alloc] initRead:inStr forDataClass: [handler objectType]];
        readSymbol(inStr); // skip [
        while(TRUE) {
            GRSDataObject *obj = [reader read:inStr];
            if(obj && handler) [handler onObjectReaded:obj withError:error];

            [progress setCurrent:(int)inStr.curPos];

            NSString* nextString = [inStr nextSym];
            if(!nextString || ![nextString isEqualToString:@"["]) {
                break;
            }
            readSymbol(inStr);
        }
    }
    [inStr close];
    
    return !err;
}

- (BOOL)sendingPacket:(NSData *)packet progress:(id<GRSNetworkProgress>)progress errorText:(NSString **)error {
    if(!self.output) return FALSE;
    
    BOOL hasError = FALSE;
    const void* p = [packet bytes];
    unsigned cp = 0;
    unsigned size = (unsigned)packet.length;
    
    [progress setMax:size];
    
    while(size > 0) {
        if([self.output hasSpaceAvailable]) {
            int writed = (int)[self.output write: p maxLength:size];
            if(writed < 0) {
                hasError = TRUE;
                break;
            }
            p += writed;
            size -= writed;
            cp += writed;
            
            [progress setCurrent:cp];
        } else if([self.output streamStatus] == NSStreamStatusError) {
            hasError = TRUE;
            break;
        }
    }
    
    return !hasError;
}

- (void)sendByeCommand {
    GRSByeData* bd = [[GRSByeData alloc] init];
    NSArray<GRSObjectsToSend> *objSend = (NSArray<GRSObjectsToSend>*)@[bd];
    NSData* data = [GRSNetworkExhange makePacket:objSend compressing:TRUE];
    NSString *err;
    [self sendingPacket:data progress:nil errorText:&err];
}

@end

@implementation GRSNetworkExhange (NetworkInternal)

- (void)completeConnect:(GRSConnectOp*)operator {
    @synchronized (self) {
        if(!connectCompleete) {
            if(!operator.hasError || [queue operationCount] <= 1) {
                connectCompleete = TRUE;
                
                if(!operator.hasError) {
                    self.address = operator.address;
                    self.port = operator.port;
                    
                    self.input = operator.input;
                    self.output = operator.output;
                    
                    operator.output = nil;
                    operator.input = nil;
                }
                dispatch_semaphore_signal(connectSem);
            }
        }
    }
}

@end
