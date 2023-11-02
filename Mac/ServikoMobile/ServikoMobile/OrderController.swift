//
//  OrderController.swift
//  ServikoMobile
//
//  Created by Александра on 21.10.2020.
//

import Foundation
import UIKit

class OrderController : UIViewController, UITableViewDataSource {
    
    @IBOutlet weak var noOrder : UIStackView!
    
    @IBOutlet weak var orderTable : UITableView!
    
    @IBOutlet weak var active : UIButton!
    @IBOutlet weak var finished : UIButton!
    @IBOutlet weak var unpayed : UIButton!
    
    var orders = [Order]()
    var buttons = [UIButton]()
    var curItem = 0
    var mode = Order.ORDER_STATE_ACTIVE
    
    var hID : String = ""
    let refreshControl = UIRefreshControl()
    
    override func viewDidLoad() {
        orderTable.dataSource = self
        
        buttons = [active, finished, unpayed]
        for b in buttons {
            b.addTarget(self, action: #selector(setActiveButton(_:)), for: .touchUpInside)
        }
        onNewPartner(ProgData.curPartner)
        hID = ProgData.AddHandler(onNewPartner(_:))
        orderTable.refreshControl = refreshControl
        refreshControl.addTarget(self, action: #selector(refresh), for: .valueChanged)
    }
    
    func handleError(_ res: ErrResult) -> Bool {
        if(res.result) {
            return false
        }
        
        DispatchQueue.main.async {
            let ctrl = UIAlertController(title: "Ошибка",
                                         message: res.error,
                                         preferredStyle: .alert)
            ctrl.addAction(UIAlertAction(title: "OK", style: .default))
            self.present(ctrl, animated: true)
        }
        return true
    }

    @objc func refresh() {
        let prm = ReqOrdersParam()
        prm.appId = ProgData.appID
        prm.orgId = ProgData.curPartner!.id
        
        let ws = WSExchange()
        ws.reqOrders(data: prm) { (res) in
            DispatchQueue.main.async { self.refreshControl.endRefreshing() }

            if self.handleError(res) { return }
            
            ProgData.curPartner!.orders = res.orders
            DispatchQueue.main.async {
                self.updateOrders(self.curItem, partner: ProgData.curPartner)
            }
        }
        
    }
    
    override func viewDidAppear(_ animated: Bool) {
        if let bc = navigationController as? BaseController {
            bc.canChange = true
            bc.refreshPartner()
        }
        orderTable.reloadData()
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "openPrice" { return }
        
        let row = (orderTable?.indexPathForSelectedRow?.row)!
        let el = orders[row]
        let dest = segue.destination as? OrderDetailController
        dest?.order = el
    }

    @objc func setActiveButton(_ sender:AnyObject) {
        if let index = buttons.firstIndex(of: sender as! UIButton) {
            updateOrders(index, partner: ProgData.curPartner)
        }
    }
    
    func updateOrders(_ index: Int, partner: Partner?) {
        curItem = index
        var i = 0
        for b in buttons {
            b.backgroundColor = i == curItem ? ProgData.MAIN_COLOR : .none
            b.setTitleColor(i == curItem ? .white : .blue, for: .normal)
            i+=1
        }
        
        orders.removeAll()
        if let p = partner {
            mode = curItem == 0 ? Order.ORDER_STATE_ACTIVE :
                curItem == 1 ? Order.ORDER_STATE_DONE:
                Order.ORDER_STATE_DEBT;
            for order in p.orders {
                if order.inState(state: mode) {
                    orders.append(order)
                }
            }
        }
        
        if orders.count == 0 {
            //orderTable.isHidden = true
            noOrder.isHidden = false
            view.bringSubviewToFront(noOrder)
        } else {
            //orderTable.isHidden = false
            noOrder.isHidden = true
            
            
            if curItem < 2 {
                orders.sort() { (el1, el2) in
                    el2.orderDate < el1.orderDate
                }
            } else {
                orders.sort() { (el1, el2) in
                    el1.orderDate < el2.orderDate
                }
            }
        }
        orderTable.reloadData()
    }
    
    func onNewPartner(_ partner: Partner?) {
        updateOrders(curItem, partner: partner)
    }
    
    override func removeFromParent() {
        ProgData.RemoveHandler(id: hID)
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        orders.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let order = orders[indexPath.row]
        let cell = tableView.dequeueReusableCell(withIdentifier: "orderID") as! OrderDocCell
        cell.set(order:order, mode: mode)
        
        return cell
    }
    
}

class OrderDocCell : UITableViewCell {
    @IBOutlet weak var number : UILabel!;
    @IBOutlet weak var date : UILabel!;
    @IBOutlet weak var info : UILabel!;
    
    func set(order: Order, mode:Int) {
        let df = DateFormatter()
        df.dateFormat = "dd/MM/yyyy"
        var text = "\(order.number)\n" + df.string(from: order.orderDate)
        number.text = text
        
        text = "дост. " + df.string(from: order.deliveryDate)
        date.text = text
        
        text = String(format:"%.2f\n", order.sumFact());
        if mode != Order.ORDER_STATE_DEBT { text += order.status }
        info.text = text
    }
}
