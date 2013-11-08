package org.jbpm.ee.service.remote;

import java.util.Map;

import javax.ejb.Remote;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jbpm.ee.support.KieReleaseId;
import org.kie.api.runtime.process.ProcessInstance;

/**
 * 
 * @author bdavis, abaxter
 *
 * Interface to BRMS process runtime. 
 * 
 * For starting, creating, and aborting processes and signaling events to a process
 */
public interface ProcessRuntimeRemote {
	
	/**
	 * Starts a process with no variables
	 * 
	 * @param releaseId Deployment information for the process's kjar
	 * @param processId The process's name 
	 * @return Process instance information
	 */
	ProcessInstance startProcess(KieReleaseId releaseId, String processId);
	
	/**
	 * Starts a process with provided variables
	 * 
	 * @param releaseId Deployment information for the process's kjar
	 * @param processId The process's name 
	 * @param parameters Process variables to start with
	 * @return Process instance information
	 */
	ProcessInstance startProcess(KieReleaseId releaseId, String processId, Map<String, Object> parameters);
	
	/**
	 * Created, but doesn't start, a process with provided variables
	 * 
	 * @param releaseId Deployment information for the process's kjar
	 * @param processId The process's name 
	 * @param parameters Process variables to start with
	 * @return Process instance information
	 */
	ProcessInstance createProcessInstance(KieReleaseId releaseId, String processId, Map<String, Object> parameters);
	
	/**
	 * Starts a previously created process
	 * 
	 * @param processInstanceId The process instance's unique identifier
	 * @return Process instance information
	 */
	ProcessInstance startProcessInstance(long processInstanceId);
	
	/**
	 * Signal an event to a single process
	 * 
	 * @param type The event's ID in the process
	 * @param event The event object to be passed in with the event
	 * @param processInstanceId The process instance's unique identifier
	 */
	void signalEvent(String type, Object event, long processInstanceId);
	
	
	/**
	 * Returns process instance information. Will return null if no
	 * active process with that id is found
	 * 
	 * @param processInstanceId The process instance's unique identifier
	 * @return Process instance information
	 */
	ProcessInstance getProcessInstance(long processInstanceId);
	
	/**
	 * Aborts the specified process
	 * 
	 * @param processInstanceId The process instance's unique identifier
	 */
	void abortProcessInstance(long processInstanceId);
}
