import Foundation
import CoreLocation
/*
* Notes: The @objc shows that this class & function should be exposed to Cordova.
*/
@objc(Beaconing) class Beaconing : CDVPlugin, CLLocationManagerDelegate {
    
    var locationManager = CLLocationManager()
    var callBackId = String()
    
    override func pluginInitialize() {
        super.pluginInitialize()
        print("adding delegate")
        self.locationManager = CLLocationManager()
        self.locationManager.delegate = self
        
    }
    
    
    
  @objc(beaconDelegate:) // Declare your function name.
  func beaconDelegate(command: CDVInvokedUrlCommand) { // write the function code.
    self.commandDelegate.run {
        print("beaconDelegate", command.callbackId)
        // get the callbackid
        self.callBackId = command.callbackId
        /*
         * Always assume that the plugin will fail.
         * Even if in this example, it can't.
         */
        // Set the plugin result to fail.
        let pluginResult = CDVPluginResult (status: CDVCommandStatus_NO_RESULT);
        
        pluginResult?.setKeepCallbackAs(true)
        // Send the function result back to Cordova.
        self.commandDelegate!.send(pluginResult, callbackId: command.callbackId);
    }
  }

  @objc(rangeBeacons:)
  func rangeBeacons(command: CDVInvokedUrlCommand) {
    print("rangeBeacons", command.callbackId)
    print("beacon array string: ", command.arguments![0])

        // Set the plugin result to fail.
        var pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: "The Plugin Failed");
        

        // turn the csv style string to an array
    let newBeaconList = (command.arguments![0] as AnyObject).components(separatedBy: ",") // or "\n"
        print("newBeaconList")
        print(newBeaconList)

        let uuid = UUID(uuidString: newBeaconList[0]) // UUID(uuidString: "426C7565-4368-6172-6D42-6561636F6E73")
        let major: CLBeaconMajorValue = CLBeaconMajorValue(Int(newBeaconList[1])!) // 3838
        let minor: CLBeaconMinorValue = CLBeaconMinorValue(Int(newBeaconList[2])!) // 4949
        let id: String = newBeaconList[3] // "bennyBeacon"

        let region = CLBeaconRegion(proximityUUID: uuid!, major: major, minor: minor, identifier: id)

//        self.locationManager.startRangingBeacons( in: region)
        self.locationManager.startMonitoring(for: region)
        // return region as an object
        pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: "region set!");
        self.commandDelegate!.send(pluginResult, callbackId: command.callbackId)
  }

  @objc(authManager: ) // declare function name
    func authManager(command: CDVInvokedUrlCommand) { // [uuid, major, minor, identifier/name]
    print("auth manager: ", command.callbackId)
        var pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: "Authorization and management Failed");


         // Set the plugin result to succeed.
        pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: "Authorization and management succeeded");
    
        // Send the function result back to Cordova.
        self.commandDelegate!.send(pluginResult, callbackId: command.callbackId)
    }

    
    // MARK: Delegate functions
    
    @objc func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        print("Permission granted!")
    }
    
    @objc func locationManager(_ manager: CLLocationManager, didRangeBeacons beacons: [CLBeacon], in: CLBeaconRegion) {
        
        print("started ranging beacons")
        guard let discoveredBeaconProximity = beacons.first?.proximity
            else {
                print("couldn't find any beacons.")
                return
        }
        
        print("beacon proximity", self.callBackId)
        print(discoveredBeaconProximity.rawValue)
        
        var pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: "discovered beacon failed");
        
        
        // Set the plugin result to succeed.
        pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: "discovered beacon succeeded");
        
//        pluginResult?.setKeepCallbackAs(true)
        
        self.commandDelegate.send(pluginResult, callbackId: self.callBackId)
    }
    
    @objc func locationManager(_ manager: CLLocationManager, didStartMonitoringFor region: CLRegion) {
        print("monitoring beacon")
        self.commandDelegate.run {
            guard (region.identifier) != "" else { print("couldn't find a ranged beacon"); return}
            
            print("within beacon range!", region)
            print(region.identifier)
            
            let pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: region.identifier);
            
            pluginResult?.setKeepCallbackAs(true)
            
            self.commandDelegate.send(pluginResult, callbackId: self.callBackId)
        }
        
        
    }

}
