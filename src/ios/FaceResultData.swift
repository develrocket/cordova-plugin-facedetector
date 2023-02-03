//
//  FaceResultData.swift
//  EsCareDemo
//
//  Created by kyengshin on 2022/10/25.
//

import Foundation

struct FaceResultData: Codable {
    let code: String?
    let msg: String?
    let score: String?
    let cust_no: String?
    let chnl_dv: String?
    let age: String?
    let gender: String?
    let threshold: String?
    let batch_ids: String?
    let hash: String?
}
