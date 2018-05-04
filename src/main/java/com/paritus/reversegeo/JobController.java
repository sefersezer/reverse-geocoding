package com.paritus.reversegeo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class JobController<T> {

	public abstract Runnable createThread(List<T> lines);
	
	public void process(List<T> lines ,int numThreads ) {
	
		try {
			int last= 0 ;
			List<Thread> threads = new ArrayList<Thread>();
			List<T> sublist = null;
			int size = lines.size() ;
			int batchCount =  size  / numThreads;
			boolean singleThread = false;
			for (int i = 0; i < numThreads ; i++) {
				if (last >= size){
					break;
				}
				last = last +  batchCount ;
				int remainig = size - last ;
				if (remainig > 0 && remainig < numThreads){
					last = size;
				}
				if (size < 50){
					sublist = lines;
					singleThread = true;
				}else{
					sublist = lines.subList(i  * batchCount , last );
				}
					Thread t = new Thread(createThread (sublist));
					threads.add(t);
					t.start();
					if (singleThread){
						break;
					}
			}
			
			Date threadsStart = new Date();
			for (Thread thread : threads) {
				thread.join();
			}
	
			Date threadsEnd = new Date();
			long totalExecutionTime = threadsEnd.getTime() - threadsStart.getTime();
			System.out.println(" Total Execution Time is  " + totalExecutionTime/1000   + " seconds");

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
}
