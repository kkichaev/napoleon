//
//  MainViewController.swift
//  ServikoMobile
//
//  Created by Александра on 18.10.2020.
//

import Foundation
import UIKit

class MainViewController : UIViewController, UITableViewDataSource, UITableViewDelegate {
    static let TEXT_WIDTH = CGFloat(200)
    static let FONT_SIZE = CGFloat(15)
    static let CTRL_GAP = CGFloat(4)
    
    @IBOutlet weak var actionView : UIView?
    @IBOutlet weak var actionWidth: NSLayoutConstraint!
    @IBOutlet weak var actionScroll: UIScrollView!

    @IBOutlet weak var orderView : UIView?
    @IBOutlet weak var orderWidth: NSLayoutConstraint!
    @IBOutlet weak var orderScroll: UIScrollView!

    @IBOutlet weak var priceTable : UITableView?
    @IBOutlet weak var priceHeight: NSLayoutConstraint!
    
    var folders = [Folder]()
    var hID: String = ""
    var orders = [Order]()
    
//    override func viewDidAppear(_ animated: Bool) {
//        ProgData.AddHandler(onNewPartner(_:))
//    }
//
//    override func viewDidDisappear(_ animated: Bool) {
//        ProgData.RemoveHandlers()
//    }
    
    override func removeFromParent() {
        super.removeFromParent()
        ProgData.RemoveHandler(id: hID)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        priceTable?.delegate = self
        priceTable?.dataSource = self
        priceTable?.rowHeight = UITableView.automaticDimension
        priceTable?.estimatedRowHeight = 45
    
        hID = ProgData.AddHandler(onNewPartner(_:))
    }
    
    override func viewDidAppear(_ animated: Bool) {
        if let bc = navigationController as? BaseController {
            bc.canChange = true
            bc.refreshPartner()
        }
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int { folders.count }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "folderID", for: indexPath) as! FolderCell
        let f = folders[indexPath.row]
        cell.set(folder:f)
        return cell
    }
    
