//
//  Header.h
//  HelloCordova
//
//  Created by wxy on 2020/7/21.
//

#ifndef Header_h
#define Header_h


#endif /* Header_h */

#import <UIKit/UIKit.h>
#import <MAMapKit/MAMapKit.h>
#import <AMapLocationKit/AMapLocationKit.h>

@protocol ShowMapViewControllerDelegate  //委托协议的声明

@end

@interface ShowMapViewController : UIViewController

@property(assign, nonatomic) id <ShowMapViewControllerDelegate> delegate;

@property (nonatomic, strong) MAMapView *mapView;

@property (nonatomic, strong) AMapLocationManager *locationManager;

- (void)showMap;

@end
