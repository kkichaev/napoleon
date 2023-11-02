//
//  ContentView.swift
//  Ace Team App
//
//  Created by Denis Mosyagin on 08.03.2023.
//

import SwiftUI

struct ContentView: View {
    @State var code: String = ""
    @State var uid: String
    @State var errMessage = ""
    
    init() {
        uid = Config.uid
        LocationService.shared.prepare()
    }

    init(testUid:String) {
        uid = testUid
    }

    var body: some View {
        VStack {
            if uid.isEmpty {
                ReqLinkView(
                    onCompleete: {
                        uid = Config.uid
                    },
                    onDissmiss: {
                    }
                )
            } else {
                TabView {
                    HomeView()
                        .tabItem {
                            Image(systemName: "house")
                            Text("Home")
                        }
                    StockView()
                        .tabItem{
                            Image(systemName: "doc.text")
                            Text("Stock")
                        }
                    DocumentsView()
                        .tabItem{
                            Image(systemName: "archivebox")
                            Text("Docs")
                        }
                    MoreView()
                        .tabItem{
                            Image(systemName: "line.3.horizontal")
                            Text("More")
                        }
                }
//                SyncView()
            }
        }
        .padding()
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView(testUid: "test")
            .environment(\.locale, .init(identifier: "ru"))
    }
}
