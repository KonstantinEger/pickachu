// @ts-check

import { Log } from "./log.js";

class Tile {
	constructor({ occupied, x, y } = { occupied: false, x: 0, y: 0 }) {
		this.occupied = occupied;
        this.committedOccupied = occupied;
		this.x = x;
		this.y = y;
        this.visited = false;
        this.reachableThrough = -1;
	}
}

export class Command {
    /**
     * @param {string} type
     * @param {number} amount
     */
    constructor(type, amount) {
        this.type = type;
        this.amount = amount;
    }

    toString() {
        return this.type + ":" + Math.round(this.amount);
    }
}


export class PathService {
    /**
     * @param { any } a
     * @param { Log } log 
     */
	constructor({ gridSize, ntiles }, log) {
        /** @private */
        this._log = log;
		this.gridSize = gridSize;
		this.tileSize = {
			width: Math.floor(gridSize.width / ntiles.x),
			height: Math.floor(gridSize.height / ntiles.y)
		};

		// if (gridSize.width % this.tileSize.width !== 0)
		// 	throw new Error("grid width is not divisible by tile width");
		// if (gridSize.height % this.tileSize.height !== 0)
		// 	throw new Error("grid height is not divisible by tile height");

		this.tiles = [];

		this.nTilesX = ntiles.x;
		this.nTilesY = ntiles.y;

		for (let j = 0; j < this.nTilesY; j++) {
			for (let i = 0; i < this.nTilesX; i++) {
				this.tiles.push(new Tile({
					occupied: false,
					x: i * this.tileSize.width,
					y: j * this.tileSize.height,
				}));
			}
		}
	}

    bakeOccupied(roboPos) {
        const [rTileX, rTileY] = this.imageToTileCoords(roboPos.x, roboPos.y);
        for (let i = rTileX-1; i <= rTileX+1; i++) {
            for (let j = rTileY-1; j <= rTileY+1; j++) {
                const idx = this.tileCoordsToIndex(i, j);
                if (idx < 0 || this.tiles.length <= idx) {
                    continue;
                }
                const tile = this.tiles[idx];
                tile.committedOccupied = false;
                tile.occupied = false;
            }
        }
        for (const tile of this.tiles) {
            tile.committedOccupied = tile.occupied;
        }
    }

    /**
     * @param {{ x: number, y: number }} roboPos
     * @param {number} roboDir
     * @param {number} roboDirDefault
     * @param {{ x: number, y: number }} targetPos
     * @returns {Command[]}
     */
    calculatePath(roboPos, roboDir, roboDirDefault, targetPos) {
        this._log.info("calculating path", { roboPos, roboDir, roboDirDefault, targetPos });
        // Mark occupied fields around the robo to not be occupied
        const [rTileX, rTileY] = this.imageToTileCoords(roboPos.x, roboPos.y);
        const rIdx = this.tileCoordsToIndex(rTileX, rTileY);
        // for (let i = rTileX-2; i <= rTileX+2; i++) {
        //     for (let j = rTileY-2; j <= rTileY+2; j++) {
        //         const idx = this.tileCoordsToIndex(i, j);
        //         if (idx < 0 || this.tiles.length <= idx) {
        //             continue;
        //         }
        //         const tile = this.tiles[idx];
        //         tile.occupied = false;
        //     }
        // }

        // BFS through the grid
        const [tTileX, tTileY] = this.imageToTileCoords(targetPos.x, targetPos.y);
        /** @type {[number, number][]} */
        const queue = [[rTileX, rTileY]];
        let found = false;

        while (queue.length > 0 && !found) {
            // @ts-ignore
            const [tileX, tileY] = queue.shift();
            const currIdx = this.tileCoordsToIndex(tileX, tileY);
            const neighbors = [[tileX, tileY-1], [tileX-1, tileY], [tileX, tileY+1], [tileX+1, tileY]];
            for (const [i, j] of neighbors) {
                if (i < 0 || this.nTilesX < i
                    || j < 0 || this.nTilesY < j
                ) {
                    continue;
                }

                const neighborIdx = this.tileCoordsToIndex(i, j);
                if (neighborIdx < 0
                    || this.tiles.length <= neighborIdx
                    || this.tiles[neighborIdx].committedOccupied
                    || this.tiles[neighborIdx].visited
                    || neighborIdx === currIdx
                ) {
                    continue;
                }
                this.tiles[neighborIdx].visited = true;
                this.tiles[neighborIdx].reachableThrough = currIdx;
                queue.push([i, j]);
                found = i === tTileX && j === tTileY;
            }
            this.tiles[currIdx].visited = true;
        }

        // Build path of indices
        const path = [];
        const targetIndex = this.tileCoordsToIndex(tTileX, tTileY);
        let currIdx = this.tiles[targetIndex].reachableThrough;
        while (currIdx !== rIdx) {
            if (currIdx < 0) {
                this._log.warn("could not reach target", {});
                return [];
            }
            path.unshift(currIdx);
            currIdx = this.tiles[currIdx].reachableThrough;
        }
        this._log.debug("indices of path", { path });
        this._log.debug("coords of path", { path: path.map(this.indexToCoords.bind(this)) });

        /** @type {Command[]} */
        const commands = [];
        let normDir = roboDirDefault - roboDir;
        this._log.debug("rotations", { normDir, roboDirDefault, roboDir });

        // debugger;
        const movementLength = 187; // für 30x25
        // const movementLength = 130; // für 40x35
        const rotationMultiplier = 1.8963;
        for (let i = 0; i < path.length-1; i++) {
            const currIdx = path[i];
            const nextIdx = path[i+1];

            const rotationFrom0 = getDir(currIdx, nextIdx, this.nTilesX);
            let actualRotation = -normDir + rotationFrom0;

            if (actualRotation > 200) {
                actualRotation = actualRotation - 360;
            } else if (actualRotation < -200) {
                actualRotation = actualRotation + 360;
            }

            commands.push(new Command("Right", rotationMultiplier*actualRotation));
            commands.push(new Command("Forward", movementLength));
            normDir += actualRotation;
        }

        return commands;
    }

