//
//  ImageCache.swift
//  ServikoMobile
//
//  Created by Александра on 20.10.2020.
//

import Foundation
import UIKit

public class ImageCache {
    
    public static let publicCache = ImageCache()
    var placeholderImage = UIImage(named: "coming_soon")!
    private let cachedImages = NSCache<NSURL, UIImage>()
    private var loadingResponses = [NSURL: [(ImageItem, UIImage?) -> Swift.Void]]()
    
    public final func image(url: NSURL) -> UIImage? {
        return cachedImages.object(forKey: url)
    }
    /// - Tag: cache
    // Returns the cached image if available, otherwise asynchronously loads and caches it.
    final func load(url: NSURL, item: ImageItem, completion: @escaping (ImageItem, UIImage?) -> Swift.Void) {
        // Check for a cached image.
        if let cachedImage = image(url: url) {
            DispatchQueue.main.async {
                completion(item, cachedImage)
            }
            return
        }
        // In case there are more than one requestor for the image, we append their completion block.
        if loadingResponses[url] != nil {
            loadingResponses[url]?.append(completion)
            return
        } else {
            loadingResponses[url] = [completion]
        }
        
        var request = URLRequest(url: url as URL)
        request.httpMethod = "GET"
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            guard let responseData = data, let image = UIImage(data: responseData),
                let blocks = self.loadingResponses[url], error == nil else {
                self.loadingResponses.removeValue(forKey:url)
                DispatchQueue.main.async {
                    completion(item, nil)
                }
                return
            }
            // Cache the image.
            self.loadingResponses.removeValue(forKey:url)
            self.cachedImages.setObject(image, forKey: url, cost: responseData.count)
            // Iterate over each requestor for the image and pass it back.
            for block in blocks {
                DispatchQueue.main.async {
                    block(item, image)
                }
                return
            }
        }.resume()        
    }
        
}

class ImageItem: Hashable {
    static let BASE_URL = "http://1c.serviko.ru:8081/images/"
    
    var image: UIImage!
    let url: URL!
    let identifier = UUID()
    
    func hash(into hasher: inout Hasher) {
        hasher.combine(identifier)
    }
    static func == (lhs: ImageItem, rhs: ImageItem) -> Bool {
        return lhs.identifier == rhs.identifier
    }
    
    init(image: UIImage, code: String) {
        self.image = image
        let str = ImageItem.BASE_URL + code + ".jpg"
        self.url = URL(string: str)
    }
}
