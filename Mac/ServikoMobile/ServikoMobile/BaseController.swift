//
//  BaseCobtroller.swift
//  ServikoMobile
//
//  Created by Александра on 17.10.2020.
//

import Foundation
import UIKit

//@IBDesignable
class BaseController : UINavigationController, BasketHandler {

    static let TEXT_HEIGHT = CGFloat(40)
    static let IMG_HEIGHT = CGFloat(24)
    static let ORG_FONT_SIZE = CGFloat(14)

    var orgView = UIView()
    var orgButton = UIButton()
    var orgLinesButton = UIButton()
    var imgv: UIImageView?
    var heightView : NSLayoutConstraint?
    var widthOrg : NSLayoutConstraint?
    var showFullPartnerText = false
    var basketHandlerId : String = ""
    
    var canChange = true
    
    override func viewDidLoad() {
        setup()
        if let p = ProgData.curPartner {
            setPartner(p)
        }
    }
    
    func changed(sender: Basket) {
        if let tabItems = tabBarController?.tabBar.items {
            let tabItem = tabItems[3]
            let qty = sender.count
            if qty > 0 {
                tabItem.badgeValue = String(qty)
            } else {
                tabItem.badgeValue = nil
            }
        }
    }
    
    func refreshPartner() {
        if let cp = ProgData.curPartner {
            if canChange { setPartner(cp) }
        } else {
            selectPartner()
        }
    }
    
    @objc
    func partnerSelect(_ sender:AnyObject) {
        if canChange { selectPartner() }
    }
    
    func onNewPartner(ctrl:PartnerSelectController, partner: Partner?) {
        ctrl.dismiss(animated: true, completion:nil)
        
        ProgData.curPartner?.basket.removeHandler(basketHandlerId)
        
        ProgData.curPartner = partner
        basketHandlerId = partner?.basket.addHandler(handler: self) ?? ""
        setPartner(partner)
    }
    
