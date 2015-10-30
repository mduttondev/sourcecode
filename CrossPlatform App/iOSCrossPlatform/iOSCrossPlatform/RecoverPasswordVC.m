//
//  RecoverPasswordVC.m
//  iOSCrossPlatform
//
//  Created by Matthew Dutton on 12/3/14.
//  Copyright (c) 2014 Matthew Dutton. All rights reserved.
//

#import "RecoverPasswordVC.h"

@interface RecoverPasswordVC ()

@end

@implementation RecoverPasswordVC
@synthesize emailField;


- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    emailField.delegate = self;
    
    
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]
                                   initWithTarget:self
                                   action:@selector(dismissKeyboard)];
    
    [self.view addGestureRecognizer:tap];
    
    
}


- (void) dismissKeyboard {
    
    [self.emailField resignFirstResponder];
    
}

- (IBAction)recoverButtonPressed:(UIButton *)sender {
    
    if ( [emailField.text length] > 0) {
        
        if ( [Utils validateEmail:emailField.text] ) {
        
            if ( [Utils isNetworkReachable] ) {
                
                [PFUser requestPasswordResetForEmailInBackground:emailField.text block:^(BOOL succeeded, NSError *error) {
                    
                    if (succeeded) {
                        [self alertUser:@"Recovery:" :@"Recovery instructions were sent to the address provided"];
                        [self.navigationController popToRootViewControllerAnimated:YES];
                        
                    } else {
                        
                        [self alertUser:@"Reset Failed:" :@"Please check the email and try again."];
                    }
                }];
                
            } else {
                
                [self alertNoNetworkConnection];
                
            }
            
        } else {
            
            [self alertUser:@"Invalid Email" :@"Please check email address and try again"];
        }
        
    } else {
        
        [self alertUser:@"Error" :@"You must enter a valid email to recover your password"];
        
    }
    
}

-(void)alertNoNetworkConnection{
    
    [self alertUser:@"Network Connection" :@"No Network Connection, Please Try Again Later!"];
    
}


-(void)alertUser:(NSString*)alertTitle :(NSString*)alertMessage {
    
    UIAlertView* alert = [[UIAlertView alloc]initWithTitle:alertTitle message:alertMessage delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles: nil];
    
    [alert show];
}

-(BOOL)textFieldShouldReturn:(UITextField *)textField {
    [textField resignFirstResponder];
    return NO;
}


@end
