#import <Cordova/CDVPlugin.h>
#import <AMapFoundationKit/AMapFoundationKit.h>
#import <AMapTrackKit/AMapTrackKit.h>
#import <AMapLocationKit/AMapLocationKit.h>
#import "ShowMapViewController.h"

@interface AmapTrack : CDVPlugin<AMapLocationManagerDelegate, ShowMapViewControllerDelegate,AMapTrackManagerDelegate>

@property (nonatomic, strong) AMapLocationManager *locationManager;

@property(nonatomic, strong) ShowMapViewController *showMapViewController;

@property(nonatomic, strong) AMapTrackManager *trackManager;

- (void)init:(CDVInvokedUrlCommand*)command;
- (void)startTrack:(CDVInvokedUrlCommand*)command;
- (void)stopService:(CDVInvokedUrlCommand*)command;
- (void)getCurrentPosition:(CDVInvokedUrlCommand*)command;

- (void)showMap:(CDVInvokedUrlCommand*)command;

@end
