const CALIBRATION_TIME_LIMIT = 5 * 1000;

class CalibrationContext {
    constructor() {
        this.isCalibrating = false;
    }

    startCalibrating(pos) {
        this.startPos = pos;
        this.isCalibrating = true;
        this.startTime = performance.now();
    }

    isTimeUp() {
        const now = performance.now();
        const isDone = (now - this.startTime) >= CALIBRATION_TIME_LIMIT;
        if (isDone) this.isCalibrating = false;
        return isDone;
    }
}

export class AppState {
    /**
     * @param {HTMLInputElement | null} streamUrlElement
     * @param {HTMLCanvasElement | null} streamCanvas
     * @param {number} calcEveryNFrames
     */
    constructor(streamUrlElement, streamCanvas, calcEveryNFrames) {
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
        this._roboDir = { x: 0, y: 0 };
        /** @private */
        this._calibrationCtx = new CalibrationContext();
    }

    calibrationCtx() {
        return this._calibrationCtx;
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
     * @param { { x: number, y: number } } dir
     */
    setRoboPosAndDir(pos, dir) {
        this._roboPos = pos;
        this._roboDir = dir;
    }
}

