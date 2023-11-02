<?php
defined('BASEPATH') OR exit('No direct script access allowed');
?><!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
 	<link rel="stylesheet" href="/jquery//jquery-ui.css">
 	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css">
	<link href="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/css/toastr.min.css" rel="stylesheet">
	
	<style type="text/css">
	.error_dialog.ui-dialog-titlebar {
    	background-color: rgb(220, 0, 0);
    	color: white;
    }
	</style>
</head>
<body onbeforeunload="return cleanup()">
<!-- Guacamole -->
    
<div id="dialog" title="Connect to device" >
	<p/>
    <div class="spinner-border" role="status">
      <span class="sr-only">Connecting...</span>
    </div>
      &nbsp;Connecting...
      <button type="button" class="btn btn-primary btn-sm" id="closeConnect" style="float:right;" >Cancel</button>
</div>

<div id="error_dialog" class="error_dialog" title="<?php echo lang('error');?>" >
	<p/>
    <div id="error_message"></div>
    <p/>
      <button type="button" class="btn btn-primary btn-sm" id="retryConnect" style="float:right;" >Retry</button>
</div>

    <!-- Display -->
    <div id="display" style='width: 100%'></div>
    
	<script src="/jquery/jquery.js"></script>
    <script src="/jquery/jquery-ui.min.js"></script>
	<script type="text/javascript" src="/gkl.all.min.js"></script>
