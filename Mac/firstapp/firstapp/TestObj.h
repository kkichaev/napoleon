//
//  TestObj.h
//  firstapp
//
//  Created by ert on 22/01/2019.
//  Copyright © 2019 GRSoft. All rights reserved.
//

#ifndef TestObj_h
#define TestObj_h

//#import <common.h>

@interface GRSOBjItem : GRSDataObject
@property NSString *id;
@property int val;
@end

@interface GRSObjTest : GRSDataObject

@property NSString* id;
@property int value;
@property long long sum;
@property float cost;
@property NSDate* date;

@property NSMutableArray* items;

+(NSString*) getTableName;
+(NSString*) getKeyFields;
+(NSString*) getIndexes;

@end

#endif /* TestObj_h */
