//
//  FaceDetector.swift
//  EsCareDemo
//
//  Created by Walter on 1/24/23.
//

import Foundation


@objc(FaceDetector)
public class FaceDetector : CDVPlugin, BaseViewController, MyDelegate {
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
        let type = command.argument(at: 0) as! String?
        let deviceId = command.argument(at: 1) as! String?

        self.custNo = deviceId

        let detect = FaceDetectViewController.createViewController(type: FaceDetectViewController.DetectType(rawValue: Int(type)) ?? .regist)
        detect.delegate = self
        self.present(detect, animated: true)
    }

    func returnDetectResult(text: String) {
        let pluginResult:CDVPluginResult
        pluginResult = CDVPluginResult.init(status: CDVCommandStatus_OK, messageAs: text)
        self.commandDelegate.send(pluginResult, callbackId: command.callbackId)
    }

}
