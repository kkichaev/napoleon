//
//  core.h
//  NapoleonCore
//
//  Created by Denis Mosyagin on 25.09.2021.
//

#import <Foundation/Foundation.h>
#import <sqlite3.h>

typedef enum {
    ftNone,
    ftInteger, // int
    ftUnsigned, // unsigned
    ftLong,    // long long
    ftUnsignedLong, // unsigned long long
    ftFloat,   // float
    ftString,  // NSString*
    ftDate,    // NSDate*
    ftArray,   // NSMutableArray*
} FieldType;


@interface GRSObject : NSObject

// network
+(NSString*) getObjectName;

// database
+(NSString*) getTableName;
+(NSString*) getKeyFields;
+(NSString*) getIndexes;

+(NSDictionary*) listTypes; // type of items contains arrays [name->Class]
+(NSDictionary*) aliases;   // fieldName -> serverFieldName
+(NSSet*) binaryFields;     // set of string (NSString*) fields which contains path to file


// helpers
+(NSDictionary*) getFields; // [NSString*] => FieldType
+(Class) getItemType:(NSString*) listName;
@end


//
// DataBase classes
//
@interface GRSDBManager : NSObject
+(BOOL) initWithFileName: (NSString*)fileName dir:(NSURL*)dir ;
+(sqlite3*) get;
+(void) close;

+ (BOOL) isTableExists:(NSString*) name;
+ (BOOL) directExecute:(NSString*) sql;
+ (BOOL) checkTable:(Class) dataObject;
+ (BOOL) recreateTable:(Class) dataObject;
@end

@interface GRSDBWriter : NSObject
-(instancetype) init;
-(void) close;
-(void) dealloc;

// update or insert record
- (BOOL) write:(GRSObject*) data;
- (BOOL) removeByRowid:(NSString*) tableName withRowid:(long long) rowid;
- (BOOL) removeByKey:(GRSObject*) data;
@end

@interface GRSDBReader : NSObject
-(instancetype) init;
-(void) close;
-(void) dealloc;

// reading by primary key object must have assigned key fields.
-(BOOL) readByKey:(GRSObject*) data;
-(BOOL) read:(GRSObject*) data where:(NSString*) where orderBy:(NSString*) orderBy;
-(BOOL) readNext:(GRSObject*) data;

-(NSArray<GRSObject*>*) fetch:(Class) dataObject;
-(NSArray<GRSObject*>*) fetch:(Class) dataObject where: (NSString*)filter;
-(NSArray<GRSObject*>*) fetch:(Class) dataObject where: (NSString*)filter order: (NSString*) order;

-(NSDictionary*) fetchDic: (Class) dataObject key: (NSString*) key;
-(NSDictionary*) fetchDic: (Class) dataObject key: (NSString*) key where: (NSString*)filter;
@end


//
// network
//
@interface GRSSenderBase : NSObject
@property (weak) NSString* name;
@property unsigned count;
-(GRSObject*) get: (int)index;
@end

@interface GRSReceiverBase : NSObject
@property NSString* name;
@property Class rcvType;

- (instancetype) init: (Class) rcvType;
- (void) starting;
- (void) finishing;

- (void) reading: (GRSObject*)object;
@end

@interface GRSLoginData : NSObject
@property NSString* login;
@property NSString* password;
@property NSString* impersonate;
@property int duration;
@property NSString* uuid;
@end

@interface GRSServerAnswer : GRSObject
@property NSString* message;
@property int response;
- (instancetype) init;
@end

@interface GRSNetworkData : NSObject
@property NSString* address;
@property int port;
@property GRSLoginData* login;
@end

@class GRSNetworkRouting;
@class GRSObjectSender;
@protocol GRSNetworkEvents <NSObject>
@optional
- (void) serverResponsed: (GRSNetworkRouting*) sender;

// if count == 0 we continue previous (changing handled object)
- (void) starting: (NSString*) info count: (unsigned) count;
- (void) progress: (unsigned) current;
@end

@interface GRSNetworkRouting : NSOperation
// find connection fill error if nil
+(GRSNetworkRouting*) probe: (NSArray<GRSNetworkData*>*) data;
+(GRSServerAnswer*) probeAnswer;
+(NSString*) probeError;

-(instancetype) init: (GRSNetworkData*) data;

-(void) addReceiver: (GRSReceiverBase*) receiver;
-(void) removeReceiver: (GRSReceiverBase*) receiver;

-(void) addRequestor: (GRSSenderBase*) sender;

-(void) addSender: (GRSObjectSender*) sender;

-(void) addHandler: (id<GRSNetworkEvents>) handler;
-(void) removeHandler: (id<GRSNetworkEvents>) handler;

-(void) main;

-(GRSNetworkData*) networkData;
-(GRSServerAnswer*) authObject;

-(void) putError: (NSString*) error;
@property (readonly) NSString* error;
@property (readonly) BOOL isError;
@end


@interface GRSServerCommand : GRSObject
+(void) setProgVersion: (NSString*) version;
+(void) setProgCategory: (NSString*) category;
+(NSString*) name;

-(instancetype)init:(NSString*) command;
-(instancetype)init:(NSString*) command withParam: (NSString*) param;

-(void) setLogin: (GRSLoginData*)login;

@property (readonly) NSString* command;
@property NSString* param;
@property NSString* userid;
@property NSString* password;
@property NSString* version;
@property NSString* category;
@property NSString* uuid;

@property int duration;
@end

@interface GRSGetCommand : GRSServerCommand
-(instancetype) init:(NSString*) objName;
@end

@interface GRSSelectCommand : GRSServerCommand
-(instancetype) init:(NSString*) objName withFilter:(NSString*)filter;
@end


@interface GRSReceiver : GRSReceiverBase
-(instancetype) init: (Class) rcvType;
-(instancetype) init: (Class) rcvType withFilter:(NSString*)filter;

-(void) bindTo: (GRSNetworkRouting*)network;
@end

typedef void (^ObjHandler)(GRSObject*);

@interface GRSDBReceiver : GRSReceiver
-(instancetype) init: (Class) rcvType;
-(instancetype) initClearBase: (Class) rcvType;
-(instancetype) init: (Class) rcvType withFilter:(NSString*)filter clearBase:(BOOL)clearBase;
@end

// use force put
@interface GRSObjectSender: NSObject
-(instancetype) init: (NSArray<GRSSenderBase*>*)objectsToSend;

-(NSArray<GRSSenderBase*>*) nextObjects;
-(void) readed: (NSString*) object withResult:(BOOL)result;
-(void) bindTo: (GRSNetworkRouting*)network;
@end

@interface GRSReportHandler : GRSReceiverBase
-(instancetype) init: (NSString*)reportName withParam:(GRSObject *)param result:(NSArray<GRSReceiverBase*>*)result;
-(void) bindTo: (GRSNetworkRouting*)network;
@end

