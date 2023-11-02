//
//  database.m
//  common
//
//  Created by ert on 22/01/2019.
//  Copyright © 2019 GRSoft. All rights reserved.
//

#import <objc/runtime.h>
#import <Foundation/NSUserDefaults.h>
#import <sqlite3.h>
#import <string.h>
#import "common.h"

static sqlite3* db;

#define ROWID_FIELD @"rowid"

@interface GRSFieldBinder : NSObject {
    int pos;
    NSString* fieldName;
}
+(GRSFieldBinder*) create:(FieldType)type forField:(NSString*)fieldName withPos:(int)pos;

-(instancetype) initForField:(NSString*)fieldName withPos:(int) pos;
-(BOOL) write:(GRSDataObject*) data toStmt:(sqlite3_stmt*) stmt;
-(BOOL) read:(sqlite3_stmt*) stmt toObject:(GRSDataObject*) data;
@end

@interface GRSIntBinder : GRSFieldBinder
@end

@interface GRSInt64Binder : GRSFieldBinder
@end

@interface GRSFloatBinder : GRSFieldBinder
@end

@interface GRSStringBinder : GRSFieldBinder
@end

@interface GRSDateBinder : GRSFieldBinder
@end

@interface GRSArrayBinder : GRSFieldBinder
@end



@implementation GRSIntBinder
-(BOOL) write:(GRSDataObject*) data toStmt:(sqlite3_stmt*) stmt {
    int val = [[data valueForKey:fieldName] intValue];
    return (sqlite3_bind_int(stmt,pos, val) == SQLITE_OK);
}
-(BOOL) read:(sqlite3_stmt*) stmt toObject:(GRSDataObject*) data {
    int ival = sqlite3_column_int(stmt, pos);
    NSNumber* val = [NSNumber numberWithInt:ival];
    [data setValue:val forKey:fieldName];
    return TRUE;
}
@end

@implementation GRSInt64Binder
-(BOOL) write:(GRSDataObject*) data toStmt:(sqlite3_stmt*) stmt {
    long long val = [[data valueForKey:fieldName] intValue];
    return (sqlite3_bind_int64(stmt,pos, val) == SQLITE_OK);
}
-(BOOL) read:(sqlite3_stmt*) stmt toObject:(GRSDataObject*) data {
    long long ival = sqlite3_column_int64(stmt, pos);
    NSNumber* val = [NSNumber numberWithLongLong:ival];
    [data setValue:val forKey:fieldName];
    return TRUE;
}
@end

@implementation GRSFloatBinder
-(BOOL) write:(GRSDataObject*) data toStmt:(sqlite3_stmt*) stmt {
    double val = [[data valueForKey:fieldName] doubleValue];
    return (sqlite3_bind_double(stmt,pos, val) == SQLITE_OK);
}
-(BOOL) read:(sqlite3_stmt*) stmt toObject:(GRSDataObject*) data {
    double ival = sqlite3_column_double(stmt, pos);
    NSNumber* val = [NSNumber numberWithDouble:ival];
    [data setValue:val forKey:fieldName];
    return TRUE;
}
@end

@implementation GRSStringBinder
-(BOOL) write:(GRSDataObject*) data toStmt:(sqlite3_stmt*) stmt {
    NSString* val = [data valueForKey:fieldName];
    return (sqlite3_bind_text(stmt,pos, val.UTF8String, -1, SQLITE_STATIC) == SQLITE_OK);
}
-(BOOL) read:(sqlite3_stmt*) stmt toObject:(GRSDataObject*) data {
    const char* pval = (const char*)sqlite3_column_text(stmt, pos);
    NSString* val = [NSString stringWithUTF8String:pval];
    [data setValue:val forKey:fieldName];
    return TRUE;
}
@end

@implementation GRSDateBinder
-(BOOL) write:(GRSDataObject*) data toStmt:(sqlite3_stmt*) stmt {
    NSDate* val = [data valueForKey:fieldName];
    long long tval = (long long)val.timeIntervalSince1970;
    return (sqlite3_bind_int64(stmt,pos, tval) == SQLITE_OK);
}
-(BOOL) read:(sqlite3_stmt*) stmt toObject:(GRSDataObject*) data {
    long long ival = sqlite3_column_int64(stmt, pos);
    NSDate* val = [NSDate dateWithTimeIntervalSince1970:ival];
    [data setValue:val forKey:fieldName];
    return TRUE;
}
@end

