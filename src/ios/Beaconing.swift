import Foundation
import CoreLocation
/*
* Notes: The @objc shows that this class & function should be exposed to Cordova.
*/
@objc(Beaconing) class Beaconing : CDVPlugin, CLLocationManagerDelegate {
  @objc(beaconDelegate:) // Declare your function name.
  func beaconDelegate(command: CDVInvokedUrlCommand) { // write the function code.
    
    print("beaconDelegate")

    /* 
     * Always assume that the plugin will fail.
     * Even if in this example, it can't.
     */
    // Set the plugin result to fail.
    var pluginResult = CDVPluginResult (status: CDVCommandStatus_ERROR, messageAs: "The Plugin Failed");
    
    print("Getting delegate!")
    let locationManager: CLLocationManager = CLLocationManager()
    locationManager.delegate = self
    print("Delegate is now self")

    
    // Set the plugin result to succeed.
        pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: "The plugin succeeded");
    // Send the function result back to Cordova.
    self.commandDelegate!.send(pluginResult, callbackId: command.callbackId);
  }

  @objc(rangeBeacons:)
  func rangeBeacons(command: CDVInvokedUrlCommand) {
    print("rangeBeacons")
    print("beacon array string: ", command.arguments![0])

        // Set the plugin result to fail.
        var pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: "The Plugin Failed");
        

        // turn the csv style string to an array
        let newBeaconList = command.arguments![0].components(separatedBy: ",") // or "\n"
        print("newBeaconList")
        print(newBeaconList)

        let uuid = UUID(uuidString: newBeaconList[0]) // UUID(uuidString: "426C7565-4368-6172-6D42-6561636F6E73")
        let major: CLBeaconMajorValue = CLBeaconMajorValue(Int(newBeaconList[1])!) // 3838
        let minor: CLBeaconMinorValue = CLBeaconMinorValue(Int(newBeaconList[2])!) // 4949
        let id: String = newBeaconList[3] // "bennyBeacon"

        let region = CLBeaconRegion(proximityUUID: uuid!, major: major, minor: minor, identifier: id)

        let locationManager: CLLocationManager = CLLocationManager()
        locationManager.startRangingBeacons( in: region)

        // return region as an object
        pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: "region set!");
        self.commandDelegate!.send(pluginResult, callbackId: command.callbackId)
  }

  @objc(authManager: ) // declare function name
    func authManager(command: CDVInvokedUrlCommand) { // [uuid, major, minor, identifier/name]
        
        var pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: "Authorization and management Failed");


        print("Getting permission to range beacons!")
        func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
            print("Permission granted!")
        }

        func locationManager(_ manager: CLLocationManager, didRangeBeacons beacons: [CLBeacon], in: CLBeaconRegion) {

            print("started ranging beacons")
            guard
            let discoveredBeaconProximity = beacons.first?.proximity
            else {
                print("couldn't find any beacons.")
                return
            }

            print("beacon proximity")
            print(discoveredBeaconProximity)
        }

         // Set the plugin result to succeed.
        pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: "Authorization and management succeeded");
        
        // Send the function result back to Cordova.
        self.commandDelegate!.send(pluginResult, callbackId: command.callbackId)
    }

}