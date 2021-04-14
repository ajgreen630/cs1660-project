import java.util.*;
import java.io.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextArea;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.Bucket;
import com.google.api.gax.paging.Page;

// For submitting a Dataproc jobs and Hadoop jobs:
import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.dataproc.v1.Job;
import com.google.cloud.dataproc.v1.JobControllerClient;
import com.google.cloud.dataproc.v1.JobControllerSettings;
import com.google.cloud.dataproc.v1.JobMetadata;
import com.google.cloud.dataproc.v1.JobPlacement;
import com.google.cloud.dataproc.v1.HadoopJob;


import com.google.cloud.storage.Blob;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.common.collect.ImmutableList;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.dataproc.v1.Job;
import com.google.cloud.dataproc.v1.JobControllerClient;
import com.google.cloud.dataproc.v1.JobControllerSettings;
import com.google.cloud.dataproc.v1.JobMetadata;
import com.google.cloud.dataproc.v1.JobPlacement;
import com.google.cloud.dataproc.v1.SparkJob;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {
	
	static JFrame mainFrame = new JFrame("Alexi Green Search Engine");
	static JFileChooser fileChooser = null;
	static File[] selectedFiles = null;
	
	public static void main(String[] args) {
		//authImplicit();
		
		/*try {
			Process process = Runtime.getRuntime().exec("gcloud beta compute ssh --zone \"us-central1-b\" \"cluster-c5e7-m\"  --project \"cs1660-project-310304\"\nY\n\n"
					+ "");
			printResults(process);
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}*/
		
		mainFrame.setSize(1090, 700);
		mainFrame.setOpacity(1.0f);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.getContentPane().setLayout(new BoxLayout(mainFrame.getContentPane(), BoxLayout.Y_AXIS));
		
		// Labels:
		JLabel l1 = new JLabel("Loading Engine");
		l1.setFont(new Font("Calibri", Font.BOLD, 16));
		l1.setAlignmentX(Component.CENTER_ALIGNMENT);
		l1.setForeground(Color.LIGHT_GRAY);
		
		JLabel l2 = new JLabel("Choose up to 3 documents to search:");
		l2.setFont(new Font("Calibri", Font.BOLD, 14));
		l2.setAlignmentX(Component.CENTER_ALIGNMENT);
		l2.setForeground(Color.LIGHT_GRAY);
		
		JLabel l3 = new JLabel("", JLabel.CENTER);
		l3.setFont(new Font("Calibri", Font.BOLD, 14));
		l3.setForeground(Color.LIGHT_GRAY);
		l3.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		
		JLabel l4 = new JLabel("", JLabel.CENTER);
		l4.setFont(new Font("Calibri", Font.BOLD, 14));
		l4.setForeground(Color.LIGHT_GRAY);
		l4.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		
		JLabel l5 = new JLabel("Enter Your Search Term");
		l5.setFont(new Font("Calibri", Font.BOLD, 14));
		l5.setForeground(Color.LIGHT_GRAY);
		l5.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		
		// Buttons:
		JButton b1 = new JButton("Choose Files");
		b1.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JButton b2 = new JButton("Load Engine and Construct Inverted Indices");
		b2.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JButton b3 = new JButton("Search for Term");
		b3.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JButton b4 = new JButton("Top-N");
		b4.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JButton b5 = new JButton("Search");
		b5.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		// Text fields:
		JTextArea t1 = new JTextArea(5, 20);
		t1.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		// Layout components:
		Component topGlue = Box.createVerticalGlue();
		Component bottomGlue = Box.createVerticalGlue();
		
		// Panel:
		JPanel myPanel = new JPanel();
		myPanel.setOpaque(true);
		myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));
		myPanel.setBackground(Color.DARK_GRAY);
		
		myPanel.add(topGlue);
		myPanel.add(l1);
		myPanel.add(Box.createVerticalStrut(20));
		myPanel.add(l2);
		myPanel.add(Box.createVerticalStrut(10));
		myPanel.add(b1);
		myPanel.add(Box.createVerticalStrut(10));
		myPanel.add(bottomGlue);
		
		// Set visible:
		mainFrame.getContentPane().add(myPanel);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
		
		b1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals("Choose Files")) {
					fileChooser= new JFileChooser("/src/data");
					//fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
					fileChooser.setMultiSelectionEnabled(true);
					
					int d = fileChooser.showOpenDialog(null);
					if(d == JFileChooser.APPROVE_OPTION) {
						selectedFiles = fileChooser.getSelectedFiles();
						
						String text = "<html><p align=center>";
						for(int i = 0; i < selectedFiles.length; i++) {
							if(i == selectedFiles.length - 1) {
								text += selectedFiles[i].getName();
							}
							else {
								text += selectedFiles[i].getName() + "<br/>";
							}
						}
						text += "</p></html>";
						l3.setText(text);
						
						myPanel.remove(bottomGlue);
						myPanel.add(l3);
						myPanel.add(Box.createVerticalStrut(10));
						myPanel.add(b2);
						myPanel.add(bottomGlue);
						
						myPanel.repaint();
						myPanel.revalidate();
					}
					else {
						return;
					}
				}
				
			}
		});
		
		b2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Get gsutil URI for bucket:
				//String gsutil_URI = "gs://cs1660-bucket-1";
				
				// Construct InvertedIndices:
				
				// 1) Copy the files that were uploaded into the GCP dataproc cluster.
				//	  (a) Assume InvertedIndices.java, and its jar file, invertedindices.jar, are already in the cluster.
				
				// 2) In your shell (manually), export the needed environment variables.
				
				// 3) In your shell (manually), remove the /data folder and the /output folder.
				
				// 4) Run a hadoop job to put the /data into / in HDFS.
				
				// 5) Run a hadoop job to execute the inverted indices jar
				
				// 6) Merge the output using the hadoop -getmerge /output mergedResults
				
				// 7) Grab mergedResults output from the cluster
				
				// Combination mapping:
				// Hugo: /data1
				// Tolstoy: /data2
				// Shakespeare: /data3
				// Hugo, Tolstoy: /data4
				// Hugo, Shakespeare: /data5
				// Tolstoy, Shakespeare: /data6
				// Hugo, Tolstory, Shakespeare: /data7
				
				for(int i = 0; i < selectedFiles.length; i++) {
					System.out.println(selectedFiles[i].getName());
				}
				
				// Determine the input data folder in HDFS given the combination:
				String inputFolder = "";
				
				if(contains(selectedFiles, "Hugo.tar.gz") && !contains(selectedFiles, "Tolstoy.tar.gz") && !contains(selectedFiles, "shakespeare.gz")) {
					inputFolder = "/data1";
				}
				else if(!contains(selectedFiles, "Hugo.tar.gz") && contains(selectedFiles, "Tolstoy.tar.gz") && !contains(selectedFiles, "shakespeare.gz")) {
					inputFolder = "/data2";
				}
				else if(!contains(selectedFiles, "Hugo.tar.gz") && !contains(selectedFiles, "Tolstoy.tar.gz") && contains(selectedFiles, "shakespeare.gz")) {
					inputFolder = "/data3";
				}
				else if(contains(selectedFiles, "Hugo.tar.gz") && contains(selectedFiles, "Tolstoy.tar.gz") && !contains(selectedFiles, "shakespeare.gz")) {
					inputFolder = "/data4";
				}
				else if(contains(selectedFiles, "Hugo.tar.gz") && !contains(selectedFiles, "Tolstoy.tar.gz") && contains(selectedFiles, "shakespeare.gz")) {
					inputFolder = "/data5";
				}
				else if(!contains(selectedFiles, "Hugo.tar.gz") && contains(selectedFiles, "Tolstoy.tar.gz") && contains(selectedFiles, "shakespeare.gz")) {
					inputFolder = "/data6";
				}
				else if(contains(selectedFiles, "Hugo.tar.gz") && contains(selectedFiles, "Tolstoy.tar.gz") && contains(selectedFiles, "shakespeare.gz")) {
					inputFolder = "/data7";
				}
				else {
					inputFolder = "/data1";
				}
				
				System.out.println("Input folder: " + inputFolder);
				
				String inputUri = inputFolder;
				String outputUri = "/output";
				List<String> hadoopFsQuery = ImmutableList.of("InvertedIndices", inputUri, outputUri);
				try {
					submitHadoopFsJob(hadoopFsQuery);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				// Wipe the JPanel and replace with new interface:
				myPanel.removeAll();
				myPanel.repaint();
				myPanel.revalidate();
				
				myPanel.setOpaque(true);
				myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));
				myPanel.setBackground(Color.DARK_GRAY);
				
				String text = "<html><p align=center>";
				text += "Engine Loaded<br/>and<br/> Inverted Indices Constructed Successfully!<br/><br/>Please make a selection below:";
				text += "</p></html>";
				l4.setText(text);
				
				myPanel.add(topGlue);
				myPanel.add(l4);
				myPanel.add(Box.createVerticalStrut(20));
				myPanel.add(b3);
				myPanel.add(Box.createVerticalStrut(20));
				myPanel.add(b4);
				myPanel.add(bottomGlue);
				
				b3.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						myPanel.removeAll();
						myPanel.repaint();
						myPanel.revalidate();
						
						myPanel.setOpaque(true);
						myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));
						myPanel.setBackground(Color.DARK_GRAY);
						
						myPanel.add(topGlue);
						myPanel.add(Box.createVerticalStrut(175));
						myPanel.add(t1);
						myPanel.add(Box.createVerticalStrut(175));
						myPanel.add(b5);
						myPanel.add(Box.createVerticalStrut(20));
						myPanel.add(bottomGlue);	
					}
				});
				
				b4.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
					}
				});
			}
		});
	}
	
	public static void uploadObject(String projectId, String bucketName, String objectName, String filePath) throws IOException {
		
		Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
		BlobId blobId = BlobId.of(bucketName, objectName);
	    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
	    storage.create(blobInfo, Files.readAllBytes(Paths.get(filePath)));
	    System.out.println("File " + filePath + " uploaded to bucket " + bucketName + " as " + objectName);
	}
	
	public static void authImplicit() {
		  // If you don't specify credentials when constructing the client, the client library will
		  // look for credentials via the environment variable GOOGLE_APPLICATION_CREDENTIALS.
		  Storage storage = StorageOptions.getDefaultInstance().getService();

		  System.out.println("Buckets:");
		  Page<Bucket> buckets = storage.list();
		  for (Bucket bucket : buckets.iterateAll()) {
			    System.out.println(bucket.toString());
		  }
	}
	
	public static ArrayList<String> stringToList(String s) {
		return new ArrayList<>(Arrays.asList(s.split(" ")));
	}
	
	public static void submitHadoopFsJob(List<String> hadoopFsQuery) throws IOException, InterruptedException {
		String projectId = "cs1660-project-310304";
		String region = "us-central1";
		String clusterName = "cluster-c5e7";
		//hadoopFsQuery = ImmutableList.of("InvertedIndices", inputUri, outputUri);
		
		String myEndpoint = String.format("%s-dataproc.googleapis.com:443", region);
		
		JobControllerSettings jobControllerSettings = null;
		// Configure settings for job controller client:
		try {
			jobControllerSettings = JobControllerSettings.newBuilder().setEndpoint(myEndpoint).build();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		// Create job controller client with the configured settings:
		try(JobControllerClient jobControllerClient = JobControllerClient.create(jobControllerSettings)) {
			// Configure cluster placement for the job:
			JobPlacement jobPlacement = JobPlacement.newBuilder().setClusterName(clusterName).build();
			
			 // Configure Hadoop job settings and set HDFS query:
			HadoopJob hadoopJob =
			          HadoopJob.newBuilder().setMainJarFileUri("gs://dataproc-staging-us-central1-371441699998-js2yljty/jar/invertedindices.jar")
			              .addAllArgs(hadoopFsQuery)
			              .build();
			
			Job job = Job.newBuilder().setPlacement(jobPlacement).setHadoopJob(hadoopJob).build();
			
			// Submit an asynchronous request to execute the job.
		    OperationFuture<Job, JobMetadata> submitJobAsOperationAsyncRequest = jobControllerClient.submitJobAsOperationAsync(projectId, region, job);
		    
		    Job response = submitJobAsOperationAsyncRequest.get();
		    
		 // Print output from Google Cloud Storage.
		      Matcher matches =
		          Pattern.compile("gs://(.*?)/(.*)").matcher(response.getDriverOutputResourceUri());
		      matches.matches();

		      Storage storage = StorageOptions.getDefaultInstance().getService();
		      Blob blob = storage.get(matches.group(1), String.format("%s.000000000", matches.group(2)));

		      System.out.println(
		          String.format("Job finished successfully: %s", new String(blob.getContent())));
		}
		catch(ExecutionException e2) {
			e2.printStackTrace();
			System.err.println(String.format("submitHadoopFSJob: %s ", e2.getMessage()));
		}
	}
	
	public static void printResults(Process process) throws IOException {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	    String line = "";
	    while ((line = reader.readLine()) != null) {
	        System.out.println(line);
	    }
	}
	
	public static boolean contains(File[] arr, String elem) {
		boolean isContains = false;
		
		for(int i = 0; i < arr.length; i++) {
			if (arr[i].getName().equals(elem)) {
				isContains = true;
			}
		}
		return isContains;
	}
}
