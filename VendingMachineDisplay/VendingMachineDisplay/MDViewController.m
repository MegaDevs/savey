//
//  MDViewController.m
//  VendingMachineDisplay
//
//  Created by Nicola Miotto on 5/4/13.
//  Copyright (c) 2013 Nicola Miotto. All rights reserved.
//

#import "MDViewController.h"
#import "MDNetworkManager.h"
#import "NSData+Base64.h"
#import <QuartzCore/QuartzCore.h>
#import "UIView+Fancy.h"
#import "MDCoin.h"

#define MD50CentButton 50
#define MD10CentButton 10
#define MD20CentButton 20
#define MD1EuroButton 1

#define MDCaffeButton 1
#define MDTeaButton 2
#define MDLatteButton 3
#define MDOrzoButton 4
#define MDCappuccinoButton 5
#define MDCioccolatoButton 6

@interface MDViewController ()

@end

@implementation MDViewController
{
    UIActivityIndicatorView *__activityIndicator;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
    
    /*__networkManager = [[MDNetworkManager alloc] initWithMessageSentBlock:^(NSDictionary *json){
        NSLog(@"JSON %@", json);
        if ([json[MDTypeKey] isEqualToString:MD_GET_QWRCODE]) {
            [self didSendQrCode:json];
        }
        else if ([json[MDTypeKey] isEqualToString:MD_SELECT_PRODUCT]){
            [self didSelectProduct:json];
        }
    }];*/
    
    [__networkManager start];
    
    double delayInSeconds = 2.0;
    dispatch_time_t popTime = dispatch_time(DISPATCH_TIME_NOW, (int64_t)(delayInSeconds * NSEC_PER_SEC));
    dispatch_after(popTime, dispatch_get_main_queue(), ^(void){
        [__networkManager requestQRCode];
    });
    
    self.qrCode.layer.borderColor = [UIColor brownColor].CGColor;
    self.qrCode.layer.borderWidth = 2.0;
    
    __activityIndicator = [[UIActivityIndicatorView alloc] initWithFrame:self.qrCode.frame];
    __activityIndicator.backgroundColor = [UIColor clearColor];
    __activityIndicator.activityIndicatorViewStyle = UIActivityIndicatorViewStyleGray;
    [self.view addSubview:__activityIndicator];
    [__activityIndicator startAnimating];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(coinDropped:)
                                                 name:MDNotificationCoinDropped
                                               object:nil];
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self
                                                    name:MDNotificationCoinDropped
                                                  object:nil];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)repositionCoin:(UIView *)coin
{
    switch (coin.tag) {
        case 10:
            coin.frame = self.coin10.frame;
            break;
        case 20:
            coin.frame = self.coin20.frame;
            break;
        case 50:
            coin.frame = self.coin50.frame;
            break;
        case 100:
            coin.frame = self.coin100.frame;
            break;
        case 200:
            coin.frame = self.coin200.frame;
            break;
        default:
            break;
    }
}

- (void)coinDropped:(NSNotification *)notification
{
    UIView *view = notification.object;
    if(CGRectIntersectsRect(view.frame, self.coinTaker.frame)){
        
        [UIView animateWithDuration:0.3
                              delay:0.0
                            options:0
                         animations:^{
                             view.center = CGPointMake(938,134);
                         }
                         completion:^(BOOL finished){
                             [UIView animateWithDuration:0.6
                                                   delay:0.0
                                                 options:0
                                              animations:^{
                                                  self.coinTaker.transform = view.transform = CGAffineTransformMakeTranslation(300, 0);
                                              }
                                              completion:^(BOOL finished){
                                                  [UIView animateWithDuration:0.6
                                                                        delay:0.0
                                                                      options:0
                                                                   animations:^{
                                                                       self.coinTaker.transform = CGAffineTransformIdentity;
                                                                   }
                                                                   completion:^(BOOL finished){
                                                                       view.transform = CGAffineTransformIdentity;
                                                                       [self repositionCoin:view];
                                                                       [__networkManager addCredit:view.tag / 100.0];
                                                                   }
                                                   ];
                                              }
                              ];
                         }];
    };
}

- (void)didSendQrCode:(id)json
{
    NSData *data = [NSData dataWithBase64EncodedString:json[@"qrcode"]];
    
    //create actual image
    UIImage *image = [UIImage imageWithData:data];
    self.qrCode.hidden = YES;
    self.qrCode.image = image;
    [__activityIndicator stopAnimating];
    [self.qrCode fadeIn];
}

- (void)didSelectProduct:(id)json
{
    if (json[@"success"]) {
        [[[UIAlertView alloc] initWithTitle:@"Success" message:@"Making product.." delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
    }
    else {
        [[[UIAlertView alloc] initWithTitle:@"Fail" message:@"Put moar money" delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
    }
}

- (IBAction)addCredit:(id)sender
{
    [__networkManager addCredit:(CGFloat)[sender tag] / 100.0];
}

- (IBAction)selectProduct:(id)sender
{
    [__networkManager selectProduct:[sender tag]];
}

@end
