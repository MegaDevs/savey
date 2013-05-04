//
//  MDNetworkManager.m
//  VendingMachineDisplay
//
//  Created by Nicola Miotto on 5/4/13.
//  Copyright (c) 2013 Nicola Miotto. All rights reserved.
//

#import "MDNetworkManager.h"

@implementation MDNetworkManager

- (void)initNetworkCommunication {
    CFReadStreamRef readStream;
    CFWriteStreamRef writeStream;
    CFStreamCreatePairWithSocketToHost(NULL, (CFStringRef)@"localhost", 80, &readStream, &writeStream);
    __inputStream = (__bridge NSInputStream *)readStream;
    __outputStream = (__bridge NSOutputStream *)writeStream;
    
    [__inputStream setDelegate:self];
    [__outputStream setDelegate:self];
    
    [__inputStream scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
    [__outputStream scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
}

- (void)start
{
    [__inputStream open];
    [__outputStream open];
}

- (void)sendMessage:(NSDictionary *)json
{
    
}

- (void)stream:(NSStream *)theStream handleEvent:(NSStreamEvent)streamEvent {
    
	switch (streamEvent) {
            
		case NSStreamEventOpenCompleted:
			NSLog(@"Stream opened");
			break;
            
		case NSStreamEventHasBytesAvailable:
            if (theStream == __inputStream) {
                
                uint8_t buffer[1024];
                int len;
                
                while ([__inputStream hasBytesAvailable]) {
                    len = [__inputStream read:buffer maxLength:sizeof(buffer)];
                    if (len > 0) {
                        
                        NSString *output = [[NSString alloc] initWithBytes:buffer length:len encoding:NSASCIIStringEncoding];
                        
                        if (nil != output) {
                            NSLog(@"server said: %@", output);
                        }
                    }
                }
            }
			break;
            
		case NSStreamEventErrorOccurred:
			NSLog(@"Can not connect to the host!");
			break;
            
		case NSStreamEventEndEncountered:
			break;
            
		default:
			NSLog(@"Unknown event");
	}
    
}

@end
