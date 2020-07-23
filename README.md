Autonomous Surface Vehicle Simulator
=========================

The Autonomous Surface Vehicle Simulator was developed for, and used by, the Queensland University of Technology (QUT) team at the 2016 Maritime RobotX Challenge. This simulator was developed to address a gap in existing robotic marine vehicle simulation software by producing high-fidelity sensor data from a realistic operating environment, inclusion of ASV motion dynamics, as well as a direct interface for hardware-in-the-loop simulation. The key functions required for the simulator to be an effective model of the QUT ASV included a high-fidelity camera, LiDAR (Velodyne HDL-32E), IMU and GPS sensor simulation, as well as buoyancy and physics simulation for modelling sea-state, vehicle motion and control performance. Another key feature of the simulator was the provision to directly interface to the Robotic Operating System (ROS) to publish and subscribe to sensor topics (in real-time) and allow hardware-in-the-loop simulation of the ASV systems for algorithm development. The fidelity and performance of the simulator was benchmarked against the existing best available alternative simulator, V-Rep, with results shown to demonstrate the Autonomous Marine Surface Vessel Simulator achieved improved performance and fidelity when simulating above surface marine environments.


![Alt text](BoatVis/res/screenshots/SimulatorDisplayOfComponents.png?raw=true "A display of some simulator features")

For building - it is recommended that you use Java version 8 or later.
