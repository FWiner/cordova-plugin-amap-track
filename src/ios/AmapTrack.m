/********* AmapTrack.m Cordova Plugin Implementation *******/

#import "AmapTrack.h"


@implementation AmapTrack


-(NSString *)getAMapApiKey{
    return [[[NSBundle mainBundle] infoDictionary] objectForKey:@"AMapApiKey"];
}

- (void) initConfig{
    [AMapServices sharedServices].apiKey = [self getAMapApiKey];
}

- (void)configLocationManager
{
    if(!self.locationManager){
        self.locationManager = [[AMapLocationManager alloc] init];
           
           self.locationManager.delegate = self;
           
           //设置期望定位精度
           [self.locationManager setDesiredAccuracy:kCLLocationAccuracyHundredMeters];
           
           //设置不允许系统暂停定位
           [self.locationManager setPausesLocationUpdatesAutomatically:NO];
           
           //设置允许在后台定位
           [self.locationManager setAllowsBackgroundLocationUpdates:YES];
           
           //设置定位超时时间
           [self.locationManager setLocationTimeout:2];
           
           //设置逆地理超时时间
           [self.locationManager setReGeocodeTimeout:2];
           
           //设置开启虚拟定位风险监测，可以根据需要开启
           [self.locationManager setDetectRiskOfFakeLocation:NO];
    }
    
}

- (void)getCurrentPosition:(CDVInvokedUrlCommand*)command

{
    [self initConfig];
    [self configLocationManager];
    // 带逆地理（返回坐标和地址信息）。将下面代码中的 YES 改成 NO ，则不会返回地址信息。
    [self.locationManager requestLocationWithReGeocode:YES completionBlock:^(CLLocation *location, AMapLocationReGeocode *regeocode, NSError *error) {
            
            CDVPluginResult* pluginResult = nil;
            if (error)
            {
                NSLog(@"locError:{%ld - %@};", (long)error.code, error.localizedDescription);
                
                if (error.code == AMapLocationErrorLocateFailed)
                {
                    NSString *errorCode = [NSString stringWithFormat: @"%ld", (long)error.code];
                    NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:
                                          errorCode,@"errorCode",
                                          error.localizedDescription,@"errorInfo",
                                          nil];
                    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:dict];
                    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                }
            }
            
            NSLog(@"location:%@", location);
            
            if (regeocode)
            {
                NSLog(@"reGeocode:%@", regeocode);
                 if (regeocode)
                   {
                       NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:
                                             regeocode.province,@"province",
                                             regeocode.city,@"cityName",
                                             regeocode.district,@"district",
                                             @(location.coordinate.latitude),@"latitude",
                                             @(location.coordinate.longitude),@"longitude",
                                             nil];
                       pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:dict];
                       [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                   }
            }
    }];
}

- (void)init:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    NSString* echo = [command.arguments objectAtIndex:0];

    if (echo != nil && [echo length] > 0) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:echo];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}


- (void)startTrack:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    NSString* echo = [command.arguments objectAtIndex:0];

    if (echo != nil && [echo length] > 0) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:echo];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}


- (void)stopTrack:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    NSString* echo = [command.arguments objectAtIndex:0];

    if (echo != nil && [echo length] > 0) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:echo];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)amapLocationManager:(AMapLocationManager *)manager doRequireLocationAuth:(CLLocationManager*)locationManager
{
    [locationManager requestAlwaysAuthorization];
}

@end