@interface GRSBinaryString : NSObject
    @property NSString* field;

// always 2 ANSI characters ':[c,f,i,d,a...]
+ (NSString*) typeString;
- (instancetype)init:(Class) dataClass fieldName:(NSString*) _fn;
- (const char*) prepareRead: (const char*)header :(const char*)ep;
- (int) putItem: (GRSDataObject*)item : (NSOutputStream*) stream;
- (void) setItem: (NSInputStream*)stream : (GRSDataObject*)item ;
- (void) addHeader: (NSMutableString*) header;
@end

@interface GRSBinaryInt : GRSBinaryString
@end

@interface GRSBinaryLong : GRSBinaryString
@end

@interface GRSBinaryFloat : GRSBinaryString
@end

@interface GRSBinaryDate : GRSBinaryString
@end

@interface GRSBinaryArray : GRSBinaryString {
    NSArray* fields;
    NSMutableArray* rdFields;
    Class itemClass;
}
- (const char*) prepareRead: (const char*)header :(const char*)ep;
@end

@implementation GRSBinaryArray

+ (NSString *)typeString { return @":a"; }
- (instancetype)init:(Class)dataClass fieldName:(NSString *)_fn {
    self = [super init: dataClass fieldName:_fn];
    if (self) {
        itemClass = [dataClass getItemType:_fn];
        if(!itemClass) {
            self = nil;
        } else {
            NSMutableArray* src = [[NSMutableArray alloc] init];
            NSDictionary* _f = [itemClass getFields];
            for(NSString* key in _f) {
                FieldType ft = (FieldType)[_f[key] intValue];
                GRSBinaryString* item = nil;
                switch(ft) {
                case ftInteger:
                    item = [[GRSBinaryInt alloc] init: itemClass fieldName:key];
                    break;
                case ftLong:
                    item = [[GRSBinaryLong alloc] init: itemClass fieldName:key];
                    break;
                case ftFloat:
                    item = [[GRSBinaryFloat alloc] init: itemClass fieldName:key];
                    break;
                case ftDate:
                    item = [[GRSBinaryDate alloc] init: itemClass fieldName:key];
                    break;
                case ftString:
                    item = [[GRSBinaryString alloc] init: itemClass fieldName:key];
                    break;
                case ftArray:
                    item = [[GRSBinaryArray alloc] init: itemClass fieldName:key];
                    break;
                default:
                    break;
                }
                if(item)
                    [src addObject:item];
            }
            fields = [NSArray arrayWithArray:src];
            rdFields = [NSMutableArray array];
        }
    }
    return self;
}
- (void)addHeader:(NSMutableString *)header {
    for(GRSBinaryString* bh in fields) {
        [header appendString:bh.field];
        [header appendString:[[bh class] typeString]];
        [bh addHeader:header];
    }
    [header appendString:@"|"];
}
- (int)putItem:(GRSDataObject *)item :(NSOutputStream *)stream {
    NSArray* val = [item valueForKey:self.field];
    int count = (int)val.count;
    [stream write:(const uint8_t*)&count maxLength:sizeof(count)];
    for(GRSDataObject* di in val) {
        for(GRSBinaryString* wr in fields) {
            [wr putItem:di :stream];
        }
    }
    return count;
}
- (const char*)prepareRead:(const char *)p :(const char *)ep {
    [rdFields removeAllObjects];
    
    NSMutableString *name = [[NSMutableString alloc] init];
    while(p < ep) {
        if(*p == '|') break;
        if(*p == ':') {
            p++; // skip type
            for(GRSBinaryString *fi in fields) {
                if([fi.field compare:name] == kCFCompareEqualTo) {
                    [rdFields addObject:fi];
                    p = [fi prepareRead: p: ep];
                    break;
                }
            }
            [name setString:@""];
        } else {
            [name appendFormat:@"%c", *p];
        }
        p++;
    }
    return p;
}

