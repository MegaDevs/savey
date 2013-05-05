//
//  UIView+Fancy.m
//  JustBook
//
//  Created by Nicola Miotto on 8/17/12.
//  Copyright (c) 2012 JustBook Mobile GmbH. All rights reserved.
//

#import "UIView+Fancy.h"
#import <QuartzCore/QuartzCore.h>

@implementation UIView (Fancy)

- (void)fadeIn {
    
    if (self.hidden) {
        self.alpha = 0.0;
        [UIView animateWithDuration:0.4
                              delay:0.0
                            options:UIViewAnimationOptionAllowUserInteraction
                         animations:^{
                             
                             self.hidden = NO;
                             self.alpha = 1.0;
                         }
                         completion:^(BOOL completed){
                         }];
    }
}

- (void)fadeOut {
    
    if(!self.hidden) {
        [UIView animateWithDuration:0.4
                              delay:0.0
                            options:UIViewAnimationOptionAllowUserInteraction
                         animations:^{
                             self.alpha = 0.0;
                         }
                         completion:^(BOOL completed){
                             self.hidden = YES;
                         }];
        
    }
}
@end
