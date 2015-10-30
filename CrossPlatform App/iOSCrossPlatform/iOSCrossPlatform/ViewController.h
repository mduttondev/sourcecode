//
//  ViewController.h
//  iOSCrossPlatform
//
//  Created by Matthew Dutton on 11/24/14.
//  Copyright (c) 2014 Matthew Dutton. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <Parse/Parse.h>
#import "Utils.h"

@interface ViewController : UIViewController <UITextFieldDelegate>

@property (weak, nonatomic) IBOutlet UITextField *uNameField;
@property (weak, nonatomic) IBOutlet UITextField *pWordField;

- (IBAction)buttonPressed:(UIButton *)sender;

- (void)loginSucessfull;

@end