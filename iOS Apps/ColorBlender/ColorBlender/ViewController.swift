//
//  ViewController.swift
//  ColorBlender
//
//  Created by Matthew Dutton on 2/5/15.
//  Copyright (c) 2015 Matthew Dutton. All rights reserved.
//

import UIKit

class ViewController: UIViewController {

    @IBOutlet weak var firstColorView: UIView!
    @IBOutlet weak var secondColorView: UIView!
    
    /* Slider Outlets and action */
    @IBOutlet weak var greenSlider: UISlider!;
    @IBOutlet weak var redSlider: UISlider!;
    @IBOutlet weak var blueSlider: UISlider!;
    
    
    /* Blend View outlet Collections */
    @IBOutlet var blendViewCollection: [UIView]!
    @IBOutlet var blendViewCollection_L1: [UIView]!
    @IBOutlet var blendViewCollection_D1: [UIView]!
    @IBOutlet weak var hexLabel: UILabel!
    
    /* RGB info Labels */
    @IBOutlet weak var redLabel: UITextField!
    @IBOutlet weak var greenLabel: UITextField!
    @IBOutlet weak var blueLabel: UITextField!
    
    
    /* Current View to modify or display info about */
    var currentActiveView: UIView?
    

    override func viewDidLoad() {
        super.viewDidLoad()
        
        /* Add Tap Recognisers */
        var tapGesture = UITapGestureRecognizer(target: self, action: "TapGestureRecognized:");
        firstColorView.addGestureRecognizer(tapGesture);
        
        /* Fire the gesture recognizer so that view 1 is selected from the beginning */
        TapGestureRecognized(tapGesture);
        
        tapGesture = UITapGestureRecognizer(target: self, action: "TapGestureRecognized:");
        secondColorView.addGestureRecognizer(tapGesture);
        
        updateBlendedViews();
        
        
        for var i = 0; i < blendViewCollection.count; i++ {
            
            tapGesture = UITapGestureRecognizer(target: self, action: "TapGestureRecognized:");
            blendViewCollection[i].addGestureRecognizer(tapGesture);
            
            tapGesture = UITapGestureRecognizer(target: self, action: "TapGestureRecognized:");
            blendViewCollection_D1[i].addGestureRecognizer(tapGesture);
            
            tapGesture = UITapGestureRecognizer(target: self, action: "TapGestureRecognized:");
            blendViewCollection_L1[i].addGestureRecognizer(tapGesture);
            
        }
        
    }
    
    
    /* Function fired when one of the primary color views are touched */
    func TapGestureRecognized ( sender:UITapGestureRecognizer ){
        
//        /* disable the sliders when something other than the main 2 colors */
        if sender.view?.tag == 5 {
            
            redSlider.enabled = true;
            greenSlider.enabled = true;
            blueSlider.enabled = true;
            
        } else {
            
            redSlider.enabled = false;
            greenSlider.enabled = false;
            blueSlider.enabled = false;
        }
        
        /* if there was a currentActiveview then undo the color */
        if let v = currentActiveView? {
            v.layer.borderWidth = 0;
        }
        
        currentActiveView = sender.view;
        
        currentActiveView?.layer.borderColor = UIColor.whiteColor().CGColor;
        currentActiveView?.layer.borderWidth = 3;
        
        /* Adjust sliders, HEX label and RGB Text Fields To Reflect current selected */
        let intensity = sender.view!.layer.backgroundColor.RGBA();
        
        /* Update Sliders */
        redSlider.value = Float(intensity.red);
        greenSlider.value = Float(intensity.green);
        blueSlider.value = Float(intensity.blue);
        
        updateHexAndRGBLabels(intensity);
        
    
    }
    
    
    @IBAction func sliderChangedValue(sender: UISlider) {
        
        if let intensities = currentActiveView?.layer.backgroundColor.RGBA() {
        
            switch sender.tag {
                
            case 10:
                currentActiveView?.layer.backgroundColor =
                    UIColor(red: CGFloat(sender.value), green: intensities.green, blue: intensities.blue, alpha: 1.0).CGColor;
            
            case 20:
                currentActiveView?.layer.backgroundColor =
                    UIColor(red: intensities.red, green: CGFloat(sender.value), blue: intensities.blue, alpha: 1.0).CGColor;
                
            case 30:
                currentActiveView?.layer.backgroundColor =
                    UIColor(red: intensities.red, green: intensities.green, blue: CGFloat(sender.value), alpha: 1.0).CGColor;
                
            default:
                println("Default Hit");
                
            }
        
            updateBlendedViews();
            
            updateHexAndRGBLabels(intensities);
        }
        
        
    }
    
    
    
    func updateHexAndRGBLabels(colors:(red:CGFloat, green:CGFloat, blue:CGFloat, alpha:CGFloat)) {
        
        var _red = Int(colors.red * 255);
        
        var _green = Int(colors.green * 255);
        
        var _blue = Int(colors.blue * 255);
        
        hexLabel.text = "#" + String(format: "%02X", _red) +
        String(format: "%02X", _green) + String(format: "%02X", _blue)
        
        redLabel.text! = String(_red)
        greenLabel.text! = String(_green)
        blueLabel.text! = String(_blue)
        
    }
    
    
    
    
    func updateBlendedViews() {
        
        /* Get the color valuyes from the main color views */
        let color1 = firstColorView.layer.backgroundColor.RGBA();
        let color2 = secondColorView.layer.backgroundColor.RGBA();
        
        
        /* calculate the delta for each blended view */
        let redDelta = (color1.red - color2.red) / 8;
        let greenDelta = (color1.green - color2.green) / 8;
        let blueDelta = (color1.blue - color2.blue) / 8;
        
    
        /* loop through and change the blend views */
        var _red:CGFloat = 0;
        var _green:CGFloat = 0;
        var _blue:CGFloat = 0;
        
        for var i = 0; i < blendViewCollection.count; i++ {
            
            _red = color1.red - redDelta * CGFloat(i);
            _green = color1.green - greenDelta * CGFloat(i);
            _blue = color1.blue - blueDelta * CGFloat(i);
            
            blendViewCollection[i].layer.backgroundColor =
                UIColor(red: _red, green: _green, blue: _blue, alpha: 1.0).CGColor;

            
            blendViewCollection_D1[i].layer.backgroundColor =
                UIColor(red: _red - (_red * 0.25), green: _green - (_green * 0.25), blue: _blue - (_blue * 0.25), alpha: 1.0).CGColor;

            blendViewCollection_L1[i].layer.backgroundColor =
                UIColor(red: _red + (_red * 0.25), green: _green + (_green * 0.25), blue: _blue + (_blue * 0.25), alpha: 1.0).CGColor;
            
            
        }
        
        
        
        
    }
    
    

    
    
}

