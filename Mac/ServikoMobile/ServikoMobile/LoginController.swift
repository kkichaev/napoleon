//
//  LoginController.swift
//  ServikoMobile
//
//  Created by ert on 12.10.2020.
//

import Foundation
import UIKit

class LoginController : UIViewController, UITableViewDataSource {
    var reqCode : Int = 0
    var runCode = false

    var partners = [Partner]()
    
    var curLoaded : Int = 0
    
    @IBOutlet weak var phoneNumber: UITextField!
    @IBOutlet weak var edCode: UITextField!
    
    @IBOutlet weak var llProgress: UIStackView!
    @IBOutlet weak var progressBar: UIProgressView!
    
    @IBOutlet weak var progressTable: UITableView!
    @IBOutlet weak var btnAccept: UIButton!
    @IBOutlet weak var llLogin: UIStackView!
    @IBOutlet weak var llCode: UIStackView!
    
    @IBOutlet weak var image: UIImageView!
//    @IBOutlet weak var cllCode: NSLayoutConstraint!
    
    @IBOutlet weak var cllProgress: NSLayoutConstraint!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        phoneNumber.text = ProgData.phoneID
        phoneNumber.becomeFirstResponder()
        
        if let pos = phoneNumber.position(from: phoneNumber.beginningOfDocument, offset: phoneNumber.text!.count) {
            phoneNumber.selectedTextRange = phoneNumber.textRange(from: pos, to: pos)
        }
        
        progressTable.rowHeight = UITableView.automaticDimension
        progressTable.estimatedRowHeight = 45.0
        
        llProgress.isHidden = true
        progressBar.progress = 0

        // works with local data
//        if true {
//            let xp = WSParser()
//            let price = ReqPriceResult()
//            let orders = ReqOrdersResult()
//            let orgs = AcceptCodeResult()
//
//            let price1 = ReqPriceResult()
//            let orders1 = ReqOrdersResult()
//
//            xp.setObject(object: price, data: priceXML.data(using: .utf8)!)
//            xp.setObject(object: orders, data: ordersXML.data(using: .utf8)!)
//            xp.setObject(object: orgs, data: orgsXML.data(using: .utf8)!)
//
//            xp.setObject(object: price1, data: price1XML.data(using: .utf8)!)
//            xp.setObject(object: orders1, data: order1XML.data(using: .utf8)!)
//
//            let p = orgs.partners[0]
//            let prc = PriceTree.make(data: price.price)
//            let act = ActionDef.create(price.price, actions: price.actionRules, price.actionConditions)
//    //        for p in orgs.partners {
//                p.price = prc
//                p.actions = act
//                p.orders = orders.orders
//    //        }
//
//
//            let p1 = orgs.partners[1]
//            let prc1 = PriceTree.make(data: price1.price)
//            let act1 = ActionDef.create(price1.price, actions: price1.actionRules, price1.actionConditions)
//            p1.price = prc1
//            p1.actions = act1
//            p1.orders = orders1.orders
//
//            ProgData.SetPartners(orgs.partners)
//            ProgData.loggedIn = true
//            ProgData.SetRootController()
//        }
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
    
    func toPhoneNumber(phNum : String) -> String {
        var ret = ""
        
        let letters = phNum.map { Character(extendedGraphemeClusterLiteral: $0) }
        for ch in letters {
            if ch == "+" && letters.firstIndex(of: ch) == 0 {
                ret += String(ch)
                continue
            }
            if(ch.isNumber) {
                ret += String(ch)
            }
        }
        
        return ret
    }
    
    @IBAction func login(_ button : UIButton) {
        guard !runCode else {
            return
        }
        
        runCode = true
        
        let param = ReqCodeParam()
        param.phone = toPhoneNumber(phNum: phoneNumber.text!)
        param.appId = ProgData.appID	
        param.deviceId = ProgData.deviceID
        
        let ws = WSExchange()
        ws.reqCode(data: param) { (res) in
            self.runCode = false
            if self.handleError(res) {
                return
            }
            
            self.reqCode = res.code
            ProgData.phoneID = param.phone

            DispatchQueue.main.async {
//                self.cllCode.constant = -10
                self.llLogin.isHidden = true
                self.llCode.isHidden = false
                
                if self.reqCode == -1 {
                    self.acceptCode(self.btnAccept)
                } else {
                    self.edCode.becomeFirstResponder()
                }
            }
        }
    }

    @IBAction func acceptCode(_ button : UIButton) {
        guard !runCode else {
            return
        }

        let ccode = Int(edCode.text ?? "0")
        if reqCode == -1 || reqCode == ccode {
            runCode = true
        } else {
            return
        }
        
        UIApplication.shared.isIdleTimerDisabled = true
        
        let param = AcceptCodeParam()
        param.phone = ProgData.phoneID
        param.appId = ProgData.appID
        param.deviceId = ProgData.deviceID
        
        let ws = WSExchange()
        ws.acceptCode(data: param) { (res) in
            self.runCode = false
            if self.handleError(res) {
                return
            }

            DispatchQueue.main.async {
                self.btnAccept.isEnabled = false
                self.loadData(res.partners)
            }
        }
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return partners.count
    }
        
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "progressCell", for: indexPath) as! ProgressCell
        let partner = partners[indexPath.row]

        cell.name.text = partner.text
        
        if(partner.price.count > 0) {
            cell.action.text = "OK"
        } else if( self.curLoaded == indexPath.row) {
            cell.action.text = "Загрузка"
        } else {
            cell.action.text = ""
        }
        
        return cell
    }
    
    func loadingPartner(index:Int) {
        if index >= self.partners.count {
            ProgData.SetPartners(self.partners)
            return
        }
        
        let partner = self.partners[index]
        let prm = ReqPriceParam()
        
        prm.appId = ProgData.appID
        prm.deviceId = ProgData.deviceID
        prm.orgId = partner.id

        self.curLoaded = index
        let ip = IndexPath(row: index, section: 0)
        self.progressTable.reloadRows(at: [ip], with: .none)

        var ws = WSExchange()
        ws.reqPrice(data: prm) { (res) in
            if(self.handleError(res)) {
                return
            }

            partner.price = PriceTree.make(data: res.price)
            partner.actions = ActionDef.create(res.price, actions: res.actionRules, res.actionConditions)
            
            let prmo = ReqOrdersParam()
            prmo.orgId = partner.id
            prmo.appId = ProgData.appID
            
            ws = WSExchange()
            ws.reqOrders(data: prmo) { (resO) in
                if(self.handleError(res)) {
                    return
                }
                
                partner.orders = resO.orders
                self.curLoaded = -1

                DispatchQueue.main.async {
                    self.progressBar.progress = Float(index + 1) / Float(self.partners.count)
                    self.progressTable.reloadRows(at: [ip], with: .none)

                    self.loadingPartner(index: index + 1)
                }
            }
        }
    }
    
    func loadData(_ partners:[Partner]) {
        self.curLoaded = -1
        self.partners = partners

        self.cllProgress.constant = -10
        self.llCode.isHidden = true
        self.llProgress.isHidden = false
        
        progressTable.dataSource = self
        progressTable.reloadData()
        
        let tap = UITapGestureRecognizer(target:self.view, action: #selector(UIView.endEditing(_:)))
        view.addGestureRecognizer(tap)
        
        loadingPartner(index: 0)
    }
}

class ProgressCell : UITableViewCell {
    @IBOutlet weak var name: UILabel!
    @IBOutlet weak var action: UILabel!
}