<!-- 	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/js/toastr.min.js"></script> -->

    <!-- Init -->
    <script type="text/javascript">

    function sleep(delay) {
		const start = new Date().getTime();
		while(new Date().getTime() < start + delay);
    } 
    

    HTTPTunnel = function (tunnelURL, crossDomain) {
        
        /**
         * Reference to this HTTP tunnel.
         * @private
         */
        var tunnel = this;
        
        var tunnel_uuid;
        
        var TUNNEL_CONNECT = tunnelURL + "?connect";
        var TUNNEL_READ = tunnelURL + "?read:";
        var TUNNEL_WRITE = tunnelURL + "?write:";
        
        var POLLING_ENABLED = 1;
        var POLLING_DISABLED = 0;
        
        // Default to polling - will be turned off automatically if not needed
        var pollingMode = POLLING_ENABLED;
        
        var sendingMessages = false;
        var outputMessageBuffer = "";
        var cursending = "";
		var forceSend = false;

        
        // If requests are expected to be cross-domain, the cookie that the HTTP
        // tunnel depends on will only be sent if withCredentials is true
        var withCredentials = !!crossDomain;
        
        /**
         * The current receive timeout ID, if any.
         * @private
         */
        var receive_timeout = null;
        
        /**
         * Initiates a timeout which, if data is not received, causes the tunnel
         * to close with an error.
         *
         * @private
         */
        function reset_timeout() {
            // Get rid of old timeout (if any)
            window.clearTimeout(receive_timeout);
            
            // Set new timeout
            receive_timeout = window.setTimeout(function () {
                close_tunnel(new Guacamole.Status(Guacamole.Status.Code.UPSTREAM_TIMEOUT, "Server timeout."));
            }, tunnel.receiveTimeout);
                
        }
        
        /**
         * Closes this tunnel, signaling the given status and corresponding
         * message, which will be sent to the onerror handler if the status is
         * an error status.
         *
         * @private
         * @param {Guacamole.Status} status The status causing the connection to
         *                                  close;
         */
        function close_tunnel(status) {
            // Ignore if already closed
            if (tunnel.state === Guacamole.Tunnel.State.CLOSED)
                return;
                
                // If connection closed abnormally, signal error.
                if (status.code !== Guacamole.Status.Code.SUCCESS && tunnel.onerror) {
                    
                    // Ignore RESOURCE_NOT_FOUND if we've already connected, as that
                    // only signals end-of-stream for the HTTP tunnel.
                    if (tunnel.state === Guacamole.Tunnel.State.CONNECTING
                        || status.code !== Guacamole.Status.Code.RESOURCE_NOT_FOUND)
                        tunnel.onerror(status);
                        
                }
                
                // Mark as closed
                tunnel.state = Guacamole.Tunnel.State.CLOSED;
                
                // Reset output message buffer
                sendingMessages = false;
                
                if (tunnel.onstatechange)
                    tunnel.onstatechange(tunnel.state);
                    
        }
        
        
        this.sendMessage = function () {
            
            // Do not attempt to send messages if not connected
            if (tunnel.state !== Guacamole.Tunnel.State.OPEN)
                return;
                
                // Do not attempt to send empty messages
                if (arguments.length === 0)
                    return;
                    
                    /**
                     * Converts the given value to a length/string pair for use as an
                     * element in a Guacamole instruction.
                     *
                     * @private
                     * @param value The value to convert.
                     * @return {String} The converted value.
                     */
                    function getElement(value) {
                        var string = new String(value);
                        return string.length + "." + string;
                    }
                    
                    // Initialized message with first element
                    var message = getElement(arguments[0]);
                    
                    // Append remaining elements
                    for (var i = 1; i < arguments.length; i++)
                        message += "," + getElement(arguments[i]);
                        
                        // Final terminator
                        message += ";";
                        
                        // Add message to buffer
                        outputMessageBuffer += message;
                        
                        // Send if not currently sending
                        if (tunnel.forceSend || !sendingMessages)
                            sendPendingMessages();
                            
        };
        
        function sendPendingMessages() {
            
            // Do not attempt to send messages if not connected
            if (tunnel.state !== Guacamole.Tunnel.State.OPEN)
                return;
                
                if (outputMessageBuffer.length > 0) {
                    
                    sendingMessages = true;
                    
                    var message_xmlhttprequest = new XMLHttpRequest();
                    message_xmlhttprequest.open("POST", TUNNEL_WRITE + tunnel_uuid);
                    message_xmlhttprequest.withCredentials = withCredentials;
                    message_xmlhttprequest.setRequestHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
                    
                    // Once response received, send next queued event.
                    message_xmlhttprequest.onreadystatechange = function () {
                        if (message_xmlhttprequest.readyState === 4) {
                            
                            // If an error occurs during send, handle it
                            if (message_xmlhttprequest.status !== 200)
                                handleHTTPTunnelError(message_xmlhttprequest);
                                
                                // Otherwise, continue the send loop
                                else
                                    sendPendingMessages();
                                    
                        }
                    };
                    
                    message_xmlhttprequest.send(outputMessageBuffer);
//                     console.log(outputMessageBuffer);
					tunnel.cursending = outputMessageBuffer;

                    outputMessageBuffer = ""; // Clear buffer
                    
                } else
                    sendingMessages = false;
                    
        }
        
        function handleHTTPTunnelError(xmlhttprequest) {

        	var code = parseInt(xmlhttprequest.getResponseHeader("Guacamole-Status-Code"));
            var message = xmlhttprequest.getResponseHeader("Guacamole-Error-Message");

			console.log("handleHTTPTunnelError");
			
            close_tunnel(new Guacamole.Status(code, message));
        }
        
        function handleResponse(xmlhttprequest) {
            
            var interval = null;
            var nextRequest = null;
            
            var dataUpdateEvents = 0;
            
            // The location of the last element's terminator
            var elementEnd = -1;
            
            // Where to start the next length search or the next element
            var startIndex = 0;
            
            // Parsed elements
            var elements = new Array();
            
            function parseResponse() {
                
                // Do not handle responses if not connected
                if (tunnel.state !== Guacamole.Tunnel.State.OPEN) {
                    
                    // Clean up interval if polling
                    if (interval !== null)
                        clearInterval(interval);
                        
                        return;
                }
                
                // Do not parse response yet if not ready
                if (xmlhttprequest.readyState < 2)
                    return;
                    
                    // Attempt to read status
                    var status;
                    try {
                        status = xmlhttprequest.status;
                    }
                    
                    // If status could not be read, assume successful.
                    catch (e) {
                        status = 200;
                    }
                    
                    // Start next request as soon as possible IF request was successful
                    if (!nextRequest && status === 200)
                        nextRequest = makeRequest();
                        
                        // Parse stream when data is received and when complete.
                        if (xmlhttprequest.readyState === 3 ||
                            xmlhttprequest.readyState === 4) {
                                
                                reset_timeout();
                                
                                // Also poll every 30ms (some browsers don't repeatedly call onreadystatechange for new data)
                                if (pollingMode === POLLING_ENABLED) {
                                    if (xmlhttprequest.readyState === 3 && !interval)
                                        interval = setInterval(parseResponse, 30);
                                        else if (xmlhttprequest.readyState === 4 && !interval)
                                            clearInterval(interval);
                                }
                                
                                // If canceled, stop transfer
                                if (xmlhttprequest.status === 0) {
                                    tunnel.disconnect();
                                    return;
                                }
                                
                                // Halt on error during request
                                else if (xmlhttprequest.status !== 200) {
                                    handleHTTPTunnelError(xmlhttprequest);
                                    return;
                                }
                                
                                // Attempt to read in-progress data
                                var current;
                                try {
                                    current = xmlhttprequest.responseText;
                                }
                                
                                // Do not attempt to parse if data could not be read
                                catch (e) {
                                    return;
                                }
                                
                                // While search is within currently received data
                                while (elementEnd < current.length) {
                                    
                                    // If we are waiting for element data
                                    if (elementEnd >= startIndex) {
                                        
                                        // We now have enough data for the element. Parse.
                                        var element = current.substring(startIndex, elementEnd);
                                        var terminator = current.substring(elementEnd, elementEnd + 1);
                                        
                                        // Add element to array
                                        elements.push(element);
                                        
                                        // If last element, handle instruction
                                        if (terminator === ";") {
                                            
                                            // Get opcode
                                            var opcode = elements.shift();
                                            
                                            // Call instruction handler.
                                            if (tunnel.oninstruction)
                                                tunnel.oninstruction(opcode, elements);
                                                
                                                // Clear elements
                                                elements.length = 0;
                                                
                                        }
                                        
                                        // Start searching for length at character after
                                        // element terminator
                                        startIndex = elementEnd + 1;
                                        
                                    }
                                    
                                    // Search for end of length
                                    var lengthEnd = current.indexOf(".", startIndex);
                                    if (lengthEnd !== -1) {
                                        
                                        // Parse length
                                        var length = parseInt(current.substring(elementEnd + 1, lengthEnd));
                                        
                                        // If we're done parsing, handle the next response.
                                        if (length === 0) {
                                            
                                            // Clean up interval if polling
                                            if (!interval)
                                                clearInterval(interval);
                                                
                                                // Clean up object
                                                xmlhttprequest.onreadystatechange = null;
                                                xmlhttprequest.abort();
                                                
                                                // Start handling next request
                                                if (nextRequest)
                                                    handleResponse(nextRequest);
                                                    
                                                    // Done parsing
                                                    break;
                                                    
                                        }
                                        
                                        // Calculate start of element
                                        startIndex = lengthEnd + 1;
                                        
                                        // Calculate location of element terminator
                                        elementEnd = startIndex + length;
                                        
                                    }
                                    
                                    // If no period yet, continue search when more data
                                    // is received
                                    else {
                                        startIndex = current.length;
                                        break;
                                    }
                                    
                                } // end parse loop
                                
                            }
                            
            }
            
            // If response polling enabled, attempt to detect if still
            // necessary (via wrapping parseResponse())
            if (pollingMode === POLLING_ENABLED) {
                xmlhttprequest.onreadystatechange = function () {
                    
                    // If we receive two or more readyState==3 events,
                    // there is no need to poll.
                    if (xmlhttprequest.readyState === 3) {
                        dataUpdateEvents++;
                        if (dataUpdateEvents >= 2) {
                            pollingMode = POLLING_DISABLED;
                            xmlhttprequest.onreadystatechange = parseResponse;
                        }
                    }
                    
                    parseResponse();
                };
            }
            
            // Otherwise, just parse
            else
                xmlhttprequest.onreadystatechange = parseResponse;
                
                parseResponse();
                
        }
        
        /**
         * Arbitrary integer, unique for each tunnel read request.
         * @private
         */
        var request_id = 0;
        
        function makeRequest() {
            
            // Make request, increment request ID
            var xmlhttprequest = new XMLHttpRequest();
            xmlhttprequest.open("GET", TUNNEL_READ + tunnel_uuid + ":" + (request_id++));
            xmlhttprequest.withCredentials = withCredentials;
            xmlhttprequest.send(null);
            
            return xmlhttprequest;
            
        }
        
        this.connect = function (data) {
            // Start waiting for connect
            reset_timeout();
            
            // Start tunnel and connect
            var connect_xmlhttprequest = new XMLHttpRequest();
            connect_xmlhttprequest.onreadystatechange = function () {
                if (connect_xmlhttprequest.readyState !== 4)
                    return;
                    
                    // If failure, throw error
                    if (connect_xmlhttprequest.status !== 200) {
                        handleHTTPTunnelError(connect_xmlhttprequest);
                        return;
                    }
                    
                    reset_timeout();
                    
                    // Get UUID from response
                    tunnel_uuid = connect_xmlhttprequest.responseText;
                    
                    tunnel.state = Guacamole.Tunnel.State.OPEN;
                    if (tunnel.onstatechange)
                        tunnel.onstatechange(tunnel.state);
                        
                        // Start reading data
                        handleResponse(makeRequest());
                        
            };
            
            url = TUNNEL_CONNECT;
            url += "&address=<?php echo $serverLocalAdr ?>&port=" + this.port + "&colorDepth=" + this.colorDepth;
//             console.log(url);
            connect_xmlhttprequest.open("POST", url, true);
            connect_xmlhttprequest.withCredentials = withCredentials;
            connect_xmlhttprequest.setRequestHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
            connect_xmlhttprequest.send(data);
        };
        
        this.disconnect = function () {
            tunnel.forceSend = true;
            tunnel.sendMessage("disconnect");
            
            close_tunnel(new Guacamole.Status(Guacamole.Status.Code.SUCCESS, "Manually closed."));
            window.clearTimeout(receive_timeout);
        };

        this.port = null;
        this.colorDepth = 16;
		this.disconnecting = false;
        
    };
    
    HTTPTunnel.prototype = new Guacamole.Tunnel();
    
    function getParameterByName(name, url) {
        if (!url)
            url = window.location.href;
            name = name.replace(/[\[\]]/g, "\\$&");
            var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
            results = regex.exec(url);
            if (!results)
                return null;
                if (!results[2])
                    return '';
                    return decodeURIComponent(results[2].replace(/\+/g, " "));
    }


    // Get display div from document
    var webSocket;
    var tryRecconect = 0;
    var displayElement = document.getElementById("display");
    var guacElement;    
    var tunnel;
    var guac;

	function closeTunnel() {
        if(tunnel != null) {
    		tunnel.disconnecting = true;
    		tunnel.forceSend = true;
            tunnel.sendMessage("disconnect");
            
           	sleep(300);
        }
	}
    
    function cleanup() {
        if(webSocket) {
            webSocket.close();
        }


        closeTunnel();
        return null;
    }

    window.onresize = function updateScale() {
		if(guac) {
            var disp = guac.getDisplay();
            if(disp.getWidth() && disp.getHeight()) {
                var scale = Math.min(window.innerWidth/disp.getWidth(), window.innerHeight / disp.getHeight());
                disp.scale(scale);
            }
		}
    }

