//
//  ContentView.swift
//  Manager
//
//  Created by Denis Mosyagin on 23.09.2021.
//

import SwiftUI

import NapoleonCore

struct ContentView: View  {
    @Environment(\.programConfig) var config : GRSConfig
    
    @State var inSync = false
    @StateObject var progress = ProgressHandler.ProgressData()
    @State var showAlert = false
    @State var errorMessage = ""
    
    @StateObject var data = SummaryData()

    func loadData() {
        data.load(Date())
    }
    
    var body: some View {
        NavigationView {
            let v = VStack {
                if inSync {
                    ProgressView(progress.text, value: progress.current, total: progress.total ).padding()
                }
                List {
                    ForEach(data.divisions) { division in
                        DivisionRowView(division: division)
                    }
                }
            }
            .navigationTitle("Подразделения")
            .toolbar {
                ToolbarItemGroup(placement: .navigationBarTrailing) {
                    Button("Refresh") { refresh() }
                    Button("Settings") { openSettings() }
                }
            }

            if #available(iOS 15.0, *) {
                v.alert(errorMessage, isPresented: $showAlert) {
                    Button("OK", role: .cancel) {}
                }
            } else {
                v.alert(isPresented: $showAlert) {
                    Alert(title:Text("Error"), message: Text(errorMessage))
                }
            }

            // Placeholder
            Text("Выберите агента")
                .font(.headline)
        }.onAppear(perform: { loadData() })
    }
    
    func openSettings() {
        UIApplication.shared.open(URL(string: UIApplication.openSettingsURLString)!)
    }
    
    func refresh() {
        if config.isEmpty {
            openSettings()
            return
        }
        
        let result : [GRSReceiverBase] =
        [GRSDBReceiver(ManagerAgent.self),
         GRSDBReceiver(clearBase:Division.self),
         GRSDBReceiver(AgentSummaryData.self, withFilter: "", clearBase: true)]

        
        inSync = true

        let net = GRSNetwork()
        let addrs = [config.networkData]
        
        // summary
//        repResult.add(new ManagerAgentHitching());
//        repResult.add(new RcvNewHitching(Division.class, "Division"));
//        repResult.add(new Hitching(AgentReportData.class, "TypeName"));
//
//        Date start = Util.resetTime(getDate());
//        Calendar c = Calendar.getInstance();
//        c.setTime(start);
//        c.add(Calendar.DATE, 1);
//        Date finish = c.getTime();
////        c.add(Calendar.MONTH, -1);
////        c.add(Calendar.DATE, -1);
////        start = c.getTime();
//        list.add(new ReportHitching(getSummaryReportName(), new ReportOnAgentForDatesParams(start, finish), repResult));


//        net.run(addrs, [agents, dvsn], nil, ProgressHandler(progress:progress)) { result, network in
//            DispatchQueue.main.async {
//                inSync = false
//                if !result {
//                    showAlert = true
//                    errorMessage = network.error
//                }
//            }
//        }
        
        let cd = Date()
        let param = SummaryParam()
        param.start_date = Calendar.current.date(byAdding: DateComponents(day:-2), to: cd)!
        param.end_date = cd.endOfDay
        
        net.runReport(addrs, "summary", param, result, ProgressHandler(progress:progress)) { result, network in
            DispatchQueue.main.async {
                inSync = false
                if !result {
                    showAlert = true
                    errorMessage = network.error
                } else {
                    loadData()
                }
            }
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
//        AgentRowView(agent: Person(name: "Гусев Сергей", phoneNumber: "(408) 555-4301"), progress: 60)
    }
}

extension Date {

    var startOfDay : Date {
        let calendar = Calendar.current
        let unitFlags = Set<Calendar.Component>([.year, .month, .day])
        let components = calendar.dateComponents(unitFlags, from: self)
        return calendar.date(from: components)!
   }

    var endOfDay : Date {
        var components = DateComponents()
        components.day = 1
        let date = Calendar.current.date(byAdding: components, to: self.startOfDay)
        return (date?.addingTimeInterval(-1))!
    }
}

class SummaryData: ObservableObject {
    @Published var divisions = [DivisionData]()
    
