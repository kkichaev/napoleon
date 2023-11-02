//
//  SyncController.m
//  firstapp
//
//  Created by ert on 28/01/2019.
//  Copyright © 2019 GRSoft. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "SyncController.h"

#import "Settings.h"
#import <common.h>

@interface GRSSendData : NSObject <GRSObjectsToSend>
@end

@interface GRSNetTest : NSOperation
@end

@interface GRSPriceCost : GRSDataObject
@property float cost;
@end

@interface GRSPrice : GRSDataObject

@property NSString* id;
@property NSString* name;
@property float qty;
@property float qtyInPack;
@property NSMutableArray* cost;

+(NSMutableDictionary*) listTypes; // type of array items
@end

@implementation GRSPriceCost
@end

@implementation GRSPrice
+ (NSMutableDictionary *)listTypes {
    NSMutableDictionary *ret = [super listTypes];
    ret[@"cost"] = [GRSPriceCost class];
    return ret;
}
@end

@interface GRSPriceRcv : NSObject<GRSNetworkPacketHandler>
-(BOOL) canHandleObject:(NSString*) objName;
-(Class) objectType;
-(void) onObjectReaded:(GRSDataObject*)object withError:(NSString**)error;
-(void) onEnd;
@end

@implementation GRSPriceRcv {
    NSMutableArray* array;
}

- (BOOL)canHandleObject:(NSString *)objName {
    if([objName isEqualToString:@"Price"]) {
        array = [[NSMutableArray alloc] init];
        return TRUE;
    }
    return FALSE;
}
- (Class)objectType { return [GRSPrice class]; }
- (void)onEnd {}

- (void)onObjectReaded:(GRSDataObject *)object withError:(NSString *__autoreleasing *)error {
    [array addObject:object];
}

@end

@implementation GRSSendData

- (NSString *)name { return @"ServerCommand"; }
- (int)count { return 1; }
- (GRSDataObject *)get:(int)index {
    if(index > 0) return nil;
    
    GRSSettings* pref = [GRSSettings get];
    GRSLoginData* loginData = [[GRSLoginData alloc] init];
    loginData.login = pref.login;
    loginData.password = pref.password;
    
    GRSServerCommand* cmd = [[GRSSelectCommand alloc] init:loginData object:@"Price" withFilter:@"SetQtyFilter(FALSE)"];
    return cmd;
}
@end

@implementation GRSNetTest

- (void)main {
    
    GRSSettings* pref = [GRSSettings get];
    
    GRSConnectionData* conData = [[GRSConnectionData alloc] init:pref.ip1 :pref.port1];
    NSArray* cona = @[conData];
    
    GRSSendData* snd = [[GRSSendData alloc] init];
    
    NSArray<GRSObjectsToSend> *objSend = (NSArray<GRSObjectsToSend>*)@[snd];
    NSData* pkt = [GRSNetworkExhange makePacket:objSend compressing:TRUE];
    
    GRSNetworkExhange *exch = [[GRSNetworkExhange alloc] init];
    if([exch connecting:cona :pkt]) {
        NSString* err;
        NSData* packet = [exch readingPacket:nil errorText:&err];
        BOOL parsed = FALSE;
        if(packet) {
            GRSPriceRcv* h = [[GRSPriceRcv alloc] init];
            parsed = [exch parsePacket:packet handlers:(NSArray<GRSNetworkPacketHandler>*)@[h] progress:nil errorText:&err];
        }
        [exch sendByeCommand];
    }
    [exch close];
}

@end

@implementation GRSSyncController


- (IBAction)doSync:(id)sender {
    NSOperationQueue* queue = [[NSOperationQueue alloc] init];
    NSOperation* op = [[GRSNetTest alloc] init];
    [queue addOperation:op];
}

@end
