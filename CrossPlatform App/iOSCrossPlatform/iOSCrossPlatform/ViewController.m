//
//  ViewController.m
//  iOSCrossPlatform
//
//  Created by Matthew Dutton on 11/24/14.
//  Copyright (c) 2014 Matthew Dutton. All rights reserved.
//

#import "ViewController.h"

@interface ViewController ()

@end

@implementation ViewController
@synthesize uNameField, pWordField;


- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.navigationController.navigationBar.barTintColor =
        [UIColor colorWithRed: (229.0f/255.0f) green:(148.0f/255.0f) blue:(0.0f/255.0f) alpha:1];
    
    
    self.navigationController.navigationBar.translucent = NO;
    
    uNameField.delegate = self;
    pWordField.delegate = self;

    PFUser* currentUser = [PFUser currentUser];
    if (currentUser) {
        
        // the user was still logged in so will be forwarded to their info
        // with out having to log in again
        if ( [Utils isNetworkReachable] ) {
            
            [self loginSucessfull];
            
        } else {
            
            [self alertNoNetworkConnection];
            
        }
        
    }
    
    
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]
                                   initWithTarget:self
                                   action:@selector(dismissKeyboard)];
    
    [self.view addGestureRecognizer:tap];
    
    
}

- (void) dismissKeyboard {
    
    [self.uNameField resignFirstResponder];
    [self.pWordField resignFirstResponder];
}



- (IBAction)buttonPressed:(UIButton *)sender {

    if (sender.tag == 10 ) {
        
        BOOL fieldsAreFilled = NO;
        
        if ( [uNameField.text length] > 0  ) {
            
            if ( [pWordField.text length] > 0) {
                
                fieldsAreFilled = YES;
                
                if ( [Utils isNetworkReachable] ) {
                
                    [PFUser logInWithUsernameInBackground:uNameField.text password:pWordField.text block:^(PFUser *user, NSError *error) {
                        
                        if (user) {
                            [self loginSucessfull];
                            
                        } else {
                            NSString *errorString = [error userInfo][@"error"];
                            [self alertUser:@"Error" :errorString];
                        }
                        
                    }];
                    
                } else {
                    [self alertNoNetworkConnection];
                }
                
            }
            
        }
        
        
        if ( !fieldsAreFilled ) {
            [self alertUser:@"Error" :@"Both username and password must be completed\nPlease try again"];
        }
        
    }

}

-(void)alertNoNetworkConnection{
    
    [self alertUser:@"Network Connection" :@"No Network Connection, Please Try Again Later!"];
    
}


-(void)loginSucessfull{
    
    
    [self performSegueWithIdentifier:@"toLoggedinFromMain" sender:self];
    
}

-(void)alertUser:(NSString*)alertTitle :(NSString*)alertMessage {
    
    UIAlertView* alert = [[UIAlertView alloc]initWithTitle:alertTitle message:alertMessage delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles: nil];
    
    [alert show];
}

-(BOOL)textFieldShouldReturn:(UITextField*)textField;
{
    NSInteger nextTag = textField.tag + 1;
    // Try to find next responder
    UIResponder* nextResponder = [textField.superview viewWithTag:nextTag];
    if (nextResponder) {
        // Found next responder, so set it.
        [nextResponder becomeFirstResponder];
    } else {
        // Not found, so remove keyboard.
        [textField resignFirstResponder];
    }
    return NO;
}

@end
