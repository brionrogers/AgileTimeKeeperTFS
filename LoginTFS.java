import java.net.URI;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.microsoft.tfs.core.TFSTeamProjectCollection;
import com.microsoft.tfs.core.clients.workitem.WorkItem;
import com.microsoft.tfs.core.clients.workitem.WorkItemClient;
import com.microsoft.tfs.core.clients.workitem.fields.Field;
import com.microsoft.tfs.core.clients.workitem.fields.FieldCollection;
import com.microsoft.tfs.core.clients.workitem.project.Project;
import com.microsoft.tfs.core.clients.workitem.project.ProjectCollection;
import com.microsoft.tfs.core.clients.workitem.query.WorkItemCollection;
import com.microsoft.tfs.core.config.IllegalConfigurationException;
import com.microsoft.tfs.core.exceptions.TFSUnauthorizedException;
import com.microsoft.tfs.core.httpclient.Credentials;

public class LoginTFS 
{
	public ProjectCollection TFSProjects;
	public URI tfsURI;
	public TFSTeamProjectCollection tpc;
	public Credentials loginCredentials;
	public Project demoProject;
	public WorkItemCollection workItems;
	
	public LoginTFS(URI uri, Credentials credentials)
	{
		loginCredentials = credentials;
		tfsURI = uri;

        	
       	 tpc = new TFSTeamProjectCollection(uri,credentials);
       	 
       	 try{
       	 tpc.authenticate();
       	 
       	//TFSProjects = tpc.getWorkItemClient().getProjects();
      	//demoProject = tpc.getWorkItemClient().getProjects().get("DemoTeamProject");
      	 

	        
       	 } catch (TFSUnauthorizedException a)
       	 {
       		 //a.printStackTrace();
       		 System.out.println("Bad credentials");
       		 JFrame frame = new JFrame();
       		JOptionPane.showMessageDialog(frame,
       			    "The supplied credentials are not valid. Please contact your TFS administrator for more information.");
       	 } catch (IllegalConfigurationException i)
       	 {
       		 //i.printStackTrace();
       		 System.out.println("Bad URI");
       	 }
       	 
	}
	
}
