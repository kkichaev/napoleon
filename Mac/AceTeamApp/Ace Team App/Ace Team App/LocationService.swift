//
//  LocationService.swift
//  AceTeamApp
//
//  Created by Denis Mosyagin on 29.03.2023.
//

import Foundation
import CoreLocation
import Combine
import NapoleonCore

class LocationService : NSObject, CLLocationManagerDelegate {
    
    static let shared = LocationService()
    
    let refreshHandler : AnyCancellable?
    var locMgr : CLLocationManager?
    var writer : GRSDBWriter
    
    private var lastLocation : CLLocation?
    private var lastLocationTime : TimeInterval?
    
    func configureManager() {
        let lm = locMgr!
        lm.desiredAccuracy = kCLLocationAccuracyBest
        lm.distanceFilter = Config.locationDistanceFilter
    }
    
    func prepare() {
        if locMgr == nil {
            locMgr = CLLocationManager()
            locMgr?.delegate = self
        }
        
        let lm = locMgr!
        if Config.GPSTrackingWanted {
            if lm.authorizationStatus != .authorizedAlways {
                lm.requestAlwaysAuthorization()
            } else {
                configureManager()
                lm.startUpdatingLocation()
            }
        } else {
            lm.stopUpdatingLocation()
        }
    }
    
    var currentLocation : CLLocation? {
        if lastLocationTime != nil {
            if Date().timeIntervalSince1970 - lastLocationTime! > Config.keepLocationInterval {
                lastLocation = nil
                lastLocationTime = nil
            }
        }
        return lastLocation
    }
    
    func allowCreate(org: Org, docType: DocType) -> Bool {
        return Config.docRadius.allowed(currentLocation, forType: docType, forOrg: org)
    }
    
    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        if manager.authorizationStatus == .authorizedAlways {
            if Config.GPSTrackingWanted {
                configureManager()
                locMgr?.startUpdatingLocation()
            }
        }
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        lastLocation = locations.last
        lastLocationTime = Date().timeIntervalSince1970
        
        let pos = GPSPos.from(src: lastLocation!)
        writer.write(pos)
    }
    
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        let code = (error as NSError).code
        
        if code == CLError.denied.rawValue {
            
        }
    }
    
    private override init() {
        writer = GRSDBWriter()
        refreshHandler = Exchange.closePublisher.sink{ _ in LocationService.shared.prepare() }
    }
}
