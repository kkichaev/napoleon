//
//  objects.swift
//  swifttest
//
//  Created by ert on 08.10.2020.
//

import Foundation

protocol NotifyListChanged {
    func changed(data:[NotifyData], unreaded:Int)
}

class NotifyRoot : Codable {
    var notifis = [NotifyData] ()
}

class NotifyData : Codable {
    var title: String?
    var body: String?
    var date: Date? = Date()
    var readed = false
    
    static var delegate: NotifyListChanged?
    
    static func add(notify:NotifyData) {
        var data = read()
        data.append(notify)
        NotifyData.write(data)
        delegate?.changed(data: data, unreaded: NotifyData.countUnreaded(data: data))
    }
    
    // removed or mark readed
    static func updated(_ data:[NotifyData]) {
        NotifyData.write(data)
        delegate?.changed(data: data, unreaded: NotifyData.countUnreaded(data: data))
    }
        
    static func read() -> [NotifyData] {
        let defaults = UserDefaults.standard
        if let data = defaults.data(forKey: "notifies") {
            do {
                let js = JSONDecoder()
                let ret = try js.decode(NotifyRoot.self, from: data) as NotifyRoot
                return ret.notifis
            } catch {
            }
        }
        return [NotifyData]()
    }
    
    static func write(_ data:[NotifyData]) {
        let json = JSONEncoder()
        do {
            let nr = NotifyRoot()
            nr.notifis = data
            let data = try json.encode(nr)
            let defaults = UserDefaults.standard
            defaults.set(data, forKey: "notifies")
        } catch  {
        }
    }
    
    static func countUnreaded(data:[NotifyData]) -> Int {
        var count = 0
        for n in data {
            if !n.readed { count += 1 }
        }
        
        return count
    }
    
    static func countUnreaded() -> Int {
        return countUnreaded(data: NotifyData.read())
    }
}

class ActionRule : Instantinable {
    @WSDLElement(name:"ИдентификаторТовара")
    var idPrice: String = ""

    @WSDLElement(name:"ИдентификаторУсловия")
    var idCondition: String = ""

    @WSDLElement(name:"Скидка")
    var discount: Float = 0

    @WSDLElement(name:"ЭтоПодарок")
    var isGift: Bool = false
}

class ActionCondition : Instantinable {
    @WSDLElement(name:"ИдентификаторУсловияСрабатыванияСкидки")
    var id: String = ""

    @WSDLElement(name:"УсловиеСрабатыванияСкидкиПредставление")
    var name: String = ""

    @WSDLElement(name:"ИдентификаторРодителя")
    var parent: String = ""

    @WSDLElement(name:"УсловиеПредоставленияСкидки")
    var condition: String = ""

    @WSDLElement(name:"ЕдиницаИзмеренияУсловий")
    var unit: String = ""

    @WSDLElement(name:"ВидСравнения")
    var compareType: String = ""

    @WSDLElement(name:"ВидОбъединенияУсловий")
    var combineType: String = ""

    @WSDLElement(name:"Количество")
    var qty: Float = 0

    @WSDLElement(name:"ЭтоГруппа")
    var isFolder: Bool = false

    @WSDLElement(name:"ИдентификаторНоменклатуры")
    var items = [String]()
}

class OrderItem : Instantinable {
    @WSDLElement(name:"Номенклатура")
    var id: String = ""

    @WSDLElement(name:"НоменклатураПредставление")
    var name: String = ""

    @WSDLElement(name:"ЗаказаноКоличество")
    var qty: Float = 0

    @WSDLElement(name:"ЗаказаноСумма")
    var sum: Float = 0

    @WSDLElement(name:"ФактКоличество")
    var qtyFact: Float = 0

    @WSDLElement(name:"ФактСумма")
    var sumFact: Float = 0
}

class Order : Instantinable {
    static let ORDER_STATE_ACTIVE = 1
    static let ORDER_STATE_DONE = 2
    static let ORDER_STATE_DEBT = 3

    var uid: String = UUID().description.replacingOccurrences(of: "-", with: "")

    @WSDLElement(name:"Представление")
    var text: String = ""

    @WSDLElement(name:"Номер")
    var number: String = ""

    @WSDLElement(name:"Дата")
    var orderDate: Date = Date()

    @WSDLElement(name:"ДатаОтгрузки")
    var deliveryDate: Date = Date()

    @WSDLElement(name:"СтатусЗаказа")
    var status: String = ""

    @WSDLElement(name:"СостояниеЗаказа")
    var state: Int = 1

    @WSDLElement(name:"Задолженность")
    var debtSum: Float = 0

    @WSDLElement(name:"Комментарий")
    var remark: String = ""

    @WSDLElement(name:"Товары")
    var items = [OrderItem]()

    func inState(state: Int) -> Bool {
        if state == Order.ORDER_STATE_ACTIVE {
            return self.state ==  Order.ORDER_STATE_ACTIVE
        }
        if state == Order.ORDER_STATE_DONE {
            return self.state ==  Order.ORDER_STATE_DONE
        }
        if state == Order.ORDER_STATE_DEBT {
            return debtSum > 0
        }
        return false
    }

    func sum() -> Float {
        var ret: Float = 0
        for oi in items {
            ret += oi.sum
        }
        return ret
    }

    func sumFact() -> Float {
        var ret: Float = 0
        for oi in items {
            ret += oi.sumFact
        }
        return ret
    }
}

class Price : Instantinable {
    @WSDLElement(name:"Родитель")
    var parent: String = ""

