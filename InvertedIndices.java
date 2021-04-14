import java.io.IOException;
import java.util.StringTokenizer;
import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;

import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.util.logging.Logger;

public class InvertedIndices {
	
	private static final Logger logger = Logger.getLogger(InvertedIndices.class.getName());
	
	public static class InvertedMapper extends Mapper<LongWritable, Text, Text, Text>{	// Key-in, Value-in, Key-out, Value-out
		
		private Text word = new Text();
		
		public void map(LongWritable key, Text value, Context context) 
				throws IOException, InterruptedException 
		{
			String docID = ((FileSplit) context.getInputSplit()).getPath().getName();
			String line = value.toString();
			StringTokenizer itr = new StringTokenizer(line);
						
			while(itr.hasMoreTokens()) {
				
				word.set(itr.nextToken().replaceAll("[^a-zA-Z]", "").toLowerCase());
				context.write(word, new Text(docID));
				
			}
		}
		
		/* procedure MAP(did id, doc d)
		 * {
		 *	F <- new ASSOCIATIVEARRAY
		 *  forall term t in doc d do {
		 *  	F{t} <- F{t} + 1
		 *  }
		 *  forall term t in F do {
		 *  	EMIT(term t, posting[id, F{t}]
		 *  }
		 * } */
	}
	
	public static class InvertedReducer extends Reducer<Text, Text, Text, Text> 
	{
		
		public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
			
			HashMap<String, Integer> h = new HashMap<String, Integer>();
			
			int sum = 0;
			for (Text val : values) {
				
				String t = val.toString();
				
				if(h != null && h.get(t) != null) {
					sum = h.get(t);
					sum++;
					h.put(t, sum);
				}
				else {
					h.put(t, 1);
				}
			}
			context.write(key,  new Text(h.toString()));
		}
	}
	
	public static void main(String[] args)
		throws IOException, InterruptedException, ClassNotFoundException {
		
		logger.info("Logging INFO with java.util.logging");
	    logger.severe("Logging ERROR with java.util.logging");
		
		Configuration conf = new Configuration();
		GenericOptionsParser optionParser = new GenericOptionsParser(conf, args);
		
		String[] remainingArgs = optionParser.getRemainingArgs();
		if((remainingArgs.length != 2) && remainingArgs.length != 4) {
			System.err.println("Usage: invertedindices <in> <out>");
			System.exit(2);
		}
		
		Job job = Job.getInstance(conf, "invertedindices");
		
		job.setJarByClass(InvertedIndices.class);
		job.setMapperClass(InvertedMapper.class);
		job.setReducerClass(InvertedReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		job.waitForCompletion(true);
	}
}