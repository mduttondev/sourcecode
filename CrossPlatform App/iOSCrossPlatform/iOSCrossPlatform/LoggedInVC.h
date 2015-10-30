//
//  LoggedInVC.h
//  iOSCrossPlatform
//
//  Created by Matthew Dutton on 12/4/14.
//  Copyright (c) 2014 Matthew Dutton. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <Parse/Parse.h>
#import "GroceryCell.h"
#import "Utils.h"
#import "EditItemVC.h"

@interface LoggedInVC : UIViewController <UITableViewDataSource, UITabBarDelegate>



@property (weak, nonatomic) IBOutlet UITableView *itemTable;

@property NSMutableArray* listItems;

@property UIRefreshControl* refreshController;

@property (strong, nonatomic) NSString* objectSelectedId;

@property (strong, nonatomic) NSTimer* updateTimer;

- (IBAction)logoutPressed:(UIBarButtonItem *)sender;

@end
