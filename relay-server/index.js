const express = require('express');
const fs = require("fs");
const app = express();

const { proxy, scriptUrl } = require('rtsp-relay')(app);
console.log(scriptUrl);

const handler = proxy({
	url: `rtsp://141.46.137.93:8554/mystream`,
	// if your RTSP stream need credentials, include them in the URL as above
	verbose: true,
});

// the endpoint our RTSP uses
app.ws('/api/stream', handler);

app.get('/client.js', (req, res) =>{
	const filename = "client.js";
	const html = fs.readFileSync(filename, { encoding: "utf-8" });
	res.send(html);
});
// this is an example html page to view the stream
app.get('/', (req, res) =>{
	const filename = "index.html";
	const html = fs.readFileSync(filename, { encoding: "utf-8" });
	res.send(html);
});

app.listen(2000);
