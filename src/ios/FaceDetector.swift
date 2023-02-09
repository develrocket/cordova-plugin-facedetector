import Cordova
//
//  FaceDetector.swift
//  EsCareDemo
//
//  Created by Walter on 1/24/23.
//

import Foundation


@objc(FaceDetector)
public class FaceDetector : CDVPlugin, MyDelegate {

    var callbackId: String?

    @objc
    func scan(_ command: CDVInvokedUrlCommand) {
//        let echo = command.argument(at: 0) as! String?
//
//        let pluginResult:CDVPluginResult
//
//        if echo != nil && echo!.count > 0 {
//            pluginResult = CDVPluginResult.init(status: CDVCommandStatus_OK, messageAs: echo!)
//        } else {
//            pluginResult = CDVPluginResult.init(status: CDVCommandStatus_ERROR)
//        }
//
//        self.commandDelegate.send(pluginResult, callbackId: command.callbackId)
        let type = command.argument(at: 0) as! String? ?? ""
        let deviceId = command.argument(at: 1) as! String? ?? ""
        let typeInt = Int(type) ?? 0

        self.callbackId = command.callbackId

        let detect = FaceDetectViewController.createViewController(type: FaceDetectViewController.DetectType(rawValue: typeInt) ?? .regist, custNo: deviceId)
        detect.delegate = self
        self.viewController.present(detect, animated: true)
    }

    func returnDetectResult(text: String) {
        let pluginResult:CDVPluginResult
        pluginResult = CDVPluginResult.init(status: CDVCommandStatus_OK, messageAs: text)
        self.commandDelegate.send(pluginResult, callbackId: self.callbackId)
    }

}
