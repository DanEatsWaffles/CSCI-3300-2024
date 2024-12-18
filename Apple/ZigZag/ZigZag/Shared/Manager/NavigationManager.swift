//
//  NavigationManager.swift
//  ZigZag
//
//  Created by Daniel W on 10/16/24.
//

import Foundation
import SwiftUI

enum ZigZagDestination: Hashable {
    case createPost
    case tagFilter(String)
    
}

class NavigationManager: ObservableObject {
    @Published var path = NavigationPath()

    // Method to navigate to different destinations
    func navigateTo(_ destination: ZigZagDestination) {
        path.append(destination)
    }

    // Method to pop back to the root (Home)
    func navigateBackToRoot() {
        path.removeLast(path.count)
    }

    // Method to pop back one step
    func navigateBack() {
        path.removeLast()
    }
}

enum AuthDestination: Hashable {
    case SignUp
    case Login
    case SMSVerification
    
}

class AuthNavigationManager: ObservableObject {
    @Published var path = NavigationPath()
    
    func navigateTo(_ destination: AuthDestination) {
        path.append(destination)
    }
    
    func navigateBackToRoot() {
        path.removeLast(path.count) // Clears all views in the stack to go back to the root
    }
    
    func navigateBack() {
        if !path.isEmpty {
            path.removeLast()
        }
    }
}
