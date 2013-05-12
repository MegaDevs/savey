//
//  MDNetworkManager.h
//  VendingMachineDisplay
//
//  Created by Nicola Miotto on 5/4/13.
//  Copyright (c) 2013 Nicola Miotto. All rights reserved.
//

#import <Foundation/Foundation.h>

#define MDTypeKey @"type"

#define MD_GET_QWRCODE @"GET_MACHINE_QRCODE" // qrcode = <byte base 64>
#define MD_ADD_CREDIT @"ADD_CREDIT" // credit = 32
#define MD_SELECT_PRODUCT @"SELECT_PRODUCT" // product = 23423 | return success = [true|false]


typedef void (^MessageSentBlock) (NSDictionary *json);

@interface MDNetworkManager : NSObject<NSStreamDelegate>
{
    NSInputStream *__inputStream;
    NSOutputStream *__outputStream;
    MessageSentBlock __mblock;
}

- (id)initWithMessageSentBlock:(MessageSentBlock)block;

- (void)addCredit:(CGFloat)value;
- (void)selectProduct:(NSInteger)identifier;
- (void)requestQRCode;
- (void)setManagerWithIP:(NSString *)ip;

@end
