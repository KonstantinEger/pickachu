const streamcanvas = document.getElementById("original");

document.getElementById("btn1").addEventListener("click", () => {
	const urlInput = document.querySelector("#stream-input");
	loadPlayer({
		url: urlInput.value,
		canvas: streamcanvas,
		disableGl: true
	});
});

let DIM;
let K;
let D;
let map1;
let map2;
let woFisheye;
let originalForDiff;
let gray;

document.getElementById("btn2").addEventListener("click", () => {
	DIM = new cv.Size(1024, 768);
	K = cv.matFromArray(3, 3, cv.CV_32F, [26864.957112648033, 0.0, 462.391611030626, 0.0, 26100.130628757528, 355.3302327869351, 0.0, 0.0, 1.0]);
	D = cv.matFromArray(1, 4, cv.CV_32F, [-267.22399997112325, 21930.119543790075, 3445785.4755827268, 772191813.4918289]);
	map1 = cv.Mat.zeros(1024, 768, cv.CV_16SC2);
	map2 = cv.Mat.zeros(1024, 768, cv.CV_16SC2);
	gray = new cv.Mat();
	cv.fisheye_initUndistortRectifyMap(K, D, cv.Mat.eye(3, 3, cv.CV_32FC1), K, DIM, cv.CV_16SC2, map1, map2);
	displayCanvasLoop();
});

// cv.Size(width, height) für DIM

function displayCanvasLoop() {
	const mat = cv.imread(streamcanvas);


	if (!woFisheye) {
		woFisheye = mat.clone();
	}


	cv.remap(mat, woFisheye, map1, map2, cv.INTER_LINEAR, cv.BORDER_CONSTANT);

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
	cv.imshow("diff", gray);

	const contours = new cv.MatVector();
	const hierarchy = new cv.Mat();
	cv.findContours(gray, contours, hierarchy, cv.RETR_CCOMP, cv.CHAIN_APPROX_SIMPLE);

	const contoursColor = new cv.Scalar(255, 0, 255);

	for (let i = 0; i < contours.size(); i++) {
		const rect = cv.minAreaRect(contours.get(i));
		const verts = cv.RotatedRect.points(rect);

		for (let j = 0; j < 4; j++) {
			const rectangleColor = new cv.Scalar(0, 0, 0);
			cv.line(woFisheye, verts[j], verts[(j + 1) % 4], rectangleColor, 2, cv.LINE_AA, 0);
		}
	}
	cv.imshow("wo-fisheye", woFisheye);
	contours.delete();
	hierarchy.delete();

	mat.delete();

	setTimeout(() => {
		requestAnimationFrame(() => {
			displayCanvasLoop();
		});
	}, 60);
}
