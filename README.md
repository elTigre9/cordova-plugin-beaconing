# cordova-plugin-beaconing
iOS test plugin for ibeacons

Features:
  • Ranging
  • Monitoring (entering beacon region, exiting beacon region)
 
Currently, the plugin allows for one beacon to be ranged or monitored at a time (it's in testing)
 
You can enter your beacon information in the form of an array, but it must be turned into a string in the parameter (see example below). It returns an array with beacon information (UUID, Proximity value, Major, Minor, RSSI)

Example in Ionic:

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
      // const dataArray = data.split(',');
      if(data[0] === 'XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX') { // replace the string with checking an array of uuids
        console.log('found the beacon');
        this.welcomeAlert();
      // then stop ranging
      
      } else {
        console.log('beacon id not compatible');
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
      header: 'You\'ve gained XP!',
      subHeader: 'You found a beacon!',
      message: 'Time to connect with people!',
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
