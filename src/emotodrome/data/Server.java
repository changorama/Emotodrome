package emotodrome.data;

import java.io.*;
import java.util.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import javax.net.SocketFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import emotodrome.mesh.Vec3;
import emotodrome.user.User;

import android.util.Log;

//TODO server sends id at start, server sends id request upon opening an output socket with client, client sends id to server

public class Server {
	//	private final String INTERACT_URL = "http://emotodrome.com/scripts/interact.php";
	private static final String INTERACT_URL = "http://darrenlincheng.com/emoto/scripts/interact.php";
	private static final String DATA_URL = "http://darrenlincheng.com/emoto/";
	private String android_id;
	private double longitude, latitude;
	private int zoom;
	private String serverResponse;
	private SocketChannel out_socket = null;
	private Socket in_socket = null;
	private Queue<LocationValuePair> addQueue;
	private Queue<Vec3> remQueue;
	public boolean connected = true;
	private BufferedReader in = null;

	public Server(String android_id, double longitude, double latitude, int zoom) {
		this.android_id = android_id;
		this.longitude = longitude;
		this.latitude = latitude;
		this.zoom = zoom;
		serverResponse = "";
		addQueue = new LinkedList<LocationValuePair>();
		remQueue = new LinkedList<Vec3>();
		listenSocket();
	}
	
	public void listenSocket(){
		//Create socket connection
		InetSocketAddress ip = null;
		try {
		   ip = new InetSocketAddress("129.170.22.61", 3455);
		   //localip = InetAddress.getByName(getLocalIpAddress());
		   //localip = InetAddress.getLocalHost();
	       //socket = SocketFactory.getDefault().createSocket(ip, 3455, localip, 2024);
	       //socket = SocketFactory.getDefault().createSocket(ip, 3455, localip, 3024);
		   out_socket = SocketChannel.open();
		   out_socket.configureBlocking(false);
		   out_socket.connect(ip);
		   while(out_socket.isConnectionPending()){
			   out_socket.finishConnect();
		   }
		   try {
			Thread.sleep(10);
		   } catch (InterruptedException e) {
			   // TODO Auto-generated catch block
			   e.printStackTrace();
		   }
	       in_socket = SocketFactory.getDefault().createSocket("129.170.22.61", 3465);
    	   //out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.socket().getOutputStream())), true);
    	   in  = new BufferedReader(new InputStreamReader(in_socket.getInputStream()));
    	   connected = true;
	    	   
	    } catch (UnknownHostException e) {
	       System.out.println("Unknown host: " + ip.getHostName());
	    }catch  (IOException e) {
	       System.out.println("No I/O");
	     }
	}
	
	public String getLocalIpAddress() {
	    try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                if (!inetAddress.isLoopbackAddress()) {
	                    return inetAddress.getHostAddress().toString();
	                }
	            }
	        }
	    } catch (SocketException ex) {
	        Log.e("SOCKERR", ex.toString());
	    }
	    return null;
	}
	
	public void sendToServer(String s){
		if (out_socket.isConnected() && connected)
			try {
				CharsetEncoder enc = Charset.forName("US-ASCII").newEncoder();
				out_socket.write(enc.encode(CharBuffer.wrap(s)));
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public static String getFileFromServer(String filename){
		BufferedInputStream in;
		try {
			in = new BufferedInputStream(new URL(DATA_URL + filename).openStream());
			byte[] buffer = new byte[1024];
			in.read(buffer);
			return new String(buffer);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	public int receiveIceDataFile(FileOutputStream file){
		PrintWriter out = new PrintWriter(file);
		while (connected){
			String line = "";
			try {
				if (in != null){
					line = in.readLine();
					System.out.println("RECEIVED " + line);
					if (line.startsWith("85,360")){
						out.print(line);
						out.close();
						return 0;
					}
					else {
						out.print(line + "\n");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				return -1;
			}
		}
		return -1;
	}

	public void runServerLoop(double longitude, double latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
		try {
			InputStream content = queryURL(INTERACT_URL + "?id=" + android_id +
					"&lng=" + this.longitude + "&lat=" + this.latitude + "&mv=99&z=" + zoom);
			parseInputStream(content);
		} catch (IOException e) {e.printStackTrace();}
	}


	//Query URL and open and return input stream
	private InputStream queryURL(String url) throws IOException {
		InputStream content = null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response = httpclient.execute(new HttpGet(url));
		content = response.getEntity().getContent();
		return content;
	}
	
	public String getServerResponse() {
		return serverResponse;
	}

	//parse input stream and close it 
	private void parseInputStream(InputStream content) throws IOException {
		//Retrieve Content
		InputStreamReader reader = new InputStreamReader(content);
		BufferedReader in = new BufferedReader(reader,15000);
		System.out.println("buffer reader " + in);
		String str;
		serverResponse = "";
		System.out.println("Server Return: \"");
		if((str = in.readLine()) != null) {
			System.out.println("\t" + str);
			serverResponse += str;
		}
		System.out.println("\"");
		content.close();
		in.close();
	}
	
	public void listenToServer(HashMap<Integer, User> users){
		while (connected){
			String line = "";
			try {
				if (in != null){
					line = in.readLine();
					System.out.println("RECEIVED " + line);
				}
				if (line != null && !line.equals("") && line.contains(",")){
					String[] values = line.split(",");
					String request = values[0];
					if (request.equals("LOC")){
						int key = Integer.valueOf(values[1]);
						float x = Float.valueOf(values[2]);
						float y = Float.valueOf(values[3]);
						float z = Float.valueOf(values[4]);
						Vec3 userVector = new Vec3(x,y,z);
						if (users.containsKey(key)){
							users.get(key).setUserVector(userVector);
						}
						else{
							User newUser = new User(userVector);
							users.put(key, newUser);
						}
					}
					else if (request.equals("LALO")){
						int key = Integer.valueOf(values[1]);
						float lat = Float.valueOf(values[2]);
						float lon = Float.valueOf(values[3]);
						if (users.containsKey(key)){
							User u = users.get(key);
							u.setLat(lat);
							u.setLon(lon);
						}
					}
					else if (request.equals("ADDICE")){
						int lat = Integer.valueOf(values[1]);
						int lon = Integer.valueOf(values[2]);
						float icePercentage = Float.valueOf(values[3]);
						addQueue.add(new LocationValuePair(new Vec3(lat, 0, lon), icePercentage));
					}
					else if (request.equals("REMICE")){
						int lat = Integer.valueOf(values[1]);
						int lon = Integer.valueOf(values[2]);
						remQueue.add(new Vec3(lat, 0, lon));
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public Queue<LocationValuePair> getAddQueue(){
		return addQueue;
	}
	
	public Queue<Vec3> getRemQueue(){
		return remQueue;
	}

	public class ServerListener implements Runnable{
		HashMap<Integer, User> users;
		public ServerListener(HashMap<Integer, User> users){
			this.users = users;
		}
		@Override
		public void run() {
			listenToServer(users);
		}
	}

	public void close() throws IOException, InterruptedException {
		connected = false;
		Thread.sleep(50);
		if (out_socket != null && out_socket.isOpen()){
			out_socket.close();
		}
		if (in_socket != null && !in_socket.isClosed()){
			if (in != null){
				in.close();
			}
			in_socket.close();
		}
		
	}
}

