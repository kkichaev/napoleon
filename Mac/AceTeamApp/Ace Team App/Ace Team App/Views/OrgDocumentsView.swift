//
//  OrgDocumentsView.swift
//  AceTeamApp
//
//  Created by Denis Mosyagin on 17.03.2023.
//

import SwiftUI
import Introspect
import Combine

extension View {
    
    func docTypeSelector(isVisible:Binding<Bool>, org:Org?, createDocView: @escaping (DocType) -> some View) -> some View {
        return confirmationDialog("Select doc type", isPresented: isVisible) {
            ForEach(DocType.documents()) { dt in
                NavigationLink(LocalizedStringKey(dt.title)) {
                    createDocView(dt)
                }
            }
        }
    }
}

class OrgDocumentsModel : ObservableObject {
    let org: Org
    
    @Published var docs : [CreatableDocument]

    init(org: Org) {
        self.org = org
        docs = [CreatableDocument]()
    }
    
    func docUpdated(doc:CreatableDocument) {
        if docs.first(where: {$0 === doc }) == nil {
            docs.insert(doc, at: 0)
        }
    }
}

struct OrgDocumentsView: View {
    
    @ObservedObject var model : OrgDocumentsModel
    
    @State var uiTabarController: UITabBarController?
    @State var tabBarFrame: CGRect?
    @State var showDocTypes = false
    
    @State var noLocationAlert = false
    
    var docUpdateListener : AnyCancellable?
    
    init(org:Org) {
        let model = OrgDocumentsModel(org: org)
        self.model = model
        
        docUpdateListener = CreatableDocument.docUpdatePublisher.sink { doc in
            model.docUpdated(doc: doc)
        }
    }
    
    func openDocument(doc:Document) -> AnyView {
        if let d = doc as? any DocumentView {
            return AnyView(d.open(data: model))
        }
        return AnyView(EmptyView())
    }
    
    func createDoc(docType: DocType) -> AnyView {
        if LocationService.shared.allowCreate(org: model.org, docType: docType) {
            let doc = docType.createDoc()
            doc.initFrom(data:model)
            return openDocument(doc: doc)
        }
        return AnyView(OrgMeMap(org: model.org))
    }
    
    var content : some View {
        ZStack {
            VStack {
                Text(model.org.name)
                    .navigationTitle("Docs")
                List(model.docs, id: \.created) { doc in
                    NavigationLink {
                        openDocument(doc: doc)
                    } label:  {
                        doc.docView()
                    }
                }
            }
            .docTypeSelector(isVisible: $showDocTypes, org:model.org, createDocView: createDoc)
            
            if !showDocTypes {
                AddButton{
                    if Config.GPSTrackingWanted && LocationService.shared.currentLocation == nil {
                        noLocationAlert = true
                    } else {
                        showDocTypes = true
                    }
                }
            }
        }
        .alert("Need location alert", isPresented: $noLocationAlert) {
            Button("OK", role: .cancel) {}
        }
    }
    
    var body: some View {
        if #available(iOS 16.0, *) {
            content
                .toolbar(.hidden, for:.tabBar)
        } else {
            content
                .introspectTabBarController { (UITabBarController) in
                    uiTabarController = UITabBarController
                    self.tabBarFrame = uiTabarController?.view.frame
                    uiTabarController?.tabBar.isHidden = true
                    uiTabarController?.view.frame = CGRect(x:0, y:0, width:tabBarFrame!.width, height:tabBarFrame!.height+UITabBarController.tabBar.frame.height);
                }
                .onDisappear{
                    if let frame = tabBarFrame {
                        uiTabarController?.tabBar.isHidden = false
                        uiTabarController?.view.frame = frame
                    }
                }
        }
    }
}

//struct OrgDocumentsView_Previews: PreviewProvider {
//    static var previews: some View {
//        OrgDocumentsView(org: Org())
//    }
//}
