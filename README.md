KinectHacking
=============

Boilerplate code for getting started with Kinect on Java and Linux.
What you need to do to run this:

1) Get the installer from SimpleOpenNI website. Use it to install the Kinect Driver, OpenNI and NITE (not in this order, see the docs!).
2) Copy the so library from SimpleOpenNI (depending on your system, 32 or 64-bit) in /lib
3) Install ant, plug in your Kinect, and enjoy by running ant run

PS: make sure you have Sun Java 1.6 or 1.7 (and not any other JVMs. They're not as good as Sun's)
PPS: in order to run the latest version of SimpleOpenNI, you need glib 2.15. Find out this version by running ldd --version
