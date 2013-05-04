//
//  MDAppDelegate.h
//  VendingMachineDisplay
//
//  Created by Nicola Miotto on 5/4/13.
//  Copyright (c) 2013 Nicola Miotto. All rights reserved.
//

#import <UIKit/UIKit.h>

@class MDViewController;

@interface MDAppDelegate : UIResponder <UIApplicationDelegate>

@property (strong, nonatomic) UIWindow *window;

@property (strong, nonatomic) MDViewController *viewController;

@end
