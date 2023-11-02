//
//  objects.swift
//  swifttest
//
//  Created by ert on 08.10.2020.
//

import Foundation

class ActionRule {
    @WSDLElement(name:"ИдентификаторТовара")
    var idPrice: String = ""

    @WSDLElement(name:"ИдентификаторУсловия")
    var idCondition: String = ""

    @WSDLElement(name:"Скидка")
    var discount: Float = 0

    @WSDLElement(name:"ЭтоПодарок")
    var isGift: Bool = false
}

class ActionCondition {
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

class OrderItem {
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

class Order {
    static let ORDER_STATE_ACTIVE = 1
    static let ORDER_STATE_DONE = 2
    static let ORDER_STATE_DEBT = 3

    var uid: String = UUID.randomUUID().toString().replace("-", "")

    @WSDLElement(name:"Представление")
    var text: String = ""

    @WSDLElement(name:"Номер")
    var number: String = ""

    @WSDLElement(name:"Дата")
    var orderDate: Date = new Date()

    @WSDLElement(name:"ДатаОтгрузки")
    var deliveryDate: Date = new Date()

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

    func inState(int state) -> Bool {
        if(state == ORDER_STATE_ACTIVE)
            return self.state ==  ORDER_STATE_ACTIVE
        if(state == ORDER_STATE_DONE)
            return self.state ==  ORDER_STATE_DONE
        if(state == ORDER_STATE_DEBT)
            return debtSum > 0

        return false
    }

    func sum() -> Float {
        ret: Float = 0
        for (OrderItem oi : items)
            ret += oi.sum
        return ret
    }

    func sumFact() -> Float {
        ret: Float = 0
        for (OrderItem oi : items)
            ret += oi.sumFact
        return ret
    }
}

class Price {
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

    //public ActionDef action
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
    
}

class OrderSendItem {
    @WSDLElement(name:"Номенклатура")
    var id: String = "";

    @WSDLElement(name:"Количество")
    var qty: Float = 0;

    @WSDLElement(name:"Цена")
    var cost: Float = 0;

//    public OrderSendItem(BasketItem oi) {
//        id = oi.item.id;
//        qty = oi.qty;
//        cost = oi.cost - oi.discount;
//    }
}

class OrderSend {

//    public OrderSend(Basket src) {
//        for(BasketItem oi : src.items) {
//            items.add(new OrderSendItem(oi));
//        }
//        remark = src.remark;
//        deliveryDate = src.dlvDate;
//        id = src.uid;
//    }

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

    @WSDLElement(name:"ИдентификаторыЗаказов", order:["Идентификатор","Дата","ДатаОтгрузки","Товары","Комментарий"])
    var orders = [OrderSend]();
}

class ErrResult {
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
