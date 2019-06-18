definition(
    name: "Virtual Thermostat With Device",
    namespace: "piratemedia/smartthings",
    author: "Eliot S. - Modified for cooling by Daniel K.",
    description: "Control an AC in conjunction with any temperature sensor, like a SmartSense Multi.",
    category: "Green Living",
    iconUrl: "https://raw.githubusercontent.com/eliotstocker/SmartThings-VirtualThermostat-WithDTH/master/logo-small.png",
    iconX2Url: "https://raw.githubusercontent.com/eliotstocker/SmartThings-VirtualThermostat-WithDTH/master/logo.png"
)

preferences {
	section("Choose a temperature sensor(s)... (If multiple sensors are selected, the average value will be used)"){
		input "sensors", "capability.temperatureMeasurement", title: "Sensor", multiple: true
	}
	section("Select the AC outlet(s)... "){
		input "outlets", "capability.switch", title: "Outlets", multiple: true
	}
	section("Only cool when contact isnt open (optional, leave blank to not require contact sensor)..."){
		input "motion", "capability.contactSensor", title: "Contact", required: false
	}
	section("Never go above this temperature: (optional)"){
		input "emergencySetpoint", "decimal", title: "Emergency Temp", required: false
	}
	section("Temperature Threshold (Don't allow cooling to go above or bellow this amount from set temperature)") {
		input "threshold", "decimal", "title": "Temperature Threshold", required: false, defaultValue: 1.0
	}
}

def installed()
{
    log.debug "running installed"
    state.deviceID = Math.abs(new Random().nextInt() % 9999) + 1
	state.lastTemp = null
    state.contact = true
    /*def thermostat = createDevice()
    
	subscribe(sensor, "temperature", temperatureHandler)
	if (motion) {
		subscribe(motion, "motion", motionHandler)
	}
    
    subscribe(thermostat, "thermostatSetpoint", thermostatTemperatureHandler)
    subscribe(thermostat, "thermostatMode", thermostatModeHandler)
    thermostat.setVirtualTemperature(sensor.currentValue("temperature"))*/
}

def createDevice() {
    def thermostat
    def label = app.getLabel()
	// Commenting out hub refernce - breaks in several thermostat DHs
    log.debug "create device with id: pmvt$state.deviceID, named: $label" //, hub: $sensor.hub.id"
    try {
        thermostat = addChildDevice("piratemedia/smartthings", "Virtual Thermostat Device", "pmvt" + state.deviceID, null, [label: label, name: label, completedSetup: true])
    } catch(e) {
        log.error("caught exception", e)
    }
    return thermostat
}

def getThermostat() {
	def child = getChildDevices().find {
    	d -> d.deviceNetworkId.startsWith("pmvt" + state.deviceID)
  	}
    return child
}

def uninstalled() {
    deleteChildDevice("pmvt" + state.deviceID)
}

def updated()
{
    log.debug "running updated: $app.label"
	unsubscribe()
    def thermostat = getThermostat()
    if(thermostat == null) {
        thermostat = createDevice()
    }
    state.contact = true
	state.lastTemp = null
	subscribe(sensors, "temperature", temperatureHandler)
	if (motion) {
		subscribe(motion, "contact", motionHandler)
	}
    subscribe(thermostat, "thermostatSetpoint", thermostatTemperatureHandler)
    subscribe(thermostat, "thermostatMode", thermostatModeHandler)
    thermostat.clearSensorData()
    thermostat.setVirtualTemperature(getAverageTemperature())
}

def getAverageTemperature() {
	def total = 0;
    def count = 0;
	for(sensor in sensors) {
    	total += sensor.currentValue("temperature")
        thermostat.setIndividualTemperature(sensor.currentValue("temperature"), count, sensor.label)
        count++
    }
    return total / count
}

def temperatureHandler(evt)
{
    def thermostat = getThermostat()
    thermostat.setVirtualTemperature(getAverageTemperature())
	if (state.contact || emergencySetpoint) {
		evaluate(evt.doubleValue, thermostat.currentValue("thermostatSetpoint"))
        state.lastTemp = evt.doubleValue
	}
	else {
		coolingOff()
	}
}

def motionHandler(evt)
{
    def thermostat = getThermostat()
	if (evt.value == "closed") {
    	state.contact = true
		def thisTemp = getAverageTemperature()
		if (thisTemp != null) {
			evaluate(thisTemp, thermostat.currentValue("thermostatSetpoint"))
			state.lastTemp = thisTemp
		}
	} else if (evt.value == "open") {
        log.debug "should turn heating off"
    	state.contact = false
	    coolingOff()
	}
}

def thermostatTemperatureHandler(evt) {
	def temperature = evt.doubleValue
    //setpoint = temperature
	log.debug "Desired Temperature set to: $temperature $state.contact"
    
    def thisTemp = getAverageTemperature()
	if (state.contact) {
		evaluate(thisTemp, temperature)
	}
	else {
		coolingOff()
	}
}

def thermostatModeHandler(evt) {
	def mode = evt.value
	log.debug "Mode Changed to: $mode"
    def thermostat = getThermostat()
    
    def thisTemp = getAverageTemperature()
	if (state.contact) {
		evaluate(thisTemp, thermostat.currentValue("thermostatSetpoint"))
	}
	else {
		coolingOff(mode == 'cool' ? false : true)
        // *change from heat
	}
}

private evaluate(currentTemp, desiredTemp)
{
	log.debug "EVALUATE($currentTemp, $desiredTemp)"
	// heater
    
    if ( (desiredTemp - currentTemp <= threshold)) {
		coolingOn()
	//	} else if ( (currentTemp - desiredTemp >= threshold)) {
	} else if ( (currentTemp - desiredTemp <= threshold))
		coolingOff()
      else if(state.current == "on") {
      updateTimings()
    }
}
    // swapped cooling state's, and swapped order of statements


def coolingOn() {
    if(thermostat.currentValue('thermostatMode') == 'cool' || force) {
    	log.debug "Heating on Now"
        outlets.on()
        //outlets.off makes all temperature changes turn outlets off
        thermostat.setCoolingStatus(true)
    } else {
        coolingOff(true)
    }
}

def coolingOff(coolingOff) {
	def thisTemp = getAverageTemperature()
    if (thisTemp >= emergencySetpoint) {
        log.debug "Heating in Emergency Mode Now"
        outlet.on()
        thermostat.setEmergencyMode(true)
    } else {
    	log.debug "Heating off Now"
    	outlets.off()
        //outletsOff()//
		if(coolingOff) {
			thermostat.setCoolingOff(true)
		} else {
			thermostat.setCoolingStatus(false)
		}
     }
}
