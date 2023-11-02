//
//  Exchange.swift
//  AceTeamApp
//
//  Created by Denis Mosyagin on 11.03.2023.
//

import NapoleonCore
import Combine

class Exchange {
    static private var publisher = PassthroughSubject<(),Never>()
    static var closePublisher = Exchange.publisher.eraseToAnyPublisher()
    
    var result = ServerAnswer()
    
    func run(receivers:[GRSReceiver]?, senders:[GRSObjectSender]?, reports:[GRSReportHandler]?,
             progress:GRSNetworkEvents?, completion: @escaping (Bool, Exchange)->Void) {
        getAddress() {
            (serverInfo) in
            if let si = serverInfo {
                let adr = GRSNetworkData()
                adr.address = si.address
                adr.port = si.port
                adr.login.uuid = Config.uid
                
                self.work(address: adr, receivers: receivers, senders: senders, reports: reports, progress: progress) {
                    (done) in
                    completion(done, self)
                    if done {
                        Exchange.publisher.send()
                    }
                }
            } else {
                completion(false, self)
            }
        }
    }
    
    func work(address:GRSNetworkData, receivers:[GRSReceiver]?, senders:[GRSObjectSender]?, reports:[GRSReportHandler]?,
              progress:GRSNetworkEvents?, completion: @escaping (Bool)->Void) {
        let queue = OperationQueue()
        queue.addOperation{
            guard let network = GRSNetworkRouting(address) else {
                completion(false)
                return
            }
            
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
            if let rpts = reports {
                for ri in rpts {
                    ri.bind(to: network)
                }
            }
            
            if let prgs = progress {
                network.addHandler(prgs)
            }
            
            network.main()

            if network.isError {
                self.result.message = network.error
                completion(false)
            } else {
                completion(true)
            }
        }
    }
    
    func getAddress(handler:@escaping (ServerInfo?) -> Void) {
        let serverCode = Config.serverId
        if serverCode.isEmpty {
            result.message = "not connected to server"
            handler(nil)
            return
        }
        let ch = ConfigHelper()
        ch.serverInfo(serverCode) {
            (cfgHelper, serverInfo) in
            if let si = serverInfo {
                handler(si)
            } else {
                self.result.message = ch.result.message
                handler(nil)
            }
        }
    }
}
