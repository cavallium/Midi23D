# Midi23D
**Midi23D** is a tool made in Java that converts every note of a .midi music into GCODE instructions to send directly to a 3D printer.

![GUI](https://cavallium.it/assets/midi23d/midi-gui.png)

# How it works?
Every 3D printer has 3 or more particular motors, called *stepper motors*. Despite of the regular DC motors their angular speed and rotation be controlled very precisely.

Sending an impulse to that motors, in addition to result in a rotation, it will produce a sound, that it can be modulated by changing it.

With the GCODE you can tell to the 3D printer extruder to go in a position with a determined speed.

Setting the right speed for each motor by sending to the printer only the position and the total speed seems difficult, but it's quite easy.
You must use this formula: https://cavallium.it/assets/midi23d/delta.svg

In this way you can control simultaneously one note for each motor.

# Usage
First of all, download ***Midi23D*** ([***Cross-platform***](https://cavallium.it/assets/midi23d/Midi23D.jar)) ([***Windows***](https://cavallium.it/assets/midi23d/Midi23D.exe))

Run the program by executing this code into your terminal:
**java -jar Midi23D.jar <input-file.mid> <output-file.gcode> <speed-multiplier> <tone-multiplier> <motor-test>**
(Motor-test is a boolean (true/false) parameter that if it's true it plays the same notes on all the motors. It helps when you try to accord each motor speed).

Insert the parameters that asks to you.

Drag and drop the generated file into Repetier Host or your program that controls your printer.

Enjoy

# [Download samples and MIDIs](https://cavallium.it/Midi23D)

# Videos
[![Video](http://img.youtube.com/vi/4rcnu8j1Xqk/0.jpg)](https://www.youtube.com/embed/4rcnu8j1Xqk)
