//
//  WSExchange.swift
//  swifttest
//
//  Created by ert on 07.10.2020.
//

import Foundation

class Instantinable {
    required init() {
    }
}

enum ObjectValue {
    case string(String)
    case array([String])
    indirect case object([String], [Dictionary<String, ObjectValue>], String)
}

protocol SimpleArray {
	var count: Int { get }

    func getElementType() -> Any.Type
	func get(_ index: Int) -> Any
    mutating func addElement(element: Any)
}

extension Array : SimpleArray {
    func getElementType() -> Any.Type {
        return Element.self
    }
    
	func get(_ index: Int) -> Any {
		return self[index]
	}

    mutating func addElement(element: Any) {
        append(element as! Element)
    }
}

protocol WSElement {
    var element: String { get set }
    var order: [String] { get set }
    
    func setValue(_ newValue: String) -> Bool
    func getValue() -> ObjectValue

    func getNativeValue() -> Any
	func addElement(element: Any)
}

@propertyWrapper
class WSDLElement<Value> : WSElement {
    var element : String
    var arrayElement : String = ""
    var order : [String]
    var wrappedValue : Value
    
    init(wrappedValue value: Value, name element:String) {
        self.element = element
        self.wrappedValue = value
        self.order = [String]()
    }
    
    init(wrappedValue value: Value, name element:String, order:[String]) {
        self.element = element
        self.wrappedValue = value
        self.order = order
    }
    
    init(wrappedValue value: Value, name element:String, order:[String], arrayElement:String) {
        self.element = element
        self.wrappedValue = value
        self.order = order
        self.arrayElement = arrayElement
    }

    func getNativeValue() -> Any {
        wrappedValue
    }

	func addElement(element: Any) {
        if wrappedValue is SimpleArray {
            var array = wrappedValue as! SimpleArray
            array.addElement(element: element)
			wrappedValue = array as! Value
		}
	}
    
    func setValue(_ newValue: String) -> Bool {
        
        if wrappedValue is SimpleArray {
            var array = wrappedValue as! SimpleArray
            if let val = WSParser.convertValue(strValue: newValue, type: array.getElementType()) {
                array.addElement(element: val)
                wrappedValue = array as! Value
            }
		} else {
           if let val = WSParser.convertValue(strValue: newValue, type: type(of: wrappedValue)) {
               wrappedValue = val as! Value
           }
		}
        
        return true
    }

	func valueToString(_ value: Any) -> String? {
        switch(value) {
        case let iv as Int:
            return String(iv)
        case let sv as String:
            return sv
        case let fv as Float:
            return String(describing: fv)
        case let bv as Bool:
            return bv ? "true" : "false"
        case let dv as Date:
            let df = DateFormatter()
            df.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
            return df.string(from: dv)
        default: return nil
		}
	}
    
    func getValue() -> ObjectValue {
        if let val = valueToString(wrappedValue) {
			return ObjectValue.string(val)
		}
        switch(wrappedValue) {
        case let av as SimpleArray:
			if av.count == 0 {
				return ObjectValue.array([String]())
			}
			var el = av.get(0)
			if let val = valueToString(el) {
				var arr = [val]
                var i = 1
                while i < arr.count{
                    if let val = valueToString(av.get(i)) {
						arr.append(val)
					} else {
						arr.append("")
					}
                    i += 1
				}
				return ObjectValue.array(arr)
			}
			if order.count == 0 {
                print("No order for element " + element)
                return ObjectValue.string("")
			}
			var arr = Array<[String:ObjectValue]>()
			var index = 0
			while true {
                arr.append(ObjToValue(object: el as AnyObject))
				index += 1
				if index >= av.count {
					break
				}

				el = av.get(index)
			}
			return ObjectValue.object(order, arr, arrayElement)
		default: break
        }
        return ObjectValue.string("")
    }
}

func ObjToValue(object: AnyObject) -> Dictionary<String, ObjectValue> {
    var ret = Dictionary<String, ObjectValue>()
        
    var mirror : Mirror? = Mirror(reflecting: object)
    repeat {
        for ch in mirror!.children {
            if ch.value is WSElement {
                let ws = ch.value as! WSElement
                ret[ws.element] = ws.getValue()
            }
        }
        mirror = mirror!.superclassMirror
    } while mirror != nil
        
    return ret
}

