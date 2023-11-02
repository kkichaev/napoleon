//
//  PriceTree.swift
//  ServikoMobile
//
//  Created by ert on 13.10.2020.
//

import Foundation

class TreeElement: Comparable {

    static func < (lhs: TreeElement, rhs: TreeElement) -> Bool {
        return lhs.item.name < rhs.item.name
    }
    
    static func == (lhs: TreeElement, rhs: TreeElement) -> Bool {
        return lhs.item.name == rhs.item.name
    }
    
    var item : Price

    init(src: Price) { self.item = src }
}

class Folder : TreeElement {
    var childs = [Folder]()
    var items = [TreeElement]()
    
    init() {
        super.init(src: Price())
    }
    
    override init(src: Price) {
        super.init(src:src)
    }

    var count : Int {  get { return childs.count + items.count } }

    func setSrc(src: Price) { self.item = src; }

    func add(folder: Folder) { childs.append(folder) }
    
    func add(price: Price) {
        let te = TreeElement(src:price);
        items.append(te);
    }

    subscript(index: Int) -> TreeElement {
        return index < childs.count ? childs[index] : items[index - childs.count]
    }
    

    func sort() {
        childs.sort()
        items.sort()
    }

    func findItem( dest:inout [TreeElement], pattern:String) {
        for el in items {
            if el.item.name.range(of: pattern, options: [.caseInsensitive, .regularExpression]) != nil {
                dest.append(el)
            }
        }
        
        for f in childs {
            f.findItem(dest:&dest, pattern:pattern)
        }
    }
   
    func findFolder(id:String) -> Folder? {
        if item.id == id {
            return self
        }
        for f in childs {
            if let fndF = f.findFolder(id:id) {
                return fndF
            }
        }
        
        return nil
    }

    func findItem(id:String) -> Price? {
        for i in items {
            if i.item.id == id {
                return i.item
            }
        }
        
        for f in childs {
            if let pi = f.findItem(id: id) {
                return pi
            }
        }
        
        return nil
    }
}

class PriceTree {
    let root = Folder()

    func find(id: String) -> Price? {
        return root.findItem(id:id);
    }

    func findFolder(id: String) -> Folder? {
        return root.findFolder(id: id);
    }
    
    static func make(data : [Price]) -> PriceTree {
        let ret = PriceTree()
        var folders = [String:Folder]()
        folders[""] =  ret.getRoot()

        var mapPrice = [String:Price]()

        for i in data {
            mapPrice[i.id] = i

            var parent = folders[i.parent]
            if(parent == nil) {
                parent = Folder()
                folders[i.parent] = parent
            }

            if(i.isFolder) {
                var dest = folders[i.id]
                if(dest == nil) {
                    dest = Folder(src:i);
                    folders[i.id] = dest
                } else {
                    dest!.setSrc(src:i);
                }
                parent!.add(folder: dest!)
            } else {
                parent!.add(price:i)
            }
        }
        for (_, f) in folders  {
            f.sort();
        }
        return ret;
    }

    var count: Int { get { return root.count } }
    
    func getRoot() -> Folder { return root }
}
