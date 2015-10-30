//
//  EditItemVC.m
//  iOSCrossPlatform
//
//  Created by Matthew Dutton on 12/11/14.
//  Copyright (c) 2014 Matthew Dutton. All rights reserved.
//

#import "EditItemVC.h"

@interface EditItemVC ()

@end

@implementation EditItemVC
@synthesize objectID, itemName, itemQuantity, editItem;

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self getParseObject:objectID];
    
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]
                                   initWithTarget:self
                                   action:@selector(dismissKeyboard)];
    
    [self.view addGestureRecognizer:tap];
    
}

- (void) dismissKeyboard {
    
    [self.itemName resignFirstResponder];
    [self.itemQuantity resignFirstResponder];
}

#pragma mark -Get Parse Object
-(void)getParseObject:(NSString*) itemID{
    
    if ([Utils isNetworkReachable]) {
        
        PFQuery* query = [PFQuery queryWithClassName:@"Shopping_Item"];
        
        [query whereKey:@"objectId" equalTo:itemID];
        
        [query whereKey:@"Owner" equalTo:[PFUser currentUser]];
        
        [query findObjectsInBackgroundWithBlock:^(NSArray *objects, NSError *error) {
            
            if (!error) {
                
                if ( [objects count] == 1 ) {
                    
                    editItem = [objects objectAtIndex:0];
                    
                    itemName.text = editItem[@"Item"];
                    
                    [itemQuantity setText:[NSString stringWithFormat:@"%ld", [editItem[@"Qty"]longValue]]];
                    
                }
            }
        }];
        
    } else {
        
        [self alertNoNetworkConnection];
        [self.navigationController popViewControllerAnimated:YES];
        
    }
}

#pragma mark -Button Press
- (IBAction)saveChangesPressed:(UIButton *)sender {
    
    if ([Utils isNetworkReachable]) {
        
        [self submitChanges];
        
    } else {
        
        [self alertNoNetworkConnection];
    }
    
    
}

#pragma mark -Submitting Saved Fields
-(void) submitChanges{
    
    BOOL fieldsComplete = false;
    
    if ([itemName text] > 0 ) {
        
        // input validation --> ensuring the text is less than 4 digits
        if ( [[itemQuantity text] length] > 0 && [[itemQuantity text]length] <= 4 ) {
            
            fieldsComplete = YES;
        
        } else {
            
            [self alertUser:@"Quantity Error" :@"Quantity must be present and 4 digits or less"];
        }
        
    }
    
    if (fieldsComplete) {
    
        if ([Utils isNetworkReachable]) {
        
            editItem[@"Item"] = [itemName text];
            
            editItem[@"Qty"] = @([[itemQuantity text]integerValue]);
            
            [editItem saveInBackgroundWithBlock:^(BOOL succeeded, NSError *error) {
                if (succeeded) {
                    [self.navigationController popViewControllerAnimated:YES];
                } else {
                    [self alertUser:@"Oops" :@"Something happened, please try again!"];
                }
            }];
            
        } else {
            
            [self alertNoNetworkConnection];
        
        }
    }
}



#pragma mark -Alert Methods
-(void) alertNoNetworkConnection{
    
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
