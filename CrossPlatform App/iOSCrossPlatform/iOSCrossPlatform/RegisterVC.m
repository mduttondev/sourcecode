//
//  RegisterVC.m
//  iOSCrossPlatform
//
//  Created by Matthew Dutton on 12/3/14.
//  Copyright (c) 2014 Matthew Dutton. All rights reserved.
//

#import "RegisterVC.h"


@interface RegisterVC ()

@end

@implementation RegisterVC
@synthesize uNameField, pWordField, nameField, emailField;

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.uNameField.delegate = self;
    self.pWordField.delegate = self;
    self.nameField.delegate = self;
    self.emailField.delegate = self;
    
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]
                                   initWithTarget:self
                                   action:@selector(dismissKeyboard)];
    
    [self.view addGestureRecognizer:tap];
    
    
}


- (void) dismissKeyboard {
    
    [self.uNameField resignFirstResponder];
    [self.pWordField resignFirstResponder];
    [self.nameField resignFirstResponder];
    [self.emailField resignFirstResponder];
    
}



- (IBAction)registerButtonPressed:(UIButton *)sender {
    BOOL areFieldsFilled = false;
    
    if ( [uNameField.text length] > 0) {
        
        if ( [pWordField.text length] > 0) {
            
            if ( [nameField.text length] > 0) {
                
                if ( ( [emailField.text length] > 0 && [Utils validateEmail:emailField.text] ) ) {
                    
                        areFieldsFilled = true;
                    
                } else {
                    
                    [self alertUser:@"Invaid Email" :@"Please Correct the email address and try again"];
                }
            }
        }
    }
    
    if (areFieldsFilled) {
        [self registerUser];
        
    } else {
        
        [self alertUser:@"Field Error" :@"Please ensure all fields are complete and try again"];
        
    }
    
}


-(void)registerUser{
    
    if ( [Utils isNetworkReachable] ) {
    
        PFUser* newUser = [PFUser user];
        newUser.username = uNameField.text;
        newUser.password = pWordField.text;
        newUser.email = emailField.text;
        
        newUser[@"Name"] = nameField.text;
        newUser[@"User_ID"] = [Utils randomStringWithLength:16];
        
        [newUser signUpInBackgroundWithBlock:^(BOOL succeeded, NSError *error) {
            if (!error) {
              
                
                [self performSegueWithIdentifier:@"toLoggedInFromReg" sender:self];
                
            } else {
                
                NSString* errorMessage = [error userInfo][@"error"];
                
                [self alertUser:@"Oops" : errorMessage];
                
            }
        }];
        
    } else {
        
        [self alertNoNetworkConnection];
        
    }
    
}


-(BOOL)textFieldShouldReturn:(UITextField*)textField {
    
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


-(void)alertNoNetworkConnection{
    
    [self alertUser:@"Network Connection" :@"No Network Connection, Please Try Again Later!"];
    
}


-(void)alertUser:(NSString*)alertTitle :(NSString*)alertMessage {
    
    UIAlertView* alert = [[UIAlertView alloc]initWithTitle:alertTitle message:alertMessage delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles: nil];
    
    [alert show];
}

@end
