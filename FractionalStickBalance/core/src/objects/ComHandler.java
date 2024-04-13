package objects;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;

import com.badlogic.gdx.Gdx;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class ComHandler implements Runnable, SerialPortEventListener {

	InputStream in;
	static OutputStream output;
	BufferedReader input;
	SerialPort serialPort;
	public double data;

	private static final String PORT_NAMES[] = { "/dev/cu.usbmodem411", "/dev/cu.usbmodem621", // Mac OS X port2
			"/dev/ttyACM0", // Ubuntu
			"COM1","COM2","COM3","COM4","COM5","COM6","COM7","COM8" // Windows
	};

	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 115200;
	//private static final double quant = 0.05;

	public ComHandler() {
		this.data = 0;
	}

	/*
	 * Connect to serial port
	 */
	@SuppressWarnings("rawtypes")
	public void connect(String portname) throws Exception {

		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		// First, Find an instance of serial port as set in PORT_NAMES.
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					break;
				}
			}
			if (currPortId.getName().equals(portname)) {
				portId = currPortId;
				break;
			}
		}
		if (portId == null) {
			System.out.println("Could not find COM port.");
			return;
		}

		if (portId.isCurrentlyOwned()) {
			Gdx.app.log("Error: ", "Port is currently in use");
		} else {

			CommPort commPort = portId.open(this.getClass().getName(), TIME_OUT);

			if (commPort instanceof SerialPort) {
				serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
						SerialPort.PARITY_NONE);

				in = serialPort.getInputStream();
				output=serialPort.getOutputStream();

				input = new BufferedReader(new InputStreamReader(in));

				serialPort.addEventListener(this);
				serialPort.notifyOnDataAvailable(true);

				(new Thread(new ComHandler())).start();

			} else {
				Gdx.app.log("Error:", " Only serial ports are handled by this example.");
			}
		}
	}

	/*
	 * Close communication
	 */
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
			Gdx.app.log("", " serial port closed");
		}
	}

	/*
	 * Check avaliable ports
	 */
	@SuppressWarnings("rawtypes")
	public void checkPorts() {
		Enumeration ports = CommPortIdentifier.getPortIdentifiers();
		Gdx.app.log(".......", "......");
		while (ports.hasMoreElements()) {
			CommPortIdentifier port = (CommPortIdentifier) ports.nextElement();
			String type;
			switch (port.getPortType()) {
			case CommPortIdentifier.PORT_PARALLEL:
				type = "Parallel";
				break;
			case CommPortIdentifier.PORT_SERIAL:
				type = "Serial";
				break;
			default: /// Shouldn't happen
				type = "Unknown";
				break;
			}
			Gdx.app.log((port.getName() + ": " + type), "");
			Gdx.app.log(".......", "......");
		}

	}
	
	public synchronized static void writeData(String c) {
		//System.out.println("Sent: " + data);
		try {
			output.write(c.getBytes());
		} catch (Exception e) {
			//System.out.println("could not write to port");
		}
	}

/*
 * Implementations
 * @see java.lang.Runnable#run()
 */
	@Override
	public void run() {
		// keep app alive for 1000 seconds,
		// waiting for events to occur and responding to them (printing incoming
		// messages to console).
		try {
			Thread.sleep(TIME_OUT);
		} catch (InterruptedException ie) {

		}
	}

	@Override
	public void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				String inputLine = input.readLine();
				//this.data = quant*(int)(Double.parseDouble(inputLine)/quant);
				this.data = (Double.parseDouble(inputLine));
			} catch (Exception e) {
			}
		}
	}


}