- (void)setItem:(NSInputStream *)stream :(GRSDataObject *)item {
    NSMutableArray* val = [[NSMutableArray alloc] init];
    
    int count = 0;
    [stream read:(uint8_t*)&count maxLength:sizeof(count)];
    
    for(int i=0; i<count; i++) {
        GRSDataObject* obj = [[itemClass alloc] init];
        for(GRSBinaryString* rd in rdFields) {
            [rd setItem:stream :obj];
        }
        [val addObject:obj];
    }
    
    [item setValue:val forKey:self.field];
}
@end

@implementation GRSBinaryString

+ (NSString *)typeString { return @":c"; }

- (void)addHeader:(NSMutableString *)header { }
- (const char*) prepareRead: (const char*)header :(const char*)ep { return header; }

- (instancetype)init: (Class) dataClass fieldName:(NSString*) _fn {
    self = [super init];
    if (self) {
        self.field = _fn;
    }
    return self;
}

- (int)putItem:(GRSDataObject *)item :(NSOutputStream *)stream {
    NSString* val = [item valueForKey:self.field];
    const char* pVal = val.UTF8String;
    int len = 0;
    if( pVal )
        len = (int)strlen(pVal);
    [stream write:(const uint8_t*)&len maxLength:sizeof(len)];
    if( len )
        [stream write:(const uint8_t*)pVal maxLength:len];
    return 1;
}

- (void)setItem:(NSInputStream *)stream :(GRSDataObject *)item {
    int len = 0;
    char *pStr = NULL;
    [stream read: (uint8_t*)&len maxLength: sizeof(len)];
    if(len) {
        pStr = (char*)malloc(len + 1);
        [stream read:(uint8_t*)pStr maxLength:len];
    }
    NSString* val = (len > 0) ? [NSString stringWithUTF8String:pStr] : @"";
    [item setValue:val forKey:self.field];
    free(pStr);
}
@end

@implementation GRSBinaryInt
+ (NSString *)typeString { return @":i"; }
- (int)putItem:(GRSDataObject *)item :(NSOutputStream *)stream {
    int val = [[item valueForKey:self.field] intValue];
    [stream write:(const uint8_t*)&val maxLength:sizeof(val)];
    return 1;
}
- (void)setItem:(NSInputStream *)stream :(GRSDataObject *)item {
    int val = 0;
    [stream read: (uint8_t*)&val maxLength: sizeof(val)];
    [item setValue:[NSNumber numberWithInt:val] forKey:self.field];
}
@end

@implementation GRSBinaryLong
+ (NSString *)typeString { return @":l"; }
- (int)putItem:(GRSDataObject *)item :(NSOutputStream *)stream {
    long long val = [[item valueForKey:self.field] longLongValue];
    [stream write:(const uint8_t*)&val maxLength:sizeof(val)];
    return 1;
}
- (void)setItem:(NSInputStream *)stream :(GRSDataObject *)item {
    long long val = 0;
    [stream read: (uint8_t*)&val maxLength: sizeof(val)];
    [item setValue:[NSNumber numberWithLongLong:val] forKey:self.field];
}
@end

@implementation GRSBinaryFloat
+ (NSString *)typeString { return @":f"; }
- (int)putItem:(GRSDataObject *)item :(NSOutputStream *)stream {
    float val = [[item valueForKey:self.field] floatValue];
    [stream write:(const uint8_t*)&val maxLength:sizeof(val)];
    return 1;
}
- (void)setItem:(NSInputStream *)stream :(GRSDataObject *)item {
    float val = 0;
    [stream read: (uint8_t*)&val maxLength: sizeof(val)];
    [item setValue:[NSNumber numberWithFloat:val] forKey:self.field];
}
@end

@implementation GRSBinaryDate
+ (NSString *)typeString { return @":d"; }
- (int)putItem:(GRSDataObject *)item :(NSOutputStream *)stream {
    NSDate* date = (NSDate*)[item valueForKey:self.field];
    long long val = (long long)date.timeIntervalSince1970;
    [stream write:(const uint8_t*)&val maxLength:sizeof(val)];
    return 1;
}
- (void)setItem:(NSInputStream *)stream :(GRSDataObject *)item {
    long long val = 0;
    [stream read: (uint8_t*)&val maxLength: sizeof(val)];
    [item setValue:[NSDate dateWithTimeIntervalSince1970:val] forKey:self.field];
}
@end


