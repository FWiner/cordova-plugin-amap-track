#import <Cordova/CDVPlugin.h>
#import <AMapFoundationKit/AMapFoundationKit.h>
#import <AMapTrackKit/AMapTrackKit.h>
#import <AMapLocationKit/AMapLocationKit.h>

@interface AmapTrack : CDVPlugin {}

@property (nonatomic, strong) AMapLocationManager *locationManager;

- (void)init:(CDVInvokedUrlCommand*)command;
- (void)startTrack:(CDVInvokedUrlCommand*)command;
- (void)stopTrack:(CDVInvokedUrlCommand*)command;
- (void)getCurrentPosition:(CDVInvokedUrlCommand*)command;

@end
