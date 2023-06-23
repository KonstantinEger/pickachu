import { PathService } from "./paths.js";

const streamcanvas = document.getElementById("original");

/**
 * @param {{ x: number, y: number }} vec
 * @param {number} t
 */
function vec2scale(vec, t) {
	vec.x *= t;
	vec.y *= t;
}

/**
 * @param {{ x: number, y: number }} a
 * @param {{ x: number, y: number }} b
 * @returns {{ x: number, y: number }}
 */
function vec2dir(a, b) {
	return {
		x: b.x - a.x,
		y: b.y - a.y,
	};
}

/**
 * @param {{ x: number, y: number }} vec
 * @returns {number}
 */
function vec2len(vec) {
	return Math.sqrt(vec.x*vec.x + vec.y*vec.y);
}

document.getElementById("btn1").addEventListener("click", () => {
	const urlInput = document.querySelector("#stream-input");
	if (!urlInput.value) {
		const imageInput = document.querySelector("#image-input");
		const file = imageInput.files[0];
		const ctx = streamcanvas.getContext("2d");
		const img = new Image();
		img.onload = () => {
			ctx.drawImage(img, 0, 0);
		};
		img.src = URL.createObjectURL(file);
	} else {
		loadPlayer({
			url: urlInput.value,
			canvas: streamcanvas,
			disableGl: true
		});
	}
});

let DIM;
let K;
let D;
let map1;
let map2;
let woFisheye;
let originalForDiff;
let gray;

let roboHelper;
let roboHelperRed;
let roboHelperGreen;
let redLower;
let redUpper;
let greenLower;
let greenUpper;

document.getElementById("btn2").addEventListener("click", () => {
	DIM = new cv.Size(1024, 768);
	K = cv.matFromArray(3, 3, cv.CV_32F, [26864.957112648033, 0.0, 462.391611030626, 0.0, 26100.130628757528, 355.3302327869351, 0.0, 0.0, 1.0]);
	D = cv.matFromArray(1, 4, cv.CV_32F, [-267.22399997112325, 21930.119543790075, 3445785.4755827268, 772191813.4918289]);
	map1 = cv.Mat.zeros(1024, 768, cv.CV_16SC2);
	map2 = cv.Mat.zeros(1024, 768, cv.CV_16SC2);
	gray = new cv.Mat();
	cv.fisheye_initUndistortRectifyMap(K, D, cv.Mat.eye(3, 3, cv.CV_32FC1), K, DIM, cv.CV_16SC2, map1, map2);

    roboHelper = new cv.Mat();
    roboHelperRed = new cv.Mat();
    roboHelperGreen = new cv.Mat();
    // // yellow
    // const low = new cv.Scalar(20, 100, 100);
    // const high = new cv.Scalar(40, 200, 200);
    // // red
    const redlow = new cv.Scalar(0, 100, 100);
    const redhigh = new cv.Scalar(20, 255, 255);
    // // green
    const greenlow = new cv.Scalar(50, 50, 50);
    const greenhigh = new cv.Scalar(70, 255, 255);

    redLower = new cv.Mat(768, 1024, cv.CV_8UC3, redlow);
    redUpper = new cv.Mat(768, 1024, cv.CV_8UC3, redhigh);
    greenLower = new cv.Mat(768, 1024, cv.CV_8UC3, greenlow);
    greenUpper = new cv.Mat(768, 1024, cv.CV_8UC3, greenhigh);

    // cv.cvtColor(yellowLower, yellowLower, cv.COLOR_HSV2RGB);
    // cv.cvtColor(yellowUpper, yellowUpper, cv.COLOR_HSV2RGB);

    // cv.imshow("yellow-lower", yellowLower);
    // cv.imshow("yellow-upper", yellowUpper);
	displayCanvasLoop();
});

// cv.Size(width, height) für DIM

const finalCanvas = document.querySelector("#wo-fisheye");
const finalCanvasCtx = finalCanvas.getContext("2d");

const pathService = new PathService({
	gridSize: {
		width: 1024,
		height: 768
	},
	ntiles: {
		x: 30,
		y: 25,
	}
});

let redPos;
let greenPos;

