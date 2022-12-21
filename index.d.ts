import { AwesomeCordovaNativePlugin } from '@awesome-cordova-plugins/core';

export declare class FaceDetectorOriginal extends AwesomeCordovaNativePlugin {
    scan(options?: String): Promise<String>;
}

export declare const FaceDetector: FaceDetectorOriginal;
