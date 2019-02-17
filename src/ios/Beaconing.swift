import Foundation
import CoreLocation
/*
* Notes: The @objc shows that this class & function should be exposed to Cordova.
*/
@objc(Beaconing) class Beaconing : CDVPlugin {
  @objc(beaconDelegate:) // Declare your function name.
  func beaconDelegate(command: CDVInvokedUrlCommand) { // write the function code.
    
    print("beaconDelegate")

    /* 
     * Always assume that the plugin will fail.
     * Even if in this example, it can't.
     */
    // Set the plugin result to fail.
    var pluginResult = CDVPluginResult (status: CDVCommandStatus_ERROR, messageAs: "The Plugin Failed");
    // Set the plugin result to succeed.
        pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: "The plugin succeeded");
    // Send the function result back to Cordova.
    self.commandDelegate!.send(pluginResult, callbackId: command.callbackId);
  }

  @objc(rangeBeacons:)
  func rangeBeacons(command: CDVInvokedUrlCommand) {
    print("rangeBeacons")
    print(command.arguments![0])
    // Set the plugin result to fail.
    var pluginResult = CDVPluginResult (status: CDVCommandStatus_ERROR, messageAs: "The Plugin Failed");
    // Set the plugin result to succeed.
        pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: "The plugin succeeded");
    // Send the function result back to Cordova.
    self.commandDelegate!.send(pluginResult, callbackId: command.callbackId);
  }

}