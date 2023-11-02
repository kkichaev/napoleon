//
//  PriceDetail.swift
//  ServikoMobile
//
//  Created by Александра on 21.10.2020.
//

import Foundation
import UIKit

class PriceDetailController : UIViewController {
    @IBOutlet weak var name : UILabel!
    @IBOutlet weak var cost : CostCrossout!
    @IBOutlet weak var actCost : UILabel!
    @IBOutlet weak var qty : QtyPicker!
    @IBOutlet weak var avail : UILabel!
    @IBOutlet weak var info : UILabel!
    
    @IBOutlet weak var image : UIImageView!
    
    var item : Price?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        guard let price = item else {
            return
        }
        
        self.qty.setData(qty: ProgData.curPartner?.getQty(item:price) ?? 0, step: price.quant, pack: price.inPack)

        name.text = price.name
        cost.text = String(format: "%.2f", price.cost)
        
        if price.discount > 0 {
            actCost.isHidden = false
            actCost.text = String(format: "%.2f", price.cost - price.discount)
            cost.crossout = true
        } else {
            cost.crossout = false
            actCost.isHidden = true
        }

        self.qty.handler = { (newQty) in
            ProgData.curPartner?.changeBasket(item:price, qty:newQty)
        }
        
        let ii = ImageItem(image:ImageCache.publicCache.placeholderImage, code:price.code)
        image.image = ii.image
        ImageCache.publicCache.load(url: ii.url as NSURL, item: ii) { (fetchedItem, image) in
            if let img = image, img != fetchedItem.image {
                self.image.image = img
            }
        }
        
        let btn1 = UIBarButtonItem(image: UIImage(named: "arrow_back"), style:.plain, target: self, action: #selector(doBack))
        btn1.tintColor = .white
        navigationItem.leftBarButtonItem = btn1
    }
    
    override func viewDidAppear(_ animated: Bool) {
        if let bc = navigationController as? BaseController {
            bc.canChange = false
        }
    }

    @objc
    func doBack() {
        navigationController?.popViewController(animated: true)
    }
}
