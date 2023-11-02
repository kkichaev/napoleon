//
//  Objects.swift
//  AceTeamApp
//
//  Created by Denis Mosyagin on 08.03.2023.
//

import Foundation
import NapoleonCore
import CoreLocation

@objc class SelectableObject : GRSObject, Identifiable {
    @objc var id = ""
    @objc var name = ""
    
    override class func getKeyFields() -> String! { "id" }
    
    static func readValues<T:SelectableObject>(order:String?) -> [T] {
        var res = [T]()
        
        let rdr = GRSDBReader()!
        for el in rdr.fetch(T.self, where: "", order: order ?? "") {
            if let src = el as? T {
                res.append(src)
            }
        }
        
        return res
    }
}

extension Array where Element: SelectableObject {
    func valueOf(id:String) -> Element? {
        return first(where: {$0.id == id})
    }
}


@objc class PythonExecError : GRSObject {
    @objc var type = ""
    @objc var text = ""
    @objc var stackTrace = ""

    override class func getName() -> String! { "PythonExecError" }
}

@objc class Org : GRSObject, Comparable {
    
    static func < (lhs: Org, rhs: Org) -> Bool {
        let cmp = lhs.name.compare(rhs.name, options: .caseInsensitive)
        if cmp == .orderedDescending {
            return false
        }
        if cmp == .orderedAscending {
            return true
        }
        return lhs.address < rhs.address
    }
    
    @objcMembers class Contact : GRSObject {
        var name = ""
        var phone = ""
    }

    static let FL_STOP_BLOCK = 1
    static let FL_STOP_LIST = 2
    static let FL_EXPORTED = 16
    static let FL_USER_CREATED = 32

    @objc var contacts = [Contact]()
//    @objc var contacts : NSMutableArray = []

    @objc var id = ""
    @objc var name = ""
    @objc var address = ""
//    @objc var srchName = ""
    
    @objc var color = 0
    
    @objc var longitude = 0.0
    @objc var latitude = 0.0
    
    
    @objc var type = 0
    @objc var flags = 0
    @objc var prcType = ""
    @objc var hidden = 0
    
    var readLocatiion = false
    var __location : CLLocation?
    
    var location : CLLocation? {
        if readLocatiion {
            return __location
        }
        
        readLocatiion = true
        if latitude != 0.0 || longitude != 0.0 {
            __location = CLLocation(latitude:latitude, longitude:longitude)
        } else {
            let ol = OrgLocation()
            ol.id = id
            let rd = GRSDBReader()
            if rd?.read(byKey: ol) ?? false {
                __location = CLLocation(latitude: ol.latitude, longitude: ol.longitude)
            }
        }
        return __location
    }
    
    override class func getName() -> String! { "Org" }
    override class func getTableName() -> String! { "Org" }
    override class func getKeyFields() -> String! { "id" }
    override class func listTypes() -> [AnyHashable : Any]! { return ["contacts":Contact.self] }
}

@objc class Schedule : GRSObject {
    @objc class Item : GRSObject {
        @objc var timeIndex = 0
        @objc var id = ""
    }

    @objc var date = Date()
    @objc var items = [Item]()
    
    override class func getName() -> String! { "Schedule" }
    override class func getTableName() -> String! { "Schedule" }
    override class func getKeyFields() -> String! { "date" }
    
    override class func listTypes() -> [AnyHashable : Any]! {
        return [
            "items": Item.self,
        ]
    }
}

@objc class Matrix : GRSObject {
    @objc class Item : GRSObject {
        @objc var id = ""
        @objc var order = 0
    }

    @objc var name = ""
    @objc var items = [Item]()

    override class func getName() -> String! { "Matrix" }
    override class func getTableName() -> String! { "Matrix" }
    override class func getKeyFields() -> String! { "name" }
    
    override class func listTypes() -> [AnyHashable : Any]! {
        return [
            "items": Item.self,
        ]
    }
}

@objc class Firm : SelectableObject {
    
    override class func getName() -> String! { "Firm" }
    override class func getTableName() -> String! { "Firm" }
}

@objc class OrgLocation : GRSObject {
    @objc var id = ""
    @objc var longitude = 0.0
    @objc var latitude = 0.0

    override class func getName() -> String! { "OrgLocation" }
    override class func getTableName() -> String! { "OrgLocation" }
    override class func getKeyFields() -> String! { "id" }
}

