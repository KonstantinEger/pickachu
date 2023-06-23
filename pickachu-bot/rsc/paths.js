class Tile {
	constructor({ occupied, x, y } = { occupied: false, x: 0, y: 0 }) {
		this.occupied = occupied;
		this.x = x;
		this.y = y;
	}
}


export class PathService {
	constructor({ gridSize, ntiles }) {
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
		const tileX = Math.floor(x / this.tileSize.width);
		const tileY = Math.floor(y / this.tileSize.height);
		const tileIndex = tileY * this.nTilesX + tileX;
		if (tileIndex >= this.tiles.length)
			return;
		this.tiles[tileIndex].occupied = true;
	}

	resetTiles() {
		for (const tile of this.tiles) {
			tile.occupied = false;
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
			ctx.strokeStyle = "#ffff00";
			ctx.beginPath();
			ctx.rect(tile.x, tile.y, this.tileSize.width, this.tileSize.height);
			ctx.closePath();
			ctx.stroke();
			ctx.fill();
		}

		ctx.restore();
	}
}