@interface GRSBinaryHolder : GRSBinaryArray
- (const void*) write:(GRSDataObject*) data : (int*) outBytes;
-(void) read:(GRSDataObject*)data fromBuffer: (const void*) buffer count: (int) cb;

-(instancetype)init:(Class)dataClass fieldName:(NSString *)_fn;
-(void) dealloc;
@end

@implementation GRSBinaryHolder {
    const char* readHeader;
}

- (instancetype)init:(Class)dataClass fieldName:(NSString *)_fn {
    self = [super init:dataClass fieldName:_fn];
    if( self ) {
        readHeader = nil;
    }
    return  self;
}

- (void)dealloc {
    free((void*)readHeader);
}

- (NSString*) header {
    NSMutableString* outStr = [[NSMutableString alloc] init];
    [self addHeader:outStr];
    return outStr;
}

- (const void*) write:(GRSDataObject*) data : (int*) outBytes {
    NSOutputStream* stream = [NSOutputStream outputStreamToMemory];
    [stream open];
    
    NSString* header = [self header];
    const char* hbytes = header.UTF8String;
    int len = (int)strlen(hbytes);
    
    // put header
    [stream write:(const uint8_t*)&len maxLength:sizeof(len)];
    [stream write:(const uint8_t*)hbytes maxLength:len];

    int count = [self putItem:data :stream];
    void *outP = nil;

    if( count > 0 ) {
        NSData* outData = [stream propertyForKey:NSStreamDataWrittenToMemoryStreamKey];
        *outBytes = (int)outData.length;
        outP = malloc(*outBytes);
        memcpy(outP, outData.bytes, *outBytes);
    }
    [stream close];
    return outP;
}

- (void)read:(GRSDataObject *)data fromBuffer:(const void *)buffer count:(int)cb {

    if(cb > 0) {
        NSData* pdata = [NSData dataWithBytes:buffer length:cb];
        NSInputStream *stream = [NSInputStream inputStreamWithData:pdata];
        [stream open];
        
        int head = 0;
        [stream read:(uint8_t*)&head maxLength:sizeof(head)];
        
        char* hp = (char*)malloc(head);
        [stream read:(uint8_t*)hp maxLength:head];
        
        if(!readHeader || memcmp(readHeader, hp, head) != 0) {
            readHeader = hp;
            [self prepareRead :hp :hp + head];
        } else {
            free(hp);
        }
        
        [self setItem :stream : data];
        
        [stream close];
    } else {
        NSMutableArray* val = [[NSMutableArray alloc] init];
        [data setValue:val forKey:self.field];
    }
}
@end

@implementation GRSArrayBinder {
    GRSBinaryHolder* holder;
}
-(instancetype)initForField:(NSString *)fieldName withPos:(int)pos {
    self = [super initForField:fieldName withPos:pos];
    if(self) {
        holder = nil;
    }
    return self;
}

-(BOOL) write:(GRSDataObject*) data toStmt:(sqlite3_stmt*) stmt {
    if(!holder)
        holder = [[GRSBinaryHolder alloc] init:[data class] fieldName:fieldName];
    int cb = 0;
    const void* pb = [holder write:data :&cb];
    return (sqlite3_bind_blob(stmt, pos, pb, cb, free) == SQLITE_OK);
}

-(BOOL) read:(sqlite3_stmt*) stmt toObject:(GRSDataObject*) data {
    if(!holder)
        holder = [[GRSBinaryHolder alloc] init:[data class] fieldName:fieldName];
    int cb = sqlite3_column_bytes(stmt, pos);
    const void* pb = (cb > 0) ? sqlite3_column_blob(stmt, pos) : nil;
    [holder read: data fromBuffer: pb count: cb];
    return TRUE;
}
@end


@implementation GRSFieldBinder

