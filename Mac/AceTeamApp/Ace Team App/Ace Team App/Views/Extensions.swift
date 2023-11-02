//
//  DocTypeSelector.swift
//  AceTeamApp
//
//  Created by Denis Mosyagin on 17.03.2023.
//

import SwiftUI
import Introspect
import NapoleonCore

struct DatePickCtrl : View {
    @Binding var date: Date
    
    @State var dateInt = Date()
    
    let prefix:LocalizedStringKey?
    let range:PartialRangeFrom<Date>?
    
    @State var showDatePicker = false

    let dateFormatter : DateFormatter = {
        let df = DateFormatter()
        df.dateFormat = "EEE, dd MMM yyyy"
        
        return df
    }()

    var datePicker : some View {
        if let range = range {
            return DatePicker(
                ""
                , selection: $dateInt
                , in: range
                , displayedComponents: [.date])

        } else {
            return DatePicker(
                ""
                , selection: $dateInt
                , displayedComponents: [.date])
        }
    }
    
    var body: some View {
        VStack {
            if showDatePicker {
                datePicker
                    .datePickerStyle(.graphical)
                    .onChange(of: dateInt){
                        newDate in
                        date = dateInt
                        showDatePicker.toggle()
                    }
            } else {
                HStack {
                    if let prefix = prefix {
                        HStack {
                            Text(prefix)
                        }
                    }
                    Text("\(dateFormatter.string(from: dateInt))")
                }
                .onTapGesture { showDatePicker.toggle() }
                .padding()
            }
        }.onAppear{
            dateInt = date
        }
    }
}

struct ElementPicker<T:SelectableObject> : View {
    let values: [T]
    @Binding var selected:T?
    let prompt: LocalizedStringKey?
    @State var showChoices = false
    
    var body: some View {
        VStack(alignment: .leading) {
            if let prompt = prompt {
                Text(prompt)
            }
            Spacer().frame(height: 4)
            Button{
                showChoices.toggle()
            } label: {
                Text(selected?.name ?? "")
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .frame(minHeight: 48)
                    .font(.headline)
                    .padding(.horizontal, 8)
                    .background(Color(uiColor: UIColor.tertiarySystemGroupedBackground))
            }
            Divider()
        }
        .confirmationDialog("", isPresented: $showChoices) {
            ForEach(values) { v in
                Button {
                    selected = v
                } label: {
                    Text(v.name)
                }
            }
        }
    }
}

struct ElementIDPicker<T:SelectableObject> : View {

    let values: [T]
    @Binding var selectedId:String
    let prompt: LocalizedStringKey?
    @State var showChoices = false
    @State var name = ""
    
    var body: some View {
        VStack(alignment: .leading) {
            if let prompt = prompt {
                Text(prompt)
            }
            Spacer().frame(height: 4)
            Button{
                showChoices.toggle()
            } label: {
                Text(name)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .frame(minHeight: 48)
                    .font(.headline)
                    .padding(.horizontal, 8)
                    .background(Color(uiColor: UIColor.tertiarySystemGroupedBackground))
            }
            Divider()
        }
        .onAppear {
            name = values.valueOf(id: selectedId)?.name ?? ""
        }
        .confirmationDialog("", isPresented: $showChoices) {
            ForEach(values) { v in
                Button {
                    selectedId = v.id
                    name = v.name
                } label: {
                    Text(v.name)
                }
            }
        }
    }
}

struct AddButton : View {
    
    var handler: ()->Void
    
    var body: some View {
        VStack {
            Spacer()
            HStack {
                Spacer()
                Button(action: handler, label: {
                    Text("+")
                        .font(.system(.largeTitle))
                        .frame(width: 60, height: 54)
                        .foregroundColor(Color.white)
                        .padding(.bottom, 4)
                    
                })
                .background(ProgColor.add_button)
                .opacity(0.85)
                .cornerRadius(30)
                .padding()
                .shadow(color: Color.black.opacity(0.3), radius: 3, x: 3, y: 3)
                
            }
        }
    }
}

struct Some_Preview: PreviewProvider {
    @State static var date = Date()
    @State static var name = ""
    
    @State static var selectedId: String = ""
    
    static func make() -> some View {
        let values = [Firm(), Firm(), Firm()]
        values[0].name = "firm name long long"
        values[0].id = "1"
        
        values[1].name = "f2"
        values[1].id = "f2"

        values[2].name = "f3"
        values[2].id = "f3"
        return VStack {
            HStack {
                ElementIDPicker(values: values, selectedId: $selectedId, prompt: "Select frim")
                .pickerStyle(.automatic)
                .padding(.horizontal, 0)
                Spacer()
            }
        }
    }
    
    static var previews: some View {
//        make()
        DatePickCtrl(date: $date, prefix: "Test", range: Date()...)
    }
}

