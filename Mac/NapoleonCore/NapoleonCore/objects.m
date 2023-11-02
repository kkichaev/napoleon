//
//  objects.m
//  NapoleonCore
//
//  Created by Denis Mosyagin on 30.08.2021.
//

#import <Foundation/Foundation.h>
#import "core.h"
#import "objects.h"
#import <objc/runtime.h>

static FieldType getObjectType(const char *type) {
    const char *ep = strchr(type, ',');
    size_t len = strlen(type);
    if( ep)
        len = ep - type;
    
    if(strncmp(type, "\"NSString\"", len) == 0)
        return ftString;
    if(strncmp(type, "\"NSDate\"", len) == 0)
        return ftDate;
    if(strncmp(type, "\"NSMutableArray\"", len) == 0)
        return ftArray;
    if(strncmp(type, "\"NSArray\"", len) == 0)
        return ftArray;

    return ftNone;
}

static FieldType getPropertyType(objc_property_t property) {
    const char *attr = property_getAttributes(property);
    switch(attr[1]) {
        case 'i':
        case 's':
        case 'l':
            return ftInteger;
        case 'I':
        case 'S':
        case 'L':
            return ftUnsigned;
        case 'q':
            return ftLong;
        case 'Q':
            return ftUnsignedLong;
        case 'f':
        case 'd':
            return ftFloat;
        case '@':
            return getObjectType(attr + 2);
        default:
            return ftNone;
    }
}

@implementation GRSObject

+(NSDictionary*) getFields {
    NSMutableDictionary* ret = [[NSMutableDictionary alloc] init];
    Class cls = self;
    unsigned i, outCount;

    do {
        objc_property_t *properties = class_copyPropertyList(cls, &outCount);
        for (i = 0; i < outCount; i++) {
            objc_property_t property = properties[i];
            const char *propName = property_getName(property);
            if(propName) {
                NSString *name = [NSString stringWithUTF8String:propName];
                if([ret objectForKey:name] == nil) {
                    FieldType ft = getPropertyType(property);
                    if(ft != ftNone)
                        [ret setObject:[NSNumber numberWithInt:ft] forKey:name];
                }
            }
        }
        cls = class_getSuperclass(cls);
    } while( [cls isSubclassOfClass:[GRSObject class]] );
    return ret;
}

+ (NSString *)getObjectName { return nil; }
+ (NSString *)getTableName { return nil; }
+ (NSString *)getKeyFields { return nil; }
+ (NSString *)getIndexes { return nil; }
+ (NSDictionary *)listTypes { return [[NSDictionary alloc] init]; }
+ (NSDictionary *)aliases { return [[NSDictionary alloc] init]; }
+ (NSSet *)binaryFields { return [[NSSet alloc] init]; }

+ (Class)getItemType:(NSString *)listName {
    return [[self listTypes] objectForKey:listName];
}

@end

@implementation Agent
+ (NSString *)getObjectName { return @"Agents"; }
+ (NSString *)getTableName { return @"Agents"; }
+ (NSString *)getKeyFields { return @"id"; }
@end

@implementation Division
+ (NSString *)getObjectName { return @"Division"; }
+ (NSString *)getTableName { return @"Divisions"; }
+ (NSString *)getKeyFields { return @"id"; }
+ (NSDictionary *)listTypes { return @{@"agents" : DivisionAgent.class}; }
+ (NSDictionary *)aliases { return @{@"descr" : @"description"}; }
@end

@implementation DivisionAgent
@end
