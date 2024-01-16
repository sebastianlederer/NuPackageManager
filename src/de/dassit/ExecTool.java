package de.dassit;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class ExecTool {

	public String call(String command) throws IOException, InterruptedException {
		StringBuffer buffer=new StringBuffer();
		Process proc = Runtime.getRuntime().exec(command);

		DataInputStream in = new DataInputStream(proc.getInputStream());
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));

		String line;
		try {
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
				buffer.append('\n');
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		proc.waitFor();

		return buffer.toString();
	}	
	
	public String pipe(String command, String input) throws IOException, InterruptedException {
		if(input.length()>63) {
			throw new IOException("pipe buffer size exceeded");
		}

		StringBuffer buffer=new StringBuffer();
		Process proc = Runtime.getRuntime().exec(command);

		DataInputStream in = new DataInputStream(proc.getInputStream());
		DataOutputStream out = new DataOutputStream(proc.getOutputStream());
		
		PrintWriter writer = new PrintWriter(out);
		writer.print(input);
		writer.close();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));

		String line;
		try {
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
				buffer.append('\n');
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		proc.waitFor();

		return buffer.toString();
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		ExecTool t=new ExecTool();
		
		System.out.println(t.call("ls"));

	}

}