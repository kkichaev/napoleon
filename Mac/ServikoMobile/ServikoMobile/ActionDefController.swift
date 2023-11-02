//
//  ActionDefController.swift
//  ServikoMobile
//
//  Created by ert on 23.10.2020.
//

import Foundation
import UIKit

class ActionDefController : UIViewController, UITableViewDelegate, UITableViewDataSource, QtyChange {
    
    @IBOutlet weak var actionTable : UITableView!
    static var expandedActions : ActionDef?
    var hID: String = ""
    var curPartner : Partner?

    func qtyChanged(item: Price, qty: Int) {
        curPartner?.changeBasket(item: item, qty: qty)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        guard let btn = sender as? UIButton, let pcell = btn.superview?.superview as? UITableViewCell else { return }
        
        if let row = actionTable.indexPath(for: pcell)?.row , let item = itemAt(row) as? ActionDef {
            let ad = segue.destination as! ActionDetailController
            ad.actionDef = item
        }        
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        guard let item = itemAt(indexPath.row), let ad = item as? ActionDef  else {
            return
        }
        
        ad.nodeSelected(tableView, indexPath: indexPath)
    }
    
    func itemAt(_ index:Int) -> ActionBaseNode? {
        if let actions = curPartner?.actions {
            return ActionBaseNode.itemAt(actions: actions, index)
        }

        return nil
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if let actions = curPartner?.actions {
            return ActionBaseNode.count(actions:actions)
        }
        return 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let item = itemAt(indexPath.row) {
            if let ad = item as? ActionDef {
                let cell = tableView.dequeueReusableCell(withIdentifier: "actionDef", for: indexPath) as! ActionDefCell
                cell.set(action:ad)
                return cell
            }
            let cell = tableView.dequeueReusableCell(withIdentifier: "actionGood", for: indexPath) as! PriceCell
            let ag = item as! ActionGoods
            cell.set(price: ag.item, qty: curPartner?.getQty(item:ag.item) ?? 0, newCost: ag.newCost)
            cell.delegate = self
            
            return cell
        }
        return tableView.dequeueReusableCell(withIdentifier: "actionGood", for: indexPath)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        actionTable.delegate = self
        actionTable.dataSource = self
        
        hID = ProgData.AddHandler(onNewPartner(_:))

        onNewPartner(ProgData.curPartner)
    }
    
    func onNewPartner(_ partner: Partner?) {
        curPartner = partner
        if let actions = partner?.actions {
            for ad in actions {
                ad.isExpanded = (ad === ActionDefController.expandedActions)
            }
        }
        actionTable.reloadData()
    }
    
    override func removeFromParent() {
        ProgData.RemoveHandler(id: hID)
    }
    
    override func viewDidAppear(_ animated: Bool) {
        if let bc = navigationController as? BaseController {
            bc.canChange = true
            bc.refreshPartner()
        }
    }
}

class ActionDefCell : UITableViewCell {
    @IBOutlet weak var name : UILabel!
    @IBOutlet weak var stateIndicator : UIImageView!
    @IBOutlet weak var button : UIButton!
    
    func set(action: ActionDef) {
        name.text = action.getName()
        let imgName = action.isExpanded ? "expand_less" : "expand_more"
        stateIndicator.image = UIImage(named: imgName)
        button.isHidden = !action.isExpanded
    }
}
