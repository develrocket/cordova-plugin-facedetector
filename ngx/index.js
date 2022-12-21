import { __decorate, __extends } from "tslib";
import { Injectable } from '@angular/core';
import { AwesomeCordovaNativePlugin, cordova } from '@awesome-cordova-plugins/core';
import * as i0 from "@angular/core";
var FaceDetector = /** @class */ (function (_super) {
    __extends(FaceDetector, _super);
    function FaceDetector() {
        var _this = _super !== null && _super.apply(this, arguments) || this;
        return _this;
    }
    FaceDetector.prototype.scan = function (options) { return cordova(this, "scan", { "callbackOrder": "reverse" }, arguments); };
    FaceDetector.ɵfac = i0.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "12.2.16", ngImport: i0, type: FaceDetector, deps: null, target: i0.ɵɵFactoryTarget.Injectable });
    FaceDetector.ɵprov = i0.ɵɵngDeclareInjectable({ minVersion: "12.0.0", version: "12.2.16", ngImport: i0, type: FaceDetector });
    FaceDetector.pluginName = "FaceDetector";
    FaceDetector.plugin = "cordova-plugin-facedetector";
    FaceDetector.pluginRef = "cordova.plugins.faceDetector";
    FaceDetector.repo = "E:/103.study/cordova/cordova-plugin-facedetector";
    FaceDetector.platforms = ["Android"];
    FaceDetector = __decorate([], BarcodeScanner);
    return FaceDetector;
}(AwesomeCordovaNativePlugin));
export { FaceDetector };
i0.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "12.2.16", ngImport: i0, type: FaceDetector, decorators: [{
            type: Injectable
        }], propDecorators: { scan: [], encode: [] } });
