//
//  Ace_Team_AppApp.swift
//  Ace Team App
//
//  Created by Denis Mosyagin on 08.03.2023.
//

import SwiftUI
import NapoleonCore

@main
class AceTeamApp: App {
    required init() {
        let fileManager = FileManager.default
        guard let folder = try? fileManager.url(for: .applicationSupportDirectory, in: .userDomainMask, appropriateFor: nil, create: true) else {
            print("Can't find cache folder")
            return
        }
        
        let fileName = "aceteam.db"
        GRSDBManager.initWithFileName(fileName, dir: folder)
    }
    
    var body: some Scene {
        WindowGroup {
            getView()
        }
    }
    
    func getView() -> some View {
        return ContentView()
    }
}
