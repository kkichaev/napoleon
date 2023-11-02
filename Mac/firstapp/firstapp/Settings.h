//
//  Settings.h
//  test
//
//  Created by ert on 11/01/2019.
//  Copyright © 2019 GRSoft. All rights reserved.
//

#ifndef Settings_h
#define Settings_h

#import <common.h>

@interface GRSSettings : GRSSettingsBase

@property NSString *ip1;
@property int port1;

@property NSString *login;
@property NSString *password;


- (void) setDefaults;

+(GRSSettings*) get;

@end

#endif /* Settings_h */
