'use strict';

var tslib = require('tslib');
var i0 = require('@angular/core');
var core = require('@awesome-cordova-plugins/core');

function _interopNamespaceDefault(e) {
    var n = Object.create(null);
    if (e) {
        Object.keys(e).forEach(function (k) {
            if (k !== 'default') {
                var d = Object.getOwnPropertyDescriptor(e, k);
                Object.defineProperty(n, k, d.get ? d : {
                    enumerable: true,
                    get: function () { return e[k]; }
                });
            }
        });
    }
    n.default = e;
    return Object.freeze(n);
}

var i0__namespace = /*#__PURE__*/_interopNamespaceDefault(i0);

var FaceDetector = /** @class */ (function (_super) {
    tslib.__extends(FaceDetector, _super);
    function FaceDetector() {
        var _this = _super !== null && _super.apply(this, arguments) || this;
        return _this;
    }
    FaceDetector.prototype.scan = function (options) { return core.cordova(this, "scan", { "callbackOrder": "reverse" }, arguments); };
    FaceDetector.ɵfac = i0__namespace.ɵɵngDeclareFactory({ minVersion: "12.0.0", version: "12.2.16", ngImport: i0__namespace, type: FaceDetector, deps: null, target: i0__namespace.ɵɵFactoryTarget.Injectable });
    FaceDetector.ɵprov = i0__namespace.ɵɵngDeclareInjectable({ minVersion: "12.0.0", version: "12.2.16", ngImport: i0__namespace, type: FaceDetector });
    FaceDetector.pluginName = "FaceDetector";
    FaceDetector.plugin = "cordova-plugin-facedetector";
    FaceDetector.pluginRef = "cordova.plugins.faceDetector";
    FaceDetector.repo = "E:/103.study/cordova/cordova-plugin-facedetector";
    FaceDetector.platforms = ["Android"];
    FaceDetector = tslib.__decorate([], FaceDetector);
    return FaceDetector;
}(core.AwesomeCordovaNativePlugin));
i0__namespace.ɵɵngDeclareClassMetadata({ minVersion: "12.0.0", version: "12.2.16", ngImport: i0__namespace, type: FaceDetector, decorators: [{
            type: i0.Injectable
        }], propDecorators: { scan: [], encode: [] } });

exports.FaceDetector = FaceDetector;
