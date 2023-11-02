//
//  PartnerSelectController.swift
//  ServikoMobile
//
//  Created by Александра on 20.10.2020.
//

import Foundation
import UIKit


class PartnerSelectController : UIViewController, UITableViewDelegate, UITableViewDataSource {
    @IBOutlet weak var closeButton : UIButton?
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let partner = ProgData.partners[indexPath.row]
        if let h = handler {
            h(self, partner)
        }
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        ProgData.partners.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "partnerCell", for: indexPath)
        let partner = ProgData.partners[indexPath.row]

        if #available(iOS 14.0, *) {
            var content = cell.defaultContentConfiguration()
            
            content.text = partner.name
            content.secondaryText = partner.address

            cell.contentConfiguration = content
        } else {
            cell.textLabel?.text = partner.name
            cell.detailTextLabel?.text = partner.address
        }
        return cell
    }
    
    @IBOutlet weak var table : UITableView?
    
    var handler : ((PartnerSelectController,Partner?) -> Void)? = nil
    
    @objc
    func closing() {
        if let h = handler {
            h(self, ProgData.curPartner)
        }
    }
    
    override func viewDidLoad() {
        table?.rowHeight = UITableView.automaticDimension
        table?.estimatedRowHeight = 45.0

        table?.delegate = self
        table?.dataSource = self
        table?.reloadData()
        
        closeButton?.addTarget(self, action: #selector(closing), for: .touchUpInside)
        self.view.layer.cornerRadius = 5
    }
}


