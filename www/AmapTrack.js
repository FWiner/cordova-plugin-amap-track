var exec = require('cordova/exec');

exports.getCurrentPosition = function (arg0, success, error) {
    exec(success, error, 'AmapTrack', 'getCurrentPosition', [arg0]);
};

exports.init = function (arg0, success, error) {
    exec(success, error, 'AmapTrack', 'init', [arg0]);
};

exports.startTrack = function (arg0, success, error) {
    exec(success, error, 'AmapTrack', 'startTrack', [arg0]);
};

exports.stopTrack = function (arg0, success, error) {
    exec(success, error, 'AmapTrack', 'stopTrack', [arg0]);
};

exports.showMap = function (arg0, success, error) {
    exec(success, error, 'AmapTrack', 'showMap', [arg0]);
};