//     tunnel.onstatechange = function(data) {
//         if(!tunnel.disconnecting && data !== Guacamole.Tunnel.State.OPEN) {
//        		if(tryRecconect++ < 3 ) {
// 				console.log("try connect (" + tryConnect + ")...");
// 				sleep(500);
// 		    	establishVNCConnection();
//             } else {
//                 showError("Can't establish connect to device.");
//             }
//         }
//     };
//     guac.onerror = function (error) {
//         alert(error);
//     };

    
    function doConnect(port, colorDepth) {
        console.log("Do connect");
        
        if(guacElement) {
        	displayElement.removeChild(guacElement);
        	$('#dialog').dialog('open');
        }

        tunnel = new HTTPTunnel("<?php echo $TUNNEL_ADDRESS;?>");
        guac = new Guacamole.Client(tunnel);
        guacElement = guac.getDisplay().getElement()
        displayElement.appendChild(guacElement);
        
        tryReconnect = 0;


//     guac.onerror = function (error) {
//	console.log(error);
//     };

        tunnel.disconnecting = false;
    	tunnel.port = port;
    	tunnel.colorDepth = colorDepth;
    	tunnel.onstatechange = function(data) {
        	if(data == Guacamole.Tunnel.State.OPEN) {
        		closeDialog();
//        	} else if(!tunnel.disconnecting && data !== Guacamole.Tunnel.State.OPEN) {
//           		if(tryRecconect++ < 3 ) {
//    				console.log("try connect (" + tryConnect + ")...");
//    				sleep(300);
//    		    	establishVNCConnection();
//                } else {
//                    showError("Can't establish connect to device.");
//                }
    	    }
    	}

        // Mouse
        var display = guac.getDisplay();
        var displayContainer = display.getElement();
        var mouse = new Guacamole.Mouse(displayContainer);
        mouse.onmousedown =
        mouse.onmouseup =
        mouse.onmousemove = function (mouseState) {

        	startIdleCount();
            
            // Scale event by current scale
            var scaledState = new Guacamole.Mouse.State(
                mouseState.x / display.getScale(),
                mouseState.y / display.getScale(),
                mouseState.left,
                mouseState.middle,
                mouseState.right,
                mouseState.up,
                mouseState.down);
            guac.sendMouseState(scaledState);
        };
        
        // Touch screen
        var touchScreen = new Guacamole.Mouse.Touchscreen(displayContainer);
        touchScreen.onmousedown =
        touchScreen.onmouseup =
        touchScreen.onmousemove = function (mouseState) {

        	startIdleCount();    	
            
            // Scale event by current scale
            var scaledState = new Guacamole.Mouse.State(
                mouseState.x / display.getScale(),
                mouseState.y / display.getScale(),
                mouseState.left,
                mouseState.middle,
                mouseState.right,
                mouseState.up,
                mouseState.down);
            guac.sendMouseState(scaledState);
        };
        
        // Keyboard
        var keyboard = new Guacamole.Keyboard(document);
        keyboard.onkeydown = function (keysym) {
        	startIdleCount();
        	guac.sendKeyEvent(1, keysym);
        };
        keyboard.onkeyup = function (keysym) {
            guac.sendKeyEvent(0, keysym);
        };

        guac.connect();
    	startIdleCount();
    }

    function makePacket(command) {
    	var bytes = longToByteArray(0x474B4C53);
    	bytes = bytes.concat(longToByteArray(curUser));
    	bytes = bytes.concat(longToByteArray(curDevice));
    	bytes = bytes.concat(longToByteArray(command));
    	bytes = bytes.concat(longToByteArray(0)); //CRC
    	return  new Uint8Array(bytes);
    }

    longToByteArray = function(/*long*/longVal) {
        // we want to represent the input as a 4-bytes array
        var byteArray = [0, 0, 0, 0];

        for ( var index = 0; index < byteArray.length; index ++ ) {
            var byteVal = longVal & 0xff;
            byteArray [ index ] = byteVal;
            longVal = (longVal - byteVal) / 256 ;
        }

        return byteArray;
    };

    byteArrayToLong = function(/*byte[]*/byteArray) {
        var value = 0;
        for ( var i = byteArray.length - 1; i >= 0; i--) {
            value = (value * 256) + byteArray[i] * 1;
        }

        return value;
    };

    function closeDialog() {
    	$('#dialog').dialog("close");
    }

    
    var CMD_PHP_VNC_REQ = 100;
    var CMD_PHP_VNC_CANCEL = 101;
    var CMD_PHP_VNC_ACCEPT = 102;
    var CMD_PHP_VNC_FAIL = 103;
    var CMD_PHP_VNC_RESTART = 104;

    var port = 0;
    var curUser = <?php echo $uid; ?>;
    var curDevice = <?php echo $device; ?>;