function findRobo(original) {
    // yellow hsv = 30 255 255
    cv.cvtColor(original, roboHelper, cv.COLOR_RGB2HSV);

    cv.inRange(roboHelper, greenLower, greenUpper, roboHelperGreen);
    cv.inRange(roboHelper, redLower, redUpper, roboHelperRed);

	// OPENING IMAGE
	const M = cv.Mat.ones(2, 2, cv.CV_8U);
	const anchor = new cv.Point(-1, -1);
	cv.morphologyEx(roboHelperRed, roboHelperRed, cv.MORPH_OPEN, M, anchor, 4, cv.BORDER_CONSTANT, cv.morphologyDefaultBorderValue());
	cv.morphologyEx(roboHelperGreen, roboHelperGreen, cv.MORPH_OPEN, M, anchor, 4, cv.BORDER_CONSTANT, cv.morphologyDefaultBorderValue());

	const contours = new cv.MatVector();
	const hierarchy = new cv.Mat();
	cv.findContours(roboHelperRed, contours, hierarchy, cv.RETR_CCOMP, cv.CHAIN_APPROX_SIMPLE);
    if (contours.size() > 0) {
        redPos = cv.minAreaRect(contours.get(0)).center;
    }
	cv.findContours(roboHelperGreen, contours, hierarchy, cv.RETR_CCOMP, cv.CHAIN_APPROX_SIMPLE);
    if (contours.size() > 0) {
        greenPos = cv.minAreaRect(contours.get(0)).center;
    }

    contours.delete();
    hierarchy.delete();
    M.delete();
}

function displayCanvasLoop() {
	const mat = cv.imread(streamcanvas);

	if (!woFisheye) {
		woFisheye = mat.clone();
	}


	cv.remap(mat, woFisheye, map1, map2, cv.INTER_LINEAR, cv.BORDER_CONSTANT);

    findRobo(woFisheye);

	cv.cvtColor(woFisheye, gray, cv.COLOR_RGBA2GRAY, 0);
	// cv.imshow("gray", gray);

	if (!originalForDiff) {
		originalForDiff = gray.clone();
	}

	cv.subtract(originalForDiff, gray, gray);

	// OPENING IMAGE
	const M = cv.Mat.ones(5, 5, cv.CV_8U);
	const anchor = new cv.Point(-1, -1);
	cv.morphologyEx(gray, gray, cv.MORPH_OPEN, M, anchor, 4, cv.BORDER_CONSTANT, cv.morphologyDefaultBorderValue());

	cv.threshold(gray, gray, 20, 255, cv.THRESH_BINARY);
	// cv.imshow("diff", gray);

	const contours = new cv.MatVector();
	const hierarchy = new cv.Mat();
	cv.findContours(gray, contours, hierarchy, cv.RETR_CCOMP, cv.CHAIN_APPROX_SIMPLE);

	cv.imshow("wo-fisheye", woFisheye);

	finalCanvasCtx.save();
	pathService.resetTiles();
	for (let i = 0; i < contours.size(); i++) {
		const rect = cv.minAreaRect(contours.get(i));
		const verts = cv.RotatedRect.points(rect)
			.map(v => {
				const dir = vec2dir(rect.center, v);
				vec2scale(dir, 1.3);
				return { x: rect.center.x + dir.x, y: rect.center.y + dir.y };
			});
		finalCanvasCtx.beginPath();

		finalCanvasCtx.moveTo(verts[3].x, verts[3].y);

		for (let j = 0; j < verts.length; j++) {
			const curr = verts[j];
			const next = verts[(j + 1) % verts.length];

			const dir = vec2dir(curr, next);

			for (let d = 0.1; d < 1.0; d += 0.1) {
				const x = curr.x + (dir.x * d);
				const y = curr.y + (dir.y * d);

	 			pathService.markOccupiedForPixel(x, y);
			}

			finalCanvasCtx.lineTo(curr.x, curr.y);
		}

	 	finalCanvasCtx.closePath();
	 	finalCanvasCtx.strokeStyle = "#ff0000";
	 	finalCanvasCtx.stroke();
	}
	finalCanvasCtx.restore();

	pathService.drawToCanvas(finalCanvasCtx);

    finalCanvasCtx.fillRect(redPos?.x, redPos?.y, 10, 10);
    finalCanvasCtx.fillRect(greenPos?.x, greenPos?.y, 10, 10);

	contours.delete();
	hierarchy.delete();

	mat.delete();
	setTimeout(() => {
		requestAnimationFrame(() => {
			displayCanvasLoop();
		});
	}, 60);
}
