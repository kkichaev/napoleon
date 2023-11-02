//
//  objects.h
//  common
//
//  Created by ert on 29/01/2019.
//  Copyright © 2019 GRSoft. All rights reserved.
//

// import inside common.h

@interface GRSServerCommand : GRSDataObject
+(void) setProgVersion: (NSString*) version;
+(void) setProgCategory: (NSString*) category;

-(instancetype) init:(GRSLoginData*)login;

@property (nonatomic, strong) NSString* command;
@property NSString* param;
@property NSString* userid;
@property NSString* password;
@property NSString* version;
@property NSString* category;
@property unsigned duration;
@end

@interface GRSGetCommand : GRSServerCommand
-(instancetype) init:(GRSLoginData*)login object:(NSString*) objName;
@end

@interface GRSSelectCommand : GRSServerCommand
-(instancetype) init:(GRSLoginData*)login object:(NSString*) objName withFilter:(NSString*)filter;
@end

@interface GRSByeCommand : GRSServerCommand
@end

@interface GRSServerAnswer : GRSDataObject
@property NSString* message;
@property int response;
@end
