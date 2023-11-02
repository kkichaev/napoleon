//
//  network.m
//  NapoleonCore
//
//  Created by Denis Mosyagin on 01.09.2021.
//

#import <Foundation/Foundation.h>
#import <zlib.h>
#import "core.h"
#import "network_packet.h"


@implementation GRSSenderBase
-(GRSObject*) get:(int)index { return nil; }
@end

@implementation GRSLoginData
@end

@implementation GRSNetworkData

- (instancetype)init {
    self = super.init;
    if(self) {
        self.login = [[GRSLoginData alloc] init];
    }
    return self;
}

@end

@implementation GRSServerAnswer
- (instancetype)init {
    if(self = [super init]) {
        self.message = @"";
        self.response = 0;
    }
    
    return self;
}
@end

@implementation GRSReceiverBase
- (instancetype) init: (Class) objType {
    self = [super init];
    if(self) {
        if(![objType isSubclassOfClass: GRSObject.class]) {
            self = nil;
        } else {
            self.rcvType = objType;
        }
    }
    return self;
}

- (void) starting {}
- (void) finishing {}
- (void) reading: (GRSObject*)object {}
@end

// combine senders with same name
@interface GRSCombineSender : GRSSenderBase
-(instancetype) init: (GRSSenderBase*) sender;
-(BOOL) add: (GRSSenderBase*) sender;
@end

@implementation GRSCombineSender {
    unsigned _current;
    NSMutableArray<GRSSenderBase*>* _data;
}
-(instancetype) init: (GRSSenderBase*) sender {
    self = [super init];
    if(self) {
        _current = 0;
        _data = [[NSMutableArray alloc] init];
        [_data addObject:sender];

        self.name = sender.name;
        self.count = sender.count;
    }
    return self;
}

-(BOOL) add: (GRSSenderBase*) sender {
    if([sender.name isEqualToString: self.name]) {
        [_data addObject:sender];
        self.count += sender.count;
        return TRUE;
    }
    return FALSE;
}

- (GRSObject *)get:(int)index {
    if([_data count] == 0) return nil;
    GRSSenderBase* sender = [_data objectAtIndex:0];
    if(_current + 1 <= [sender count]) return [sender get: _current++];
    
    [_data removeObject:sender];
    _current = 0;
    return [self get:index];
}
@end

@interface GRSWriteResultHandler : GRSReceiverBase
-(instancetype) init: (GRSObjectSender*)sender;
@end

@implementation GRSWriteResultHandler {
    GRSObjectSender* _sender;
}
-(instancetype) init: (GRSObjectSender*)sender {
    self = [super init: GRSServerAnswer.class];
    if(self) {
        _sender = sender;
    }
    return self;
}

-(void) reading:(GRSObject *)object {
    GRSServerAnswer* answ = (GRSServerAnswer*)object;
    [_sender readed:answ.message withResult:answ.response == 1];
}
@end

// empty category -> class extenstion
@interface GRSNetworkRouting ()
@property BOOL isError;
@property NSString* error;
@end

@interface GRSNetworkRouting (ProbeNetwork)
@property BOOL probing;
-(instancetype) initProbe: (GRSNetworkData*) data;
@end

@interface GRSNetworkProbeHandler : NSObject<GRSNetworkEvents>
@property NSString* error;
-(instancetype) init:(NSArray<GRSNetworkData*>*) data;
-(GRSNetworkRouting*) run;
@end

@implementation GRSNetworkProbeHandler {
    GRSNetworkRouting* _responsed;
    NSMutableArray<GRSNetworkRouting*>* _routins;
}
-(instancetype)init:(NSArray<GRSNetworkData *> *)data {
    self = [super init];
    if(self) {
        _routins = [[NSMutableArray alloc] init];
        for(GRSNetworkData* d in data) {
            GRSNetworkRouting* r = [[GRSNetworkRouting alloc] initProbe:d];
            GRSProbeConnectionObject* probe = [[GRSProbeConnectionObject alloc] init];
            [r addHandler:self];
            [probe bindTo:r];
            [_routins addObject: r];
        }
    }
    return self;
}

- (void) serverResponsed: (GRSNetworkRouting*) sender {
    @synchronized (self) {
        if(!_responsed) {
            _responsed = sender;
            for(GRSNetworkRouting* r in _routins) {
                if(r != sender)
                    [r cancel];
            }
        }
    }
}

-(GRSNetworkRouting *)run {
    NSOperationQueue* queue = [[NSOperationQueue alloc] init];
    for(GRSNetworkRouting* r in _routins) {
        [queue addOperation:r];
    }
    [queue waitUntilAllOperationsAreFinished];
    if(!_responsed) {
        for(GRSNetworkRouting* r in _routins) {
            if(r.isError) {
                self.error = r.error;
                break;
            }
        }
    } else {
        GRSServerAnswer* auth = [_responsed authObject];
        if(auth.response == 0) {
            self.error = auth.message;
            _responsed = nil;
        }
    }
    return _responsed;
}

