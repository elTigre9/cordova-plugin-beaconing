var exec = require('cordova/exec');

var PLUGIN_NAME = "Beaconing"; // This is just for code completion uses.

var Beaconing = function () {}; // This just makes it easier for us to export all of the functions at once.
// All of your plugin functions go below this. 
// Note: We are not passing any options in the [] block for this, so make sure you include the empty [] block.
Beaconing.rangeBeaconIdListener = function (onSuccess, onError) {
    exec(onSuccess, onError, PLUGIN_NAME, "rangeBeaconIdListener", []);
};

Beaconing.monitorBeaconIdListener = function (onSuccess, onError) {
    exec(onSuccess, onError, PLUGIN_NAME, "monitorBeaconIdListener", []);
};

Beaconing.enteredRegionIdListener = function (onSuccess, onError) {
    exec(onSuccess, onError, PLUGIN_NAME, "enteredRegionIdListener", []);
};

Beaconing.leftRegionIdListener = function (onSuccess, onError) {
    exec(onSuccess, onError, PLUGIN_NAME, "leftRegionIdListener", []);
};

Beaconing.rangeBeacons = function (options, onSuccess, onError) {
    exec(onSuccess, onError, PLUGIN_NAME, "rangeBeacons", [options]);
};

Beaconing.monitorBeacons = function (options, onSuccess, onError) {
    exec(onSuccess, onError, PLUGIN_NAME, "monitorBeacons", [options]);
};

module.exports = Beaconing;