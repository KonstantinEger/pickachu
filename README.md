# PICKachu - Automated Cargo Handling Unit

<img src="https://github.com/KonstantinEger/pickachu/assets/81491426/6f56d098-7466-4ae5-9127-c19709c30ce4" width="400">

PICKachu is an educational project that combines two courses: **Mobile Robots** and **Image Processin**g. This project involves building an automated cargo handling unit that can be controlled through a web interface from a mobile device. The robot, constructed using LEGO Mindstorms components, is powered by the leJOS (Java for LEGO Mindstorms) firmware.

## Introduction

PICKachu's primary objective is to autonomously detect and navigate around obstacles with the help of an overhead camera, once the operator (human) specified a destination. The entire software stack, including the servers, is hosted on the robot itself, allowing for simpler setup ans less dependencies.

## Demo
![robot_final_composition](https://github.com/KonstantinEger/pickachu/assets/81491426/c09b19f9-b181-4fbd-8620-d439002ffd5a)

Check out our demo video on YouTube to see PICKachu in action:

[![PICKachu Demo Video](https://github.com/KonstantinEger/pickachu/assets/81491426/2feb9630-8053-495c-9933-89d890cd0567)]([link-to-demo-video](https://youtu.be/JIuG0PSL9tk))

## Setup Guide

Please note that PICKachu's code is tailored for our specific robot and camera setup, making it essential to replicate the hardware and software environment for successful execution. **Since we provide no blueprints and you need an overhead camera this is basically impossible.**
Anyways, to run the code, follow these steps:

1. Install and set up the leJOS Eclipse plugin.
2. Connect to the robot via Bluetooth or any other compatible method.
3. Launch the leJOS control panel via the plugin.
4. Upload the compiled JAR file to the robot using the leJOS control panel or Eclipse's run configurations.
5. Access the web interface hosted by the robot via `<RobotIP:8080>`.
6. Specify a relayed RTSP stream converted to WebSocket for live camera feed.

## Acknowledgments

We would like to express our gratitude to the Mobile Robots and Image Processing course instructors for providing us with the opportunity to work on this exciting project.
