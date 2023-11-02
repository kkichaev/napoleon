//
//  HomeView.swift
//  AceTeamApp
//
//  Created by Denis Mosyagin on 13.03.2023.
//

import SwiftUI

struct SearchView : View {
    var searchHandler : ((String)->Void)
    
    let searchHint:String
    
    init(searchHint:String, searchHandler: @escaping ((String) -> Void)) {
        self.searchHint = searchHint
        self.searchHandler = searchHandler
    }

    class SearchBarHandler : NSObject, UISearchResultsUpdating, UISearchBarDelegate {

        var searchText: ((String)->Void)
        
        init(searchText: @escaping (String) -> Void) {
            self.searchText = searchText
        }

        func updateSearchResults(for searchController: UISearchController) {
            if let text = searchController.searchBar.text {
                self.searchText(text)
            }
        }
    }
    
    struct SearchBarCtr : UIViewRepresentable {
        typealias UIViewType = UISearchBar
        
        let ctrl : UISearchController
        
        let handler: SearchBarHandler
        
        init(owner:SearchView, searchHint: String, searchText: @escaping ((String)->Void)) {
            ctrl = UISearchController()
            
            handler = SearchBarHandler(searchText:searchText)
            
            ctrl.searchBar.delegate = handler
            ctrl.searchResultsUpdater = handler
            
            
            ctrl.searchBar.placeholder = String.localizedStringWithFormat(
                NSLocalizedString(searchHint, comment: ""))
        }
        
        func makeUIView(context: Context) -> UISearchBar { return ctrl.searchBar }
        func updateUIView(_ uiView: UISearchBar, context: Context) {}
    }
    
    var body : some View {
        HStack {
            SearchBarCtr(owner: self,
                         searchHint: searchHint,
                         searchText: searchHandler)
                .padding(.horizontal, -8)
                .padding(.vertical, -10)
        }
    }
}

class HomeModel : ObservableObject {

    enum Mode : String, CaseIterable, Identifiable {
        case plain, route
        var id: Self { self }
    }
    
    @Published var scheduleDate = Date()
    @Published var mode : Mode = .plain
    
    func searchHandler(_ text: String) {
        self.searching?(text)
    }
    var searching: ((String)->Void)?
}

struct HomeView: View {
    
    @State var searchText = ""
    
    @StateObject private var model = HomeModel()

    var routeHeader : some View {
        DatePickCtrl(date:$model.scheduleDate, prefix: nil, range: nil)
    }
    
    var body: some View {
        if #available(iOS 16.0, *) {
            NavigationStack{
                VStack {
                    SearchView(
                        searchHint: "Find partner",
                        searchHandler: model.searchHandler
                    )
                    Picker("Mode", selection: $model.mode) {
                        Text("Clients").tag(HomeModel.Mode.plain)
                        Text("Route").tag(HomeModel.Mode.route)
                    }.pickerStyle(.segmented)
                    
                    if model.mode == .route {
                        routeHeader
                    }
                    OrgsView()
                        .environmentObject(model)
                }
                .padding(.horizontal, 16)
            }
        } else {
            NavigationView{
                VStack {
                    SearchView(
                        searchHint: "Find partner",
                        searchHandler: model.searchHandler
                    )
                    Picker("Mode", selection: $model.mode) {
                        Text("Clients").tag(HomeModel.Mode.plain)
                        Text("Route").tag(HomeModel.Mode.route)
                    }.pickerStyle(.segmented)
                    
                    if model.mode == .route {
                        routeHeader
                    }
                    OrgsView()
                        .environmentObject(model)
                }
                .padding(.horizontal, 16)
            }
        }
    }
}

struct HomeView_Previews: PreviewProvider {
    static var previews: some View {
        HomeView()
    }
}