+(GRSFieldBinder*) create:(FieldType)type forField:(NSString*)fieldName withPos:(int)pos {
    GRSFieldBinder* ret = nil;
    switch(type) {
        case ftInteger:
            ret = [[GRSIntBinder alloc] initForField:fieldName withPos:pos];
            break;
        case ftLong:
            ret = [[GRSInt64Binder alloc] initForField:fieldName withPos:pos];
            break;
        case ftFloat:
            ret = [[GRSFloatBinder alloc] initForField:fieldName withPos:pos];
            break;
        case ftString:
            ret = [[GRSStringBinder alloc] initForField:fieldName withPos:pos];
            break;
        case ftDate:
            ret = [[GRSDateBinder alloc] initForField:fieldName withPos:pos];
            break;
        case ftArray:
            ret = [[GRSArrayBinder alloc] initForField:fieldName withPos:pos];
            break;

        default:
            break;

    }
    
    return ret;
}

-(instancetype) initForField:(NSString*)_fieldName withPos:(int) _pos {
    if(self = [super init]) {
        fieldName = _fieldName;
        pos = _pos;
    }
    return self;
}

-(BOOL) write:(GRSDataObject*) data toStmt:(sqlite3_stmt*) stmt { return TRUE; }
-(BOOL) read:(sqlite3_stmt*) stmt toObject:(GRSDataObject*) data { return TRUE; }

@end

static NSString* getSqlType(FieldType ft) {
    switch (ft) {
        case ftString:
            return @"TEXT";
        case ftInteger:
        case ftLong:
        case ftDate:
            return @"INTEGER";
        case ftFloat:
            return @"REAL";
        case ftArray:
            return @"BLOB";
        default:
            break;
    }
    return nil;
}

static BOOL CreateTable(Class dataClass) {
    NSString* table = [dataClass getTableName];
    NSDictionary* fields = [dataClass getFields];
    NSString* keyFields = [dataClass getKeyFields];
    
    NSMutableString* sql = [NSMutableString stringWithFormat:@"CREATE TABLE \"%@\" (", table];
    for(NSString *propName in fields) {
        if([propName caseInsensitiveCompare:ROWID_FIELD] == NSOrderedSame)
            continue;
        NSNumber* num = [fields valueForKey:propName];
        NSString* sqlType = getSqlType([num intValue]);
        if(!sqlType)
            continue;

        [sql appendFormat:@"\"%@\" %@,", propName, sqlType];
    }
    if(keyFields && [keyFields length] > 0)
        [sql appendFormat:@"PRIMARY KEY (%@)", keyFields];
    else
        [sql deleteCharactersInRange:NSMakeRange([sql length]-1, 1)];

    [sql appendString:@")"];

    return [GRSDBManager directExecute:sql];
}

static FieldType getColumnType(const char* type) {
    NSString* tp = [NSString stringWithUTF8String:type];
    if([tp caseInsensitiveCompare:@"INTEGER"] == NSOrderedSame) return ftInteger;
    if([tp caseInsensitiveCompare:@"TEXT"] == NSOrderedSame) return ftString;
    if([tp caseInsensitiveCompare:@"BLOB"] == NSOrderedSame) return ftArray;
    return ftFloat;
}

static NSDictionary* getTableFields(NSString* tableName) {
    NSMutableDictionary* ret = [[NSMutableDictionary alloc] init];
    sqlite3_stmt *statement = nil;
    if (sqlite3_prepare_v2([GRSDBManager get],
        [[NSString stringWithFormat:@"PRAGMA TABLE_INFO('%@')", tableName] UTF8String], -1, &statement, nil) == SQLITE_OK) {
        while (sqlite3_step(statement) == SQLITE_ROW) {
            const char *name = (const char *)sqlite3_column_text(statement, 1);
            const char *type = (const char *)sqlite3_column_text(statement, 2);

            FieldType ft = getColumnType(type);
            NSString* key = [NSString stringWithUTF8String:name];
            [ret setObject:[NSNumber numberWithInt:ft] forKey:key];
        }
    }
    
    sqlite3_finalize(statement);
    return ret;
}

static BOOL AddFieldsToTable(NSString* tableName, NSDictionary* fields) {
    BOOL ret = TRUE;
    for(NSString* propName in fields) {
        NSNumber* num = [fields valueForKey:propName];
        NSString* sqlType = getSqlType([num intValue]);
        if(!sqlType) {
            ret = FALSE;
            break;
        }
        
        NSString* stmt = [NSString stringWithFormat:@"ALTER TABLE \"%@\" ADD COLUMN [%@] %@", tableName, propName, sqlType];
        if(![GRSDBManager directExecute:stmt]) {
            ret = FALSE;
            break;
        }
    }
    
    return ret;
}
            
