//
//  MDViewController.h
//  VendingMachineDisplay
//
//  Created by Nicola Miotto on 5/4/13.
//  Copyright (c) 2013 Nicola Miotto. All rights reserved.
//

#import <UIKit/UIKit.h>

@class MDNetworkManager;

@interface MDViewController : UIViewController
{
    MDNetworkManager *__networkManager;
}

@property (nonatomic, strong) IBOutlet UIImageView *qrCode;
@property (nonatomic, strong) IBOutlet UIImageView *coinTaker;

@property (nonatomic, strong) IBOutlet UITextField *ipField;
@property (nonatomic, strong) IBOutlet UIButton    *resetConnectionButton;

@property (nonatomic, strong) IBOutlet UIView *coin10;
@property (nonatomic, strong) IBOutlet UIView *coin20;
@property (nonatomic, strong) IBOutlet UIView *coin50;
@property (nonatomic, strong) IBOutlet UIView *coin100;
@property (nonatomic, strong) IBOutlet UIView *coin200;

- (IBAction)addCredit:(id)sender;
- (IBAction)selectProduct:(id)sender;

- (IBAction)showAdmin:(id)sender;

- (IBAction)resetConnection:(id)sender;

@end
