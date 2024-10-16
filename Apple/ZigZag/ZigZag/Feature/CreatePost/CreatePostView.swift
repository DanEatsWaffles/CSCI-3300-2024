//
//  CreatePostView.swift
//  ZigZag
//
//  Created by Daniel W on 10/14/24.
//

import SwiftUI
import MapKit

struct CreatePostView: View {
    @StateObject var viewModel = CreatePostViewModel()
    
    @State private var postText: String = ""
    @State private var timerSet = false
    @State private var location: String = "Current Location"
    
    var body: some View {
        VStack(spacing: 32) {
            // Map background with "Create Post" title
            MapView(region: $viewModel.region, overlayText: "Create Post")
                .frame(height: 200) // Adjust height as needed
            
            // Post Input Field
            VStack {
                HStack() {
                    Image(systemName: "location.circle")
                        .foregroundColor(.gray)
                    Text(location)
                        .foregroundColor(.gray)
                    Spacer()
                }
                
                TextEditor(text: $postText)
                    .frame(minHeight: 20, maxHeight: 200) // Set min/max height for the expanding text area
                    .padding()
                    .background(
                        RoundedRectangle(cornerRadius: 15)
                            .strokeBorder(Color.gray.opacity(0.4), lineWidth: 1)
                    )
                    .overlay(
                        Text(postText.isEmpty ? "What's happening in your area?" : "")
                            .foregroundColor(.gray)
                            .padding(.leading, 8),
                        alignment: .topLeading
                    )
                
                // Set Timer
                HStack {
                    Image(systemName: "clock")
                        .foregroundColor(.gray)
                    Text("Set Timer")
                        .foregroundColor(.gray)
                    Spacer()
                }
                
                Spacer()
                
                // POST Button
                Button(action: {
                    // Action for creating post
                }) {
                    Text("POST")
                        .font(.title2)
                        .bold()
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(10)
                        .padding(.horizontal)
                }
                
            }
            .padding(.horizontal)
            .navigationBarBackButtonHidden(true)
        }
        .ignoresSafeArea(edges: .top)
        
    }
}

struct MapViewPlaceholder: View {
    var body: some View {
        ZStack {
            Color.gray.opacity(0.5) // Simulate map background
            Text("Map Placeholder")
                .foregroundColor(.white)
        }
    }
}

#Preview {
    CreatePostView()
}