    func load(_ date: Date) {
        let reader = GRSDBReader();
        
        let dv = reader?.fetchDic(DivisionData.self, key:"id") as? [Int: DivisionData]
        let ag = reader?.fetchDic(AgentData.self, key: "id") as? [String:AgentData]
        
        let sd = Int(date.startOfDay.timeIntervalSince1970) - 24 * 3600
        let ed = sd + 24 * 3600 - 1
        let filter = "start_date >= \(sd) and start_date <= \(ed)"

        let pdq = reader?.fetchDic(AgentSummaryData.self, key:"id", where:filter) as? [String:AgentSummaryData]

        for pcheck in pdq!.values {
            if pcheck.progress != 0 {
                print("Test")
            }
        }
        if let pd1 = reader?.fetch(AgentSummaryData.self, where:filter) as? [AgentSummaryData] {
            for p in pd1 {
                if let a = ag![p.id] {
                    a.summary.progress += p.progress
                }
            }
        }

        var rootDivisions = [DivisionData]()
        if dv != nil {
            for division in dv!.values {
                division.readAgents(ag)
                if division.parent <= 0 {
                    rootDivisions.append(division)
                } else {
                    if let parent = dv![Int(division.parent)] {
                        parent.divisions.append(division)
                    }
                }
            }
//            if progressData != nil {
//                for pd in progressData! {
//                    dv![Int(pd.division_id)]?.updateProgress(pd)
//                }
//            }
        }
        divisions = rootDivisions
    }
}

class AgentData : ManagerAgent, Identifiable {
    var summary = AgentSummaryData()
}

class DivisionData: Division, Identifiable {
    var divionAgents = [String:AgentData] ()
    var divisions = [DivisionData]()

    var progress: Float {
        get {
            var cd = ProgressData(count:0)
            agentProgress(&cd)
            return cd.count > 0 ? cd.progress / Float(cd.count) : 0;
        }
    }
    
    func updateProgress(_ data : AgentSummaryData) {
        if let a = divionAgents[data.id] {
            a.summary.progress += data.progress
        }
    }
    
    func readAgents(_ agentDic : [String:AgentData]?) {
        for da in agents {
            if let divAgent = da as? DivisionAgent {
                if let a = agentDic?[divAgent.id] {
                    divionAgents[a.id] = a
                }
            }
        }
    }
    
    struct ProgressData {
        var progress = Float(0)
        var count : Int
    }
    
    func agentProgress(_ data: inout ProgressData) {
        data.count += divionAgents.count
        for agent in divionAgents.values {
            data.progress += Float(agent.summary.progress)
        }
        
        for division in divisions {
            division.agentProgress(&data)
        }
    }
}

struct DivisionRowView: View {
    var division: DivisionData
    
    var body : some View {
        Section(header: header()) {
            ForEach(division.divisions) { division in
                DivisionRowView(division: division)
            }
            ForEach(division.divionAgents.values.sorted{ $0.name < $1.name }) { agent in
                NavigationLink(destination: AgentDetailView(agent: agent)) {
                    AgentRowView(agent: agent)
                }
            }
        }
    }
    
    func header() -> some View {
        HStack {
            Text(division.name)
                .frame(maxWidth:.infinity, alignment: .leading)
            WorkProgressView(progress: division.progress, circleShape: false)
        }
    }
}

struct AgentRowView: View {
    var agent: AgentData

    var body: some View {
        HStack() {
            VStack(alignment: .leading, spacing: 4) {
                Text(agent.name)
                    .foregroundColor(.primary)
                    .font(.headline)
                    .frame(maxWidth:.infinity, alignment:.leading)
                Label(agent.phone, systemImage: "phone")
                    .foregroundColor(.secondary)
                    .font(.subheadline)
            }
            WorkProgressView(progress: Float(agent.summary.progress))
        }
    }
}

struct AgentDetailView: View {
    var agent: AgentData

    var body: some View {
        VStack {
            Text(agent.name)
                .foregroundColor(.primary)
                .font(.title)
                .padding()
            HStack {
                Label(agent.phone, systemImage: "phone")
            }
            .foregroundColor(.secondary)
        }
    }
}