static BOOL CheckTable(Class dataClass) {
   NSMutableDictionary* fields = [NSMutableDictionary dictionaryWithDictionary:[dataClass getFields]];
   NSDictionary* dbFields = getTableFields([dataClass getTableName]);
   
   for(NSString* name in dbFields) {
       [fields removeObjectForKey:name];
   }
   
   return AddFieldsToTable([dataClass getTableName], fields);
}


@implementation GRSDBManager

+(BOOL) initWithFileName: (NSString*)fileName {
    if(!db) {
        NSURL *dir = [GRSPath dataDir];
        NSURL *dbURL = [dir URLByAppendingPathComponent: fileName];
        
        NSFileManager*fm = [NSFileManager defaultManager];
        NSString* dbName = [dbURL absoluteString];
        [fm createFileAtPath:dbName contents:nil attributes:nil];
        
        const char* path = [dbName UTF8String];
        sqlite3_open(path, &db);
    }
    return TRUE;
}

+(sqlite3*) get {
    return db;
}

+(void) close {
    if(db) {
        sqlite3_close(db);
        db = nil;
    }
}

+ (BOOL) isTableExists:(NSString*) name {
    if(!db)
        return FALSE;
    
    sqlite3_stmt *stmt = nil;
    NSString* sql = [NSString stringWithFormat:@"SELECT name FROM SQLITE_MASTER WHERE type='table' AND name='%@'", name];
    int rc = sqlite3_prepare_v2(db, [sql UTF8String], -1, &stmt, nil);
    if(rc == SQLITE_OK) {
        rc = sqlite3_step(stmt);
    }
    sqlite3_finalize(stmt);
    return (rc == SQLITE_ROW);
}

+ (BOOL) directExecute:(NSString*) sql {
    if(!db)
        return FALSE;

    char *errMsg = NULL;
    int res = sqlite3_exec(db, [sql UTF8String], nil, nil, &errMsg);
    sqlite3_free(errMsg);
    return (res == SQLITE_OK);
}

+ (BOOL) checkTable:(Class) dataClass {
    NSString* table = [dataClass getTableName];
    
    BOOL res = FALSE;
    if(![GRSDBManager isTableExists:table]) {
        res = CreateTable(dataClass);
    } else {
        res = CheckTable(dataClass);
    }
    
    return res;
}

@end

 typedef enum {
    wsNone,
    wsUpdate,
    wsRemoveRID,
    wsRemoveKey,
} WriterState;

#define MAX_DO_COUNT 200

@implementation GRSDBWriter {
    WriterState state;
    sqlite3_stmt *stmt;
    NSMutableArray* binder;
    int doCount;
}

-(instancetype) init {
    if (self = [super init]) {
        state = wsNone;
        stmt = nil;
        doCount = 0;
        binder = [[NSMutableArray alloc] init];
    }
    return self;
}

-(void) closeStmt {
    if(stmt) {
        sqlite3_finalize(stmt);
        [GRSDBManager directExecute:@"END;"];
        stmt = nil;
        doCount = 0;
    }
    [binder removeAllObjects];
}

- (void)dealloc {
    [self closeStmt];
}
-(void) close {
    state = wsNone;
    [self closeStmt];
}

-(BOOL) prepareStmt: (NSString*) sql stmtType: (WriterState) ws {
    if (sqlite3_prepare_v2([GRSDBManager get], [sql UTF8String], -1, &stmt, nil) != SQLITE_OK) {
        return FALSE;
    }
    
    state = ws;
    [GRSDBManager directExecute:@"BEGIN;"];
    return TRUE;
}

-(BOOL) doStmt {
    int ret = sqlite3_step(stmt);
    sqlite3_reset(stmt);
    
    if(doCount++ >= MAX_DO_COUNT) {
        doCount = 0;
        [GRSDBManager directExecute:@"END;"];
        [GRSDBManager directExecute:@"BEGIN;"];
    }
    
    return (ret = SQLITE_OK);
}

