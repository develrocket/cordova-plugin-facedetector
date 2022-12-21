var __extends = (this && this.__extends) || (function () {
    var extendStatics = function (d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (Object.prototype.hasOwnProperty.call(b, p)) d[p] = b[p]; };
        return extendStatics(d, b);
    };
    return function (d, b) {
        if (typeof b !== "function" && b !== null)
            throw new TypeError("Class extends value " + String(b) + " is not a constructor or null");
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
import { AwesomeCordovaNativePlugin, cordova } from '@awesome-cordova-plugins/core';
var FaceDetectorOriginal = /** @class */ (function (_super) {
    __extends(FaceDetectorOriginal, _super);
    function FaceDetectorOriginal() {
        var _this = _super !== null && _super.apply(this, arguments) || this;
        return _this;
    }
    FaceDetectorOriginal.prototype.scan = function (options) { return cordova(this, "scan", { "callbackOrder": "reverse" }, arguments); };
    FaceDetectorOriginal.pluginName = "FaceDetector";
    FaceDetectorOriginal.plugin = "cordova-plugin-facedetector";
    FaceDetectorOriginal.pluginRef = "cordova.plugins.faceDetector";
    FaceDetectorOriginal.repo = "E:/103.study/cordova/cordova-plugin-facedetector";
    FaceDetectorOriginal.platforms = ["Android"];
    return FaceDetectorOriginal;
}(AwesomeCordovaNativePlugin));
var FaceDetector = new FaceDetectorOriginal();
export { FaceDetector };