	/** @returns {number} */
	tileHeight() {
		return this.tileSize.height;
	}

	/** @returns {number} */
	tileWidth() {
		return this.tileSize.width;
	}

	/**
	 * @param {number} x
	 * @param {number} y
	 */
	markOccupiedForPixel(x, y) {
		if (x < 0 || y < 0)
			return;
        const [tileX, tileY] = this.imageToTileCoords(x, y);
		const tileIndex = this.tileCoordsToIndex(tileX, tileY);
		if (tileIndex >= this.tiles.length)
			return;
		this.tiles[tileIndex].occupied = true;
	}

	resetTiles() {
		for (const tile of this.tiles) {
			tile.occupied = false;
            tile.visited = false;
            tile.reachableThrough = -1;
		}
	}

	/**
	 * @param {CanvasRenderingContext2D} ctx
	 */
	drawToCanvas(ctx) {
		ctx.save();

		// ctx.strokeStyle = "#ffff00";
		// for (let i = 0; i < this.nTilesX; i++) {
		// 	ctx.beginPath();
		// 	ctx.moveTo(i * this.tileSize.width, 0);
		// 	ctx.lineTo(i * this.tileSize.width, this.gridSize.height);
		// 	ctx.closePath();
		// 	ctx.stroke();
		// }

		// for (let i = 0; i < this.nTilesY; i++) {
		// 	ctx.beginPath();
		// 	ctx.moveTo(0, i * this.tileSize.height);
		// 	ctx.lineTo(this.gridSize.width, i * this.tileSize.height);
		// 	ctx.closePath();
		// 	ctx.stroke();
		// }

		for (const tile of this.tiles) {
			ctx.fillStyle = tile.occupied ? "rgba(255, 0, 0, 0.1)" : "rgba(0, 0, 0, 0)";
			ctx.strokeStyle = "#ffff0030";
			ctx.beginPath();
			ctx.rect(tile.x, tile.y, this.tileSize.width, this.tileSize.height);
			ctx.closePath();
			ctx.stroke();
			ctx.fill();
		}

		ctx.restore();
	}

    /**
     * @private
     * @param {number} x
     * @param {number} y
     * @returns {[number, number]}
     */
    imageToTileCoords(x, y) {
		const tileX = Math.floor(x / this.tileSize.width);
		const tileY = Math.floor(y / this.tileSize.height);
        return [tileX, tileY];
    }

    /**
     * @private
     * @param {number} tileX
     * @param {number} tileY
     * @returns {number}
     */
    tileCoordsToIndex(tileX, tileY) {
        return tileY * this.nTilesX + tileX;
    }

    /** @param {number} idx */
    indexToCoords(idx) {
        const y = Math.floor(idx / this.nTilesX);
        const x = idx % this.nTilesX;
        return [x, y];
    }
}

/**
 * Returns how many degrees the robo has to turn if it faces right and wants to go to
 * an adjacent field.
 *
 * @param {number} rIdx
 * @param {number} tIdx
 * @param {number} w
 */
function getDir(rIdx, tIdx, w) {
    // r-w-1 | r-w | r-w+1
    // ------|-----|------
    //  r-1  | r   | r+1
    // ------|-----|------
    // r+w-1 | r+w | r+w+1
    const n = tIdx - rIdx;
    if (n === -w) return -90;
    else if (n === -1) return -180;
    else if (n === 0) return 0;
    else if (n === 1) return 0;
    else if (n === w) return 90;
    else {
        throw new Error("invalid dir");
    }
}
