//
//  network_packet.h
//  NapoleonCore
//
//  Created by Denis Mosyagin on 08.09.2021.
//

@interface GRSByeCommand : GRSServerCommand
@end

@interface GRSCommandSender : GRSSenderBase
-(instancetype) init: (NSString*) command withParam: (NSString*) param;
-(instancetype) init: (GRSServerCommand*) command;
@end

@interface GRSByeSend : GRSCommandSender
@end

@interface GRSDoneSend : GRSCommandSender
@end

@interface GRSForcePutSend : GRSCommandSender
@end

@interface GRSAnswerHandler : GRSReceiverBase
@property GRSServerAnswer* answer;
@end

// send to probe connection to server
// server probe command ObjFormatGet ServerAnswer
@interface GRSProbeConnectionObject : GRSReceiver
@end

@interface GRSPacketHandler : NSObject

-(instancetype) init: (GRSNetworkRouting*) owner;

-(NSData*) makePacket :(NSArray<GRSSenderBase*>*) objects compressing:(BOOL)compress;
- (BOOL)parsePacket:(NSData *)packet handlers:(NSArray<GRSReceiverBase*>*)_handlers;

@end

@interface GRSNetworkRouting (NetworkProgressHandler)
-(void) sayDoing: (NSString*) info count: (unsigned) count;
-(void) sayProgress: (unsigned) count;
@end

BOOL waitData(NSInputStream* stream, uint32_t ms);
NSData* stringToData(NSString* str);
NSString* readUntill(NSInputStream* stream, NSString* syms, uint32_t ms, NSString** endStr);
BOOL readData(NSInputStream* stream, void* buffer, unsigned length, uint32_t ms, void (^progress)(unsigned));
NSMutableData* decompress(NSMutableData* src, unsigned length);

extern NSString* PACKET_TAG;
extern NSString* DATA_TAG;
extern NSString* GZIP_TAG;
extern NSString* CRC_TAG;

extern const uint32_t WAIT_READ;