class WSParser : NSObject, XMLParserDelegate {
    var objects = [(elements:[String:WSElement], endTag:String)]()
    var element : WSElement? = nil
    var stringValue = ""
    
    static func convertValue(strValue:String, type:Any.Type) -> Any? {
        if type == String.self {
            return strValue
        }
        if type == Int.self {
            return Int(strValue)
        }
        if type == Float.self {
            return Float(strValue)
        }
        if type == Bool.self {
            return (strValue.caseInsensitiveCompare("true") == .orderedSame)
        }
        if type == Date.self {
            let df = DateFormatter()
            df.dateFormat = strValue.contains("T") ? "yyyy-MM-dd'T'HH:mm:ss" : "yyyy-MM-dd"
            return df.date(from: strValue)
        }
        return nil
    }
    
    func setObject(object: AnyObject, data: Data) {
        objects.removeAll()
        objects.append((readMembers(object), ""))
        element = nil
        
        let xp = XMLParser(data: data)
        xp.delegate = self
        xp.shouldProcessNamespaces = true
        xp.parse()
    }

	func readMembers(_ object: AnyObject) -> [String:WSElement] {
        var ret = [String:WSElement]()
		var mirror : Mirror? = Mirror(reflecting: object)
        repeat {
            for ch in mirror!.children {
                if ch.value is WSElement {
                    let ws = ch.value as! WSElement
					ret[ws.element] = ws
                }
            }
            mirror = mirror!.superclassMirror
        } while mirror != nil

		return ret
	}
    
    func findElement(_ name:String) -> WSElement? {
        
        if let (elDic, _) = objects.last {
			return elDic[name]
        }
        
        return nil
    }
    
    func parser(_ parser: XMLParser, didStartElement elementName: String, namespaceURI: String?, qualifiedName qName: String?, attributes attributeDict: [String : String] = [:]) {
        element = findElement(elementName)
        if let nv = element?.getNativeValue(), let ar = nv as? SimpleArray {
            if let objInst = ar.getElementType() as? Instantinable.Type {
                let obj = objInst.init()
                element?.addElement(element: obj)
                objects.append((readMembers(obj), elementName))
                element = nil
            }
        }
        if element != nil {
            stringValue = ""
        }
    }
    
    func parser(_ parser: XMLParser, didEndElement elementName: String, namespaceURI: String?, qualifiedName qName: String?) {
        if let res = element?.setValue(stringValue), res == true {
            element = nil
        }
        if let (_, endEl) = objects.last, endEl == elementName {
            objects.remove(at: objects.count - 1)
        }
    }
    
    func parser(_ parser: XMLParser, foundCharacters string: String) {
        if element != nil {
            stringValue += string
        }
    }
}

class WSExchange {
    
    static let UrlStr = "https://1c.serviko.ru/DISTR_ORIGINAL/ws/ASFMobileTrade/wsdl/"
//    static let AuthToken = "Basic R1JTb2Z0OlF3MTIzMTIz"
    static let AuthToken = "Basic R1JTT0ZUOnRZMm5pNG1h"
    
    init() {
        //        self.handler = {(res, data) in }
    }
    
    func reqCode(data: ReqCodeParam, handler: @escaping (ReqCodeResult) -> Void) {
        exchange(param: data, method: "ПолучитьКодПодтверждения",
                 order: ["Контакт", "ИдентификаторПриложения", "ИдентификаторУстройства"], handler: handler)
    }

    func acceptCode(data: AcceptCodeParam, handler: @escaping (AcceptCodeResult) -> Void) {
        exchange(param: data, method: "ПодтвердитьКодПодтверждения",
                 order: ["Контакт", "ИдентификаторПриложения", "ИдентификаторУстройства"], handler: handler)
    }

    func reqPrice(data: ReqPriceParam, handler: @escaping (ReqPriceResult) -> Void) {
        exchange(param: data, method: "ПолучитьНоменклатуру_v2",
                 order: ["ИдентификаторКонтрагента", "ИдентификаторыНоменклатур", "ИдентификаторПриложения", "ИдентификаторУстройства"],
                 handler: handler)
    }

