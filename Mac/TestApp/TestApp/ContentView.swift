//
//  ContentView.swift
//  TestApp
//
//  Created by Denis Mosyagin on 30.08.2021.
//

import SwiftUI
import NapoleonCore


class TestObj: GRSObject {
    override class func listTypes() -> [AnyHashable : Any]! {
        ["items" : TestObj.self]
    }
}

struct ContentView: View {
    @State var infoText = ""
    @State var color = Color.green
    var body: some View {
        VStack {
            VStack(alignment: .center) {
                Text("Hello, test!")
                    .padding()
                    .foregroundColor(.green)
                
                Button(action: doTest) {
                    Text("Click in")
                }
                Text(infoText)
                    .padding()
                    .foregroundColor(color)
            }
        }
        .padding()
    }
    
//    let test = GRSNetTest()
    func doTest() {
//        test.testing()
        
        let login : GRSLoginData = GRSLoginData()
        login.login = "2"
        login.password = "2"

        let adr1 : GRSNetworkData = GRSNetworkData()
        adr1.address = "192.168.0.161"//"212.232.41.126"
        adr1.port = 8282
        adr1.login = login

        let adr2 : GRSNetworkData = GRSNetworkData()
        adr2.address = "192.168.0.161"
        adr2.port = 8888
        adr2.login = login

        let connect = [adr1, adr2]

        if let adr = GRSNetworkRouting.probe(connect) {
            color = .green
            infoText = "Connected! to " + (adr.address as String) + ":" + String(adr.port)
        } else {
            color = .red
            infoText = GRSNetworkRouting.probeError()
        }
        
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            ContentView()
        }
    }
}
