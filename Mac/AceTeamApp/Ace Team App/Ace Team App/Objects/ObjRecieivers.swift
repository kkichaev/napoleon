//
//  ObjRecieivers.swift
//  AceTeamApp
//
//  Created by Denis Mosyagin on 12.03.2023.
//

import NapoleonCore


@objc class OrgReciever : GRSDBReceiver {
    override init() {
        super.init(Org.self)
    }
    
    override func starting() {
        super.starting()
        
        let db = GRSDBManager.get()
        let upd = "update " + Org.getTableName() + " set hidden = 1"
        sqlite3_exec(db, upd, nil, nil, nil)
    }
    
//    override func reading(_ object: GRSObject!) {
//        if let org = object as? Org {
//            let t = org.name.lowercased() + org.address.lowercased()
//            print(t + " " + org.id)
//            print(org.contacts.count)
//        }
//        super.reading(object)
//    }
}

@objc class PotenzialOrgReceiver : OrgReciever {
    override init() {
        super.init()
        name = "PotenzialOrg"
    }
    
    override func reading(_ object: GRSObject!) {
        if let org = object as? Org {
            org.flags |= Org.FL_USER_CREATED
        }
        super.reading(object)
    }
}

@objc class PriceReceiver : GRSDBReceiver {
    override init() {
        super.init(Price.self)
    }
    
    override func starting() {
        super.starting()
                
        let db = GRSDBManager.get()
        let upd = "update " + Price.getTableName() + " set hidden = 1"
        sqlite3_exec(db, upd, nil, nil, nil)
    }
    
//    override func reading(_ object: GRSObject!) {
//        if let el = object as? Price {
//            el.srchName = el.name.lowercased()
//        }
//        super.reading(object)
//    }
}

@objc class PythonExecReceiver : GRSReceiver {
    static var Error = PythonExecError()
    
    override init() {
        super.init(PythonExecError.self)
    }
    
    override func reading(_ object: GRSObject!) {
        if let err = object as? PythonExecError {
            PythonExecReceiver.Error = err
            print(err.stackTrace)
        }
    }
}

@objc class ScheduleReceiver : GRSReportHandler {
    @objc class Param : GRSObject {
        @objc var start:String
        @objc var finish:String
        @objc var userid:String
        
        override init() {
            let df = DateFormatter()
            df.dateFormat = "yyyyMMdd"
            
            var add = DateComponents()
            
            add.day = -7
            self.start = df.string(from: Calendar.current.date(byAdding: add, to: Date())!)
            
            add.day = 14
            self.finish = df.string(from: Calendar.current.date(byAdding: add, to: Date())!)
            self.userid = Config.userid
        }
    }
    override init() {
        super.init("make_schedule", withParam: Param()
                   , result: [
                        GRSDBReceiver.init(clearBase: Schedule.self),
                        PythonExecReceiver(),
                   ])
    }
}
