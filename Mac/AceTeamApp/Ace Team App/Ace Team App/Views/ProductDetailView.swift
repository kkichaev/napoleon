//
//  ProductDetailView.swift
//  AceTeamApp
//
//  Created by Denis Mosyagin on 19.03.2023.
//

import SwiftUI

class ProductDetailModel : ObservableObject {
    let numFmt =  {
        let nf = NumberFormatter()
        nf.minimumFractionDigits = 0
        nf.maximumFractionDigits = 3
        return nf
    }()

    var data: PriceModel
    var itemsHolder: ItemsHolder?
    
    @Published var value:String
    
    var units:[Price.Unit]
    
    init(data: PriceModel, itemsHolder:ItemsHolder?) {
        self.data = data
        self.itemsHolder = itemsHolder

        let p = Price.get(id: data.id)
        self.units = p.units
        
//        itemsHolder?.updatePriceModel(item: data)
        
        var qty = data.qty == 0 ? ProductDetailModel.startQty() : data.qty
        if data.qtyPack != 0 {
            qty = data.qtyPack
        }

        value = numFmt.string(from: NSNumber(value: qty))?
            .replacingOccurrences(of: ".", with: ",") ?? "0"
    }    
        
    var qty : Double {
        Double(value.replacingOccurrences(of:",", with:".")) ?? 0
    }
    
    func update() {
        data.qtyPack = qty
        data.qty = qty * (data.unit?.inpack ?? 1)
        itemsHolder?.update(from: data)
    }
    
    static func startQty() -> Double { 1.0 }
}

struct ProductDetailView: View {
    @Environment(\.dismiss) var dismiss
    
    var sumFormatter = {
       let nf = NumberFormatter()
        nf.minimumFractionDigits = 2
        nf.maximumFractionDigits = 2
        return nf
    }()
    
    @StateObject var model : ProductDetailModel
    @State var sum = "0.00"
    
    init(data: PriceModel, itemsHolder: ItemsHolder? = nil) {
        self._model = StateObject(
            wrappedValue:ProductDetailModel(data: data, itemsHolder: itemsHolder)
        )
    }
    
    var body: some View {
        VStack {
            Text(model.data.name)
                .font(.title)
            Text(model.value)
                .font(.title)
                .padding()
            Text(sum)
                .font(.title)
                .padding()
//            ElementPicker(values: model.units, selected: $model.data.unit, prompt: "Select unit")
            ElementPicker(values: model.units, selected: Binding<Price.Unit?> (
                get: { model.data.unit },
                set: {
                    model.data.unit = $0
                    countSum()
                }), prompt: "Select unit")
            NumPad(value: Binding<String> (
                get: { model.value },
                set: {
                    model.value = $0
                    countSum()
                }
            ))
        }
        .toolbar{
            ToolbarItem(placement: .navigationBarTrailing) {
                Button("Done") {
                    model.update()
                    dismiss()
                }
            }
        }
        .onAppear{
            countSum()
        }
    }
    
    func countSum() {
        let nsum = model.qty * (model.data.cost ?? 0) * model.data.scale
        sum = sumFormatter.string(from: NSNumber(value: nsum)) ?? "0.00"
    }
}

struct NumPad : View {
    @Binding var value:String
    @State var starting = true
    
    struct Key : View {
        let text:String
        let action: ()->Void
        
        var body : some View {
            Button {
                action()
            } label: {
                Text(text)
                    .font(.system(.largeTitle))
                    .frame(width: 60, height: 54)
                    .foregroundColor(Color.white)
                    .padding(.bottom, 2)
            }
            .background(ProgColor.add_button)
            .cornerRadius(8)
            .shadow(color: Color.black.opacity(0.3), radius: 3, x: 3, y: 3)
        }
    }
    
    var body : some View {
        VStack {
            HStack {
                Key(text:"1") { add("1") }
                Key(text:"2") { add("2") }
                Key(text:"3") { add("3") }
                Key(text:"\u{232b}") { removeLast() }
            }
            HStack {
                Key(text:"4") { add("4") }
                Key(text:"5") { add("5") }
                Key(text:"6") { add("6") }
                Key(text:",") { add(",") }
            }
            HStack {
                Key(text:"7") { add("7") }
                Key(text:"8") { add("8") }
                Key(text:"9") { add("9") }
                Key(text:"0") { add("0") }
            }
        }.onAppear {
            starting = true
        }
    }
    
    func add(_ sym:String) {
        if sym == "," {
            if !value.contains(",") {
                value += ","
            }
            return
        }
        if starting {
            value = sym
            starting = false
        } else {
            value += sym
        }
    }
    
    func removeLast() {
        if starting {
            starting = false
            value = ""
        } else {
            let _ = value.popLast()
        }
    }
}

struct TestView : View {
    @State var value = ""

    var body: some View {
        VStack {
            Text(value).font(.title)
            NumPad(value: $value)
        }
    }
}

//struct ProductDetailView_Previews: PreviewProvider {
//    static var previews: some View {
////        TestView()
//        let price = Price()
//        price.name = "Test name"
//        return ProductDetailView(item:price)
//    }
//}
