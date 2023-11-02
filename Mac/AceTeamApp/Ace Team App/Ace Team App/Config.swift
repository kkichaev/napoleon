//
//  Config.swift
//  AceTeamApp
//
//  Created by Denis Mosyagin on 08.03.2023.
//

import SwiftUI
import NapoleonCore
import CoreLocation

class DocRadiusInfo {
    static let ORG_CACHE_TIME = 5.0 * 60;
    
    var radius: Double?
    var types = [DocType]()
    
    private var checkTime : TimeInterval?
    private var cachedOrg : Org?
    
    func allowed(_ location: CLLocation?, forType: DocType, forOrg: Org) -> Bool {
        let _ol = forOrg.location
        if radius == nil || !types.contains(where: { $0.name == forType.name}) || _ol == nil {
            return true
        }
        
        if let l = location {
            if l.distance(from: _ol!) < radius! {
                checkTime = Date().timeIntervalSince1970
                cachedOrg = forOrg
                return true
            }
            
            return false
        }
        
        return checkCached(forOrg)
        
    }
    
    private func checkCached(_ org: Org) -> Bool {
        if checkTime == nil || cachedOrg == nil || cachedOrg != org {
            return false
        }
        
        let diff = Date().timeIntervalSince1970 - checkTime!
        if diff < DocRadiusInfo.ORG_CACHE_TIME {
            // extend check time
            checkTime = Date().timeIntervalSince1970
            return true
        }
        return false
    }
}

struct Config {
    private static let PRIVAT_PREFIX = "aceteam.prvt."
    private static var radiusData : DocRadiusInfo?

    static func read(_ key:String, withPrefix:Bool = false) -> String {
        let pref = UserDefaults.standard
        return (pref.string(forKey: withPrefix ? Config.PRIVAT_PREFIX + key : key) ?? "")
    }
    
    static func clear() {
        radiusData = nil
        let pref = UserDefaults.standard
        for key in Array(pref.dictionaryRepresentation().keys) {
            if !key.starts(with: Config.PRIVAT_PREFIX) {
                pref.removeObject(forKey: key)
            }
        }
    }
    
    static func write(_ key:String, _ value:String, withPrefix:Bool = false) -> Void {
        let pref = UserDefaults.standard
        pref.set(value, forKey: withPrefix ? Config.PRIVAT_PREFIX + key : key)
    }
    
    static var serverId : String {
        get { return read("ServerId", withPrefix: true) }
        set { write("ServerId", newValue, withPrefix: true) }
    }
    
    static var userid: String {
        get { return read("UserID", withPrefix: true) }
        set { write("UserID", newValue, withPrefix: true) }
    }
    
    static var uid : String {
        get { return read("UUID", withPrefix: true) }
        set { write("UUID", newValue, withPrefix: true) }
    }
    
    static var lastSync : Date? {
        get {
            let val = UserDefaults.standard.double(forKey: Config.PRIVAT_PREFIX + "syncTime")
            if val > 1000 {
                return Date(timeIntervalSince1970: val)
            }
            return nil
        }
        set {
            UserDefaults.standard.set(newValue!.timeIntervalSince1970, forKey: Config.PRIVAT_PREFIX + "syncTime")
        }
    }
    
    static let DEFAULT_ORG_RADIUS = 400.0
    static var docRadius : DocRadiusInfo {
        if radiusData == nil {
            radiusData = DocRadiusInfo()
            var v = read("Disposition")
            if !v.isEmpty {
                v = read("OrgRadius")
                radiusData!.radius = Double(v) ?? Config.DEFAULT_ORG_RADIUS
                for docName in read("ORG_DISPOSITION_DOCS").components(separatedBy: ",") {
                    if let dt = DocType.getType(forName: docName) {
                        radiusData?.types.append(dt)
                    }
                }
            }
        }
        return radiusData!
    }
    
    static var locationDistanceFilter : Double { 100 }
    
    static var GPSTrackingWanted : Bool { read("Tracking") == "GPSroute" }
    
    static var keepLocationInterval = 15.0 * 60;

    static var hideOldOrgs = true
    static var hideOldPrice = true
}