-(BOOL) prepareToWrite:(GRSDataObject*) data {
    [self closeStmt];
    
    NSMutableString *fields = [[NSMutableString alloc] init];
    NSMutableString *params = [[NSMutableString alloc] init];
    
    int idx = 1;
    NSDictionary* dbFields = [[data class] getFields];
    for(NSString* fieldName in dbFields) {
        if([fieldName caseInsensitiveCompare:ROWID_FIELD] == NSOrderedSame)
            continue;

        id fval = dbFields[fieldName];
        FieldType ft = (FieldType)[fval intValue];
        GRSFieldBinder* fb = [GRSFieldBinder create: ft forField: fieldName withPos: idx];
        if( fb ) {
            [binder addObject:fb];
            [fields appendFormat:@"\"%@\",", fieldName];
            [params appendString:@"?,"];
            idx++;
        }
    }
    [fields deleteCharactersInRange:NSMakeRange([fields length]-1, 1)];
    [params deleteCharactersInRange:NSMakeRange([params length]-1, 1)];


    NSString* tableName = [[data class] getTableName];
    NSMutableString* sql = [NSMutableString stringWithFormat:@"INSERT OR REPLACE INTO \"%@\" (", tableName];
    [sql appendString:fields];
    [sql appendString:@") VALUES ("];
    [sql appendString:params];
    [sql appendString:@")"];
    
    return [self prepareStmt:sql stmtType:wsUpdate];
}

- (BOOL) write:(GRSDataObject*) data {
    if(state != wsUpdate && ![self prepareToWrite:data]) {
        return FALSE;
    }
    if(!stmt)
        return FALSE;
    
    for(GRSFieldBinder* b in binder) {
        [b write:data toStmt:stmt];
    }
    
    return [self doStmt];
}

- (BOOL) prepareRemoveByRowid:(NSString*) tableName {
    [self closeStmt];

    NSString* sql = [NSString stringWithFormat:@"DELETE FROM \"%@\" WHERE ROWID=?", tableName];
    return [self prepareStmt:sql stmtType:wsRemoveRID];
}

- (BOOL) removeByRowid:(NSString*) tableName withRowid:(long long) rowid {
    if(state != wsRemoveRID && ![self prepareRemoveByRowid:tableName]) {
        return FALSE;
    }
    if(!stmt)
        return FALSE;

    sqlite3_bind_int64(stmt, 0, rowid);

    return [self doStmt];
}

-(BOOL) prepareRemoveByKey:(Class) dataClass {
    NSString *keyFields = [dataClass getKeyFields];
    if(!keyFields || keyFields.length == 0)
        return FALSE;

    int idx = 1;
    NSDictionary* flds = [dataClass getFields];
    NSString *tableName = [dataClass getTableName];
    NSMutableString* sql = [NSMutableString stringWithFormat:@"DELETE FROM \"%@\" WHERE ", tableName];
    NSArray* kfa = [keyFields componentsSeparatedByString:@","];
    for(NSString *keyF in kfa) {
        NSNumber *fval = flds[keyF];
        FieldType ft = (FieldType)[fval intValue];
        GRSFieldBinder* fb = [GRSFieldBinder create: ft forField: keyF withPos: idx];
        if( fb ) {
            if(idx) {
                [sql appendString:@" AND "];
            }
            [sql appendFormat:@"\"%@\"=?", keyF];
            [binder addObject:fb];
            idx++;
        }
    }
    
    return [self prepareStmt:sql stmtType:wsRemoveKey];
}

- (BOOL) removeByKey:(GRSDataObject*) data {
    if(state != wsRemoveKey && ![self prepareRemoveByKey:[data class]]) {
        return FALSE;
    }
    if(!stmt)
    return FALSE;
    
    for(GRSFieldBinder* b in binder) {
        [b write:data toStmt:stmt];
    }
    
    return [self doStmt];
}

@end

typedef enum {
    rsNone,
    rsReadByKey,
    rsRead,
} ReadState;

@implementation  GRSDBReader {
    ReadState state;
    sqlite3_stmt *stmt;
    NSMutableArray* binder;
    NSMutableArray* paramBinder;
}

