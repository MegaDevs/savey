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

- (IBAction)addCredit:(id)sender;
- (IBAction)selectProduct:(id)sender;

@end
