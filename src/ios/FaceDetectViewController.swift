//
//  FaceDetectViewController.swift
//  EsCareDemo
//
//  Created by kyengshin on 2022/10/17.
//

import Foundation
import UIKit
import UFaceDetectorLite
import UFaceFRClient

class FaceDetectViewController: BaseViewController, UFaceDetectorDelegate {
    enum DetectType: Int {
        case regist = 0
        case auth = 1
        case bulk = 2
    }

    var detector:UFaceDetector!

    var type: DetectType = .auth

    @IBOutlet weak var viewCamera: UIView!

    override func viewDidLoad() {
        super.viewDidLoad()
        self.detector = UFaceDetector()
        self.detector.delegate = self
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        self.detector.initDetector(licenseKey: "4F5A46527631008159DB9F7FCCB6BC3D6170E79B3F1DF734BDC103035E9264D38EFF4ECB88BE5DD2509E27199D03A183B4A8D6EC0A32E9AE", modelDirectory: Bundle.main.resourcePath!)
    }

    func ufaceDetectorSetCameraSessionComplete() {
        self.detector.previewLayer.frame = CGRect(origin: .zero, size: self.viewCamera.frame.size)
        self.viewCamera.layer.addSublayer(self.detector.previewLayer)
        self.detector.startDetect()
    }

    func ufaceDetector(detector: UFaceDetector, error: UFaceError) {
        print(String(format: "%@(Code : %@)", error.errorDescription, error.errorCode))
        if error.errorCode == "72001" {
            //If face is out of screen
            self.detector.startDetect()
        }else {
            self.openAlertView(msg: String(format: "%@(Code : %@)", error.errorDescription, error.errorCode)) {
                if let delegate = self.delegate {
                    delegate.returnDetectResult(self.detectResult)
                }
                self.dismiss(animated: true)
            }
        }
    }

    func ufaceDetector(detector: UFaceDetector, result: UFaceResult) {
        if result.isFake {
            self.openAlertView(msg: "Detection Fake") {
                if let delegate = self.delegate {
                    delegate.returnDetectResult(self.detectResult)
                }
                self.dismiss(animated: true)
            }
            return
        }
        if let imgData = result.fullImage.jpegData(compressionQuality: 0.9) {
            let depthData = result.depthImage?.jpegData(compressionQuality: 0.9)
            switch self.type {
            case .regist :
                let data = UFaceFRClient.getRegistrationData(uuid: self.uuidString, custNo: self.custNo, chnlDV: "ESC", image: imgData, depth: depthData, userAgreement: true)
                self.callAPI(params: ["data":data, "type":"REGIST"]) { result in
                    DispatchQueue.main.async {
                        self.detectResult = result.cust_no ?? ""
                        self.openAlertView(msg: "Success in registeration face.") {
                            if let delegate = self.delegate {
                                delegate.returnDetectResult(self.detectResult)
                            }
                            self.dismiss(animated: true)
                        }
                    }
                } failure: { error in
                    print(error)
                    DispatchQueue.main.async {
                        self.openAlertView(msg: String(format: "%@(code : %@)", error?.errorDescription ?? "Unknown", error?.errorCode ?? "99999")) {
                            if let delegate = self.delegate {
                                delegate.returnDetectResult(self.detectResult)
                            }
                            self.dismiss(animated: true)
                        }
                    }
                }
                break
            case .auth :
                let data = UFaceFRClient.getVerifyData(uuid: self.uuidString, custNo: self.custNo, chnlDV: "ESC", image: imgData, depth: depthData, userAgreement: true)
                self.callAPI(params: ["data":data, "type":"VERIFY"]) { result in
                    DispatchQueue.main.async {
                        self.detectResult = result.cust_no ?? ""
                        self.openAlertView(msg: String(format: "%@\nSuccess in verification.", result.cust_no ?? "")) {
                            if let delegate = self.delegate {
                                delegate.returnDetectResult(self.detectResult)
                            }
                            self.dismiss(animated: true)
                        }
                    }
                } failure: { error in
                    DispatchQueue.main.async {
                        self.openAlertView(msg: String(format: "%@(code : %@)", error?.errorDescription ?? "Unknown", error?.errorCode ?? "99999")) {
                            if let delegate = self.delegate {
                                delegate.returnDetectResult(self.detectResult)
                            }
                            self.dismiss(animated: true)
                        }
                    }
                }
                break
            case .bulk :
                let data = UFaceFRClient.getBulkVerifyData(uuid: self.uuidString, chnlDV: "ESC", image: imgData, depth: nil, userAgreement: true)
                self.callAPI(params: ["data":data, "type":"BULKVERIFY"]) { result in
                    DispatchQueue.main.async {
                        DispatchQueue.main.async {
                            self.openAlertView(msg: String(format: "%@\nSuccess in verification.", result.cust_no ?? "")) {
                                if let delegate = self.delegate {
                                    delegate.returnDetectResult(self.detectResult)
                                }
                                self.dismiss(animated: true)
                            }
                        }
                    }
                } failure: { error in
                    DispatchQueue.main.async {
                        self.openAlertView(msg: String(format: "%@(code : %@)", error?.errorDescription ?? "Unknown", error?.errorCode ?? "99999")) {
                            if let delegate = self.delegate {
                                delegate.returnDetectResult(self.detectResult)
                            }
                            self.dismiss(animated: true)
                        }
                    }
                }
                break
            }
        }
    }

    @IBAction func onClose(_ sender: UIButton) {
        if let delegate = self.delegate {
            delegate.returnDetectResult(self.detectResult)
        }
        self.dismiss(animated: true)
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        if self.detector != nil {
            self.detector.stopDetector()
        }
    }

    public static func createViewController(type: DetectType) -> FaceDetectViewController {
        let sb = UIStoryboard(name: "Main", bundle: nil)
        let vc = sb.instantiateViewController(withIdentifier: "FaceDetectViewController") as! FaceDetectViewController
        vc.type = type
        vc.modalPresentationStyle = .fullScreen
        return vc
    }
}
