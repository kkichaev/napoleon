//
//  ManagerApp.swift
//  Manager
//
//  Created by Denis Mosyagin on 23.09.2021.
//

import SwiftUI
import NapoleonCore

extension EnvironmentValues {
    private struct ConfigEnvKey : EnvironmentKey {
        static var defaultValue: GRSConfig = GRSConfig(false)
    }

    var programConfig: GRSConfig {
        get { self[ConfigEnvKey.self] }
        set { self[ConfigEnvKey.self] = newValue }
    }
}

@main
class ManagerApp: App {
    var config = GRSConfig(true)
    
    required init() {
        NotificationCenter.default.addObserver(self, selector: #selector(defaultsChanged),
                                               name: UserDefaults.didChangeNotification, object: nil)
        let _ = initApp()
    }
    
    func initApp() -> Bool {
        let fileManager = FileManager.default
        guard let folder = try? fileManager.url(for: .cachesDirectory, in: .userDomainMask, appropriateFor: nil, create: true) else {
            print("Can't find cache folder")
            return false
        }
        
        let fileName = "manager.db"
        GRSDBManager.initWithFileName(fileName, dir: folder)
        return true
    }
    
    @objc func defaultsChanged() {
        config.read()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
                .environment(\.programConfig, config)
        }
    }
}