@objcMembers class GPSPos : GRSObject {
    var date = Date()
    var latitude = 0.0
    var longitude = 0.0
    var speed = 0.0
    
    var params = 0
    var accuracy = 0
    
    var stltime = Date()
    var isMock = 0
    
    override class func getName() -> String! { "GPSPos" }
    override class func getTableName() -> String! { "Tracking" }
    override class func getKeyFields() -> String! { "date" }
    
    static func from(src: CLLocation) -> GPSPos {
        let dest = GPSPos()
        
        dest.latitude = src.coordinate.latitude
        dest.longitude = src.coordinate.longitude
        dest.speed = src.speed
        dest.accuracy = Int(src.horizontalAccuracy + 0.05)
        dest.stltime = src.timestamp

        return dest
    }
}


//@objc class Task : GRSObject {
//    @objc var id = ""
//    public String orgid = "";
//    public Date start;
//    public Date finish;
//    public String text = "";
//    public Date created;
//    public String manager = "";
//
//    override class func getName() -> String! { "OrgTask" }
//    override class func getTableName() -> String! { "OrgTask" }
//    override class func getKeyFields() -> String! { "id" }
//}

@objc class ScriptDef : GRSObject {
    @objc class Item : GRSObject {
        @objc var curType = ""
        @objc var nextDoc = 0
        @objc var condition = 0
        @objc var condParam = ""
        @objc var name = ""
        @objc var pos = 0

        @objc var id = ""

        var canSkip: Bool {
            get { return (condition == 0) }
        }
    }

    @objc var id = 0
    @objc var items = [Item]()

    override class func getName() -> String! { "ScriptDef" }
    override class func getTableName() -> String! { "ScriptDef" }
    override class func getKeyFields() -> String! { "id" }
    
    override class func listTypes() -> [AnyHashable : Any]! {
        return [
            "items": Item.self,
        ]
    }
}

@objc class Question : GRSObject {
    @objc class Item : GRSObject {
        static let TEXT = 0
        static let NUMBER = 1
        static let LIST = 2
        static let SET = 3
        static let BOOLEAN = 4
        static let DATASET = 5
        static let SPINNER = 6
        static let IMAGE = 7
        static let NUMBER_LIST = 8
        
        @objc class Value : GRSObject {
            @objc var value = ""
        }

        @objc var id = ""
        @objc var type = 0
        @objc var values = [Value]()
        
        @objc var iditem = ""
        @objc var text = ""
        @objc var optional = 0
        @objc var number = 0

        override class func listTypes() -> [AnyHashable : Any]! {
            return [
                "values": Value.self,
            ]
        }
    }

    @objc class Attach : GRSObject {
        @objc var name = ""
        @objc var id = ""
    }

    @objc var idquest = ""
    @objc var name = ""
    @objc var from = Date()
    @objc var till = Date()
    @objc var text = ""
    @objc var html = ""
    @objc var params = 0
    @objc var number = 0
    
    @objc var items = [Item]()
    @objc var attach = [Attach]()

    override class func getName() -> String! { "Question" }
    override class func getTableName() -> String! { "Question" }
    override class func getKeyFields() -> String! { "idquest" }
    
    override class func listTypes() -> [AnyHashable : Any]! {
        return [
            "items": Item.self,
            "attach": Attach.self,
        ]
    }
}

class ServerAnswer : Codable {
    var message = ""
    var response: Int = 0
    
    var good: Bool {
        get { return self.response != 0 }
    }
}

struct ServerInfo : Codable {
    var address : String = ""
    var port: Int32 = 0
}

struct LinkedUser : Codable {
    var code: String = ""
    var server_code: String = ""
    var id: String = ""
}

extension Decodable {
    init<Value:Encodable>(_ dict: Value) throws {
        let data = try JSONEncoder().encode(dict)
//        let sd = String(bytes: data, encoding: .utf8)!
//        print(sd)
        self = try JSONDecoder().decode(Self.self, from: data)
    }
}

class DicWrapper : Encodable {
    var src:Dictionary<String, Any>
    
    static var curContainer :Any? = nil

    struct Key : CodingKey {
        var stringValue: String
        
        init(stringValue: String) {
            self.stringValue = stringValue
        }
        
