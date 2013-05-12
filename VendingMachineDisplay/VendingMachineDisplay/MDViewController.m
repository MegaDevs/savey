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
    
    __networkManager = [[MDNetworkManager alloc] initWithMessageSentBlock:^(NSDictionary *json){
            NSLog(@"JSON %@", json);
            if ([json[MDTypeKey] isEqualToString:MD_GET_QWRCODE]) {
                [self didSendQrCode:json];
            }
            else if ([json[MDTypeKey] isEqualToString:MD_SELECT_PRODUCT]){
                [self didSelectProduct:json];
            }
        }];
        [__networkManager requestQRCode];
    
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


- (IBAction)resetConnection:(id)sender
{
    NSString *ip = self.ipField.text;
    if (ip.length > 0) {
        [__networkManager setManagerWithIP:ip];
    }
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
                             view.center = CGPointMake(937,168);
                         }
                         completion:^(BOOL finished){
                             [UIView animateWithDuration:0.6
                                                   delay:0.0
                                                 options:0
                                              animations:^{
                                                  self.coinTaker.transform = view.transform = CGAffineTransformMakeTranslation(200, 0);
                                              }
                                              completion:^(BOOL finished){
                                                  [UIView animateWithDuration:0.4
                                                                        delay:0.0
                                                                      options:UIViewAnimationOptionCurveEaseOut
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
    if ([json[@"success"] boolValue]) {
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

- (IBAction)showAdmin:(id)sender
{
    NSDictionary *json = @{@"qrcode" : @"iVBORw0KGgoAAAANSUhEUgAAAOYAAADmCAIAAABOCG7sAAAABmJLR0QA/wD/AP+gvaeTAAAELElEQVR4nO3dwW7jNhRA0bjo//9yusimgDCARiOS77bn7GM5xgUB0jT5+f7+/oKOv06/Afg9kiVGssRIlhjJEiNZYiRLjGSJkSwxkiVGssRIlhjJEiNZYiRLjGSJkSwxkiVGssRIlhjJEiNZYiRLjGSJkSwxkiVGssRIlhjJEiNZYiRLjGSJkSwxkiVGssRIlhjJEiNZYiRLzN+nHvz5fE49+uvr63rb2Vvv59k9aneevu49P3PqxjijLDGSJUayxEiWGMkSc2zF4GrdDHTnasDZlYf5n+GfM8oSI1liJEuMZImRLDGDVgyuns1Sz86a33r69Vnrdi9cndo/cIdRlhjJEiNZYiRLjGSJGb1isNPZOfJb6wP/B0ZZYiRLjGSJkSwxkiXGisEvrduHf10NuHNGgTWEH0ZZYiRLjGSJkSwxkiVm9IrBzjnyzvWBnf576wxGWWIkS4xkiZEsMZIlZtCKwZyz9X48+97/zn+xbv/AtM9wBaMsMZIlRrLESJYYyRJzbMWg+N33zrMH7yh+hn/OKEuMZImRLDGSJUayxAzaY3D11jn/0+4duL7Os50Jz56+85VXMMoSI1liJEuMZImRLDGfU/O+1iz1x7rbFafd2zj53EWjLDGSJUayxEiWGMkSc2zF4I635tFv/S7g2bOenXWw7lnP3s+VPQZwi2SJkSwxkiVGssTE9hhc7fxO/9k37zv3D9x5+uQ1ojuMssRIlhjJEiNZYiRLzOhzDK52nj+w7tv5t7z1f11N/p2CUZYYyRIjWWIkS4xkiRm9YrBu//y6+e+zXQc7fxfw7H+fszPBKEuMZImRLDGSJUayxAz6VcLOnfl3rDsToHiGoT0G8JBkiZEsMZIlRrLEjN5j8NYs9a25/871gWnnPNhjAA9JlhjJEiNZYiRLzOgVg2nnBlxNm4/Xzyi4wyhLjGSJkSwxkiVGssSMvl1xp533IMw/tWDyGoJRlhjJEiNZYiRLjGSJGXSOwU47Z83rfl9w9jYH5xjALZIlRrLESJYYyRIz6FcJ808+vPPKO+fRb53qcDXt1x//ZpQlRrLESJYYyRIjWWIGrRhcTdu9v+7OhTvemsXv/FRXMMoSI1liJEuMZImRLDGjVwzO2jmv37nKMe20xt9llCVGssRIlhjJEiNZYqwY/NJbs+91f3XndebsDXiLUZYYyRIjWWIkS4xkiRm9YnD2TICzJxbe8eymhjqjLDGSJUayxEiWGMkSM2jFYNppe2dPPnxr/8DZ+xRWMMoSI1liJEuMZImRLDHHbleEZ4yyxEiWGMkSI1liJEuMZImRLDGSJUayxEiWGMkSI1liJEuMZImRLDGSJUayxEiWGMkSI1liJEuMZImRLDGSJUayxEiWGMkSI1liJEuMZImRLDGSJUayxEiWGMkSI1liJEuMZImRLDH/AFOfeMRlEe8sAAAAAElFTkSuQmCC"};
    [self didSendQrCode:json];
    
    self.ipField.hidden = !self.ipField.hidden;
    self.resetConnectionButton.hidden = !self.resetConnectionButton.hidden;
}

@end
