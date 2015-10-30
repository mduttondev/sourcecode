//
//  RegisterVC.h
//  iOSCrossPlatform
//
//  Created by Matthew Dutton on 12/3/14.
//  Copyright (c) 2014 Matthew Dutton. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <Parse/Parse.h>
#import "LoggedInVC.h"
#import "Utils.h"

@interface RegisterVC : UIViewController <UITextFieldDelegate>

@property (weak, nonatomic) IBOutlet UITextField *uNameField;
@property (weak, nonatomic) IBOutlet UITextField *pWordField;
@property (weak, nonatomic) IBOutlet UITextField *nameField;
@property (weak, nonatomic) IBOutlet UITextField *emailField;


- (IBAction)registerButtonPressed:(UIButton *)sender;
@end
