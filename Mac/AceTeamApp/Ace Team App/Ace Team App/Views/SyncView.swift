//
//  Sync.swift
//  AceTeamApp
//
//  Created by Denis Mosyagin on 10.03.2023.
//

import SwiftUI
import NapoleonCore

class ProgressHandler : NSObject, GRSNetworkEvents {
    var count = 0.0;
    var handler: ((String?, Double) -> Void)?
    
    func starting(_ info: String!, count: UInt32) {
        self.count = Double(count)
        handler?(info, 0.0)
    }
    
    func progress(_ current: UInt32) {
        handler?(nil, Double(current)/self.count)
    }
}

struct SyncView: View {
    
    @SceneStorage("SyncView.rcvMainData") private var rcvMainData = true
    @SceneStorage("SyncView.sendDocs") private var sendDocs = true
    @SceneStorage("SyncView.sendVisits") private var sendVisits = false
    
    @State private var insyncing:Bool = false
    @State private var syncText:String = ""
    @State private var syncProgress = 0.0
    @State private var error:String = ""
    
    @State private var lastSync:LocalizedStringKey
    
    init() {
        lastSync = SyncView.updateLastSync()
    }
    
    static func updateLastSync() -> LocalizedStringKey {
        if let lsync = Config.lastSync {
            let df = DateFormatter()
            df.dateStyle = .medium
            df.timeStyle = .medium
            let dfs = df.string(from: lsync)
            return LocalizedStringKey("Last synced in \(dfs)")
        }
        return LocalizedStringKey("Not synced yet")
    }

    var body: some View {
        VStack {
            Spacer()
                .frame(height: 24)
            
            if insyncing {
                VStack {
                    ProgressView(value: syncProgress) {
                        Text(LocalizedStringKey(syncText))
                    }
                }.padding(.vertical, 16)
            }
            
            Text("Synchronization")
                .font(.system(size: 23, weight: .bold))
            
            Text(error)
                .frame(height: 64)
                .foregroundColor(ProgColor.error)
            Text(lastSync)
            GroupBox(label: Label("Received data", systemImage: "arrow.down.app")) {
                Toggle("Main data", isOn: $rcvMainData)
//                Toggle("Deliveries and Debet", isOn: $rcvDebet)
//                Spacer().frame(height: 50)
//                Toggle("Photos", isOn: $rcvPhoto)
            }
            GroupBox(label: Label("Sended data", systemImage: "arrow.up.square")) {
                Toggle("Send docs", isOn: $sendDocs)
                Toggle("Send photos", isOn: $sendVisits)
            }
            .padding(.vertical, 16)

            Button {
                syncing()
            } label: {
                HStack {
                    Text("Sync")
                        .font(.system(size: 20))
                    Spacer()
                    Image(systemName: "arrow.triangle.2.circlepath.circle")
                        .resizable()
                        .frame(width: 32, height: 32)
                }
                .padding(8)
                .frame(maxWidth: .infinity)
            }
            .buttonStyle(.borderedProminent)
            .padding(.vertical, 16)
            Spacer()
        }
        .padding()
    }
    
    func getReceivers() -> [GRSReceiver] {
        return [
//            OrgReciever(),

            ConfigReceiver(serverConfig: true),
            ConfigReceiver(serverConfig: false),

            OrgReciever(),
            GRSDBReceiver(clearBase: Matrix.self),
//            GRSDBReceiver(clearBase: Task.self),
            GRSDBReceiver(clearBase: ScriptDef.self),
            GRSDBReceiver(clearBase: Question.self),
            GRSDBReceiver(clearBase: Firm.self),
            GRSDBReceiver(clearBase: OrgLocation.self),
            GRSDBReceiver(clearBase: Folder.self),
            PriceReceiver(),
//            GRSDBReceiver(clearBase: Price.self),
            GRSDBReceiver(clearBase: Store.self),
            GRSDBReceiver(clearBase: PriceType.self),
            GRSDBReceiver(clearBase: PriceCost.self),
            GRSDBReceiver(clearBase: StoreQty.self),
        ]
    }
    
    func getReports() -> [GRSReportHandler] {
        return [
            ScheduleReceiver(),
        ]
    }
    
    func syncing() {
        self.insyncing = true
        
        let ph = ProgressHandler()
        ph.handler = {
            (messge, count) in
            DispatchQueue.main.async {
                syncProgress = count
                if let m = messge {
                    syncText = m
                }
            }
        }
        
        let receivers = rcvMainData ? getReceivers() : nil
        let reports = rcvMainData ? getReports() : nil
        
        let exch = Exchange()
        exch.run(receivers: receivers, senders: nil, reports: reports, progress: ph ) {
            (result, sender) in
            if result {
                Config.lastSync = Date()
            }
            
            DispatchQueue.main.async {
                self.insyncing = false
                if !result {
                    error = sender.result.message
                } else {
                    self.lastSync = SyncView.updateLastSync()
                }
            }
        }
    }
}

struct SyncView_Previews: PreviewProvider {
    static var previews: some View {
        SyncView()
    }
}
