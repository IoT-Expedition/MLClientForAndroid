====================
Connect a SensorTag
====================

This page describes how to setup a SensorTag to be used with ML Client for Android.
In this example, we use Raspberry PI as a gateway.
Raspberry PI connects a SensorTag via BLE, collects data,
and sends the data to BuildingDepot.
Once you setup Raspberry PI to send data from a SensorTag to BuildingDepot,
you can try the demo that we did using dummy scripts with a real sensor.

Setup a Raspberry PI
-----------------------
Connect a Raspberry PI to a network as you like, and setup a BLE dongle.
Currently (04/10/2016), gatttool that we install in the next step does not work on Jessie.
Thus, we recommend you to use Wheezy for your Raspberry PI.

Install gatttool
-----------------------
Our demo script ``sensor_tag_connector.py`` uses gatttool to communicate with a SensorTag.
So, install it by following the steps below.

Download the latest Bluez source:

.. code-block:: bash

	$ wget http://www.kernel.org/pub/linux/bluetooth/bluez-5.39.tar.xz 
	$ tar xvf bluez-5.18.tar.xz 
	$ sudo apt-get install libglib2.0-dev libdbus-1-dev libusb-dev libudev-dev libical-dev systemd libreadline-dev
	$ .configure --enable-library
	$ make -j8 && sudo make install

The install script does not copy gatttool to your /usr/local/bin/ directory. You must do it manually

.. code-block:: bash

	$ sudo cp attrib/gatttool /usr/local/bin/

Get a SensorTag's MAC Address
-------------------------------
To communicate with a SensorTag, you need its MAC Address.

.. code-block:: bash

	$ sudo hciconfig hci0 up
	$ sudo hcitool lescan

You will find something similar to:

.. code-block:: bash

	LE Scan ...
	90:59:AF:0A:A8:4E (unknown)
	90:59:AF:0A:A8:4E SensorTag

The string next to SensorTag is its MAC address.
Set the address in ``demo_scripts/sensor_tag/connector_setting.json``

Install pexpect
------------------
You also need pexpect to run our script.
Install it with:

.. code-block:: bash
	
	$ sudo pip install pexpect

Get data from a SensorTag
----------------------------
Now run ``demo_scripts/sensor_tag/sensor_tag_connect.py`` to send data from a SensorTag to BuildingDepot.

.. code-block:: bash
	
	$ python sensor_tag_connect.py
	