@end

static BOOL _probing = false;
@implementation GRSNetworkRouting (ProbeNetwork)
- (void)setProbing:(BOOL)probing {
    _probing = probing;
}

- (BOOL)probing { return _probing; }

- (instancetype)initProbe:(GRSNetworkData *)data {
    self = [self init:data];
    if(self) {
        self.probing = true;
    }
    return self;
}
@end

static NSString* _probeError;
static GRSServerAnswer* _probeAnswer;
@implementation GRSNetworkRouting {
    GRSNetworkData* _data;
    bool _probing;

    GRSAnswerHandler* _authHander;
    GRSAnswerHandler* _impersonateHander;
    
    NSMutableArray<GRSReceiverBase*>* _receivers;
    NSMutableArray<GRSCombineSender*>* _requestors;
    NSMutableArray<GRSObjectSender*>* _senders;
    
    NSInputStream* _input;
    NSOutputStream* _output;
    NSMutableArray<id<GRSNetworkEvents>>* _handlers;
}

+(NSString*) probeError {return _probeError; }

+(GRSServerAnswer*) probeAnswer { return _probeAnswer; }

+(GRSNetworkRouting*) probe: (NSArray<GRSNetworkData*>*) data {
    _probeError = @"";
    GRSNetworkProbeHandler *handler = [[GRSNetworkProbeHandler alloc] init:data];
    GRSNetworkRouting* ret = [handler run];
    if(!ret) {
        if(handler.error == nil)
            handler.error = NSLocalizedStringFromTable(@"Connection error", @"strings", @"");
        _probeError = [[NSString alloc] initWithString:handler.error];
    } else {
        _probeAnswer = [ret authObject];
        if(_probeAnswer && _probeAnswer.response == 0) {
            _probeError = [[NSString alloc] initWithString:_probeAnswer.message];
            ret = nil;
        }
    }
    return ret;
}

-(GRSServerAnswer*) authObject { return _authHander.answer; }

-(instancetype) init: (GRSNetworkData*) data{
    self = [super init];
    if(self) {
        _data = data;
        _receivers = [[NSMutableArray alloc] init];
        _senders = [[NSMutableArray alloc] init];
        _requestors = [[NSMutableArray alloc] init];
        _handlers = [[NSMutableArray alloc] init];
        _input = nil;
        _output = nil;
        _authHander = [[GRSAnswerHandler alloc] init];
        _impersonateHander = [[GRSAnswerHandler alloc] init];
    }
    return self;
}

-(GRSNetworkData*) networkData { return _data; }

-(void) addHandler: (id<GRSNetworkEvents>) handler { [_handlers addObject:handler]; }
-(void) removeHandler: (id<GRSNetworkEvents>) handler { [_handlers removeObject:handler]; }

-(void) addSender: (GRSObjectSender*) sender { [_senders addObject:sender]; }

-(void) addReceiver: (GRSReceiverBase*) receiver { [_receivers addObject:receiver]; }
-(void) removeReceiver:(GRSReceiverBase *)receiver { [_receivers removeObject:receiver]; }

-(void) addRequestor: (GRSSenderBase*) sender {
    if([_requestors count] == 0 || ! [_requestors.lastObject add: sender]) {
        [_requestors addObject: [[GRSCombineSender alloc] init:sender]];
    }
}

-(void) putError: (NSString*) _error {
    self.isError = TRUE;
    self.error = _error;
}

-(NSArray<GRSSenderBase*>*) getSenders {
    NSMutableArray<GRSSenderBase*>* ret = [[NSMutableArray alloc] init];
    
    if([_senders count] > 0) {
        for(GRSObjectSender* s in _senders) {
            NSArray<GRSSenderBase*>* sndObjs;
            while( (sndObjs = [s nextObjects]) ) {
                [ret addObjectsFromArray:sndObjs];
                for(int i=0 ; i<sndObjs.count; i++) {
                    // add handler for each sended object type
                    [_receivers addObject:[[GRSWriteResultHandler alloc] init: s]];
                }
            }
        }
    }
    
    if([ret count]) {
        [ret insertObject:[[GRSForcePutSend alloc] init] atIndex:0];
        return ret;
    }
    return _requestors;
}

