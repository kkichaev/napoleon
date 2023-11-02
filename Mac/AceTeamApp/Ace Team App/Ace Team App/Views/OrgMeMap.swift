//
//  OrgMeMap.swift
//  AceTeamApp
//
//  Created by Denis Mosyagin on 29.03.2023.
//

import SwiftUI
import MapKit

class OrgAnnotation : NSObject, MKAnnotation {
    let title: String?
    let coordinate: CLLocationCoordinate2D
    
    init(org: Org) {
        title = org.name
        
        let loc = org.location
        
        coordinate = CLLocationCoordinate2D(latitude: loc?.coordinate.latitude ?? 0, longitude: loc?.coordinate.longitude ?? 0)
    }
    
}

class MapDelegate : NSObject, MKMapViewDelegate {
    func mapView(_ mapView: MKMapView, rendererFor overlay: MKOverlay) -> MKOverlayRenderer {
        switch overlay {
        case let overlay as MKCircle:
            let rndr = MKCircleRenderer(circle: overlay)
            rndr.strokeColor = UIColor(ProgColor.error)
            rndr.fillColor = UIColor(ProgColor.error).withAlphaComponent(0.2)
            rndr.lineWidth = 2
            return rndr
        default:
            return MKOverlayRenderer(overlay: overlay)
        }
    }
}

struct MapView : UIViewRepresentable {
    let org: Org
    let mv = MKMapView()
    let delegate = MapDelegate()
    
    func updateUIView(_ uiView: UIViewType, context: Context) {
    }
    
    func makeUIView(context: Context) -> some UIView {
        mv.showsUserLocation = true
        mv.isZoomEnabled = true
        mv.delegate = delegate
        
        let oa = OrgAnnotation(org: org)
        mv.addAnnotation(oa)
        if let myloc = LocationService.shared.currentLocation {
            let circle = MKCircle(center: CLLocationCoordinate2D(latitude: myloc.coordinate.latitude, longitude: myloc.coordinate.longitude), radius:
//                                    5000)
                                    Config.docRadius.radius ?? Config.DEFAULT_ORG_RADIUS)
            
            let dist = myloc.distance(from: CLLocation(latitude: oa.coordinate.latitude, longitude: oa.coordinate.longitude)) * 2.1
            let reg = MKCoordinateRegion(center: myloc.coordinate, latitudinalMeters: dist, longitudinalMeters: dist)
            mv.setRegion(reg, animated: true)
            mv.addOverlay(circle, level:.aboveLabels)
        }
        return mv
    }
}

struct OrgMeMap: View {
    var org: Org
    
    var body: some View {
        MapView(org: org)
    }
}

struct OrgMeMap_Previews: PreviewProvider {
    static var previews: some View {
        OrgMeMap(org:Org())
    }
}
