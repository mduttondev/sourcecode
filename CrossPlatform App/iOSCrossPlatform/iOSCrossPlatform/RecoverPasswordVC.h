//
//  RecoverPasswordVC.h
//  iOSCrossPlatform
//
//  Created by Matthew Dutton on 12/3/14.
//  Copyright (c) 2014 Matthew Dutton. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <Parse/Parse.h>
#import "Utils.h"

@interface RecoverPasswordVC : UIViewController <UITextFieldDelegate>

@property (weak, nonatomic) IBOutlet UITextField *emailField;

- (IBAction)recoverButtonPressed:(UIButton *)sender;

@end
