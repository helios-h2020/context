# Context Manager core module

# Context Management API

## Introduction

The Context manager module handles context-related monitoring and reasoning for the user. It provides a framework for
implementing contexts and context detection with core implementations. New context classes can be introduced by extending 
the provided base class implementations. The context manager can access sensors with the help of a separate Sensor Manager 
as well as other context sources. The Context module includes also information overload control interface to contextually 
limit the amount of incoming message alerts for the user.

The *Context Management API* provides methods to accessing and managing context information.

Context Manager is one of the HELIOS Core modules as highlighted in the picture below:

![HELIOS Context Management API](https://raw.githubusercontent.com/helios-h2020/h.core-Context/master/doc/images/helios-context.png "Context Management API")

## API usage

See javadocs in [javadocs.zip](https://raw.githubusercontent.com/helios-h2020/h.core-Context/master/doc/javadocs.zip).

### Introduction
Applications should include this library by adding it in the specific `build.gradle` file. An example:
```
dependencies {
    implementation 'eu.h2020.helios_social.core.context:context:1.0.14'
}
```
To use the dependency in `build.gradle` of the "father" project, you should specify the last version available in Nexus, related to the last Jenkins's deploy.


### Base classes and interfaces 

The Context API provides methods for accessing and managing context information. The following outlines the main classes and 
interfaces of the module.

#### Context 

`Context` (package: `eu.h2020.helios_social.core.context`) is the base class for HELIOS contexts, which provides base implementations 
for the context interface methods. New context types are created by extending it. The following lists the base methods of the Context class:
```
public class Context {
    String getId(); 
    String getName(); 
    void setName(String name); 
    boolean isActive(); 
    void setActive(boolean active); 
    void registerContextListener(ContextListener listener); 
    void unregisterContextListener(ContextListener listener); 
    Iterator<ContextListener> getContextListeners();
}
```

The module includes implementations for a set of basic context types (see subpackage `ext`), e.g.: `LocationContext`, `ActivityContext`, `DeviceContext` and `TimeContext`. 
There are also implementations for combination (Boolean) contexts `ContextAnd`, `ContextOr`, `ContextNot`.

`ContextListener` is an interface for tracking context active value changes. The contextChanged method is used to inform about changes 
in the active value (true/false) of the context. The listeners can be registered for contexts using the registerContextListener method of Helios contexts.
```
public interface ContextListener { 
    void contextChanged(boolean active); 
}
```
For more information about the usage of the context classes, see [the examples](#examples-of-api-usage).

#### Sensor

`Sensor` (package: `eu.h2020.helios_social.core.sensor`) is the abstract base class for HELIOS sensors. The subclasses of it need to implement 
the methods `startUpdates` and `stopUpdates`, which are used to start/stop receiving data values from the sensor.
The following shows the public and abstract methods of the sensor base class: 
```
public abstract class Sensor { 
    abstract void startUpdates(); 
    abstract void stopUpdates(); 
    void receiveValue(Object value); 
    void registerValueListener(SensorValueListener listener);
    void unregisterValueListener(SensorValueListener listener); 
    Iterator<SensorValueListener> getValueListeners(); 
}
```
The module includes implementations for a set of basic sensor types (see subpackage `ext`): `LocationSensor`, `ActivitySensor`, 
`DeviceSensor`, `BluetoothSensor`, `WifiSensor`, `TimeSensor`.
To receive data value updates from the sensor, the context should implement the `SensorValueListener` interface:
```
public interface SensorValueListener { 
    void receiveValue(Object value); 
}
```
The implemented listener can be registered using the registerValueListener method of the sensor.

#### InformationOverloadControl

`InformationOverloadControl` is the main interface for information overload control 
implementations (package: `eu.h2020.helios_social.core.info_control`): 
```
public interface InformationOverloadControl {
    List<ContextProbability> getContextProbabilities(MessageInfo message);
    int getMessageImportance(MessageInfo message, Context context);
    List<MessageImportance> getMessageImportance(MessageInfo message);
    void readMessage(MessageInfo message);
    void addMessageContext(MessageContext messageContext);
}
```
The class `InfoControl` is the main implementing class of the the `InformationOverloadControl` interface. E.g., it can be used to estimate
the importance of the received message in the user's context (see the method `getMessageImportance()` of the interface).


### Examples of API usage

See the source folder: ["app/src/main/java/eu/h2020/helios_social/core/context_example1/"](https://github.com/helios-h2020/h.core-Context/tree/master/app/src/main/java/eu/h2020/helios_social/core/context_example1/)

#### *LocationContext* example
"ContextExample1.java" (Android app)
- see the description: [ContextExample1.pdf](https://raw.githubusercontent.com/helios-h2020/h.core-Context/master/doc/ContextExample1.pdf)
- shows how to create two contexts "at work" and "at home". In addition, it shows how to relate 
the created contexts to CEN (contextual ego network), and how to use profile information within the contexts
- this example application depends on the modules: eu.h2020.helios_social.core.profile and eu.h2020.helios_social.core.contextualegonetwork

#### *ActivityContext* example 
"ActivityContextExample1.java" shows how to create contexts for different activity types: "Walking", "In vehicle", ... 

#### *WifiContext* example
A wifi-based context created (see "WifiContextExample1.java")

#### *BluetoothLESensor* example
A bluetooth LE sensor example ("BluetoothLESensorExample1.java"])

#### *DeviceSensor* example
An example how to use the class DeviceSensor, which provides methods to use Android devices build-in 
sensors that measure motion, orientation, and various environmental conditions ("DeviceSensorExample1.java").

#### *MyContexts* example
This example shows user's contexts and their status in a view ("MyContextsExample.java")

### How to build the example application
- [how to build the example application.](https://github.com/helios-h2020/h.core-Context/blob/master/doc/building.md)

### How to create a new context class by extending the base Context class
- [creating a time-based context class](https://github.com/helios-h2020/h.core-Context/blob/master/doc/creatingContext1.md)

## Android Studio project structure

* app - Context test application

* doc - Additional documentation files

* lib - Context module and API implementation
