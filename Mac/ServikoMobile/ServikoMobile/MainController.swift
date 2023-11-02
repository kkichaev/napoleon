//
//  MainController.swift
//  ServikoMobile
//
//  Created by ert on 10.10.2020.
//

import UIKit

class MainController : UITabBarController, NotifyListChanged {
    
    func changed(data: [NotifyData], unreaded: Int) {
        let tb = tabBar.items![4]
        if unreaded > 0 {
            tb.badgeValue = String(unreaded)
        } else {
            tb.badgeValue = nil
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        selectedViewController = viewControllers![2]
        
        let center = UNUserNotificationCenter.current()
        center.requestAuthorization(options: [.alert, .badge, .sound]) { granted, error in

            if let _ = error {
                // Handle the error here.
            }

            // Enable or disable features based on the authorization.
        }
        
//        var nd = NotifyData()
//        nd.title = "Title 1"
//        nd.body = "Data"
//        NotifyData.add(notify: nd)
//
//        nd = NotifyData()
//        nd.title = "Title 2"
//        nd.body = "Data2"
//        NotifyData.add(notify: nd)
//
//        nd = NotifyData()
//        nd.title = "Title 3"
//        nd.body = "Data3"
//        NotifyData.add(notify: nd)
//
//        nd = NotifyData()
//        nd.title = "Title 4"
//        nd.body = "Data4"
//        NotifyData.add(notify: nd)

        let cnt = NotifyData.countUnreaded()
        if cnt > 0 {
            let tb = tabBar.items![4]
            tb.badgeValue = String(cnt)
        }
        NotifyData.delegate = self
    }
}
