//
//  common.h
//  common
//
//  Created by ert on 11/01/2019.
//  Copyright © 2019 GRSoft. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <sqlite3.h>

typedef enum {
    ftNone,
    ftInteger, // int
    ftLong,    // long long
    ftFloat,   // float
    ftString,  // NSString*
    ftDate,    // NSDate*
    ftArray,   // NSMutableArray*
} FieldType;


@interface GRSDataObject : NSObject
+(NSString*) getTableName;
+(NSString*) getKeyFields;
+(NSString*) getIndexes;

+(NSMutableDictionary*) listTypes; // type of array items
+(NSMutableSet*) binaryFields; // set of string (NSString*) fields which contains path to file


// helpers
+(NSDictionary*) getFields; // [NSString*] => FieldType
+(Class) getItemType:(NSString*) listName;

@end

@interface GRSLoginData : NSObject
@property NSString* login;
@property NSString* password;
@property NSString* impersonate;
@end

#import "objects.h"

@interface GRSSettingsBase : NSObject
- (int) read;
- (int) write;

- (void) setDefaults;
@end

@interface GRSIniter : NSObject
+(BOOL) initWithDBName: (NSString*)fileName;
@end

@interface GRSPath : NSObject
+(NSURL*) dataDir;
@end



@interface GRSDBManager : NSObject
+(BOOL) initWithFileName: (NSString*)fileName;
+(sqlite3*) get;
+(void) close;

+ (BOOL) isTableExists:(NSString*) name;
+ (BOOL) directExecute:(NSString*) sql;
+ (BOOL) checkTable:(Class) dataObject;
@end

@interface GRSDBWriter : NSObject
-(instancetype) init;
-(void) close;
-(void) dealloc;

// update or insert record
- (BOOL) write:(GRSDataObject*) data;
- (BOOL) removeByRowid:(NSString*) tableName withRowid:(long long) rowid;
- (BOOL) removeByKey:(GRSDataObject*) data;
@end

@interface GRSDBReader : NSObject
-(instancetype) init;
-(void) close;
-(void) dealloc;

// reading by primary key object must have assigned key fields.
-(BOOL) readByKey:(GRSDataObject*) data;
-(BOOL) read:(GRSDataObject*) data where:(NSString*) where orderBy:(NSString*) orderBy;
-(BOOL) readNext:(GRSDataObject*) data;
@end



@protocol GRSObjectsToSend
- (NSString*) name;
- (int) count;
- (GRSDataObject*) get: (int) index;
@end

@protocol GRSNetworkProgress
-(void) setObjName:(NSString*)messaqe;
-(void) setMax:(int)max;
-(void) setCurrent:(int)pos;
@end

@protocol GRSNetworkPacketHandler
-(BOOL) canHandleObject:(NSString*) objName;
-(Class) objectType;
-(void) onObjectReaded:(GRSDataObject*)object withError:(NSString**)error;
-(void) onEnd;
@end

@class GRSConnectOp;

@interface GRSConnectionData : NSObject
@property NSString* address;
@property unsigned port;

-(instancetype) init: (NSString*) address : (unsigned)port;
@end

@interface GRSNetworkExhange : NSObject
@property NSString* address;
@property unsigned port;
@property NSInputStream* input;
@property NSOutputStream* output;

+(NSData*) makePacket :(NSArray<GRSObjectsToSend>*) objects compressing:(BOOL)compress;

-(instancetype) init;
-(BOOL) connecting: (NSArray*)address : (NSData*)data;
-(NSData*) readingPacket: (id<GRSNetworkProgress>)progress errorText:(NSString**) error;
-(BOOL) parsePacket: (NSData*)packet handlers: (NSArray<GRSNetworkPacketHandler>*) handlers
           progress:(id<GRSNetworkProgress>)progress errorText:(NSString**) error;
-(BOOL) sendingPacket:(NSData*)packet progress: (id<GRSNetworkProgress>)progress errorText:(NSString**) error;
-(void) sendByeCommand;
-(void) close;
@end

