var exec = require('cordova/exec');

exports.init = function (arg0, success, error) {
    exec(success, error, 'AmapTrack', 'init', [arg0]);
};

exports.startTrack = function (arg0, success, error) {
    exec(success, error, 'AmapTrack', 'startTrack', [arg0]);
};

exports.stopTrack = function (arg0, success, error) {
    exec(success, error, 'AmapTrack', 'stopTrack', [arg0]);
};