- (instancetype)init
{
    self = [super init];
    if (self) {
        state = rsNone;
        binder = [[NSMutableArray alloc] init];
        paramBinder = [[NSMutableArray alloc] init];
        stmt = nil;
    }
    return self;
}

-(void) closeStmt {
    if(stmt) {
        sqlite3_finalize(stmt);
        stmt = nil;
    }
    [binder removeAllObjects];
    [paramBinder removeAllObjects];
}

- (void)dealloc {
    [self closeStmt];
}

-(void) close {
    state = rsNone;
    [self closeStmt];
}

-(NSMutableString*) prepareForSelect:(GRSDataObject*)data {
    [self closeStmt];
    
    NSMutableString *sql = [NSMutableString stringWithString:@"SELECT "];
    
    int idx = 0;
    NSDictionary* dbFields = [[data class] getFields];
    for(NSString* fieldName in dbFields) {
        id fval = dbFields[fieldName];
        FieldType ft = (FieldType)[fval intValue];
        GRSFieldBinder* fb = [GRSFieldBinder create: ft forField: fieldName withPos: idx];
        if( fb ) {
            [binder addObject:fb];
            [sql appendFormat:@"\"%@\",", fieldName];
            idx++;
        }
    }
    [sql deleteCharactersInRange:NSMakeRange([sql length]-1, 1)];
    [sql appendFormat:@" FROM \"%@\"", [[data class] getTableName]];

    return sql;
}

-(BOOL) prepareReadByKey:(GRSDataObject *)data {
    NSMutableString *sql = [self prepareForSelect:data];

    [sql appendString:@" WHERE "];

    int idx = 1;
    NSDictionary* dbFields = [[data class] getFields];
    NSArray* kfa = [[[data class] getKeyFields] componentsSeparatedByString:@","];
    for(NSString* fieldName in kfa) {
        id fval = dbFields[fieldName];
        FieldType ft = (FieldType)[fval intValue];
        GRSFieldBinder* fb = [GRSFieldBinder create: ft forField: fieldName withPos: idx];
        if( fb ) {
            if( idx > 1 )
                [sql appendString:@" AND "];
            [paramBinder addObject:fb];
            [sql appendFormat:@"\"%@\" = ?", fieldName];
            idx++;
        }
    }
    if (sqlite3_prepare_v2([GRSDBManager get], [sql UTF8String], -1, &stmt, nil) != SQLITE_OK) {
        return FALSE;
    }
    state = rsReadByKey;
    return TRUE;
}

- (BOOL)readByKey:(GRSDataObject *)data {
    if(state != rsReadByKey && ![self prepareReadByKey:data]) {
        return FALSE;
    }
    
    int res = 0;
    for(GRSFieldBinder* b in paramBinder) {
        res = [b write:data toStmt:stmt];
    }
    
    res = [self readNext:data];
    sqlite3_reset(stmt);
    return (res == SQLITE_ROW);
}

- (BOOL) prepareRead:(GRSDataObject*) data where:(NSString *)where orderBy:(NSString *)orderBy {
    NSMutableString *sql = [self prepareForSelect:data];
    if( where && where.length) {
        [sql appendString:@" WHERE "];
        [sql appendString:where];
    }
    if( orderBy && orderBy.length) {
        [sql appendString:@" ORDER BY "];
        [sql appendString:orderBy];
    }
    if (sqlite3_prepare_v2([GRSDBManager get], [sql UTF8String], -1, &stmt, nil) != SQLITE_OK) {
        return FALSE;
    }
    state = rsRead;
    return TRUE;
}

- (BOOL)read:(GRSDataObject *)data where:(NSString *)where orderBy:(NSString *)orderBy {
    if(state != rsRead && ![self prepareRead:data where:where orderBy:orderBy]) {
        return FALSE;
    }

    return [self readNext:data];
}

- (BOOL)readNext:(GRSDataObject *)data {
    if(state == rsNone) return FALSE;
    
    int res = sqlite3_step(stmt);
    if(res == SQLITE_ROW) {
        for(GRSFieldBinder* b in binder) {
            [b read:stmt toObject:data];
        }
    }
    return (res == SQLITE_ROW);
}

@end
