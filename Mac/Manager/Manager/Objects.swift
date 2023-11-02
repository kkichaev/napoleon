//
//  Objects.swift
//  Manager
//
//  Created by Denis Mosyagin on 23.09.2021.
//

import Foundation
import NapoleonCore

struct GRSConfig {
    var networkData = GRSNetworkData()
 
    init(_ reading:Bool) {
        if(reading) {
            read()
        }
    }
    
    func read() {
        let ud = UserDefaults.init()
        networkData.address = ud.string(forKey:"server_address")
        networkData.port = Int32(ud.integer(forKey:"server_port"))
        networkData.login.login = ud.string(forKey:"login")
        networkData.login.password = ud.string(forKey: "password")
        networkData.login.duration = Int32(ud.integer(forKey: "duration"))
    }
    
    var isEmpty : Bool {
        networkData.address.isEmpty || networkData.login.login.isEmpty
    }
}

class ProgressHandler : NSObject, GRSNetworkEvents {
    var progress : ProgressData
    
    class ProgressData: ObservableObject {
        @Published var text = "Connecting"
        @Published var total = 100.0
        @Published var current = 0.0
    }
    
    init(progress: ProgressData) {
        self.progress = progress
    }
    
    func starting(_ info: String!, count: UInt32) {
        DispatchQueue.main.async {
            if !info.isEmpty {
                self.progress.text = info
            }
            if count != 0 {
                self.progress.total = Double(count)
            }
        }
    }
    
    func progress(_ current: UInt32) {
        DispatchQueue.main.async {
            self.progress.current = Double(current)
        }
    }
}

class GRSNetwork {
    var error = ""
    var serverAnswer : GRSServerAnswer?
    
    func updateDuration() {
        if self.serverAnswer != nil {
            let scanner = Scanner.init(string: self.serverAnswer!.message)
            var val = UInt64(0)
            scanner.scanHexInt64(&val)
            let ud = UserDefaults.init()
            ud.set(Int(truncatingIfNeeded: val), forKey: "duration")
        }
    }
    
    func run(_ address: [GRSNetworkData], _ receivers:[GRSReceiver]?, _ senders: [GRSObjectSender]?,
             _ progress: GRSNetworkEvents?, completion: @escaping (Bool, GRSNetwork)->Void) {
        
        let queue = OperationQueue()
        queue.addOperation{
            progress?.starting?("Connecting", count: 0)
            guard let network = GRSNetworkRouting.probe(address) else {
                self.error = GRSNetworkRouting.probeError()
                completion(false, self)
                return
            }
            self.serverAnswer = GRSNetworkRouting.probeAnswer()
            self.updateDuration()

            if let rcvrs = receivers {
                for rcv in rcvrs {
                    rcv.bind(to: network)
                }
            }
            if let sndrs = senders {
                for snd in sndrs {
                    snd.bind(to: network)
                }
            }
            if progress != nil {
                network.addHandler(progress)
            }
            network.main()
            completion(true, self)
        }
    }
    
    func runReport(_ address: [GRSNetworkData], _ name:String, _ param:GRSObject, _ result:[GRSReceiverBase],
                   _ progress: GRSNetworkEvents?, completion: @escaping (Bool, GRSNetwork)->Void) {
        let queue = OperationQueue()
        queue.addOperation{
            progress?.starting?("Connecting", count: 0)
            guard let network = GRSNetworkRouting.probe(address) else {
                self.error = GRSNetworkRouting.probeError()
                completion(false, self)
                return
            }
            self.serverAnswer = GRSNetworkRouting.probeAnswer()
            self.updateDuration()

            let rpt = GRSReportHandler(name, withParam: param, result: result)
            rpt?.bind(to: network)
            if progress != nil {
                network.addHandler(progress)
            }
            network.main()
            completion(true, self)
        }
    }
}

@objc class AgentSummaryData : GRSObject {
    @objc var id = ""
    @objc var start_date = Date()
    @objc var end_date = Date()
    @objc var visits = 0
    @objc var orders = 0
    @objc var sum = 0.0
    @objc var progress = 0
    @objc var order_progress = 0
    
    @objc var dist = 0.0

    @objc var orgsz = 0
    @objc var plan = 0
    @objc var visited = 0
    @objc var plannedVisits = 0
    
    override class func getName() -> String! { "TypeName" }
    override class func getTableName() -> String! { "ReportData" }
    override class func getKeyFields() -> String! { "id,start_date" }
}

@objc class ManagerAgent : Agent {
    @objc var phone = ""
    @objc var date = Date()
    override class func getName() -> String! { "ManagerAgent" }
}

@objc class SummaryParam : GRSObject {
    @objc var start_date = Date()
    @objc var end_date = Date()
}
