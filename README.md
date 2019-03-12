# cordova-plugin-beaconing
iOS and Android test plugin for ibeacons (a work in progress!)

## Features:
  * Ranging
  * Monitoring (entering beacon region, exiting beacon region)
  
## Prerequesites:
Make sure to add the native package into your node_modules/@ionic-native folder:

1. Go here: https://github.com/elTigre9/ionic-native-beaconing
2. Get the beaconing folder
3. Place it into your @ionic-native folder

## What you should know:
Currently, the plugin allows **for one beacon to be ranged or monitored at a time (it's in testing)**
 
You can enter your beacon information in the form of an array, **but it must be turned into a string in the parameter (see example below)**. The Ranging listener/delegate returns an array with beacon information (UUID, Proximity value, Major, Minor, RSSI). The monitoring listener/delegate returns the beacon name (upon entering or exiting the beacon region)

**For Android, make sure all of your utils, etc. are set to the max target SDK.** At the time of this README update, the Android version I was using was 27.0.1.
Also, two things: 

1. When you create a platform, make sure your build.gradle has a defaultBuildToolsVersion set to the Android version you're targeting (in this case it was 27.0.1).
2. Change the original training app after android platform add is added:
In AndroidManifest you have to change the theme in the opening tag of <activity> to android:theme="@style/Theme.AppCompat.NoActionBar"
**Find in:** platform>android>src>main>Android.xml
**Permanent fix might be adding the following in the plugin.xml (haven't tried it yet):**
```
<edit-config file="AndroidManifest.xml" target="/manifest/application/activity[@android:name='Beaconing']" mode="merge">
   <activity android:theme="@style/Theme.AppCompat.NoActionBar" />
</edit-config>
```

## Example in Ionic:
* Make sure you're importing the plugin into your app.module.ts file, then add it in your providers array!
```
import { Beaconing } from '@ionic-native/beaconing/ngx';

@Component({
  selector: 'app-beacon-test',
  templateUrl: './beacon-test.page.html',
  styleUrls: ['./beacon-test.page.scss'],
})
export class BeaconTestPage implements OnInit {

  constructor(private beaconing: Beaconing, private alertCtrl: AlertController) { }

  ngOnInit() {
    console.log('beacon delegate initializes within plugin! :)');
    // Beacon location permission is requested within the .Swift file

    // set up delegate functions as listeners
    // range beacons
    this.beaconing.rangeBeaconIdListener().then((data) => {
      console.log('Beacon ranging listener good to go! ', data);
      if (data[1] === 1) {
       console.log('You\'re very close!');
      }
    }).catch(err => {
      console.log('Beacon ranging not good to go :( ', err);
    });

    // monitor beacons
    this.beaconing.monitorBeaconIdListener().then((data) => {
      console.log('Monitoring listener good to go! ', data);
    }).catch(err => {
      console.log('Monitoring not good to go :( ', err);
    });

    // if monitoring, then set up entering and leaving beacon region
    this.beaconing.enteredRegionIdListener().then((data) => {
      console.log('Region was entered! ', data);
      if (data === 'bennyBeacon') {
        this.welcomeAlert();
      }
    }).catch(err => {
      console.log('There was an error with region enter: ', err);
    });

    this.beaconing.leftRegionIdListener().then((data) => {
      console.log('Region was exited! ', data);
    }).catch(err => {
      console.log('There was an error with region exit: ', err);
    });
  }

  beginRanging() {
    console.log('ranging!');
    const beaconArray = ['XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX','XXXX','XXXX','beaconName']
    this.beaconing.rangeBeacons(beaconArray.toString()).then((data) => {
      console.log('Ranging beacons good to go!');
    
    }).catch(err => {
      console.log('Ranging beacons not good to go :(');
    });
  }

  beginMonitoring() {
    console.log('monitoring!');
    const beaconArray = ['XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX','XXXX','XXXX','beaconName']
    this.beaconing.monitorBeacons(beaconArray.toString()).then(() => {
      console.log('Monitoring beacons good to go!');
    }).catch(err => {
      console.log('Monitoring beacons not good to go :(');
    });
  }

  async welcomeAlert() {
    const alert = await this.alertCtrl.create({
      header: 'Beacon found!',
      subHeader: 'You found a beacon!',
      buttons: ['Sweet!']
    });

    await alert.present();
    alert.onDidDismiss().then(() => {
      console.log('alert dismissed');
    }).catch(err => {
      console.log('something went wrong: ', err);
    });
  }

}
```
