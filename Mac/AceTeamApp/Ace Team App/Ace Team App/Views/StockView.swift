//
//  StockView.swift
//  AceTeamApp
//
//  Created by Denis Mosyagin on 13.03.2023.
//

import SwiftUI

class StockData : ObservableObject {
    var allFolders:[FolderRow]
    @Published var folders:[FolderRow]

    var searchMode = false
    
    init() {
        allFolders = FolderRow.makeTree()
        folders = allFolders
    }
    
    func searching(_ text: String) -> Void {
        searchMode = !text.isEmpty
 
        if text.isEmpty {
            folders = allFolders
            return
        }

        let restr = text.replacingOccurrences(of: " ", with: ".*") + ".*"
        let re = try! NSRegularExpression(pattern: restr, options: .caseInsensitive)
        
        folders.removeAll()

        for f in allFolders {
            if let tf = f.filter({ p in re.numberOfMatches(in: p.name, range: NSMakeRange(0, p.name.count)) > 0}) {
                folders.append(tf)
            }
        }
    }
}

struct StockView: View {
    
    @StateObject var data = StockData()
    
    var itemsHolder : ItemsHolder?
    
    var body: some View {
        if let _ = itemsHolder {
            view
        } else {
            NavigationView {
                view
            }
        }
    }
    
    var view : some View {
        VStack {
            HStack {
                Button {
                    
                } label: {
                    Image(systemName: "circle.hexagonpath")
                }
                SearchView(
                    searchHint: "Find goods",
                    searchHandler: data.searching)
            }
            ScrollView {
                ForEach(data.folders) { f in
                    LazyVStack(pinnedViews: .sectionHeaders) {
                        FolderView(folder: f, itemsHolder: itemsHolder, showPrice: data.searchMode)
                    }
                }
            }
        }
    }
}

struct FolderView : View {
    @ObservedObject var folder : FolderRow
    var itemsHolder : ItemsHolder?

    @State var showFolders = true
    @State var showPrice:Bool
    @State var goodsItems = [PriceModel]()
        
    var headerView : some View {
        HStack {
            Spacer()
                .frame(width: 8 * CGFloat(folder.level))
            Image(systemName: "folder")
                .foregroundColor(ProgColor.folder_fore)
            Text(folder.name)
                .font(.system(size:16, weight: .bold))
            Spacer()
            Image(systemName: (showFolders && showPrice) || (!showPrice && !folder.haveLeaf) ? "chevron.down" : "chevron.up")
        }
        .padding(.vertical, 8)
        .background(ProgColor.folder_back)
//        .background(Color.primary
//            .colorInvert()
//            .opacity(0.95)
//        )
        .onTapGesture {
            withAnimation {
                if !showPrice {
                    showPrice = true
                    showFolders = folder.haveLeaf
                } else {
                    showFolders.toggle()
                }
            }
        }
    }
    
    func updatePriceData() {
        goodsItems.removeAll(keepingCapacity: true)
        
        for p in folder.items {
            itemsHolder?.updatePriceModel(item: p)
            goodsItems.append(p)
        }
    }
    
    func drawGoods(item: PriceModel) -> some View {
        VStack(alignment: .leading) {
            Text(item.name)
                .multilineTextAlignment(.leading)
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.vertical, 4)
                .foregroundColor( item.qty > 0 ? .green : .black)
            Divider()
        }
    }
    
    var body: some View {
        Section(header: headerView) {
            if showFolders && showPrice {
                ForEach(goodsItems) { childP in
                    NavigationLink(
                        destination: itemsHolder?.editItem(item: childP)) {
                            drawGoods(item: childP)
                        }
                }
            }
        }
        .onAppear {
            if showPrice {
                updatePriceData()
            }
        }
        .onChange(of: showPrice) {
            if $0 {
                updatePriceData()
            }
        }
        if showFolders {
            ForEach(folder.folders) { childF in
                Self(folder: childF, itemsHolder: itemsHolder, showPrice: showPrice)
            }
        }
    }
}

//struct TI : Identifiable {
//    var id:String { name }
//    
//    var name:String
//    init(name: String) {
//        self.name = name
//    }
//}
//
//class TIModel : ObservableObject {
//    @Published var items = [TI(name:"1"), TI(name: "2")]
//}
//
//struct TestView1 : View {
//    @StateObject var model = TIModel()
//    
//    var body : some View {
//        VStack{
//            List(model.items, id: \.name) { el in
//                Text(el.name)
//            }
//            
//            Button("Test") {
//                model.items[0].name = "test"
//            }
//        }
//    }
//}
//
//struct StockView_Previews: PreviewProvider {
//    static var previews: some View {
//        TestView1()
//    }
//}
