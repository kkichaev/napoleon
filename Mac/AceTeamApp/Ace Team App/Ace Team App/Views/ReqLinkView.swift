//
//  ReqLinkView.swift
//  AceTeamApp
//
//  Created by Denis Mosyagin on 10.03.2023.
//

import SwiftUI

struct ReqLinkView: View {
    
    var onCompleete: () -> ()
    var onDissmiss: () -> ()
    
    @State private var code: String = ""
    @State private var errMessage = ""
    @State private var visible = true
    
    @FocusState private var focused:Bool

    var body: some View {
        VStack {
            if !errMessage.isEmpty {
                Text(errMessage)
            }
            TextField(
                "Input request code",
                text: $code
            )
            .multilineTextAlignment(.center)
            .focused($focused)
            Button("Connect") {
                linkingUser()
            }
        }
        .onAppear {
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.25) {
                focused = true
            }
        }
    }
    
    func linkingUser() {
        if code.isEmpty {
            return
        }
        
        let cf = ConfigHelper()
        cf.requestLink(code) {
            sender,user in
            if let user = user {
                Config.uid = user.code
                Config.serverId = user.server_code
                Config.userid = user.id
                
                onCompleete()
            } else {
                errMessage = sender.result.message
            }
        }
    }
}

struct ReqLinkView_Previews: PreviewProvider {
    static var previews: some View {
        ReqLinkView(onCompleete:{}, onDissmiss: {})
    }
}
