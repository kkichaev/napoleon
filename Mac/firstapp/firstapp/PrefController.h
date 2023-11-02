//
//  PrefController.h
//  firstapp
//
//  Created by ert on 19/01/2019.
//  Copyright © 2019 GRSoft. All rights reserved.
//

#ifndef PrefController_h
#define PrefController_h

#import <UIKit/UIKit.h>

@interface PrefController : UIViewController

@property (weak, nonatomic) IBOutlet UITextField *ip1;
@property (weak, nonatomic) IBOutlet UITextField *port;
@property (weak, nonatomic) IBOutlet UITextField *login;
@property (weak, nonatomic) IBOutlet UITextField *password;

@end

#endif /* PrefController_h */
