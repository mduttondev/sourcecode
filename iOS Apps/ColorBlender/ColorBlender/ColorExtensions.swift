//
//  ColorExtensions.swift
//  ColorBlender
//
//  Created by Matthew Dutton on 2/12/15.
//  Copyright (c) 2015 Matthew Dutton. All rights reserved.
//

import Foundation
import UIKit


extension UIColor {
    
    func RGBA() -> (red:CGFloat, green:CGFloat, blue:CGFloat, alpha:CGFloat){
        
        /* Basically an Array of CGFloats */
        let intensities = CGColorGetComponents(self.CGColor)
        
        /* Return the Tuple of the floats */
        return (intensities[0],intensities[1],intensities[2],intensities[3])
    
    }
    
}


extension CGColor {
    
    func RGBA() -> (red:CGFloat, green:CGFloat, blue:CGFloat, alpha:CGFloat){
        
        /* Basically an Array of CGFloats */
        let intensities = CGColorGetComponents(self)
        
        /* Return the Tuple of the floats */
        return (intensities[0],intensities[1],intensities[2],intensities[3])
        
    }
    
    
}