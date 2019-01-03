/**
 *  OSRAM 4x Switch v2
 *
 *  Copyright 2019 ditzy
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
metadata {
	definition (name: "OSRAM 4x Switch v2", namespace: "korsul", author: "ditzy") {
		//capability "Battery"
		//capability "Button"
        capability "Configuration"
        capability "Refresh"
        capability "Sensor"
        capability "Switch"
	}


	simulator {
		// TODO: define status and reply messages here
	}

	tiles (scale: 2){
		
        standardTile(name:"switch", type:"device.switch", width:6, height:4) {
            state "off", label: "off", icon: "st.switches.switch.off", backgroundColor: "#ffffff", action: "switch.on"
            state "on", label: "on", icon: "st.switches.switch.on", backgroundColor: "#00a0dc", action: "switch.off"
        }
        /*multiAttributeTile(name:"button", type:"generic", width:6, height:4) {
  			tileAttribute("device.button", key: "PRIMARY_CONTROL"){
    		attributeState "default", label:'Fibaro Button', backgroundColor:"#44b621", icon:"st.Home.home30"
            attributeState "held", label: "holding", backgroundColor: "#C390D4"
  			}
            tileAttribute ("device.battery", key: "SECONDARY_CONTROL") {
			attributeState "battery", label:'${currentValue} % battery'
            }
            
        }*/
		//standardTile("button", "device.button", width: 6, height: 4) {
		//	state "default", label: "", icon: "st.Home.home30", backgroundColor: "#ffffff"
        //    state "held", label: "holding", icon: "st.Home.home30", backgroundColor: "#C390D4"
       // } 
    	//valueTile("battery", "device.battery", width: 3, height: 2, inactiveLabel: false, decoration: "flat") {
         //tileAttribute ("device.battery", key: "PRIMARY_CONTROL"){
          // state "battery", label:'${currentValue}% battery', unit:""
        //}
        //}
        valueTile("configure", "device.button", width: 2, height: 2, decoration: "flat") {
			state "default", label: "configure", backgroundColor: "#ffffff", action: "configure", icon:"st.secondary.configure"
        }
        
        main "button"
		details(["button", "configure"])
	}
}

def configure() {
  log.debug "Confuguring Reporting and Bindings."
  zigbee.configureReporting(0x0006, 0x0000, 0x10, 0, 600, null)
  /*def configCmds = [
    // Bind Button 1 and 2. No Change from source.
    "zdo bind 0x${device.deviceNetworkId} 0x01 0x01 0x0006 {${device.zigbeeId}} {}",
    // Bind Button 3 and 4. New cluster IDs.
    "zdo bind 0x${device.deviceNetworkId} 0x01 0x01 0x0300 {${device.zigbeeId}} {}",
    // Bind the outgoing level cluster from remote to hub, so the hub receives messages when Dim Up/Down buttons pushed
    "zdo bind 0x${device.deviceNetworkId} 0x01 0x01 0x0008 {${device.zigbeeId}} {}",
   // Bind the incoming battery info cluster from remote to hub, so the hub receives battery updates
    "zdo bind 0x${device.deviceNetworkId} 0x01 0x01 0x0001 {${device.zigbeeId}} {}",
  //ToDO: Determine what other clusters are on there and how they can be used. What purpose do 0003, 0004, 0005, 0019, and 1000 have?
  ]
  return configCmds */
}

def refresh() {
  //Straight copy. Need to check device and clusterID for battery.
  def refreshCmds = [
    zigbee.readAttribute(0x0001, 0x0021)
  ]
  //when refresh button is pushed, read updated status
  return refreshCmds
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
    def zDescription = zigbee.parseDescriptionAsMap(description)
    log.debug "ZigBee parsed description: ${zDescription}"
	// TODO: handle 'battery' attribute
	// TODO: handle 'button' attribute
	// TODO: handle 'numberOfButtons' attribute
	// TODO: handle 'supportedButtonValues' attribute
	
    def evts = []
    
    /*if ( !state.numberOfButtons || state.numberOfButtons != "2" ) {
        state.numberOfButtons = "2"
    	log.debug "Number of buttons not defined. Setting to: ${state.numberOfButtons}"
        evts.add(createEvent(name: "numberOfButtons", value: state.numberOfButtons, displayed: false))
    }
    
    if ( !state.supportedButtonValues ) {
    	state.supportedButtonValues = [ "pushed", "held" ]
        log.debug "Supported button values not defined. Setting to: ${state.supportedButtonValues}"
        evts.add(createEvent(name: "supportedButtonValues", value: state.supportedButtonValues, displayed: false))
    }*/

	if ( !zDescription ) {
    	log.debug "Description not successfully parsed as zigbee event."
        return
    }
    
    if ( zDescription.clusterInt == 0x0006 ) {
    	log.debug "Button event"
        evts.addAll(handleButtonEvent(zDescription))
    }
    
    log.debug "Events: ${evts}"
    return evts
}

private List handleButtonEvent(event) {

	if ( event.sourceEndpoint == "01" ) {
    	log.debug "Button 1"
        return [createEvent(name: "switch", value: "on", descriptionText: "$device.displayName on button was pushed", isStateChange: true)]
        //return [createEvent(name: "button", value: "pushed", data: [buttonNumber: 1], descriptionText: "$device.displayName button 1 was pushed", isStateChange: true)]
    }

	if ( event.sourceEndpoint == "03" ) {
    	log.debug "Button 2"
        return [createEvent(name: "switch", value: "off", descriptionText: "$device.displayName off button was pushed", isStateChange: true)]
        //return [createEvent(name: "button", value: "pushed", data: [buttonNumber: 2], descriptionText: "$device.displayName button 2 was pushed", isStateChange: true)]
    }
    
    return []
}

def on() {
	log.debug "Command: On"
}

def off() {
	log.debug "Command: Off"
}
