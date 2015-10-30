//
//  Utils.h
//  iOSCrossPlatform
//
//  Created by Matthew Dutton on 12/3/14.
//  Copyright (c) 2014 Matthew Dutton. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Reachability.h"
@interface Utils : NSObject

+(NSString *) randomStringWithLength: (int)ofLength;

+ (BOOL) validateEmail: (NSString *) email;

+ (BOOL) isNetworkReachable;

@end
