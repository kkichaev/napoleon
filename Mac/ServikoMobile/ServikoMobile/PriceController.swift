//
//  PriceController.swift
//  ServikoMobile
//
//  Created by Александра on 18.10.2020.
//

import Foundation
import UIKit

class PriceController : UIViewController, UINavigationControllerDelegate, UITableViewDataSource, UISearchBarDelegate, QtyChange, OpenAction {
        
    static let SEARCH_HEIGHT = CGFloat(42)
    
    @IBOutlet weak var priceTable : UITableView!
    @IBOutlet weak var searchBar : UISearchBar!
    var tableConstraint : NSLayoutConstraint?
    
    var reqFolderId:String?
    var folders = [Folder]()
    var curFolder: Folder?
    var searchRoot: Folder?

    var inSearch = false
    var searchTimer : Timer?
    var hID : String = ""
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        priceTable.dataSource = self
        priceTable.rowHeight = UITableView.automaticDimension
//        priceTable.estimatedRowHeight = 150
        
        if #available(iOS 11.0, *) {
            tableConstraint = priceTable.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor)
        } else {
            tableConstraint = priceTable.topAnchor.constraint(equalTo: view.topAnchor)
        }
        tableConstraint?.isActive = true
        navigationItem.leftBarButtonItem?.tintColor = .white
        
        let btn = UIBarButtonItem(image: UIImage(named: "search_24"), style:.plain, target: self, action: #selector(searchOnOf))
        btn.tintColor = .white
        navigationItem.rightBarButtonItem = btn

        let btn1 = UIBarButtonItem(image: UIImage(named: "arrow_back"), style:.plain, target: self, action: #selector(doBack))
        btn1.tintColor = .white
        navigationItem.leftBarButtonItem = btn1
        navigationController!.navigationBar.backgroundColor = ProgData.MAIN_COLOR
        
        onNewPartner(ProgData.curPartner)
        searchBar.delegate = self

        hID = ProgData.AddHandler(onNewPartner(_:))
    }
    
    override func viewDidAppear(_ animated: Bool) {
        if let bc = navigationController as? BaseController {
            bc.canChange = true
            bc.refreshPartner()
        }
        priceTable.reloadData()
    }
    
    func open(action: ActionDef) {
        ActionDefController.expandedActions = action
        tabBarController?.selectedIndex = 1
//        let ctrl = storyboard?.instantiateViewController(withIdentifier: "ActionList") as! ActionDefController
//        navigationController?.pushViewController(ctrl, animated: true)
    }
    
    func qtyChanged(item: Price, qty: Int) {
        ProgData.curPartner?.changeBasket(item:item, qty:qty)
    }
    
    func doSearch(string: String) {
        if !inSearch { searchRoot = curFolder }
        
        let srchFolder = Folder()
        srchFolder.item.name = "Поиск товаров"
        let str = string.replacingOccurrences(of: " ", with: "(.* .*)")
        searchRoot?.findItem(dest: &srchFolder.items, pattern: str)
        
        if inSearch { let _ = folders.popLast() }
        
        inSearch = true
        openFolder(folder: srchFolder)
    }
    
    func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
        if let tmr = searchTimer {
            tmr.invalidate()
        }
        if searchText.trimmingCharacters(in: .whitespaces).isEmpty {
//            clearSearch()
            return
        }
        searchTimer = Timer(timeInterval: 0.5, repeats: false) { (timer) in
            timer.invalidate()
            self.searchTimer = nil
            self.doSearch(string: searchText)
        }
        RunLoop.current.add(searchTimer!, forMode: .common)
    }
    
    override func removeFromParent() {
        ProgData.RemoveHandler(id:hID)
    }
    
    override func shouldPerformSegue(withIdentifier identifier: String, sender: Any?) -> Bool {
        let row = (priceTable?.indexPathForSelectedRow?.row)!
        if let f = curFolder![row] as? Folder {
            openFolder(folder: f)
            return false
        }
        
        return true
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        let row = (priceTable?.indexPathForSelectedRow?.row)!
        let el = curFolder![row]
        let dest = segue.destination as? PriceDetailController
        dest?.item = el.item
    }

    func openFolder(folder: Folder) {
        folders.append(folder)
        curFolder = folder
        
        let title = folder.item.name
        navigationItem.title = title.isEmpty ? "Прайс-лист" : title
        
        priceTable.reloadData()
    }

    func onNewPartner(_ partner: Partner?) {
        let rootFolder = partner?.price.getRoot()
        if reqFolderId == nil {
            curFolder = rootFolder
        } else {
            curFolder = rootFolder?.findFolder(id: reqFolderId!) ?? rootFolder
            reqFolderId = nil
        }
        folders.removeAll()
        openFolder(folder: curFolder!)
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        curFolder?.count ?? 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let el = curFolder?[indexPath.row]
        if let fld = el as? Folder {
            let cell = tableView.dequeueReusableCell(withIdentifier: "folderID", for: indexPath) as! FolderCell
            cell.set(folder: fld)
            return cell
        }
        let cell = tableView.dequeueReusableCell(withIdentifier: "priceID", for: indexPath) as! PriceCell
        let prc = el!.item
        cell.set(price: prc, qty: ProgData.curPartner?.getQty(item:prc) ?? 0)
        cell.delegate = self
        cell.actionDelegate = self
        return cell
    }
    

    @objc
    func doBack() {
        if !searchBar.isHidden {
            searchOnOf()
            return
        }
        
        if folders.count <= 1 {
            navigationController?.popViewController(animated: true)
        } else {
            let _ = folders.popLast()
            let cf = folders.popLast()!
            openFolder(folder: cf)
        }
    }
    
    @objc
    func searchOnOf() {
        if inSearch {
            let _ = folders.popLast()
            let cf = folders.popLast()!
            openFolder(folder: cf)
        }
        
        inSearch = false
        searchBar.isHidden = !searchBar.isHidden
        if searchBar.isHidden {
            tableConstraint?.constant = 0
            searchBar.text = ""
            searchBar.endEditing(true)
        } else {
            searchBar.becomeFirstResponder()
            tableConstraint?.constant = PriceController.SEARCH_HEIGHT
        }
    }
}

