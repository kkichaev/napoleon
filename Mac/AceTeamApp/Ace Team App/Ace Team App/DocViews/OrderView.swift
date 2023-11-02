//
//  OrderView.swift
//  AceTeamApp
//
//  Created by Denis Mosyagin on 17.03.2023.
//

import SwiftUI

class OrderViewModel : ObservableObject {
    var data : OrgDocumentsModel
    var doc : Order
    
    init(data: OrgDocumentsModel, doc: Order) {
        self.data = data
        self.doc = doc
    }
}

struct OrderView: View {
    @Environment(\.dismiss) private var dismiss
    @ObservedObject var model : OrderViewModel
        
    @State var openStock = false
    
    let cache = ProgDataCache.shared
    
    init(model:OrgDocumentsModel, doc:Order) {
        self.model = OrderViewModel(data: model, doc:doc)
    }
    
    var propsView : some View {
        ZStack {
            VStack(alignment: .leading) {
                Spacer().frame(height: 40)
                Divider()
                VStack {
                    Toggle("Cash", isOn: Binding<Bool>(
                        get: { (model.doc.params & CreatableDocument.CASH_FL) != 0},
                        set: {
                            if $0 {
                                model.doc.params |= CreatableDocument.CASH_FL
                            } else {
                                model.doc.params &= (~CreatableDocument.CASH_FL)
                            }
                        }
                    ))
                    Divider()
                    ElementIDPicker(values: cache.firms, selectedId: $model.doc.firmCode, prompt: "Select firm")
                    ElementIDPicker(values: cache.stores, selectedId: $model.doc.whCode, prompt: "Select store")
                    ElementIDPicker(values: cache.priceTypes, selectedId: $model.doc.prcType, prompt: "Select price type")
                } .listStyle(.plain)
                    .pickerStyle(.segmented)
                Text("Remark")
                TextEditor(text: $model.doc.remark)
                    .border(Color(uiColor: UIColor.systemGray))
                    .cornerRadius(4)
                    .frame(height: 120)
                    .colorMultiply(Color(uiColor: UIColor.tertiarySystemGroupedBackground))
                Spacer()
            }
            .padding()
            VStack {
                HStack{
                    DatePickCtrl(
                        date: $model.doc.date,
                        prefix: LocalizedStringKey("Delivery date"),
                        range: Date()...
                    )
                    .background(Color(UIColor.systemBackground))
                }
                Spacer()
            }
        }
    }
    
    var addButon : some View {
        ZStack {
            NavigationLink(
                destination: StockView(itemsHolder: model.doc),
                isActive: $openStock
            ) {
                EmptyView()
            }
            AddButton {
                openStock = true
            }
        }
    }
    
    var itemsView : some View {
        ZStack {
            List(model.doc.items) { el in
                NavigationLink{
                    model.doc.editItem(item: PriceModel(price: el.price, holder:model.doc))
                } label: {
                    VStack {
                        Text(el.price.name)
                        Text(String(el.qtyPack))
                        Text(el.packName())
                    }
                }
            }
            addButon
        }
    }
    
    var body: some View {
        VStack {
            Text(model.data.org.name).padding(.vertical, 0)
            if !(model.data.org.address.isEmpty) {
                Text(model.data.org.address)
            }
            TabView {
                propsView
                    .tabItem{ Text("Order Properties") }
                itemsView
                    .tabItem{ Text("Goods") }
            }
            .tabViewStyle(.automatic)
            .indexViewStyle(.page(backgroundDisplayMode: .always))
        }
        .navigationTitle("Order")
        .toolbar{
            ToolbarItem(placement: .navigationBarTrailing) {
                Button("Done") {
                    model.doc.write()
                    dismiss()
                }
            }
        }
    }
}

//struct OrderView_Previews: PreviewProvider {
//    static func makeView() -> some View {
//        let org = Org()
//        org.name = "Test org"
//        org.address = "Address"
//        let ov = OrderView(org:org, doc:Order())
//        return ov.body
//    }
//    static var previews: some View {
//        makeView()
//    }
//}
