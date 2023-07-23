/**
 * @typedef {0 | 1 | 2 | 3} LogLevel
 */

export const LEVEL_DEBUG = 0;
export const LEVEL_INFO = 1;
export const LEVEL_WARN = 2;
export const LEVEL_ERROR = 3;


export class Log {
    /**
     * @param {string} name
     * @param {LogLevel} initialLevel
     */
    constructor(name, initialLevel) {
        /** @private */
        this._name = name;
        /** @private */
        this._level = initialLevel;
    }

    /**
     * @param {string} msg
     * @param {any | undefined } data
     */
    debug(msg, data) {
        this._log(LEVEL_DEBUG, msg, data);
    }

    /**
     * @param {string} msg
     * @param {any | undefined } data
     */
    info(msg, data) {
        this._log(LEVEL_INFO, msg, data);
    }

    /**
     * @param {string} msg
     * @param {any | undefined } data
     */
    warn(msg, data) {
        this._log(LEVEL_WARN, msg, data);
    }

    /**
     * @param {string} msg
     * @param {any | undefined } data
     */
    error(msg, data) {
        this._log(LEVEL_ERROR, msg, data);
    }

    /**
     * @private
     * @param {LogLevel} level
     * @param {string} msg
     * @param {any | undefined } data
     */
    _log(level, msg, data) {
        if (this._level <= level) {
            console.log({ level, name: this._name, msg, data });
        }
    }
}
