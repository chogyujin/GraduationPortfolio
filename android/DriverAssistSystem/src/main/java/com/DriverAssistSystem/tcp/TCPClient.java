package com.DriverAssistSystem.tcp;

import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class TCPClient{

	private Context _context; 
	private static TCPClient instance = null;
	private NetworkThread _connect_thread;

	private String _ip_address;
	private int _port;

	/****************************************************************************************
	 *************************************  Singleton  **************************************
	 ****************************************************************************************/
	public TCPClient()
	{  
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); 
		StrictMode.setThreadPolicy(policy); 
		_connect_thread = new NetworkThread(true);
	}

	public static synchronized TCPClient getInstance(){
		if(null == instance){
			instance = new TCPClient();
		}
		return instance;
	}  

	/****************************************************************************************
	 *******************************  User define functions  ********************************
	 ****************************************************************************************/

	public void setContext(Context _context)
	{
		this._context = _context;
	}

	public String get_ip_address() {
		return _ip_address;
	}


	public void set_ip_address(String _ip_address) {
		this._ip_address = _ip_address;
	}


	public int get_port() {
		return _port;
	}


	public void set_port(int _port) {
		this._port = _port;
	}

	public void startclient()
	{
		_connect_thread.start();
	}
	
	public void stopsclient()
	{
		_connect_thread.set_running(false);
		_connect_thread.interrupt();

		if(_connect_thread.getSocket() != null)
			try {
				_connect_thread.getSocket().close();
				//_connect_thread.setSocket(null);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
		
		_connect_thread = new NetworkThread(true);
	}

	public boolean is_connected()
	{
		if(_connect_thread.getSocket() != null && _connect_thread.getSocket().isConnected())
		{
			return true;
		} 
		else 
			return false;
	}
 
	public void message_connect_broadcast(int _message)				
	{   
		Intent i = new Intent(Constants.INTENTFILTER_CONNECTION);  
		if(_message == 1)
			i.putExtra(Constants.INTENTEXTRA_CONNECT, _message);
		else 
			i.putExtra(Constants.INTENTEXTRA_DISCONNECT, _message);
		_context.sendBroadcast(i); 
	} 

	public void message_broadcast(String _message)				
	{   
		Intent i = new Intent(Constants.INTENTFILTER_DATA);  
		i.putExtra(Constants.INTENTEXTRA_RECEIVED_DATA, _message); 
		_context.sendBroadcast(i); 
	} 
	
	public boolean sendMessage(byte[] _bytes)
	{  
		return _connect_thread.sendMessage(_bytes);
	}  

	private class NetworkThread extends Thread {
		private Socket socket;

		public Socket getSocket() {
			return socket;
		}

		/*
		public boolean is_running() {
			return _running;
		}*/

		public void set_running(boolean _running) {
			this._running = _running;
		}

		public void setSocket(Socket socket) {
			this.socket = socket;
		}

		private boolean _running = false;

		private InputStream _inputstream;
		private OutputStream _outputstream;
		private BufferedInputStream _bufferedinputstream;
		private BufferedOutputStream _bufferedoutputstream; 

		public NetworkThread(boolean isPlay){
			this._running = isPlay;
		}

		/*
		public void stopThread(){
			_running = !_running;
		} 
*/
		public boolean sendMessage(byte[] _bytes)
		{  
			try {  
				Log.d("TCP", "C: Sending: '" + IoTUtility.byteArrayToString(_bytes) + "'");
				if(_bufferedoutputstream != null)
				{
					_bufferedoutputstream.write(_bytes);
					_bufferedoutputstream.flush();

					Log.d("TCP", "C: Sent.");
					Log.d("TCP", "C: Done.");
				}
			}catch(Exception e) {
				Log.e("TCP", "S: Error", e);
				return false;
			} 

			return true;
		} 

		@Override
		public void run() {
			try { 
				_running = true;
				InetAddress serverAddr = InetAddress.getByName(_ip_address);


				Log.d("TCP", "C: Connecting...");
				Log.d("TCP", ""+_ip_address + _port);
				socket = new Socket(serverAddr, _port);

				_inputstream = socket.getInputStream();
				_outputstream = socket.getOutputStream();
				_bufferedinputstream = new BufferedInputStream(_inputstream);
				_bufferedoutputstream = new BufferedOutputStream(_outputstream); 

				message_connect_broadcast(1);

				while(_running)
				{
					if (_bufferedinputstream.available() > 0)
					{  
						
						byte[] buffer = new byte[_bufferedinputstream.available()];
						int _size = _bufferedinputstream.read(buffer, 0, buffer.length);

						if(_size > 0)
							message_broadcast(IoTUtility.byteArrayToString(buffer)); 

						Log.d("TCP", "S: Received: '" + IoTUtility.byteArrayToString(buffer) + "'");
					}
				}
			} catch (Exception e) {
				Log.e("TCP", "C: Error", e);
				message_connect_broadcast(0);
			}
		}
	} 
}