//    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
//        let f = folders[indexPath.row]
//        let pc = storyboard?.instantiateViewController(identifier: "priceController") as! PriceController
//        pc.curFolder = f
//
//        navigationController?.pushViewController(pc, animated: true)
//    }
    
    override func shouldPerformSegue(withIdentifier identifier: String, sender: Any?) -> Bool {
        identifier == "openPrice"
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "openPrice",
           let dest = segue.destination as? PriceController,
           let row = priceTable?.indexPathForSelectedRow?.row
        {
            dest.reqFolderId = folders[row].item.id
        }
    }
    
    @objc func openAcion(_ sender : AnyObject) {
        guard let btn = sender as? UIButton else { return }
        if let index = btn.superview?.subviews.firstIndex(of: btn) {
            if let act = ProgData.curPartner?.actions[index] {
                
                ActionDefController.expandedActions = act
                tabBarController?.selectedIndex = 1
            }
        }
        
    }
    
    func makeActionButton(action:ActionDef, bounds:CGRect, font:UIFont) -> UIButton {
        let b = UIButton()

        b.frame = bounds
        b.setTitle(action.getName(), for: .normal)

        let label = b.titleLabel!
        label.lineBreakMode = .byWordWrapping
        label.font = font

        b.setTitleColor(.black, for: .normal)
        b.layer.borderWidth = 1
        
        let sp = CGColorSpace(name:CGColorSpace.genericRGBLinear)!
        let comps : [CGFloat] = [0.3, 0.3, 0.3, 1]
        let c = CGColor(colorSpace: sp, components: comps)!
        b.layer.borderColor = c
        b.layer.cornerRadius = 5
        
        b.addTarget(self, action: #selector(openAcion(_:)), for: .touchUpInside)
        
        return b
    }
    
    func loadActions(_ partner:Partner) {
        let font = UIFont.systemFont(ofSize: MainViewController.FONT_SIZE)
        var bounds = CGRect(x:0, y:0, width: MainViewController.TEXT_WIDTH, height: (actionView?.frame.height)!)
        
        for ad in partner.actions {
            let b = makeActionButton(action:ad, bounds:bounds, font: font)
            actionView?.addSubview(b)
            bounds = bounds.offsetBy(dx: bounds.width + MainViewController.CTRL_GAP, dy: 0)
        }
        let actionViewWidth = bounds.minX - MainViewController.CTRL_GAP
        actionScroll.contentSize.width = actionViewWidth
        actionWidth.constant = actionViewWidth
    }
    
    func uiColor(_ color: Int) -> UIColor {
        return UIColor(
            red: CGFloat((color & 0xFF0000) >> 16) / 255.0,
            green: CGFloat((color & 0xFF00) >> 8) / 255.0,
            blue:  CGFloat(color & 0xFF) / 255.0,
            alpha: CGFloat((color & 0xFF000000) >> 24) / 255.0)
    }
    
    @objc func openOrder(_ sender : AnyObject) {
        guard let btn = sender as? UIButton else { return }
        if let index = btn.superview?.subviews.firstIndex(of: btn) {
            let order = orders[index]
            let ctrl = storyboard?.instantiateViewController(withIdentifier: "OrderDetail") as! OrderDetailController
            ctrl.order = order
            navigationController?.pushViewController(ctrl, animated: true)
        }
        
    }

    func makeOrderButton(order:Order, bounds:CGRect, font:UIFont, boldFont:UIFont) -> UIButton {
        let b = UIButton()

        b.frame = bounds

        let status = NSAttributedString(string: order.status, attributes: [NSAttributedString.Key.font: boldFont])

        let df = DateFormatter()
        df.dateFormat = "dd/MM/yyyy"
        var text = "Заказ от " + df.string(from: order.orderDate)
        text += String(format:"\nСумма %.2f\nСтатус ", order.sumFact())

        let title = NSMutableAttributedString(string: text)
        title.append(status)
        b.setAttributedTitle(title, for: .normal)

        let label = b.titleLabel!
        label.lineBreakMode = .byWordWrapping
        label.font = font
        
        b.setTitleColor(.black, for: .normal)
        b.layer.borderWidth = 1
        b.layer.borderColor = uiColor(0xFF60ec0b).cgColor
        b.layer.cornerRadius = 5
        
        b.addTarget(self, action: #selector(openOrder(_:)), for: .touchUpInside)
        
        return b
    }
    

    func loadOrders(_ partner:Partner) {
        let font = UIFont.systemFont(ofSize: MainViewController.FONT_SIZE)
        let boldFont = UIFont.boldSystemFont(ofSize: MainViewController.FONT_SIZE)
        var bounds = CGRect(x:0, y:0, width: MainViewController.TEXT_WIDTH, height: (orderView?.frame.height)!)
        
        orders = [Order]()
        for o in partner.orders {
            if !o.inState(state: Order.ORDER_STATE_ACTIVE) { continue }
            orders.append(o)
        }
        
        orders.sort() { (o1, o2) in o2.orderDate < o1.orderDate }
        
        for o in orders {
            let b = makeOrderButton(order:o, bounds:bounds, font: font, boldFont: boldFont)
            orderView?.addSubview(b)
            bounds = bounds.offsetBy(dx: bounds.width + MainViewController.CTRL_GAP, dy: 0)
        }
        let viewWidth = bounds.minX - MainViewController.CTRL_GAP
        orderScroll.contentSize.width = viewWidth
        orderWidth.constant = viewWidth
    }
    
    func onNewPartner(_ partner: Partner?) {
        actionView?.subviews.forEach{ $0.removeFromSuperview() }
        orderView?.subviews.forEach{ $0.removeFromSuperview() }

        if partner == nil {
            folders = []
            priceTable?.reloadData()
            return
        }
        
        loadActions(partner!)
        loadOrders(partner!)

        folders = partner!.price.getRoot().childs
        priceTable?.reloadData()
        
        let tblH : CGFloat
        if #available(iOS 11.0, *) {
            let maxH = self.view.bounds.height - (actionView?.frame.maxY)! - self.view.safeAreaInsets.bottom - MainViewController.CTRL_GAP
            tblH = min((priceTable?.contentSize.height)!, maxH)
        } else {
            let maxH = self.view.bounds.height - (actionView?.frame.maxY)! - self.view.frame.minX - MainViewController.CTRL_GAP
            tblH = min((priceTable?.contentSize.height)!, maxH)
        }
        priceHeight.constant = tblH
    }
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
//        if let wdh = actioViewWidth {
//            actionScroll.contentSize.width = wdh
//        }
    }
}

