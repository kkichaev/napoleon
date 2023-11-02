//
//  ProgData.swift
//  ServikoMobile
//
//  Created by ert on 10.10.2020.
//

import Foundation
import UIKit

class ProgData {
    static var loggedIn : Bool = false
    static var appID : String {
        get {
            let ud = UserDefaults()
            
            var val = ud.string(forKey: "AppID")
            if val == nil {
                val = UUID().description.replacingOccurrences(of: "-", with: "")
                ud.setValue(val, forKey: "AppID")
            }
            return val!
        }
        
    }
    static var deviceID = UIDevice.current.identifierForVendor?.uuidString ??
        UUID().description.replacingOccurrences(of: "-", with: "")
        
    static let MAIN_COLOR = UIColor(red: 0.188, green: 0.31, blue: 0.56, alpha: 1)

    static var partners = [Partner]()
    static var curPartner: Partner? = nil {
        willSet {
            handlers.forEach{ $0.value(newValue) }
        }
    }
    
    static var handlers = [String:((_:Partner?)->Void)]()
    
    static func SetPartners(_ partners:[Partner]) {
        for p in partners {
            if !p.isEmpty() {
                self.partners.append(p)
            }
        }
        ProgData.loggedIn = true
        ProgData.SetRootController()
    }
    
    static func AddHandler(_ h: @escaping (_:Partner?)->Void) -> String {
        let id = UUID.init().uuidString
        handlers[id] = h
        return id
    }
    
    static func RemoveHandler(id:String) {
        handlers.removeValue(forKey: id)
    }
    
    static func SetRootController() {
        DispatchQueue.main.async {
            UIApplication.shared.isIdleTimerDisabled = false
            
            if let window = UIApplication.shared.windows.first(where: { $0.isKeyWindow }) {
                let ctrlid = loggedIn ? "mainid" : "loginid"
                window.rootViewController =
                    UIStoryboard(name:"Main",
                                 bundle:nil).instantiateViewController(withIdentifier: ctrlid)
            }
        }
    }
    
    static var phoneID : String {
        get {
            let ud = UserDefaults()
            return ud.string(forKey: "PhoneID") ?? "+7"
        }
        
        set {
            let ud = UserDefaults()
            ud.setValue(newValue, forKey: "PhoneID")
        }
    }
}
