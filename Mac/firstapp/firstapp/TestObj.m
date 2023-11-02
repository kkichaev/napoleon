//
//  TestObj.m
//  firstapp
//
//  Created by ert on 22/01/2019.
//  Copyright © 2019 GRSoft. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "TestObj.h"

@implementation GRSOBjItem
@end

@implementation GRSObjTest
+ (NSString *)getTableName { return @"TestTable"; }
+ (NSString *)getKeyFields { return @"id"; }
+ (NSString *)getIndexes { return @"value"; }
+ (NSMutableDictionary *)listTypes {
    NSMutableDictionary *ret = [super listTypes];
    ret[@"items"] = [GRSOBjItem class];
    return ret;
}
@end
