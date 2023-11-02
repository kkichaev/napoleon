//
//  ActionDetailController.swift
//  ServikoMobile
//
//  Created by ert on 23.10.2020.
//

import Foundation
import UIKit

class ActionDetailController : UIViewController, UITableViewDelegate, UITableViewDataSource, QtyChange {
        
    var actionDef : ActionDef?
    @IBOutlet weak var actionTable : UITableView!

    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.actionTable.dataSource = self
        self.actionTable.delegate = self
        
        navigationItem.title = actionDef?.getName()
        if let clauses = actionDef?.clauses {
            for cl in clauses {
                cl.isExpanded = false
            }
        }
    }
    
    override func viewDidAppear(_ animated: Bool) {
        if let bc = navigationController as? BaseController {
            bc.canChange = false
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        guard let item = itemAt(indexPath.row), let ad = item as? ActionClause  else {
            return
        }
        
        ad.nodeSelected(tableView, indexPath: indexPath)
    }
    
    func itemAt(_ index:Int) -> ActionBaseNode? {
        if let actions = actionDef?.clauses {
            return ActionBaseNode.itemAt(actions: actions, index)
        }
        return nil
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if let actions = actionDef?.clauses {
            return ActionBaseNode.count(actions:actions)
        }
        return 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let item = itemAt(indexPath.row) {
            if let ad = item as? ActionClause {
                let cell = tableView.dequeueReusableCell(withIdentifier: "actionDef", for: indexPath) as! ActionClauseCell
                cell.set(action:ad)
                return cell
            }
            let cell = tableView.dequeueReusableCell(withIdentifier: "actionGood", for: indexPath) as! PriceCell
            let ag = item as! ActionPrice
            cell.set(price: ag.item, qty: ProgData.curPartner?.getQty(item:ag.item) ?? 0)
            cell.delegate = self

            return cell
        }
        return tableView.dequeueReusableCell(withIdentifier: "actionDef", for: indexPath)
    }

    func qtyChanged(item: Price, qty: Int) {
        ProgData.curPartner?.changeBasket(item: item, qty: qty)
    }
}

class ActionClauseCell : UITableViewCell {
    @IBOutlet weak var name : UILabel!
    @IBOutlet weak var stateIndicator : UIImageView!
    
    func set(action: ActionClause) {
        name.text = action.action.name
        let imgName = action.isExpanded ? "expand_less" : "expand_more"
        stateIndicator.image = UIImage(named: imgName)
    }
}
