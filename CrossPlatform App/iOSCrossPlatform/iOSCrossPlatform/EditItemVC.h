//
//  EditItemVC.h
//  iOSCrossPlatform
//
//  Created by Matthew Dutton on 12/11/14.
//  Copyright (c) 2014 Matthew Dutton. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Utils.h"
#import <Parse/Parse.h>

@interface EditItemVC : UIViewController <UITextFieldDelegate>

@property (weak, nonatomic) IBOutlet UITextField *itemName;
@property (weak, nonatomic) IBOutlet UITextField *itemQuantity;

- (IBAction)saveChangesPressed:(UIButton *)sender;

@property (strong, nonatomic) NSString* objectID;

@property (strong, nonatomic) PFObject* editItem;

@end