    @WSDLElement(name:"ЭтоГруппа")
    var isFolder: Bool = false

    @WSDLElement(name:"Идентификатор")
    var id: String = ""

    @WSDLElement(name:"Код")
    var code: String = ""

    @WSDLElement(name:"Наименование")
    var name: String = ""

    @WSDLElement(name:"Остаток")
    var qty: Float = 0

    @WSDLElement(name:"Цена")
    var cost: Float = 0

    @WSDLElement(name:"Скидка")
    var discount: Float = 0

    @WSDLElement(name:"ЕстьОграниченияПоБюджету")
    var haveBudgetContrain: Bool = false

    @WSDLElement(name:"АлкогольнаяПродукция")
    var isAlcohol: Bool = false

    @WSDLElement(name:"Кратность")
    var quant: Int = 1

    @WSDLElement(name:"Коэффициент")
    var inPack: Int = 1

    var action: ActionDef?
}

class Partner : Instantinable {
    @WSDLElement(name:"Идентификатор")
    var id: String = ""

    @WSDLElement(name:"АдресДоставки")
    var address: String = ""

    @WSDLElement(name:"Наименование")
    var name: String = ""

    @WSDLElement(name:"ТорговляПоЛицензии")
    var haveLicense: Bool  = false
    
    var price = PriceTree()
    var orders = [Order]()
    var actions = [ActionDef]()

    var basket = Basket()
    
    var text : String { get { return "\(name) \(address)" }  }
    func isEmpty() -> Bool { return price.count == 0 }
    
    func changeBasket(item:Price, qty:Int, canRemove:Bool = true) {
        basket.canRemove = canRemove
        basket.changeQty(item:item, qty:qty)
    }
    
    func getQty(item:Price) -> Int {
        basket.getQty(item:item)
    }
}

class OrderSendItem {
    @WSDLElement(name:"Номенклатура")
    var id: String = "";

    @WSDLElement(name:"Количество")
    var qty: Float = 0;

    @WSDLElement(name:"Цена")
    var cost: Float = 0;
    
    init(_ oi: BasketItem) {
        id = oi.item.id
        qty = Float(oi.qty)
        cost = oi.cost - oi.discount
    }
}

class OrderSend {
    
    init(src:Basket) {
        id = src.uid
        deliveryDate = src.dlvDate!
        remark = src.remark
        
        for oi in src.items {
            items.append(OrderSendItem(oi))
        }
    }

    @WSDLElement(name:"Идентификатор")
    var id: String = "";

    @WSDLElement(name:"Дата")
    var date = Date();

    @WSDLElement(name:"ДатаОтгрузки")
    var deliveryDate = Date(timeIntervalSinceReferenceDate: 24 * 3600);

    @WSDLElement(name:"Комментарий")
    var remark: String = "";

    @WSDLElement(name:"Товары", order:["Номенклатура","Количество","Цена"])
    var items = [OrderSendItem]();
}

//
// exchange objects
//
class ReqCodeParam {
    @WSDLElement(name:"Контакт")
    var phone: String = ""
    
    @WSDLElement(name:"ИдентификаторПриложения")
    var appId: String = ""
    
    @WSDLElement(name:"ИдентификаторУстройства")
    var deviceId: String = ""
}

class AcceptCodeParam : ReqCodeParam {
    
}

class ReqOrdersParam {
    @WSDLElement(name:"ИдентификаторКонтрагента")
    var orgId: String = "";

    @WSDLElement(name:"ИдентификаторПриложения")
    var appId: String = "";
}

class ReqPriceParam {
    @WSDLElement(name:"ИдентификаторКонтрагента")
    var orgId: String = "";

    @WSDLElement(name:"ИдентификаторПриложения")
    var appId: String = "";

    @WSDLElement(name:"ИдентификаторУстройства")
    var deviceId: String = "";

    @WSDLElement(name:"ИдентификаторыНоменклатур")
    var ids = [String]();
}

class SendBasketParam {
    @WSDLElement(name:"ИдентификаторКонтрагента")
    var orgId: String = "";

    @WSDLElement(name:"ИдентификаторПриложения")
    var appId: String = "";

    @WSDLElement(name:"ИдентификаторУстройства")
    var deviceId: String = "";

    @WSDLElement(name:"ИдентификаторыЗаказов", order:["Идентификатор","Дата","ДатаОтгрузки","Товары","Комментарий"], arrayElement:"Значение")
    var orders = [OrderSend]();
}

class ErrResult {
    required init() {
    }
    
    @WSDLElement(name:"ОписаниеОшибки")
    var error: String = ""
    
    @WSDLElement(name:"Результат")
    var result: Bool = false
}

class ReqCodeResult : ErrResult {
    @WSDLElement(name:"КодПодтверждения")
    var code: Int = 0;
}

class AcceptCodeResult : ErrResult {
    @WSDLElement(name:"ДанныеКонтрагентов")
    var partners = [Partner]()
}

class ReqOrdersResult : ErrResult {

    @WSDLElement(name:"МассивЗаказов")
    var orders = [Order]();
}

class ReqPriceResult : ErrResult {
    @WSDLElement(name:"Товары")
    var price = [Price]();

    @WSDLElement(name:"ПравилаАкций")
    var actionRules = [ActionRule]();

    @WSDLElement(name:"УсловияАкций")
    var actionConditions = [ActionCondition]();
}

class SendBasketResult : ErrResult {
}
