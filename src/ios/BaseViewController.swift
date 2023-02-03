//
//  BaseViewController.swift
//  EsCareDemo
//
//  Created by kyengshin on 2022/10/17.
//

import Foundation
import UIKit

protocol MyDelegate {
    func returnDetectResult(text: String)
}

class BaseViewController: UIViewController {
    var uuidString: String {
        get {
            if let str = UserDefaults.standard.value(forKey: "UFACE_UUID_STRING") as? String {
                return str
            }
            let uid = UUID().uuidString
            UserDefaults.standard.setValue(uid, forKey: "UFACE_UUID_STRING")
            return uid
        }
    }

    var custNo: String {
        get {
            if let str = UserDefaults.standard.value(forKey: "UFACE_CUST_NUMBER") as? String {
                return str
            }
            return ""
        }

        set(value) {
            UserDefaults.standard.setValue(value, forKey: "UFACE_CUST_NUMBER")
        }
    }

    var detectResult: String {
        get {
            if let str = UserDefaults.standard.value(forKey: "UFACE_DETECT_RESULT") as? String {
                return str
            }
            return ""
        }

        set(value) {
            UserDefaults.standard.setValue(value, forKey: "UFACE_DETECT_RESULT")
        }
    }

    func openAlertView(msg: String, okHandle: (() -> ())? = nil) {
        let alert = UIAlertController(title: nil, message: msg, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "Confirm", style: .default, handler: { (ac) in
            if okHandle != nil {
                okHandle!()
            }
        }))
        self.present(alert, animated: true, completion: nil)
    }

    func callAPI(params: Dictionary<String, String>, success: @escaping (_ result: FaceResultData)->(), failure: @escaping (_ error: FaceError?)->()) {
        if let url = URL(string: "http://61.74.179.47:18080/uface_api/face/common") {
            var request = URLRequest(url: url)
            request.cachePolicy = .reloadIgnoringLocalCacheData
            request.httpMethod = "POST"
            request.addValue("application/x-www-form-urlencoded; charset=UTF-8", forHTTPHeaderField: "Content-Type")
            request.timeoutInterval = 30
            let jsonString = params.reduce("") {"\($0)\($1.0)=\($1.1)&"}.dropLast()
            if let jsonData = jsonString.data(using: .utf8, allowLossyConversion: false) {
                request.httpBody = jsonData
            }else {
                failure(nil)
            }
            let conf = URLSessionConfiguration.ephemeral
            let session = URLSession.init(configuration: conf)
            let task = session.dataTask(with: request) { data, response, error in
                if error != nil {
                    failure(FaceError(errorCode: "99999", errorDescription: error.debugDescription))
                    return
                }
                if data != nil {
                    let decoder = JSONDecoder()
                    do {
                        print("Response low data :: ", String(decoding: data!, as: UTF8.self))
                        let returnData = try decoder.decode(FaceResultData.self, from: data!)
                        if returnData.code == "00000" {
                            if self.checkHashValue(data: returnData) {
                                success(returnData)
                            }else {
                                failure(FaceError(errorCode: "99999", errorDescription: String(format: "Incorrect hash value.(%@)", url.absoluteString)))
                            }
                            return
                        }else {
                            failure(FaceError(errorCode: returnData.code ?? "99999", errorDescription: returnData.msg ?? "Unknown"))
                            return
                        }
                    }catch {
                        if let err = error as? DecodingError {
                            switch err {
                            case .dataCorrupted(_) :
                                failure(FaceError(errorCode: "99999", errorDescription: String(format: "An indication that the data is corrupted or otherwise invalid.(%@)", url.absoluteString)))
                                break
                            case .typeMismatch(_, _):
                                failure(FaceError(errorCode: "99999", errorDescription: String(format: "An indication that a value of the given type could not be decoded because it did not match the type of what was found in the encoded payload.(%@)", url.absoluteString)))
                                break
                            case .valueNotFound(_, _):
                                failure(FaceError(errorCode: "99999", errorDescription: String(format: "An indication that a non-optional value of the given type was expected, but a null value was found.(%@)", url.absoluteString)))
                                break
                            case .keyNotFound(_, _):
                                failure(FaceError(errorCode: "99999", errorDescription: String(format: "An indication that a keyed decoding container was asked for an entry for the given key, but did not contain one.(%@)", url.absoluteString)))
                                break
                            @unknown default:
                                failure(FaceError(errorCode: "99999", errorDescription: String(format: "JSON Parsing unknown Fail.(%@)", url.absoluteString)))
                                break
                            }
                        }
                        failure(FaceError(errorCode: "99999", errorDescription: String(format: "JSON Parsing Fail with no decoding error.(%@)", url.absoluteString)))
                    }
                }
            }
            task.resume()
        }else {
            failure(FaceError(errorCode: "99998", errorDescription: "Wrong URL."))
            return
        }
    }

    //Compare Hash value to prevent fake verification
    private func checkHashValue(data: FaceResultData) -> Bool {
        let hashValue = data.hash
        let resultValue = String(format: "{\"code\":\"%@\",\"msg\":\"%@\",\"score\":\"%@\",\"cust_no\":\"%@\",\"chnl_dv\":\"%@\",\"age\":\"%@\",\"gender\":\"%@\",\"threshold\":\"%@\",\"batch_ids\":\"%@\",\"metsakuur_uface\":\"metsakuur_uface\"}", data.code ?? "", data.msg ?? "", data.score ?? "", data.cust_no ?? "", data.chnl_dv ?? "", data.age ?? "", data.gender ?? "", data.threshold ?? "", data.batch_ids ?? "")
        print("hashValue :: ", hashValue)
        print("resultValue :: ", resultValue.sha256())
        if hashValue == "" || hashValue == nil {
            return false
        }
        if resultValue.sha256() == hashValue {
            return true
        }
        return false
    }
}
