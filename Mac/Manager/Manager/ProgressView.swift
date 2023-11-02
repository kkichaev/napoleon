//
//  ProgressView.swift
//  Manager
//
//  Created by Denis Mosyagin on 28.09.2021.
//

import SwiftUI

struct Arc: Shape {
    var startAngle: Angle
    var endAngle: Angle
    var clockwise: Bool
    var color = Color.blue
    var stokeWidth: CGFloat

    func path(in rect: CGRect) -> Path {
        var path = Path()
        let dim = min(rect.width, rect.height) - stokeWidth * 2
        if(startAngle == endAngle) {
            let crect = CGRect(origin: CGPoint(x: (rect.width - dim) / 2, y: (rect.height - dim) / 2), size: CGSize(width: dim, height: dim))
            path.addEllipse(in: crect)
        } else {
            path.addArc(center: CGPoint(x: rect.midX, y: rect.midY), radius: dim / 2 - stokeWidth, startAngle: startAngle, endAngle: endAngle, clockwise: clockwise)
        }
        return path
    }
}

struct WorkProgressView: View {
    static let size = CGFloat(50)
    var progress: Float
    var circleShape = true
    
    var body: some View {
        self.makeView()
    }
    
    func makeView() -> some View {
        var cprg = progress
        if cprg > 100 { cprg = 100 }
        
        let color = progress >= 67 ? Color.green : progress > 33 ? Color.yellow : Color.red
        
//        let startAng = Double(1.8 * progress + 90)
//        let endAng = Double(-1.8 * progress + 90)
        let startAng = Double(1.8 * progress + 180)
        let endAng = Double(-1.8 * progress + 180)
        let grayClr = 0.9
        let backColor = Color(red:grayClr,green: grayClr,blue: grayClr)
        let barHeight = WorkProgressView.size / 3
        let prgWidth = WorkProgressView.size * CGFloat(cprg) / 100
        let frameHgh = circleShape ? WorkProgressView.size : barHeight
        
        return ZStack {
            if( circleShape ) {
                Arc(startAngle: .degrees(endAng), endAngle: .degrees(startAng), clockwise: true, stokeWidth: 5)
                    .stroke(backColor, lineWidth: 5)
                if(progress > 0) {
                    Arc(startAngle: .degrees(startAng), endAngle: .degrees(endAng), clockwise: true, stokeWidth: 5)
                        .stroke(color, lineWidth: 5)
                }
            } else {
                HStack(spacing:0) {
                    Rectangle()
                        .fill(color)
                        .frame(width: prgWidth, height: barHeight, alignment: .center)
                    Rectangle()
                        .fill(backColor)
                        .frame(width: WorkProgressView.size - prgWidth, height: barHeight, alignment: .center)
                }
            }
            Text(String(format: "%.0f", cprg) + "%")
                .font(.system(size: 7))
        }.frame(width: WorkProgressView.size, height: frameHgh, alignment: .center)
    }
}

struct WorkProgressViewPreviews: PreviewProvider {
    static var previews: some View {
        VStack {
            WorkProgressView(progress: 85.0, circleShape: false)
            WorkProgressView(progress: 25.0)
            WorkProgressView(progress: 45.0)
        }
    }
}

