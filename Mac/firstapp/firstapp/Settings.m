//
//  Settings.m
//  test
//
//  Created by ert on 11/01/2019.
//  Copyright © 2019 GRSoft. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Settings.h"

static GRSSettings* settings;

@implementation GRSSettings

- (void) setDefaults {
    self.ip1 = @"127.0.0.1";
    self.port1 = 8888;
    
    self.login = @"";
    self.password = @"";
    
    [super setDefaults];
}

+(GRSSettings*) get {
    if(!settings) {
        settings = [[GRSSettings alloc] init];
        [settings read];
    }
    
    return settings;
}
@end
