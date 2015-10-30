//
//  AddItemVC.m
//  iOSCrossPlatform
//
//  Created by Matthew Dutton on 12/3/14.
//  Copyright (c) 2014 Matthew Dutton. All rights reserved.
//

#import "AddItemVC.h"

@interface AddItemVC ()

@end

@implementation AddItemVC
@synthesize itemQuantity, itemToAdd;



- (void)viewDidLoad {
    [super viewDidLoad];
    
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]
                                   initWithTarget:self
                                   action:@selector(dismissKeyboard)];
    
    [self.view addGestureRecognizer:tap];
}

- (void) dismissKeyboard {
    
    [self.itemQuantity resignFirstResponder];
    [self.itemToAdd resignFirstResponder];
}


- (IBAction)addItemPressed:(UIButton *)sender {
    
    BOOL fieldsComplete = false;
    
    if ( [itemToAdd.text length] > 0) {
        
        // input validation --> ensuring the text is less than 4 digits
        if ( [itemQuantity.text length] > 0 && [[itemQuantity text]length] <= 4) {
            
            fieldsComplete = true;
            
        } else {
            
            [self alertUser:@"Quantity Error" :@"Quantity must be present and 4 digits or less"];
        }
    }
    
    
    if (fieldsComplete) {
        
        if ( [Utils isNetworkReachable] ) {
           
        
            PFObject* addItem = [PFObject objectWithClassName:@"Shopping_Item"];
            
            addItem[@"Item"] = itemToAdd.text;
            
            addItem[@"Owner"] = [PFUser currentUser];
            
            addItem.ACL = [PFACL ACLWithUser:[PFUser currentUser]];
            
            addItem[@"Item_Id"] = [Utils randomStringWithLength: 10];
            
            addItem[@"Qty"] = @(itemQuantity.text.intValue);
            
            
            [addItem saveInBackgroundWithBlock:^(BOOL succeeded, NSError *error) {
                if (!error) {
                    [self.navigationController popViewControllerAnimated:YES];
                } else {
                    [self alertUser:@"Error" :@"Save Failed, Please try again"];
                }
            }];
            
        } else {
            
            [self alertNoNetworkConnection];
            
        }
        
        
    } else {
        
        [self alertUser:@"Error" : @"Both fields must be complete"];
        
    }
    
}


-(void)alertNoNetworkConnection{
    
    [self alertUser:@"Network Connection" :@"No Network Connection, Please Try Again Later!"];
    
}


-(void)alertUser:(NSString*)title :(NSString*)message{
    
    
    UIAlertView* alert = [[UIAlertView alloc]
                          initWithTitle: title
                          message: message
                          delegate:nil
                          cancelButtonTitle: @"Ok"
                          otherButtonTitles:nil];
    
    [alert show];
}










@end
