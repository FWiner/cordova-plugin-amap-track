var exec = require('cordova/exec');

//单次定位
exports.getCurrentPosition = function (arg0, success, error) {
    exec(success, error, 'AmapTrack', 'getCurrentPosition', [arg0]);
};

//猎鹰初始化
exports.init = function (arg0, success, error) {
    exec(success, error, 'AmapTrack', 'init', [arg0]);
};

//猎鹰服务停止
exports.stopService = function (arg0, success, error) {
    exec(success, error, 'AmapTrack', 'stopTrack', [arg0]);
};

//开始轨迹上报----必须在初始化完成后才能开始
exports.startTrack = function (arg0, success, error) {
    exec(success, error, 'AmapTrack', 'startTrack', [arg0]);
};

//停止轨迹上报
exports.stopTrack = function (arg0, success, error) {
    exec(success, error, 'AmapTrack', 'stopTrack', [arg0]);
};

exports.showMap = function (arg0, success, error) {
    exec(success, error, 'AmapTrack', 'showMap', [arg0]);
};