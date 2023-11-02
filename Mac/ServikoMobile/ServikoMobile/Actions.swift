//
//  Actions.swift
//  ServikoMobile
//
//  Created by Александра on 17.10.2020.
//

import Foundation
import UIKit

class ActionBaseNode : Equatable {
    init() {
        self.children = [ActionBaseNode]()
    }
    
    static func == (lhs: ActionBaseNode, rhs: ActionBaseNode) -> Bool {
        lhs !== rhs
    }
    
    var children:[ActionBaseNode]
    
    func add(_ el:ActionBaseNode) {
        children.append(el)
    }
    
    var isExpanded = false
    
    static func itemAt(actions:[ActionBaseNode], _ index:Int) ->ActionBaseNode? {
        var count = 0
        for ad in actions {
            if count == index { return ad }
            count += 1
            if ad.isExpanded {
                for ab in ad.children {
                    if count == index { return ab }
                    count += 1
                }
            }
        }
        
        return nil
    }
    
    static func count(actions:[ActionBaseNode]) -> Int {
        var count = 0
        for ad in actions {
            count += 1
            if ad.isExpanded { count += ad.children.count }
        }
        return count
    }
    
    var selecting = false
    func nodeSelected(_ tableView : UITableView, indexPath: IndexPath) {
        if selecting { return }
        
        selecting = true
        var ip = [IndexPath]()
        for i in 1...children.count {
            ip.append(IndexPath(row: indexPath.row + i, section: 0))
        }
        
        tableView.performBatchUpdates({
            let expanded = self.isExpanded
            self.isExpanded = !self.isExpanded
            if(expanded) {
                tableView.deleteRows(at: ip, with: .automatic)
            } else {
                tableView.insertRows(at: ip, with: .automatic)
            }
            tableView.reloadRows(at: [indexPath], with: .automatic)
        }, completion: { (_) in
            self.selecting = false
        })
    }
}

class ActionPrice : ActionBaseNode {
    let item : Price
    
    init(_ item: Price) {
        self.item = item
    }

    func getPrice() -> Price { return item }
}

class ActionGoods : ActionBaseNode {
    let item : Price
    let newCost : Float

    init(price:Price, discount: Float) {
        self.item = price
        self.newCost = item.cost - discount
    }
    func getItem() -> Price { return item }
}

class ActionClause : ActionBaseNode {
    let action : ActionCondition

    init(_ c: ActionCondition, price: [String:Price]) {
//        super.init()
        
        self.action = c;
        super.init()
        
        for id in c.items {
            if let p = price[id] {
                add(ActionPrice(p))
            }
        }
    }

    func isGood() -> Bool { children.count > 0 }
}

class ActionDef : ActionBaseNode {
    let action : ActionCondition
    var clauses = [ActionClause]()
    
    func getName() -> String { action.name }
    func getId() -> String { action.id }
    func getClauses() -> [ActionClause] { clauses }
    func isAdditiveAction() -> Bool { action.combineType.compare("ИЛИ", options: .caseInsensitive) == .orderedSame }
    func isGood() -> Bool { children.count > 0 }
    
//    public void expand(boolean expand) {
//        for(InMemoryTreeNode ch : getChildren()) {
//            ch.setVisible(expand);
//        }
//    }


    func addItem(_ p :Price?, rule: ActionRule) {
        if let prc = p, !contains(prc) {
            add(ActionGoods(price:prc, discount:rule.discount));
            if prc.discount < rule.discount || prc.action == nil {
                prc.discount = rule.discount;
                prc.action = self;
            }
        }
    }

    func contains(_ p:Price) -> Bool {
        for el in children {
            if let ag = el as? ActionGoods, ag.getItem().id == p.id {
                return true
            }
        }

        return false;
    }

    static func convertPrice(newPrice:[Price]) -> [String: Price] {
        var price = [String:Price]()
        for p in newPrice {
            price[p.id] = p
        }

        return price
    }

    static func create(_ newPrice:[Price], actions:[ActionRule], _ actionConditions:[ActionCondition])  -> [ActionDef]{
        let price = convertPrice(newPrice: newPrice)
        var actionMap = [String:ActionDef]()
        let conditionMap = convertContidions(actionConditions)
        
        for ar in actions {
            let p = price[ar.idPrice]
            if p == nil { continue }
            
            let ad = conditionMap[ar.idCondition]
            if ad == nil { continue }
            
            var def = actionMap[ar.idCondition]
            if def == nil {
                def = ActionDef(ar, ad!.root!, ad!.items, price)
                if !def!.isGood() { continue }
                actionMap[ar.idCondition] = def
            } else {
                def?.addItem(p, rule: ar)
            }
        }
        return Array(actionMap.values)
    }

    static func convertContidions(_ actionContidions: [ActionCondition]) -> [String:ActionData] {
        var ret = [String:ActionData]()
        
        for i in actionContidions {
            if i.isFolder {
                var ad = ret[i.id]
                if ad == nil {
                    ad = ActionData()
                    ret[i.id] = ad
                }
                ad!.root = i
            } else{
                var ad = ret[i.parent]
                if ad == nil {
                    ad = ActionData()
                    ret[i.parent] = ad
                }
                ad!.items.append(i)
            }
        }
        return ret
    }

    class ActionData {
        var root : ActionCondition? = nil
        var items = [ActionCondition]()
    }
    
//    init(text: String) {
//        let ar = ActionCondition()
//        ar.name = text
//        self.action = ar
//    }

    init(_ rule: ActionRule, _ action: ActionCondition, _ items:[ActionCondition], _ price:[String:Price]) {
        self.action = action
        super.init()
        
        addItem(price[rule.idPrice], rule: rule)
        
        for ac in items {
            let item = ActionClause(ac, price: price)
            if item.isGood() {
                clauses.append(item)
            } else if(!isAdditiveAction()) {
                children = []
            }
        }
    }
}
