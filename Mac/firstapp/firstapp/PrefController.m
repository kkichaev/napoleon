//
//  PrefCopntroller.m
//  firstapp
//
//  Created by ert on 19/01/2019.
//  Copyright © 2019 GRSoft. All rights reserved.
//

//#import <Foundation/Foundation.h>

#import "PrefController.h"
#import "Settings.h"

#import "TestObj.h"
#import <common.h>

@implementation PrefController

-(void) viewDidLoad {
    [super viewDidLoad];    
    self.navigationItem.rightBarButtonItem = self.editButtonItem;
    
    GRSSettings* pref = [GRSSettings get];

    self.port.text = [NSString stringWithFormat:@"%d", pref.port1];
    self.ip1.text = pref.ip1;

    self.login.text = pref.login;
    self.password.text = pref.password;

    self.view.userInteractionEnabled = FALSE;
    
    
    GRSObjTest *testObj = [[GRSObjTest alloc] init];
    GRSDBReader* r = [[GRSDBReader alloc] init];
    testObj.id = @"id";

    [r readByKey:testObj];
    [r close];

//    testObj.id = @"id";
//    testObj.value = 1;
//    testObj.sum = 2;
//    testObj.cost = 12.25;
//    testObj.date = [[NSDate alloc] init];
//    testObj.items = [[NSMutableArray alloc] init];
//
//    for(int i=0; i<5; i++) {
//        GRSOBjItem *item = [[GRSOBjItem alloc] init];
//        item.id = [NSString stringWithFormat:@"id %d", i];
//        item.val = i + 5;
//
//        [testObj.items addObject:item];
//    }
//
//    [GRSDBWriter checkTable:[GRSObjTest class]];
//    GRSDBWriter* wr = [[GRSDBWriter alloc] init];
//    [wr write:testObj];
//    [wr close];
}

- (void)setEditing:(BOOL)editing animated:(BOOL)animated {
    [super setEditing:editing animated:animated];
    
    self.view.userInteractionEnabled = editing;
//    self.port.enabled = editing;
//    self.ip1.enabled = editing;
    
    if(!editing) {
        GRSSettings* pref = [GRSSettings get];
        pref.port1 = [self.port.text intValue];
        pref.ip1 = self.ip1.text;
        
        pref.login = self.login.text;
        pref.password = self.password.text;
        
        [pref write];
        [self.navigationController popViewControllerAnimated:true];
    }
}

@end
