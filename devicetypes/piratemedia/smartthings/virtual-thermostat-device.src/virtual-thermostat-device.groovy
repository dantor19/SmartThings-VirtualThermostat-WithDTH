metadata {
	definition (name: "Virtual Thermostat Device", namespace: "piratemedia/smartthings", author: "Eliot S.") {
		capability "Actuator"
		capability "Refresh"
		capability "Sensor"
		capability "Thermostat"
		//capability "Thermostat Heating Setpoint"
		capability "Thermostat Mode"
		capability "Thermostat Operating State"
		//capability "Thermostat Setpoint"
		capability "Temperature Measurement"
		capability "Health Check"

		command "refresh"
		command "poll"
        
		command "offbtn"
		command "coolbtn"
		command "setThermostatMode", ["string"]
		command "levelUpDown"
		command "levelUp"
		command "levelDown"
		command "coolingSetpointUp"
		command "coolingSetpointDown"
		command "log"
		command "changeMode"
		command "setVirtualTemperature", ["number"]
		command "setCoolingStatus", ["boolean"]
		command "setEmergencyMode", ["boolean"]
		command "setCoolingOff", ["boolean"]
        
		attribute "temperatureUnit", "string"
		attribute "targetTemp", "string"
		attribute "debugOn", "string"
		attribute "safetyTempMin", "string"
		attribute "safetyTempMax", "string"
		attribute "safetyTempExceeded", "string"
	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"temperature", type:"thermostat", width:6, height:4, canChangeIcon: true) {
			tileAttribute("device.temperature", key: "PRIMARY_CONTROL") {
				attributeState("default", label:'${currentValue}°', unit: unitString())
			}
			tileAttribute("device.thermostatSetpoint", key: "VALUE_CONTROL") {
				attributeState("default", action: "levelUpDown")
				attributeState("VALUE_UP", action: "levelUp")
				attributeState("VALUE_DOWN", action: "levelDown")
			}
			tileAttribute("device.thermostatOperatingState", key: "OPERATING_STATE") {
				attributeState("idle",		backgroundColor: "#44B621")
				attributeState("cooling",	backgroundColor: "#007ee6")
				attributeState("off",		backgroundColor: "#ddcccc")
				attributeState("emergency",	backgroundColor: "#e60000")
			}
			tileAttribute("device.thermostatMode", key: "THERMOSTAT_MODE") {
				attributeState("off", label:'Off')
				attributeState("cool", label:'Cool')
			}
			tileAttribute("device.thermostatSetpoint", key: "COOLING_SETPOINT") {
				attributeState("default", label:'${currentValue}')
			}
		}
		valueTile("temp2", "device.temperature", width: 2, height: 2, decoration: "flat") {
			state("default", label:'${currentValue}°', icon:"https://raw.githubusercontent.com/eliotstocker/SmartThings-VirtualThermostat-WithDTH/master/device.png",
					backgroundColors: getTempColors(), canChangeIcon: true)
		}
		standardTile("thermostatMode", "device.thermostatMode", width:2, height:2, decoration: "flat") {
			state("off", 	action:"changeMode", nextState: "updating", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/off_icon.png")
			state("cool", 	action:"changeMode", nextState: "updating", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/cool_icon.png")
			state("Updating", label:"", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/cmd_working.png")
		}
        
		standardTile("offBtn", "device.off", width:1, height:1, decoration: "flat") {
			state("Off", action: "offbtn", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/off_icon.png")
		}
		standardTile("coolBtn", "device.canCool", width:1, height:1, decoration: "flat") {
			state("Cool", action: "coolbtn", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/cool_icon.png")
			state "false", label: ''
		}
		standardTile("refresh", "device.refresh", width:2, height:2, decoration: "flat") {
			state "Refresh", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
		valueTile("coolingSetpoint", "device.thermostatSetpoint", width: 1, height: 1) {
			state("coolingSetpoint", label:'${currentValue}', unit: unitString(), foregroundColor: "#FFFFFF",
				backgroundColors: [ [value: 0, color: "#007ee6"], [value: 7, color: "#007ee6"], [value: 15, color: "#007ee6"] ])
			state("disabled", label: '', foregroundColor: "#007ee6", backgroundColor: "#007ee6")
		}
		standardTile("coolingSetpointUp", "device.thermostatSetpoint", width: 1, height: 1, canChangeIcon: true, decoration: "flat") {
			state "default", label: '', action:"coolingSetpointUp", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/cool_arrow_up.png"
			state "", label: ''
		}
		standardTile("coolingSetpointDown", "device.thermostatSetpoint",  width: 1, height: 1, canChangeIcon: true, decoration: "flat") {
			state "default", label:'', action:"coolingSetpointDown", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/cool_arrow_down.png"
			state "", label: ''
		}
		controlTile("coolSliderControl", "device.thermostatSetpoint", "slider", height: 1, width: 3, range: getRange(), inactiveLabel: false) {
			state "default", action:"setCoolingSetpoint", backgroundColor:"#007ee6"
			state "", label: ''
		}
        
        valueTile("tempName1", "device.name1",  width: 1, height: 1, decoration: "flat") {
			state "default", label:'${currentValue}', defaultState: true
		}
        valueTile("tempName2", "device.name2",  width: 1, height: 1, decoration: "flat") {
			state "default", label:'${currentValue}', defaultState: true
		}
        valueTile("tempName3", "device.name3",  width: 1, height: 1, decoration: "flat") {
			state "default", label:'${currentValue}', defaultState: true
		}
        valueTile("tempName4", "device.name4",  width: 1, height: 1, decoration: "flat") {
			state "default", label:'${currentValue}', defaultState: true
		}
        
        valueTile("tempSensor1", "device.temp1",  width: 1, height: 1, canChangeIcon: true, decoration: "flat") {
			state "default", label:'${currentValue}°', defaultState: true
		}
        valueTile("tempSensor2", "device.temp2",  width: 1, height: 1, canChangeIcon: true, decoration: "flat") {
			state "default", label:'${currentValue}°', defaultState: true
		}
        valueTile("tempSensor3", "device.temp3",  width: 1, height: 1, canChangeIcon: true, decoration: "flat") {
			state "default", label:'${currentValue}°', defaultState: true
		}
        valueTile("tempSensor4", "device.temp4",  width: 1, height: 1, canChangeIcon: true, decoration: "flat") {
			state "default", label:'${currentValue}°', defaultState: true
		}

		main("temp2")
		details( ["temperature", "thermostatMode",
				"coolingSetpointDown", "coolingSetpoint", "coolingSetpointUp",
				"coolSliderControl", "offBtn", "coolBtn", "refresh",
                "tempName1", "tempName2", "tempName3", "tempName4",
                "tempSensor1", "tempSensor2", "tempSensor3", "tempSensor4"] )
	}
	/*preferences {
		input "resetHistoryOnly", "bool", title: "Reset History Data", description: "", displayDuringSetup: false
		input "resetAllData", "bool", title: "Reset All Stored Event Data", description: "", displayDuringSetup: false
	}*/
}

def shouldReportInCentigrade() {
	def retVal = false  //Set this to true for Centigrade, false for Fahrenheit  so that enums and colors are correct (due to ST issue of compile time evaluation)
	try {
    	def ts = getTemperatureScale();
    	retVal = ts == "C"
    } finally {
		return retVal
    }
}

def installed() {
    log.trace "Executing 'installed'"
    initialize()
    done()
}

def configure() {
    log.trace "Executing 'configure'"
    initialize()
    done()
}

private initialize() {
    log.trace "Executing 'initialize'"

    sendEvent(name:"temperature", value: defaultTemp(), unit: unitString(), displayed: false)
    sendEvent(name:"thermostatSetpoint", value: defaultTemp(), unit: unitString(), displayed: false)
    sendEvent(name: "coolingSetpoint", value: defaultTemp(), unit: unitString(), displayed: false)
  	sendEvent(name:"thermostatOperatingState", value: "off")
    sendEvent(name:"thermostatMode", value: "cool")
}

def getTempColors() {
	def colorMap
        //getTemperatureScale() == "C"   wantMetric()
	if(shouldReportInCentigrade()) {
		colorMap = [
			// Celsius Color Range
			[value: 0, color: "#153591"],
			[value: 7, color: "#1e9cbb"],
			[value: 15, color: "#90d2a7"],
			[value: 23, color: "#44b621"],
			[value: 29, color: "#f1d801"],
			[value: 33, color: "#d04e00"],
			[value: 36, color: "#bc2323"]
			]
	} else {
		colorMap = [
			// Fahrenheit Color Range
			[value: 40, color: "#153591"],
			[value: 44, color: "#1e9cbb"],
			[value: 59, color: "#90d2a7"],
			[value: 74, color: "#44b621"],
			[value: 84, color: "#f1d801"],
			[value: 92, color: "#d04e00"],
			[value: 96, color: "#bc2323"]
		]
	}
}

def unitString() {  return shouldReportInCentigrade() ? "°C": "°F" }
def defaultTemp() { return shouldReportInCentigrade() ? 20 : 70 }
def lowRange() { return shouldReportInCentigrade() ? 9 : 45 }
def highRange() { return shouldReportInCentigrade() ? 32 : 90 }
def getRange() { return "${lowRange()}..${highRange()}" }

def getTemperature() {
	return device.currentValue("temperature")
}

def setCoolingSetpoint(temp) {
    log.debug "setting temp to: $temp"
	sendEvent(name:"thermostatSetpoint", value: temp, unit: unitString())
	sendEvent(name:"coolingSetpoint", value: temp, unit: unitString())
	refresh()
	runIn(10, refresh)
}

def coolingSetpointUp() {
	def hsp = device.currentValue("thermostatSetpoint")
	setCoolingSetpoint(hsp + 1.0)
}

def coolingSetpointDown() {
	def hsp = device.currentValue("thermostatSetpoint")
	setCoolingSetpoint(hsp - 1.0)
}

def levelUp() {
	def hsp = device.currentValue("thermostatSetpoint")
    setCoolingSetpoint(hsp + 1.0)
}

def levelDown() {
    def hsp = device.currentValue("thermostatSetpoint")
    setCoolingSetpoint(hsp - 1.0)
}

private void done() {
    log.trace "---- DONE ----"
}

def ping() {
    log.trace "Executing ping"
    refresh()
}
def parse(data) {
    log.debug "parse data: $data"
}
def refresh() {
    log.trace "Executing refresh"
    sendEvent(name: "thermostatMode", value: getThermostatMode())
    sendEvent(name: "thermostatOperatingState", value: getOperatingState())
    sendEvent(name: "thermostatSetpoint", value: getThermostatSetpoint(), unit: unitString())
    sendEvent(name: "coolingSetpoint", value: getCoolingSetpoint(), unit: unitString())
    sendEvent(name: "temperature", value: getTemperature(), unit: unitString())
    done()
}
def getThermostatMode() {
	return device.currentValue("thermostatMode")
}
def getOperatingState() {
	return device.currentValue("thermostatOperatingState")
}
def getThermostatSetpoint() {
	return device.currentValue("thermostatSetpoint")
}
def getCoolingSetpoint() {
	return device.currentValue("coolingSetpoint")
}
def poll() {
}
def offbtn() {
	sendEvent(name: "thermostatMode", value: "off")
}
def coolbtn() {
	sendEvent(name: "thermostatMode", value: "cool")
}
def setThermostatMode(mode) {
    sendEvent(name: "thermostatMode", value: mode)
}
def levelUpDown() {
}
def log() {
}
def changeMode() {
	def val = device.currentValue("thermostatMode") == "off" ? "cool" : "off"
	sendEvent(name: "thermostatMode", value: val)
    return val
}
def setVirtualTemperature(temp) {
	sendEvent(name:"temperature", value: temp, unit: unitString(), displayed: false)
}
def setIndividualTemperature(temp, id, name) {
	switch(id) {
      case 0:
        sendEvent(name:"name1", value: name, displayed: false)
      	sendEvent(name:"temp1", value: temp, unit: unitString(), displayed: false)
      	break
      case 1:
        sendEvent(name:"name2", value: name, displayed: false)
      	sendEvent(name:"temp2", value: temp, unit: unitString(), displayed: false)
      	break
      case 2:
     	sendEvent(name:"name3", value: name, displayed: false)
      	sendEvent(name:"temp3", value: temp, unit: unitString(), displayed: false)
      	break
      case 3:
        sendEvent(name:"name4", value: name, displayed: false)
      	sendEvent(name:"temp4", value: temp, unit: unitString(), displayed: false)
      	break
    }
}
def clearSensorData() {
	sendEvent(name:"name1", value: null, displayed: false)
    sendEvent(name:"temp1", value: null, unit: unitString(), displayed: false)
    sendEvent(name:"name2", value: null, displayed: false)
    sendEvent(name:"temp2", value: null, unit: unitString(), displayed: false)
    sendEvent(name:"name3", value: null, displayed: false)
    sendEvent(name:"temp3", value: null, unit: unitString(), displayed: false)
    sendEvent(name:"name4", value: null, displayed: false)
    sendEvent(name:"temp4", value: null, unit: unitString(), displayed: false)
}
def setCoolingStatus(bool) {
	sendEvent(name:"thermostatOperatingState", value: bool ? "cooling" : "idle")
}
def setEmergencyMode(bool) {
    sendEvent(name: "thermostatOperatingState", value: bool ? "emergency" : "idle")
}
def setCoolingOff(bool) {
	sendEvent(name:"thermostatOperatingState", value: bool ? "off": "idle")
}
