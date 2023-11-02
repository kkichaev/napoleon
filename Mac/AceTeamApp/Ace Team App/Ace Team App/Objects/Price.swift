//
//  Price.swift
//  AceTeamApp
//
//  Created by Denis Mosyagin on 15.03.2023.
//

import Foundation
import NapoleonCore

@objc class Folder : GRSObject {
    @objc var id = 0
    @objc var name = ""
    @objc var level = 0
    
    @objc var fid = ""

    override class func getName() -> String! { "Folder" }
    override class func getTableName() -> String! { "Folder" }
    override class func getKeyFields() -> String! { "id" }
}

@objc class PriceInfo : GRSObject {
    @objc var id = ""
    @objc var folderID = 0

    @objc var name = ""

    @objc var weight = 0.0
    @objc var color = 0
    @objc var hidden = 0

    override class func getName() -> String! { "Price" }
    override class func getTableName() -> String! { "Price" }
    override class func getKeyFields() -> String! { "id" }
}

@objc class Price : PriceInfo {
    
    @objc class Unit : SelectableObject {
        @objc var inpack = 1.0
        @objc var code = ""
    }

//    @objc var srchName = ""

    @objc var units:[Unit] = []

    func getUnit(id: String) -> Unit? {
        for u in units {
            if u.id == id {
                return u
            }
        }
        return nil
    }
    
    static func get(id:String) -> Price {
        let r = GRSDBReader()
        let p = Price()
        p.id = id
        r?.read(byKey: p)
        return p
    }

    override class func listTypes() -> [AnyHashable : Any]! {
        return [
            "units": Unit.self,
        ]
    }
}

@objc class PriceType : SelectableObject {
    static let CFG_KEY = "ВидЦены"
    
    override class func getName() -> String! { "PriceTypes" }
    override class func getTableName() -> String! { "PriceType" }
}

@objc class PriceCost : GRSObject, CachableObject {
    @objc var id:String = ""
    @objc var idItem:String = ""
    
    @objc var cost = 0.0

    override class func getName() -> String! { "PriceCost" }
    override class func getTableName() -> String! { "PriceCost" }
    override class func getKeyFields() -> String! { "id,idItem" }
}

@objc class Store : SelectableObject {
    static let CFG_KEY = "Склады"
    
    override class func getName() -> String! { "Stores" }
    override class func getTableName() -> String! { "Store" }
}

@objc class StoreQty : GRSObject, CachableObject {
    @objc var id:String = ""
    @objc var idItem:String = ""
    
    @objc var qty = 0.0

    override class func getName() -> String! { "StoreQty" }
    override class func getTableName() -> String! { "StoreQty" }
    override class func getKeyFields() -> String! { "id,idItem" }
}

class PriceModel : Identifiable {
    var price : PriceInfo
    
    var stock : Double? = nil
    var cost : Double? = nil
    var qty : Double = 0
    var unit: Price.Unit? = nil
    var qtyPack:Double = 0

    init(price: PriceInfo, holder:ItemsHolder? = nil) {
        self.price = price
        holder?.updatePriceModel(item: self)
    }
    
    var name : String { price.name }
    var id : String { price.id }
    
    var scale : Double { unit?.inpack ?? 1 }
    
    static func load(_ fmap:[Int:FolderRow]) {
        let pr = GRSDBReader()!
        let filter = Config.hideOldOrgs ? "hidden=0" : ""
        for pi in pr.fetch(PriceInfo.self, where: filter) {
            if let p = pi as? PriceInfo {
                fmap[p.folderID]?.items.append(PriceModel(price: p))
            }
        }
    }
}

class FolderRow : Comparable, ObservableObject, Identifiable {
    static func < (lhs: FolderRow, rhs: FolderRow) -> Bool {
        lhs.folder.name < rhs.folder.name
    }
    
    static func == (lhs: FolderRow, rhs: FolderRow) -> Bool {
        lhs.folder.id == rhs.folder.id
    }
    
    var folder : Folder
    
    var folders = [FolderRow]()
    @Published var items = [PriceModel]()
    
    init(_ folder: Folder) {
        self.folder = folder
    }
    
    func filter(_ filter: (PriceModel)->Bool ) -> FolderRow? {
        let ret = FolderRow(folder)
        for pi in items {
            if filter(pi) {
                ret.items.append(pi)
            }
        }
        
        for fi in folders {
            if let ff = fi.filter(filter) {
                ret.folders.append(ff)
            }
        }
        
        return ret.items.isEmpty && ret.folders.isEmpty ? nil : ret
    }
    
    func sorting(_ level: Int) {
        folder.level = level
        for f in folders {
            f.sorting(level + 1)
        }
        folders.sort()
    }
    
    func removeEmpty() {
        for f in folders {
            f.removeEmpty()
        }
        folders = folders.filter{ $0.haveChilds }
    }
    
    var haveLeaf : Bool { !items.isEmpty }
    
    var haveChilds : Bool {
        for f in folders {
            if f.haveChilds {
                return true
            }
        }
        
        return !items.isEmpty
    }
    
    var level : Int { folder.level }
    var name: String { folder.name }
    
    static func makeTree() -> [FolderRow] {
        let fr = GRSDBReader()
        var res = [FolderRow]()
        
        var fmap = [Int:FolderRow]()
        var current = [FolderRow]()
        for fi in fr!.fetch(Folder.self, where: "", order: "id") {
            let f = FolderRow(fi as! Folder)
            
            fmap[f.folder.id] = f

            let cf = current.last
            if var curF = cf {
                if f.level > curF.level {
                    curF.folders.append(f)
                } else {
                    let _ = current.popLast()
                    while !current.isEmpty {
                        curF = current.last!
                        if f.level > curF.level {
                            break
                        }
                        let _ = current.popLast()!
                    }
                    if current.isEmpty {
                        res.append(f)
                    } else {
                        current.last?.folders.append(f)
                    }
                }
                current.append(f)
            } else {
                res.append(f)
                current.append(f)
            }
        }
        
        PriceModel.load(fmap)
        
        res = res.filter{ $0.haveChilds }
        res.forEach{ $0.sorting(0) }
        return res
    }
}