class FolderCell : UITableViewCell {
    @IBOutlet weak var folder : UILabel!
    
    func set(folder: Folder) {
        self.folder?.text = folder.item.name
    }
}

class CostCrossout : UILabel {
    var crossout : Bool {
        didSet {
            setNeedsDisplay()
        }
    }
    override init(frame: CGRect) {
        crossout = false
        super.init(frame: frame)
    }
    
    required init?(coder: NSCoder) {
        crossout = false
        super.init(coder: coder)
    }
        
    override func draw(_ rect: CGRect) {
        super.draw(rect)
        guard crossout, let context = UIGraphicsGetCurrentContext() else {
            return
        }
        
        let bnd = CGRect(x:0, y:0, width: bounds.width * 2, height: bounds.height * 2)
        let rc = textRect(forBounds: bnd, limitedToNumberOfLines: 1)
        context.setStrokeColor(UIColor.red.cgColor)
        context.setLineWidth(3)
        context.move(to: CGPoint(x: rc.minX, y: rc.minY))
        context.addLine(to: CGPoint(x: rc.maxX, y: rc.maxY + 2))
        context.strokePath()
    }
}

protocol QtyChange {
    func qtyChanged(item:Price, qty:Int)
}

protocol OpenAction {
    func open(action:ActionDef)
}

class PriceCell : UITableViewCell {
    
    @IBOutlet weak var name : UILabel!
    @IBOutlet weak var cost : CostCrossout!
    @IBOutlet weak var actCost : UILabel!
    @IBOutlet weak var qty: QtyPicker!
    
    @IBOutlet weak var actionButton : UIButton?
    @IBOutlet weak var actionHeight: NSLayoutConstraint?
    
    @IBOutlet var itemImage : UIImageView!
    
    var delegate : QtyChange?
    var actionDelegate : OpenAction?
    
    var action : ActionDef?
    
    func set(price: Price, qty:Int, newCost:Float = 0) {
        self.qty.setData(qty: qty, step: price.quant, pack: price.inPack)
        
        let ccost = price.cost
        let dscCost = newCost == 0 ? ccost - price.discount : newCost
        name.text = price.name
        
        cost.text = String(format: "%.2f", ccost)
        
        if dscCost < ccost {
            actCost.isHidden = false
            actCost.text = String(format: "%.2f", dscCost)
            cost.crossout = true
        } else {
            cost.crossout = false
            actCost.isHidden = true
        }
        
        let ii = ImageItem(image:ImageCache.publicCache.placeholderImage, code:price.code)
        itemImage.image = ii.image
        ImageCache.publicCache.load(url: ii.url as NSURL, item: ii) { (fetchedItem, image) in
            if let img = image, img != fetchedItem.image {
                self.itemImage.image = img
            }
        }

        self.qty.handler = { (newQty) in
            if let dlg = self.delegate {
                dlg.qtyChanged(item:price, qty:newQty)
            }
        }
        
        if let btn = actionButton {
            if let act = price.action, act.isGood() {
                self.action = act
                btn.isHidden = false
                actionHeight?.constant = 30
                btn.addTarget(self, action: #selector(openAction(_:)), for: .touchUpInside)
            } else {
                btn.isHidden = true
                actionHeight?.constant = 0
            }
        }
    }
    
    @objc func openAction(_ sender : AnyObject) {
        actionDelegate?.open(action: self.action!)
    }
}

class QtyPicker : UIView {
    static let BTN_HEIGHT = CGFloat(30)
    let btnUp = UIButton()
    let btnDn = UIButton()
    let qtyLabel = UILabel()
    
