//
//  AddItemVC.h
//  iOSCrossPlatform
//
//  Created by Matthew Dutton on 12/3/14.
//  Copyright (c) 2014 Matthew Dutton. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Utils.h"
#import <Parse/Parse.h>
#import "Utils.h"

@interface AddItemVC : UIViewController

@property (weak, nonatomic) IBOutlet UITextField *itemToAdd;

@property (weak, nonatomic) IBOutlet UITextField *itemQuantity;

- (IBAction)addItemPressed:(UIButton *)sender;

@end
