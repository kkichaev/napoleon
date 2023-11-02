//
//  ViewController.swift
//  swifttest
//
//  Created by ert on 05.10.2020.	//

import UIKit


class Param {
//    @WSElement("TestName") var testField : Int = 0
//    @WSElement("TestName1") var field2 : String = ""
}


class ViewController: UIViewController {

    @IBAction func testButton(_ sender: Any) {
    }

    func testHTML() {

let reqStr = """
<?xml version="1.0" encoding="utf-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:asf="ASFMobileTrade">
  <soapenv:Header/>
  <soapenv:Body>
    <asf:ПодтвердитьКодПодтверждения>
      <asf:Контакт>+79036384915</asf:Контакт>
      <asf:ИдентификаторПриложения>65D66369-C330-49fa-B49D-37360C122269</asf:ИдентификаторПриложения>
      <asf:ИдентификаторУстройства>0123456789012345</asf:ИдентификаторУстройства>
    </asf:ПодтвердитьКодПодтверждения>
  </soapenv:Body>
</soapenv:Envelope>
"""

let auth = "Basic R1JTb2Z0OlF3MTIzMTIz"
let action = "ASFMobileTrade#ASFMobileTrade:ПодтвердитьКодПодтверждения"
let url = URL(string: "https://1c.serviko.ru/DISTR_13/ws/ASFMobileTrade/")

var request = URLRequest(url: url!)
request.setValue(auth, forHTTPHeaderField: "Authorization")
request.setValue(action, forHTTPHeaderField: "SOAPAction")
request.httpMethod = "POST"
request.httpBody = reqStr.data(using: .utf8)


let task = URLSession.shared.dataTask(with: request) { data, response, error in
    guard let data = data, 
        let response = response as? HTTPURLResponse, 
        error == nil else {                                              // check for fundamental networking error
        print("error", error ?? "Unknown error")
        return
    }

    guard (200 ... 299) ~= response.statusCode else {                    // check for http errors
        print("statusCode should be 2xx, but is \(response.statusCode)")
        print("response = \(response)")
        return
    }

    let responseString = String(data: data, encoding: .utf8)
    print("responseString = \(responseString)")
}

task.resume()

    }
    	
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let res = AcceptCodeResult()
        let wp = WSParser()
        wp.setObject(object: res, data: orgXml.data(using: .utf8)!)
        
//        let param = AcceptCodeParam()
//        param.appId = "65D66369-C330-49fa-B49D-37360C122269"
//        param.deviceId = "0123456789012345"
//        param.phone = "+779036384915"
//
//        let res = AcceptCodeResult()
//        let method = "ПодтвердитьКодПодтверждения"
//        let order = ["Контакт","ИдентификаторПриложения","ИдентификаторУстройства"]
//
//
//        let exch = WSExchange()
//        exch.exchange(param: param, result: res, method: method, order: order) { (data) in
//            print(data)
//        }

//        testHTML()
        
//        let p = Param()
//        let mirror = Mirror(reflecting: p)
//        for ch in mirror.children {
//            if ch.value is WSElementProtocol {
//                let ws = ch.value as! WSElementProtocol
//                print("Member is ", ws.element)
//                print("Value ", ws.getValue())
//
//                ws.setValue("12")
//                print("New value ", ws.getValue())
//            }
////            print ("Name ", ch.label!)
////            print ("Value ", ch.value)
////            print("Type ", type(of:ch.value))
//        }
//
//        print (p.field2)
//        print (p.testField)
    }


}

