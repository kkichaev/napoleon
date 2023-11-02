//
//  NotifyController.swift
//  ServikoMobile
//
//  Created by ert on 24.10.2020.
//

import Foundation
import UIKit

class NotifyController : UIViewController, NotifyListChanged, UITableViewDelegate, UITableViewDataSource {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        notifyData.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "notify", for: indexPath) as! NotifyCell

        let n = notifyData[indexPath.row]
        cell.set(n)
        
        return cell
    }
    

    @IBOutlet weak var table : UITableView!
    
    static let FONT_SIZE = CGFloat(16)

    var notifyData = [NotifyData]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        notifyData = NotifyData.read()
        table.delegate = self
        table.dataSource = self
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        notifyData[indexPath.row].readed = true
        NotifyData.updated(notifyData)

        table.beginUpdates()
        table.reloadRows(at: [indexPath], with: .none)
        table.endUpdates()
    }
    
    @objc func markReaded(_ sender:AnyObject) {
        guard let btn = sender as? UIButton else {
            return
        }
        
        if let index = btn.superview?.subviews.firstIndex(of: btn) {
            notifyData[index].readed = true
            NotifyData.updated(notifyData)
        }
    }

    func changed(data: [NotifyData], unreaded: Int) {
        notifyData = data
        table.reloadData()
    }
}

class NotifyCell : UITableViewCell {
    static let unreadFont = UIFont.systemFont(ofSize: NotifyController.FONT_SIZE, weight: .bold)
    static let readFont = UIFont.systemFont(ofSize: NotifyController.FONT_SIZE)

    @IBOutlet weak var ntext : UILabel!
    
    func set(_ n:NotifyData) {
        ntext.text = "\n" + n.title! + "\n" + n.body! + "\n"
        ntext.font = n.readed ? NotifyCell.readFont : NotifyCell.unreadFont

        ntext.layer.borderWidth = 2
        let sp = CGColorSpace(name:CGColorSpace.sRGB)!
        let comps : [CGFloat] = [0.0, 0.0, 0.0, 1]
        ntext.layer.borderColor = CGColor(colorSpace: sp, components: comps)
        ntext.layer.cornerRadius = 6
    }
}
