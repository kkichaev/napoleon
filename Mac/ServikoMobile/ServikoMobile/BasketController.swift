//
//  Basket.swift
//  ServikoMobile
//
//  Created by ert on 23.10.2020.
//

import Foundation
import UIKit

class BasketController : UIViewController, UITableViewDataSource, QtyChange {
    @IBOutlet weak var table : UITableView!

    var basket : Basket?
    var sending = false
    var hID : String = ""
    @IBOutlet weak var progress: UIActivityIndicatorView!

    override func viewDidLoad() {
        super.viewDidLoad()

        basket = ProgData.curPartner?.basket
        navigationItem.title = String(format: "Итого: %.02f", basket?.sum ?? 0)
        table.dataSource = self
        
        let btn = UIBarButtonItem(image: UIImage(named: "calendar"), style:.plain, target: self, action: #selector(openDetail))
        btn.tintColor = .white
        let btn1 = UIBarButtonItem(image: UIImage(named: "send"), style:.plain, target: self, action: #selector(send))
        btn1.tintColor = .white
        
        navigationItem.rightBarButtonItems = [btn1, btn]
        hID = ProgData.AddHandler(onNewPartner(_:))
        
        progress.center = view.center
        view.bringSubviewToFront(progress)
        self.progress.isHidden = true
    }
    
    override func viewDidAppear(_ animated: Bool) {
        if let bc = navigationController as? BaseController {
            bc.canChange = true
            bc.refreshPartner()
        }
    }
    
    override func removeFromParent() {
        ProgData.RemoveHandler(id:hID)
    }

    func onNewPartner(_ partner: Partner?) {
        basket = partner?.basket
        navigationItem.title = String(format: "Итого: %.02f", basket?.sum ?? 0)
        table.reloadData()
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

    @objc func send() {
        if sending { return }
        sending = true
        
        basket?.commit()
        guard (basket?.count ?? 0) > 0, let partner = ProgData.curPartner else {
            sending = false
            return
        }
        
        progress.isHidden = false
        progress.startAnimating()
        
        let prm = SendBasketParam()
        prm.orgId = partner.id
        prm.appId = ProgData.appID
        prm.deviceId = ProgData.deviceID
        prm.orders.append(OrderSend(src:basket!))
        
        let ws = WSExchange()
        ws.sendOrder(data: prm) { (res) in
            self.sending = false
                        
            guard !self.handleError(res) else {
                DispatchQueue.main.async {
                    self.progress.isHidden = true
                    self.progress.stopAnimating()
                }
                return
            }
            
            let prm = ReqOrdersParam()
            prm.appId = ProgData.appID
            prm.orgId = ProgData.curPartner!.id
            
            let ws = WSExchange()
            ws.reqOrders(data: prm) { (res) in
                if self.handleError(res) {
                    DispatchQueue.main.async {
                        self.progress.isHidden = true
                        self.progress.stopAnimating()
                    }
                    return
                }
                
                ProgData.curPartner!.orders = res.orders
                DispatchQueue.main.async {
                    self.progress.isHidden = true
                    self.progress.stopAnimating()
                    self.basket?.clear()
                    self.tabBarController?.selectedIndex = 0
                }
            }
        }
    }
    
    @objc func openDetail() {
        var basketDetail : UIViewController?
        if #available(iOS 13.0, *) {
            basketDetail = storyboard?.instantiateViewController(identifier: "basketDetail")
        } else {
            basketDetail = storyboard?.instantiateViewController(withIdentifier: "basketDetail")
        }

        navigationController?.pushViewController(basketDetail!, animated: true)
    }
    
    func qtyChanged(item: Price, qty: Int) {
        navigationItem.title = String(format: "Итого: %.02f", basket!.sum)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        table.reloadData()
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        basket?.count ?? 0
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        guard let fld = basket?.items[indexPath.row], let cell = tableView.dequeueReusableCell(withIdentifier: "basket", for: indexPath) as? BasketCell else {
            return tableView.dequeueReusableCell(withIdentifier: "basket", for: indexPath)
        }
        cell.set(item:fld)
        cell.delegate = self
        return cell
    }
}

class BasketDetailController : UIViewController {
    @IBOutlet weak var dlvDate : UIDatePicker!
    @IBOutlet weak var remark : UITextView!
    
    @IBOutlet weak var bottomContraint: NSLayoutConstraint!
    
    @IBAction func close (_ sender : UIButton) {
        if let basket = ProgData.curPartner?.basket {
            basket.remark = remark.text ?? ""
            basket.dlvDate = dlvDate.date
        }
        navigationController?.popViewController(animated: true)
    }
    
    @objc func kbShow(_ sender: NSNotification) {
        let i = sender.userInfo!
        let s: TimeInterval = (i[UIResponder.keyboardAnimationDurationUserInfoKey] as! NSNumber).doubleValue
        let k = (i[UIResponder.keyboardFrameEndUserInfoKey] as! NSValue).cgRectValue.height
        bottomContraint.constant = k
        // Note. that is the correct, actual value. Some prefer to use:
        // bottomConstraintForKeyboard.constant = k - bottomLayoutGuide.length
        UIView.animate(withDuration: s) { self.view.layoutIfNeeded() }
    }
    
    @objc func kbHide(_ sender: NSNotification) {
        let info = sender.userInfo!
        let s: TimeInterval = (info[UIResponder.keyboardAnimationDurationUserInfoKey] as! NSNumber).doubleValue
        bottomContraint.constant = 0
        UIView.animate(withDuration: s) { self.view.layoutIfNeeded() }
    }
    

    override func viewDidLoad() {
        super.viewDidLoad()
        
        if let basket = ProgData.curPartner?.basket {
            remark.text = basket.remark
            let minDate = Calendar.current.date(byAdding: .day, value: 1, to: Date())
            let maxDate = Calendar.current.date(byAdding: .day, value: 14, to: Date())
            dlvDate.date = basket.dlvDate ?? minDate!
            dlvDate.minimumDate = minDate
            dlvDate.maximumDate = maxDate
        }
        navigationItem.title = "Детали доставки заказа"

        let btn2 = UIBarButtonItem(image: UIImage(named: "arrow_back"), style:.plain, target: self, action: #selector(doBack))
        navigationItem.leftBarButtonItem = btn2
    
        NotificationCenter.default.addObserver(self, selector: #selector(kbShow), name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(kbHide), name: UIResponder.keyboardWillHideNotification, object: nil)
    }
    
    override func viewDidAppear(_ animated: Bool) {
        if let bc = navigationController as? BaseController {
            bc.canChange = false
        }
    }

    @objc func doBack() {
        navigationController?.popViewController(animated: true)
    }

}

class BasketCell : UITableViewCell {
    @IBOutlet weak var name: UILabel!
    @IBOutlet weak var cost: CostCrossout!
    @IBOutlet weak var actCost : UILabel!
    @IBOutlet weak var qty: QtyPicker!
    
    @IBOutlet var itemImage : UIImageView!
    @IBOutlet weak var sum : UILabel!
    
    var delegate : QtyChange?
    var item : BasketItem?
    
    func set(item:BasketItem) {
        self.item = item
        self.qty.setData(qty: item.qty, step: item.item.quant, pack: item.item.inPack)
        self.sum.text =  String(format: "%.2f", item.sum)
        
        let ccost = item.cost
        let dscCost = item.cost - item.discount
        name.text = item.item.name
        
        cost.text = String(format: "%.2f", ccost)
        
        if dscCost < ccost {
            actCost.isHidden = false
            actCost.text = String(format: "%.2f", dscCost)
            cost.crossout = true
        } else {
            cost.crossout = false
            actCost.isHidden = true
        }
        
        let ii = ImageItem(image:ImageCache.publicCache.placeholderImage, code:item.item.code)
        itemImage.image = ii.image
        ImageCache.publicCache.load(url: ii.url as NSURL, item: ii) { (fetchedItem, image) in
            if let img = image, img != fetchedItem.image {
                self.itemImage.image = img
            }
        }

        self.qty.handler = { (newQty) in
            item.qty = newQty
            self.sum.text =  String(format: "%.2f", item.sum)
            if let dlg = self.delegate {
                dlg.qtyChanged(item:self.item!.item, qty:newQty)
            }
        }
    }
}

protocol BasketHandler {
	func changed(sender:Basket)
}

class BasketItem : Equatable {
    var item: Price
    var qty: Int = 0;
    var cost: Float
    var discount: Float

    static func == (lhs: BasketItem, rhs: BasketItem) -> Bool {
        lhs.item.id == rhs.item.id
    }
    
    init(src: Price) {
	    self.item = src
        self.cost = src.cost
        self.discount = src.discount
    }

    var sum: Float { get { Float(qty) * cost } }
}

class Basket {
    var handlers = [String:BasketHandler]()
	var items = [BasketItem]()

    var canRemove = false

    var remark : String = ""
    var dlvDate = Calendar.current.date(byAdding: .day, value: 1, to: Date())

    var uid =  UUID().uuidString

    var count: Int { get { items.count } }

    func addHandler(handler:BasketHandler) -> String {
        let id = UUID().uuidString
        handlers[id] = handler
        return id
	}

    func removeHandler(_ id: String) {
        handlers.removeValue(forKey: id)
	}

    func getQty(item:Price) -> Int {
        for i in items {
            if i.item.id == item.id { return i.qty }  
		}
        return 0
	}
    
    func sayChanged() {
        for h in handlers {
            h.value.changed(sender:self)
        }
    }

    func commit() {
        var dest = [BasketItem]()
        for i in items {
            if i.qty > 0 { dest.append(i)}  
		}

        items = dest

        sayChanged()
	}

    func clear() {
        items = [BasketItem]()
        dlvDate = Calendar.current.date(byAdding: .day, value: 1, to: Date())
        dlvDate = Calendar.current.date(byAdding: .day, value: 1, to: Date())

        sayChanged()
	}

    func changeQty(item:Price, qty:Int) {
        var i = find(item:item)
        if i == nil {
            if qty == 0 { return }
            i = add(item: item)
        }

        if i!.qty != qty {
            if qty == 0  {
                if canRemove {
                    items.remove(at: items.firstIndex(of: i!)!)
                } else {
                    i!.qty = 0
                }
            } else {
                i!.qty = qty;
            }
            
            sayChanged()
        }
    }

    func find(item:Price) -> BasketItem? {
        for i in self.items {
            if i.item.id == item.id { return i }
		}

        return nil
    }

    func add(item:Price) -> BasketItem {
        let bi = BasketItem(src:item)
        items.append(bi)
        return bi
    }

    var sum: Float { 
        get {
            var tsum = Float(0)
            for i in items {
                 tsum += i.sum 
		    }
            return tsum
        }
    }
}
