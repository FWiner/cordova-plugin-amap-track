#import <Cordova/CDVPlugin.h>

@interface AmapTrack : CDVPlugin {}

- (void)init:(CDVInvokedUrlCommand*)command;
- (void)startTrack:(CDVInvokedUrlCommand*)command;
- (void)stopTrack:(CDVInvokedUrlCommand*)command;

@end