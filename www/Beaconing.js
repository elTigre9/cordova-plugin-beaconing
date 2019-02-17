var exec = require('cordova/exec');

var PLUGIN_NAME = "Beaconing"; // This is just for code completion uses.

var Beaconing = function () {}; // This just makes it easier for us to export all of the functions at once.
// All of your plugin functions go below this. 
// Note: We are not passing any options in the [] block for this, so make sure you include the empty [] block.
Beaconing.beaconDelegate = function (onSuccess, onError) {
    exec(onSuccess, onError, PLUGIN_NAME, "beaconDelegate", []);
};

Beaconing.authManager = function (onSuccess, onError) {
    exec(onSuccess, onError, PLUGIN_NAME, "authManager", []);
};

Beaconing.rangeBeacons = function (options, onSuccess, onError) {
    exec(onSuccess, onError, PLUGIN_NAME, "rangeBeacons", [options]);
};

module.exports = Beaconing;