class ProgColor {
    static let error = Color("error")
    static let folder_back = Color("folder_back")
    static let folder_fore = Color("folder_fore")
    static let add_button = Color("add_button")
}

class ConfigHelper {
    static var HOST = "https://napmobile.ru"
    static let TIMEOUT = 60.0
    
    var result = ServerAnswer()

    func requestLink(_ code: String, _ handler:@escaping (_ sender:ConfigHelper, _ user:LinkedUser?) -> Void) -> Void {
//        let answ = """
//[{"data":[{"message":"","response":1}],"name":"ServerAnswer"},{"data":[{"code":"5e55b5a786bd4184845acef6aa2d6d0e","id":"2f84ddbc-c11f-11ea-b5c3-d05099274bdb","server_code":"5bf230afcf76fbeb"}],"name":"LinkedUsers"}]
//""".data(using: .utf8)!
//        let ap = AnswerPool(answ)
//        let ll:[LinkedUser] = ap.get(forName: "LinkedUsers")
//        print(ll.count)
        
        sendRequest("/api/link_user?code=\(code)&type=Agents", nil) {
            (data) -> Void in
            if let data = data {
                let lu:Array<LinkedUser> = data.get(forName:"LinkedUsers")
                if lu.count > 0 {
                    self.result.response = 1
                    handler(self, lu[0])
                    return
                }
            }
            handler(self, nil)
        }
    }
    
    func serverInfo(_ code:String, _ handler: @escaping (_ sender:ConfigHelper, _ info:ServerInfo?) -> Void) {
        sendRequest("/api/server", code) {
            (data) -> Void in
            if let data = data {
                let lu:Array<ServerInfo> = data.get(forName:"ServerInfo")
                if lu.count > 0 {
                    self.result.response = 1
                    handler(self, lu[0])
                    return
                }
            }
            handler(self, nil)
        }
    }

    func sendRequest(_ url:String, _ code:String?, _ handler: @escaping ((_ data: AnswerPool?) -> Void)) -> Void {
//        let tst = """
//[{"data":[{"message":"","response":1}],"name":"ServerAnswer"},{"data":[{"address":"185.240.103.252","code":"5bf230afcf76fbeb","name":"Server","port":3000}],"name":"ServerInfo"}]
//""".data(using: .utf8)!
//        do {
//            let m = try JSONSerialization.jsonObject(with: tst)
//            print(m)
//        } catch {
//            print(error.localizedDescription)
//        }
//        return
        
        let urlStr = "\(ConfigHelper.HOST)\(url)"
        
        let url = URL(string: urlStr)
        var request = URLRequest(url: url!)
        request.httpMethod = "GET"
        request.setValue("application/json", forHTTPHeaderField:"Content-Type")
        if let code = code {
            request.setValue("Bearer \(code)", forHTTPHeaderField:"Authorization")
        }
        request.timeoutInterval = ConfigHelper.TIMEOUT
        
        URLSession.shared.dataTask(with: request) {
            (data: Data?, response: URLResponse?, error: Error?) -> Void in
            if let error = error {
                self.result.message = error.localizedDescription
            } else {
                if let data = data {
                    let ap = AnswerPool(data)
                    if ap.result.good {
                        handler(ap)
                        return
                        
                    } else {
                        if !ap.result.message.isEmpty {
                            self.result.message = ap.result.message
                        }
                    }
                }
            }
            if self.result.message.isEmpty {
                self.result.message = "no_data"
            }
            handler(nil)
        }
        .resume()
    }
}

@objc class ProgConfig : GRSObject {
    @objc var key = ""
    @objc var value = ""
    
    override class func getName() -> String! { "Config" }
    override class func getTableName() -> String! { "Config" }
    override class func getKeyFields() -> String! { "key" }
}

@objc class ConfigReceiver : GRSReceiver {
    init(serverConfig:Bool) {
        super.init(ProgConfig.self)

        if serverConfig {
            self.name  = "ServerConfig"
        } else {
            self.name  = "Config"
        }
    }
    
    override func reading(_ object: GRSObject!) {
        if let pc = object as? ProgConfig {
            Config.write(pc.key, pc.value)
        }
    }
}
