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

#define MD50CentButton 50
#define MD1EuroButton 1
#define MD2EuroButton 2

#define MDCaffeButton 1
#define MDCappuccinoButton 2

@interface MDViewController ()

@end

@implementation MDViewController

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
    
    [__networkManager start];
    
    double delayInSeconds = 2.0;
    dispatch_time_t popTime = dispatch_time(DISPATCH_TIME_NOW, (int64_t)(delayInSeconds * NSEC_PER_SEC));
    dispatch_after(popTime, dispatch_get_main_queue(), ^(void){
        [__networkManager requestQRCode];
    });
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)didSendQrCode:(id)json
{
    NSData *data = [NSData dataWithBase64EncodedString:json[@"qrcode"]];
    
    //create actual image
    UIImage *image = [UIImage imageWithData:data];
    
    self.qrCode.image = image;
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
    switch ([sender tag]) {
        case MD50CentButton:
            [__networkManager addCredit:0.5];
            break;
        case MD1EuroButton:
            [__networkManager addCredit:1.0];
            break;
        case MD2EuroButton:
            [__networkManager addCredit:2.0];
            break;
        default:
            break;
    }
}

- (IBAction)selectProduct:(id)sender
{
    switch ([sender tag]) {
        case MDCaffeButton:
            [__networkManager selectProduct:42];
            break;
        case MDCappuccinoButton:
            [__networkManager selectProduct:43];
        default:
            break;
    }
}

@end
