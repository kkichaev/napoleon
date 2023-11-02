//
//  OrgView.swift
//  AceTeamApp
//
//  Created by Denis Mosyagin on 13.03.2023.
//

import SwiftUI
import NapoleonCore

import Introspect

class OrgsData : ObservableObject {
    class RowData : Identifiable, Comparable {
        static func == (lhs: OrgsData.RowData, rhs: OrgsData.RowData) -> Bool {
            lhs.org.name == rhs.org.name
        }
        
        static func < (lhs: OrgsData.RowData, rhs: OrgsData.RowData) -> Bool {
            lhs.org < rhs.org
        }
        
        let org: Org
        
        init(org: Org) {
            self.org = org
        }
        
        func contains(_ text:String) -> Bool {
            org.name.lowercased().contains(text) || org.address.lowercased().contains(text)
        }
        
        var id: ObjectIdentifier { ObjectIdentifier(self.org) }
    }

    
    @Published var orgs = [RowData]()
    
    var allOrgs = [String:RowData]()
    var routeOrgs = [RowData]()
    var routeMode = false

    init() {
        let reader = GRSDBReader()
        let filter = Config.hideOldOrgs ? "hidden=0" : ""
        for o in reader?.fetch(Org.self, where: filter) as! [Org] {
            let od = RowData(org:o)
            allOrgs[o.id] = od
        }
    }
        
    func setRoute(_ route:Bool, forDate:Date) {
        routeMode = route

        routeOrgs.removeAll()
        orgs.removeAll()
        if route {
            let tz = TimeZone.current.secondsFromGMT()
            let val = Int(forDate.timeIntervalSince1970) / (24 * 3600) * 24 * 3600
            let filter = "date = \(val - tz)"
            let reader = GRSDBReader()
            for sci in reader?.fetch(Schedule.self, where:filter) as! [Schedule] {
                for ri in sci.items {
                    if let o = allOrgs[ri.id] {
                        orgs.append(o)
                        routeOrgs.append(o)
                    }
                }
            }
        } else {
            for o in allOrgs.values {
                orgs.append(o)
            }
        }
        orgs.sort()
    }
    
    func search(_ text:String) {
        let src = routeMode ? routeOrgs : Array(allOrgs.values)
        orgs.removeAll()
        let text = text.lowercased()
        for o in src {
            if text.isEmpty || o.contains(text) {
                orgs.append(o)
            }
        }
        orgs.sort()
    }
}

struct OrgsView: View {
    @EnvironmentObject var model : HomeModel
    
    @StateObject var data = OrgsData()
    
    var onInit : ((OrgsView) -> Void)?
    
    var body: some View {
        List(data.orgs) { item in
            NavigationLink {
                OrgDocumentsView(org:item.org)
            } label: {
                OrgRowView(data:item)
            }
            .listRowInsets(EdgeInsets())
        }
        .listStyle(.plain)
        .onChange(of: model.mode) {newValue in
            data.setRoute(newValue == .route, forDate: model.scheduleDate)
        }
        .onChange(of: model.scheduleDate) { newValue in
            if model.mode == .route {
                data.setRoute(true, forDate: model.scheduleDate)
            }
        }
        .onAppear{
            data.setRoute(model.mode == .route, forDate: model.scheduleDate)
            model.searching = data.search
            onInit?(self)                        
        }
    }
}

struct OrgRowView : View {
    let data: OrgsData.RowData
    
    init(data: OrgsData.RowData) {
        self.data = data
    }
    
    var body: some View {
        VStack {
            Text(data.org.name)
                .font(.system(size: 16, weight: .semibold))
                .frame(maxWidth: .infinity, alignment: .leading)
            Spacer().frame(height: 4)
            Text(data.org.address)
                .font(.system(size: 14, weight: .light))
                .frame(maxWidth: .infinity, alignment: .leading)
        }.padding(.vertical, 8)
    }
}

//struct OrgsView_Previews: PreviewProvider {
//    static var orgs = makeOrgs()
//
//    static func makeOrgs() -> [Org] {
//        var ret = [Org]()
//        let o = Org()
//        o.name = "Test"
//        o.address = "Address"
//
//        ret.append(o)
//
//        ret.append(o)
//        return ret
//    }
//
//    static var previews: some View {
//        OrgsView(onInit:{sender in
//            sender.data.setOrgs(orgs: orgs)
//        }).environmentObject(HomeModel())
//    }
//}