    var qty : Int = 0
    var step : Int = 1
    var pack : Int = 1
    
    var runned = false
    let startInterval = 0.8
    let repeatInterval = 0.5
    
    var handler : ((_:Int)->Void)?
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        self.setup()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        self.setup()
    }
        
    func setData(qty:Int, step:Int, pack:Int) {
        self.qty = qty
        self.pack = pack
        self.step = step
        
        draw()
    }
    
    func draw() {
        var text = String(qty)
        if qty > 0 && pack > 1 && ((qty % pack) == 0) {
             text += "(\(qty / pack) уп)"
        }
        qtyLabel.text = text
    }
    
    @objc func down() {
        runned = true
        performDown()
        
        DispatchQueue.main.asyncAfter(deadline: .now() + startInterval) {
            self.performDown()
        }
    }
    
    func performDown() {
        if !runned { return }
        
        if qty > step {
            qty -= step
        } else {
            qty = 0
        }
        if let h = handler {
            h(qty)
        }
        draw()

        DispatchQueue.main.asyncAfter(deadline: .now() + repeatInterval) {
            self.performDown()
        }
    }
    
    @objc func up() {
        runned = true
        performUp()
        
        DispatchQueue.main.asyncAfter(deadline: .now() + startInterval) {
            self.performUp()
        }
    }
    
    func performUp() {
        if !runned { return }

        qty += step
        if let h = handler {
            h(qty)
        }
        draw()

        DispatchQueue.main.asyncAfter(deadline: .now() + repeatInterval) {
            self.performUp()
        }
    }
    
    @objc func stop() { runned = false  }
    
    func setup() {
//        backgroundColor = .green
//        qtyLabel.backgroundColor = .red

        qtyLabel.font = UIFont.systemFont(ofSize: 16, weight: .bold)
        qtyLabel.textAlignment = .center
        
        addSubview(btnUp)
        addSubview(btnDn)
        addSubview(qtyLabel)
        
        btnDn.translatesAutoresizingMaskIntoConstraints = false
        btnDn.leadingAnchor.constraint(equalTo: leadingAnchor).isActive = true
        btnDn.centerYAnchor.constraint(equalTo: centerYAnchor).isActive = true
        btnDn.heightAnchor.constraint(equalToConstant: QtyPicker.BTN_HEIGHT).isActive = true
        btnDn.widthAnchor.constraint(equalTo: btnDn.heightAnchor).isActive = true
        btnDn.addTarget(self, action: #selector(down), for: .touchDown )
        btnDn.addTarget(self, action: #selector(stop), for: .touchUpInside)
        btnDn.addTarget(self, action: #selector(stop), for:UIControl.Event.touchUpOutside )
        btnDn.setImage(UIImage(named: "arrow_back"), for: .normal)
        btnDn.tintColor = tintColor
        btnDn.layer.borderWidth = 1
        btnDn.layer.borderColor = tintColor.cgColor
        btnDn.layer.cornerRadius = 3
        btnDn.contentMode = .center
        btnDn.imageView?.contentMode = .scaleAspectFit
        
        btnUp.translatesAutoresizingMaskIntoConstraints = false
        btnUp.trailingAnchor.constraint(equalTo: trailingAnchor).isActive = true
        btnUp.centerYAnchor.constraint(equalTo: centerYAnchor).isActive = true
        btnUp.heightAnchor.constraint(equalToConstant: QtyPicker.BTN_HEIGHT).isActive = true
        btnUp.widthAnchor.constraint(equalTo: btnUp.heightAnchor).isActive = true
        btnUp.addTarget(self, action: #selector(up), for: .touchDown)
        btnUp.addTarget(self, action: #selector(stop), for: .touchUpInside)
        btnUp.addTarget(self, action: #selector(stop), for: .touchUpOutside)
        btnUp.setImage(UIImage(named: "arrow_right"), for: .normal)
        btnUp.tintColor = tintColor
        btnUp.layer.borderWidth = 1
        btnUp.layer.borderColor = tintColor.cgColor
        btnUp.layer.cornerRadius = 3
        btnUp.contentMode = .center
        btnUp.imageView?.contentMode = .scaleAspectFit

        qtyLabel.translatesAutoresizingMaskIntoConstraints = false
        qtyLabel.leadingAnchor.constraint(equalTo: btnDn.trailingAnchor, constant: 5).isActive = true
        qtyLabel.trailingAnchor.constraint(equalTo: btnUp.leadingAnchor, constant: -5).isActive = true
        qtyLabel.centerYAnchor.constraint(equalTo: centerYAnchor).isActive = true
        qtyLabel.bottomAnchor.constraint(equalTo: bottomAnchor).isActive = true
    }
}