//	var connStr = "ws://<?php echo $serverAdr;?>:<?php echo $serverPort?>/connect";
	var connStr = "<?php echo base_url("/connect/", "ws"); ?>";
if(window.location.protocol == "https:") {
    connStr = connStr.replace("ws:","wss:");
}

   	function showError(errorMessage) {
   		$('#error_message').html(errorMessage);
   		$('#error_dialog').dialog('open');
   	}    


    function establishVNCConnection() {
        if(webSocket != null) {
    		var data = makePacket(CMD_PHP_VNC_REQ);
    		webSocket.send(data);
            return;
        } else {
        	$('#dialog').dialog('open');
        }
        
    	webSocket = new WebSocket(connStr); 
    	webSocket.onopen = function() {
    		var data = makePacket(CMD_PHP_VNC_REQ);
    		webSocket.send(data);
    	};
    	webSocket.onclose = function(event) {
    		webSocket = null;
    		closeDialog();
    	};

    	webSocket.onmessage = function(event) {

    		if(event.data.size > 0) {
        		var fr = new FileReader();
        		fr.readAsArrayBuffer(event.data);
        		fr.onloadend = function() {
            		var data = new Uint32Array(fr.result.slice(0, 24));
//             		console.log(data);
            		var cmd = data[3];
            		if( cmd == CMD_PHP_VNC_FAIL ) { 
			    		if(webSocket)
            				webSocket.close();
            			
                	    var dec = new TextDecoder("utf-8");
                	    var u8Array = new Uint8Array(fr.result.slice(24));
                	    var message = dec.decode(u8Array);
                	    showError(message);
            		} else if(cmd == CMD_PHP_VNC_ACCEPT) {
                		data = new Uint32Array(fr.result.slice(24, 28));
                 		console.log(data);
    					port = data[0];
    	        		doConnect(port, 16);
            		} else if(cmd == CMD_PHP_VNC_RESTART) {
						closeTunnel();
                		var data = makePacket(CMD_PHP_VNC_REQ);
                		webSocket.send(data);
                	}
        		};
    		}
    	};

    	webSocket.onerror = function(error) {
    		webSocket.close();

    		var errMessage = "<?php echo lang('cant_connect_srv');?>";
    		showError(errMessage);
    	};   
   	}

    $( document ).ready(function() {
        
    	$('#dialog').dialog({
    		dialogClass: "no-close",
    		autoOpen: false,
    		modal: true,
    		title: "Connect to device " + <?php echo $device; ?>
    	});

    	$('#error_dialog').dialog({
    		autoOpen: false,
    		modal: true,
    		classes: {
    			"ui-dialog-titlebar" : "error_dialog"
    		}
    	});

    	$('#retryConnect').on('click', function() {
    		$('#error_dialog').dialog('close');
        	establishVNCConnection();
    	});
    	
    	
//     	console.log(connStr);
    	establishVNCConnection();

    	$('#closeConnect').on('click', function() {
    		if(webSocket != null) {
    			data = makePacket(CMD_PHP_VNC_CANCEL);
    			webSocket.send(data);
    			webSocket.close();
    		} else {
    			closeDialog();
    		}
    	});
  });
        
	var idleCount = null;

	function stopIdleCount() {
		if(idleCount)
			clearTimeout(idleCount);
	}

	var IDLE_TIMEOUT = 180;
	function startIdleCount() {
		stopIdleCount();


// 		if(tunnel.state === Guacamole.Tunnel.State.CLOSED) {
// 			idleCount = null;
// 		} else {
    		idleCount = setTimeout(function() {
				closeTunnel();
				if(webSocket != null) {
	    			webSocket.close();
	    			webSocket = null;
				}
									
    			showError("The system is idle " + IDLE_TIMEOUT + " seconds. It's disconnected now");
    		} , IDLE_TIMEOUT * 1000);
// 		}
	}

    </script>
    </body>
    </html>
    