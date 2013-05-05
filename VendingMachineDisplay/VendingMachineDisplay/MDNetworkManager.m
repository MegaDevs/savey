//
//  MDNetworkManager.m
//  VendingMachineDisplay
//
//  Created by Nicola Miotto on 5/4/13.
//  Copyright (c) 2013 Nicola Miotto. All rights reserved.
//

#import "MDNetworkManager.h"

#define MDPort 9876
#define MDIP   @"192.168.2.15"
#define IPPROTO_IP              0               /* dummy for IP */
#define IPPROTO_UDP             17              /* user datagram protocol */
#define IPPROTO_TCP             6               /* tcp */

@implementation MDNetworkManager
{
    NSMutableString *stringRead;
    int len;
}

- (id)initWithMessageSentBlock:(MessageSentBlock)block {
    
    self = [super init];
    
    CFReadStreamRef readStream;
    CFWriteStreamRef writeStream;
    CFStreamCreatePairWithSocketToHost(NULL, (CFStringRef)MDIP, MDPort, &readStream, &writeStream);
    __inputStream = (__bridge NSInputStream *)readStream;
    __outputStream = (__bridge NSOutputStream *)writeStream;
    
    [__inputStream setDelegate:self];
    [__outputStream setDelegate:self];
    
    [__inputStream scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
    [__outputStream scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
    
    __mblock = [block copy];
    
    return self;
}

- (void)start
{
    [__inputStream open];
    [__outputStream open];
}

- (void)sendMessage:(NSDictionary *)json
{
    NSString *output = [NSString stringWithFormat:@"%@\n", [json JSONString]];
    NSData *data = [[NSData alloc] initWithData:[output dataUsingEncoding:NSASCIIStringEncoding]];
	NSLog(@"%d", [__outputStream write:[data bytes] maxLength:[data length]]);
}

- (void)addCredit:(CGFloat)value
{
    NSDictionary *json = @{
                           MDTypeKey : MD_ADD_CREDIT,
                           @"credit" : @(value)
                           };
    
    [self sendMessage:json];
}

- (void)selectProduct:(NSInteger)identifier
{
    
    NSDictionary *json = @{
                           MDTypeKey : MD_SELECT_PRODUCT,
                           @"product" : @(identifier)
                           };
    
    [self sendMessage:json];
    
}

- (void)requestQRCode
{
    NSDictionary *json = @{MDTypeKey: MD_GET_QWRCODE};
    [self sendMessage:json];
}

- (void)stream:(NSStream *)theStream handleEvent:(NSStreamEvent)streamEvent {
    
   switch (streamEvent) {
            
		case NSStreamEventOpenCompleted:
			NSLog(@"Stream opened");
			break;
            
		case NSStreamEventHasBytesAvailable:
            NSLog(@"Data arriving...");
            if (theStream == __inputStream) {
                
                uint32_t max_size = 1000000;
                if (stringRead == nil) {
                    len = 0;
                    stringRead = [[NSMutableString alloc] init];
                }
                
                /*while ([__inputStream hasBytesAvailable]) {
                    len += [__inputStream read:[dataRead mutableBytes] maxLength:max_size];
                }*/
                
                uint8_t buffer[10024];
                int buf_len;
                NSString *outp;
                while ([__inputStream hasBytesAvailable]) {
                    buf_len = [__inputStream read:buffer maxLength:sizeof(buffer)];
                    if (buf_len > 0) {
                        outp = [[NSString alloc] initWithBytes:buffer length:buf_len encoding:NSASCIIStringEncoding];
                        [stringRead appendString:outp];
                        if (nil != outp) {
                        }
                    }
                    len += buf_len;
                }
                
                
                NSLog(@"Output %@", stringRead);
                if (stringRead.length > 0 && [stringRead characterAtIndex:[stringRead length] - 1] == '}') {
                    NSDictionary *json = [stringRead objectFromJSONString];
                    __mblock(json);
                    stringRead = nil;
                }
                
                
            }
			break;
            
		case NSStreamEventErrorOccurred:
			NSLog(@"Can not connect to the host!");
			break;
            
		case NSStreamEventEndEncountered:
            NSLog(@"Event random");
			break;
            
		default:
			NSLog(@"Unknown event");
	}
    
}

@end