    func sendOrder(data: SendBasketParam, handler: @escaping (SendBasketResult) -> Void) {
        exchange(param: data, method: "ОтправитьЗаказы",
                 order: ["ИдентификаторыЗаказов", "ИдентификаторКонтрагента", "ИдентификаторПриложения", "ИдентификаторУстройства"],
                 handler: handler)
    }

    func reqOrders(data: ReqOrdersParam, handler: @escaping (ReqOrdersResult) -> Void) {
        exchange(param: data, method: "ПолучитьЗаказы",
                 order: ["ИдентификаторКонтрагента", "ИдентификаторПриложения"], handler: handler)
    }
    
    //    var handler : (Bool, AnyObject) -> Void
    
    func exchange<Res : ErrResult>(param: AnyObject, method: String, order: [String], handler: @escaping (Res) -> Void) {
        //        self.handler = handler
        let action = "ASFMobileTrade#ASFMobileTrade:" + method
        let body = makeBody(method: method, param: param, order:order)
        
        let url = URL(string: WSExchange.UrlStr)
        
        var request = URLRequest(url: url!)
        request.setValue(WSExchange.AuthToken, forHTTPHeaderField: "Authorization")
        request.setValue(action, forHTTPHeaderField: "SOAPAction")
        request.httpMethod = "POST"
        request.httpBody = body
        
        let task = URLSession.shared.dataTask(with: request) { data, response, error in
            let result = Res()
            guard let data = data, let response = response as? HTTPURLResponse, error == nil else {
                if let err = error {
                    result.error = "\(err)"
                } else {
                    result.error = "Неизвестная ошибка"
                }
                result.result = false
                handler(result)
                return
            }
            
            guard (200 ... 299) ~= response.statusCode else {
                let responseString = String(data: data, encoding: .utf8)
                
                result.result = false
                result.error = responseString ?? ""
                
                print("statusCode is \(response.statusCode)")
                print("response = \(result.error)")
                
                handler(result)
                return
            }
            
            let wsParser = WSParser()
            wsParser.setObject(object: result, data: data)
            handler(result)
        }
        
        task.resume()
    }
    
    func memberValueToString(ns: String, member: String, value : ObjectValue) -> String {
        switch value {
        case .string(let strVal):
            return "<\(ns):\(member)>\(strVal)</\(ns):\(member)>"
        case .array(let arrVal):
            var str = ""
            if arrVal.count > 0 {
                for strVal in arrVal {
                    str += "<\(ns):\(member)>\(strVal)</\(ns):\(member)>"
                }
            } else {
                str = "<\(ns):\(member) />"
            }
            
            return str
        case .object(let orders, let objVal, let element):
            var str = ""
            for elDict in objVal {
                str += "<\(ns):\(member)>"
                if !element.isEmpty { str += "<\(ns):\(element)>" }
                for ordStr in orders {
                    if let value = elDict[ordStr] {
                        str += memberValueToString(ns: ns, member: ordStr, value: value)
                    } else {
                        print("No member \(ordStr)")
                    }
                }
                if !element.isEmpty { str += "</\(ns):\(element)>" }
                str += "</\(ns):\(member)>"
            }
            return str
        }
    }
    
    func objectToXml(method: String, param: AnyObject, ns: String, order: [String]) -> String {
        var str = "<\(ns):\(method)>"
        let objData = ObjToValue(object: param)
        
        for ordStr in order {
            if let value = objData[ordStr] {
                str += memberValueToString(ns: ns, member: ordStr, value: value)
            } else {
                print("No Value for member \(ordStr)")
            }
        }
        
        str += "</\(ns):\(method)>"
        return str
    }
    
    func makeBody(method: String, param: AnyObject, order: [String]) -> Data {
        let xmlHead = """
<?xml version="1.0" encoding="utf-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:asf="ASFMobileTrade">
  <soapenv:Header/>
  <soapenv:Body>

"""
        
        let xmlTail = """

  </soapenv:Body>
</soapenv:Envelope>
"""
        let str = xmlHead + objectToXml(method:method, param:param, ns:"asf", order:order) + xmlTail
//        print(str)
        
        return str.data(using: .utf8)!
    }
    
}
