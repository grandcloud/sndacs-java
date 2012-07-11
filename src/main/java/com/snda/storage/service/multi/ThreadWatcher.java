package com.snda.storage.service.multi;

import com.snda.storage.service.io.BytesProgressWatcher;



public class ThreadWatcher {
	private long completedThreads = 0L;
	private long threadCount = 0L;
	private CancelEventTrigger cancelEventListener = null;
	private BytesProgressWatcher[] progressWatchers = null;
	
	public ThreadWatcher(BytesProgressWatcher[] progressWatchers) {
        this.progressWatchers = progressWatchers;
        this.threadCount = this.progressWatchers.length;
    }
	
	public ThreadWatcher(long threadCount) {
		this.threadCount = threadCount;
	}
	
	public void updateThreadsCompletedCount(long completedThreads) {
		updateThreadsCompletedCount(completedThreads, null);
	}
	
	public void updateThreadsCompletedCount(long completedThreads, 
			CancelEventTrigger cancelEventTrigger) {
		this.completedThreads = completedThreads;
		this.cancelEventListener = cancelEventTrigger;
	}
	
	public long getCompletedThreads() {
		return completedThreads;
	}
	
	public long getThreadCount() {
		return threadCount;
	}
	
	public boolean isCancelTaskSupported() {
		return cancelEventListener != null;
	}
	
	public void cancelTask() {
		if (isCancelTaskSupported()) {
			cancelEventListener.cancelTask(this);
		}
	}
	
	public CancelEventTrigger getCancelEventListener() {
        return cancelEventListener;
    }
	
}