    func selectPartner() {
        var pc : PartnerSelectController
        if #available(iOS 13.0, *) {
            pc = self.storyboard?.instantiateViewController(identifier: "parentSelect") as! PartnerSelectController
        } else {
            pc = self.storyboard?.instantiateViewController(withIdentifier: "parentSelect") as! PartnerSelectController
        }
        pc.handler = onNewPartner(ctrl:partner:)
        present(pc, animated: true, completion: nil)
    }
    
    func calcTextSize() -> (width:CGFloat, height:CGFloat) {
        let maxWidth: CGFloat
        if #available(iOS 11.0, *) {
            maxWidth = self.view.safeAreaLayoutGuide.layoutFrame.width - BaseController.TEXT_HEIGHT * 2
        } else {
            maxWidth = self.view.frame.width - BaseController.TEXT_HEIGHT * 2
        }

        var textWidth : CGFloat
        var textHeight : CGFloat
        let textL = orgButton.titleLabel?.text!
        var fontL = orgButton.titleLabel?.font
        if fontL!.pointSize != BaseController.ORG_FONT_SIZE {
            fontL = UIFont.systemFont(ofSize: BaseController.ORG_FONT_SIZE)
            orgButton.titleLabel?.font = fontL
        }
        let sizeT = textL!.size(withAttributes:[.font: fontL!])
        if(!showFullPartnerText) {
            orgButton.titleLabel?.lineBreakMode = sizeT.width < maxWidth ? .byClipping : .byTruncatingTail
            textWidth = min(sizeT.width, maxWidth)
            textHeight = BaseController.TEXT_HEIGHT
        } else {
            if sizeT.width < maxWidth {
                orgButton.titleLabel?.lineBreakMode = .byClipping
                textWidth = sizeT.width
                textHeight = BaseController.TEXT_HEIGHT
            } else {
                orgButton.titleLabel?.lineBreakMode = .byWordWrapping
                let options: NSStringDrawingOptions = [.usesLineFragmentOrigin,.usesFontLeading]
                let sizeL = CGSize(width: maxWidth, height: maxWidth)
                let boundT = textL?.boundingRect(with: sizeL, options: options, attributes: [.font: fontL!], context: nil)
                textWidth = boundT!.width
                textHeight = boundT!.height
            }
        }
        
        return (textWidth, textHeight)
    }
    
    func setPartner(_ partner: Partner?) {
        let orgName = partner?.text ?? ""
                
        let title = NSAttributedString(string: orgName, attributes:
                        [NSAttributedString.Key.underlineStyle:1,
                         NSAttributedString.Key.foregroundColor : UIColor.blue,
                        ])
        
        orgButton.setAttributedTitle(title, for: .normal)
        
        let (textWidth, textHeight) = calcTextSize()
        widthOrg?.constant = textWidth
        heightView?.constant = textHeight

        imgv?.isHidden = false
        orgLinesButton.isHidden = false

        let expImg = UIImage(named: self.showFullPartnerText ? "expand_less" : "expand_more")?.withRenderingMode(.alwaysTemplate)
        orgLinesButton.setImage(expImg, for: .normal)

        updateSafeAreaInsets()
        view.setNeedsLayout()
        
        if let basket = partner?.basket {
            			changed(sender: basket)
        }
    }
    
    @objc
    func changeOrgLines(_ sender: AnyObject) {
        if let prtn = ProgData.curPartner {
            showFullPartnerText = !showFullPartnerText
            setPartner(prtn)
        }
    }
    
    func createOrgView() {
        let image = UIImage(named: "place_img")?.withRenderingMode(.alwaysTemplate)
        imgv = UIImageView(image: image)
        imgv?.tintColor = ProgData.MAIN_COLOR

        view.addSubview(orgView)
        
        orgView.addSubview(orgButton)
        orgButton.addTarget(self, action: #selector(BaseController.partnerSelect(_:)), for: .touchUpInside)
                
        orgView.addSubview(imgv!)
        
        imgv?.isHidden = true
        orgLinesButton.isHidden = true
        
        orgView.addSubview(orgLinesButton)
        orgLinesButton.addTarget(self, action: #selector(BaseController.changeOrgLines(_:)), for: .touchUpInside)

        orgButton.translatesAutoresizingMaskIntoConstraints = false
        orgButton.topAnchor.constraint(equalTo: orgView.topAnchor, constant: 0).isActive = true
        orgButton.centerXAnchor.constraint(equalTo: orgView.centerXAnchor, constant: 0).isActive = true
        widthOrg = orgButton.widthAnchor.constraint(equalToConstant: BaseController.TEXT_HEIGHT)
        widthOrg?.isActive = true
        orgButton.heightAnchor.constraint(equalTo: orgView.heightAnchor).isActive = true
        
        let expImg = UIImage(named: self.showFullPartnerText ? "expand_less" : "expand_more")?.withRenderingMode(.alwaysTemplate)
            //.withTintColor(ProgData.MAIN_COLOR)
        orgLinesButton.setImage(expImg, for: .normal)
        orgLinesButton.tintColor = ProgData.MAIN_COLOR
        orgLinesButton.imageView?.contentMode = .scaleToFill
        orgLinesButton.isOpaque = true
        orgLinesButton.translatesAutoresizingMaskIntoConstraints = false
        orgLinesButton.centerYAnchor.constraint(equalTo: orgButton.centerYAnchor).isActive = true
        orgLinesButton.widthAnchor.constraint(equalToConstant: BaseController.IMG_HEIGHT).isActive = true
        orgLinesButton.heightAnchor.constraint(equalToConstant: BaseController.IMG_HEIGHT).isActive = true
        orgLinesButton.leadingAnchor.constraint(equalTo: orgButton.trailingAnchor, constant: 0).isActive = true
        
        if let imgv_ = imgv {
            imgv_.contentMode = UIView.ContentMode.scaleAspectFill//.scaleAspectFit
            imgv_.translatesAutoresizingMaskIntoConstraints = false
            imgv_.centerYAnchor.constraint(equalTo:orgButton.centerYAnchor, constant: 0).isActive = true
            imgv_.heightAnchor.constraint(equalToConstant: BaseController.IMG_HEIGHT).isActive = true
            imgv_.trailingAnchor.constraint(equalTo: orgButton.leadingAnchor, constant: 0).isActive = true
            imgv_.widthAnchor.constraint(equalToConstant: BaseController.IMG_HEIGHT).isActive = true
        }
        
        orgView.translatesAutoresizingMaskIntoConstraints = false
        orgView.topAnchor.constraint(equalTo: navigationBar.bottomAnchor).isActive = true
        if #available(iOS 11.0, *) {
            let margins = view.safeAreaLayoutGuide
            orgView.leadingAnchor.constraint(equalTo: margins.leadingAnchor).isActive = true
            orgView.trailingAnchor.constraint(equalTo: margins.trailingAnchor).isActive = true
        } else {
            orgView.leadingAnchor.constraint(equalTo: view.leadingAnchor).isActive = true
            orgView.trailingAnchor.constraint(equalTo: view.trailingAnchor).isActive = true
        }
        heightView = orgView.heightAnchor.constraint(equalToConstant: BaseController.TEXT_HEIGHT)
        heightView?.isActive = true
    
        let title = NSAttributedString(string: "<Выберите контрагента>", attributes:
                        [NSAttributedString.Key.underlineStyle:1,
                         NSAttributedString.Key.foregroundColor : UIColor.blue,
                        ])
        
        orgButton.setAttributedTitle(title, for: .normal)

        let (textWidth, textHeight) = calcTextSize()
        widthOrg?.constant = textWidth
        heightView?.constant = textHeight
    }
    
    func setup() {
        self.navigationBar.barTintColor = ProgData.MAIN_COLOR
        self.navigationBar.titleTextAttributes = [NSAttributedString.Key.foregroundColor: UIColor.white]
        self.navigationBar.barStyle = .black
        createOrgView()
    }
    
//    @IBInspectable var testString : String = "test"
    
    func updateSafeAreaInsets() {
        if self.children.count == 0 {
            return
        }

        var inset = UIEdgeInsets()
        inset.top += (heightView?.constant)!

        let child = self.children[children.count - 1]
        child.additionalSafeAreaInsets = inset
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        updateSafeAreaInsets()
    }
}

