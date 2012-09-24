package com.snda.storage.service.multi;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.snda.storage.exceptions.ServiceException;
import com.snda.storage.security.ProviderCredentials;
import com.snda.storage.service.StorageService;
import com.snda.storage.service.multi.event.ServiceEvent;


/**
 * Storage service wrapper that performs multiple service requests at a time using
 * multi-threading and an underlying thread-safe {@link StorageService} implementation.
 * <p>
 * This service is designed to be run in non-blocking threads that therefore communicates
 * information about its progress by firing {@link ServiceEvent} events. It is the responsibility
 * of applications using this service to correctly handle these events.
 * </p>
 *
 */
@Deprecated
public class ThreadedStorageService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ThreadedStorageService.class);

	protected StorageService storageService = null;
    protected final boolean[] isShutdown = new boolean[] { false };

    protected final List<StorageServiceEventListener> serviceEventListeners =
        new ArrayList<StorageServiceEventListener>();
    protected final long sleepTime;

    /**
     * Construct a multi-threaded service based on a StorageService and which sends event notifications
     * to an event listening class. EVENT_IN_PROGRESS events are sent at the default time interval
     * of 500ms.
     *
     * @param service
     * an storage service implementation that will be used to perform requests.
     * @param listener
     * the event listener which will handle event notifications.
     */
    public ThreadedStorageService(StorageService service, StorageServiceEventListener listener)
            throws ServiceException {
        this(service, listener, 500);
    }

    /**
     * Construct a multi-threaded service based on an storage service and which sends event notifications
     * to an event listening class, and which will send EVENT_IN_PROGRESS events at the specified
     * time interval.
     *
     * @param service
     * a storage service implementation that will be used to perform requests.
     * @param listener
     * the event listener which will handle event notifications.
     * @param threadSleepTimeMS
     * how many milliseconds to wait before sending each EVENT_IN_PROGRESS notification event.
     */
    public ThreadedStorageService(StorageService service, StorageServiceEventListener listener,
            long threadSleepTimeMS) throws ServiceException {
    	this.storageService = service;
        addServiceEventListener(listener);
        this.sleepTime = threadSleepTimeMS;

        // Sanity-check the maximum thread and connection settings to ensure the maximum number
        // of connections is at least equal to the largest of the maximum thread counts, and warn
        // the use of potential problems.
        int adminMaxThreadCount = 20;
        int maxThreadCount = 5;
        int maxConnectionCount = 20;
        if (maxConnectionCount < maxThreadCount) {
            throw new ServiceException(
                "Insufficient connections available (httpclient.max-connections="
                + maxConnectionCount + ") to run (threaded-service.max-thread-count="
                + maxThreadCount + ") simultaneous threads - please adjust properties");
        }
        if (maxConnectionCount < adminMaxThreadCount) {
            throw new ServiceException(
                "Insufficient connections available (httpclient.max-connections="
                + maxConnectionCount + ") to run (threaded-service.admin-max-thread-count="
                + adminMaxThreadCount
                + ") simultaneous admin threads - please adjust properties");
        }
    }
    
    /**
     * Make a best-possible effort to shutdown and clean up any resources used by this
     * service such as HTTP connections, connection pools, threads etc. After calling
     * this method the service instance will no longer be usable -- a new instance must
     * be created to do more work.
     */
    public void shutdown() {
        this.isShutdown[0] = true;
        this.getStorageService().shutdown();
    }

    /**
     * @return true if the {@link #shutdown()} method has been used to shut down and
     * clean up this service. If this function returns true this service instance
     * can no longer be used to do work.
     */
    public boolean isShutdown() {
        return this.isShutdown[0];
    }

    /**
     * @return
     * the underlying service implementation.
     */
    public StorageService getStorageService() {
        return storageService;
    }

    /**
     * Adds a service event listener to the set of listeners that will be notified of events.
     *
     * @param listener
     * an event listener to add to the event notification chain.
     */
    public void addServiceEventListener(StorageServiceEventListener listener) {
        if (listener != null) {
            serviceEventListeners.add(listener);
        }
    }
    
    /**
     * Removes a service event listener from the set of listeners that will be notified of events.
     *
     * @param listener
     * an event listener to remove from the event notification chain.
     */
    public void removeServiceEventListener(StorageServiceEventListener listener) {
        if (listener != null) {
            serviceEventListeners.remove(listener);
        }
    }
    
    /**
     * Sends a service event to each of the listeners registered with this service.
     * @param event
     * the event to send to this service's registered event listeners.
     */
    protected void fireServiceEvent(ServiceEvent event) {
        if (serviceEventListeners.size() == 0) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("ThreadedStorageService invoked without any StorageServiceEventListener objects, this is dangerous!");
            }
        }
    }
    
    /**
     * @return
     * true if the underlying service implementation is authenticated.
     */
    public boolean isAuthenticatedConnection() {
        return storageService.isAuthenticatedConnection();
    }

    /**
     * @return the credentials in the underlying storage service.
     */
    public ProviderCredentials getProviderCredentials() {
        return storageService.getProviderCredentials();
    }
    
    /**
     * All the operation threads used by this service extend this class, which provides common
     * methods used to retrieve the result object from a completed thread (via {@link #getResult()}
     * or force a thread to be interrupted (via {@link #forceInterrupt}.
     */
    protected abstract class AbstractRunnable implements Runnable {

        public abstract Object getResult();

        public abstract void forceInterruptCalled();

        protected void forceInterrupt() {
            forceInterruptCalled();
        }
    }
	
    /**
     * The thread group manager is responsible for starting, running and stopping the set of threads
     * required to perform an operation.
     * <p>
     * The manager starts all the threads, monitors their progress and stops threads when they are
     * cancelled or an error occurs - all the while firing the appropriate {@link ServiceEvent} event
     * notifications.
     */
	protected abstract class ThreadGroupManager {
		
		private final Logger LOGGER = LoggerFactory.getLogger(ThreadGroupManager.class);

		/**
         * the set of runnable objects to execute.
         */
		private AbstractRunnable[] runnables = null;
		private ThreadWatcher threadWatcher = null;
		private int maxThreadCount = 1;
		private boolean ignoreExceptions = false;
		
		/**
         * Thread objects that are currently running, where the index corresponds to the
         * runnables index. Any AbstractThread runnable that is not started, or has completed,
         * will have a null value in this array.
         */
		private Thread[] threads = null;
		
		/**
         * set of flags indicating which runnable items have been started
         */
		private boolean[] started = null;
		
		/**
         * set of flags indicating which threads have already had In Progress events fired on
         * their behalf. These threads have finished running.
         */
		private boolean[] alreadyFired = null;

		private long lastProgressEventFiredTime = 0;

		public ThreadGroupManager(AbstractRunnable[] runnables,
	            ThreadWatcher threadWatcher, boolean isAdminTask) {
            this.runnables = runnables;
            this.threadWatcher = threadWatcher;
            if (isAdminTask) {
                this.maxThreadCount = 20;
            } else {
                this.maxThreadCount = 5;
            }
            this.ignoreExceptions = false;

            this.threads = new Thread[runnables.length];
            started = new boolean[runnables.length]; // All values initialized to false.
            alreadyFired = new boolean[runnables.length]; // All values initialized to false.
        }
		
		/**
         * @return
         * the number of threads that have not finished running (sum of those currently running, and those awaiting start)
         */
		private int getPendingThreadCount() {
            int pendingThreadCount = 0;
            for (int i = 0; i < runnables.length; i++) {
                if (!alreadyFired[i]) {
                    pendingThreadCount++;
                }
            }
            return pendingThreadCount;
        }
		
		/**
         * Invokes the {@link AbstractRunnable#forceInterrupt} on all threads being managed.
         *
         */
		private void forceInterruptAllRunnables() {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Setting force interrupt flag on all runnables");
            }
            for (int i = 0; i < runnables.length; i++) {
                if (runnables[i] != null) {
                    runnables[i].forceInterrupt();
                    runnables[i] = null;
                }
            }
        }

		/**
         * Starts pending threads such that the total of running threads never exceeds the
         * maximum count <i>threaded-service.max-thread-count</i>.
         *
         * @throws Throwable
         */
		private void startPendingThreads() throws Throwable {
            // Count active threads that are running (i.e. have been started but final event not fired)
            int runningThreadCount = 0;
            for (int i = 0; i < runnables.length; i++) {
                if (started[i] && !alreadyFired[i]) {
                    runningThreadCount++;
                }
            }

            // Start threads until we are running the maximum number allowed.
            for (int i = 0; runningThreadCount < maxThreadCount && i < started.length; i++) {
                if (!started[i]) {
                    threads[i] = new Thread(runnables[i]);
                    threads[i].start();
                    started[i] = true;
                    runningThreadCount++;
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Thread " + (i+1) + " of " + runnables.length + " has started");
                    }
                }
            }
        }
		
		/**
         * Determine which threads, if any, have finished since the last time an In Progress event
         * was fired.
         *
         * @return
         * a list of the threads that finished since the last In Progress event was fired. This list may
         * be empty.
         *
         * @throws Throwable
         */
		private ResultsTuple getNewlyCompletedResults() throws Throwable
        {
            ArrayList completedResults = new ArrayList();
            ArrayList errorResults = new ArrayList();

            for (int i = 0; i < threads.length; i++) {
                if (!alreadyFired[i] && started[i] && !threads[i].isAlive()) {
                    alreadyFired[i] = true;
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Thread " + (i+1) + " of " + threads.length
                            + " has recently completed, releasing resources");
                    }

                    if (runnables[i].getResult() instanceof Throwable) {
                        Throwable throwable = (Throwable) runnables[i].getResult();
                        runnables[i] = null;
                        threads[i] = null;

                        if (ignoreExceptions) {
                            // Ignore exceptions
                            if (LOGGER.isWarnEnabled()) {
                                LOGGER.warn("Ignoring exception (property " +
                                        "threaded-service.ignore-exceptions-in-multi is set to true)",
                                        throwable);
                            }
                            errorResults.add(throwable);
                        } else {
                            throw throwable;
                        }
                    } else {
                        completedResults.add(runnables[i].getResult());
                        runnables[i] = null;
                        threads[i] = null;
                    }
                }
            }

            Throwable[] ignoredErrors = new Throwable[] {};
            if (errorResults.size() > 0) {
                ignoredErrors = (Throwable[]) errorResults.toArray(new Throwable[errorResults.size()]);
            }

            return new ResultsTuple(completedResults, ignoredErrors);
        }
		
		/**
         * Runs and manages all the threads involved in a multi-operation.
         *
         */
		public void run() {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Started ThreadManager");
            }

            final boolean[] interrupted = new boolean[] { false };

            /*
             * Create a cancel event trigger, so all the managed threads can be cancelled if required.
             */
            final CancelEventTrigger cancelEventTrigger = new CancelEventTrigger() {
            	private static final long serialVersionUID = 2994294691835326368L;

                public void cancelTask(Object eventSource) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Cancel task invoked on ThreadManager");
                    }

                    // Flag that this ThreadManager class should shutdown.
                    interrupted[0] = true;

                    // Set force interrupt flag for all runnables.
                    forceInterruptAllRunnables();
                }
            };

            // Actual thread management happens in the code block below.
            try {
                // Start some threads
                startPendingThreads();

                threadWatcher.updateThreadsCompletedCount(0, cancelEventTrigger);
                fireStartEvent(threadWatcher);

                // Loop while threads haven't been interrupted/cancelled, and at least one thread is
                // still active (ie hasn't finished its work)
                while (!interrupted[0] && getPendingThreadCount() > 0) {
                    try {
                        // Shut down threads if this service has been shutdown.
                        if (isShutdown[0]) {
                            throw new InterruptedException("StorageServiceMulti#shutdown method invoked");
                        }

                        Thread.sleep(100);

                        if (interrupted[0]) {
                            // Do nothing, we've been interrupted during sleep.
                        } else {
                            if (System.currentTimeMillis() - lastProgressEventFiredTime > sleepTime) {
                                // Fire progress event.
                                int completedThreads = runnables.length - getPendingThreadCount();
                                threadWatcher.updateThreadsCompletedCount(completedThreads, cancelEventTrigger);
                                ResultsTuple results = getNewlyCompletedResults();

                                lastProgressEventFiredTime = System.currentTimeMillis();
                                fireProgressEvent(threadWatcher, results.completedResults);

                                if (results.errorResults.length > 0) {
                                    fireIgnoredErrorsEvent(threadWatcher, results.errorResults);
                                }
                            }

                            // Start more threads.
                            startPendingThreads();
                        }
                    } catch (InterruptedException e) {
                        interrupted[0] = true;
                        forceInterruptAllRunnables();
                    }
                }

                if (interrupted[0]) {
                    fireCancelEvent();
                } else {
                    int completedThreads = runnables.length - getPendingThreadCount();
                    threadWatcher.updateThreadsCompletedCount(completedThreads, cancelEventTrigger);
                    ResultsTuple results = getNewlyCompletedResults();

                    fireProgressEvent(threadWatcher, results.completedResults);
                    if (results.completedResults.size() > 0) {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug(results.completedResults.size() + " threads have recently completed");
                        }
                    }

                    if (results.errorResults.length > 0) {
                        fireIgnoredErrorsEvent(threadWatcher, results.errorResults);
                    }

                    fireCompletedEvent();
                }
            } catch (Throwable t) {
                if (LOGGER.isErrorEnabled()) {
                    LOGGER.error("A thread failed with an exception. Firing ERROR event and cancelling all threads", t);
                }
                // Set force interrupt flag for all runnables.
                forceInterruptAllRunnables();

                fireErrorEvent(t);
            }
        }

        public abstract void fireStartEvent(ThreadWatcher threadWatcher);

        public abstract void fireProgressEvent(ThreadWatcher threadWatcher, List completedResults);

        public abstract void fireCompletedEvent();

        public abstract void fireCancelEvent();

        public abstract void fireErrorEvent(Throwable t);

        public abstract void fireIgnoredErrorsEvent(ThreadWatcher threadWatcher, Throwable[] ignoredErrors);

        private class ResultsTuple {
            public List completedResults = null;
            public Throwable[] errorResults = null;

            public ResultsTuple(List completedResults, Throwable[] errorResults) {
                this.completedResults = completedResults;
                this.errorResults = errorResults;
            }
        }
        
	}
}