-(void) main {
    self.isError = FALSE;
    
    if([self isCancelled])
        return;
   
    @try {
        // connect
        [self connecting];
        
        GRSPacketHandler* handler = [[GRSPacketHandler alloc] init: self];
        while(!self.isError && ![self isCancelled]) {
            NSArray<GRSSenderBase*>* toSend = [self getSenders];
            if([toSend count] == 0) break;
            
            // send
            if( ![self sending: toSend handler:handler waitTimeout:WAIT_READ] ) break;
            
            if([_senders count] > 0) [_senders removeAllObjects];
            else [_requestors removeAllObjects];
            
            // check & say what server responsed
            BOOL waiting = FALSE, finished = FALSE;
            for(id<GRSNetworkEvents> h in _handlers) {
                if([h respondsToSelector: @selector(serverResponsed:)]) {
                    if(!waiting) {
                        if(!waitData(_input, WAIT_READ) || [self isCancelled]) {
                            finished = TRUE;
                            break;
                        }
                        waiting = TRUE;
                    }
                    [h serverResponsed:self];
                }
            }
            
            if(finished || [self isCancelled]) break;
            
            // receive
            NSData* data = [self receive];
            if(!data || [self isCancelled]) break;
            
            // handle packet
            // add auth handler first
            [_receivers insertObject:_authHander atIndex:0];
            if([_data.login.impersonate length] > 0) {
                [_receivers insertObject:_impersonateHander atIndex:1];
            }
            if(![handler parsePacket:data handlers:_receivers]) {
                break;
            }
        }
        
        if(!self.probing || self.isCancelled) {
            // send by & closing
            [self sendBye: handler];
            [self closing];
        } else {
            self.probing = false;
        }
    }
    @catch(...) {
        // Do not rethrow exceptions.
        self.isError = TRUE;
    }

}

-(int) getValueFromOpt:(NSString*) opt {
    NSRange start = [opt rangeOfString:@"("];
    NSRange end = [opt rangeOfString:@")"];
    if(start.location == NSNotFound) start.location = 0;
    else start.location++;
    start.length = (end.location != NSNotFound) ? end.location - start.location : opt.length - start.location - 1;
    
    return (int)[[opt substringWithRange:start] integerValue];
}

