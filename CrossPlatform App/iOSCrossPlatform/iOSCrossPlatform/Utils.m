//
//  Utils.m
//  iOSCrossPlatform
//
//  Created by Matthew Dutton on 12/3/14.
//  Copyright (c) 2014 Matthew Dutton. All rights reserved.
//

#import "Utils.h"

@implementation Utils


+(NSString *) randomStringWithLength: (int)ofLength {
    
    NSString* chars = @"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    
    NSMutableString *randomString = [NSMutableString stringWithCapacity: ofLength];
    
    for (int i = 0; i < ofLength; i++) {
        
        [randomString appendFormat: @"%C", [chars characterAtIndex: arc4random() % [chars length] ]];
        
    }
    
    return randomString;
}

+ (BOOL) validateEmail: (NSString *) email {
    NSString *emailRegex = @"[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}";
    NSPredicate *emailTest = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", emailRegex];
    
    return [emailTest evaluateWithObject:email];
}



+ (BOOL) isNetworkReachable {
    
    Reachability* reach = [Reachability reachabilityForInternetConnection];
    
    if (reach.isReachable) {
        return true;
    } else {
        return false;
    }
}

@end
