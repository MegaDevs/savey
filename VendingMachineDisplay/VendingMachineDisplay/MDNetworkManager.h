//
//  MDNetworkManager.h
//  VendingMachineDisplay
//
//  Created by Nicola Miotto on 5/4/13.
//  Copyright (c) 2013 Nicola Miotto. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef void (^MessageSentBlock) (NSDictionary *json);

@interface MDNetworkManager : NSObject<NSStreamDelegate>
{
    NSInputStream *__inputStream;
    NSOutputStream *__outputStream;
    MessageSentBlock mblock;
}

- (void)initNetworkCommunicationWithMessageSentBlock:(MessageSentBlock)block;
- (void)start;

- (void)sendMessage:(NSDictionary *)json;


@end