        var intValue: Int? {
            get { return stringValue.hashValue }
            set {
                if let ival = newValue {
                    self.stringValue = String(ival)
                } else {
                    self.stringValue = ""
                }
            }
        }
        
        init(intValue: Int) {
            self.stringValue = String(intValue)
        }
    }
    
    init(src: Dictionary<String, Any>) {
        self.src = src
    }
    
    static func nested(_ key: Key?) ->KeyedEncodingContainer<Key>? {
        if var kval = DicWrapper.curContainer as? KeyedEncodingContainer<Key> {
            return kval.nestedContainer(keyedBy: DicWrapper.Key.self, forKey: key!)
        }
        if var kval = DicWrapper.curContainer as? UnkeyedEncodingContainer {
            return kval.nestedContainer(keyedBy: DicWrapper.Key.self)
        }
        return nil
    }
    
    static func nestedUnkeyd(_ key: Key?) ->UnkeyedEncodingContainer? {
        if var kval = DicWrapper.curContainer as? KeyedEncodingContainer<Key> {
            return kval.nestedUnkeyedContainer(forKey: key!)
        }
        if var kval = DicWrapper.curContainer as? UnkeyedEncodingContainer {
            return kval.nestedUnkeyedContainer()
        }
        return nil
    }
    
    fileprivate static func encodeDic(_ key: DicWrapper.Key?, _ kval: [String : Any]) throws {
        if let nested = DicWrapper.nested(key) {
            DicWrapper.curContainer = nested
            let dw = DicWrapper(src: kval)
            try dw.encodeInt()
        }
    }
    
    fileprivate static func encodeArray(_ key: DicWrapper.Key?, _ kval: [Any]) throws {
        if let nested = DicWrapper.nestedUnkeyd(key) {
            DicWrapper.curContainer = nested
            
            for ael in kval {
                if let aval = ael as? Dictionary<String, Any> {
                    try DicWrapper.putValue(nil, aval)
                }
            }
        }
    }
    
    static func putValue(_ key: Key?,  _ value:Any) throws {
        let ctr = DicWrapper.curContainer
        
        if let kval = value as? Dictionary<String, Any> {
            try encodeDic(key, kval)
            DicWrapper.curContainer = ctr
            return
        }
        
        if let kval = value as? Array<Any> {
            try encodeArray(key, kval)
            DicWrapper.curContainer = ctr
            return
        }
        
        var container = ctr as! KeyedEncodingContainer<Key>
        
        switch value {
        case let val as Int:
            try container.encode(val, forKey: key!)
        case let val as String:
            try container.encode(val, forKey: key!)
        case let val as Double:
            try container.encode(val, forKey: key!)
        default:
            break
        }
    }
    
    func encodeInt() throws {
        for (key, v) in src {
            let kv = Key(stringValue: key)
            try DicWrapper.putValue(kv, v)
        }
    }
    
    func encode(to encoder: Encoder) throws {
        let ct = DicWrapper.curContainer
        if ct == nil {
            DicWrapper.curContainer = encoder.container(keyedBy: Key.self)
        }

        try encodeInt()
        
        DicWrapper.curContainer = ct
    }
}

class AnswerPool {
    var data : Array<Any> = Array()
    
    init(_ data: Data) {
        do {
            self.data = try JSONSerialization.jsonObject(with: data) as! Array<Any>
        } catch {
            print(error.localizedDescription)
        }
    }
    
    func get<T:Decodable>(forName name:String) -> Array<T> {
        var res: Array<T> = Array()
        
        for el in data {
            if let elmap = el as? Dictionary<String,Any> {
                if let elname = elmap["name"] as? String {
                    if elname == name {
                        if let eldata = elmap["data"] as? Array<Any> {
                            for del in eldata {
                                do {
                                    if let ddic = del as? Dictionary<String, Any> {
                                        let obj:T = try T(DicWrapper(src:ddic))
                                        res.append(obj)
                                    }
                                } catch {
                                    let err = "Error in decode \(error)"
                                    print(err)
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return res
    }
    
    var _tres : ServerAnswer? = nil
    var result : ServerAnswer {
        get {
            if _tres == nil {
                let res : Array<ServerAnswer> = get(forName: "ServerAnswer")
                _tres = res.count > 0 ? res[0] : ServerAnswer()
            }
            return _tres!
        }
    }
}
