//
//  MoreView.swift
//  AceTeamApp
//
//  Created by Denis Mosyagin on 13.03.2023.
//

import SwiftUI

struct MoreView: View {
    var body: some View {
        NavigationView{
            VStack {
                NavigationLink(destination: SyncView()) {
                    Text("Sync")
                }
                NavigationLink(destination: ReqLinkView(onCompleete: {}, onDissmiss: {})) {
                    Text("Code")
                }
            }
        }
//        Text("More")
    }
}

struct MoreView_Previews: PreviewProvider {
    static var previews: some View {
        MoreView()
    }
}
