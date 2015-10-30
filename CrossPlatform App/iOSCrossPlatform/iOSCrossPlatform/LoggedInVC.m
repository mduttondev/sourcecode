//
//  LoggedInVC.m
//  iOSCrossPlatform
//
//  Created by Matthew Dutton on 12/4/14.
//  Copyright (c) 2014 Matthew Dutton. All rights reserved.
//

#import "LoggedInVC.h"

@interface LoggedInVC ()

@end

@implementation LoggedInVC
@synthesize listItems, itemTable, refreshController, objectSelectedId, updateTimer;

- (void)viewDidLoad {
    [super viewDidLoad];
    
    listItems = [[NSMutableArray alloc]init];
    
    updateTimer = [NSTimer scheduledTimerWithTimeInterval: 20.0 target:self selector:@selector(getItemsfromParse) userInfo:nil repeats:YES];
    
    self.refreshController = [[UIRefreshControl alloc] init];
    
    self.refreshController.backgroundColor =
        [UIColor colorWithRed: (229.0f/255.0f) green:(148.0f/255.0f) blue:(0.0f/255.0f) alpha:1];
    
    self.refreshController.tintColor = [UIColor whiteColor];
    
    [self.refreshController addTarget:self
                               action:@selector(getItemsfromParse)
                  forControlEvents:UIControlEventValueChanged];
    
    [itemTable addSubview:refreshController];
    
    
}

-(void)viewWillAppear:(BOOL)animated{
    
    [self getItemsfromParse];
    
    /* if the timer is invalid and the connection is present when
        view reloads from somewhere then  restart the timer. */
    if ( !updateTimer.isValid && [Utils isNetworkReachable] ) {
        
        updateTimer = [NSTimer
                       scheduledTimerWithTimeInterval: 20.0
                       target:self
                       selector:@selector(getItemsfromParse)
                       userInfo:nil
                       repeats:YES];
    }
    
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}



#pragma mark - Parse Call for Items
-(void)getItemsfromParse{
    
    if ( [Utils isNetworkReachable] ) {
        
        PFQuery* query = [PFQuery queryWithClassName:@"Shopping_Item"];
        [query whereKey:@"Owner" equalTo: [PFUser currentUser] ];
        [query orderByAscending:@"createdAt"];
        [query findObjectsInBackgroundWithBlock:^(NSArray *objects, NSError *error) {
            if (!error) {
                
                // clear the array so that its not doubling input
                [listItems removeAllObjects];
                
                /*
                    If the timer was invalidated, and then a return is received,
                    the connection must be back so reinstanciate.
                    This would be mainly if a pull to refresh is done without leaving the
                    current VC  
                   */
                if ( !updateTimer.isValid) {
                    
                    updateTimer = [NSTimer
                                   scheduledTimerWithTimeInterval: 20.0
                                   target:self
                                   selector:@selector(getItemsfromParse)
                                   userInfo:nil
                                   repeats:YES];
                }
                
                
                for (PFObject* obj in objects) {
                    
                    [listItems addObject: obj ];
                }
                
                [self.itemTable reloadData];
                
                [refreshController endRefreshing];
                
                
            } else {
                
                NSLog(@"QUERY FAILED");
                
            }
            
        }];
        
        
    } else {
        
        [self alertUser:@"Network Connection" :@"No Network Connection, Cannot update at this time!"];
        
        // invalidate so that you you dont get the alert every 20 seconds
        [updateTimer invalidate];
    }
    
}


#pragma mark - Table View Setup
-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString* cellIdentifier = @"groceryItem";
    
    GroceryCell* cell = [tableView dequeueReusableCellWithIdentifier: cellIdentifier];
    
    if (cell == nil) {
        cell = [[GroceryCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellIdentifier];
    }
    
    PFObject* itemObj = [listItems objectAtIndex:indexPath.row];
    
    NSString* name = [itemObj valueForKey:@"Item"];
    
    NSNumber* qty = [itemObj valueForKey:@"Qty"];
    
    cell.itemField.text = name;
    
    cell.quantityField.text = [NSString stringWithFormat:@"Qty: %i", qty.intValue];
    
    return cell;
}


-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 50;
}


-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return listItems.count;
}


-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 1;
}


-(CGFloat)tableView:(UITableView *)tableView estimatedHeightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 50;
}


-(void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath{
    
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        
        if ( [Utils isNetworkReachable] ) {
            
            // code to delete-
            // get the object from the array
            PFObject* itemObj = [listItems objectAtIndex:indexPath.row];
            
            // remove the object from the array
            [listItems removeObject:itemObj];
            
            //remove the oject from parse
            [itemObj deleteInBackground];
            
            // reload the data in the table to reflect changes
            [tableView reloadData];
            
        } else  {
            
            [self alertNoNetworkConnection];
            
        }
    }
}


#pragma mark TableViewCell Selection
-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    
    if ([Utils isNetworkReachable]) {
        
        objectSelectedId = [[listItems objectAtIndex:indexPath.row] objectId];
        
        [self performSegueWithIdentifier:@"toEditItem" sender:self];
    
    } else {
        
        [self alertNoNetworkConnection];
        
    }
}


#pragma mark Perform Segue
-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    
    if ( [[segue identifier] isEqualToString:@"toEditItem"] ) {
        
        EditItemVC* vc = [segue destinationViewController];
        
        vc.objectID = self.objectSelectedId;
    }
}





#pragma mark Logout
- (IBAction)logoutPressed:(UIBarButtonItem *)sender {
    // log the user out
    [PFUser logOut];
    
    // invalidate the timer to stop it from executing after the VC is out of memory
    [updateTimer invalidate];
    
    // pop the VC off the stack and return to the log in screen
    [self.navigationController popToRootViewControllerAnimated:YES];
    
}


#pragma mark AlertView

-(void)alertNoNetworkConnection{
    
    [self alertUser:@"Network Connection" :@"No Network Connection, Please Try Again Later!"];
    
}


-(void)alertUser:(NSString*)alertTitle :(NSString*)alertMessage {
    
    UIAlertView* alert = [[UIAlertView alloc]initWithTitle:alertTitle message:alertMessage delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles: nil];
    
    [alert show];
    
}







@end

