const express = require('express');
const fs = require("fs");
const path = require("path");
const app = express();

const { proxy, scriptUrl } = require('rtsp-relay')(app);
console.log(scriptUrl);

const handler = proxy({
	url: `rtsp://141.46.137.93:8554/mystream`,
	// if your RTSP stream need credentials, include them in the URL as above
	verbose: true,
});

app.use(express.static(path.join(__dirname, "../pickachu-bot/rsc/")));

// the endpoint our RTSP uses
app.ws('/api/stream', handler);

app.listen(2000);
