// @ts-check

import { PathService } from "./paths.js"

export class AppState {
    /**
     * @param {HTMLInputElement | null} streamUrlElement
     * @param {HTMLCanvasElement | null} streamCanvas
     * @param {number} calcEveryNFrames
     * @param {PathService} pathService
     * @param {WebSocket} ws
     */
    constructor(streamUrlElement, streamCanvas, calcEveryNFrames, pathService, ws) {
        if (!streamUrlElement || !streamCanvas)
            throw new Error("parameters cannot be null");
        /** @private */
        this._streamUrlElement = streamUrlElement;
        /** @private */
        this._streamCanvasElement = streamCanvas;
        /** @private */
        this._calcEveryNFrames = calcEveryNFrames;
        /** @private */
        this._frameCount = 0;
        /** @private */
        this._roboPos = { x: 0, y: 0 };
        /** @private */
        this._roboDir = 0;
        /** @private */
        this._pathService = pathService;
        /** @private */
        this._roboDirDefault = 0;
        /** @private */
        this._ws = ws;
    }

    ws() {
        return this._ws;
    }

    pathService() {
        return this._pathService;
    }

    /**
     * @returns {string}
     */
    streamUrl() {
        return this._streamUrlElement.value;
    }

    /** Returns true, if video schould be streamed from an url.
     *
     * @returns {boolean}
     */
    shouldStreamFromUrl() {
        return this.streamUrl().trim().length !== 0;
    }

    streamCanvas() {
        return this._streamCanvasElement;
    }

    incFrameCount() {
        this._frameCount += 1;
    }

    isCalculationFrame() {
        return this._frameCount % this._calcEveryNFrames === 0;
    }

    /**
     * @param { { x: number, y: number } } pos
     */
    setRoboPos(pos) {
        this._roboPos = pos;
    }

    /**
     * @param {number} dir
     */
    setRoboDir(dir) {
        this._roboDir = dir;
    }

    calibrateDir() {
        this._roboDirDefault = this._roboDir;
        console.debug({ msg: "calibrated rot", dir: this._roboDirDefault });
    }

    /**
     * @returns { { x: number, y: number } }
     */
    roboPos() {
        return JSON.parse(JSON.stringify(this._roboPos));
    }

    /**
     * @returns {number}
     */
    roboDir() {
        return this._roboDir;
    }

    /**
     * @returns {number}
     */
    roboDirDefault() {
        return this._roboDirDefault;
    }
}

