//
//  network_packet.m
//  NapoleonCore
//
//  Created by Denis Mosyagin on 08.09.2021.
//

#import <Foundation/Foundation.h>
#import <zlib.h>
#import "core.h"
#import "network_packet.h"

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

static NSString* peakSym;
static NSString* readSymbol(NSInputStream* stream) {
    if(peakSym) {
        NSString* tstr = peakSym;
        peakSym = nil;
        return tstr;
    }
    
    uint8_t buf[4] = {0xff,0xfe,0,0};
    if([stream read:buf+2 maxLength:2] < 2) {
        return nil;
    }
    return [[NSString alloc] initWithBytes:buf length:4 encoding:NSUTF16StringEncoding];
}

BOOL waitData(NSInputStream* stream, uint32_t ms) {
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

BOOL readData(NSInputStream* stream, void* buffer, unsigned length, uint32_t ms, void (^progress)(unsigned)) {
    int pos = 0;
    progress(pos);
    while(length > 0) {
        if(!waitData(stream, ms)) return FALSE;
        
        NSUInteger size = [stream read:buffer maxLength:length];
        if(size < 0) return FALSE;
        
        length -= size;
        buffer += size;
        pos += size;
        progress(pos);
    }
    return TRUE;
}
NSString* readUntill(NSInputStream* stream, NSString* syms, uint32_t ms, NSString** endStr) {
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

NSData* stringToData(NSString* str) {
    NSData* data = [str dataUsingEncoding:NSUTF16StringEncoding];
    NSRange range = NSMakeRange(2, [data length] - 2);
    return [data subdataWithRange:range];
}

@interface GRSNetworkField : NSObject
@property NSString* name;
@property NSString* serverName;

-(instancetype) init : (NSString*) name dataClass: (Class) dataClass;
-(void) putHeader:(NSMutableString*) stream;
-(void) writeToStream:(NSMutableData*) stream : (GRSObject*) data;
-(BOOL) readToData:(GRSObject*) data : (NSInputStream*) stream;
@end

@interface GRSNetworkEmptyField : GRSNetworkField
-(instancetype) init : (NSString*) name readType: (NSString*) readType;
@end

@interface GRSNetworkString : GRSNetworkField
@end

@interface GRSNetworkInteger : GRSNetworkField
-(instancetype) init : (NSString*) name dataClass: (Class) dataClass signedValue:(BOOL)isSigned;
@end

@interface GRSNetworkLong : GRSNetworkInteger
-(instancetype) init : (NSString*) name dataClass: (Class) dataClass signedValue:(BOOL)isSigned;
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
-(GRSObject*) read: (NSInputStream*) stream;
@end

@interface GRSNetworkBinary : GRSNetworkField
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

@implementation GRSNetworkField
- (instancetype)init:(NSString *)name dataClass:(Class)dataClass {
    self = [super init];
    if(self) {
        self.name = name;
        self.serverName = name;
    }
    return self;
}
- (void)putHeader:(NSMutableString *)stream { }
- (void)writeToStream:(NSMutableData *)stream :(GRSObject *)data {}
- (BOOL)readToData:(GRSObject *)data :(NSInputStream *)stream { return FALSE; }
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

- (BOOL)readToData:(GRSObject *)data :(NSInputStream *)stream {
    [inField readToData:nil :stream];
    return TRUE;
}
@end

@implementation GRSNetworkString
- (void)putHeader:(NSMutableString *)stream { [stream appendFormat:@"%@:s", self.serverName]; }

- (void)writeToStream:(NSMutableData *)stream :(GRSObject *)data {
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

- (BOOL)readToData:(GRSObject *)data :(NSInputStream *)stream {
    NSString* outStr = readString(stream);
    [data setValue:outStr forKey: self.name];
    readSymbol(stream);
    return TRUE;
}
@end

@implementation GRSNetworkInteger {
    BOOL _isSigned;
}
-(instancetype) init : (NSString*) name dataClass: (Class) dataClass signedValue:(BOOL)isSigned{
    self = [super init:name dataClass:dataClass];
    if(self) {
        _isSigned = isSigned;
    }
    return self;
}

- (void)putHeader:(NSMutableString *)stream { [stream appendFormat:@"%@:n", self.serverName]; }

- (void)writeToStream:(NSMutableData *)stream :(GRSObject *)data {
    NSNumber* src = [data valueForKey:self.name];
    if(_isSigned)
        [stream appendData:stringToData([NSString stringWithFormat:@"%d", [src intValue]])];
    else
        [stream appendData:stringToData([NSString stringWithFormat:@"%u", [src unsignedIntValue]])];
}
- (BOOL)readToData:(GRSObject *)data :(NSInputStream *)stream {
    NSString* str = readUntill(stream, @",]", 0, nil);
    NSNumber *val = [NSNumber numberWithInteger: [str integerValue]];
    [data setValue:val forKey: self.name];
    return TRUE;
}
@end

@implementation GRSNetworkLong {
    BOOL _isSigned;
}
-(instancetype) init : (NSString*) name dataClass: (Class) dataClass signedValue:(BOOL)isSigned {
    self = [super init:name dataClass:dataClass];
    if(self) {
        _isSigned = isSigned;
    }
    return self;
}
- (void)writeToStream:(NSMutableData *)stream :(GRSObject *)data {
    NSNumber* src = [data valueForKey:self.name];
    if(_isSigned)
        [stream appendData:stringToData([NSString stringWithFormat:@"%lld", [src longLongValue]])];
    else
        [stream appendData:stringToData([NSString stringWithFormat:@"%llu", [src unsignedLongLongValue]])];
}
- (BOOL)readToData:(GRSObject *)data :(NSInputStream *)stream {
    NSString* str = readUntill(stream, @",]", 0, nil);
    NSNumber *val = [NSNumber numberWithLongLong: [str longLongValue]];
    [data setValue:val forKey: self.name];
    return TRUE;
}
@end

@implementation GRSNetworkFloat
- (void)putHeader:(NSMutableString *)stream { [stream appendFormat:@"%@:n(8)", self.serverName]; }

- (void)writeToStream:(NSMutableData *)stream :(GRSObject *)data {
    NSNumber* src = [data valueForKey:self.name];
    [stream appendData:stringToData([NSString stringWithFormat:@"%.8f", [src doubleValue]])];
}
- (BOOL)readToData:(GRSObject *)data :(NSInputStream *)stream {
    NSString* str = readUntill(stream, @",]", 0, nil);
    NSNumber *val = [NSNumber numberWithFloat: [str floatValue]];
    [data setValue:val forKey: self.name];
    return TRUE;
}
@end

@implementation GRSNetworkBinary
- (void)putHeader:(NSMutableString *)stream { [stream appendFormat:@"%@:b", self.serverName]; }

- (void)writeToStream:(NSMutableData *)stream :(GRSObject *)data {
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
- (BOOL)readToData:(GRSObject *)data :(NSInputStream *)stream {
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
        readFormat.dateFormat = @"yyyy/MM/dd HH:mm:ss";
    }
    return self;
}
- (void)putHeader:(NSMutableString *)stream { [stream appendFormat:@"%@:dt", self.serverName]; }

- (void)writeToStream:(NSMutableData *)stream :(GRSObject *)data {
    if(!networkDateFormat) {
        networkDateFormat = [[NSDateFormatter alloc] init];
        networkDateFormat.locale = [NSLocale localeWithLocaleIdentifier:@"en_US_POSIX"];
        networkDateFormat.dateFormat = @"yyyy/MM/dd HH:mm:ss";
    }
    NSDate* src = [data valueForKey:self.name];
    NSString* strDate = [networkDateFormat stringFromDate:src];
    [stream appendData:stringToData(strDate)];
}
- (void)setDateReader {
    readFormat.dateFormat = @"yyyy/MM/dd";
}
- (void)setTimeReader {
    readFormat.dateFormat = @"HH:mm:ss";
}
- (BOOL)readToData:(GRSObject *)data :(NSInputStream *)stream {
    NSString* str = readUntill(stream, @",]", 0, nil);
    NSDate* val = [readFormat dateFromString:str];
    [data setValue:val forKey: self.name];
    return TRUE;
}
@end

@implementation GRSNetworkArray {
    NSArray* objectFields;
    NSMutableArray* matchFields;
    Class itemClass;
}

- (void) createBinder: (Class) dataClass {
    objectFields = [[NSMutableArray alloc] init];
    NSDictionary* dataFields = [dataClass getFields];
    NSDictionary* aliases = [dataClass aliases];
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
        case ftUnsigned:
            fieldHeader = [[GRSNetworkInteger alloc] init:fieldName dataClass:dataClass signedValue:(ft == ftInteger)];
            break;
        case ftLong:
        case ftUnsignedLong:
            fieldHeader = [[GRSNetworkLong alloc] init:fieldName dataClass:dataClass signedValue:(ft == ftLong)];
            break;
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
        
        if(fieldHeader) {
            NSString* alias = aliases[fieldName];
            if(alias != nil) fieldHeader.serverName = alias;
            [(NSMutableArray*)objectFields addObject:fieldHeader];
        }
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
    matchFields = [[NSMutableArray alloc] init];
    while(TRUE) {
        NSString* endSym;
        NSString* fsym = readSymbol(stream);
        if(!fsym || [fsym isEqualToString:@"]"]) break;
        
        NSMutableString* formatField = [NSMutableString stringWithString:fsym];
        NSString* tfld = readUntill(stream, @":[", 0, &endSym);
        if(!tfld) {
            ret = FALSE;
            break;
        }
        
        [formatField appendString:tfld];
        GRSNetworkField* curField = nil;
        for(GRSNetworkField* fld in objectFields) {
            if( [fld.serverName isEqualToString:formatField]) {
                curField = fld;
                break;
            }
        }
        if( [endSym isEqualToString:@"["]) {
            if(!curField || ![curField isKindOfClass:[GRSNetworkArray class]]) {
                curField = [[GRSNetworkArray alloc] initRead:stream forDataClass:nil];
            } else {
                Class iclass = [dataClass getItemType:formatField];
                [(GRSNetworkArray*)curField prepareReadFields:stream forDataClass:iclass];
                fsym = readSymbol(stream);
                if(!fsym)
                    break;
                endSym = fsym;
            }
        } else {
            NSString* ftype = readUntill(stream, @",]", 0, &endSym);
            if(!curField) {
                curField = [[GRSNetworkEmptyField alloc] init:formatField readType:ftype];
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
        [matchFields addObject:curField];
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
            objectFields = [[NSMutableArray alloc] init];
        if( ![self prepareReadFields:stream forDataClass:dataClass]) self = nil;
    }
    return self;
}

- (void)putHeader:(NSMutableString *)stream {
    NSMutableString* tStream = [NSMutableString stringWithString:self.serverName];
    [tStream appendString:@"["];
    BOOL first = TRUE;
    for(GRSNetworkField *hdItem in objectFields) {
        if(first) first = FALSE;
        else [tStream appendString:@","];

        [hdItem putHeader:tStream];
    }
    [tStream appendString:@"]"];
    [stream appendString:tStream];
}

- (void)writeToStream:(NSMutableData *)stream :(GRSObject *)object {
    NSMutableData *idata = [NSMutableData dataWithData:stringToData(@"[")];
    
    BOOL first = TRUE;
    for(GRSNetworkField *hdItem in objectFields) {
        if(first) first = FALSE;
        else [idata appendData:stringToData(@",")];
        
        [hdItem writeToStream:idata :object];
    }
    
    [idata appendData:stringToData(@"]")];
    [stream appendData:idata];
}

- (GRSObject *)read:(GRSMemoryStream*)stream {
    GRSObject* outItem = itemClass ? [[itemClass alloc] init] : nil;
    for(GRSNetworkField* f in matchFields) {
        if([f isKindOfClass:[GRSNetworkArray class]]) {
            NSMutableArray* vala = [[NSMutableArray alloc] init];
            while(TRUE) {
                NSString* sym = readSymbol(stream);
                if(![sym isEqualToString:@"["]) break;
                
                // special case for empty array [],
                peakSym = readSymbol(stream);
                if([peakSym isEqualToString:@"]"]) {
                    peakSym = nil;
                    peakSym = readSymbol(stream);
                    if([peakSym isEqualToString:@","]) {
                        peakSym = nil;
                    }
                    break;
                }
                GRSObject* obj = [(GRSNetworkArray*)f read:stream];
                if(obj)
                    [vala addObject:obj];
            }
            if(outItem && [f.name length] != 0) {
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

NSMutableData* decompress(NSMutableData* src, unsigned length) {
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

@implementation GRSServerCommand {
    NSString* _impersonate;
    NSString* _command;
}

static NSString* progVersion;
static NSString* progCategory;

+ (void)setProgVersion:(NSString *)version { progVersion = version; }
+ (void)setProgCategory:(NSString *)category { progCategory = category; }
+ (NSString*) name { return @"ServerCommand"; }

-(instancetype)init:(NSString*) command { return [self init: command withParam:@""]; }

-(instancetype)init:(NSString*) command withParam: (NSString*) param {
    self = [super init];
    if(self) {
        self.userid = @"";
        self.password = @"";
        self.category = progCategory;
        self.version = progVersion;
        self.param = param;
        _command = command;
    }
    return self;
}

-(void)setLogin:(GRSLoginData *)login {
    self.userid = login.login;
    self.password = login.password;
    self.category = progCategory;
    self.version = progVersion;
    self.duration = login.duration;
    self.uuid = login.uuid;
    
    _impersonate = login.impersonate;
}


- (NSString*) command {
    return [_impersonate length] == 0 ? _command :
        [NSString stringWithFormat:@"%@ AS '%@'", _command, _impersonate];
}

@end

@implementation GRSGetCommand
- (instancetype)init:(NSString*) objName {
    self = [super init:@"GET"];
    if(self) {
        self.param = objName;
    }
    return self;
}
@end

@implementation GRSSelectCommand
- (instancetype)init:(NSString*) objName withFilter:(NSString*)filter {
    self = [super init:@"SELECT"];
    if(self) {
        self.param = [NSString stringWithFormat:@"%@:%@", objName, filter == nil ? @"" : filter];
    }
    return self;
}
@end

@implementation GRSCommandSender {
    GRSServerCommand* _command;
}

-(instancetype) init: (NSString*) command withParam: (NSString*) param {
    self = [super init];
    if(self) {
        _command = [[GRSServerCommand alloc] init:command withParam:param];
        self.count = 1;
        self.name = GRSServerCommand.name;
    }
    return self;
}

- (instancetype)init: (GRSServerCommand*) serverCommand {
    self = [super init];
    if(self) {
        _command = serverCommand;
        self.count = 1;
        self.name = GRSServerCommand.name;
    }
    return self;
}

-(GRSObject*) get:(int)index { return index > 0 ? nil : _command; }
@end

@implementation GRSByeSend
-(instancetype) init { return [super init: @"BYE" withParam:@""]; }
@end

@implementation GRSDoneSend
-(instancetype) init { return [super init: @"DONE" withParam:@""]; }
@end

@implementation GRSForcePutSend
-(instancetype) init { return [super init: @"FORCE PUT" withParam:@""]; }
@end

@implementation GRSAnswerHandler

-(instancetype) init {
    self = [super init];
    if(self) {
        self.rcvType = [GRSServerAnswer class];
        self.name = @"ServerAnswer";
    }
    return self;
}

- (void) reading: (GRSObject*)object{ self.answer = (GRSServerAnswer*)object; }
@end

@implementation GRSPacketHandler {
    GRSNetworkRouting* _owner;
    GRSReceiverBase* _continueRcvr;
}
-(instancetype) init:(GRSNetworkRouting *)owner {
    self = [super init];
    if(self) {
        _owner = owner;
    }
    return self;
}

-(NSData*) makePacket :(NSArray<GRSSenderBase*>*) objects compressing:(BOOL)compress {
    NSMutableData *stream = [NSMutableData data];
    
    for(GRSSenderBase* objList in objects) {
        unsigned count = [objList count];
        GRSNetworkArray *root = nil;
        
        for(unsigned i=0; i<count; i++) {
            GRSObject* object = [objList get:i];
            if( [object isKindOfClass: GRSServerCommand.class]) {
                GRSServerCommand* cmd = (GRSServerCommand*)object;
                [cmd setLogin: [_owner networkData].login];
            }
            if(i == 0) {
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
//        NSString* tst = [[NSString alloc] initWithData:stream encoding:NSUTF16LittleEndianStringEncoding];
//        NSLog(@"Packet %@", tst);
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

- (BOOL)parsePacket:(NSData *)packet handlers:(NSArray<GRSReceiverBase*>*)_handlers{
    
    [_owner sayDoing:@"Writing data" count:(unsigned)packet.length];
    GRSMemoryStream *inStr = [[GRSMemoryStream alloc] initStream:packet];
    @try {
        [inStr open];
    } @catch(NSException* e) {
        NSLog(@"Exception %@", e.reason);
    }

    NSMutableArray* handlers = [[NSMutableArray alloc] initWithArray:_handlers];
    
    GRSReceiverBase* handler = _continueRcvr;
    BOOL err = FALSE;
    while(!err) {
        if(![inStr hasBytesAvailable]) {
            // handler is nil in continouse reading
            [handler finishing];
            break;
        }
        
        NSString* objName = readUntill(inStr, @"[", 0, nil);
        if(!objName) {
            err = TRUE;
            break;
        }
        GRSReceiverBase* finded;
        for(GRSReceiverBase* h in handlers) {
            if( [h.name isEqualToString:objName] ) {
                finded = h;
                break;
            }
        }

        // add done command to send, read continue answer and repeat
        if(!finded && [objName isEqualToString:@"StreamContiniue"]) {
            _continueRcvr = handler;
            handler = nil;
            [_owner addRequestor: [[GRSDoneSend alloc] init]];
            
        } else {
            if(![finded isEqual:handler]) {
                [handler finishing];
                [_owner removeReceiver:handler];
                [handlers removeObject:finded];
                
                handler = finded;
                [handler starting];
            }
        }
        
//        [_owner sayDoing:objName count:0];
        GRSNetworkArray *reader = [[GRSNetworkArray alloc] initRead:inStr forDataClass: handler.rcvType];
        readSymbol(inStr); // skip [
        while(TRUE) {
            GRSObject *obj = [reader read:inStr];
            if(obj && handler) [handler reading:obj];

            [_owner sayProgress:(unsigned)inStr.curPos];

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

@end

@implementation GRSProbeConnectionObject
-(instancetype) init {
    self = [super init:GRSServerAnswer.class withFilter:@"ServerAnswer"];
    if(self) {
        self.name = @"ObjFormatGet";
    }
    return self;
}
@end
