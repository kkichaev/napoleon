//
//  OrderDetailController.swift
//  ServikoMobile
//
//  Created by Александра on 21.10.2020.
//

import Foundation
import UIKit

class OrderDetailController : UIViewController, UITableViewDataSource {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        order?.items.count ?? 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "itemID", for: indexPath) as! OrderDetailCell
        let item = order!.items[indexPath.row]
        cell.name.text = item.name
        cell.fact.text = String(format: "%d шт.  %.2f", Int(item.qtyFact + 0.05), item.sumFact)
        cell.plan.text = String(format: "%d шт.  %.2f", Int(item.qty + 0.05), item.sum)
        return cell
    }
    
    @IBOutlet weak var table : UITableView!
    
    var order : Order?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        table.dataSource = self
        table.reloadData()
        
        let df = DateFormatter()
        df.dateFormat = "dd/MM/yyyy"
        if let ord = order {
            navigationItem.title = ord.number + " " + df.string(from: ord.orderDate)
        }
    }
    
    override func viewDidAppear(_ animated: Bool) {
        if let bc = navigationController as? BaseController {
            bc.canChange = false
        }
    }
}

class OrderDetailCell : UITableViewCell {
    @IBOutlet weak var name : UILabel!
    @IBOutlet weak var plan : UILabel!
    @IBOutlet weak var fact : UILabel!
}
