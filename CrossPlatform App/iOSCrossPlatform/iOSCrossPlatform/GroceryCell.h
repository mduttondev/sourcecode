//
//  GroceryCell.h
//  iOSCrossPlatform
//
//  Created by Matthew Dutton on 12/3/14.
//  Copyright (c) 2014 Matthew Dutton. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface GroceryCell : UITableViewCell


@property (weak, nonatomic) IBOutlet UILabel *itemField;
@property (weak, nonatomic) IBOutlet UILabel *quantityField;

@end
