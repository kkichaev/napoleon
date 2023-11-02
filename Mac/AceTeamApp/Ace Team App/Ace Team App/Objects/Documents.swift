//
//  Documents.swift
//  AceTeamApp
//
//  Created by Denis Mosyagin on 17.03.2023.
//

import Foundation
import SwiftUI
import NapoleonCore
import Combine

protocol DocumentView : AnyObject {
    associatedtype Body : View
    
    func open(data:OrgDocumentsModel) -> Body
}

@objc class Document : GRSObject, Identifiable {
    @objc var id : String = ""
    @objc var date  = Date()
    @objc var remark = ""
    
    func docView() -> AnyView {
        let df = DateFormatter()
        df.dateStyle = .medium
        return AnyView(Text(df.string(from: date)))
    }
}

@objc class CreatableDocument : Document {
    static private var docUpdated = PassthroughSubject<CreatableDocument, Never>()
    static var docUpdatePublisher = docUpdated.eraseToAnyPublisher()
    
    static let CANT_EDIT_FL = 0x100
    static let CAN_SEND_FL = 0x200

    static let EXPORTED_FL = 0x1
    static let CASH_FL = 0x2
    static let PRINTED_FL = 0x10
    static let PROCEEDED_FL = 0x20000
    
    
    @objc var created = Date()
    @objc var timeZone: Int = (-NSTimeZone.local.secondsFromGMT() / 60)
    @objc var params = 0
    
    @objc var number = ""
    @objc var podRemark = ""
    @objc var latitude = 0.0
    @objc var longitude = 0.0
    @objc var userid = ""
    
    @objc var stltime = 0
    
    let writer = GRSDBWriter()!
    
    func sum() -> Double { 0 }
    
    var empty : Bool { get { false } }
    
    func initFrom(data:OrgDocumentsModel) {
        self.id = data.org.id
        
        if let loc = LocationService.shared.currentLocation {
            self.latitude = loc.coordinate.latitude
            self.longitude = loc.coordinate.longitude
        }
    }
    
    func write() {
        CreatableDocument.docUpdated.send(self)
//        if writer.write(self) {
//            CreatableDocument.docUpdated.send(self)
//        }
    }

    var canEdit : Bool {
        get { (params & CreatableDocument.CANT_EDIT_FL) == 0 }
        set {
            if newValue {
                params &= (~CreatableDocument.CANT_EDIT_FL)
            } else {
                params |= CreatableDocument.CANT_EDIT_FL
            }
        }
    }
    
    var sended : Bool {
        get { (params & CreatableDocument.EXPORTED_FL) != 0 }
        set {
            if newValue {
               params |= CreatableDocument.EXPORTED_FL
            } else {
                params &= (~CreatableDocument.EXPORTED_FL)
            }
        }
    }
    
    var canSend : Bool {
        get { (params & CreatableDocument.CAN_SEND_FL) != 0 }
        set {
            if newValue {
                params |= CreatableDocument.CAN_SEND_FL
            } else {
                params &= (~CreatableDocument.CAN_SEND_FL)
            }
        }
    }
}

protocol ItemsHolder {
    func updatePriceModel(item: PriceModel)

    func update(from: PriceModel) -> Void
    
    func editItem(item: PriceModel) -> AnyView
}

extension ItemsHolder {
    func editItem(item: PriceModel) -> AnyView {
        return AnyView(ProductDetailView(data: item, itemsHolder: self))
    }
}

@objc class PriceItem : GRSObject, Identifiable {
    @objc var id = ""
    
    var price : PriceInfo { get { ProgDataCache.shared.getItem(item:self) } }
}

@objc class Order : CreatableDocument, ItemsHolder, DocumentView, ObservableObject {
        
    func updatePriceModel(item: PriceModel) {
        if let pc = ProgDataCache.shared.getCost(type: prcType, id: item.id) {
            item.cost = pc.cost
        } else {
            item.cost = 0
        }
        
        if let pc = ProgDataCache.shared.getQty(type: whCode, id: item.id) {
            item.stock = pc.qty
        } else {
            item.stock = 0
        }
        
        let p = Price.get(id: item.id)
        var uc : String? = nil
        
        if let ii = items.first(where: { $0.id == item.id }) {
            item.qty = ii.qty
            item.qtyPack = ii.qtyPack
            uc = ii.unit
        } else {
            item.qty = 0
        }
        if uc == nil && p.units.count > 0 {
            uc = p.units[0].id
        }
        item.unit = p.getUnit(id: uc ?? "")
    }
    
    func update(from data: PriceModel) {
        var changeQty = 0.0
        
        let ii = items.first(where: { $0.id == data.id }) ?? {
            let ti = Item()
            items.append(ti)
            return ti
        }()
        
        if data.qty == 0.0 {
            changeQty = -ii.qty
            items.removeAll(where: {$0 === ii})
        } else {
            changeQty -= (data.qty - ii.qty)
            ii.update(from: data)
        }
        write()
        objectWillChange.send()
    }
    
    @objc class Item : PriceItem {

        @objc var cost = 0.0
        @objc var qty = 0.0
        @objc var unit = ""
        @objc var flags = 0
        @objc var qtyPack = 0.0
        
        func update(from: PriceModel) {
            id = from.id
            cost = from.cost ?? 0
            unit = from.unit?.id ?? ""
            self.qty = from.qty
            self.qtyPack = from.qtyPack
        }
        
        func packName() -> String {
            let p = Price.get(id: id)
            return p.getUnit(id: unit)?.name ?? ""
        }
    }
    
    @objc var items = [Item]()
    @objc var delay = 0
    @objc var firmCode = ""
    @objc var prcType = ""
    
    @objc var whCode = ""
    
    func open(data:OrgDocumentsModel) -> some View {
        OrderView(model: data, doc:self)
    }
    
    override func initFrom(data:OrgDocumentsModel) {
        super.initFrom(data: data)
        
        let od = ProgDataCache.shared
        
        if od.firms.count > 0 {
            self.firmCode = od.firms[0].id
        }
        if od.stores.count > 0 {
            self.whCode = od.stores[0].id
        }
        if od.priceTypes.count > 0 {
            self.prcType = od.priceTypes[0].id
        }
    }
    
    override func sum() -> Double {
        var ret = 0.0
        items.forEach{ i in ret += i.cost * i.qty }
        return ret
    }
}

class DocType : Identifiable {
    
    let name: String
    let title: String
    var createDoc: () -> CreatableDocument
    
    init(name: String, title: String, docCreator: @escaping () -> CreatableDocument) {
        self.name = name
        self.title = title
        self.createDoc = docCreator
    }

    static func documents() -> [DocType] {
        return [
            DocType(name:"Order", title:"Order") { Order() },
            
//            DocType(name:"Visit", title:"Visit", docView: VisitView()),
//            DocType(name:"OrgRemnants", title:"Stock check", makeView: {OrderView()}),
//            DocType(name:"Answer", title:"Inquire", makeView: {OrderView()}),
//            DocType(name:"Returns", title:"Return to supplyer", makeView: {OrderView()}),
        ]
    }
    
    static func getType(forName: String) -> DocType? {
        for dt in documents() {
            if dt.name == forName {
                return dt
            }
        }
        return nil
    }
}
