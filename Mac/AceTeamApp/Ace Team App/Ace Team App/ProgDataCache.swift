//
//  ProgDataCache.swift
//  AceTeamApp
//
//  Created by Denis Mosyagin on 22.03.2023.
//

import Combine
import NapoleonCore

protocol CachableObject {
    var id : String { get }
    var idItem : String { get }
}

class PriceCache {
    private var items = [String:PriceInfo]()
    private var reader = GRSDBReader()!

    func get(item:PriceItem) -> PriceInfo {
        if let pi = items[item.id] {
            return pi
        }
        
        let p = PriceInfo()
        p.id = item.id
        if !reader.read(byKey: p) {
            p.name = "<" + p.id + ">"
        }
        items[item.id] = p
        return p
    }
    
    func refresh() {
        items.removeAll()
        reader.close()
    }
}

class ItemList<T:CachableObject> {
    private var cache : [String:T]? = nil
    private var cacheId:String = ""
    
    func refresh() { cache = nil }
    
    func getItem(typeId:String, elId:String) -> T? {

        if cache == nil || cacheId != typeId {
            cacheId = typeId
            
            cache = [String:T]()
            let r = GRSDBReader()!
            
            for el in r.fetch(T.self as? AnyClass, where: "id='\(typeId)'")! {
                if let tel = el as? T {
                    cache![tel.idItem] = tel
                }
            }
        }
        
        return cache![elId]
    }
}

class ProgDataCache {
    private static var refreshHandler : AnyCancellable?
    
    static let shared = ProgDataCache()
    
    private let goods = PriceCache()
    private let cost = ItemList<PriceCost>()
    private let stock = ItemList<StoreQty>()
    
    var firms = [Firm]()
    var priceTypes:[PriceType] = [PriceType()]
    var stores:[Store] = [Store()]
    
    func getItem(item:PriceItem) -> PriceInfo { goods.get(item: item) }
    func getCost(type:String, id:String) -> PriceCost? { cost.getItem(typeId: type, elId: id) }
    func getQty(type:String, id:String) -> StoreQty? { stock.getItem(typeId: type, elId: id)}
    
    private init() {
        ProgDataCache.refreshHandler = Exchange.closePublisher.sink(receiveValue: { _ in ProgDataCache.shared.refresh() })
        read()
    }
    
    private func read() {
        firms = Firm.readValues(order: "name")
        priceTypes = PriceType.readValues(order: "name")
        stores = Store.readValues(order: "name")
    }
    
    private func refresh() {
        goods.refresh()
        cost.refresh()
        stock.refresh()
        
        read()
    }
}