- (NSData *)receive {
    if(!_input) return nil;
    
    if(!waitData(_input, WAIT_READ) || [self isCancelled]) return nil;
    
    NSData* pktHeader = stringToData(PACKET_TAG);
    NSMutableData* chk = [NSMutableData dataWithLength:pktHeader.length];
    
    int res = (int)[_input read:[chk mutableBytes] maxLength:chk.length];
    if(res < 0 || res < chk.length) return nil;
    if( ![pktHeader isEqualToData:chk] ) return nil;
    

    unsigned pktLen = 0;
    BOOL first = TRUE;
    BOOL err = FALSE;
    NSMutableArray* options = [[NSMutableArray alloc] init];
    while(!err) {
        NSString* opt = readUntill(_input, @";", WAIT_READ, nil);
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

    [self sayDoing:@"Receiving" count:pktLen];
    if(!readData(_input, [data mutableBytes], pktLen, WAIT_READ,
                 ^(unsigned current){[self sayProgress:current];} )) return nil;
    
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


- (BOOL)sending: (NSArray<GRSSenderBase*>*)toSend handler:(GRSPacketHandler*)handler waitTimeout: (unsigned) waitMS  {
    if(!_output) return FALSE;
    
    NSData* packet = [handler makePacket: toSend compressing:TRUE];
    self.isError = FALSE;
    const uint8_t* p = [packet bytes];
    unsigned cp = 0;
    unsigned size = (unsigned)packet.length;
    
    [self sayDoing:@"" count:size];
    
    NSDate *date = [NSDate date];
    while(size > 0 && !self.isCancelled) {
        if([_output hasSpaceAvailable]) {
            int writed = (int)[_output write: p maxLength:size];
            if(writed < 0) {
                self.isError = TRUE;
                self.error = [[_output streamError] localizedDescription];
                break;
            }
            p += writed;
            size -= writed;
            cp += writed;
            
            [self sayProgress:cp];
        } else if([_output streamStatus] == NSStreamStatusError) {
            self.isError = TRUE;
            self.error = [[_output streamError] localizedDescription];
            break;
        } else {
            unsigned interval = [date timeIntervalSinceNow] * -1000;
            if(waitMS > 0 && interval > waitMS) {
                self.isError = TRUE;
                self.error = NSLocalizedStringFromTable(@"Connection error", @"strings", @"");
                break;
            }
        }
    }
    
    return !self.isError && !self.isCancelled;
}

- (void) sendBye:(GRSPacketHandler*)handler { [self sending: @[[[GRSByeSend alloc] init]] handler:handler waitTimeout:0]; }

- (void) closing {
    [_input close];
    [_output close];
    
    _input = nil;
    _output = nil;
}

- (void) connecting {
    if(!_input) {
        CFReadStreamRef readStream;
        CFWriteStreamRef writeStream;
        CFStreamCreatePairWithSocketToHost(nil, (__bridge CFStringRef)_data.address, _data.port, &readStream, &writeStream);
        
        _input = (__bridge NSInputStream*)readStream;
        _output = (__bridge NSOutputStream*)writeStream;
        [_input open];
        [_output open];
    }
}
@end


@implementation GRSReceiver {
    NSString* _filter;
}

-(instancetype) init: (Class)objType {
    self = [super init:objType];
    if(self) {
        NSString* objName = [objType getObjectName];
        self.name = objName;
    }
    return self;
}

-(instancetype) init: (Class) objType withFilter:(NSString*)filter{
    if([self init:objType]) {
        _filter = filter;
    }
    return self;
}

-(void) bindTo:(GRSNetworkRouting *)network {
    [network addRequestor:[[GRSCommandSender alloc] init: [[GRSSelectCommand alloc] init:self.name withFilter:_filter]]];
    [network addReceiver:self];
}
@end

@implementation GRSDBReceiver {
    BOOL _clearBase;
    GRSDBWriter* _writer;
}
- (instancetype)init: (Class)rcvType {
    self = [super init:rcvType];
    if(self) {
        _clearBase = false;
        _writer = [[GRSDBWriter alloc] init];
    }
    return self;
}

-(instancetype) initClearBase: (Class) rcvType {
    self = [super init:rcvType];
    if(self) {
        _clearBase = true;
        _writer = [[GRSDBWriter alloc] init];
    }
    return self;
}

-(instancetype) init: (Class) object withFilter:(NSString*)filter clearBase:(BOOL)clearBase {
    self = [super init: object withFilter:filter];
    if(self) {
        _clearBase = clearBase;
        _writer = [[GRSDBWriter alloc] init];
    }
    return self;
}

- (void) starting {
    if(_clearBase) [GRSDBManager recreateTable: self.rcvType];
    else  [GRSDBManager checkTable:self.rcvType];
}

-(void) finishing { [_writer close]; }
-(void) reading:(GRSObject *)object { [_writer write:object]; }

@end

@implementation GRSNetworkRouting (NetworkProgressHandler)

-(void) sayDoing: (NSString*) info count: (unsigned) count {
    for(id<GRSNetworkEvents> h in _handlers) {
        if([h respondsToSelector: @selector(starting:count:)]) {
            [h starting:info count: count];
        }
    }
}

-(void) sayProgress: (unsigned) count {
    for(id<GRSNetworkEvents> h in _handlers) {
        if([h respondsToSelector: @selector(progress)]) {
            [h progress: count];
        }
    }
}
@end


@implementation GRSObjectSender {
    NSMutableArray<GRSSenderBase*>* _objects;
}
-(instancetype) init: (NSArray<GRSSenderBase*>*)objectsToSend {
    self = [super init];
    if(self) {
        _objects = [[NSMutableArray alloc] init];
        [_objects addObjectsFromArray:objectsToSend];
    }
    return self;
}

-(NSArray<GRSSenderBase*>*) nextObjects {
    NSArray<GRSSenderBase*>* ret = _objects;
    _objects = nil;
    return ret;
}

-(void) readed: (NSString*) object withResult:(BOOL)result { }
-(void) bindTo: (GRSNetworkRouting*)network { [network addSender:self]; }

@end

@interface GRSParamSender : GRSSenderBase
-(instancetype) init: (GRSObject*) object;
@end

@implementation GRSParamSender {
    GRSObject* _param;
}
- (instancetype)init:(GRSObject *)object {
    self = [super init];
    if(self) {
        self.count = 1;
        self.name = @"ReportParam";
        _param = object;
    }
    return self;
}
- (GRSObject *)get:(int)index { return index == 0 ? _param : nil; }
@end

@implementation GRSReportHandler {
    GRSCommandSender* _report;
    GRSParamSender* _param;
    NSArray<GRSReceiverBase*>* _result;
}

-(instancetype) init: (NSString*)reportName withParam:(GRSObject *)param result:(NSArray<GRSReceiverBase*>*)result {
    self = [super init];
    if(self) {
        _report = [[GRSCommandSender alloc] init:@"Get Report" withParam:reportName];
        _param = [[GRSParamSender alloc] init:param];
        _result = result;
    }
    return self;
}
-(void) bindTo: (GRSNetworkRouting*)network {
    [network addRequestor:_report];
    [network addRequestor:_param];
    for(GRSReceiverBase* rcv in _result)
        [network addReceiver:  rcv];
}

@end
