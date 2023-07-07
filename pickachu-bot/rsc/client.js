import { PathService } from "./paths.js";
import { AppState } from "./state.js";
import { Log, LEVEL_INFO } from "./log.js";

const LOGLEVEL = LEVEL_INFO;
const cvlog = new Log("OpenCV", LOGLEVEL);

const appState = new AppState(
    document.querySelector("#stream-input"),
    document.querySelector("#original"),
    // calculate every n frames:
    1,
    new PathService(
        {
            gridSize: {
                width: 1024,
                height: 768
            },
            ntiles: {
                x: 40,
                y: 35,
            },
        },
        new Log("PathService", LOGLEVEL),
    ),
    startWebSocket(),
    new Log("AppState", LOGLEVEL),
);

/**
 * @param {number} alpha
 */
function rad2deg(alpha) {
    return alpha * 180 / Math.PI;
}

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
function vec2add(a, b) {
	return {
		x: b.x + a.x,
		y: b.y + a.y,
	};
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

// start stream btn
document.getElementById("btn2").addEventListener("click", () => {
    document.querySelector("#loading").style.opacity = "100%";

	if (!appState.shouldStreamFromUrl()) {
		const imageInput = document.querySelector("#image-input");
		const file = imageInput?.files[0];
		const ctx = appState.streamCanvas().getContext("2d");
		const img = new Image();
		img.onload = () => {
			ctx.drawImage(img, 0, 0);
		};
		img.src = URL.createObjectURL(file);
	} else {
		loadPlayer({
			url: appState.streamUrl(),
			canvas: appState.streamCanvas(),
			disableGl: true
		});
	}
    setTimeout(() => {
        document.querySelector("#menu").style.display = "none";
        document.querySelector("#controls-container").style.display = "block";
        startOpenCv();
    }, 5000);
});

document.querySelector("#stop-btn").addEventListener("click", () => {
    cvlog.info("stopping robo");
    appState.setOnRoute(false);
});

function startWebSocket() {
    const ws = new WebSocket(`ws://141.46.137.146:8081/`);

    ws.onerror = (event) => {
        cvlog.error("ws error occurred", { event });
    };

    ws.onmessage = (event) => {
        cvlog.debug("got ws msg", { event });
        const [type, ...params] = event.data.split(":");
        if (type === "Gyro") {
            const degs = parseInt(params[0]);
            cvlog.debug("setting robo dir", { degs });
            appState.setRoboDir(degs);
        } else if (type === "Ack" && params[0] === "Forward" && appState.isOnRoute()) {
            const commands = appState.pathService().calculatePath(appState.roboPos(), appState.roboDir(), appState.roboDirDefault(), appState.targetPos());
            if (commands.length < 2) {
                appState.setOnRoute(false);
            } else {
                appState.ws().send(commands[0].toString());
                appState.ws().send(commands[1].toString());
            }
        }
    };

    ws.onopen = (event) => {
        cvlog.info("connected to ws", { event });
    };

    ws.onclose = (event) => {
        cvlog.info("ws closed", { event });
    };
    return ws;
}

let DIM;
let K;
let D;
let map1;
let map2;
let woFisheye;
let originalForDiff;
let gray;

let roboHelper;
let yellowMask;
let yellowLower;
let yellowUpper;

function  getMousePos(canvas, evt) {
  var rect = canvas.getBoundingClientRect(), // abs. size of element
    scaleX = canvas.width / rect.width,    // relationship bitmap vs. element for x
    scaleY = canvas.height / rect.height;  // relationship bitmap vs. element for y

  return {
    x: (evt.clientX - rect.left) * scaleX,   // scale mouse coordinates after they have
    y: (evt.clientY - rect.top) * scaleY     // been adjusted to be relative to element
  }
}

// start opencv
function startOpenCv() {
	DIM = new cv.Size(1024, 768);
	K = cv.matFromArray(3, 3, cv.CV_32F, [26864.957112648033, 0.0, 462.391611030626, 0.0, 26100.130628757528, 355.3302327869351, 0.0, 0.0, 1.0]);
	D = cv.matFromArray(1, 4, cv.CV_32F, [-267.22399997112325, 21930.119543790075, 3445785.4755827268, 772191813.4918289]);
	map1 = cv.Mat.zeros(1024, 768, cv.CV_16SC2);
	map2 = cv.Mat.zeros(1024, 768, cv.CV_16SC2);
	gray = new cv.Mat();
	cv.fisheye_initUndistortRectifyMap(K, D, cv.Mat.eye(3, 3, cv.CV_32FC1), K, DIM, cv.CV_16SC2, map1, map2);

    roboHelper = new cv.Mat();
    yellowMask = new cv.Mat();
    // // yellow
    const low = new cv.Scalar(20, 100, 100);
    const high = new cv.Scalar(40, 200, 200);
    // // red
    // const redlow = new cv.Scalar(0, 100, 100);
    // const redhigh = new cv.Scalar(20, 255, 255);
    // // // green
    // const greenlow = new cv.Scalar(50, 50, 50);
    // const greenhigh = new cv.Scalar(70, 255, 255);

    yellowLower = new cv.Mat(768, 1024, cv.CV_8UC3, low);
    yellowUpper = new cv.Mat(768, 1024, cv.CV_8UC3, high);

    // cv.cvtColor(yellowLower, yellowLower, cv.COLOR_HSV2RGB);
    // cv.cvtColor(yellowUpper, yellowUpper, cv.COLOR_HSV2RGB);

    // cv.imshow("yellow-lower", yellowLower);
    // cv.imshow("yellow-upper", yellowUpper);
	displayCanvasLoop();
}

document.getElementById("calibrate-btn").addEventListener("click", () => {
    appState.calibrateDir();
});

document.getElementById("wo-fisheye").addEventListener("click", (event) => {
    const pos = getMousePos(event.target, event);
    appState.setTargetPos(pos.x, pos.y);
    const commands = appState.pathService().calculatePath(appState.roboPos(), appState.roboDir(), appState.roboDirDefault(), pos);
    cvlog.debug("commands after click", { commands });
    if (commands.length > 0) {
        appState.setOnRoute(true);
        appState.ws().send(commands[0].toString());
        appState.ws().send(commands[1].toString());
    }
});

// cv.Size(width, height) für DIM

const finalCanvas = document.querySelector("#wo-fisheye");
/** @type {CanvasRenderingContext2D} */
const finalCanvasCtx = finalCanvas.getContext("2d");

function findRobo(original) {
    // yellow hsv = 30 255 255
    cv.cvtColor(original, roboHelper, cv.COLOR_RGB2HSV);

    cv.inRange(roboHelper, yellowLower, yellowUpper, yellowMask);

	// OPENING IMAGE
	const M = cv.Mat.ones(2, 2, cv.CV_8U);
	const anchor = new cv.Point(-1, -1);
	cv.morphologyEx(yellowMask, yellowMask, cv.MORPH_OPEN, M, anchor, 4, cv.BORDER_CONSTANT, cv.morphologyDefaultBorderValue());

    // cv.imshow("gray", yellowMask);

	const contours = new cv.MatVector();
	const hierarchy = new cv.Mat();
    let yellowPosition = {x: 0, y: 0};

	cv.findContours(yellowMask, contours, hierarchy, cv.RETR_CCOMP, cv.CHAIN_APPROX_SIMPLE);
    if (contours.size() === 0) {
        cvlog.debug("found no contours");
        return;
    }

    finalCanvasCtx.strokeStyle = "#00ff00";
    let maxSize = 0;
    for (let i = 0; i < contours.size(); i++) {
        const rect = cv.minAreaRect(contours.get(i));
        const { width, height } = rect.size;
        const size = width*height;
        if (size > maxSize) {
            yellowPosition = rect.center;
            maxSize = size;
        }
    }

    // TODO dir
    appState.setRoboPos(yellowPosition);

    finalCanvasCtx.fillStyle = "#00ff00";
    finalCanvasCtx.fillRect(yellowPosition.x, yellowPosition.y, 10, 10);

    contours.delete();
    hierarchy.delete();
    M.delete();
}

function obsticleDetection() {
	cv.cvtColor(woFisheye, gray, cv.COLOR_RGBA2GRAY, 0);

	if (!originalForDiff) {
		originalForDiff = gray.clone();
	}

	cv.subtract(originalForDiff, gray, gray);

	// OPENING IMAGE
	const M = cv.Mat.ones(5, 5, cv.CV_8U);
	const anchor = new cv.Point(-1, -1);
	cv.morphologyEx(gray, gray, cv.MORPH_OPEN, M, anchor, 4, cv.BORDER_CONSTANT, cv.morphologyDefaultBorderValue());

	cv.threshold(gray, gray, 40, 255, cv.THRESH_BINARY);
	// cv.imshow("diff", gray);

	const contours = new cv.MatVector();
	const hierarchy = new cv.Mat();
	cv.findContours(gray, contours, hierarchy, cv.RETR_CCOMP, cv.CHAIN_APPROX_SIMPLE);


	finalCanvasCtx.save();
	appState.pathService().resetTiles();
	for (let i = 0; i < contours.size(); i++) {
		const rect = cv.minAreaRect(contours.get(i));
		const verts = cv.RotatedRect.points(rect)
			.map(v => {
				const dir = vec2dir(rect.center, v);
				vec2scale(dir, 1.6);
				return { x: rect.center.x + dir.x, y: rect.center.y + dir.y };
			});
		finalCanvasCtx.beginPath();

		finalCanvasCtx.moveTo(verts[3].x, verts[3].y);

		for (let j = 0; j < verts.length; j++) {
			const curr = verts[j];
			const next = verts[(j + 1) % verts.length];

			const dir = vec2dir(curr, next);

			for (let d = 0.05; d < 1.0; d += 0.05) {
				const x = curr.x + (dir.x * d);
				const y = curr.y + (dir.y * d);

	 			appState.pathService().markOccupiedForPixel(x, y);
			}

			finalCanvasCtx.lineTo(curr.x, curr.y);
		}

	 	finalCanvasCtx.closePath();
	 	finalCanvasCtx.strokeStyle = "#ff0000";
	 	finalCanvasCtx.stroke();
	}
	finalCanvasCtx.restore();

	contours.delete();
	hierarchy.delete();

}

function displayCanvasLoop() {
    appState.incFrameCount();

	const mat = cv.imread(appState.streamCanvas());

	if (!woFisheye) {
		woFisheye = mat.clone();
	}


	cv.remap(mat, woFisheye, map1, map2, cv.INTER_LINEAR, cv.BORDER_CONSTANT);

	cv.imshow("wo-fisheye", woFisheye);

    if (appState.isCalculationFrame()) {
        findRobo(woFisheye);
        obsticleDetection();
    }

    // appState.pathService().calculatePath(appState.roboPos(), appState.roboDir(), appState.roboDirDefault(), null);
	appState.pathService().drawToCanvas(finalCanvasCtx);

	mat.delete();
	setTimeout(() => {
		requestAnimationFrame(() => {
			displayCanvasLoop();
		});
	}, 60);
}
