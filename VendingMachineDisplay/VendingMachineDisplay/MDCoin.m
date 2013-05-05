//
//  MDCoin.m
//  VendingMachineDisplay
//
//  Created by Nicola Miotto on 5/5/13.
//  Copyright (c) 2013 Nicola Miotto. All rights reserved.
//

#import "MDCoin.h"

@implementation MDCoin

- (void) touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    
}

- (void) touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event
{
    UITouch *touch = [touches anyObject];
    CGPoint loc = [touch locationInView:self.superview];
    self.center = loc;
}

- (void) touchesCancelled:(NSSet *)touches withEvent:(UIEvent *)event
{
   
}

- (void) touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event
{
    [[NSNotificationCenter defaultCenter] postNotificationName:MDNotificationCoinDropped
                                                        object:self];
}

// Further methods...
@end
