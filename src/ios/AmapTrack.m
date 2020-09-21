/********* AmapTrack.m Cordova Plugin Implementation *******/

#import "AmapTrack.h"

@interface AmapTrack ()

@property (nonatomic, strong) CDVInvokedUrlCommand *trackInitCommand;
@property (nonatomic, strong) CDVInvokedUrlCommand *trackStartCommand;
@property (nonatomic, strong) CDVInvokedUrlCommand *trackStopCommand;
@property (nonatomic, strong) NSString *sid;
@property (nonatomic, strong) NSString *tid;
@property (nonatomic, strong) NSString *trid;

@end

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


- (void)showMap:(CDVInvokedUrlCommand*)command

{
    [self initConfig];
    
    self.showMapViewController = [[ShowMapViewController alloc] init];
    
    self.showMapViewController.delegate = self;
    
    self.showMapViewController.modalPresentationStyle = UIModalPresentationFullScreen;
   
    [self.viewController presentViewController:self.showMapViewController animated:YES completion:nil];
    
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
                                             regeocode.formattedAddress,@"address",
                                             regeocode.POIName,@"poiname",
                                             regeocode.province,@"province",
                                             regeocode.city,@"city",
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
    self.trackInitCommand = command;
    NSDictionary *paramOptions = [command.arguments objectAtIndex: 0];
    self.sid = [[paramOptions objectForKey:@"serviceId"] stringValue];
    self.tid = [[paramOptions objectForKey:@"terminalId"] stringValue];
    self.trid = [[paramOptions objectForKey:@"traceId"] stringValue];
    NSLog(@"sid:%@",self.sid);
    
    [self initConfig];
    
    // 创建service id
    AMapTrackManagerOptions *option = [[AMapTrackManagerOptions alloc] init];
    option.serviceID = self.sid;
    
    // 初始化AMapTrackManager
    self.trackManager = [[AMapTrackManager alloc] initWithOptions:option];
    self.trackManager.delegate = self;
    
    // 配置定位属性
    [self.trackManager setAllowsBackgroundLocationUpdates:YES];
    [self.trackManager setPausesLocationUpdatesAutomatically:NO];
    
    // 配置本地缓存大小（无法正常上报轨迹点时将未成功上报的轨迹点缓存在本地）
    [self.trackManager setLocalCacheMaxSize:50];
    
    // 开始服务
    AMapTrackManagerServiceOption *serviceOption = [[AMapTrackManagerServiceOption alloc] init];
    // Terminal ID
    serviceOption.terminalID = self.tid;
    
    [self.trackManager startServiceWithOptions:serviceOption];
}


- (void)startTrack:(CDVInvokedUrlCommand*)command
{
    self.trackStartCommand = command;
    [self.trackManager startGatherAndPack];
}

- (void)pauseTrack:(CDVInvokedUrlCommand*)command
{
    [self.trackManager stopGaterAndPack];
}

- (void)stopService:(CDVInvokedUrlCommand*)command
{
    self.trackStopCommand = command;
    [self.trackManager stopService];
}
#pragma mark - AMapTrackManager Delegate
/**
 * @brief 设定定位信息的采集周期和上传周期，注意：上传周期必须为采集周期的整数倍
 * @param gatherTimeInterval 定位信息的采集周期，单位秒，有效值范围[1, 60]
 * @param packTimeInterval 定位信息的上传周期，单位秒，有效值范围[5, 3000]
 */
- (void)changeGatherAndPackTimeInterval:(NSInteger)gatherTimeInterval packTimeInterval:(NSInteger)packTimeInterval
{
    [self.trackManager changeGatherAndPackTimeInterval:10 packTimeInterval:60];
}

//service 开启服务结果回调
- (void)onStartService:(AMapTrackErrorCode)errorCode {
    if (errorCode == AMapTrackErrorOK) {
        //开始服务成功，继续开启收集上报
        NSLog(@"onStartService success");
        self.trackManager.trackID = self.trid;

        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:nil];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:self.trackInitCommand.callbackId];
    } else {
        //开始服务失败
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:nil];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:self.trackInitCommand.callbackId];
    }
}

//gather 开启采集结果回调
- (void)onStartGatherAndPack:(AMapTrackErrorCode)errorCode {
    if (errorCode == AMapTrackErrorOK) {
        //开始采集成功
        NSLog(@"track success");
        
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:nil];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:self.trackStartCommand.callbackId];

    } else {
        //开始采集失败
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:nil];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:self.trackStartCommand.callbackId];
    }
}

//service 关闭服务结果回调
- (void)onStopService:(AMapTrackErrorCode)errorCode {
    if (errorCode == AMapTrackErrorOK) {
          //关闭成功
          NSLog(@"close service success");
          
          CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:nil];
          [self.commandDelegate sendPluginResult:pluginResult callbackId:self.trackStopCommand.callbackId];

      } else {
          CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:nil];
          [self.commandDelegate sendPluginResult:pluginResult callbackId:self.trackStopCommand.callbackId];
      }
}

//service 关闭采集结果回调
- (void)onStopGatherAndPack:(AMapTrackErrorCode)errorCode {
    
}

- (void)onQueryTrackHistoryAndDistanceDone:(AMapTrackQueryTrackHistoryAndDistanceRequest *)request response:(AMapTrackQueryTrackHistoryAndDistanceResponse *)response{
    
}

#pragma mark - AmapLocationManager Delegate
- (void)amapLocationManager:(AMapLocationManager *)manager doRequireLocationAuth:(CLLocationManager*)locationManager
{
    [locationManager requestAlwaysAuthorization];
}

@end
