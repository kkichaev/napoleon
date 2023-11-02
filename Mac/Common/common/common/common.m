//
//  common.m
//  common
//
//  Created by ert on 11/01/2019.
//  Copyright © 2019 GRSoft. All rights reserved.
//

#import "common.h"
#import <objc/runtime.h>
#import <Foundation/NSUserDefaults.h>

@implementation GRSSettingsBase

static NSString* progVersion;
static NSString* progCategory;

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
    
    return ftNone;
}

static FieldType getPropertyType(objc_property_t property) {
    const char *attr = property_getAttributes(property);
    switch(attr[1]) {
        case 'i':
            return ftInteger;
        case 'q':
            return ftLong;
        case 'f':
            return ftFloat;
        case '@':
            return getObjectType(attr + 2);
        default:
            return ftNone;
    }
}

-(void) setDefaults {
    NSUserDefaults* defaults = [NSUserDefaults standardUserDefaults];
    if([defaults boolForKey:@"_defPreferenceInited"]) {
        return;
    }
    
    [defaults setBool:TRUE forKey:@"_defPreferenceInited"];
    
    
    NSMutableDictionary *appDefaults = [[NSMutableDictionary alloc] init];
    unsigned int outCount, i;
    objc_property_t *properties = class_copyPropertyList([self class], &outCount);
    for (i = 0; i < outCount; i++) {
        objc_property_t property = properties[i];
        const char *propName = property_getName(property);
        if(propName) {
            NSString *name = [NSString stringWithUTF8String:propName];
            
            id val = [self valueForKey:name];
            if(val)
                [appDefaults setObject:val forKey:name];
        }
    }

    [[NSUserDefaults standardUserDefaults] registerDefaults:appDefaults];
}

-(int) write {
//    [self setDefaults];

    Class cls = [self class];
    
    NSUserDefaults* defaults = [NSUserDefaults standardUserDefaults];
    unsigned int outCount, i;
    objc_property_t *properties = class_copyPropertyList(cls, &outCount);
    for (i = 0; i < outCount; i++) {
        objc_property_t property = properties[i];
        const char *propName = property_getName(property);
        if(propName) {
            NSString *name = [NSString stringWithUTF8String:propName];
            
            id val = [self valueForKey:name];
            if(val)
                [defaults setObject:val forKey:name];
            else
                [defaults removeObjectForKey:name];
        }
    }
    return 0;
}

-(int) read {
    [self setDefaults];
    
    Class cls = [self class];
    
    NSUserDefaults* defaults = [NSUserDefaults standardUserDefaults];
    unsigned int outCount, i;
    objc_property_t *properties = class_copyPropertyList(cls, &outCount);
    for (i = 0; i < outCount; i++) {
        objc_property_t property = properties[i];
        const char *propName = property_getName(property);
        if(propName) {
            NSString *name = [NSString stringWithUTF8String:propName];
            
            id val = [defaults objectForKey:name];
            if(val)
                [self setValue:val forKey:name];
        }
    }

    return 0;
}

@end

@implementation GRSPath

+ (NSURL *)dataDir {
    NSString* bundleID = [[NSBundle mainBundle] bundleIdentifier];
    NSFileManager*fm = [NSFileManager defaultManager];
    NSURL*    dirPath = nil;
    
    // Find the application support directory in the home directory.
    NSArray* appSupportDir = [fm URLsForDirectory:NSApplicationSupportDirectory
                                        inDomains:NSUserDomainMask];
    if ([appSupportDir count] > 0)
    {
        // Append the bundle ID to the URL for the
        // Application Support directory
        dirPath = [[appSupportDir objectAtIndex:0] URLByAppendingPathComponent:bundleID];
        
        // If the directory does not exist, this method creates it.
        // This method is only available in macOS 10.7 and iOS 5.0 or later.
        NSError*    theError = nil;
        if (![fm createDirectoryAtURL:dirPath withIntermediateDirectories:YES
                           attributes:nil error:&theError])
        {
            // Handle the error.
            
            return nil;
        }
    }
    
    return dirPath;
}

@end

@implementation GRSDataObject

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
    } while( [cls isSubclassOfClass:[GRSDataObject class]] );
    return ret;
}

+ (NSString *)getTableName { return nil; }
+ (NSString *)getKeyFields { return nil; }
+ (NSString *)getIndexes { return nil; }
+ (NSMutableDictionary *)listTypes { return [[NSMutableDictionary alloc] init]; }
+ (NSMutableSet *)binaryFields { return [[NSMutableSet alloc] init]; }

+ (Class)getItemType:(NSString *)listName {
    return [[self listTypes] objectForKey:listName];
}

@end

@implementation GRSIniter

+ (BOOL)initWithDBName:(NSString *)fileName {
    [GRSServerCommand setProgCategory:@"pda"];
    [GRSServerCommand setProgVersion:@"3.5.2.1"];

    return [GRSDBManager initWithFileName:fileName];
}

@end

@implementation GRSLoginData
@end

@implementation GRSServerCommand {
    NSString* impersonate;
}

@synthesize command = _command;

+ (void)setProgVersion:(NSString *)version {
    progVersion = version;
}

+ (void)setProgCategory:(NSString *)category {
    progCategory = category;
}
-(instancetype)init:(GRSLoginData *)login {
    self = [super init];
    if(self) {
        self.userid = login.login;
        self.password = login.password;
        self.category = progCategory;
        self.version = progVersion;
        impersonate = login.impersonate;
    }
    return self;
}

- (void)setCommand:(NSString *)command {
    if(command != _command) {
        _command = nil;
        if([impersonate length] == 0) {
            _command = command;
        } else {
            _command = [NSString stringWithFormat:@"%@ AS '%@'", command, impersonate];
        }
    }
}

@end


@implementation GRSGetCommand

- (instancetype)init:(GRSLoginData *)login object:(NSString*) objName {
    self = [super init:login];
    if(self) {
        self.command = @"GET";
        self.param = objName;
    }
    return self;
}

@end

@implementation GRSSelectCommand

- (instancetype)init:(GRSLoginData *)login object:(NSString*) objName withFilter:(NSString*)filter {
    self = [super init:login];
    if(self) {
        self.command = @"SELECT";
        self.param = [NSString stringWithFormat:@"%@:%@", objName, filter];
    }
    return self;
}

@end

@implementation GRSByeCommand

- (instancetype)init {
    self = [super init];
    if(self) {
        self.command = @"BYE";
    }
    return self;
}

@end

@implementation GRSServerAnswer
@end
